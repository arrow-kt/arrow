package arrow.core.extensions.list.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ap as _ap
import arrow.core.map2 as _map2
import arrow.core.product as _product
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1).map(arg2)", "arrow.core.tupledN"))
fun <A, B, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1).map(arg2)

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1).map(arg2)", "arrow.core.tupledN"))
fun <A, B, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1).map(arg2)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2).map(arg3)", "arrow.core.tupledN"))
fun <A, B, C, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1, arg2).map(arg3)

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2).map(arg3)", "arrow.core.tupledN"))
fun <A, B, C, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1, arg2).map(arg3)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3).map(arg4)", "arrow.core.tupledN"))
fun <A, B, C, D, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1, arg2, arg3).map(arg4)

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3).map(arg4)", "arrow.core.tupledN"))
fun <A, B, C, D, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1, arg2, arg3).map(arg4)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4).map(arg5)", "arrow.core.tupledN"))
fun <A, B, C, D, E, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4).map(arg5)

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4).map(arg5)", "arrow.core.tupledN"))
fun <A, B, C, D, E, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4).map(arg5)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5).map(arg6)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5).map(arg6)

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5).map(arg6)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): List<Z> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5).map(arg6)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6).map(arg7)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6).map(arg7)

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6).map(arg7)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6).map(arg7)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7).map(arg8)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7).map(arg8)

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7).map(arg8)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7).map(arg8)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8).map(arg9)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8).map(arg9)

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8).map(arg9)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8).map(arg9)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9).map(arg10)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9).map(arg10)

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9).map(arg10)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9).map(arg10)

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map2(arg1, arg2)", "arrow.core.map2"))
fun <A, B, Z> List<A>.map2(arg1: List<B>, arg2: Function1<Tuple2<A, B>, Z>): List<Z> =
  _map2(arg1, arg2)

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B> List<A>.product(arg1: List<B>): List<Tuple2<A, B>> =
  _product(arg1)

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, Z> List<Tuple2<A, B>>.product(arg1: List<Z>): List<Tuple3<A, B, Z>> =
  _product(arg1)

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, Z> List<Tuple3<A, B, C>>.product(arg1: List<Z>): List<Tuple4<A, B, C, Z>> =
  _product(arg1)

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, Z> List<Tuple4<A, B, C, D>>.product(arg1: List<Z>): List<Tuple5<A, B, C, D, Z>> =
  _product(arg1)

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, Z> List<Tuple5<A, B, C, D, E>>.product(arg1: List<Z>): List<Tuple6<A, B, C, D, E, Z>> =
  _product(arg1)

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, FF, Z> List<Tuple6<A, B, C, D, E, FF>>.product(arg1: List<Z>): List<Tuple7<A, B,
  C, D, E, FF, Z>> =
  _product(arg1)

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, FF, G, Z> List<Tuple7<A, B, C, D, E, FF, G>>.product(arg1: List<Z>):
  List<Tuple8<A, B, C, D, E, FF, G, Z>> =
  _product(arg1)

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, FF, G, H, Z> List<Tuple8<A, B, C, D, E, FF, G, H>>.product(arg1: List<Z>):
  List<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  _product(arg1)

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, FF, G, H, I, Z> List<Tuple9<A, B, C, D, E, FF, G, H, I>>.product(arg1: List<Z>):
  List<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
  _product(arg1)

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1)", "arrow.core.tupledN"))
fun <A, B> tupled(arg0: List<A>, arg1: List<B>): List<Tuple2<A, B>> =
  ListK.tupledN(arg0, arg1)

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1)", "arrow.core.tupledN"))
fun <A, B> tupledN(arg0: List<A>, arg1: List<B>): List<Tuple2<A, B>> =
  ListK.tupledN(arg0, arg1)

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2)", "arrow.core.tupledN"))
fun <A, B, C> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>
): List<Tuple3<A, B, C>> =
  ListK.tupledN(arg0, arg1, arg2)

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2)", "arrow.core.tupledN"))
fun <A, B, C> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>
): List<Tuple3<A, B, C>> =
  ListK.tupledN(arg0, arg1, arg2)

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3)", "arrow.core.tupledN"))
fun <A, B, C, D> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>
): List<Tuple4<A, B, C, D>> =
  ListK.tupledN(arg0, arg1, arg2, arg3)

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3)", "arrow.core.tupledN"))
fun <A, B, C, D> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>
): List<Tuple4<A, B, C, D>> =
  ListK.tupledN(arg0, arg1, arg2, arg3)

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4)", "arrow.core.tupledN"))
fun <A, B, C, D, E> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>
): List<Tuple5<A, B, C, D, E>> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4)

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4)", "arrow.core.tupledN"))
fun <A, B, C, D, E> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>
): List<Tuple5<A, B, C, D, E>> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4)

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>
): List<Tuple6<A, B, C, D, E, FF>> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5)

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>
): List<Tuple6<A, B, C, D, E, FF>> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5)

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>
): List<Tuple7<A, B, C, D, E, FF, G>> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>
): List<Tuple7<A, B, C, D, E, FF, G>> =
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)", "arrow.core.tupledN"))
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
  ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)

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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(this, arg1) { left, _ -> left }", "arrow.core.mapN"))
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
