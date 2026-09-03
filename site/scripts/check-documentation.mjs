import { execFileSync } from 'node:child_process'
import { readFile, readdir } from 'node:fs/promises'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { Parser } from 'htmlparser2'

const siteOrigin = 'https://tapik.akif.dev'
const linkAttributes = { a: 'href', area: 'href', link: 'href', script: 'src', img: 'src', iframe: 'src', source: 'src' }

export async function checkDocumentation(directory, { expectedPages = [], release = false } = {}) {
  const files = new Set(await listFiles(directory))
  const pages = new Map()
  const errors = []
  let linksChecked = 0
  for (const required of expectedPages) {
    if (!files.has(required)) errors.push(`Required page missing: ${required}`)
  }
  for (const file of files) {
    if (file.endsWith('.html')) pages.set(file, parsePage(await readFile(path.join(directory, file), 'utf8')))
  }
  for (const [file, page] of pages) {
    for (const link of page.links) {
      if (link.unresolved) errors.push(`${file}: Unresolved cross-reference: ${link.href}`)
      let target
      let fragment
      try {
        const url = new URL(link.href, `${siteOrigin}/${file}`)
        if (url.protocol === 'file:' && release) errors.push(`${file}: Local file link in release: ${link.href}`)
        if (url.origin !== siteOrigin) continue
        target = decodeURIComponent(url.pathname).replace(/^\//, '')
        fragment = decodeURIComponent(url.hash.slice(1))
        if (!target || target.endsWith('/')) target += 'index.html'
      } catch {
        errors.push(`${file}: Malformed internal URL: ${link.href}`)
        continue
      }
      linksChecked++
      if (!files.has(target)) errors.push(`${file}: Missing target: ${link.href}`)
      else if (fragment && pages.has(target) && !pages.get(target).anchors.has(fragment)) {
        errors.push(`${file}: Missing anchor: ${link.href}`)
      }
    }
  }
  return { pagesChecked: pages.size, linksChecked, errors: [...new Set(errors)].sort() }
}

function parsePage(html) {
  const anchors = new Set()
  const links = []
  const parser = new Parser({
    onopentag(name, attributes) {
      if (attributes.id) anchors.add(attributes.id)
      if (name === 'a' && attributes.name) anchors.add(attributes.name)
      const attribute = linkAttributes[name]
      if (attribute && attributes[attribute]) {
        links.push({ href: attributes[attribute], unresolved: (attributes.class || '').split(/\s+/).includes('unresolved') })
      }
    }
  }, { decodeEntities: true })
  parser.end(html)
  return { anchors, links }
}

async function listFiles(directory, prefix = '') {
  const files = []
  for (const entry of await readdir(directory, { withFileTypes: true })) {
    const relative = prefix + entry.name
    if (entry.isDirectory()) files.push(...await listFiles(path.join(directory, entry.name), `${relative}/`))
    else if (entry.isFile()) files.push(relative)
  }
  return files.sort()
}

async function requiredPages(repository, release) {
  const sourcePages = await listFiles(path.join(repository, 'docs', 'modules', 'ROOT', 'pages'))
  const descriptor = await readFile(path.join(repository, 'docs', 'antora.yml'), 'utf8')
  const currentVersion = descriptor.match(/^version:\s*(\S+)/m)?.[1]
  const tags = execFileSync('git', ['tag', '--list'], { cwd: repository, encoding: 'utf8' }).trim().split('\n')
  const historicalPages = tags
    .filter(tag => /^v\d+\.\d+\.\d+$/.test(tag) && !tag.startsWith('v0.1.') && tag !== 'v0.2.0' && tag !== currentVersion)
    .map(tag => `docs/${tag}/index.html`)
  return [
    'index.html', 'docs/api/index.html', 'docs/v0.5.0/index.html',
    ...sourcePages.filter(file => file.endsWith('.adoc')).map(file => `docs/${file.replace(/\.adoc$/, '.html')}`),
    ...historicalPages,
    ...(release ? [`docs/${currentVersion}/index.html`, `docs/${currentVersion}/api/index.html`] : [])
  ]
}

const script = fileURLToPath(import.meta.url)
if (process.argv[1] && path.resolve(process.argv[1]) === script) {
  const repository = path.resolve(path.dirname(script), '..', '..')
  const release = process.argv.includes('--release')
  try {
    const result = await checkDocumentation(path.join(repository, 'site', 'target', 'site'), {
      expectedPages: await requiredPages(repository, release), release
    })
    if (result.errors.length) {
      console.error(result.errors.join('\n'))
      console.error(`Documentation check failed: ${result.errors.length} errors.`)
      process.exitCode = 1
    } else console.log(`Checked ${result.pagesChecked} documentation pages and ${result.linksChecked} internal links.`)
  } catch (cause) {
    console.error(cause.message)
    process.exitCode = 1
  }
}
