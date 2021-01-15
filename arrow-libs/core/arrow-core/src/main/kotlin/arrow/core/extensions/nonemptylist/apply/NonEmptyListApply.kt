package arrow.core.extensions.nonemptylist.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.NonEmptyListApply
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: NonEmptyListApply = object : arrow.core.extensions.NonEmptyListApply
    {}

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
fun <A, B> Kind<ForNonEmptyList, A>.ap(arg1: Kind<ForNonEmptyList, Function1<A, B>>):
    NonEmptyList<B> = arrow.core.NonEmptyList.apply().run {
  this@ap.ap<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
fun <A, B> Kind<ForNonEmptyList, A>.apEval(arg1: Eval<Kind<ForNonEmptyList, Function1<A, B>>>):
    Eval<Kind<ForNonEmptyList, B>> = arrow.core.NonEmptyList.apply().run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.core.ForNonEmptyList, B>>
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
fun <A, B, Z> Kind<ForNonEmptyList, A>.map2Eval(
  arg1: Eval<Kind<ForNonEmptyList, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<ForNonEmptyList, Z>> =
  arrow.core.NonEmptyList.apply().run {
    this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
    arrow.core.Eval<arrow.Kind<arrow.core.ForNonEmptyList, Z>>
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
  "arrow.core.NonEmptyList.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Kind<ForNonEmptyList, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as
    arrow.core.NonEmptyList<Z>

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
  "arrow.core.NonEmptyList.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Kind<ForNonEmptyList, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as
    arrow.core.NonEmptyList<Z>

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
fun <A, B, Z> Kind<ForNonEmptyList, A>.map2(
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): NonEmptyList<Z> = arrow.core.NonEmptyList.apply().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.NonEmptyList<Z>
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
fun <A, B> Kind<ForNonEmptyList, A>.product(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<Tuple2<A,
    B>> = arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>
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
fun <A, B, Z> Kind<ForNonEmptyList, Tuple2<A, B>>.product(arg1: Kind<ForNonEmptyList, Z>):
    NonEmptyList<Tuple3<A, B, Z>> = arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple3<A, B, Z>>
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
fun <A, B, C, Z> Kind<ForNonEmptyList, Tuple3<A, B, C>>.product(arg1: Kind<ForNonEmptyList, Z>):
    NonEmptyList<Tuple4<A, B, C, Z>> = arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple4<A, B, C, Z>>
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
fun <A, B, C, D, Z> Kind<ForNonEmptyList, Tuple4<A, B, C, D>>.product(
  arg1: Kind<ForNonEmptyList, Z>
): NonEmptyList<Tuple5<A, B, C, D, Z>> = arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple5<A, B, C, D,
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
fun <A, B, C, D, E, Z> Kind<ForNonEmptyList, Tuple5<A, B, C, D,
    E>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple6<A, B, C, D, E, Z>> =
    arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple6<A, B, C,
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
fun <A, B, C, D, E, FF, Z> Kind<ForNonEmptyList, Tuple6<A, B, C, D, E,
    FF>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple7<A, B, C, D, E, FF, Z>> =
    arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.NonEmptyList<arrow.core.Tuple7<A,
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
fun <A, B, C, D, E, FF, G, Z> Kind<ForNonEmptyList, Tuple7<A, B, C, D, E, FF,
    G>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple8<A, B, C, D, E, FF, G, Z>> =
    arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as
    arrow.core.NonEmptyList<arrow.core.Tuple8<A, B, C, D, E, FF, G, Z>>
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
fun <A, B, C, D, E, FF, G, H, Z> Kind<ForNonEmptyList, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
    arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as
    arrow.core.NonEmptyList<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, Z>>
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
fun <A, B, C, D, E, FF, G, H, I, Z> Kind<ForNonEmptyList, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(arg1: Kind<ForNonEmptyList, Z>): NonEmptyList<Tuple10<A, B, C, D, E, FF, G, H, I,
    Z>> = arrow.core.NonEmptyList.apply().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as
    arrow.core.NonEmptyList<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
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
  "arrow.core.NonEmptyList.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupled(arg0: Kind<ForNonEmptyList, A>, arg1: Kind<ForNonEmptyList, B>):
    NonEmptyList<Tuple2<A, B>> = arrow.core.NonEmptyList
   .apply()
   .tupled<A, B>(arg0, arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>

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
  "arrow.core.NonEmptyList.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupledN(arg0: Kind<ForNonEmptyList, A>, arg1: Kind<ForNonEmptyList, B>):
    NonEmptyList<Tuple2<A, B>> = arrow.core.NonEmptyList
   .apply()
   .tupledN<A, B>(arg0, arg1) as arrow.core.NonEmptyList<arrow.core.Tuple2<A, B>>

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
  "arrow.core.NonEmptyList.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>
): NonEmptyList<Tuple3<A, B, C>> = arrow.core.NonEmptyList
   .apply()
   .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.NonEmptyList<arrow.core.Tuple3<A, B, C>>

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
  "arrow.core.NonEmptyList.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>
): NonEmptyList<Tuple3<A, B, C>> = arrow.core.NonEmptyList
   .apply()
   .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.NonEmptyList<arrow.core.Tuple3<A, B, C>>

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
  "arrow.core.NonEmptyList.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>
): NonEmptyList<Tuple4<A, B, C, D>> = arrow.core.NonEmptyList
   .apply()
   .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.NonEmptyList<arrow.core.Tuple4<A, B, C,
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
  "arrow.core.NonEmptyList.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>
): NonEmptyList<Tuple4<A, B, C, D>> = arrow.core.NonEmptyList
   .apply()
   .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.NonEmptyList<arrow.core.Tuple4<A, B,
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
  "arrow.core.NonEmptyList.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>
): NonEmptyList<Tuple5<A, B, C, D, E>> = arrow.core.NonEmptyList
   .apply()
   .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as
    arrow.core.NonEmptyList<arrow.core.Tuple5<A, B, C, D, E>>

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
  "arrow.core.NonEmptyList.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>
): NonEmptyList<Tuple5<A, B, C, D, E>> = arrow.core.NonEmptyList
   .apply()
   .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as
    arrow.core.NonEmptyList<arrow.core.Tuple5<A, B, C, D, E>>

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
  "arrow.core.NonEmptyList.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>
): NonEmptyList<Tuple6<A, B, C, D, E, FF>> = arrow.core.NonEmptyList
   .apply()
   .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.core.NonEmptyList<arrow.core.Tuple6<A, B, C, D, E, FF>>

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
  "arrow.core.NonEmptyList.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>
): NonEmptyList<Tuple6<A, B, C, D, E, FF>> = arrow.core.NonEmptyList
   .apply()
   .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.core.NonEmptyList<arrow.core.Tuple6<A, B, C, D, E, FF>>

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
  "arrow.core.NonEmptyList.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>
): NonEmptyList<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.NonEmptyList
   .apply()
   .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.NonEmptyList<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
  "arrow.core.NonEmptyList.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>
): NonEmptyList<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.NonEmptyList
   .apply()
   .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.NonEmptyList<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
  "arrow.core.NonEmptyList.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>
): NonEmptyList<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.NonEmptyList
   .apply()
   .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.NonEmptyList<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "arrow.core.NonEmptyList.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>
): NonEmptyList<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.NonEmptyList
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.NonEmptyList<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "arrow.core.NonEmptyList.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>
): NonEmptyList<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.NonEmptyList
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.NonEmptyList<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "arrow.core.NonEmptyList.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>
): NonEmptyList<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.NonEmptyList
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.NonEmptyList<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "arrow.core.NonEmptyList.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Kind<ForNonEmptyList, J>
): NonEmptyList<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.NonEmptyList
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.core.NonEmptyList<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
  "arrow.core.NonEmptyList.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<ForNonEmptyList, A>,
  arg1: Kind<ForNonEmptyList, B>,
  arg2: Kind<ForNonEmptyList, C>,
  arg3: Kind<ForNonEmptyList, D>,
  arg4: Kind<ForNonEmptyList, E>,
  arg5: Kind<ForNonEmptyList, FF>,
  arg6: Kind<ForNonEmptyList, G>,
  arg7: Kind<ForNonEmptyList, H>,
  arg8: Kind<ForNonEmptyList, I>,
  arg9: Kind<ForNonEmptyList, J>
): NonEmptyList<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.NonEmptyList
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.core.NonEmptyList<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
fun <A, B> Kind<ForNonEmptyList, A>.followedBy(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<B> =
    arrow.core.NonEmptyList.apply().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.NonEmptyList<B>
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
fun <A, B> Kind<ForNonEmptyList, A>.apTap(arg1: Kind<ForNonEmptyList, B>): NonEmptyList<A> =
    arrow.core.NonEmptyList.apply().run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.NonEmptyList<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.apply(): NonEmptyListApply = apply_singleton
