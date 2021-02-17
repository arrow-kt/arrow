package arrow.core.extensions.list.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ap as _ap
import kotlin.collections.flatMap as _flatMap
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.ListKApply
import arrow.core.fix
import arrow.core.k
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("_ap(arg1)", "arrow.core.ap"))
fun <A, B> List<A>.ap(arg1: List<Function1<A, B>>): List<B> =
  _ap(arg1)

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("arg1.map { this.ap(it.fix()) }.map { it.k() }", "arrow.core.k", "arrow.core.fix"))
fun <A, B> List<A>.apEval(arg1: Eval<Kind<ForListK, Function1<A, B>>>): Eval<Kind<ForListK, B>> =
  arg1.map { this.ap(it.fix()) }.map { it.k() }

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("apEval(arg1.map { it.fix().map { b -> { a: A -> arg2(Tuple2(a, b)) } } })", "arrow.core.k", "arrow.core.Tuple2"))
fun <A, B, Z> List<A>.map2Eval(arg1: Eval<Kind<ForListK, B>>, arg2: Function1<Tuple2<A, B>, Z>):
  Eval<Kind<ForListK, Z>> =
    apEval(arg1.map { it.fix().map { b -> { a: A -> arg2(Tuple2(a, b)) } } })

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1) { a, b -> arg2(Tuple2(a, b)) }", "arrow.core.ListK", "arrow.core.Tuple2"))
fun <A, B, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1) { a, b -> arg2(Tuple2(a, b)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1) { a, b -> arg2(Tuple2(a, b)) }", "arrow.core.ListK", "arrow.core.Tuple2"))
fun <A, B, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1) { a, b -> arg2(Tuple2(a, b)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2) { a, b, c -> arg3(Tuple3(a, b, c)) }", "arrow.core.ListK", "arrow.core.Tuple3"))
fun <A, B, C, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2) { a, b, c -> arg3(Tuple3(a, b, c)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2) { a, b, c -> arg3(Tuple3(a, b, c)) }", "arrow.core.ListK", "arrow.core.Tuple3"))
fun <A, B, C, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2) { a, b, c -> arg3(Tuple3(a, b, c)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }", "arrow.core.ListK", "arrow.core.Tuple4"))
fun <A, B, C, D, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }", "arrow.core.ListK", "arrow.core.Tuple4"))
fun <A, B, C, D, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> arg4(Tuple4(a, b, c, d)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }", "arrow.core.ListK", "arrow.core.Tuple5"))
fun <A, B, C, D, E, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }", "arrow.core.ListK", "arrow.core.Tuple5"))
fun <A, B, C, D, E, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, ff -> arg6(Tuple6(a, b, c, d, e, ff)) }", "arrow.core.ListK", "arrow.core.Tuple6"))
fun <A, B, C, D, E, FF, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, ff -> arg6(Tuple6(a, b, c, d, e, ff)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, ff -> arg6(Tuple6(a, b, c, d, e, ff)) }", "arrow.core.ListK", "arrow.core.Tuple6"))
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, ff -> arg6(Tuple6(a, b, c, d, e, ff)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, ff, g -> arg7(Tuple7(a, b, c, d, e, ff, g)) }", "arrow.core.ListK", "arrow.core.Tuple7"))
fun <A, B, C, D, E, FF, G, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, ff, g -> arg7(Tuple7(a, b, c, d, e, ff, g)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, ff, g -> arg7(Tuple7(a, b, c, d, e, ff, g)) }", "arrow.core.ListK", "arrow.core.Tuple7"))
fun <A, B, C, D, E, FF, G, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, ff, g -> arg7(Tuple7(a, b, c, d, e, ff, g)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, ff, g, h -> arg8(Tuple8(a, b, c, d, e, ff, g, h)) }", "arrow.core.ListK", "arrow.core.Tuple8"))
fun <A, B, C, D, E, FF, G, H, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, ff, g, h -> arg8(Tuple8(a, b, c, d, e, ff, g, h)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, ff, g, h -> arg8(Tuple8(a, b, c, d, e, ff, g, h)) }", "arrow.core.ListK", "arrow.core.Tuple8"))
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, ff, g, h -> arg8(Tuple8(a, b, c, d, e, ff, g, h)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, ff, g, h -> arg8(Tuple8(a, b, c, d, e, ff, g, h)) }", "arrow.core.ListK", "arrow.core.Tuple8"))
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, ff, g, h, i -> arg9(Tuple9(a, b, c, d, e, ff, g, h, i)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, ff, g, h, i -> arg9(Tuple9(a, b, c, d, e, ff, g, h, i)) }", "arrow.core.ListK", "arrow.core.Tuple9"))
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, ff, g, h, i -> arg9(Tuple9(a, b, c, d, e, ff, g, h, i)) }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, ff, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, ff, g, h, i, j)) }", "arrow.core.ListK", "arrow.core.Tuple10"))
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: List<J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, ff, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, ff, g, h, i, j)) }

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, ff, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, ff, g, h, i, j)) }", "arrow.core.ListK", "arrow.core.Tuple10"))
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: List<J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): List<Z> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, ff, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, ff, g, h, i, j)) }

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { a, b -> arg2(Tuple2(a, b)) }", "arrow.core.Tuple2", "kotlin.collections.zip"))
fun <A, B, Z> List<A>.map2(arg1: List<B>, arg2: Function1<Tuple2<A, B>, Z>): List<Z> =
  zip(arg1) { a, b -> arg2(Tuple2(a, b)) }

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { a, b -> Tuple2(a, b) }", "arrow.core.Tuple2", "kotlin.collections.zip"))
fun <A, B> List<A>.product(arg1: List<B>): List<Tuple2<A, B>> =
  zip(arg1) { a, b -> Tuple2(a, b) }

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { (a, b), z -> Tuple3(a, b, z)", "arrow.core.Tuple3", "kotlin.collections.zip"))
fun <A, B, Z> List<Tuple2<A, B>>.product(arg1: List<Z>): List<Tuple3<A, B, Z>> =
  zip(arg1) { (a, b), z -> Tuple3(a, b, z) }

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { (a, b, c), z -> Tuple4(a, b, c, z) }", "arrow.core.Tuple4", "kotlin.collections.zip"))
fun <A, B, C, Z> List<Tuple3<A, B, C>>.product(arg1: List<Z>): List<Tuple4<A, B, C, Z>> =
  zip(arg1) { (a, b, c), z -> Tuple4(a, b, c, z) }

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { (a, b, c, d), z -> Tuple5(a, b, c, d, z) }", "arrow.core.Tuple5", "kotlin.collections.zip"))
fun <A, B, C, D, Z> List<Tuple4<A, B, C, D>>.product(arg1: List<Z>): List<Tuple5<A, B, C, D, Z>> =
  zip(arg1) { (a, b, c, d), z -> Tuple5(a, b, c, d, z) }

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { (a, b, c, d, e), z -> Tuple6(a, b, c, d, e, z) }", "arrow.core.Tuple6", "kotlin.collections.zip"))
fun <A, B, C, D, E, Z> List<Tuple5<A, B, C, D, E>>.product(arg1: List<Z>): List<Tuple6<A, B, C, D, E, Z>> =
  zip(arg1) { (a, b, c, d, e), z -> Tuple6(a, b, c, d, e, z) }

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { (a, b, c, d, e, ff), z -> Tuple7(a, b, c, d, e, ff, z) }", "arrow.core.Tuple7", "kotlin.collections.zip"))
fun <A, B, C, D, E, FF, Z> List<Tuple6<A, B, C, D, E, FF>>.product(arg1: List<Z>): List<Tuple7<A, B,
    C, D, E, FF, Z>> =
  zip(arg1) { (a, b, c, d, e, ff), z -> Tuple7(a, b, c, d, e, ff, z) }

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { (a, b, c, d, e, ff, g), z -> Tuple8(a, b, c, d, e, ff, g, z) }", "arrow.core.Tuple8", "kotlin.collections.zip"))
fun <A, B, C, D, E, FF, G, Z> List<Tuple7<A, B, C, D, E, FF, G>>.product(arg1: List<Z>):
  List<Tuple8<A, B, C, D, E, FF, G, Z>> =
    zip(arg1) { (a, b, c, d, e, ff, g), z -> Tuple8(a, b, c, d, e, ff, g, z) }

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { (a, b, c, d, e, ff, g, h), z -> Tuple9(a, b, c, d, e, ff, g, h, z) }", "arrow.core.Tuple9", "kotlin.collections.zip"))
fun <A, B, C, D, E, FF, G, H, Z> List<Tuple8<A, B, C, D, E, FF, G, H>>.product(arg1: List<Z>):
  List<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
    zip(arg1) { (a, b, c, d, e, ff, g, h), z -> Tuple9(a, b, c, d, e, ff, g, h, z) }

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("zip(arg1) { (a, b, c, d, e, ff, g, h, i), z -> Tuple10(a, b, c, d, e, ff, g, h, i, z) }", "arrow.core.Tuple10", "kotlin.collections.zip"))
fun <A, B, C, D, E, FF, G, H, I, Z> List<Tuple9<A, B, C, D, E, FF, G, H, I>>.product(arg1: List<Z>):
  List<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
    zip(arg1) { (a, b, c, d, e, ff, g, h, i), z -> Tuple10(a, b, c, d, e, ff, g, h, i, z) }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1) { a, b -> Tuple2(a, b) }", "arrow.core.ListK", "arrow.core.Tuple2"))
fun <A, B> tupled(arg0: List<A>, arg1: List<B>): List<Tuple2<A, B>> =
  ListK.mapN(arg0, arg1) { a, b -> Tuple2(a, b) }

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1) { a, b -> Tuple2(a, b) }", "arrow.core.ListK", "arrow.core.Tuple2"))
fun <A, B> tupledN(arg0: List<A>, arg1: List<B>): List<Tuple2<A, B>> =
  ListK.mapN(arg0, arg1) { a, b -> Tuple2(a, b) }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2) { a, b, c -> Tuple3(a, b, c) }", "arrow.core.ListK", "arrow.core.Tuple3"))
fun <A, B, C> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>
): List<Tuple3<A, B, C>> =
  ListK.mapN(arg0, arg1, arg2) { a, b, c -> Tuple3(a, b, c) }

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2) { a, b, c -> Tuple3(a, b, c) }", "arrow.core.ListK", "arrow.core.Tuple3"))
fun <A, B, C> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>
): List<Tuple3<A, B, C>> =
  ListK.mapN(arg0, arg1, arg2) { a, b, c -> Tuple3(a, b, c) }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> Tuple4(a, b, c, d) }", "arrow.core.ListK", "arrow.core.Tuple4"))
fun <A, B, C, D> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>
): List<Tuple4<A, B, C, D>> =
  ListK.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> Tuple4(a, b, c, d) }

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> Tuple4(a, b, c, d) }", "arrow.core.ListK", "arrow.core.Tuple4"))
fun <A, B, C, D> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>
): List<Tuple4<A, B, C, D>> =
  ListK.mapN(arg0, arg1, arg2, arg3) { a, b, c, d -> Tuple4(a, b, c, d) }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> Tuple5(a, b, c, d, e) }", "arrow.core.ListK", "arrow.core.Tuple5"))
fun <A, B, C, D, E> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>
): List<Tuple5<A, B, C, D, E>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> Tuple5(a, b, c, d, e) }

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> Tuple5(a, b, c, d, e) }", "arrow.core.ListK", "arrow.core.Tuple5"))
fun <A, B, C, D, E> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>
): List<Tuple5<A, B, C, D, E>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> Tuple5(a, b, c, d, e) }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, ff -> Tuple6(a, b, c, d, e, ff) }", "arrow.core.ListK", "arrow.core.Tuple6"))
fun <A, B, C, D, E, FF> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>
): List<Tuple6<A, B, C, D, E, FF>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, ff -> Tuple6(a, b, c, d, e, ff) }

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, ff -> Tuple6(a, b, c, d, e, ff) }", "arrow.core.ListK", "arrow.core.Tuple6"))
fun <A, B, C, D, E, FF> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>
): List<Tuple6<A, B, C, D, E, FF>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, ff -> Tuple6(a, b, c, d, e, ff) }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, ff, g -> Tuple7(a, b, c, d, e, ff, g) }", "arrow.core.ListK", "arrow.core.Tuple7"))
fun <A, B, C, D, E, FF, G> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>
): List<Tuple7<A, B, C, D, E, FF, G>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, ff, g -> Tuple7(a, b, c, d, e, ff, g) }

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, ff, g -> Tuple7(a, b, c, d, e, ff, g) }", "arrow.core.ListK", "arrow.core.Tuple7"))
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>
): List<Tuple7<A, B, C, D, E, FF, G>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, ff, g -> Tuple7(a, b, c, d, e, ff, g) }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, ff, g, h -> Tuple8(a, b, c, d, e, ff, g, h) }", "arrow.core.ListK", "arrow.core.Tuple8"))
fun <A, B, C, D, E, FF, G, H> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>
): List<Tuple8<A, B, C, D, E, FF, G, H>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, ff, g, h -> Tuple8(a, b, c, d, e, ff, g, h) }

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, ff, g, h -> Tuple8(a, b, c, d, e, ff, g, h) }", "arrow.core.ListK", "arrow.core.Tuple8"))
fun <A, B, C, D, E, FF, G, H> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>
): List<Tuple8<A, B, C, D, E, FF, G, H>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, ff, g, h -> Tuple8(a, b, c, d, e, ff, g, h) }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, ff, g, h, i -> Tuple9(a, b, c, d, e, ff, g, h, i) }", "arrow.core.ListK", "arrow.core.Tuple9"))
fun <A, B, C, D, E, FF, G, H, I> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>
): List<Tuple9<A, B, C, D, E, FF, G, H, I>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, ff, g, h, i -> Tuple9(a, b, c, d, e, ff, g, h, i) }

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, ff, g, h, i -> Tuple9(a, b, c, d, e, ff, g, h, i) }", "arrow.core.ListK", "arrow.core.Tuple9"))
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>
): List<Tuple9<A, B, C, D, E, FF, G, H, I>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, ff, g, h, i -> Tuple9(a, b, c, d, e, ff, g, h, i) }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, ff, g, h, i, j -> Tuple10(a, b, c, d, e, ff, g, h, i, j) }", "arrow.core.ListK", "arrow.core.Tuple10"))
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: List<J>
): List<Tuple10<A, B, C, D, E, FF, G, H, I, J>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, ff, g, h, i, j -> Tuple10(a, b, c, d, e, ff, g, h, i, j) }

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, ff, g, h, i, j -> Tuple10(a, b, c, d, e, ff, g, h, i, j) }", "arrow.core.ListK", "arrow.core.Tuple10"))
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: List<J>
): List<Tuple10<A, B, C, D, E, FF, G, H, I, J>> =
  ListK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, ff, g, h, i, j -> Tuple10(a, b, c, d, e, ff, g, h, i, j) }

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { arg1 }"))
fun <A, B> List<A>.followedBy(arg1: List<B>): List<B> =
  _flatMap { arg1 }

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(this, arg1) { left, _ -> left }", "arrow.core.ListK"))
fun <A, B> List<A>.apTap(arg1: List<B>): List<A> =
  ListK.mapN(this, arg1) { left, _ -> left }

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: ListKApply = object : arrow.core.extensions.ListKApply {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Apply typeclasses is deprecated. Use concrete methods on List")
  inline fun apply(): ListKApply = apply_singleton
}
