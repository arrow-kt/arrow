package kategory.optics

import kategory.Tuple10
import kategory.Tuple2
import kategory.Tuple3
import kategory.Tuple4
import kategory.Tuple5
import kategory.Tuple6
import kategory.Tuple7
import kategory.Tuple8
import kategory.Tuple9
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

/**
 * [PTraversal] to focus into the first and second value of a [kategory.Tuple2]
 */
fun <A, B> pTraversalTuple2(): PTraversal<Tuple2<A, A>, Tuple2<B, B>, A, B> = PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        set = { a, b, _ -> a toT b }
)

/**
 * [Traversal] to focus into the first and second value of a [kategory.Tuple2]
 */
fun <A> traversalTuple2(): Traversal<Tuple2<A, A>, A> = pTraversalTuple2()

/**
 * [PTraversal] to focus into the first, second and third value of a [kategory.Tuple3]
 */
fun <A, B> pTraversalTuple3(): PTraversal<Tuple3<A, A, A>, Tuple3<B, B, B>, A, B> = PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        set = { a, b, c, _ -> Tuple3(a, b, c) }
)

/**
 * [Traversal] to focus into the first, second and third value of a [kategory.Tuple3]
 */
fun <A> traversalTuple3(): Traversal<Tuple3<A, A, A>, A> = pTraversalTuple3()

/**
 * [PTraversal] to focus into the first, second, third and fourth value of a [kategory.Tuple4]
 */
fun <A, B> pTraversalTuple4(): PTraversal<Tuple4<A, A, A, A>, Tuple4<B, B, B, B>, A, B> = PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        set = { a, b, c, d, _ -> Tuple4(a, b, c, d) }
)

/**
 * [Traversal] to focus into the first, second, third and fourth value of a [kategory.Tuple4]
 */
fun <A> traversalTuple4(): Traversal<Tuple4<A, A, A, A>, A> = pTraversalTuple4()

/**
 * [PTraversal] to focus into the first, second, third, fourth and fifth value of a [kategory.Tuple5]
 */
fun <A, B> pTraversalTuple5(): PTraversal<Tuple5<A, A, A, A, A>, Tuple5<B, B, B, B, B>, A, B> = PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        get5 = { it.e },
        set = { a, b, c, d, e, _ -> Tuple5(a, b, c, d, e) }
)

/**
 * [Traversal] to focus into the first, second, third, fourth and fifth value of a [kategory.Tuple5]
 */
fun <A> traversalTuple5(): Traversal<Tuple5<A, A, A, A, A>, A> = pTraversalTuple5()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth and sixth value of a [kategory.Tuple6]
 */
fun <A, B> pTraversalTuple6(): PTraversal<Tuple6<A, A, A, A, A, A>, Tuple6<B, B, B, B, B, B>, A, B> = PTraversal(
        get1 = { it.a },
        get2 = { it.b },
        get3 = { it.c },
        get4 = { it.d },
        get5 = { it.e },
        get6 = { it.f },
        set = { a, b, c, d, e, f, _ -> Tuple6(a, b, c, d, e, f) }
)

/**
 * [Traversal] to focus into the first, second, third, fourth, fifth and sixth value of a [kategory.Tuple6]
 */
fun <A> traversalTuple6(): Traversal<Tuple6<A, A, A, A, A, A>, A> = pTraversalTuple6()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [kategory.Tuple7]
 */
fun <A, B> pTraversalTuple7(): PTraversal<Tuple7<A, A, A, A, A, A, A>, Tuple7<B, B, B, B, B, B, B>, A, B> = PTraversal(
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
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth and seventh value of a [kategory.Tuple7]
 */
fun <A> traversalTuple7(): Traversal<Tuple7<A, A, A, A, A, A, A>, A> = pTraversalTuple7()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [kategory.Tuple8]
 */
fun <A, B> pTraversalTuple8(): PTraversal<Tuple8<A, A, A, A, A, A, A, A>, Tuple8<B, B, B, B, B, B, B, B>, A, B> = PTraversal(
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
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh and eight value of a [kategory.Tuple8]
 */
fun <A> traversalTuple8(): Traversal<Tuple8<A, A, A, A, A, A, A, A>, A> = pTraversalTuple8()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [kategory.Tuple9]
 */
fun <A, B> pTraversalTuple9(): PTraversal<Tuple9<A, A, A, A, A, A, A, A, A>, Tuple9<B, B, B, B, B, B, B, B, B>, A, B> = PTraversal(
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
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight and ninth value of a [kategory.Tuple9]
 */
fun <A> traversalTuple9(): Traversal<Tuple9<A, A, A, A, A, A, A, A, A>, A> = pTraversalTuple9()

/**
 * [PTraversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [kategory.Tuple10]
 */
fun <A, B> pTraversalTuple10(): PTraversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, Tuple10<B, B, B, B, B, B, B, B, B, B>, A, B> = PTraversal(
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
 * [Traversal] to focus into the first, second, third, fourth, fifth, sixth, seventh, eight, ninth and tenth value of a [kategory.Tuple10]
 */
fun <A> traversalTuple10(): Traversal<Tuple10<A, A, A, A, A, A, A, A, A, A>, A> = pTraversalTuple10()