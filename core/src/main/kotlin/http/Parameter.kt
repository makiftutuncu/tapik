@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

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
    val name: String
    val codec: StringCodec<T>
    val position: ParameterPosition
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

    companion object : Defaults<PathVariable<Unit>, PathVariable<Boolean>, PathVariable<Byte>, PathVariable<Short>, PathVariable<Int>, PathVariable<Long>, PathVariable<Float>, PathVariable<Double>, PathVariable<BigInteger>, PathVariable<BigDecimal>, PathVariable<String>, PathVariable<UUID>> {
        override fun unit(name: String): PathVariable<Unit> = PathVariable(name, StringCodecs.unit(name))

        override fun boolean(name: String): PathVariable<Boolean> = PathVariable(name, StringCodecs.boolean(name))

        override fun byte(name: String): PathVariable<Byte> = PathVariable(name, StringCodecs.byte(name))

        override fun short(name: String): PathVariable<Short> = PathVariable(name, StringCodecs.short(name))

        override fun int(name: String): PathVariable<Int> = PathVariable(name, StringCodecs.int(name))

        override fun long(name: String): PathVariable<Long> = PathVariable(name, StringCodecs.long(name))

        override fun float(name: String): PathVariable<Float> = PathVariable(name, StringCodecs.float(name))

        override fun double(name: String): PathVariable<Double> = PathVariable(name, StringCodecs.double(name))

        override fun bigInteger(name: String): PathVariable<BigInteger> =
            PathVariable(name, StringCodecs.bigInteger(name))

        override fun bigDecimal(name: String): PathVariable<BigDecimal> =
            PathVariable(name, StringCodecs.bigDecimal(name))

        override fun string(name: String): PathVariable<String> = PathVariable(name, StringCodecs.string(name))

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

    companion object : Defaults<QueryParameter<Unit>, QueryParameter<Boolean>, QueryParameter<Byte>, QueryParameter<Short>, QueryParameter<Int>, QueryParameter<Long>, QueryParameter<Float>, QueryParameter<Double>, QueryParameter<BigInteger>, QueryParameter<BigDecimal>, QueryParameter<String>, QueryParameter<UUID>> {
        override fun unit(name: String): QueryParameter<Unit> =
            QueryParameter(name, StringCodecs.unit(name), required = true, default = null)

        override fun boolean(name: String): QueryParameter<Boolean> =
            QueryParameter(name, StringCodecs.boolean(name), required = true, default = null)

        override fun byte(name: String): QueryParameter<Byte> =
            QueryParameter(name, StringCodecs.byte(name), required = true, default = null)

        override fun short(name: String): QueryParameter<Short> =
            QueryParameter(name, StringCodecs.short(name), required = true, default = null)

        override fun int(name: String): QueryParameter<Int> =
            QueryParameter(name, StringCodecs.int(name), required = true, default = null)

        override fun long(name: String): QueryParameter<Long> =
            QueryParameter(name, StringCodecs.long(name), required = true, default = null)

        override fun float(name: String): QueryParameter<Float> =
            QueryParameter(name, StringCodecs.float(name), required = true, default = null)

        override fun double(name: String): QueryParameter<Double> =
            QueryParameter(name, StringCodecs.double(name), required = true, default = null)

        override fun bigInteger(name: String): QueryParameter<BigInteger> =
            QueryParameter(name, StringCodecs.bigInteger(name), required = true, default = null)

        override fun bigDecimal(name: String): QueryParameter<BigDecimal> =
            QueryParameter(name, StringCodecs.bigDecimal(name), required = true, default = null)

        override fun string(name: String): QueryParameter<String> =
            QueryParameter(name, StringCodecs.string(name), required = true, default = null)

        override fun uuid(name: String): QueryParameter<UUID> =
            QueryParameter(name, StringCodecs.uuid(name), required = true, default = null)
    }
}
