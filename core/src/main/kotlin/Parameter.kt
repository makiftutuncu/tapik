@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import dev.akif.tapik.codec.Defaults
import dev.akif.tapik.codec.StringCodec
import dev.akif.tapik.codec.StringCodecs
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID

/**
 * Describes an HTTP parameter that participates in an endpoint URI.
 *
 * @property name canonical parameter name.
 * @property codec codec used to encode and decode parameter values.
 * @property position declares whether the parameter belongs to the path or query section.
 * @property required indicates whether a value must be supplied by the caller.
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

/** Required path parameter definition.
 * @property name canonical parameter name embedded in the URI template.
 * @property codec codec used to encode and decode path values.
 */
data class PathVariable<P : Any>(
    override val name: String,
    override val codec: StringCodec<P>
) : Parameter<P> {
    override val position: ParameterPosition = ParameterPosition.Path
    override val required: Boolean = true

    /** Factory helpers for building [PathVariable] definitions backed by common codecs. */
    companion object : Defaults<PathVariable<Unit>, PathVariable<Boolean>, PathVariable<Byte>, PathVariable<Short>, PathVariable<Int>, PathVariable<Long>, PathVariable<Float>, PathVariable<Double>, PathVariable<BigInteger>, PathVariable<BigDecimal>, PathVariable<String>, PathVariable<UUID>> {
        /**
         * Creates a required [PathVariable] expecting a [Unit] value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the unit codec.
         */
        override fun unit(name: String): PathVariable<Unit> = PathVariable(name, StringCodecs.unit(name))

        /**
         * Creates a required [PathVariable] expecting a boolean value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the boolean codec.
         */
        override fun boolean(name: String): PathVariable<Boolean> = PathVariable(name, StringCodecs.boolean(name))

        /**
         * Creates a required [PathVariable] expecting a byte value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the byte codec.
         */
        override fun byte(name: String): PathVariable<Byte> = PathVariable(name, StringCodecs.byte(name))

        /**
         * Creates a required [PathVariable] expecting a short value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the short codec.
         */
        override fun short(name: String): PathVariable<Short> = PathVariable(name, StringCodecs.short(name))

        /**
         * Creates a required [PathVariable] expecting an integer value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the int codec.
         */
        override fun int(name: String): PathVariable<Int> = PathVariable(name, StringCodecs.int(name))

        /**
         * Creates a required [PathVariable] expecting a long value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the long codec.
         */
        override fun long(name: String): PathVariable<Long> = PathVariable(name, StringCodecs.long(name))

        /**
         * Creates a required [PathVariable] expecting a float value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the float codec.
         */
        override fun float(name: String): PathVariable<Float> = PathVariable(name, StringCodecs.float(name))

        /**
         * Creates a required [PathVariable] expecting a double value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the double codec.
         */
        override fun double(name: String): PathVariable<Double> = PathVariable(name, StringCodecs.double(name))

        /**
         * Creates a required [PathVariable] expecting a [BigInteger] value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the big integer codec.
         */
        override fun bigInteger(name: String): PathVariable<BigInteger> =
            PathVariable(name, StringCodecs.bigInteger(name))

        /**
         * Creates a required [PathVariable] expecting a [BigDecimal] value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the big decimal codec.
         */
        override fun bigDecimal(name: String): PathVariable<BigDecimal> =
            PathVariable(name, StringCodecs.bigDecimal(name))

        /**
         * Creates a required [PathVariable] expecting a [String] value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the string codec.
         */
        override fun string(name: String): PathVariable<String> = PathVariable(name, StringCodecs.string(name))

        /**
         * Creates a required [PathVariable] expecting a [UUID] value.
         *
         * @param name canonical parameter name embedded in the URI template.
         * @return path variable backed by the UUID codec.
         */
        override fun uuid(name: String): PathVariable<UUID> = PathVariable(name, StringCodecs.uuid(name))
    }
}

/** Query parameter definition with optional default values.
 * @property default optional fallback when the caller omits the query parameter.
 */
data class QueryParameter<Q : Any>(
    override val name: String,
    override val codec: StringCodec<Q>,
    override val required: Boolean,
    val default: Q?
) : Parameter<Q> {
    override val position: ParameterPosition = ParameterPosition.Query

    /**
     * Returns an optional version of this parameter with no default value.
     *
     * @return a copy of the parameter marked as optional with its default cleared.
     */
    val optional: QueryParameter<Q>
        get() = copy(required = false, default = null)

    /**
     * Returns an optional version of this parameter with the supplied [default] value.
     *
     * @param default default value used when the caller omits the parameter.
     * @return a copy of the parameter marked as optional with the provided default.
     * @see optional
     */
    fun optional(default: Q): QueryParameter<Q> = copy(required = false, default = default)

    /** Factory helpers for building [QueryParameter] definitions backed by common codecs. */
    companion object : Defaults<QueryParameter<Unit>, QueryParameter<Boolean>, QueryParameter<Byte>, QueryParameter<Short>, QueryParameter<Int>, QueryParameter<Long>, QueryParameter<Float>, QueryParameter<Double>, QueryParameter<BigInteger>, QueryParameter<BigDecimal>, QueryParameter<String>, QueryParameter<UUID>> {
        /**
         * Creates a required [QueryParameter] expecting a [Unit] value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the unit codec.
         */
        override fun unit(name: String): QueryParameter<Unit> =
            QueryParameter(name, StringCodecs.unit(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a boolean value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the boolean codec.
         */
        override fun boolean(name: String): QueryParameter<Boolean> =
            QueryParameter(name, StringCodecs.boolean(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a byte value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the byte codec.
         */
        override fun byte(name: String): QueryParameter<Byte> =
            QueryParameter(name, StringCodecs.byte(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a short value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the short codec.
         */
        override fun short(name: String): QueryParameter<Short> =
            QueryParameter(name, StringCodecs.short(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting an integer value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the int codec.
         */
        override fun int(name: String): QueryParameter<Int> =
            QueryParameter(name, StringCodecs.int(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a long value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the long codec.
         */
        override fun long(name: String): QueryParameter<Long> =
            QueryParameter(name, StringCodecs.long(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a float value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the float codec.
         */
        override fun float(name: String): QueryParameter<Float> =
            QueryParameter(name, StringCodecs.float(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a double value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the double codec.
         */
        override fun double(name: String): QueryParameter<Double> =
            QueryParameter(name, StringCodecs.double(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a [BigInteger] value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the big integer codec.
         */
        override fun bigInteger(name: String): QueryParameter<BigInteger> =
            QueryParameter(name, StringCodecs.bigInteger(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a [BigDecimal] value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the big decimal codec.
         */
        override fun bigDecimal(name: String): QueryParameter<BigDecimal> =
            QueryParameter(name, StringCodecs.bigDecimal(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a [String] value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the string codec.
         */
        override fun string(name: String): QueryParameter<String> =
            QueryParameter(name, StringCodecs.string(name), required = true, default = null)

        /**
         * Creates a required [QueryParameter] expecting a [UUID] value.
         *
         * @param name canonical query parameter name.
         * @return query parameter backed by the UUID codec.
         */
        override fun uuid(name: String): QueryParameter<UUID> =
            QueryParameter(name, StringCodecs.uuid(name), required = true, default = null)
    }
}
