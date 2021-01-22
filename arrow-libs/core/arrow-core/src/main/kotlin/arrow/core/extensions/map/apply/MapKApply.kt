package arrow.core.extensions.map.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForMapK
import arrow.core.MapK
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.ap as _ap
import arrow.core.extensions.MapKApply
import arrow.core.extensions.mapk.foldable.isEmpty
import arrow.core.fix
import arrow.core.k
import arrow.core.zip
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.JvmName

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
fun <K, A, B> Map<K, A>.ap(arg1: Map<K, Function1<A, B>>): Map<K, B> =
  _ap(arg1)

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
    "arg1.map { ff -> this.ap(ff) }",
    "arrow.core.ap"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.apEval(arg1: Eval<Kind<Kind<ForMapK, K>, Function1<A, B>>>): Eval<Kind<Kind<ForMapK, K>, B>> =
  arg1.map { ff -> this.ap(ff.fix()) }.map { it.k() }

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
    "if (arg1.value().isEmpty()) Eval.now(emptyMap<K, Z>()) else arg1.map { b -> MapK.mapN(this, b) { _, a, b -> arg2(Tuple2(a, b)) }) }",
    "arrow.core.MapK",
    "arrow.core.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> Map<K, A>.map2Eval(arg1: Eval<Kind<Kind<ForMapK, K>, B>>, arg2: Function1<Tuple2<A, B>, Z>): Eval<Kind<Kind<ForMapK, K>, Z>> =
  if (arg1.value().fix().isEmpty()) Eval.now(emptyMap<K, Z>().k()) else arg1.map { b -> MapK.mapN(this, b.fix()) { _, a, b -> arg2(Tuple2(a, b)) }.k() }

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
    "MapK.mapN(arg0, arg1) { _, a, b -> arg2(Tuple2(a, b)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> map(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Map<K, Z> =
  MapK.mapN(arg0, arg1) { _, a, b -> arg2(Tuple2(a, b)) }

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
    "MapK.mapN(arg0, arg1) { _, a, b -> arg2(Tuple2(a, b)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> mapN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .mapN<A, B, Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arg2) as kotlin.collections.Map<K,
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
    "MapK.mapN(arg0, arg1, arg2) { _, a, b, c -> arg3(Tuple3(a, b, c)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, Z> map(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .map<A, B, C, Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arg3) as
  kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2) { _, a, b, c -> arg3(Tuple3(a, b, c)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, Z> mapN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .mapN<A, B, C, Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arg3) as
  kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3) { _, a, b, c, d -> arg4(Tuple4(a, b, c, d)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple4"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, Z> map(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .map<A, B, C, D,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arg4)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3) { _, a, b, c, d -> arg4(Tuple4(a, b, c, d)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple4"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, Z> mapN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .mapN<A, B, C, D,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arg4)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4) { _, a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple5"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, Z> map(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .map<A, B, C, D, E,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arg5)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4) { _, a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple5"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, Z> mapN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .mapN<A, B, C, D, E,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arg5)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { _, a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple6"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, Z> map(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .map<A, B, C, D, E, FF,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arg6)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { _, a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple6"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, Z> mapN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .mapN<A, B, C, D, E, FF,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arg6)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { _, a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple7"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, Z> map(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .map<A, B, C, D, E, FF, G,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arg7)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { _, a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple7"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, Z> mapN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .mapN<A, B, C, D, E, FF, G,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arg7)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { _, a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple8"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, Z> map(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .map<A, B, C, D, E, FF, G, H,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arg8)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { _, a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple8"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .mapN<A, B, C, D, E, FF, G, H,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arg8)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { _, a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple9"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Map<K, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .map<A, B, C, D, E, FF, G, H, I,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arrow.core.MapK(arg8), arg9)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { _, a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple9"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Map<K, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .mapN<A, B, C, D, E, FF, G, H, I,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arrow.core.MapK(arg8), arg9)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { _, a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple10"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Map<K, I>,
  arg9: Map<K, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arrow.core.MapK(arg8), arrow.core.MapK(arg9), arg10)
  as kotlin.collections.Map<K, Z>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { _, a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j)) }",
    "arrow.core.MapK",
    "arrow.core.Tuple10"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Map<K, I>,
  arg9: Map<K, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Map<K, Z> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arrow.core.MapK(arg8), arrow.core.MapK(arg9), arg10)
  as kotlin.collections.Map<K, Z>

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
    "zip(arg1) { _, a, b -> arg2(Tuple2(a, b)) }",
    "arrow.core.zip",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> Map<K, A>.map2(arg1: Map<K, B>, arg2: Function1<Tuple2<A, B>, Z>): Map<K, Z> =
  zip(arg1) { _, a, b -> arg2(Tuple2(a, b)) }

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
    "zip(arg1) { _, a, b -> Tuple2(a, b) }",
    "arrow.core.zip",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.product(arg1: Map<K, B>): Map<K, Tuple2<A, B>> =
  zip(arg1) { _, a, b -> Tuple2(a, b) }

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
    "zip(arg1) { _, (a, b), z -> Tuple3(a, b, z) }",
    "arrow.core.zip",
    "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> Map<K, Tuple2<A, B>>.product(arg1: Map<K, Z>): Map<K, Tuple3<A, B, Z>> =
  arrow.core.extensions.map.apply.Map.apply<K>().run {
    arrow.core.MapK(this@product).product<A, B, Z>(arrow.core.MapK(arg1)) as kotlin.collections.Map<K,
      arrow.core.Tuple3<A, B, Z>>
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
    "zip(arg1) { _, (a, b, c), z -> Tuple4(a, b, c, z) }",
    "arrow.core.zip",
    "arrow.core.Tuple4"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, Z> Map<K, Tuple3<A, B, C>>.product(arg1: Map<K, Z>): Map<K, Tuple4<A, B, C, Z>> =
  arrow.core.extensions.map.apply.Map.apply<K>().run {
    arrow.core.MapK(this@product).product<A, B, C, Z>(arrow.core.MapK(arg1)) as
      kotlin.collections.Map<K, arrow.core.Tuple4<A, B, C, Z>>
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
    "zip(arg1) { _, (a, b, c, d), z -> Tuple5(a, b, c, d, z) }",
    "arrow.core.zip",
    "arrow.core.Tuple5"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, Z> Map<K, Tuple4<A, B, C, D>>.product(arg1: Map<K, Z>): Map<K, Tuple5<A, B, C,
  D, Z>> = arrow.core.extensions.map.apply.Map.apply<K>().run {
  arrow.core.MapK(this@product).product<A, B, C, D, Z>(arrow.core.MapK(arg1)) as
    kotlin.collections.Map<K, arrow.core.Tuple5<A, B, C, D, Z>>
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
    "zip(arg1) { _, (a, b, c, d, e), z -> Tuple6(a, b, c, d, e, z) }",
    "arrow.core.zip",
    "arrow.core.Tuple6"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, Z> Map<K, Tuple5<A, B, C, D, E>>.product(arg1: Map<K, Z>): Map<K, Tuple6<A,
  B, C, D, E, Z>> = arrow.core.extensions.map.apply.Map.apply<K>().run {
  arrow.core.MapK(this@product).product<A, B, C, D, E, Z>(arrow.core.MapK(arg1)) as
    kotlin.collections.Map<K, arrow.core.Tuple6<A, B, C, D, E, Z>>
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
    "zip(arg1) { _, (a, b, c, d, e, ff), z -> Tuple7(a, b, c, d, e, z, ff) }",
    "arrow.core.zip",
    "arrow.core.Tuple7"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, Z> Map<K, Tuple6<A, B, C, D, E, FF>>.product(arg1: Map<K, Z>): Map<K,
  Tuple7<A, B, C, D, E, FF, Z>> = arrow.core.extensions.map.apply.Map.apply<K>().run {
  arrow.core.MapK(this@product).product<A, B, C, D, E, FF, Z>(arrow.core.MapK(arg1)) as
    kotlin.collections.Map<K, arrow.core.Tuple7<A, B, C, D, E, FF, Z>>
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
    "zip(arg1) { _, (a, b, c, d, e, ff, g), z -> Tuple8(a, b, c, d, e, z, ff, g) }",
    "arrow.core.zip",
    "arrow.core.Tuple8"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, Z> Map<K, Tuple7<A, B, C, D, E, FF, G>>.product(arg1: Map<K, Z>):
  Map<K, Tuple8<A, B, C, D, E, FF, G, Z>> = arrow.core.extensions.map.apply.Map.apply<K>().run {
  arrow.core.MapK(this@product).product<A, B, C, D, E, FF, G, Z>(arrow.core.MapK(arg1)) as
    kotlin.collections.Map<K, arrow.core.Tuple8<A, B, C, D, E, FF, G, Z>>
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
    "zip(arg1) { _, (a, b, c, d, e, ff, g, h), z -> Tuple9(a, b, c, d, e, z, ff, g, h) }",
    "arrow.core.zip",
    "arrow.core.Tuple9"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, Z> Map<K, Tuple8<A, B, C, D, E, FF, G, H>>.product(
  arg1: Map<K,
Z>
): Map<K, Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  arrow.core.extensions.map.apply.Map.apply<K>().run {
    arrow.core.MapK(this@product).product<A, B, C, D, E, FF, G, H, Z>(arrow.core.MapK(arg1)) as
      kotlin.collections.Map<K, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, Z>>
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
    "zip(arg1) { _, (a, b, c, d, e, ff, g, h, i), z -> Tuple10(a, b, c, d, e, z, ff, g, h, i) }",
    "arrow.core.zip",
    "arrow.core.Tuple10"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, Z> Map<K, Tuple9<A, B, C, D, E, FF, G, H,
  I>>.product(arg1: Map<K, Z>): Map<K, Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
  arrow.core.extensions.map.apply.Map.apply<K>().run {
    arrow.core.MapK(this@product).product<A, B, C, D, E, FF, G, H, I, Z>(arrow.core.MapK(arg1)) as
      kotlin.collections.Map<K, arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
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
    "arg0.zip(arg1) { _, a, b -> Tuple2(a, b) }",
    "arrow.core.zip",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> tupled(arg0: Map<K, A>, arg1: Map<K, B>): Map<K, Tuple2<A, B>> =
  arg0.zip(arg1) { _, a, b -> Tuple2(a, b) }

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
    "arg0.zip(arg1) { _, a, b -> Tuple2(a, b) }",
    "arrow.core.zip",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> tupledN(arg0: Map<K, A>, arg1: Map<K, B>): Map<K, Tuple2<A, B>> =
  arrow.core.extensions.map.apply.Map
    .apply<K>()
    .tupledN<A, B>(arrow.core.MapK(arg0), arrow.core.MapK(arg1)) as kotlin.collections.Map<K,
    arrow.core.Tuple2<A, B>>

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
    "MapK.mapN(arg0, arg1, arg2) { _, a, b, c -> Tuple3(a, b, c) }",
    "arrow.core.Tuple3",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> tupled(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>
): Map<K, Tuple3<A, B, C>> =
  MapK.mapN(arg0, arg1, arg2) { _, a, b, c -> Tuple3(a, b, c) }

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
    "MapK.mapN(arg0, arg1, arg2) { _, a, b, c -> Tuple3(a, b, c) }",
    "arrow.core.Tuple3",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> tupledN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>
): Map<K, Tuple3<A, B, C>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupledN<A, B, C>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2)) as
  kotlin.collections.Map<K, arrow.core.Tuple3<A, B, C>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3) { _, a, b, c, d -> Tuple4(a, b, c, d) }",
    "arrow.core.Tuple4",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D> tupled(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>
): Map<K, Tuple4<A, B, C, D>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupled<A, B, C,
    D>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3))
  as kotlin.collections.Map<K, arrow.core.Tuple4<A, B, C, D>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3) { _, a, b, c, d -> Tuple4(a, b, c, d) }",
    "arrow.core.Tuple4",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D> tupledN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>
): Map<K, Tuple4<A, B, C, D>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupledN<A, B, C,
    D>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3))
  as kotlin.collections.Map<K, arrow.core.Tuple4<A, B, C, D>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4) { _, a, b, c, d, e -> Tuple5(a, b, c, d, e) }",
    "arrow.core.Tuple5",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E> tupled(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>
): Map<K, Tuple5<A, B, C, D, E>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupled<A, B, C, D,
    E>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4))
  as kotlin.collections.Map<K, arrow.core.Tuple5<A, B, C, D, E>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4) { _, a, b, c, d, e -> Tuple5(a, b, c, d, e) }",
    "arrow.core.Tuple5",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E> tupledN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>
): Map<K, Tuple5<A, B, C, D, E>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupledN<A, B, C, D,
    E>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4))
  as kotlin.collections.Map<K, arrow.core.Tuple5<A, B, C, D, E>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { _, a, b, c, d, e, ff -> Tuple6(a, b, c, d, e, ff) }",
    "arrow.core.Tuple6",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF> tupled(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>
): Map<K, Tuple6<A, B, C, D, E, FF>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupled<A, B, C, D, E,
    FF>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5))
  as kotlin.collections.Map<K, arrow.core.Tuple6<A, B, C, D, E, FF>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5) { _, a, b, c, d, e, ff -> Tuple6(a, b, c, d, e, ff) }",
    "arrow.core.Tuple6",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF> tupledN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>
): Map<K, Tuple6<A, B, C, D, E, FF>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupledN<A, B, C, D, E,
    FF>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5))
  as kotlin.collections.Map<K, arrow.core.Tuple6<A, B, C, D, E, FF>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { _, a, b, c, d, e, ff, g -> Tuple7(a, b, c, d, e, ff, g) }",
    "arrow.core.Tuple7",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G> tupled(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>
): Map<K, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupled<A, B, C, D, E, FF,
    G>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6))
  as kotlin.collections.Map<K, arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) { _, a, b, c, d, e, ff, g -> Tuple7(a, b, c, d, e, ff, g) }",
    "arrow.core.Tuple7",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G> tupledN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>
): Map<K, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupledN<A, B, C, D, E, FF,
    G>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6))
  as kotlin.collections.Map<K, arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { _, a, b, c, d, e, ff, g, h -> Tuple8(a, b, c, d, e, ff, g, h) }",
    "arrow.core.Tuple8",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H> tupled(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>
): Map<K, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupled<A, B, C, D, E, FF, G,
    H>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7))
  as kotlin.collections.Map<K, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { _, a, b, c, d, e, ff, g, h -> Tuple8(a, b, c, d, e, ff, g, h) }",
    "arrow.core.Tuple8",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H> tupledN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>
): Map<K, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupledN<A, B, C, D, E, FF, G,
    H>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7))
  as kotlin.collections.Map<K, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { _, a, b, c, d, e, ff, g, h, i -> Tuple9(a, b, c, d, e, ff, g, h, i) }",
    "arrow.core.Tuple9",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Map<K, I>
): Map<K, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupled<A, B, C, D, E, FF, G, H,
    I>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arrow.core.MapK(arg8))
  as kotlin.collections.Map<K, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { _, a, b, c, d, e, ff, g, h, i -> Tuple9(a, b, c, d, e, ff, g, h, i) }",
    "arrow.core.Tuple9",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Map<K, I>
): Map<K, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupledN<A, B, C, D, E, FF, G, H,
    I>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arrow.core.MapK(arg8))
  as kotlin.collections.Map<K, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { _, a, b, c, d, e, ff, g, h, i, j -> Tuple10(a, b, c, d, e, ff, g, h, i, j) }",
    "arrow.core.Tuple10",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Map<K, I>,
  arg9: Map<K, J>
): Map<K, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arrow.core.MapK(arg8), arrow.core.MapK(arg9))
  as kotlin.collections.Map<K, arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
    "MapK.mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { _, a, b, c, d, e, ff, g, h, i, j -> Tuple10(a, b, c, d, e, ff, g, h, i, j) }",
    "arrow.core.Tuple10",
    "arrow.core.MapK"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Map<K, C>,
  arg3: Map<K, D>,
  arg4: Map<K, E>,
  arg5: Map<K, FF>,
  arg6: Map<K, G>,
  arg7: Map<K, H>,
  arg8: Map<K, I>,
  arg9: Map<K, J>
): Map<K, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.extensions.map.apply.Map
  .apply<K>()
  .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arrow.core.MapK(arg2), arrow.core.MapK(arg3), arrow.core.MapK(arg4), arrow.core.MapK(arg5), arrow.core.MapK(arg6), arrow.core.MapK(arg7), arrow.core.MapK(arg8), arrow.core.MapK(arg9))
  as kotlin.collections.Map<K, arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
    "flatMap { arg1 }",
    "arrow.core.flatMap"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.followedBy(arg1: Map<K, B>): Map<K, B> =
  arrow.core.extensions.map.apply.Map.apply<K>().run {
    arrow.core.MapK(this@followedBy).followedBy<A, B>(arrow.core.MapK(arg1)) as
      kotlin.collections.Map<K, B>
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
    "MapK.mapN(this, arg1) { _, left, _ -> left }",
    "arrow.core.MapK",
    "arrow.core.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.apTap(arg1: Map<K, B>): Map<K, A> =
  MapK.mapN(this, arg1) { _, left, _ -> left }

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: MapKApply<Any?> = object : MapKApply<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Align typeclasses is deprecated. Use concrete methods on Map")
  inline fun <K> apply(): MapKApply<K> = apply_singleton as arrow.core.extensions.MapKApply<K>
}
