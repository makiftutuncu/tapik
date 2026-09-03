import { cp, readFile, rm, stat } from 'node:fs/promises'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const stableReleaseTag = /^v\d+\.\d+\.\d+$/

export async function validateReleaseVersion(repository, releaseTag) {
  if (!stableReleaseTag.test(releaseTag ?? '')) {
    throw new Error(`Expected a stable release tag such as v0.6.0, received ${releaseTag || 'nothing'}.`)
  }

  const pom = await readFile(path.join(repository, 'pom.xml'), 'utf8')
  const antora = await readFile(path.join(repository, 'docs', 'antora.yml'), 'utf8')
  const mavenVersion = requiredMatch(
    pom,
    /<artifactId>tapik-parent<\/artifactId>\s*<version>([^<]+)<\/version>/,
    'root Maven project version'
  )
  const documentationVersion = requiredMatch(antora, /^version:\s*([^\s#]+)\s*$/m, 'docs/antora.yml version')
  const expectedTag = `v${mavenVersion}`

  if (releaseTag !== expectedTag) {
    throw new Error(`Root Maven project declares ${mavenVersion}, but the release tag is ${releaseTag}.`)
  }
  if (documentationVersion !== releaseTag) {
    throw new Error(`docs/antora.yml declares ${documentationVersion}, but the release tag is ${releaseTag}.`)
  }
  return releaseTag
}

export async function stageReleaseApiReference(repository, releaseTag) {
  const version = await validateReleaseVersion(repository, releaseTag)
  const documentation = path.join(repository, 'site', 'target', 'site', 'docs')
  const latestApi = path.join(documentation, 'api')
  if (!(await isDirectory(latestApi))) {
    throw new Error(
      `Unified API reference not found at ${latestApi}. ` +
        'Build and integrate it before staging release documentation.'
    )
  }
  const versionedGuide = path.join(documentation, version, 'index.html')
  if (!(await isFile(versionedGuide))) {
    throw new Error(
      `Tagged narrative documentation not found at ${versionedGuide}. ` +
        'Render the release tag with Antora before staging its API reference.'
    )
  }

  const versionedApi = path.join(documentation, version, 'api')
  await rm(versionedApi, { recursive: true, force: true })
  await cp(latestApi, versionedApi, { recursive: true })
  return versionedApi
}

function requiredMatch(source, pattern, description) {
  const value = source.match(pattern)?.[1]?.trim()
  if (!value) throw new Error(`Could not read ${description}.`)
  return value
}

async function isDirectory(directory) {
  try {
    return (await stat(directory)).isDirectory()
  } catch (cause) {
    if (cause?.code === 'ENOENT') return false
    throw cause
  }
}

async function isFile(file) {
  try {
    return (await stat(file)).isFile()
  } catch (cause) {
    if (cause?.code === 'ENOENT') return false
    throw cause
  }
}

const script = fileURLToPath(import.meta.url)
if (process.argv[1] && path.resolve(process.argv[1]) === script) {
  const repository = path.resolve(path.dirname(script), '..', '..')
  const releaseTag = process.env.TAPIK_RELEASE_TAG
  const validateOnly = process.argv.includes('--validate-only')
  const action = validateOnly
    ? validateReleaseVersion(repository, releaseTag)
    : stageReleaseApiReference(repository, releaseTag)
  action
    .then(result => {
      if (validateOnly) console.log(`Validated tapik documentation release ${result}`)
      else console.log(`Archived tapik API reference at ${path.relative(repository, result)}`)
    })
    .catch(cause => {
      console.error(cause.message)
      process.exitCode = 1
    })
}
