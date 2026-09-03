import { cp, mkdir, mkdtemp, rename, rm, stat } from 'node:fs/promises'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

export async function prepareDocumentationSite(repository) {
  const site = path.join(repository, 'site', 'target', 'site')
  const api = path.join(site, 'docs', 'api')
  const resources = path.join(repository, 'site', 'src', 'main', 'resources')
  try {
    if (!(await stat(path.join(api, 'index.html'))).isFile()) throw new Error('Missing API index')
    await stat(resources)
  } catch {
    throw new Error('Build the unified API reference first with ./mvnw -f site/pom.xml package.')
  }
  const temporary = await mkdtemp(path.join(repository, 'site', 'target', 'preserved-api-'))
  await rename(api, path.join(temporary, 'api'))
  try {
    await rm(site, { recursive: true, force: true })
    await cp(resources, site, { recursive: true })
  } finally {
    await mkdir(path.dirname(api), { recursive: true })
    await rename(path.join(temporary, 'api'), api)
    await rm(temporary, { recursive: true })
  }
}

const script = fileURLToPath(import.meta.url)
if (process.argv[1] && path.resolve(process.argv[1]) === script) {
  prepareDocumentationSite(path.resolve(path.dirname(script), '..', '..'))
    .then(() => console.log('Prepared fresh tapik narrative output; unified API reference preserved.'))
    .catch(cause => { console.error(cause.message); process.exitCode = 1 })
}
