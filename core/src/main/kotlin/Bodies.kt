package dev.akif.tapik

/** A body alternative in a request input or response output. */
sealed interface BodyAlternative

/**
 * A typed body representation.
 *
 * @param Value decoded Kotlin value type.
 * @property mediaType HTTP media type.
 * @property format byte-array format used on the wire.
 */
data class Body<Value : Any>(
    val mediaType: MediaType,
    val format: ByteArrayFormat<Value>
) : BodyAlternative

/** Builds a required body using [mediaType] and [format]. */
fun <Value : Any> body(
    mediaType: MediaType,
    format: ByteArrayFormat<Value>
): Body<Value> = Body(mediaType, format)

/** An alternative representing the absence of a body. */
data object NoBody : BodyAlternative

/** The bodyless DSL value. */
val noBody: NoBody = NoBody

/** An ordered, heterogeneous tuple of body alternatives. */
typealias Bodies = Tuple<BodyAlternative>

/** A body tuple with one alternative. */
typealias Bodies1<Body1> = Tuple1<BodyAlternative, Body1>

/** A body tuple with two alternatives. */
typealias Bodies2<Body1, Body2> = Tuple2<BodyAlternative, Body1, Body2>

/** A body tuple with three alternatives. */
typealias Bodies3<Body1, Body2, Body3> = Tuple3<BodyAlternative, Body1, Body2, Body3>

/** A body tuple with four alternatives. */
typealias Bodies4<Body1, Body2, Body3, Body4> = Tuple4<BodyAlternative, Body1, Body2, Body3, Body4>

/** A body tuple with five alternatives. */
typealias Bodies5<Body1, Body2, Body3, Body4, Body5> =
    Tuple5<BodyAlternative, Body1, Body2, Body3, Body4, Body5>

/** A body tuple with six alternatives. */
typealias Bodies6<Body1, Body2, Body3, Body4, Body5, Body6> =
    Tuple6<BodyAlternative, Body1, Body2, Body3, Body4, Body5, Body6>

/** A body tuple with seven alternatives. */
typealias Bodies7<Body1, Body2, Body3, Body4, Body5, Body6, Body7> =
    Tuple7<BodyAlternative, Body1, Body2, Body3, Body4, Body5, Body6, Body7>

/** A body tuple with eight alternatives. */
typealias Bodies8<Body1, Body2, Body3, Body4, Body5, Body6, Body7, Body8> =
    Tuple8<BodyAlternative, Body1, Body2, Body3, Body4, Body5, Body6, Body7, Body8>

private fun <B : Bodies> validated(bodies: B): B {
    val mediaTypes = bodies.values.filterIsInstance<Body<*>>().map(Body<*>::mediaType)
    require(mediaTypes.distinct().size == mediaTypes.size) { "Body media types must be unique" }
    return bodies
}

/** Groups one body. */
fun <Value : Any> bodiesOf(body1: Body<Value>): Bodies1<Body<Value>> = validated(Bodies1(body1))

/** Groups two bodies for the same value type. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>
): Bodies2<Body<Value>, Body<Value>> = validated(Bodies2(body1, body2))

/** Groups three bodies for the same value type. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>
): Bodies3<Body<Value>, Body<Value>, Body<Value>> = validated(Bodies3(body1, body2, body3))

/** Groups four bodies for the same value type. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>
): Bodies4<Body<Value>, Body<Value>, Body<Value>, Body<Value>> =
    validated(Bodies4(body1, body2, body3, body4))

/** Groups five bodies for the same value type. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>
): Bodies5<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>> =
    validated(Bodies5(body1, body2, body3, body4, body5))

/** Groups six bodies for the same value type. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    body6: Body<Value>
): Bodies6<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>> =
    validated(Bodies6(body1, body2, body3, body4, body5, body6))

/** Groups seven bodies for the same value type. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    body6: Body<Value>,
    body7: Body<Value>
): Bodies7<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>> =
    validated(Bodies7(body1, body2, body3, body4, body5, body6, body7))

/** Groups eight bodies for the same value type. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    body6: Body<Value>,
    body7: Body<Value>,
    body8: Body<Value>
): Bodies8<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>> =
    validated(Bodies8(body1, body2, body3, body4, body5, body6, body7, body8))

/** Groups one body followed by [noBody]. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    noBody: NoBody
): Bodies2<Body<Value>, NoBody> = validated(Bodies2(body1, noBody))

/** Groups two bodies followed by [noBody]. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    noBody: NoBody
): Bodies3<Body<Value>, Body<Value>, NoBody> = validated(Bodies3(body1, body2, noBody))

/** Groups three bodies followed by [noBody]. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    noBody: NoBody
): Bodies4<Body<Value>, Body<Value>, Body<Value>, NoBody> =
    validated(Bodies4(body1, body2, body3, noBody))

/** Groups four bodies followed by [noBody]. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    noBody: NoBody
): Bodies5<Body<Value>, Body<Value>, Body<Value>, Body<Value>, NoBody> =
    validated(Bodies5(body1, body2, body3, body4, noBody))

/** Groups five bodies followed by [noBody]. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    noBody: NoBody
): Bodies6<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, NoBody> =
    validated(Bodies6(body1, body2, body3, body4, body5, noBody))

/** Groups six bodies followed by [noBody]. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    body6: Body<Value>,
    noBody: NoBody
): Bodies7<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, NoBody> =
    validated(Bodies7(body1, body2, body3, body4, body5, body6, noBody))

/** Groups seven bodies followed by [noBody]. */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    body6: Body<Value>,
    body7: Body<Value>,
    noBody: NoBody
): Bodies8<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, NoBody> =
    validated(Bodies8(body1, body2, body3, body4, body5, body6, body7, noBody))
