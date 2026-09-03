import assert from 'node:assert/strict'
import { mkdtemp, mkdir, rm, writeFile } from 'node:fs/promises'
import { tmpdir } from 'node:os'
import path from 'node:path'
import test from 'node:test'

import { checkDocumentation } from './check-documentation.mjs'

test('resolves directory, relative, encoded, fragment, and same-site absolute links', async () => {
  await withSite({
    'index.html': '<a href="/docs/?mode=preview#start">Guide</a><img src="logo.svg">',
    'logo.svg': '<svg/>',
    'docs/index.html': '<h1 id="start">Guide</h1><a href="api/">API</a><a href="a%20page.html#an%20anchor">Page</a>',
    'docs/a page.html': '<h1 id="an anchor">Page</h1>',
    'docs/api/index.html': '<a href="../#start">Guide</a><a href="https://tapik.akif.dev/">Home</a>'
  }, async site => {
    const result = await checkDocumentation(site, { expectedPages: ['index.html', 'docs/api/index.html'] })
    assert.deepEqual(result.errors, [])
    assert.equal(result.pagesChecked, 4)
    assert.equal(result.linksChecked, 6)
  })
})

test('reports missing pages, anchors, assets, and unresolved Antora references with source paths', async () => {
  await withSite({
    'index.html': '<a href="missing.html">Missing</a><a href="guide.html#absent">Anchor</a>' +
      '<script src="missing.js"></script><link rel="stylesheet" href="missing.css">' +
      '<a class="xref unresolved" href="#">Unresolved</a>',
    'guide.html': '<h1 id="present">Guide</h1>'
  }, async site => {
    const result = await checkDocumentation(site, { expectedPages: ['index.html', 'docs/api/index.html'] })
    assert.equal(result.errors.length, 6)
    assert.ok(result.errors.some(error => error.includes('Required page missing: docs/api/index.html')))
    for (const target of ['missing.html', 'guide.html#absent', 'missing.js', 'missing.css']) {
      assert.ok(result.errors.some(error => error.includes('index.html') && error.includes(target)), target)
    }
    assert.ok(result.errors.some(error => error.includes('Unresolved cross-reference')))
  })
})

test('ignores external links and empty fragments but rejects malformed internal URLs', async () => {
  await withSite({
    'index.html': '<a href="https://example.invalid/absent">External</a><a href="mailto:team@example.com">Mail</a>' +
      '<a href="#">Top</a><a href="broken%XX.html">Malformed</a>'
  }, async site => {
    const result = await checkDocumentation(site, { expectedPages: ['index.html'] })
    assert.equal(result.errors.length, 1)
    assert.match(result.errors[0], /Malformed internal URL.*broken%XX/)
  })
})

test('allows local edit links in previews but rejects them in release output', async () => {
  await withSite({ 'index.html': '<a href="file:///source.adoc">Edit</a>' }, async site => {
    assert.deepEqual((await checkDocumentation(site, { expectedPages: [], release: false })).errors, [])
    assert.match((await checkDocumentation(site, { expectedPages: [], release: true })).errors[0], /Local file link/)
  })
})

async function withSite(files, action) {
  const site = await mkdtemp(path.join(tmpdir(), 'tapik-site-check-'))
  try {
    for (const [name, contents] of Object.entries(files)) {
      const file = path.join(site, name)
      await mkdir(path.dirname(file), { recursive: true })
      await writeFile(file, contents)
    }
    await action(site)
  } finally {
    await rm(site, { recursive: true, force: true })
  }
}
