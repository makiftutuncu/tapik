@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.codec

/**
 * Contract that supplies default codecs for common scalar values.
 *
 * Each type parameter represents the codec shape used for a particular primitive when materialising
 * endpoint metadata or generated clients.
 *
 * @param TUnit codec type used for Kotlin `Unit`.
 * @param TBoolean codec type used for `Boolean` values.
 * @param TByte codec type used for `Byte` values.
 * @param TShort codec type used for `Short` values.
 * @param TInt codec type used for `Int` values.
 * @param TLong codec type used for `Long` values.
 * @param TFloat codec type used for `Float` values.
 * @param TDouble codec type used for `Double` values.
 * @param TBigInteger codec type used for `BigInteger` values.
 * @param TBigDecimal codec type used for `BigDecimal` values.
 * @param TString codec type used for `String` values.
 * @param TUUID codec type used for `UUID` values.
 */
interface Defaults<TUnit, TBoolean, TByte, TShort, TInt, TLong, TFloat, TDouble, TBigInteger, TBigDecimal, TString, TUUID> {
    /**
     * Builds a codec for representing a `Unit` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `Unit`.
     */
    fun unit(name: String): TUnit

    /**
     * Builds a codec for representing a `Boolean` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `Boolean`.
     */
    fun boolean(name: String): TBoolean

    /**
     * Builds a codec for representing a `Byte` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `Byte`.
     */
    fun byte(name: String): TByte

    /**
     * Builds a codec for representing a `Short` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `Short`.
     */
    fun short(name: String): TShort

    /**
     * Builds a codec for representing an `Int` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `Int`.
     */
    fun int(name: String): TInt

    /**
     * Builds a codec for representing a `Long` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `Long`.
     */
    fun long(name: String): TLong

    /**
     * Builds a codec for representing a `Float` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `Float`.
     */
    fun float(name: String): TFloat

    /**
     * Builds a codec for representing a `Double` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `Double`.
     */
    fun double(name: String): TDouble

    /**
     * Builds a codec for representing a `BigInteger` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `BigInteger`.
     */
    fun bigInteger(name: String): TBigInteger

    /**
     * Builds a codec for representing a `BigDecimal` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `BigDecimal`.
     */
    fun bigDecimal(name: String): TBigDecimal

    /**
     * Builds a codec for representing a `String` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `String`.
     */
    fun string(name: String): TString

    /**
     * Builds a codec for representing a `UUID` value.
     *
     * @param name human readable identifier used in error messages.
     * @return default codec for `UUID`.
     */
    fun uuid(name: String): TUUID
}
