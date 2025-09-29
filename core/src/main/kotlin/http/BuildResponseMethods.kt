// This file is auto-generated. Do not edit manually as your changes will be lost.
@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

private fun <B, OB: Body<B>, R: Response<B>, Option> OutputBody<OB>.decode(
   status: Status,
   bytes: ByteArray,
   buildResponse: (B) -> R,
   constructOption: (R) -> Option
): Option =
   body.codec.decode(bytes).fold(
       { error("Cannot decode response for status $status as ${body.codec.sourceClass.simpleName}") },
       { body -> constructOption(buildResponse(body)) }
   )