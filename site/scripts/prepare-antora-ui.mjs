import { createHash } from 'node:crypto'
import { mkdir, readFile, rename, rm, writeFile } from 'node:fs/promises'
import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'

const bundleUrl =
  'https://gitlab.com/antora/antora-ui-default/-/jobs/15385706035/artifacts/raw/build/ui-bundle.zip'
const expectedSha256 = 'e0ac81aad26961a9cd3cf9bce692a598eff4a1bd2822ce73c9cdc13a15a1e70d'
const scriptDirectory = dirname(fileURLToPath(import.meta.url))
const output = resolve(scriptDirectory, '../target/antora-ui-bundle.zip')
const temporaryOutput = `${output}.download`

function sha256(contents) {
  return createHash('sha256').update(contents).digest('hex')
}

async function readVerifiedBundle() {
  try {
    const contents = await readFile(output)
    return sha256(contents) === expectedSha256
  } catch (error) {
    if (error.code === 'ENOENT') return false
    throw error
  }
}

if (await readVerifiedBundle()) {
  console.log(`Using verified Antora UI bundle at ${output}`)
} else {
  await mkdir(dirname(output), { recursive: true })
  await rm(temporaryOutput, { force: true })

  console.log(`Downloading pinned Antora UI bundle from ${bundleUrl}`)
  const response = await fetch(bundleUrl, { redirect: 'follow' })
  if (!response.ok) {
    throw new Error(`Cannot download Antora UI bundle: ${response.status} ${response.statusText}`)
  }

  const contents = Buffer.from(await response.arrayBuffer())
  const actualSha256 = sha256(contents)
  if (actualSha256 !== expectedSha256) {
    throw new Error(
      `Antora UI checksum mismatch: expected ${expectedSha256}, received ${actualSha256}`
    )
  }

  await writeFile(temporaryOutput, contents)
  await rename(temporaryOutput, output)
  console.log(`Verified Antora UI bundle at ${output}`)
}
