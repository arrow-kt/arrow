package arrow.core.extensions.id.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.IdApply
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: IdApply = object : arrow.core.extensions.IdApply {}

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
fun <A, B> Kind<ForId, A>.ap(arg1: Kind<ForId, Function1<A, B>>): Id<B> =
    arrow.core.Id.apply().run {
  this@ap.ap<A, B>(arg1) as arrow.core.Id<B>
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
fun <A, B> Kind<ForId, A>.apEval(arg1: Eval<Kind<ForId, Function1<A, B>>>): Eval<Kind<ForId, B>> =
    arrow.core.Id.apply().run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.core.ForId, B>>
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
fun <A, B, Z> Kind<ForId, A>.map2Eval(arg1: Eval<Kind<ForId, B>>, arg2: Function1<Tuple2<A, B>, Z>):
    Eval<Kind<ForId, Z>> = arrow.core.Id.apply().run {
  this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as arrow.core.Eval<arrow.Kind<arrow.core.ForId, Z>>
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
  "arrow.core.Id.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> map(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.Id<Z>

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
  "arrow.core.Id.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> mapN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.Id<Z>

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
  "arrow.core.Id.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> map(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Id<Z>

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
  "arrow.core.Id.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> mapN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Id<Z>

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
  "arrow.core.Id.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> map(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Id<Z>

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
  "arrow.core.Id.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> mapN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Id<Z>

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
  "arrow.core.Id.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> map(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Id<Z>

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
  "arrow.core.Id.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> mapN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Id<Z>

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
  "arrow.core.Id.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> map(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Id<Z>

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
  "arrow.core.Id.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Id<Z>

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
  "arrow.core.Id.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as arrow.core.Id<Z>

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
  "arrow.core.Id.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Id<Z>

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
  "arrow.core.Id.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Id<Z>

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
  "arrow.core.Id.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Id<Z>

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
  "arrow.core.Id.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Kind<ForId, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Id<Z>

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
  "arrow.core.Id.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Kind<ForId, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Id<Z>

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
  "arrow.core.Id.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Kind<ForId, I>,
  arg9: Kind<ForId, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Id<Z>

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
  "arrow.core.Id.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Kind<ForId, I>,
  arg9: Kind<ForId, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Id<Z> = arrow.core.Id
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Id<Z>

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
fun <A, B, Z> Kind<ForId, A>.map2(arg1: Kind<ForId, B>, arg2: Function1<Tuple2<A, B>, Z>): Id<Z> =
    arrow.core.Id.apply().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.Id<Z>
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
fun <A, B> Kind<ForId, A>.product(arg1: Kind<ForId, B>): Id<Tuple2<A, B>> =
    arrow.core.Id.apply().run {
  this@product.product<A, B>(arg1) as arrow.core.Id<arrow.core.Tuple2<A, B>>
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
fun <A, B, Z> Kind<ForId, Tuple2<A, B>>.product(arg1: Kind<ForId, Z>): Id<Tuple3<A, B, Z>> =
    arrow.core.Id.apply().run {
  this@product.product<A, B, Z>(arg1) as arrow.core.Id<arrow.core.Tuple3<A, B, Z>>
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
fun <A, B, C, Z> Kind<ForId, Tuple3<A, B, C>>.product(arg1: Kind<ForId, Z>): Id<Tuple4<A, B, C, Z>> =
  arrow.core.Id.apply().run {
    this@product.product<A, B, C, Z>(arg1) as arrow.core.Id<arrow.core.Tuple4<A, B, C, Z>>
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
fun <A, B, C, D, Z> Kind<ForId, Tuple4<A, B, C, D>>.product(arg1: Kind<ForId, Z>): Id<Tuple5<A, B,
    C, D, Z>> = arrow.core.Id.apply().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.Id<arrow.core.Tuple5<A, B, C, D, Z>>
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
fun <A, B, C, D, E, Z> Kind<ForId, Tuple5<A, B, C, D, E>>.product(arg1: Kind<ForId, Z>):
    Id<Tuple6<A, B, C, D, E, Z>> = arrow.core.Id.apply().run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.Id<arrow.core.Tuple6<A, B, C, D, E, Z>>
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
fun <A, B, C, D, E, FF, Z> Kind<ForId, Tuple6<A, B, C, D, E, FF>>.product(arg1: Kind<ForId, Z>):
    Id<Tuple7<A, B, C, D, E, FF, Z>> = arrow.core.Id.apply().run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.Id<arrow.core.Tuple7<A, B, C, D, E,
    FF, Z>>
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
fun <A, B, C, D, E, FF, G, Z> Kind<ForId, Tuple7<A, B, C, D, E, FF, G>>.product(
  arg1: Kind<ForId, Z>
): Id<Tuple8<A, B, C, D, E, FF, G, Z>> = arrow.core.Id.apply().run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.Id<arrow.core.Tuple8<A, B, C, D,
    E, FF, G, Z>>
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
fun <A, B, C, D, E, FF, G, H, Z> Kind<ForId, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(arg1: Kind<ForId, Z>): Id<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
    arrow.core.Id.apply().run {
  this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.Id<arrow.core.Tuple9<A, B, C,
    D, E, FF, G, H, Z>>
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
fun <A, B, C, D, E, FF, G, H, I, Z> Kind<ForId, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(arg1: Kind<ForId, Z>): Id<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
    arrow.core.Id.apply().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.core.Id<arrow.core.Tuple10<A,
    B, C, D, E, FF, G, H, I, Z>>
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
  "arrow.core.Id.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupled(arg0: Kind<ForId, A>, arg1: Kind<ForId, B>): Id<Tuple2<A, B>> = arrow.core.Id
   .apply()
   .tupled<A, B>(arg0, arg1) as arrow.core.Id<arrow.core.Tuple2<A, B>>

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
  "arrow.core.Id.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupledN(arg0: Kind<ForId, A>, arg1: Kind<ForId, B>): Id<Tuple2<A, B>> = arrow.core.Id
   .apply()
   .tupledN<A, B>(arg0, arg1) as arrow.core.Id<arrow.core.Tuple2<A, B>>

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
  "arrow.core.Id.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupled(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>
): Id<Tuple3<A, B, C>> = arrow.core.Id
   .apply()
   .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.Id<arrow.core.Tuple3<A, B, C>>

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
  "arrow.core.Id.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupledN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>
): Id<Tuple3<A, B, C>> = arrow.core.Id
   .apply()
   .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.Id<arrow.core.Tuple3<A, B, C>>

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
  "arrow.core.Id.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupled(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>
): Id<Tuple4<A, B, C, D>> = arrow.core.Id
   .apply()
   .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Id<arrow.core.Tuple4<A, B, C, D>>

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
  "arrow.core.Id.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupledN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>
): Id<Tuple4<A, B, C, D>> = arrow.core.Id
   .apply()
   .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Id<arrow.core.Tuple4<A, B, C, D>>

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
  "arrow.core.Id.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupled(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>
): Id<Tuple5<A, B, C, D, E>> = arrow.core.Id
   .apply()
   .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Id<arrow.core.Tuple5<A, B, C,
    D, E>>

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
  "arrow.core.Id.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupledN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>
): Id<Tuple5<A, B, C, D, E>> = arrow.core.Id
   .apply()
   .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Id<arrow.core.Tuple5<A, B, C,
    D, E>>

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
  "arrow.core.Id.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupled(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>
): Id<Tuple6<A, B, C, D, E, FF>> = arrow.core.Id
   .apply()
   .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.core.Id<arrow.core.Tuple6<A, B, C, D, E, FF>>

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
  "arrow.core.Id.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupledN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>
): Id<Tuple6<A, B, C, D, E, FF>> = arrow.core.Id
   .apply()
   .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.core.Id<arrow.core.Tuple6<A, B, C, D, E, FF>>

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
  "arrow.core.Id.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupled(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>
): Id<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Id
   .apply()
   .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.Id<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
  "arrow.core.Id.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>
): Id<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Id
   .apply()
   .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.Id<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
  "arrow.core.Id.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>
): Id<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Id
   .apply()
   .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Id<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "arrow.core.Id.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>
): Id<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Id
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Id<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "arrow.core.Id.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Kind<ForId, I>
): Id<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Id
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Id<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "arrow.core.Id.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Kind<ForId, I>
): Id<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Id
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Id<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "arrow.core.Id.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Kind<ForId, I>,
  arg9: Kind<ForId, J>
): Id<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Id
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.core.Id<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
  "arrow.core.Id.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<ForId, A>,
  arg1: Kind<ForId, B>,
  arg2: Kind<ForId, C>,
  arg3: Kind<ForId, D>,
  arg4: Kind<ForId, E>,
  arg5: Kind<ForId, FF>,
  arg6: Kind<ForId, G>,
  arg7: Kind<ForId, H>,
  arg8: Kind<ForId, I>,
  arg9: Kind<ForId, J>
): Id<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Id
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.core.Id<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
fun <A, B> Kind<ForId, A>.followedBy(arg1: Kind<ForId, B>): Id<B> = arrow.core.Id.apply().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Id<B>
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
fun <A, B> Kind<ForId, A>.apTap(arg1: Kind<ForId, B>): Id<A> = arrow.core.Id.apply().run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.Id<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.apply(): IdApply = apply_singleton
