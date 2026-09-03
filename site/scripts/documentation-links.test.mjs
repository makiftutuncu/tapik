import assert from 'node:assert/strict'
import test from 'node:test'
import links from './documentation-links.cjs'

test('selects matching API archives and uses latest for legacy documentation', () => {
  assert.equal(links.apiReferenceUrl({ activeVersionSegment: '' }), '/docs/api/')
  assert.equal(links.apiReferenceUrl({ activeVersionSegment: 'v0.6.0' }), '/docs/v0.6.0/api/')
  assert.equal(links.apiReferenceUrl({ activeVersionSegment: 'v0.5.0', origins: [{ descriptor: { name: 'tapik' } }] }), '/docs/api/')
})

test('repairs only known legacy link spellings without rewriting the release tag', () => {
  assert.equal(
    links.repairLegacyLinks('link:api/index.html[API] xref:code-generation/index.adoc#extending-tapik[Extend]'),
    'link:{page-api-reference-url}[API] xref:code-generation/index.adoc#_extending_tapik[Extend]'
  )
})
