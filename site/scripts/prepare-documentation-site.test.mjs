import assert from 'node:assert/strict'
import { mkdtemp, mkdir, readFile, readdir, rm, writeFile } from 'node:fs/promises'
import { tmpdir } from 'node:os'
import path from 'node:path'
import test from 'node:test'
import { prepareDocumentationSite } from './prepare-documentation-site.mjs'

test('clears stale generated pages while preserving Dokka and refreshing the landing assets', async () => {
  const repository = await mkdtemp(path.join(tmpdir(), 'tapik-site-prepare-'))
  try {
    const site = path.join(repository, 'site/target/site')
    await mkdir(path.join(site, 'docs/api'), { recursive: true })
    await writeFile(path.join(site, 'docs/api/index.html'), 'Dokka')
    await writeFile(path.join(site, 'docs/deleted.html'), 'stale')
    await writeFile(path.join(site, 'old.html'), 'stale')
    const resources = path.join(repository, 'site/src/main/resources')
    await mkdir(resources, { recursive: true })
    await writeFile(path.join(resources, 'index.html'), 'Landing')
    await prepareDocumentationSite(repository)
    assert.deepEqual((await readdir(site)).sort(), ['docs', 'index.html'])
    assert.deepEqual(await readdir(path.join(site, 'docs')), ['api'])
    assert.equal(await readFile(path.join(site, 'docs/api/index.html'), 'utf8'), 'Dokka')
    assert.equal(await readFile(path.join(site, 'index.html'), 'utf8'), 'Landing')
  } finally {
    await rm(repository, { recursive: true, force: true })
  }
})
