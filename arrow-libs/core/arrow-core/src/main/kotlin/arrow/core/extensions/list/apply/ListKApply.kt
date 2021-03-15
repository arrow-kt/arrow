package arrow.core.extensions.list.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ap as _ap
import kotlin.collections.flatMap as _flatMap
import arrow.core.ForListK
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
import arrow.core.extensions.listk.apply.apply
import arrow.core.fix
import arrow.core.k
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

const val ListMapNDeprecated =
  "mapN is no longer supported for List. This operation easily results in extremely big lists, prefer flatMap chains instead."

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ap(arg1)", "arrow.core.ap"))
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
@Deprecated(ListMapNDeprecated)
fun <A, B, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2) as List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2) as List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3) as List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3) as List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4) as List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4) as List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5) as List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5) as List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6) as List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): List<Z> =
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6) as List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6.k(), arg7) as List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6.k(), arg7) as List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6.k(), arg7.k(), arg8) as List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6.k(), arg7.k(), arg8) as List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6.k(), arg7.k(), arg8.k(), arg9) as List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6.k(), arg7.k(), arg8.k(), arg9) as List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6.k(), arg7.k(), arg8.k(), arg9.k(), arg10) as List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.ListK
    .apply()
    .mapN(arg0.k(), arg1.k(), arg2.k(), arg3.k(), arg4.k(), arg5.k(), arg6.k(), arg7.k(), arg8.k(), arg9.k(), arg10) as List<Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, Z> List<A>.map2(arg1: List<B>, arg2: Function1<Tuple2<A, B>, Z>): List<Z> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@map2.k().map2(arg1.k(), arg2) as List<Z>
  }

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B> List<A>.product(arg1: List<B>): List<Tuple2<A, B>> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@product.product<A, B>(arg1) as
      List<arrow.core.Tuple2<A, B>>
  }

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, Z> List<Tuple2<A, B>>.product(arg1: List<Z>): List<Tuple3<A, B, Z>> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@product.product<A, B, Z>(arg1) as
      List<arrow.core.Tuple3<A, B, Z>>
  }

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, Z> List<Tuple3<A, B, C>>.product(arg1: List<Z>): List<Tuple4<A, B, C, Z>> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@product.product<A, B, C, Z>(arg1) as
      List<arrow.core.Tuple4<A, B, C, Z>>
  }

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, Z> List<Tuple4<A, B, C, D>>.product(arg1: List<Z>): List<Tuple5<A, B, C, D, Z>> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@product.product<A, B, C, D, Z>(arg1) as
      List<arrow.core.Tuple5<A, B, C, D, Z>>
  }

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, Z> List<Tuple5<A, B, C, D, E>>.product(arg1: List<Z>): List<Tuple6<A, B, C, D, E, Z>> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@product.product<A, B, C, D, E, Z>(arg1) as
      List<arrow.core.Tuple6<A, B, C, D, E, Z>>
  }

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF, Z> List<Tuple6<A, B, C, D, E, FF>>.product(arg1: List<Z>): List<Tuple7<A, B,
    C, D, E, FF, Z>> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@product.product<A, B, C, D, E, FF, Z>(arg1) as
      List<arrow.core.Tuple7<A, B, C, D, E, FF, Z>>
  }

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF, G, Z> List<Tuple7<A, B, C, D, E, FF, G>>.product(arg1: List<Z>):
  List<Tuple8<A, B, C, D, E, FF, G, Z>> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as
      List<arrow.core.Tuple8<A, B, C, D, E, FF, G, Z>>
  }

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF, G, H, Z> List<Tuple8<A, B, C, D, E, FF, G, H>>.product(arg1: List<Z>):
  List<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as
      List<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, Z>>
  }

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF, G, H, I, Z> List<Tuple9<A, B, C, D, E, FF, G, H, I>>.product(arg1: List<Z>):
  List<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
  arrow.core.extensions.list.apply.List.apply().run {
    this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as
      List<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
  }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B> tupled(arg0: List<A>, arg1: List<B>): List<Tuple2<A, B>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1))
    as List<Tuple2<A, B>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B> tupledN(arg0: List<A>, arg1: List<B>): List<Tuple2<A, B>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1))
    as List<Tuple2<A, B>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>
): List<Tuple3<A, B, C>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2))
    as List<Tuple3<A, B, C>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>
): List<Tuple3<A, B, C>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2))
    as List<Tuple3<A, B, C>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>
): List<Tuple4<A, B, C, D>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3))
    as List<Tuple4<A, B, C, D>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>
): List<Tuple4<A, B, C, D>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3))
    as List<Tuple4<A, B, C, D>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>
): List<Tuple5<A, B, C, D, E>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4))
    as List<Tuple5<A, B, C, D, E>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>
): List<Tuple5<A, B, C, D, E>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4))
    as List<Tuple5<A, B, C, D, E>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>
): List<Tuple6<A, B, C, D, E, FF>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5))
    as List<Tuple6<A, B, C, D, E, FF>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>
): List<Tuple6<A, B, C, D, E, FF>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5))
    as List<Tuple6<A, B, C, D, E, FF>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF, G> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>
): List<Tuple7<A, B, C, D, E, FF, G>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6))
    as List<Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>
): List<Tuple7<A, B, C, D, E, FF, G>> =
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6))
    as List<Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7))
    as List<Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7))
    as List<Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8))
    as List<Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8))
    as List<Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
  arrow.core.extensions.list.apply.List
    .apply()
    .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8), arrow.core.ListK(arg9))
    as List<Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(ListMapNDeprecated)
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
): List<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.extensions.list.apply.List
  .apply()
  .tupledN(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8), arrow.core.ListK(arg9))
  as List<Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { a -> arg1.map { a } }"))
fun <A, B> List<A>.apTap(arg1: List<B>): List<A> =
  _flatMap { a -> arg1.map { a } }

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
