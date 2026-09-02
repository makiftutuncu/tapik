package dev.akif.tapik

/**
 * Factory shape for named values backed by tapik's default scalar formats.
 *
 * Kotlin cannot express a higher-kinded `Named<Value>` return type, so each concrete result type is retained as an
 * explicit type parameter. Implementations such as path variables, queries, and headers therefore preserve their
 * precise domain type for every factory.
 */
interface NamedDefaults<
    out BooleanDefault,
    out ByteDefault,
    out ShortDefault,
    out IntDefault,
    out LongDefault,
    out FloatDefault,
    out DoubleDefault,
    out BigIntegerDefault,
    out BigDecimalDefault,
    out StringDefault,
    out UuidDefault,
    out LocalDateDefault,
    out LocalTimeDefault,
    out LocalDateTimeDefault,
    out OffsetTimeDefault,
    out OffsetDateTimeDefault,
    out InstantDefault,
    out DurationDefault,
    out PeriodDefault
> {
    /** Builds the named [BooleanDefault]. */
    fun boolean(name: String): BooleanDefault

    /** Builds the named [ByteDefault]. */
    fun byte(name: String): ByteDefault

    /** Builds the named [ShortDefault]. */
    fun short(name: String): ShortDefault

    /** Builds the named [IntDefault]. */
    fun int(name: String): IntDefault

    /** Builds the named [LongDefault]. */
    fun long(name: String): LongDefault

    /** Builds the named [FloatDefault]. */
    fun float(name: String): FloatDefault

    /** Builds the named [DoubleDefault]. */
    fun double(name: String): DoubleDefault

    /** Builds the named [BigIntegerDefault]. */
    fun bigInteger(name: String): BigIntegerDefault

    /** Builds the named [BigDecimalDefault]. */
    fun bigDecimal(name: String): BigDecimalDefault

    /** Builds the named [StringDefault]. */
    fun string(name: String): StringDefault

    /** Builds the named [UuidDefault]. */
    fun uuid(name: String): UuidDefault

    /** Builds the named [LocalDateDefault]. */
    fun localDate(name: String): LocalDateDefault

    /** Builds the named [LocalTimeDefault]. */
    fun localTime(name: String): LocalTimeDefault

    /** Builds the named [LocalDateTimeDefault]. */
    fun localDateTime(name: String): LocalDateTimeDefault

    /** Builds the named [OffsetTimeDefault]. */
    fun offsetTime(name: String): OffsetTimeDefault

    /** Builds the named [OffsetDateTimeDefault]. */
    fun offsetDateTime(name: String): OffsetDateTimeDefault

    /** Builds the named [InstantDefault]. */
    fun instant(name: String): InstantDefault

    /** Builds the named [DurationDefault]. */
    fun duration(name: String): DurationDefault

    /** Builds the named [PeriodDefault]. */
    fun period(name: String): PeriodDefault
}
