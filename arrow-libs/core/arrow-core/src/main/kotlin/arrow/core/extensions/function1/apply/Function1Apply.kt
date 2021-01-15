package arrow.core.extensions.function1.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForFunction1
import arrow.core.Function1.Companion
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.Function1Apply
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
internal val apply_singleton: Function1Apply<Any?> = object : Function1Apply<Any?> {}

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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.ap(arg1: Kind<Kind<ForFunction1, I>, Function1<A, B>>):
    arrow.core.Function1<I, B> = arrow.core.Function1.apply<I>().run {
  this@ap.ap<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.apEval(
  arg1: Eval<Kind<Kind<ForFunction1, I>, Function1<A, B>>>
): Eval<Kind<Kind<ForFunction1, I>, B>> = arrow.core.Function1.apply<I>().run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForFunction1,
    I>, B>>
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
fun <I, A, B, Z> Kind<Kind<ForFunction1, I>, A>.map2Eval(
  arg1: Eval<Kind<Kind<ForFunction1, I>, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<Kind<ForFunction1, I>, Z>> =
  arrow.core.Function1.apply<I>().run {
    this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
      arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForFunction1, I>, Z>>
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
  "arrow.core.Function1.map"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, Z> map(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, Z> mapN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.map"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, Z> map(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, Z> mapN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.map"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, Z> map(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, Z> mapN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.map"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, Z> map(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, Z> mapN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.map"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, Z> map(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Function1<I,
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
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.Function1.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Function1<I,
    Z>

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
  "arrow.core.Function1.map"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.map"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.map"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Kind<Kind<ForFunction1, I>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Kind<Kind<ForFunction1, I>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Function1<I, Z>

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
  "arrow.core.Function1.map"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H, J, Z> map(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Kind<Kind<ForFunction1, I>, I>,
  arg9: Kind<Kind<ForFunction1, I>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Function1<I,
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
  "arrow.core.Function1.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H, J, Z> mapN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Kind<Kind<ForFunction1, I>, I>,
  arg9: Kind<Kind<ForFunction1, I>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): arrow.core.Function1<I, Z> = arrow.core.Function1
   .apply<I>()
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Function1<I,
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
fun <I, A, B, Z> Kind<Kind<ForFunction1, I>, A>.map2(
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): arrow.core.Function1<I, Z> =
  arrow.core.Function1.apply<I>().run {
    this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.Function1<I, Z>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.product(arg1: Kind<Kind<ForFunction1, I>, B>):
    arrow.core.Function1<I, Tuple2<A, B>> = arrow.core.Function1.apply<I>().run {
  this@product.product<A, B>(arg1) as arrow.core.Function1<I, arrow.core.Tuple2<A, B>>
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
fun <I, A, B, Z> Kind<Kind<ForFunction1, I>, Tuple2<A, B>>.product(
  arg1: Kind<Kind<ForFunction1, I>, Z>
): arrow.core.Function1<I, Tuple3<A, B, Z>> = arrow.core.Function1.apply<I>().run {
  this@product.product<A, B, Z>(arg1) as arrow.core.Function1<I, arrow.core.Tuple3<A, B, Z>>
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
fun <I, A, B, C, Z> Kind<Kind<ForFunction1, I>, Tuple3<A, B,
    C>>.product(arg1: Kind<Kind<ForFunction1, I>, Z>): arrow.core.Function1<I, Tuple4<A, B, C, Z>> =
    arrow.core.Function1.apply<I>().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.Function1<I, arrow.core.Tuple4<A, B, C, Z>>
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
fun <I, A, B, C, D, Z> Kind<Kind<ForFunction1, I>, Tuple4<A, B, C,
    D>>.product(arg1: Kind<Kind<ForFunction1, I>, Z>): arrow.core.Function1<I, Tuple5<A, B, C, D,
    Z>> = arrow.core.Function1.apply<I>().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.Function1<I, arrow.core.Tuple5<A, B, C, D,
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
fun <I, A, B, C, D, E, Z> Kind<Kind<ForFunction1, I>, Tuple5<A, B, C, D,
    E>>.product(arg1: Kind<Kind<ForFunction1, I>, Z>): arrow.core.Function1<I, Tuple6<A, B, C, D, E,
    Z>> = arrow.core.Function1.apply<I>().run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.Function1<I, arrow.core.Tuple6<A, B, C,
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
fun <I, A, B, C, D, E, FF, Z> Kind<Kind<ForFunction1, I>, Tuple6<A, B, C, D, E,
    FF>>.product(arg1: Kind<Kind<ForFunction1, I>, Z>): arrow.core.Function1<I, Tuple7<A, B, C, D,
    E, FF, Z>> = arrow.core.Function1.apply<I>().run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.Function1<I, arrow.core.Tuple7<A,
    B, C, D, E, FF, Z>>
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
fun <I, A, B, C, D, E, FF, G, Z> Kind<Kind<ForFunction1, I>, Tuple7<A, B, C, D, E, FF,
    G>>.product(arg1: Kind<Kind<ForFunction1, I>, Z>): arrow.core.Function1<I, Tuple8<A, B, C, D, E,
    FF, G, Z>> = arrow.core.Function1.apply<I>().run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.Function1<I,
    arrow.core.Tuple8<A, B, C, D, E, FF, G, Z>>
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
fun <I, A, B, C, D, E, FF, G, H, Z> Kind<Kind<ForFunction1, I>, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(arg1: Kind<Kind<ForFunction1, I>, Z>): arrow.core.Function1<I, Tuple9<A, B, C, D, E,
    FF, G, H, Z>> = arrow.core.Function1.apply<I>().run {
  this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.Function1<I,
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
fun <I, A, B, C, D, E, FF, G, H, Z> Kind<Kind<ForFunction1, I>, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(arg1: Kind<Kind<ForFunction1, I>, Z>): arrow.core.Function1<I, Tuple10<A, B, C, D,
    E, FF, G, H, I, Z>> = arrow.core.Function1.apply<I>().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.core.Function1<I,
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
  "arrow.core.Function1.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B> tupled(arg0: Kind<Kind<ForFunction1, I>, A>, arg1: Kind<Kind<ForFunction1, I>, B>):
    arrow.core.Function1<I, Tuple2<A, B>> = arrow.core.Function1
   .apply<I>()
   .tupled<A, B>(arg0, arg1) as arrow.core.Function1<I, arrow.core.Tuple2<A, B>>

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
  "arrow.core.Function1.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B> tupledN(arg0: Kind<Kind<ForFunction1, I>, A>, arg1: Kind<Kind<ForFunction1, I>, B>):
    arrow.core.Function1<I, Tuple2<A, B>> = arrow.core.Function1
   .apply<I>()
   .tupledN<A, B>(arg0, arg1) as arrow.core.Function1<I, arrow.core.Tuple2<A, B>>

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
  "arrow.core.Function1.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C> tupled(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>
): arrow.core.Function1<I, Tuple3<A, B, C>> = arrow.core.Function1
   .apply<I>()
   .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.Function1<I, arrow.core.Tuple3<A, B, C>>

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
  "arrow.core.Function1.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C> tupledN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>
): arrow.core.Function1<I, Tuple3<A, B, C>> = arrow.core.Function1
   .apply<I>()
   .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.Function1<I, arrow.core.Tuple3<A, B, C>>

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
  "arrow.core.Function1.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D> tupled(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>
): arrow.core.Function1<I, Tuple4<A, B, C, D>> = arrow.core.Function1
   .apply<I>()
   .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Function1<I, arrow.core.Tuple4<A, B, C,
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
  "arrow.core.Function1.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D> tupledN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>
): arrow.core.Function1<I, Tuple4<A, B, C, D>> = arrow.core.Function1
   .apply<I>()
   .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Function1<I, arrow.core.Tuple4<A, B,
    C, D>>

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
  "arrow.core.Function1.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E> tupled(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>
): arrow.core.Function1<I, Tuple5<A, B, C, D, E>> = arrow.core.Function1
   .apply<I>()
   .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Function1<I,
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
  "arrow.core.Function1.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E> tupledN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>
): arrow.core.Function1<I, Tuple5<A, B, C, D, E>> = arrow.core.Function1
   .apply<I>()
   .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Function1<I,
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
  "arrow.core.Function1.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF> tupled(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>
): arrow.core.Function1<I, Tuple6<A, B, C, D, E, FF>> = arrow.core.Function1
   .apply<I>()
   .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Function1<I,
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
  "arrow.core.Function1.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF> tupledN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>
): arrow.core.Function1<I, Tuple6<A, B, C, D, E, FF>> = arrow.core.Function1
   .apply<I>()
   .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Function1<I,
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
  "arrow.core.Function1.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G> tupled(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>
): arrow.core.Function1<I, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Function1
   .apply<I>()
   .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.Function1<I, arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
  "arrow.core.Function1.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>
): arrow.core.Function1<I, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Function1
   .apply<I>()
   .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.Function1<I, arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
  "arrow.core.Function1.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>
): arrow.core.Function1<I, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Function1
   .apply<I>()
   .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Function1<I, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "arrow.core.Function1.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>
): arrow.core.Function1<I, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Function1
   .apply<I>()
   .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Function1<I, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "arrow.core.Function1.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Kind<Kind<ForFunction1, I>, I>
): arrow.core.Function1<I, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Function1
   .apply<I>()
   .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Function1<I, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "arrow.core.Function1.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Kind<Kind<ForFunction1, I>, I>
): arrow.core.Function1<I, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Function1
   .apply<I>()
   .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Function1<I, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "arrow.core.Function1.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H, J> tupled(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Kind<Kind<ForFunction1, I>, I>,
  arg9: Kind<Kind<ForFunction1, I>, J>
): arrow.core.Function1<I, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Function1
   .apply<I>()
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Function1<I,
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
  "arrow.core.Function1.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <I, A, B, C, D, E, FF, G, H, J> tupledN(
  arg0: Kind<Kind<ForFunction1, I>, A>,
  arg1: Kind<Kind<ForFunction1, I>, B>,
  arg2: Kind<Kind<ForFunction1, I>, C>,
  arg3: Kind<Kind<ForFunction1, I>, D>,
  arg4: Kind<Kind<ForFunction1, I>, E>,
  arg5: Kind<Kind<ForFunction1, I>, FF>,
  arg6: Kind<Kind<ForFunction1, I>, G>,
  arg7: Kind<Kind<ForFunction1, I>, H>,
  arg8: Kind<Kind<ForFunction1, I>, I>,
  arg9: Kind<Kind<ForFunction1, I>, J>
): arrow.core.Function1<I, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Function1
   .apply<I>()
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Function1<I,
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.followedBy(arg1: Kind<Kind<ForFunction1, I>, B>):
    arrow.core.Function1<I, B> = arrow.core.Function1.apply<I>().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Function1<I, B>
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
fun <I, A, B> Kind<Kind<ForFunction1, I>, A>.apTap(arg1: Kind<Kind<ForFunction1, I>, B>):
    arrow.core.Function1<I, A> = arrow.core.Function1.apply<I>().run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.Function1<I, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <I> Companion.apply(): Function1Apply<I> = apply_singleton as
    arrow.core.extensions.Function1Apply<I>
