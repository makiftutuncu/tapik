package dev.akif.tapik

import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.util.UUID

/** A query definition with a unique [name] within its URI. */
sealed interface Query {
    /** Query-string parameter name. */
    val name: String
}

/**
 * A typed scalar query parameter.
 *
 * @param Value decoded endpoint value type.
 * @param P presence type.
 * @property name query-string parameter name.
 * @property format string format used on the wire.
 * @property presence required, optional, or defaulted presence information.
 * @throws IllegalArgumentException if [name] is not a valid query parameter name.
 */
data class QueryParameter<Value : Any, out P : Presence<Value>>(
    override val name: String,
    val format: StringFormat<Value>,
    val presence: P
) : Query {
    init {
        requireValidQueryParameterName(name)
    }

    /** Returns this query parameter as optional without a default value. */
    fun optional(): QueryParameter<Value, Optional> = QueryParameter(name, format, Optional)

    /** Returns this query parameter as optional with [default] used when it is absent. */
    fun optional(default: Value): QueryParameter<Value, Default<Value>> =
        QueryParameter(name, format, Default(default))

    /** Built-in query-parameter factories backed by Tapik's default formats. */
    companion object :
        NamedDefaults<
            QueryParameter<Boolean, Required>,
            QueryParameter<Byte, Required>,
            QueryParameter<Short, Required>,
            QueryParameter<Int, Required>,
            QueryParameter<Long, Required>,
            QueryParameter<Float, Required>,
            QueryParameter<Double, Required>,
            QueryParameter<BigInteger, Required>,
            QueryParameter<BigDecimal, Required>,
            QueryParameter<String, Required>,
            QueryParameter<UUID, Required>,
            QueryParameter<LocalDate, Required>,
            QueryParameter<LocalTime, Required>,
            QueryParameter<LocalDateTime, Required>,
            QueryParameter<OffsetTime, Required>,
            QueryParameter<OffsetDateTime, Required>,
            QueryParameter<Instant, Required>,
            QueryParameter<Duration, Required>,
            QueryParameter<Period, Required>
        > {
        /** Builds a required scalar query parameter named [name] using [format]. */
        operator fun <Value : Any> invoke(
            name: String,
            format: StringFormat<Value>
        ): QueryParameter<Value, Required> = QueryParameter(name, format, Required)

        override fun boolean(name: String): QueryParameter<Boolean, Required> = invoke(name, format.boolean)
        override fun byte(name: String): QueryParameter<Byte, Required> = invoke(name, format.byte)
        override fun short(name: String): QueryParameter<Short, Required> = invoke(name, format.short)
        override fun int(name: String): QueryParameter<Int, Required> = invoke(name, format.int)
        override fun long(name: String): QueryParameter<Long, Required> = invoke(name, format.long)
        override fun float(name: String): QueryParameter<Float, Required> = invoke(name, format.float)
        override fun double(name: String): QueryParameter<Double, Required> = invoke(name, format.double)
        override fun bigInteger(name: String): QueryParameter<BigInteger, Required> = invoke(name, format.bigInteger)
        override fun bigDecimal(name: String): QueryParameter<BigDecimal, Required> = invoke(name, format.bigDecimal)
        override fun string(name: String): QueryParameter<String, Required> = invoke(name, format.string)
        override fun uuid(name: String): QueryParameter<UUID, Required> = invoke(name, format.uuid)
        override fun localDate(name: String): QueryParameter<LocalDate, Required> = invoke(name, format.localDate)
        override fun localTime(name: String): QueryParameter<LocalTime, Required> = invoke(name, format.localTime)
        override fun localDateTime(name: String): QueryParameter<LocalDateTime, Required> =
            invoke(name, format.localDateTime)
        override fun offsetTime(name: String): QueryParameter<OffsetTime, Required> = invoke(name, format.offsetTime)
        override fun offsetDateTime(name: String): QueryParameter<OffsetDateTime, Required> =
            invoke(name, format.offsetDateTime)
        override fun instant(name: String): QueryParameter<Instant, Required> = invoke(name, format.instant)
        override fun duration(name: String): QueryParameter<Duration, Required> = invoke(name, format.duration)
        override fun period(name: String): QueryParameter<Period, Required> = invoke(name, format.period)
    }
}

/**
 * A typed query parameter accepting repeated wire values as a list.
 *
 * @param Value decoded element type.
 * @param P list presence type.
 * @property name query-string parameter name.
 * @property format list format used for all wire occurrences.
 * @property presence required, optional, or defaulted presence information.
 * @throws IllegalArgumentException if [name] is not a valid query parameter name.
 */
data class RepeatedQueryParameter<Value : Any, out P : Presence<List<Value>>>(
    override val name: String,
    val format: Format<List<Value>, List<String>>,
    val presence: P
) : Query {
    init {
        requireValidQueryParameterName(name)
    }

    /** Returns this repeated query parameter as optional without a default value. */
    fun optional(): RepeatedQueryParameter<Value, Optional> = RepeatedQueryParameter(name, format, Optional)

    /** Returns this repeated query parameter as optional with [default] used when it is absent. */
    fun optional(default: List<Value>): RepeatedQueryParameter<Value, Default<List<Value>>> =
        RepeatedQueryParameter(name, format, Default(default))
}

/** Returns this required scalar query parameter as a required repeated parameter. */
fun <Value : Any> QueryParameter<Value, Required>.repeated(): RepeatedQueryParameter<Value, Required> =
    RepeatedQueryParameter(name, format.repeated(), Required)

/** Returns this optional scalar query parameter as an optional repeated parameter. */
@JvmName("optionalQueryRepeated")
fun <Value : Any> QueryParameter<Value, Optional>.repeated(): RepeatedQueryParameter<Value, Optional> =
    RepeatedQueryParameter(name, format.repeated(), Optional)

private const val INVALID_QUERY_PARAMETER_NAME_CHARACTERS: String = "&=#?{}"

private fun requireValidQueryParameterName(name: String) {
    require(name.isNotBlank()) { "Query parameter name must not be blank" }
    require(name.none(Char::isWhitespace) && name.none { it in INVALID_QUERY_PARAMETER_NAME_CHARACTERS }) {
        "Query parameter name contains an invalid character: '$name'"
    }
}

/** Shortcut to the built-in factories on [QueryParameter.Companion]. */
val query: QueryParameter.Companion
    get() = QueryParameter.Companion

/** An ordered, heterogeneous tuple of query parameters. */
typealias Queries = Tuple<Query>

/** A query-parameter tuple with no values. */
typealias Queries0 = Tuple0

/** A query-parameter tuple with one parameter. */
typealias Queries1<Query1> = Tuple1<Query, Query1>

/** A query-parameter tuple with two parameters. */
typealias Queries2<Query1, Query2> = Tuple2<Query, Query1, Query2>

/** A query-parameter tuple with three parameters. */
typealias Queries3<Query1, Query2, Query3> = Tuple3<Query, Query1, Query2, Query3>

/** A query-parameter tuple with four parameters. */
typealias Queries4<Query1, Query2, Query3, Query4> = Tuple4<Query, Query1, Query2, Query3, Query4>

/** A query-parameter tuple with five parameters. */
typealias Queries5<Query1, Query2, Query3, Query4, Query5> = Tuple5<Query, Query1, Query2, Query3, Query4, Query5>

/** A query-parameter tuple with six parameters. */
typealias Queries6<Query1, Query2, Query3, Query4, Query5, Query6> =
    Tuple6<Query, Query1, Query2, Query3, Query4, Query5, Query6>

/** A query-parameter tuple with seven parameters. */
typealias Queries7<Query1, Query2, Query3, Query4, Query5, Query6, Query7> =
    Tuple7<Query, Query1, Query2, Query3, Query4, Query5, Query6, Query7>

/** A query-parameter tuple with eight parameters. */
typealias Queries8<Query1, Query2, Query3, Query4, Query5, Query6, Query7, Query8> =
    Tuple8<Query, Query1, Query2, Query3, Query4, Query5, Query6, Query7, Query8>

private fun <P : Paths, Q : Queries> Uri<P, *>.append(
    parameter: Query,
    newQueries: Q
): Uri<P, Q> {
    require(queries.values.none { it.name == parameter.name }) {
        "Query parameter '${parameter.name}' is already defined"
    }

    return Uri(segments = segments, paths = paths, queries = newQueries)
}

/** Appends the first query [parameter]. */
@JvmName("uriWithNoQueriesPlusQueryParameter")
operator fun <P : Paths, QueryType : Query> Uri<P, Queries0>.plus(
    parameter: QueryType
): Uri<P, Queries1<QueryType>> = append(parameter, Queries1(parameter))

/** Appends a second query [parameter]. */
@JvmName("uriWithOneQueryPlusQueryParameter")
operator fun <P : Paths, Query1 : Query, QueryType : Query> Uri<P, Queries1<Query1>>.plus(
    parameter: QueryType
): Uri<P, Queries2<Query1, QueryType>> = append(parameter, Queries2(queries._1, parameter))

/** Appends a third query [parameter]. */
@JvmName("uriWithTwoQueriesPlusQueryParameter")
operator fun <P : Paths, Query1 : Query, Query2 : Query, QueryType : Query> Uri<P, Queries2<Query1, Query2>>.plus(
    parameter: QueryType
): Uri<P, Queries3<Query1, Query2, QueryType>> = append(parameter, Queries3(queries._1, queries._2, parameter))

/** Appends a fourth query [parameter]. */
@JvmName("uriWithThreeQueriesPlusQueryParameter")
operator fun <P : Paths, Query1 : Query, Query2 : Query, Query3 : Query, QueryType : Query>
    Uri<P, Queries3<Query1, Query2, Query3>>.plus(
        parameter: QueryType
    ): Uri<P, Queries4<Query1, Query2, Query3, QueryType>> =
        append(parameter, Queries4(queries._1, queries._2, queries._3, parameter))

/** Appends a fifth query [parameter]. */
@JvmName("uriWithFourQueriesPlusQueryParameter")
operator fun <P : Paths, Query1 : Query, Query2 : Query, Query3 : Query, Query4 : Query, QueryType : Query>
    Uri<P, Queries4<Query1, Query2, Query3, Query4>>.plus(
        parameter: QueryType
    ): Uri<P, Queries5<Query1, Query2, Query3, Query4, QueryType>> =
        append(parameter, Queries5(queries._1, queries._2, queries._3, queries._4, parameter))

/** Appends a sixth query [parameter]. */
@JvmName("uriWithFiveQueriesPlusQueryParameter")
operator fun <
    P : Paths,
    Query1 : Query,
    Query2 : Query,
    Query3 : Query,
    Query4 : Query,
    Query5 : Query,
    QueryType : Query
> Uri<P, Queries5<Query1, Query2, Query3, Query4, Query5>>.plus(
    parameter: QueryType
): Uri<P, Queries6<Query1, Query2, Query3, Query4, Query5, QueryType>> =
    append(parameter, Queries6(queries._1, queries._2, queries._3, queries._4, queries._5, parameter))

/** Appends a seventh query [parameter]. */
@JvmName("uriWithSixQueriesPlusQueryParameter")
operator fun <
    P : Paths,
    Query1 : Query,
    Query2 : Query,
    Query3 : Query,
    Query4 : Query,
    Query5 : Query,
    Query6 : Query,
    QueryType : Query
> Uri<P, Queries6<Query1, Query2, Query3, Query4, Query5, Query6>>.plus(
    parameter: QueryType
): Uri<P, Queries7<Query1, Query2, Query3, Query4, Query5, Query6, QueryType>> =
    append(parameter, Queries7(queries._1, queries._2, queries._3, queries._4, queries._5, queries._6, parameter))

/** Appends an eighth query [parameter]. */
@JvmName("uriWithSevenQueriesPlusQueryParameter")
operator fun <
    P : Paths,
    Query1 : Query,
    Query2 : Query,
    Query3 : Query,
    Query4 : Query,
    Query5 : Query,
    Query6 : Query,
    Query7 : Query,
    QueryType : Query
> Uri<P, Queries7<Query1, Query2, Query3, Query4, Query5, Query6, Query7>>.plus(
    parameter: QueryType
): Uri<P, Queries8<Query1, Query2, Query3, Query4, Query5, Query6, Query7, QueryType>> =
    append(
        parameter,
        Queries8(
            queries._1,
            queries._2,
            queries._3,
            queries._4,
            queries._5,
            queries._6,
            queries._7,
            parameter
        )
    )
