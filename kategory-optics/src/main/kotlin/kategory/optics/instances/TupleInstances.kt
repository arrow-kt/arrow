package kategory.optics

import kategory.Tuple2
import kategory.toT

/**
 * [PLens] to focus into the first value of a [kategory.Tuple2]
 */
fun <A, B, R> pFirstTuple2(): PLens<Tuple2<A, B>, Tuple2<R, B>, A, R> = PLens(
        get = { it.a },
        set = { r -> { ab -> r toT ab.b } }
)

/**
 * [Lens] to focus into the first value of a [kategory.Tuple2]
 */
fun <A, B> firstTuple2(): Lens<Tuple2<A, B>, A> = pFirstTuple2()

/**
 * [PLens] to focus into the second value of a [kategory.Tuple2]
 */
fun <A, B, R> pSecondTuple2(): PLens<Tuple2<A, B>, Tuple2<A, R>, B, R> = PLens(
        get = { it.b },
        set = { r -> { ab -> ab.a toT r } }
)

/**
 * [Lens] to focus into the second value of a [kategory.Tuple2]
 */
fun <A, B> secondTuple2(): Lens<Tuple2<A, B>, B> = pSecondTuple2()
