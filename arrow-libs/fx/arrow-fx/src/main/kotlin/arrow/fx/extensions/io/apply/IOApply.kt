package arrow.fx.extensions.io.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.IODeprecation
import arrow.fx.extensions.IOApply
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: IOApply = object : arrow.fx.extensions.IOApply {}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.ap(arg1: Kind<ForIO, Function1<A, B>>): IO<B> = arrow.fx.IO.apply().run {
  this@ap.ap<A, B>(arg1) as arrow.fx.IO<B>
}

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.apEval(arg1: Eval<Kind<ForIO, Function1<A, B>>>): Eval<Kind<ForIO, B>> =
    arrow.fx.IO.apply().run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.fx.ForIO, B>>
}

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, Z> Kind<ForIO, A>.map2Eval(arg1: Eval<Kind<ForIO, B>>, arg2: Function1<Tuple2<A, B>, Z>):
    Eval<Kind<ForIO, Z>> = arrow.fx.IO.apply().run {
  this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as arrow.core.Eval<arrow.Kind<arrow.fx.ForIO, Z>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, Z> map(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .map<A, B, Z>(arg0, arg1, arg2) as arrow.fx.IO<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, Z> mapN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.fx.IO<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, Z> map(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.fx.IO<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, Z> mapN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.fx.IO<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, Z> map(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.fx.IO<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, Z> mapN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.fx.IO<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, Z> map(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.fx.IO<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, Z> mapN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.fx.IO<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, Z> map(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.fx.IO<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.fx.IO<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as arrow.fx.IO<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as arrow.fx.IO<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.fx.IO<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.fx.IO<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Kind<ForIO, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.fx.IO<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Kind<ForIO, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.fx.IO<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Kind<ForIO, I>,
  arg9: Kind<ForIO, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.fx.IO<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Kind<ForIO, I>,
  arg9: Kind<ForIO, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): IO<Z> = arrow.fx.IO
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.fx.IO<Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, Z> Kind<ForIO, A>.map2(arg1: Kind<ForIO, B>, arg2: Function1<Tuple2<A, B>, Z>): IO<Z> =
    arrow.fx.IO.apply().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.fx.IO<Z>
}

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.product(arg1: Kind<ForIO, B>): IO<Tuple2<A, B>> =
    arrow.fx.IO.apply().run {
  this@product.product<A, B>(arg1) as arrow.fx.IO<arrow.core.Tuple2<A, B>>
}

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, Z> Kind<ForIO, Tuple2<A, B>>.product(arg1: Kind<ForIO, Z>): IO<Tuple3<A, B, Z>> =
    arrow.fx.IO.apply().run {
  this@product.product<A, B, Z>(arg1) as arrow.fx.IO<arrow.core.Tuple3<A, B, Z>>
}

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, Z> Kind<ForIO, Tuple3<A, B, C>>.product(arg1: Kind<ForIO, Z>): IO<Tuple4<A, B, C, Z>> =
    arrow.fx.IO.apply().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.fx.IO<arrow.core.Tuple4<A, B, C, Z>>
}

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, Z> Kind<ForIO, Tuple4<A, B, C, D>>.product(arg1: Kind<ForIO, Z>): IO<Tuple5<A, B,
    C, D, Z>> = arrow.fx.IO.apply().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.fx.IO<arrow.core.Tuple5<A, B, C, D, Z>>
}

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, Z> Kind<ForIO, Tuple5<A, B, C, D, E>>.product(arg1: Kind<ForIO, Z>):
    IO<Tuple6<A, B, C, D, E, Z>> = arrow.fx.IO.apply().run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.fx.IO<arrow.core.Tuple6<A, B, C, D, E, Z>>
}

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, Z> Kind<ForIO, Tuple6<A, B, C, D, E, FF>>.product(arg1: Kind<ForIO, Z>):
    IO<Tuple7<A, B, C, D, E, FF, Z>> = arrow.fx.IO.apply().run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.fx.IO<arrow.core.Tuple7<A, B, C, D, E,
    FF, Z>>
}

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, Z> Kind<ForIO, Tuple7<A, B, C, D, E, FF, G>>.product(
  arg1: Kind<ForIO,
Z>
): IO<Tuple8<A, B, C, D, E, FF, G, Z>> = arrow.fx.IO.apply().run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.fx.IO<arrow.core.Tuple8<A, B, C, D,
    E, FF, G, Z>>
}

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, Z> Kind<ForIO, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(arg1: Kind<ForIO, Z>): IO<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
    arrow.fx.IO.apply().run {
  this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.fx.IO<arrow.core.Tuple9<A, B, C,
    D, E, FF, G, H, Z>>
}

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, I, Z> Kind<ForIO, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(arg1: Kind<ForIO, Z>): IO<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
    arrow.fx.IO.apply().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.fx.IO<arrow.core.Tuple10<A, B,
    C, D, E, FF, G, H, I, Z>>
}

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> tupled(arg0: Kind<ForIO, A>, arg1: Kind<ForIO, B>): IO<Tuple2<A, B>> = arrow.fx.IO
   .apply()
   .tupled<A, B>(arg0, arg1) as arrow.fx.IO<arrow.core.Tuple2<A, B>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> tupledN(arg0: Kind<ForIO, A>, arg1: Kind<ForIO, B>): IO<Tuple2<A, B>> = arrow.fx.IO
   .apply()
   .tupledN<A, B>(arg0, arg1) as arrow.fx.IO<arrow.core.Tuple2<A, B>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C> tupled(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>
): IO<Tuple3<A, B, C>> = arrow.fx.IO
   .apply()
   .tupled<A, B, C>(arg0, arg1, arg2) as arrow.fx.IO<arrow.core.Tuple3<A, B, C>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C> tupledN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>
): IO<Tuple3<A, B, C>> = arrow.fx.IO
   .apply()
   .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.fx.IO<arrow.core.Tuple3<A, B, C>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D> tupled(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>
): IO<Tuple4<A, B, C, D>> = arrow.fx.IO
   .apply()
   .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.fx.IO<arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D> tupledN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>
): IO<Tuple4<A, B, C, D>> = arrow.fx.IO
   .apply()
   .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.fx.IO<arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E> tupled(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>
): IO<Tuple5<A, B, C, D, E>> = arrow.fx.IO
   .apply()
   .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.fx.IO<arrow.core.Tuple5<A, B, C, D,
    E>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E> tupledN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>
): IO<Tuple5<A, B, C, D, E>> = arrow.fx.IO
   .apply()
   .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.fx.IO<arrow.core.Tuple5<A, B, C,
    D, E>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF> tupled(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>
): IO<Tuple6<A, B, C, D, E, FF>> = arrow.fx.IO
   .apply()
   .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.fx.IO<arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF> tupledN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>
): IO<Tuple6<A, B, C, D, E, FF>> = arrow.fx.IO
   .apply()
   .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.fx.IO<arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G> tupled(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>
): IO<Tuple7<A, B, C, D, E, FF, G>> = arrow.fx.IO
   .apply()
   .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.fx.IO<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>
): IO<Tuple7<A, B, C, D, E, FF, G>> = arrow.fx.IO
   .apply()
   .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.fx.IO<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>
): IO<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.fx.IO
   .apply()
   .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.fx.IO<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>
): IO<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.fx.IO
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.fx.IO<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Kind<ForIO, I>
): IO<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.fx.IO
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.fx.IO<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Kind<ForIO, I>
): IO<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.fx.IO
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.fx.IO<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Kind<ForIO, I>,
  arg9: Kind<ForIO, J>
): IO<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.fx.IO
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.fx.IO<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<ForIO, A>,
  arg1: Kind<ForIO, B>,
  arg2: Kind<ForIO, C>,
  arg3: Kind<ForIO, D>,
  arg4: Kind<ForIO, E>,
  arg5: Kind<ForIO, FF>,
  arg6: Kind<ForIO, G>,
  arg7: Kind<ForIO, H>,
  arg8: Kind<ForIO, I>,
  arg9: Kind<ForIO, J>
): IO<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.fx.IO
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.fx.IO<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.followedBy(arg1: Kind<ForIO, B>): IO<B> = arrow.fx.IO.apply().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.fx.IO<B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <A, B> Kind<ForIO, A>.apTap(arg1: Kind<ForIO, B>): IO<A> = arrow.fx.IO.apply().run {
  this@apTap.apTap<A, B>(arg1) as arrow.fx.IO<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.apply(): IOApply = apply_singleton
