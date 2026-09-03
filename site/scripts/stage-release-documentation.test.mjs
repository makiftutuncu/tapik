import assert from 'node:assert/strict'
import { mkdtemp, mkdir, readFile, rm, writeFile } from 'node:fs/promises'
import { tmpdir } from 'node:os'
import path from 'node:path'
import test from 'node:test'

import { stageReleaseApiReference, validateReleaseVersion } from './stage-release-documentation.mjs'

test('validates one release version across Maven, Antora, and Git', async () => {
  const repository = await releaseWorkspace('0.6.0', 'v0.6.0')
  try {
    assert.equal(await validateReleaseVersion(repository, 'v0.6.0'), 'v0.6.0')
  } finally {
    await rm(repository, { recursive: true, force: true })
  }
})

test('rejects inconsistent or unsafe release versions', async () => {
  const repository = await releaseWorkspace('0.6.0', 'v0.6.1')
  try {
    await assert.rejects(validateReleaseVersion(repository, 'v0.6.0'), /docs\/antora.yml declares v0.6.1/)
    await assert.rejects(validateReleaseVersion(repository, '../v0.6.0'), /stable release tag/)
  } finally {
    await rm(repository, { recursive: true, force: true })
  }
})

test('archives the integrated API reference under the explicit release version', async () => {
  const repository = await releaseWorkspace('0.6.0', 'v0.6.0')
  try {
    const api = path.join(repository, 'site', 'target', 'site', 'docs', 'api')
    await mkdir(path.join(api, 'styles'), { recursive: true })
    await writeFile(path.join(api, 'index.html'), '<!DOCTYPE html><a href="../">User guide</a>')
    await writeFile(path.join(api, 'styles', 'tapik.css'), 'body {}')
    const versionedDocumentation = path.join(repository, 'site', 'target', 'site', 'docs', 'v0.6.0')
    await mkdir(versionedDocumentation, { recursive: true })
    await writeFile(path.join(versionedDocumentation, 'index.html'), '<!DOCTYPE html><title>tapik v0.6.0</title>')
    const previousApi = path.join(repository, 'site', 'target', 'site', 'docs', 'v0.5.0', 'api')
    await mkdir(previousApi, { recursive: true })
    await writeFile(path.join(previousApi, 'index.html'), 'immutable 0.5 reference')

    const archived = await stageReleaseApiReference(repository, 'v0.6.0')

    assert.equal(archived, path.join(repository, 'site', 'target', 'site', 'docs', 'v0.6.0', 'api'))
    assert.equal(
      await readFile(path.join(archived, 'index.html'), 'utf8'),
      '<!DOCTYPE html><a href="../">User guide</a>'
    )
    assert.equal(await readFile(path.join(archived, 'styles', 'tapik.css'), 'utf8'), 'body {}')
    assert.equal(await readFile(path.join(api, 'index.html'), 'utf8'), '<!DOCTYPE html><a href="../">User guide</a>')
    assert.equal(await readFile(path.join(previousApi, 'index.html'), 'utf8'), 'immutable 0.5 reference')
  } finally {
    await rm(repository, { recursive: true, force: true })
  }
})

test('requires the unified API reference before staging a release', async () => {
  const repository = await releaseWorkspace('0.6.0', 'v0.6.0')
  try {
    await assert.rejects(stageReleaseApiReference(repository, 'v0.6.0'), /Unified API reference not found/)
  } finally {
    await rm(repository, { recursive: true, force: true })
  }
})

test('requires tagged narrative documentation for the release version', async () => {
  const repository = await releaseWorkspace('0.6.0', 'v0.6.0')
  try {
    const api = path.join(repository, 'site', 'target', 'site', 'docs', 'api')
    await mkdir(api, { recursive: true })
    await writeFile(path.join(api, 'index.html'), '<!DOCTYPE html>')

    await assert.rejects(stageReleaseApiReference(repository, 'v0.6.0'), /Tagged narrative documentation not found/)
  } finally {
    await rm(repository, { recursive: true, force: true })
  }
})

async function releaseWorkspace(mavenVersion, documentationVersion) {
  const repository = await mkdtemp(path.join(tmpdir(), 'tapik-release-docs-'))
  await mkdir(path.join(repository, 'docs'), { recursive: true })
  await writeFile(
    path.join(repository, 'pom.xml'),
    `<project><groupId>dev.akif</groupId><artifactId>tapik-parent</artifactId><version>${mavenVersion}</version></project>`
  )
  await writeFile(path.join(repository, 'docs', 'antora.yml'), `name: docs\nversion: ${documentationVersion}\n`)
  return repository
}
