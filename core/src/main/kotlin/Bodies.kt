package dev.akif.tapik

/** A typed body or the absence of a body in a request input or response output. */
sealed interface BodyAlternative

/**
 * A typed body representation.
 *
 * @param Value decoded Kotlin value type.
 * @property mediaType HTTP media type.
 * @property format byte-array format used on the wire.
 */
data class Body<Value : Any>(
    /** HTTP media type. */
    val mediaType: MediaType,
    /** Byte-array format used on the wire. */
    val format: ByteArrayFormat<Value>
) : BodyAlternative

/**
 * Builds a required body using [mediaType] and [format].
 *
 * @param mediaType concrete HTTP media type for the representation.
 * @param format format that encodes and decodes the body value.
 * @return a required body representation.
 */
fun <Value : Any> body(
    mediaType: MediaType,
    format: ByteArrayFormat<Value>
): Body<Value> = Body(mediaType, format)

/** An alternative representing the absence of a body. It follows all typed body alternatives. */
data object NoBody : BodyAlternative

/** The bodyless DSL value, used as the final argument to [bodiesOf] when a body is optional. */
val noBody: NoBody = NoBody

/**
 * An ordered, non-empty heterogeneous tuple of body alternatives.
 *
 * Use [bodiesOf] to build a group with unique media types. [noBody] may occur once, only as the final alternative.
 */
typealias Bodies = NonEmptyTuple<BodyAlternative>

/** A body tuple with one ordered alternative. */
typealias Bodies1<Body1> = Tuple1<BodyAlternative, Body1>

/** A body tuple with two ordered alternatives. */
typealias Bodies2<Body1, Body2> = Tuple2<BodyAlternative, Body1, Body2>

/** A body tuple with three ordered alternatives. */
typealias Bodies3<Body1, Body2, Body3> = Tuple3<BodyAlternative, Body1, Body2, Body3>

/** A body tuple with four ordered alternatives. */
typealias Bodies4<Body1, Body2, Body3, Body4> = Tuple4<BodyAlternative, Body1, Body2, Body3, Body4>

/** A body tuple with five ordered alternatives. */
typealias Bodies5<Body1, Body2, Body3, Body4, Body5> =
    Tuple5<BodyAlternative, Body1, Body2, Body3, Body4, Body5>

/** A body tuple with six ordered alternatives. */
typealias Bodies6<Body1, Body2, Body3, Body4, Body5, Body6> =
    Tuple6<BodyAlternative, Body1, Body2, Body3, Body4, Body5, Body6>

/** A body tuple with seven ordered alternatives. */
typealias Bodies7<Body1, Body2, Body3, Body4, Body5, Body6, Body7> =
    Tuple7<BodyAlternative, Body1, Body2, Body3, Body4, Body5, Body6, Body7>

/** A body tuple with eight ordered alternatives. */
typealias Bodies8<Body1, Body2, Body3, Body4, Body5, Body6, Body7, Body8> =
    Tuple8<BodyAlternative, Body1, Body2, Body3, Body4, Body5, Body6, Body7, Body8>

internal fun <B : Bodies> validatedBodies(bodies: B): B {
    val mediaTypes = bodies.values.filterIsInstance<Body<*>>().map(Body<*>::mediaType)
    require(mediaTypes.distinct().size == mediaTypes.size) { "Body media types must be unique" }
    val noBodyIndexes = bodies.values.mapIndexedNotNull { index, body -> index.takeIf { body === noBody } }
    require(noBodyIndexes.size <= 1) { "noBody may appear at most once" }
    require(noBodyIndexes.isEmpty() || noBodyIndexes.single() == bodies.values.lastIndex) {
        "noBody must be the final body alternative"
    }
    return bodies
}

/** Returns one body as a one-item tuple. */
fun <Value : Any> bodiesOf(body1: Body<Value>): Bodies1<Body<Value>> = validatedBodies(Bodies1(body1))

/** Groups two bodies for the same value type.
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>
): Bodies2<Body<Value>, Body<Value>> = validatedBodies(Bodies2(body1, body2))

/** Groups three bodies for the same value type.
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>
): Bodies3<Body<Value>, Body<Value>, Body<Value>> = validatedBodies(Bodies3(body1, body2, body3))

/** Groups four bodies for the same value type.
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>
): Bodies4<Body<Value>, Body<Value>, Body<Value>, Body<Value>> =
    validatedBodies(Bodies4(body1, body2, body3, body4))

/** Groups five bodies for the same value type.
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>
): Bodies5<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>> =
    validatedBodies(Bodies5(body1, body2, body3, body4, body5))

/** Groups six bodies for the same value type.
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    body6: Body<Value>
): Bodies6<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>> =
    validatedBodies(Bodies6(body1, body2, body3, body4, body5, body6))

/** Groups seven bodies for the same value type.
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    body6: Body<Value>,
    body7: Body<Value>
): Bodies7<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>> =
    validatedBodies(Bodies7(body1, body2, body3, body4, body5, body6, body7))

/** Groups eight bodies for the same value type.
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
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
    validatedBodies(Bodies8(body1, body2, body3, body4, body5, body6, body7, body8))

/** Groups one body alternative followed by [noBody].
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    noBody: NoBody
): Bodies2<Body<Value>, NoBody> = validatedBodies(Bodies2(body1, noBody))

/** Groups two body alternatives followed by [noBody].
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    noBody: NoBody
): Bodies3<Body<Value>, Body<Value>, NoBody> = validatedBodies(Bodies3(body1, body2, noBody))

/** Groups three body alternatives followed by [noBody].
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    noBody: NoBody
): Bodies4<Body<Value>, Body<Value>, Body<Value>, NoBody> =
    validatedBodies(Bodies4(body1, body2, body3, noBody))

/** Groups four body alternatives followed by [noBody].
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    noBody: NoBody
): Bodies5<Body<Value>, Body<Value>, Body<Value>, Body<Value>, NoBody> =
    validatedBodies(Bodies5(body1, body2, body3, body4, noBody))

/** Groups five body alternatives followed by [noBody].
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    noBody: NoBody
): Bodies6<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, NoBody> =
    validatedBodies(Bodies6(body1, body2, body3, body4, body5, noBody))

/** Groups six body alternatives followed by [noBody].
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
fun <Value : Any> bodiesOf(
    body1: Body<Value>,
    body2: Body<Value>,
    body3: Body<Value>,
    body4: Body<Value>,
    body5: Body<Value>,
    body6: Body<Value>,
    noBody: NoBody
): Bodies7<Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, Body<Value>, NoBody> =
    validatedBodies(Bodies7(body1, body2, body3, body4, body5, body6, noBody))

/** Groups seven body alternatives followed by [noBody].
 *
 * Alternatives keep argument order, and typed bodies must have unique media types.
 *
 * @throws IllegalArgumentException if two typed bodies have the same media type.
 */
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
    validatedBodies(Bodies8(body1, body2, body3, body4, body5, body6, body7, noBody))
