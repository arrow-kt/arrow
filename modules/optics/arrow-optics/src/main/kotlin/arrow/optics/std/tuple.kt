package arrow.optics

import arrow.core.*

/**
 * [PLens] to focus into the first value of a [arrow.Tuple2]
 */
fun <A, B, R> Tuple2.Companion.pFirst(): PLens<Tuple2<A, B>, Tuple2<R, B>, A, R> = PLens(
  get = { it.a },
  set = { (_, b), r -> r toT b }
)

/**
 * [Lens] to focus into the first value of a [arrow.Tuple2]
 */
fun <A, B> Tuple2.Companion.first(): Lens<Tuple2<A, B>, A> = pFirst()

/**
 * [PLens] to focus into the second value of a [arrow.Tuple2]
 */
fun <A, B, R> Tuple2.Companion.pSecond(): PLens<Tuple2<A, B>, Tuple2<A, R>, B, R> = PLens(
  get = { it.b },
  set = { (a, _), r -> a toT r }
)

/**
 * [Lens] to focus into the second value of a [arrow.Tuple2]
 */
fun <A, B> Tuple2.Companion.second(): Lens<Tuple2<A, B>, B> = pSecond()

/**
 * [PTraversal] to focus into the first and second value of a [arrow.Tuple2]
 */
fun <A, B> Tuple2.Companion.pTraversal(): PTraversal<Tuple2<A, A>, Tuple2<B, B>, A, B> = PTraversal(
  get1 = { it.a },
  get2 = { it.b },
  set = { a, b, _ -> a toT b }
)

/**
 * [Traversal] to focus into the first and second value of a [arrow.Tuple2]
 */
fun <A> Tuple2.Companion.traversal(): Traversal<Tuple2<A, A>, A> = pTraversal()

/**
 * [PLens] to focus into the first value of a [arrow.Tuple3]
 */
fun <A, B, C, R> Tuple3.Companion.pFirst(): PLens<Tuple3<A, B, C>, Tuple3<R, B, C>, A, R> = PLens(
  get = { it.a },
  set = { (_, b, c), r -> Tuple3(r, b, c) }
)

/**
 * [Lens] to focus into the first value of a [arrow.Tuple3]
 */
fun <A, B, C> Tuple3.Companion.first(): Lens<Tuple3<A, B, C>, A> = pFirst()

/**
 * [PLens] to focus into the second value of a [arrow.Tuple3]
 */
fun <A, B, C, R> Tuple3.Companion.pSecond(): PLens<Tuple3<A, B, C>, Tuple3<A, R, C>, B, R> = PLens(
  get = { it.b },
  set = { (a, _, c), r -> Tuple3(a, r, c) }
)

/**
 * [Lens] to focus into the second value of a [arrow.Tuple3]
 */
fun <A, B, C> Tuple3.Companion.second(): Lens<Tuple3<A, B, C>, B> = pSecond()

/**
 * [PLens] to focus into the third value of a [arrow.Tuple3]
 */
fun <A, B, C, R> Tuple3.Companion.pThird(): PLens<Tuple3<A, B, C>, Tuple3<A, B, R>, C, R> = PLens(
  get = { it.c },
  set = { (a, b, _), r -> Tuple3(a, b, r) }
)

/**
 * [Lens] to focus into the third value of a [arrow.Tuple3]
 */
fun <A, B, C> Tuple3.Companion.third(): Lens<Tuple3<A, B, C>, C> = pThird()

/**
 * [PTraversal] to focus into the first, second and third value of a [arrow.Tuple3]
 */
fun <A, B> Tuple3.Companion.pTraversal(): PTraversal<Tuple3<A, A, A>, Tuple3<B, B, B>, A, B> = PTraversal(
  get1 = { it.a },
  get2 = { it.b },
  get3 = { it.c },
  set = { a, b, c, _ -> Tuple3(a, b, c) }
)

/**
 * [Traversal] to focus into the first, second and third value of a [arrow.Tuple3]
 */
fun <A> Tuple3.Companion.traversal(): Traversal<Tuple3<A, A, A>, A> = pTraversal()

/**
 * [PTraversal] to focus into the first, second, third and fourth value of a [arrow.Tuple4]
 */
fun <A, B> Tuple4.Companion.pTraversal(): PTraversal<Tuple4<A, A, A, A>, Tuple4<B, B, B, B>, A, B> = PTraversal(
  get1 = { it.a },
  get2 = { it.b },
  get3 = { it.c },
  get4 = { it.d },
  set = { a, b, c, d, _ -> Tuple4(a, b, c, d) }
)

/**
 * [Traversal] to focus into the first, second, third and fourth value of a [arrow.Tuple4]
 */
fun <A> Tuple4.Companion.traversal(): Traversal<Tuple4<A, A, A, A>, A> = pTraversal()

/**
 * [PTraversal] to focus into the first, second, third, fourth and fifth value of a [arrow.Tuple5]
 */
fun <A, B> Tuple5.Companion.pTraversal(): PTraversal<Tuple5<A, A, A, A, A>, Tuple5<B, B, B, B, B>, A, B> = PTraversal(
  get1 = { it.a },
  get2 = { it.b },
  get3 = { it.c },
  get4 = { it.d },
  get5 = { it.e },
  set = { a, b, c, d, e, _ -> Tuple5(a, b, c, d, e) }
)

/**
 * [Traversal] to focus into the first, second, third, fourth and fifth value of a [arrow.Tuple5]
 */
fun <A> Tuple5.Companion.traversal(): Traversal<Tuple5<A, A, A, A, A>, A> = pTraversal()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.Tuple6]
 */
fun <A, B> Tuple6.Companion.pTraversal(): PTraversal<Tuple6<A, A, A, A, A, A>, Tuple6<B, B, B, B, B, B>, A, B> = PTraversal(
  get1 = { it.a },
  get2 = { it.b },
  get3 = { it.c },
  get4 = { it.d },
  get5 = { it.e },
  get6 = { it.f },
  set = { a, b, c, d, e, f, _ -> Tuple6(a, b, c, d, e, f) }
)

/**
 * [Traversal] to focus into the first, second, third, fourth, fifth and sixth value of a [arrow.Tuple6]
 */
fun <A> Tuple6.Companion.traversal(): Traversal<Tuple6<A, A, A, A, A, A>, A> = pTraversal()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.Tuple7]
 */
fun <A, B> Tuple7.Companion.pTraversal(): PTraversal<Tuple7<A, A, A, A, A, A, A>, Tuple7<B, B, B, B, B, B, B>, A, B> = PTraversal(
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
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [arrow.Tuple7]
 */
fun <A> Tuple7.Companion.traversal(): Traversal<Tuple7<A, A, A, A, A, A, A>, A> = pTraversal()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.Tuple8]
 */
fun <A, B> Tuple8.Companion.pTraversal(): PTraversal<Tuple8<A, A, A, A, A, A, A, A>, Tuple8<B, B, B, B, B, B, B, B>, A, B> = PTraversal(
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
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [arrow.Tuple8]
 */
fun <A> Tuple8.Companion.traversal(): Traversal<Tuple8<A, A, A, A, A, A, A, A>, A> = pTraversal()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.Tuple9]
 */
fun <A, B> Tuple9.Companion.pTraversal(): PTraversal<Tuple9<A, A, A, A, A, A, A, A, A>, Tuple9<B, B, B, B, B, B, B, B, B>, A, B> = PTraversal(
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
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [arrow.Tuple9]
 */
fun <A> Tuple9.Companion.traversal(): Traversal<Tuple9<A, A, A, A, A, A, A, A, A>, A> = pTraversal()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.Tuple10]
 */
fun <A, B> Tuple10.Companion.pTraversal(): PTraversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, Tuple10<B, B, B, B, B, B, B, B, B, B>, A, B> = PTraversal(
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
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [arrow.Tuple10]
 */
fun <A> Tuple10.Companion.traversal(): Traversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, A> = pTraversal()
