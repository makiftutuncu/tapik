import assert from 'node:assert/strict'
import { mkdtemp, mkdir, readFile, rm, writeFile } from 'node:fs/promises'
import { tmpdir } from 'node:os'
import path from 'node:path'
import test from 'node:test'

import { integrateDokkaReference } from './integrate-dokka-reference.mjs'

test('links every Dokka page back to its narrative documentation version', async () => {
  const workspace = await mkdtemp(path.join(tmpdir(), 'tapik-dokka-'))
  try {
    const api = path.join(workspace, 'docs', 'api')
    const nested = path.join(api, 'tapik', 'dev.akif.tapik', '-api')
    await mkdir(nested, { recursive: true })
    await writeFile(path.join(api, 'index.html'), dokkaPage('index.html'))
    await writeFile(path.join(nested, 'index.html'), dokkaPage('../../../index.html'))
    const stylesheet = path.join(workspace, 'tapik.css')
    await writeFile(stylesheet, '.tapik-user-guide-link { color: white; }\n')

    await integrateDokkaReference(api, stylesheet)
    await integrateDokkaReference(api, stylesheet)

    const root = await readFile(path.join(api, 'index.html'), 'utf8')
    assert.equal(matches(root, 'class="tapik-user-guide-link"'), 1)
    assert.match(root, /href="\.\.\/"[^>]*>User guide<\/a>/)
    assert.match(root, /href="styles\/tapik-integration\.css"/)

    const declaration = await readFile(path.join(nested, 'index.html'), 'utf8')
    assert.equal(matches(declaration, 'class="tapik-user-guide-link"'), 1)
    assert.match(declaration, /href="\.\.\/\.\.\/\.\.\/\.\.\/"[^>]*>User guide<\/a>/)
    assert.match(declaration, /href="\.\.\/\.\.\/\.\.\/styles\/tapik-integration\.css"/)

    assert.equal(
      await readFile(path.join(api, 'styles', 'tapik-integration.css'), 'utf8'),
      '.tapik-user-guide-link { color: white; }\n'
    )
  } finally {
    await rm(workspace, { recursive: true, force: true })
  }
})

test('rejects a missing unified reference', async () => {
  const workspace = await mkdtemp(path.join(tmpdir(), 'tapik-dokka-'))
  try {
    await assert.rejects(
      integrateDokkaReference(path.join(workspace, 'docs', 'api'), path.join(workspace, 'tapik.css')),
      /Build the unified API reference first/
    )
  } finally {
    await rm(workspace, { recursive: true, force: true })
  }
})

function dokkaPage(home) {
  return `<!DOCTYPE html>
<html><head></head><body><header>
<a class="library-name--link" href="${home}" tabindex="1">tapik</a>
</header></body></html>`
}

function matches(value, pattern) {
  return value.split(pattern).length - 1
}
