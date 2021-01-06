package arrow.core.extensions.listk.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
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
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: ListKApply = object : arrow.core.extensions.ListKApply {}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("_ap(arg1)", "arrow.core.ap"))
fun <A, B> Kind<ForListK, A>.ap(arg1: Kind<ForListK, Function1<A, B>>): ListK<B> =
  arrow.core.ListK.apply().run {
    this@ap.ap<A, B>(arg1) as arrow.core.ListK<B>
  }

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("arg1.map { this.ap(it.fix()) }.map { it.k() }", "arrow.core.k", "arrow.core.fix"))
fun <A, B> Kind<ForListK, A>.apEval(arg1: Eval<Kind<ForListK, Function1<A, B>>>):
  Eval<Kind<ForListK, B>> = arrow.core.ListK.apply().run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.core.ForListK, B>>
}

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("apEval(arg1.map { it.fix().map { b -> { a: A -> arg2(Tuple2(a, b)) } } })", "arrow.core.k", "arrow.core.Tuple2"))
fun <A, B, Z> Kind<ForListK, A>.map2Eval(
  arg1: Eval<Kind<ForListK, B>>,
  arg2: Function1<Tuple2<A,
    B>, Z>
): Eval<Kind<ForListK, Z>> = arrow.core.ListK.apply().run {
  this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as arrow.core.Eval<arrow.Kind<arrow.core.ForListK, Z>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1).map(arg2)", "arrow.core.tupledN"))
fun <A, B, Z> map(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.ListK<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1).map(arg2)", "arrow.core.tupledN"))
fun <A, B, Z> mapN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.ListK<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2).map(arg3)", "arrow.core.tupledN"))
fun <A, B, C, Z> map(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.ListK<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2).map(arg3)", "arrow.core.tupledN"))
fun <A, B, C, Z> mapN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.ListK<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3).map(arg4)", "arrow.core.tupledN"))
fun <A, B, C, D, Z> map(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.ListK<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3).map(arg4)", "arrow.core.tupledN"))
fun <A, B, C, D, Z> mapN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.ListK<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4).map(arg5)", "arrow.core.tupledN"))
fun <A, B, C, D, E, Z> map(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.ListK<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4).map(arg5)", "arrow.core.tupledN"))
fun <A, B, C, D, E, Z> mapN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.ListK<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5).map(arg6)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, Z> map(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.ListK<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5).map(arg6)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.ListK<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6).map(arg7)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.ListK<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6).map(arg7)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.ListK<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7).map(arg8)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.ListK<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7).map(arg8)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.ListK<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8).map(arg9)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Kind<ForListK, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.core.ListK<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8).map(arg9)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Kind<ForListK, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.core.ListK<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9).map(arg10)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Kind<ForListK, I>,
  arg9: Kind<ForListK, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.ListK<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9).map(arg10)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Kind<ForListK, I>,
  arg9: Kind<ForListK, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): ListK<Z> = arrow.core.ListK
  .apply()
  .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.ListK<Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map2(arg1, arg2)", "arrow.core.map2"))
fun <A, B, Z> Kind<ForListK, A>.map2(arg1: Kind<ForListK, B>, arg2: Function1<Tuple2<A, B>, Z>):
  ListK<Z> = arrow.core.ListK.apply().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.ListK<Z>
}

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B> Kind<ForListK, A>.product(arg1: Kind<ForListK, B>): ListK<Tuple2<A, B>> =
  arrow.core.ListK.apply().run {
    this@product.product<A, B>(arg1) as arrow.core.ListK<arrow.core.Tuple2<A, B>>
  }

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, Z> Kind<ForListK, Tuple2<A, B>>.product(arg1: Kind<ForListK, Z>): ListK<Tuple3<A, B, Z>> =
  arrow.core.ListK.apply().run {
    this@product.product<A, B, Z>(arg1) as arrow.core.ListK<arrow.core.Tuple3<A, B, Z>>
  }

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, Z> Kind<ForListK, Tuple3<A, B, C>>.product(arg1: Kind<ForListK, Z>): ListK<Tuple4<A,
  B, C, Z>> = arrow.core.ListK.apply().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.ListK<arrow.core.Tuple4<A, B, C, Z>>
}

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, Z> Kind<ForListK, Tuple4<A, B, C, D>>.product(arg1: Kind<ForListK, Z>):
  ListK<Tuple5<A, B, C, D, Z>> = arrow.core.ListK.apply().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.ListK<arrow.core.Tuple5<A, B, C, D, Z>>
}

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, Z> Kind<ForListK, Tuple5<A, B, C, D, E>>.product(arg1: Kind<ForListK, Z>):
  ListK<Tuple6<A, B, C, D, E, Z>> = arrow.core.ListK.apply().run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.ListK<arrow.core.Tuple6<A, B, C, D, E,
    Z>>
}

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, FF, Z> Kind<ForListK, Tuple6<A, B, C, D, E, FF>>.product(
  arg1: Kind<ForListK,
    Z>
): ListK<Tuple7<A, B, C, D, E, FF, Z>> = arrow.core.ListK.apply().run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.ListK<arrow.core.Tuple7<A, B, C, D,
    E, FF, Z>>
}

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, FF, G, Z> Kind<ForListK, Tuple7<A, B, C, D, E, FF,
  G>>.product(arg1: Kind<ForListK, Z>): ListK<Tuple8<A, B, C, D, E, FF, G, Z>> =
  arrow.core.ListK.apply().run {
    this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.ListK<arrow.core.Tuple8<A, B, C,
      D, E, FF, G, Z>>
  }

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, FF, G, H, Z> Kind<ForListK, Tuple8<A, B, C, D, E, FF, G,
  H>>.product(arg1: Kind<ForListK, Z>): ListK<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  arrow.core.ListK.apply().run {
    this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.ListK<arrow.core.Tuple9<A, B,
      C, D, E, FF, G, H, Z>>
  }

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("product(arg1)", "arrow.core.product"))
fun <A, B, C, D, E, FF, G, H, I, Z> Kind<ForListK, Tuple9<A, B, C, D, E, FF, G, H,
  I>>.product(arg1: Kind<ForListK, Z>): ListK<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
  arrow.core.ListK.apply().run {
    this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as
      arrow.core.ListK<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
  }

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1)", "arrow.core.tupledN"))
fun <A, B> tupled(arg0: Kind<ForListK, A>, arg1: Kind<ForListK, B>): ListK<Tuple2<A, B>> =
  arrow.core.ListK
    .apply()
    .tupled<A, B>(arg0, arg1) as arrow.core.ListK<arrow.core.Tuple2<A, B>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1)", "arrow.core.tupledN"))
fun <A, B> tupledN(arg0: Kind<ForListK, A>, arg1: Kind<ForListK, B>): ListK<Tuple2<A, B>> =
  arrow.core.ListK
    .apply()
    .tupledN<A, B>(arg0, arg1) as arrow.core.ListK<arrow.core.Tuple2<A, B>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2)", "arrow.core.tupledN"))
fun <A, B, C> tupled(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>
): ListK<Tuple3<A, B, C>> = arrow.core.ListK
  .apply()
  .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.ListK<arrow.core.Tuple3<A, B, C>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2)", "arrow.core.tupledN"))
fun <A, B, C> tupledN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>
): ListK<Tuple3<A, B, C>> = arrow.core.ListK
  .apply()
  .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.ListK<arrow.core.Tuple3<A, B, C>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3)", "arrow.core.tupledN"))
fun <A, B, C, D> tupled(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>
): ListK<Tuple4<A, B, C, D>> = arrow.core.ListK
  .apply()
  .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.ListK<arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3)", "arrow.core.tupledN"))
fun <A, B, C, D> tupledN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>
): ListK<Tuple4<A, B, C, D>> = arrow.core.ListK
  .apply()
  .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.ListK<arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4)", "arrow.core.tupledN"))
fun <A, B, C, D, E> tupled(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>
): ListK<Tuple5<A, B, C, D, E>> = arrow.core.ListK
  .apply()
  .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.ListK<arrow.core.Tuple5<A, B,
  C, D, E>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4)", "arrow.core.tupledN"))
fun <A, B, C, D, E> tupledN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>
): ListK<Tuple5<A, B, C, D, E>> = arrow.core.ListK
  .apply()
  .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.ListK<arrow.core.Tuple5<A, B,
  C, D, E>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF> tupled(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>
): ListK<Tuple6<A, B, C, D, E, FF>> = arrow.core.ListK
  .apply()
  .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
  arrow.core.ListK<arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF> tupledN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>
): ListK<Tuple6<A, B, C, D, E, FF>> = arrow.core.ListK
  .apply()
  .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
  arrow.core.ListK<arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G> tupled(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>
): ListK<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.ListK
  .apply()
  .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
  arrow.core.ListK<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>
): ListK<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.ListK
  .apply()
  .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
  arrow.core.ListK<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>
): ListK<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.ListK
  .apply()
  .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.ListK<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>
): ListK<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.ListK
  .apply()
  .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.ListK<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Kind<ForListK, I>
): ListK<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.ListK
  .apply()
  .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.ListK<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Kind<ForListK, I>
): ListK<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.ListK
  .apply()
  .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.ListK<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Kind<ForListK, I>,
  arg9: Kind<ForListK, J>
): ListK<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.ListK
  .apply()
  .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
  arrow.core.ListK<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)", "arrow.core.tupledN"))
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Kind<ForListK, C>,
  arg3: Kind<ForListK, D>,
  arg4: Kind<ForListK, E>,
  arg5: Kind<ForListK, FF>,
  arg6: Kind<ForListK, G>,
  arg7: Kind<ForListK, H>,
  arg8: Kind<ForListK, I>,
  arg9: Kind<ForListK, J>
): ListK<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.ListK
  .apply()
  .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
  arrow.core.ListK<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("flatMap { arg1 }"))
fun <A, B> Kind<ForListK, A>.followedBy(arg1: Kind<ForListK, B>): ListK<B> =
  arrow.core.ListK.apply().run {
    this@followedBy.followedBy<A, B>(arg1) as arrow.core.ListK<B>
  }

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("ListK.mapN(this, arg1) { left, _ -> left }", "arrow.core.mapN"))
fun <A, B> Kind<ForListK, A>.apTap(arg1: Kind<ForListK, B>): ListK<A> =
  arrow.core.ListK.apply().run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.ListK<A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Apply typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.apply(): ListKApply = apply_singleton
