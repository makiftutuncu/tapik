import { copyFile, mkdir, readFile, readdir, stat, writeFile } from 'node:fs/promises'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const linkClass = 'tapik-user-guide-link'
const stylesheetName = 'tapik-integration.css'

export async function integrateDokkaReference(apiDirectory, stylesheetSource) {
  if (!(await isDirectory(apiDirectory))) {
    throw new Error(
      `Unified API reference not found at ${apiDirectory}. ` +
        'Build the unified API reference first with ./mvnw -f site/pom.xml package.'
    )
  }

  const htmlFiles = await findHtmlFiles(apiDirectory)
  let integrated = 0
  for (const file of htmlFiles) {
    const original = await readFile(file, 'utf8')
    if (!original.includes('<!DOCTYPE html>')) continue

    const fromPageToApi = relativeUrl(path.dirname(file), apiDirectory)
    const stylesheetHref = joinUrl(fromPageToApi, 'styles', stylesheetName)
    const guideHref = directoryUrl(path.relative(path.dirname(file), path.dirname(apiDirectory)))
    const withStylesheet = addStylesheet(original, stylesheetHref)
    const html = addUserGuideLink(withStylesheet, guideHref, file)
    if (html !== original) await writeFile(file, html)
    integrated++
  }

  if (integrated === 0) {
    throw new Error(`Unified API reference at ${apiDirectory} contains no Dokka HTML pages.`)
  }

  const stylesDirectory = path.join(apiDirectory, 'styles')
  await mkdir(stylesDirectory, { recursive: true })
  await copyFile(stylesheetSource, path.join(stylesDirectory, stylesheetName))
  return integrated
}

function addStylesheet(html, href) {
  if (html.includes(`href="${href}"`)) return html
  const closingHead = '</head>'
  if (!html.includes(closingHead)) throw new Error('Dokka HTML page has no closing head element.')
  return html.replace(closingHead, `<link href="${href}" rel="Stylesheet">\n${closingHead}`)
}

function addUserGuideLink(html, href, file) {
  if (html.includes(`class="${linkClass}"`)) return html
  const libraryLink = /(<a class="library-name--link"[^>]*>[\s\S]*?<\/a>)/
  if (!libraryLink.test(html)) throw new Error(`Dokka HTML page ${file} has no library navigation link.`)
  return html.replace(
    libraryLink,
    `$1\n        <a class="${linkClass}" href="${href}" tabindex="1">User guide</a>`
  )
}

async function findHtmlFiles(directory) {
  const entries = await readdir(directory, { withFileTypes: true })
  const files = []
  for (const entry of entries) {
    const child = path.join(directory, entry.name)
    if (entry.isDirectory()) files.push(...(await findHtmlFiles(child)))
    else if (entry.isFile() && entry.name.endsWith('.html')) files.push(child)
  }
  return files.sort()
}

async function isDirectory(directory) {
  try {
    return (await stat(directory)).isDirectory()
  } catch (cause) {
    if (cause?.code === 'ENOENT') return false
    throw cause
  }
}

function relativeUrl(from, to) {
  const relative = path.relative(from, to).split(path.sep).join('/')
  return relative === '' ? '' : relative
}

function joinUrl(...segments) {
  return segments.filter(Boolean).join('/')
}

function directoryUrl(relative) {
  const url = relative.split(path.sep).join('/')
  return `${url || '.'}/`
}

const script = fileURLToPath(import.meta.url)
if (process.argv[1] && path.resolve(process.argv[1]) === script) {
  const repository = path.resolve(path.dirname(script), '..', '..')
  const apiDirectory = path.join(repository, 'site', 'target', 'site', 'docs', 'api')
  const stylesheet = path.join(repository, 'site', 'src', 'main', 'dokka', 'tapik-integration.css')
  integrateDokkaReference(apiDirectory, stylesheet)
    .then(count => console.log(`Integrated ${count} tapik API reference pages`))
    .catch(cause => {
      console.error(cause.message)
      process.exitCode = 1
    })
}
