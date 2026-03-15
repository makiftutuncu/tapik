@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.codec

/**
 * Common factory surface for built-in scalar variants.
 *
 * Tapik reuses this shape for different abstractions, such as codecs, headers, and parameters, so the
 * DSL can ask for "the default Int representation" without caring what concrete type is being built.
 *
 * @param TUnit representation type used for Kotlin `Unit`.
 * @param TBoolean representation type used for `Boolean` values.
 * @param TByte representation type used for `Byte` values.
 * @param TShort representation type used for `Short` values.
 * @param TInt representation type used for `Int` values.
 * @param TLong representation type used for `Long` values.
 * @param TFloat representation type used for `Float` values.
 * @param TDouble representation type used for `Double` values.
 * @param TBigInteger representation type used for `BigInteger` values.
 * @param TBigDecimal representation type used for `BigDecimal` values.
 * @param TString representation type used for `String` values.
 * @param TUUID representation type used for `UUID` values.
 */
interface Defaults<TUnit, TBoolean, TByte, TShort, TInt, TLong, TFloat, TDouble, TBigInteger, TBigDecimal, TString, TUUID> {
    /** Returns the default representation of a `Unit` value identified by [name]. */
    fun unit(name: String): TUnit

    /** Returns the default representation of a `Boolean` value identified by [name]. */
    fun boolean(name: String): TBoolean

    /** Returns the default representation of a `Byte` value identified by [name]. */
    fun byte(name: String): TByte

    /** Returns the default representation of a `Short` value identified by [name]. */
    fun short(name: String): TShort

    /** Returns the default representation of an `Int` value identified by [name]. */
    fun int(name: String): TInt

    /** Returns the default representation of a `Long` value identified by [name]. */
    fun long(name: String): TLong

    /** Returns the default representation of a `Float` value identified by [name]. */
    fun float(name: String): TFloat

    /** Returns the default representation of a `Double` value identified by [name]. */
    fun double(name: String): TDouble

    /** Returns the default representation of a `BigInteger` value identified by [name]. */
    fun bigInteger(name: String): TBigInteger

    /** Returns the default representation of a `BigDecimal` value identified by [name]. */
    fun bigDecimal(name: String): TBigDecimal

    /** Returns the default representation of a `String` value identified by [name]. */
    fun string(name: String): TString

    /** Returns the default representation of a `UUID` value identified by [name]. */
    fun uuid(name: String): TUUID
}
