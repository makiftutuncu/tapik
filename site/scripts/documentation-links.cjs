'use strict'

function isLegacy(version) {
  return [...(version.origins || [])].some(origin => origin.descriptor?.name === 'tapik')
}

function apiReferenceUrl(version) {
  const segment = isLegacy(version) ? '' : version.activeVersionSegment
  return `/docs/${segment ? segment + '/' : ''}api/`
}

function repairLegacyLinks(source) {
  return source
    .replaceAll('link:api/index.html[', 'link:{page-api-reference-url}[')
    .replaceAll('code-generation/index.adoc#extending-tapik[', 'code-generation/index.adoc#_extending_tapik[')
}

function register() {
  this.once('contentClassified', ({ contentCatalog }) => {
    for (const component of contentCatalog.getComponents()) {
      for (const version of component.versions) {
        version.asciidoc = {
          ...version.asciidoc,
          attributes: { ...version.asciidoc?.attributes, 'page-api-reference-url': apiReferenceUrl(version) }
        }
        if (!isLegacy(version)) continue
        for (const file of contentCatalog.findBy({ component: component.name, version: version.version })) {
          if (file.src.extname === '.adoc') file.contents = Buffer.from(repairLegacyLinks(file.contents.toString()))
        }
      }
    }
  })
}

module.exports = { register, apiReferenceUrl, repairLegacyLinks }
