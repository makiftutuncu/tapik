import assert from 'node:assert/strict'
import test from 'node:test'

import normalizer from './normalize-documentation-components.cjs'

test('normalizes legacy documentation into the current Antora component', () => {
  const contentAggregate = [
    { name: 'tapik', version: 'v0.5.0' },
    { name: 'tapik', version: 'v0.4.1' },
    { name: 'docs', version: 'v0.6.0' },
    { name: 'another-component', version: 'v1.0.0' }
  ]

  const count = normalizer.normalizeDocumentationComponents(contentAggregate, 'tapik', 'docs')

  assert.equal(count, 2)
  assert.deepEqual(contentAggregate, [
    { name: 'docs', version: 'v0.5.0' },
    { name: 'docs', version: 'v0.4.1' },
    { name: 'docs', version: 'v0.6.0' },
    { name: 'another-component', version: 'v1.0.0' }
  ])
})

test('registers normalization before Antora classifies aggregated content', () => {
  let listener
  const context = {
    once(event, registered) {
      assert.equal(event, 'contentAggregated')
      listener = registered
    }
  }
  normalizer.register.call(context, { config: { legacy_component: 'tapik', component: 'docs' } })
  const contentAggregate = [{ name: 'tapik', version: 'v0.5.0' }]

  listener({ contentAggregate })

  assert.equal(contentAggregate[0].name, 'docs')
})
