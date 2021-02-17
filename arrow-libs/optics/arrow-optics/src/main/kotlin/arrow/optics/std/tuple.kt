package arrow.optics

import arrow.core.Tuple10
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9

/**
 * [PLens] to focus into the first value of a [Pair]
 */
fun <A, B, R> PLens.Companion.pPairFirst(): PLens<Pair<A, B>, Pair<R, B>, A, R> =
  PLens(
    get = { it.first },
    set = { (_, b), r -> r to b }
  )

/**
 * [Lens] to focus into the first value of a [arrow.core.Tuple2]
 */
fun <A, B> PLens.Companion.pairFirst(): Lens<Pair<A, B>, A> =
  pPairFirst()

/**
 * [PLens] to focus into the second value of a [arrow.core.Tuple2]
 */
fun <A, B, R> PLens.Companion.pPairSecond(): PLens<Pair<A, B>, Pair<A, R>, B, R> =
  PLens(
    get = { it.second },
    set = { (a, _), r -> a to r }
  )

/**
 * [Lens] to focus into the second value of a [arrow.core.Tuple2]
 */
fun <A, B> PLens.Companion.pairSecond(): Lens<Pair<A, B>, B> =
  pPairSecond()

/**
 * [PTraversal] to focus into the first and second value of a [arrow.core.Tuple2]
 */
fun <A, B> PTraversal.Companion.pPair(): PTraversal<Pair<A, A>, Pair<B, B>, A, B> =
  PTraversal(
    get1 = { it.first },
    get2 = { it.second },
    set = { a, b, _ -> a to b }
  )

/**
 * [Traversal] to focus into the first and second value of a [arrow.core.Tuple2]
 */
fun <A> PTraversal.Companion.pair(): Traversal<Pair<A, A>, A> =
  pPair()

/**
 * [PLens] to focus into the first value of a [arrow.core.Triple]
 */
fun <A, B, C, R> PLens.Companion.pTripleFirst(): PLens<Triple<A, B, C>, Triple<R, B, C>, A, R> =
  PLens(
    get = { it.first },
    set = { (_, b, c), r -> Triple(r, b, c) }
  )

/**
 * [Lens] to focus into the first value of a [arrow.core.Triple]
 */
fun <A, B, C> PLens.Companion.tripleFirst(): Lens<Triple<A, B, C>, A> =
  pTripleFirst()

/**
 * [PLens] to focus into the second value of a [arrow.core.Triple]
 */
fun <A, B, C, R> PLens.Companion.pTripleSecond(): PLens<Triple<A, B, C>, Triple<A, R, C>, B, R> =
  PLens(
    get = { it.second },
    set = { (a, _, c), r -> Triple(a, r, c) }
  )

/**
 * [Lens] to focus into the second value of a [arrow.core.Triple]
 */
fun <A, B, C> PLens.Companion.tripleSecond(): Lens<Triple<A, B, C>, B> =
  pTripleSecond()

/**
 * [PLens] to focus into the third value of a [arrow.core.Triple]
 */
fun <A, B, C, R> PLens.Companion.pTripleThird(): PLens<Triple<A, B, C>, Triple<A, B, R>, C, R> =
  PLens(
    get = { it.third },
    set = { (a, b, _), r -> Triple(a, b, r) }
  )

/**
 * [Lens] to focus into the third value of a [arrow.core.Triple]
 */
fun <A, B, C> PLens.Companion.tripleThird(): Lens<Triple<A, B, C>, C> =
  pTripleThird()

/**
 * [PTraversal] to focus into the first, second and third value of a [arrow.core.Triple]
 */
fun <A, B> PTraversal.Companion.pTriple(): PTraversal<Triple<A, A, A>, Triple<B, B, B>, A, B> =
  PTraversal(
    get1 = { it.first },
    get2 = { it.second },
    get3 = { it.third },
    set = { a, b, c, _ -> Triple(a, b, c) }
  )

/**
 * [Traversal] to focus into the first, second and third value of a [arrow.core.Triple]
 */
fun <A> PTraversal.Companion.triple(): Traversal<Triple<A, A, A>, A> =
  pTriple()

/**
 * [PTraversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
 */
fun <A, B> PTraversal.Companion.pTuple4(): PTraversal<Tuple4<A, A, A, A>, Tuple4<B, B, B, B>, A, B> =
  PTraversal(
    get1 = { it.a },
    get2 = { it.b },
    get3 = { it.c },
    get4 = { it.d },
    set = { a, b, c, d, _ -> Tuple4(a, b, c, d) }
  )

/**
 * [Traversal] to focus into the first, second, third and fourth value of a [arrow.core.Tuple4]
 */
fun <A> PTraversal.Companion.tuple4(): Traversal<Tuple4<A, A, A, A>, A> =
  pTuple4()

/**
 * [PTraversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
 */
fun <A, B> PTraversal.Companion.pTuple5(): PTraversal<Tuple5<A, A, A, A, A>, Tuple5<B, B, B, B, B>, A, B> =
  PTraversal(
    get1 = { it.a },
    get2 = { it.b },
    get3 = { it.c },
    get4 = { it.d },
    get5 = { it.e },
    set = { a, b, c, d, e, _ -> Tuple5(a, b, c, d, e) }
  )

/**
 * [Traversal] to focus into the first, second, third, fourth and fifth value of a [arrow.core.Tuple5]
 */
fun <A> PTraversal.Companion.tuple5(): Traversal<Tuple5<A, A, A, A, A>, A> =
  pTuple5()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
 */
fun <A, B> PTraversal.Companion.pTuple6(): PTraversal<Tuple6<A, A, A, A, A, A>, Tuple6<B, B, B, B, B, B>, A, B> =
  PTraversal(
    get1 = { it.a },
    get2 = { it.b },
    get3 = { it.c },
    get4 = { it.d },
    get5 = { it.e },
    get6 = { it.f },
    set = { a, b, c, d, e, f, _ -> Tuple6(a, b, c, d, e, f) }
  )

/**
 * [Traversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.core.Tuple6]
 */
fun <A> PTraversal.Companion.tuple6(): Traversal<Tuple6<A, A, A, A, A, A>, A> =
  pTuple6()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
 */
fun <A, B> PTraversal.Companion.pTuple7(): PTraversal<Tuple7<A, A, A, A, A, A, A>, Tuple7<B, B, B, B, B, B, B>, A, B> =
  PTraversal(
    get1 = { it.a },
    get2 = { it.b },
    get3 = { it.c },
    get4 = { it.d },
    get5 = { it.e },
    get6 = { it.f },
    get7 = { it.g },
    set = { a, b, c, d, e, f, g, _ -> Tuple7(a, b, c, d, e, f, g) }
  )

/**
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.core.Tuple7]
 */
fun <A> PTraversal.Companion.tuple7(): Traversal<Tuple7<A, A, A, A, A, A, A>, A> =
  pTuple7()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
 */
fun <A, B> PTraversal.Companion.pTuple8(): PTraversal<Tuple8<A, A, A, A, A, A, A, A>, Tuple8<B, B, B, B, B, B, B, B>, A, B> =
  PTraversal(
    get1 = { it.a },
    get2 = { it.b },
    get3 = { it.c },
    get4 = { it.d },
    get5 = { it.e },
    get6 = { it.f },
    get7 = { it.g },
    get8 = { it.h },
    set = { a, b, c, d, e, f, g, h, _ -> Tuple8(a, b, c, d, e, f, g, h) }
  )

/**
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.core.Tuple8]
 */
fun <A> PTraversal.Companion.tuple8(): Traversal<Tuple8<A, A, A, A, A, A, A, A>, A> =
  pTuple8()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
 */
fun <A, B> PTraversal.Companion.pTuple9(): PTraversal<Tuple9<A, A, A, A, A, A, A, A, A>, Tuple9<B, B, B, B, B, B, B, B, B>, A, B> =
  PTraversal(
    get1 = { it.a },
    get2 = { it.b },
    get3 = { it.c },
    get4 = { it.d },
    get5 = { it.e },
    get6 = { it.f },
    get7 = { it.g },
    get8 = { it.h },
    get9 = { it.i },
    set = { a, b, c, d, e, f, g, h, i, _ -> Tuple9(a, b, c, d, e, f, g, h, i) }
  )

/**
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.core.Tuple9]
 */
fun <A> PTraversal.Companion.tuple9(): Traversal<Tuple9<A, A, A, A, A, A, A, A, A>, A> =
  pTuple9()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.core.Tuple10]
 */
fun <A, B> PTraversal.Companion.pTuple10(): PTraversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, Tuple10<B, B, B, B, B, B, B, B, B, B>, A, B> =
  PTraversal(
    get1 = { it.a },
    get2 = { it.b },
    get3 = { it.c },
    get4 = { it.d },
    get5 = { it.e },
    get6 = { it.f },
    get7 = { it.g },
    get8 = { it.h },
    get9 = { it.i },
    get10 = { it.j },
    set = { a, b, c, d, e, f, g, h, i, j, _ -> Tuple10(a, b, c, d, e, f, g, h, i, j) }
  )

/**
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.core.Tuple10]
 */
fun <A> PTraversal.Companion.tuple10(): Traversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, A> =
  pTuple10()
