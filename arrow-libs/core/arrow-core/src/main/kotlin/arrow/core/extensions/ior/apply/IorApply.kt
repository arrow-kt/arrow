package arrow.core.extensions.ior.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForIor
import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.IorApply
import arrow.typeclasses.Semigroup

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
  "this.ap(SL, arg1)",
  "arrow.core.ap"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.ap(
  SL: Semigroup<L>,
  arg1: Kind<Kind<ForIor, L>, Function1<A, B>>
): Ior<L, B> = arrow.core.Ior.apply<L>(SL).run {
  this@ap.ap<A, B>(arg1) as arrow.core.Ior<L, B>
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
  "this.apEval(SL, arg1)",
  "arrow.core.apEval"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.apEval(
  SL: Semigroup<L>,
  arg1: Eval<Kind<Kind<ForIor, L>, Function1<A, B>>>
): Eval<Kind<Kind<ForIor, L>, B>> = arrow.core.Ior.apply<L>(SL).run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForIor, L>, B>>
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
  "this.zipEval<L, A, B, Z>(SL, arg1) { a, b -> arg2(Tuple2(a, b)) }",
  "arrow.core.zipEval", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, Z> Kind<Kind<ForIor, L>, A>.map2Eval(
  SL: Semigroup<L>,
  arg1: Eval<Kind<Kind<ForIor, L>, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<Kind<ForIor, L>, Z>> = arrow.core.Ior.apply<L>(SL).run {
  this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
    arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForIor, L>, Z>>
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
  "Ior.mapN(SL, arg0, arg1) { a, b -> arg2(Tuple2(a, b))}",
  "arrow.core.Ior", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, Z> map(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1) { a, b -> arg2(Tuple2(a, b))}",
    "arrow.core.Ior", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, Z> mapN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2) { a, b, c -> arg3(Tuple3(a, b, c))}",
    "arrow.core.Ior", "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, Z> map(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2) { a, b, c -> arg3(Tuple3(a, b, c))}",
    "arrow.core.Ior", "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, Z> mapN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3) { a, b, c, d -> arg4(Tuple4(a, b, c, d))}",
    "arrow.core.Ior", "arrow.core.Tuple4"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, Z> map(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3) { a, b, c, d -> arg4(Tuple4(a, b, c, d))}",
    "arrow.core.Ior", "arrow.core.Tuple4"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, Z> mapN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e))}",
    "arrow.core.Ior", "arrow.core.Tuple5"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, Z> map(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> arg5(Tuple5(a, b, c, d, e))}",
    "arrow.core.Ior", "arrow.core.Tuple5"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, Z> mapN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f))}",
    "arrow.core.Ior", "arrow.core.Tuple6"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, Z> map(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, f -> arg6(Tuple6(a, b, c, d, e, f))}",
    "arrow.core.Ior", "arrow.core.Tuple6"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, Z> mapN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g))}",
    "arrow.core.Ior", "arrow.core.Tuple7"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, Z> map(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, f, g -> arg7(Tuple7(a, b, c, d, e, f, g))}",
    "arrow.core.Ior", "arrow.core.Tuple7"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, Z> mapN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h))}",
    "arrow.core.Ior", "arrow.core.Tuple8"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, Z> map(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, f, g, h -> arg8(Tuple8(a, b, c, d, e, f, g, h))}",
    "arrow.core.Ior", "arrow.core.Tuple8"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, Z> mapN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i))}",
    "arrow.core.Ior", "arrow.core.Tuple9"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, I, Z> map(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Kind<Kind<ForIor, L>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, f, g, h, i -> arg9(Tuple9(a, b, c, d, e, f, g, h, i))}",
    "arrow.core.Ior", "arrow.core.Tuple9"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, I, Z> mapN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Kind<Kind<ForIor, L>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j))}",
    "arrow.core.Ior", "arrow.core.Tuple10"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, I, J, Z> map(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Kind<Kind<ForIor, L>, I>,
  arg9: Kind<Kind<ForIor, L>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Ior<L, Z>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, f, g, h, i, j -> arg10(Tuple10(a, b, c, d, e, f, g, h, i, j))}",
    "arrow.core.Ior", "arrow.core.Tuple10"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Kind<Kind<ForIor, L>, I>,
  arg9: Kind<Kind<ForIor, L>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Ior<L, Z> = arrow.core.Ior
   .apply<L>(SL)
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Ior<L, Z>

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
    "this.zip(SL, arg1) { a, b -> arg2(Tuple2(a, b)) }",
    "arrow.core.Tuple2", "arrow.core.zip"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, Z> Kind<Kind<ForIor, L>, A>.map2(
  SL: Semigroup<L>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Ior<L, Z> = arrow.core.Ior.apply<L>(SL).run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.Ior<L, Z>
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
    "this.zip(SL, arg1)",
    "arrow.core.zip"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.product(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, B>):
    Ior<L, Tuple2<A, B>> = arrow.core.Ior.apply<L>(SL).run {
  this@product.product<A, B>(arg1) as arrow.core.Ior<L, arrow.core.Tuple2<A, B>>
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
    "Ior.mapN(SL, this, arg1) { (a, b), z -> Tuple3(a, b, z) }",
    "arrow.core.Ior", "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, Z> Kind<Kind<ForIor, L>, Tuple2<A, B>>.product(
  SL: Semigroup<L>,
  arg1: Kind<Kind<ForIor, L>, Z>
): Ior<L, Tuple3<A, B, Z>> = arrow.core.Ior.apply<L>(SL).run {
  this@product.product<A, B, Z>(arg1) as arrow.core.Ior<L, arrow.core.Tuple3<A, B, Z>>
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
    "Ior.mapN(SL, this, arg1) { (a, b, c), z -> Tuple4(a, b, c, z) }",
    "arrow.core.Ior", "arrow.core.Tuple4"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, Z> Kind<Kind<ForIor, L>, Tuple3<A, B, C>>.product(
  SL: Semigroup<L>,
  arg1: Kind<Kind<ForIor, L>, Z>
): Ior<L, Tuple4<A, B, C, Z>> = arrow.core.Ior.apply<L>(SL).run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.Ior<L, arrow.core.Tuple4<A, B, C, Z>>
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
    "Ior.mapN(SL, this, arg1) { (a, b, c, d), z -> Tuple5(a, b, c, d, z) }",
    "arrow.core.Ior", "arrow.core.Tuple5"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, Z> Kind<Kind<ForIor, L>, Tuple4<A, B, C, D>>.product(
  SL: Semigroup<L>,
  arg1: Kind<Kind<ForIor, L>, Z>
): Ior<L, Tuple5<A, B, C, D, Z>> =
    arrow.core.Ior.apply<L>(SL).run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.Ior<L, arrow.core.Tuple5<A, B, C, D, Z>>
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
    "Ior.mapN(SL, this, arg1) { (a, b, c, d, e), z -> Tuple6(a, b, c, d, e, z) }",
    "arrow.core.Ior", "arrow.core.Tuple6"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, Z> Kind<Kind<ForIor, L>, Tuple5<A, B, C, D, E>>.product(
  SL: Semigroup<L>,
  arg1: Kind<Kind<ForIor, L>, Z>
): Ior<L, Tuple6<A, B, C, D, E, Z>> =
    arrow.core.Ior.apply<L>(SL).run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.Ior<L, arrow.core.Tuple6<A, B, C, D, E,
    Z>>
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
    "Ior.mapN(SL, this, arg1) { (a, b, c, d, e, f), z -> Tuple7(a, b, c, d, e, f, z) }",
    "arrow.core.Ior", "arrow.core.Tuple7"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, Z> Kind<Kind<ForIor, L>, Tuple6<A, B, C, D, E,
    FF>>.product(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, Z>): Ior<L, Tuple7<A, B, C, D, E, FF,
    Z>> = arrow.core.Ior.apply<L>(SL).run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.Ior<L, arrow.core.Tuple7<A, B, C,
    D, E, FF, Z>>
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
    "Ior.mapN(SL, this, arg1) { (a, b, c, d, e, f, g), z -> Tuple8(a, b, c, d, e, f, g, z) }",
    "arrow.core.Ior", "arrow.core.Tuple8"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, Z> Kind<Kind<ForIor, L>, Tuple7<A, B, C, D, E, FF,
    G>>.product(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, Z>): Ior<L, Tuple8<A, B, C, D, E, FF,
    G, Z>> = arrow.core.Ior.apply<L>(SL).run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.Ior<L, arrow.core.Tuple8<A, B,
    C, D, E, FF, G, Z>>
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
    "Ior.mapN(SL, this, arg1) { (a, b, c, d, e, f, g, h), z -> Tuple9(a, b, c, d, e, f, g, h, z) }",
    "arrow.core.Ior", "arrow.core.Tuple9"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, Z> Kind<Kind<ForIor, L>, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, Z>): Ior<L, Tuple9<A, B, C, D, E, FF,
    G, H, Z>> = arrow.core.Ior.apply<L>(SL).run {
  this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.Ior<L, arrow.core.Tuple9<A,
    B, C, D, E, FF, G, H, Z>>
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
    "Ior.mapN(SL, this, arg1) { (a, b, c, d, e, f, g, h, i), z -> Tuple10(a, b, c, d, e, f, g, h, i, z) }",
    "arrow.core.Ior", "arrow.core.Tuple10"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, I, Z> Kind<Kind<ForIor, L>, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, Z>): Ior<L, Tuple10<A, B, C, D, E, FF,
    G, H, I, Z>> = arrow.core.Ior.apply<L>(SL).run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.core.Ior<L,
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
    "Ior.mapN(SL, arg0, arg1) { a, b -> Tuple2(a, b) }",
    "arrow.core.Ior", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> tupled(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>
): Ior<L, Tuple2<A, B>> = arrow.core.Ior
   .apply<L>(SL)
   .tupled<A, B>(arg0, arg1) as arrow.core.Ior<L, arrow.core.Tuple2<A, B>>

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
    "Ior.mapN(SL, arg0, arg1) { a, b -> Tuple2(a, b) }",
    "arrow.core.Ior", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> tupledN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>
): Ior<L, Tuple2<A, B>> = arrow.core.Ior
   .apply<L>(SL)
   .tupledN<A, B>(arg0, arg1) as arrow.core.Ior<L, arrow.core.Tuple2<A, B>>

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
    "Ior.mapN(SL, arg0, arg1, arg2) { a, b, c -> Tuple3(a, b, c) }",
    "arrow.core.Ior", "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C> tupled(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>
): Ior<L, Tuple3<A, B, C>> = arrow.core.Ior
   .apply<L>(SL)
   .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.Ior<L, arrow.core.Tuple3<A, B, C>>

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
    "Ior.mapN(SL, arg0, arg1, arg2) { a, b, c -> Tuple3(a, b, c) }",
    "arrow.core.Ior", "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C> tupledN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>
): Ior<L, Tuple3<A, B, C>> = arrow.core.Ior
   .apply<L>(SL)
   .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.Ior<L, arrow.core.Tuple3<A, B, C>>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3) { a, b, c, d -> Tuple4(a, b, c, d) }",
    "arrow.core.Ior", "arrow.core.Tuple4"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D> tupled(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>
): Ior<L, Tuple4<A, B, C, D>> = arrow.core.Ior
   .apply<L>(SL)
   .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Ior<L, arrow.core.Tuple4<A, B, C, D>>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3) { a, b, c, d -> Tuple4(a, b, c, d) }",
    "arrow.core.Ior", "arrow.core.Tuple4"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D> tupledN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>
): Ior<L, Tuple4<A, B, C, D>> = arrow.core.Ior
   .apply<L>(SL)
   .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Ior<L, arrow.core.Tuple4<A, B, C, D>>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> Tuple5(a, b, c, d, e) }",
    "arrow.core.Ior", "arrow.core.Tuple5"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E> tupled(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>
): Ior<L, Tuple5<A, B, C, D, E>> = arrow.core.Ior
   .apply<L>(SL)
   .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Ior<L, arrow.core.Tuple5<A, B,
    C, D, E>>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4) { a, b, c, d, e -> Tuple5(a, b, c, d, e) }",
    "arrow.core.Ior", "arrow.core.Tuple5"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E> tupledN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>
): Ior<L, Tuple5<A, B, C, D, E>> = arrow.core.Ior
   .apply<L>(SL)
   .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Ior<L, arrow.core.Tuple5<A,
    B, C, D, E>>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, f -> Tuple6(a, b, c, d, e, f) }",
    "arrow.core.Ior", "arrow.core.Tuple6"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF> tupled(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>
): Ior<L, Tuple6<A, B, C, D, E, FF>> = arrow.core.Ior
   .apply<L>(SL)
   .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Ior<L,
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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5) { a, b, c, d, e, f -> Tuple6(a, b, c, d, e, f) }",
    "arrow.core.Ior", "arrow.core.Tuple6"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF> tupledN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>
): Ior<L, Tuple6<A, B, C, D, E, FF>> = arrow.core.Ior
   .apply<L>(SL)
   .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Ior<L,
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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, f, g -> Tuple7(a, b, c, d, e, f, g) }",
    "arrow.core.Ior", "arrow.core.Tuple7"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G> tupled(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>
): Ior<L, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Ior
   .apply<L>(SL)
   .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Ior<L,
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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6) { a, b, c, d, e, f, g -> Tuple7(a, b, c, d, e, f, g) }",
    "arrow.core.Ior", "arrow.core.Tuple7"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G> tupledN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>
): Ior<L, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Ior
   .apply<L>(SL)
   .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Ior<L,
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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, f, g, h -> Tuple8(a, b, c, d, e, f, g, h) }",
    "arrow.core.Ior", "arrow.core.Tuple8"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H> tupled(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>
): Ior<L, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Ior
   .apply<L>(SL)
   .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Ior<L, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) { a, b, c, d, e, f, g, h -> Tuple8(a, b, c, d, e, f, g, h) }",
    "arrow.core.Ior", "arrow.core.Tuple8"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H> tupledN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>
): Ior<L, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Ior
   .apply<L>(SL)
   .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Ior<L, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, f, g, h, i -> Tuple9(a, b, c, d, e, f, g, h, i) }",
    "arrow.core.Ior", "arrow.core.Tuple9"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, I> tupled(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Kind<Kind<ForIor, L>, I>
): Ior<L, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Ior
   .apply<L>(SL)
   .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Ior<L, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) { a, b, c, d, e, f, g, h, i -> Tuple9(a, b, c, d, e, f, g, h, i) }",
    "arrow.core.Ior", "arrow.core.Tuple9"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, I> tupledN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Kind<Kind<ForIor, L>, I>
): Ior<L, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Ior
   .apply<L>(SL)
   .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Ior<L, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, f, g, h, i, j -> Tuple10(a, b, c, d, e, f, g, h, i, j) }",
    "arrow.core.Ior", "arrow.core.Tuple10"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, I, J> tupled(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Kind<Kind<ForIor, L>, I>,
  arg9: Kind<Kind<ForIor, L>, J>
): Ior<L, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Ior
   .apply<L>(SL)
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Ior<L,
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
    "Ior.mapN(SL, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) { a, b, c, d, e, f, g, h, i, j -> Tuple10(a, b, c, d, e, f, g, h, i, j) }",
    "arrow.core.Ior", "arrow.core.Tuple10"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B, C, D, E, FF, G, H, I, J> tupledN(
  SL: Semigroup<L>,
  arg0: Kind<Kind<ForIor, L>, A>,
  arg1: Kind<Kind<ForIor, L>, B>,
  arg2: Kind<Kind<ForIor, L>, C>,
  arg3: Kind<Kind<ForIor, L>, D>,
  arg4: Kind<Kind<ForIor, L>, E>,
  arg5: Kind<Kind<ForIor, L>, FF>,
  arg6: Kind<Kind<ForIor, L>, G>,
  arg7: Kind<Kind<ForIor, L>, H>,
  arg8: Kind<Kind<ForIor, L>, I>,
  arg9: Kind<Kind<ForIor, L>, J>
): Ior<L, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Ior
   .apply<L>(SL)
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Ior<L,
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
  "this.flatMap(SL) { arg1 }",
  "arrow.core.followedBy"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.followedBy(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, B>):
    Ior<L, B> = arrow.core.Ior.apply<L>(SL).run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Ior<L, B>
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
  "Ior.mapN(SL, this, arg1) { left, _ -> left }",
  "arrow.core.Ior"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.apTap(SL: Semigroup<L>, arg1: Kind<Kind<ForIor, L>, B>):
    Ior<L, A> = arrow.core.Ior.apply<L>(SL).run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.Ior<L, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Apply typeclass is deprecated. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
inline fun <L> Companion.apply(SL: Semigroup<L>): IorApply<L> = object :
    arrow.core.extensions.IorApply<L> { override fun SL(): arrow.typeclasses.Semigroup<L> = SL }
