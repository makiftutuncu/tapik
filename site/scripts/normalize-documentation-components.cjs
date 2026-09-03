'use strict'

function normalizeDocumentationComponents(contentAggregate, legacyComponent = 'tapik', component = 'docs') {
  const currentVersions = new Set(
    contentAggregate.filter(candidate => candidate.name === component).map(candidate => candidate.version)
  )
  let normalized = 0
  for (const candidate of contentAggregate) {
    if (candidate.name !== legacyComponent) continue
    if (currentVersions.has(candidate.version)) {
      throw new Error(
        `Cannot normalize ${candidate.version}@${legacyComponent}: ${candidate.version}@${component} already exists.`
      )
    }
    candidate.name = component
    currentVersions.add(candidate.version)
    normalized++
  }
  return normalized
}

function register({ config: { legacy_component: legacyComponent = 'tapik', component = 'docs' } = {} } = {}) {
  this.once('contentAggregated', ({ contentAggregate }) => {
    normalizeDocumentationComponents(contentAggregate, legacyComponent, component)
  })
}

module.exports = { normalizeDocumentationComponents, register }
