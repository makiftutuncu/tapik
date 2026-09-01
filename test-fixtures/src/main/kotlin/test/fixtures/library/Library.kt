package dev.akif.tapik.test.fixtures.library

import dev.akif.tapik.Api

/** All library operations composed without changing the standalone domain APIs. */
object Library : Api() {
    val authors by including(Authors)
    val books by including(Books)
    val rentals by including(Rentals)
}
