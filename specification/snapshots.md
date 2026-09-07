# Structural snapshots

The shallow-snapshot rule in `product.md` applies to tapik-owned structure, not user-owned data. A constructor or
`copy` accepts ordinary Kotlin collections, stores an ordered defensive copy, and never exposes mutable stored
containers. Destructuring has the same boundary as property access. Value equality and hashing use the snapshots.

Kotlin 2.4 explicit backing fields may hide mutable implementation types behind read-only properties in internal
builders, such as the OpenAPI schema registry. They expose the same object and do not protect against mutable casts,
so public snapshot boundaries still require defensive copies and unmodifiable containers. Explicit backing fields
cannot have custom getters or be declared on primary-constructor parameters; they do not replace copying byte-array
getters or snapshot-safe value operations. Prefer them where the narrower internal type is useful, not where a plain
property already stores an immutable snapshot of its declared type.

## Audited boundaries

| Area | Protected structure |
| --- | --- |
| Core decoding | `DecodeResult.Failure.errors`; successful values retain caller identity |
| Core contracts | URI segments, endpoint tags, and status sets |
| Core schemas | Enum values, object properties, union alternatives, and discriminator mappings |
| API and tuple views | API endpoint/inclusion lists and tuple value lists are fresh views; changing a view cannot change the source |
| Compiler registry | Generated `ApiRegistry.apis` is unmodifiable and retains the created API instances |
| Discovery | API and generation-target catalogs snapshot providers' lists; third-party provider getters remain provider-owned |
| Generation | Request APIs, result artifacts, configuration lists/maps, and API-selection sets |
| Compiled/source types | Compiled API endpoints, endpoint property paths, generic arguments, and sealed-variant fields |
| OpenAPI document | Paths, operations, tags, parameters, responses, headers, media content, and components |
| OpenAPI schemas | Type/enum/required lists, properties, alternatives, discriminator mappings, and JSON default/constant containers |
| Transport values | RestClient request/response byte arrays and response headers, including each header-value list |

Private/internal generation working models and buffers do not escape as public structural values. Public format and
codec integrations carry user-owned values and configuration by identity. Generated handler/client domain payloads
also remain user-owned: this rule does not introduce copying of models or their collections. Spring request/response
objects are framework-owned; tapik's encoded transport value wrappers copy their bytes on ingress and access.

## Verification

Kotest specifications exercise caller mutation after construction and `copy`, mutation through destructured values,
JVM mutable collection casts and map views, stable equality/hash codes, and declaration order. Compiler tests load a
real generated registry and attempt element replacement, not only removal (an array-backed list already rejects
removal). OpenAPI tests cover each public collection-bearing model and nested JSON containers. Transport tests cover
constructor, getter, `copy`, and component byte-array boundaries. Domain identity tests prevent accidental deep copies.
