package arrow.core.extensions.andthen.apply

import arrow.Kind
import arrow.core.AndThen
import arrow.core.AndThen.Companion
import arrow.core.Eval
import arrow.core.ForAndThen
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.AndThenApply
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: AndThenApply<Any?> = object : AndThenApply<Any?> {}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "ap(arg1)",
  "arrow.core.ap"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.ap(arg1: Kind<Kind<ForAndThen, X>, Function1<A, B>>):
    AndThen<X, B> = arrow.core.AndThen.apply<X>().run {
  this@ap.ap<A, B>(arg1) as arrow.core.AndThen<X, B>
}

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "apEval(arg1)",
  "arrow.core.apEval"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.apEval(arg1: Eval<Kind<Kind<ForAndThen, X>, Function1<A, B>>>):
  Eval<Kind<Kind<ForAndThen, X>, B>> = arrow.core.AndThen.apply<X>().run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForAndThen, X>,
    B>>
}

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map2Eval(arg1, arg2)",
  "arrow.core.map2Eval"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, Z> Kind<Kind<ForAndThen, X>, A>.map2Eval(
  arg1: Eval<Kind<Kind<ForAndThen, X>, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<Kind<ForAndThen, X>, Z>> =
    arrow.core.AndThen.apply<X>().run {
  this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
    arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForAndThen, X>, Z>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2)",
  "arrow.core.AndThen.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, Z> map(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.AndThen<X, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2)",
  "arrow.core.AndThen.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, Z> mapN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.AndThen<X, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3)",
  "arrow.core.AndThen.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, Z> map(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.AndThen<X, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3)",
  "arrow.core.AndThen.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, Z> mapN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.AndThen<X, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.AndThen.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, Z> map(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.AndThen<X, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.AndThen.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, Z> mapN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.AndThen<X, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.AndThen.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, Z> map(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.AndThen<X, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.AndThen.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, Z> mapN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.AndThen<X, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.AndThen.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, Z> map(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.AndThen<X, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.AndThen.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.AndThen<X, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.AndThen.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.AndThen<X, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.AndThen.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.AndThen<X, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.AndThen.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.AndThen<X, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.AndThen.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.AndThen<X, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.AndThen.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Kind<Kind<ForAndThen, X>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.AndThen<X, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.AndThen.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Kind<Kind<ForAndThen, X>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.AndThen<X, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.AndThen.map"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Kind<Kind<ForAndThen, X>, I>,
  arg9: Kind<Kind<ForAndThen, X>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.AndThen<X,
    Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.AndThen.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Kind<Kind<ForAndThen, X>, I>,
  arg9: Kind<Kind<ForAndThen, X>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): AndThen<X, Z> = arrow.core.AndThen
   .apply<X>()
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.AndThen<X,
    Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map2(arg1, arg2)",
  "arrow.core.map2"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, Z> Kind<Kind<ForAndThen, X>, A>.map2(
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): AndThen<X, Z> = arrow.core.AndThen.apply<X>().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.AndThen<X, Z>
}

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.product(arg1: Kind<Kind<ForAndThen, X>, B>): AndThen<X,
    Tuple2<A, B>> = arrow.core.AndThen.apply<X>().run {
  this@product.product<A, B>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple2<A, B>>
}

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, Z> Kind<Kind<ForAndThen, X>, Tuple2<A, B>>.product(
  arg1: Kind<Kind<ForAndThen, X>, Z>
): AndThen<X, Tuple3<A, B, Z>> = arrow.core.AndThen.apply<X>().run {
  this@product.product<A, B, Z>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple3<A, B, Z>>
}

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, Z> Kind<Kind<ForAndThen, X>, Tuple3<A, B, C>>.product(
  arg1: Kind<Kind<ForAndThen, X>, Z>
): AndThen<X, Tuple4<A, B, C, Z>> = arrow.core.AndThen.apply<X>().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple4<A, B, C, Z>>
}

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, Z> Kind<Kind<ForAndThen, X>, Tuple4<A, B, C,
    D>>.product(arg1: Kind<Kind<ForAndThen, X>, Z>): AndThen<X, Tuple5<A, B, C, D, Z>> =
    arrow.core.AndThen.apply<X>().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple5<A, B, C, D,
    Z>>
}

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, Z> Kind<Kind<ForAndThen, X>, Tuple5<A, B, C, D,
    E>>.product(arg1: Kind<Kind<ForAndThen, X>, Z>): AndThen<X, Tuple6<A, B, C, D, E, Z>> =
    arrow.core.AndThen.apply<X>().run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple6<A, B, C,
    D, E, Z>>
}

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, Z> Kind<Kind<ForAndThen, X>, Tuple6<A, B, C, D, E,
    FF>>.product(arg1: Kind<Kind<ForAndThen, X>, Z>): AndThen<X, Tuple7<A, B, C, D, E, FF, Z>> =
    arrow.core.AndThen.apply<X>().run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple7<A, B,
    C, D, E, FF, Z>>
}

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, Z> Kind<Kind<ForAndThen, X>, Tuple7<A, B, C, D, E, FF,
    G>>.product(arg1: Kind<Kind<ForAndThen, X>, Z>): AndThen<X, Tuple8<A, B, C, D, E, FF, G, Z>> =
    arrow.core.AndThen.apply<X>().run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.AndThen<X, arrow.core.Tuple8<A,
    B, C, D, E, FF, G, Z>>
}

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, Z> Kind<Kind<ForAndThen, X>, Tuple8<A, B, C, D, E, FF, G,
  H>>.product(arg1: Kind<Kind<ForAndThen, X>, Z>): AndThen<X, Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  arrow.core.AndThen.apply<X>().run {
    this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.AndThen<X,
      arrow.core.Tuple9<A, B, C, D, E, FF, G, H, Z>>
  }

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, I, Z> Kind<Kind<ForAndThen, X>, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(arg1: Kind<Kind<ForAndThen, X>, Z>): AndThen<X, Tuple10<A, B, C, D, E, FF, G, H, I,
    Z>> = arrow.core.AndThen.apply<X>().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.core.AndThen<X,
    arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
}

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1)",
  "arrow.core.AndThen.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> tupled(arg0: Kind<Kind<ForAndThen, X>, A>, arg1: Kind<Kind<ForAndThen, X>, B>):
    AndThen<X, Tuple2<A, B>> = arrow.core.AndThen
   .apply<X>()
   .tupled<A, B>(arg0, arg1) as arrow.core.AndThen<X, arrow.core.Tuple2<A, B>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1)",
  "arrow.core.AndThen.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> tupledN(arg0: Kind<Kind<ForAndThen, X>, A>, arg1: Kind<Kind<ForAndThen, X>, B>):
    AndThen<X, Tuple2<A, B>> = arrow.core.AndThen
   .apply<X>()
   .tupledN<A, B>(arg0, arg1) as arrow.core.AndThen<X, arrow.core.Tuple2<A, B>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2)",
  "arrow.core.AndThen.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C> tupled(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>
): AndThen<X, Tuple3<A, B, C>> = arrow.core.AndThen
   .apply<X>()
   .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.AndThen<X, arrow.core.Tuple3<A, B, C>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2)",
  "arrow.core.AndThen.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C> tupledN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>
): AndThen<X, Tuple3<A, B, C>> = arrow.core.AndThen
   .apply<X>()
   .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.AndThen<X, arrow.core.Tuple3<A, B, C>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3)",
  "arrow.core.AndThen.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D> tupled(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>
): AndThen<X, Tuple4<A, B, C, D>> = arrow.core.AndThen
   .apply<X>()
   .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.AndThen<X, arrow.core.Tuple4<A, B, C,
    D>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3)",
  "arrow.core.AndThen.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D> tupledN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>
): AndThen<X, Tuple4<A, B, C, D>> = arrow.core.AndThen
   .apply<X>()
   .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.AndThen<X, arrow.core.Tuple4<A, B, C,
    D>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.AndThen.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E> tupled(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>
): AndThen<X, Tuple5<A, B, C, D, E>> = arrow.core.AndThen
   .apply<X>()
   .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.AndThen<X,
    arrow.core.Tuple5<A, B, C, D, E>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.AndThen.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E> tupledN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>
): AndThen<X, Tuple5<A, B, C, D, E>> = arrow.core.AndThen
   .apply<X>()
   .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.AndThen<X,
    arrow.core.Tuple5<A, B, C, D, E>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.AndThen.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF> tupled(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>
): AndThen<X, Tuple6<A, B, C, D, E, FF>> = arrow.core.AndThen
   .apply<X>()
   .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.AndThen<X,
    arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.AndThen.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF> tupledN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>
): AndThen<X, Tuple6<A, B, C, D, E, FF>> = arrow.core.AndThen
   .apply<X>()
   .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.AndThen<X,
    arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.AndThen.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G> tupled(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>
): AndThen<X, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.AndThen
   .apply<X>()
   .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.AndThen<X,
    arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.AndThen.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>
): AndThen<X, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.AndThen
   .apply<X>()
   .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.AndThen<X,
    arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.AndThen.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>
): AndThen<X, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.AndThen
   .apply<X>()
   .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.AndThen<X, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.AndThen.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>
): AndThen<X, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.AndThen
   .apply<X>()
   .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.AndThen<X, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.AndThen.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Kind<Kind<ForAndThen, X>, I>
): AndThen<X, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.AndThen
   .apply<X>()
   .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.AndThen<X, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.AndThen.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Kind<Kind<ForAndThen, X>, I>
): AndThen<X, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.AndThen
   .apply<X>()
   .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.AndThen<X, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.AndThen.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Kind<Kind<ForAndThen, X>, I>,
  arg9: Kind<Kind<ForAndThen, X>, J>
): AndThen<X, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.AndThen
   .apply<X>()
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.AndThen<X,
    arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.AndThen.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<Kind<ForAndThen, X>, A>,
  arg1: Kind<Kind<ForAndThen, X>, B>,
  arg2: Kind<Kind<ForAndThen, X>, C>,
  arg3: Kind<Kind<ForAndThen, X>, D>,
  arg4: Kind<Kind<ForAndThen, X>, E>,
  arg5: Kind<Kind<ForAndThen, X>, FF>,
  arg6: Kind<Kind<ForAndThen, X>, G>,
  arg7: Kind<Kind<ForAndThen, X>, H>,
  arg8: Kind<Kind<ForAndThen, X>, I>,
  arg9: Kind<Kind<ForAndThen, X>, J>
): AndThen<X, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.AndThen
   .apply<X>()
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.AndThen<X,
    arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "followedBy(arg1)",
  "arrow.core.followedBy"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.followedBy(arg1: Kind<Kind<ForAndThen, X>, B>):
    AndThen<X, B> = arrow.core.AndThen.apply<X>().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.AndThen<X, B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "apTap(arg1)",
  "arrow.core.apTap"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForAndThen, X>, A>.apTap(arg1: Kind<Kind<ForAndThen, X>, B>): AndThen<X, A> =
  arrow.core.AndThen.apply<X>().run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.AndThen<X, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <X> Companion.apply(): AndThenApply<X> = apply_singleton as
    arrow.core.extensions.AndThenApply<X>
