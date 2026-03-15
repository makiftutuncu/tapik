@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import dev.akif.tapik.codec.Defaults
import dev.akif.tapik.codec.StringCodec
import dev.akif.tapik.codec.StringCodecs
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

/**
 * Describes a typed value that participates in URI construction.
 *
 * Tapik treats path variables and query parameters as one family so URI builders can accumulate
 * them in declaration order while generators still know where each value belongs on the wire.
 *
 * @property name canonical parameter name.
 * @property codec codec used to encode and decode the parameter value.
 * @property position whether the parameter becomes part of the path or query string.
 * @property required whether callers must provide a value explicitly.
 */
sealed interface Parameter<T : Any> {
    /** Canonical parameter name. */
    val name: String

    /** Codec responsible for encoding and decoding parameter values. */
    val codec: StringCodec<T>

    /** Location of the parameter within the HTTP request. */
    val position: ParameterPosition

    /**
     * Indicates whether the parameter must be supplied by the caller.
     *
     * `true` for required parameters, `false` otherwise.
     */
    val required: Boolean
}

/**
 * Required parameter rendered as a `{name}` segment inside the path template.
 *
 * Path variables always remain required because omitting one would change the shape of the URI.
 */
data class PathVariable<P : Any>(
    override val name: String,
    override val codec: StringCodec<P>
) : Parameter<P> {
    override val position: ParameterPosition = ParameterPosition.Path
    override val required: Boolean = true

    /** Factory helpers for common path variable types. */
    companion object : Defaults<PathVariable<Unit>, PathVariable<Boolean>, PathVariable<Byte>, PathVariable<Short>, PathVariable<Int>, PathVariable<Long>, PathVariable<Float>, PathVariable<Double>, PathVariable<BigInteger>, PathVariable<BigDecimal>, PathVariable<String>, PathVariable<UUID>> {
        /** Creates a path variable backed by Tapik's `Unit` string codec. */
        override fun unit(name: String): PathVariable<Unit> = PathVariable(name, StringCodecs.unit(name))

        /** Creates a path variable backed by the Boolean string codec. */
        override fun boolean(name: String): PathVariable<Boolean> = PathVariable(name, StringCodecs.boolean(name))

        /** Creates a path variable backed by the Byte string codec. */
        override fun byte(name: String): PathVariable<Byte> = PathVariable(name, StringCodecs.byte(name))

        /** Creates a path variable backed by the Short string codec. */
        override fun short(name: String): PathVariable<Short> = PathVariable(name, StringCodecs.short(name))

        /** Creates a path variable backed by the Int string codec. */
        override fun int(name: String): PathVariable<Int> = PathVariable(name, StringCodecs.int(name))

        /** Creates a path variable backed by the Long string codec. */
        override fun long(name: String): PathVariable<Long> = PathVariable(name, StringCodecs.long(name))

        /** Creates a path variable backed by the Float string codec. */
        override fun float(name: String): PathVariable<Float> = PathVariable(name, StringCodecs.float(name))

        /** Creates a path variable backed by the Double string codec. */
        override fun double(name: String): PathVariable<Double> = PathVariable(name, StringCodecs.double(name))

        /** Creates a path variable backed by the BigInteger string codec. */
        override fun bigInteger(name: String): PathVariable<BigInteger> =
            PathVariable(name, StringCodecs.bigInteger(name))

        /** Creates a path variable backed by the BigDecimal string codec. */
        override fun bigDecimal(name: String): PathVariable<BigDecimal> =
            PathVariable(name, StringCodecs.bigDecimal(name))

        /** Creates a path variable backed by the String string codec. */
        override fun string(name: String): PathVariable<String> = PathVariable(name, StringCodecs.string(name))

        /** Creates a path variable backed by the UUID string codec. */
        override fun uuid(name: String): PathVariable<UUID> = PathVariable(name, StringCodecs.uuid(name))
    }
}

/**
 * Query-string parameter that may be required or optional.
 *
 * Query parameters become optional only when [default] is present, which gives generated code a
 * value to use when the caller omits the parameter.
 *
 * @property default fallback used when the parameter is omitted. `None` means the parameter is required.
 */
data class QueryParameter<Q : Any>(
    override val name: String,
    override val codec: StringCodec<Q>,
    val default: Option<Q?>
) : Parameter<Q> {
    override val position: ParameterPosition = ParameterPosition.Query
    override val required: Boolean = default.isNone()

    /** Marks this query parameter as optional by recording the [default] to use when it is absent. */
    fun optional(default: Q? = null): QueryParameter<Q> = copy(default = Some(default))

    /** Factory helpers for common query parameter types. */
    companion object : Defaults<QueryParameter<Unit>, QueryParameter<Boolean>, QueryParameter<Byte>, QueryParameter<Short>, QueryParameter<Int>, QueryParameter<Long>, QueryParameter<Float>, QueryParameter<Double>, QueryParameter<BigInteger>, QueryParameter<BigDecimal>, QueryParameter<String>, QueryParameter<UUID>> {
        /** Creates a query parameter backed by Tapik's `Unit` string codec. */
        override fun unit(name: String): QueryParameter<Unit> =
            QueryParameter(name, StringCodecs.unit(name), default = None)

        /** Creates a query parameter backed by the Boolean string codec. */
        override fun boolean(name: String): QueryParameter<Boolean> =
            QueryParameter(name, StringCodecs.boolean(name), default = None)

        /** Creates a query parameter backed by the Byte string codec. */
        override fun byte(name: String): QueryParameter<Byte> =
            QueryParameter(name, StringCodecs.byte(name), default = None)

        /** Creates a query parameter backed by the Short string codec. */
        override fun short(name: String): QueryParameter<Short> =
            QueryParameter(name, StringCodecs.short(name), default = None)

        /** Creates a query parameter backed by the Int string codec. */
        override fun int(name: String): QueryParameter<Int> =
            QueryParameter(name, StringCodecs.int(name), default = None)

        /** Creates a query parameter backed by the Long string codec. */
        override fun long(name: String): QueryParameter<Long> =
            QueryParameter(name, StringCodecs.long(name), default = None)

        /** Creates a query parameter backed by the Float string codec. */
        override fun float(name: String): QueryParameter<Float> =
            QueryParameter(name, StringCodecs.float(name), default = None)

        /** Creates a query parameter backed by the Double string codec. */
        override fun double(name: String): QueryParameter<Double> =
            QueryParameter(name, StringCodecs.double(name), default = None)

        /** Creates a query parameter backed by the BigInteger string codec. */
        override fun bigInteger(name: String): QueryParameter<BigInteger> =
            QueryParameter(name, StringCodecs.bigInteger(name), default = None)

        /** Creates a query parameter backed by the BigDecimal string codec. */
        override fun bigDecimal(name: String): QueryParameter<BigDecimal> =
            QueryParameter(name, StringCodecs.bigDecimal(name), default = None)

        /** Creates a query parameter backed by the String string codec. */
        override fun string(name: String): QueryParameter<String> =
            QueryParameter(name, StringCodecs.string(name), default = None)

        /** Creates a query parameter backed by the UUID string codec. */
        override fun uuid(name: String): QueryParameter<UUID> =
            QueryParameter(name, StringCodecs.uuid(name), default = None)
    }
}
