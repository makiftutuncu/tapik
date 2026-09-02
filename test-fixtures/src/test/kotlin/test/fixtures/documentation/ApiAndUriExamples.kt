package dev.akif.tapik.test.fixtures.documentation

import dev.akif.tapik.*
import dev.akif.tapik.test.fixtures.library.authorIdFormat

// tag::api-and-endpoints[]
class AuthorRoutes : Api() {
    val list by get(root / "authors")

    val get by get(root / "authors" / path("authorId", authorIdFormat))
}
// end::api-and-endpoints[]

// tag::uri-parameters[]
class AssetRoutes : Api() {
    val search by
        get(
            root / "assets" +
                query.string("name").optional() +
                query.int("page").optional(default = 1) +
                query.uuid("ownerId").repeated().optional()
        )

    val download by get(root / "assets" / path.remaining("path"))
}
// end::uri-parameters[]
