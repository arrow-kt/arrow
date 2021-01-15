package arrow.core.extensions.const.apply

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.Eval
import arrow.core.ForConst
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.ConstApply
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Suppress
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
  "ap(MA, arg1)",
  "arrow.core.ap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.ap(
  MA: Monoid<A>,
  arg1: Kind<Kind<ForConst, A>, Function1<A, B>>
): Const<A, B> = arrow.core.Const.apply<A>(MA).run {
  this@ap.ap<A, B>(arg1) as arrow.core.Const<A, B>
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
  "apEval(MA, arg1)",
  "arrow.core.apEval"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.apEval(
  MA: Monoid<A>,
  arg1: Eval<Kind<Kind<ForConst, A>, Function1<A, B>>>
): Eval<Kind<Kind<ForConst, A>, B>> = arrow.core.Const.apply<A>(MA).run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForConst, A>,
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
  "map2Eval(MA, arg1, arg2)",
  "arrow.core.map2Eval"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> Kind<Kind<ForConst, A>, A>.map2Eval(
  MA: Monoid<A>,
  arg1: Eval<Kind<Kind<ForConst, A>, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<Kind<ForConst, A>, Z>> = arrow.core.Const.apply<A>(MA).run {
  this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
    arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForConst, A>, Z>>
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
  "map(MA, arg0, arg1, arg2)",
  "arrow.core.Const.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> map(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.Const<A, Z>

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
  "mapN(MA, arg0, arg1, arg2)",
  "arrow.core.Const.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> mapN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.Const<A, Z>

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
  "map(MA, arg0, arg1, arg2, arg3)",
  "arrow.core.Const.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> map(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Const<A, Z>

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
  "mapN(MA, arg0, arg1, arg2, arg3)",
  "arrow.core.Const.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> mapN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Const<A, Z>

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
  "map(MA, arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.Const.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> map(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Const<A, Z>

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
  "mapN(MA, arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.Const.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> mapN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Const<A, Z>

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
  "map(MA, arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.Const.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> map(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Const<A, Z>

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
  "mapN(MA, arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.Const.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> mapN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Const<A, Z>

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
  "map(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.Const.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> map(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Const<A, Z>

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
  "mapN(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.Const.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> mapN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Const<A, Z>

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
  "map(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.Const.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> map(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Const<A, Z>

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
  "mapN(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.Const.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> mapN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Const<A, Z>

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
  "map(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.Const.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> map(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Const<A, Z>

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
  "mapN(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.Const.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Const<A, Z>

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
  "map(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.Const.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Kind<Kind<ForConst, A>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Const<A, Z>

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
  "mapN(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.Const.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Kind<Kind<ForConst, A>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Const<A, Z>

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
  "map(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.Const.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Kind<Kind<ForConst, A>, I>,
  arg9: Kind<Kind<ForConst, A>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Const<A, Z>

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
  "mapN(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.Const.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Kind<Kind<ForConst, A>, I>,
  arg9: Kind<Kind<ForConst, A>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Const<A, Z> = arrow.core.Const
   .apply<A>(MA)
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Const<A, Z>

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
  "map2(MA, arg1, arg2)",
  "arrow.core.map2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> Kind<Kind<ForConst, A>, A>.map2(
  MA: Monoid<A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Const<A, Z> = arrow.core.Const.apply<A>(MA).run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.Const<A, Z>
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
  "product(MA, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.product(MA: Monoid<A>, arg1: Kind<Kind<ForConst, A>, B>):
    Const<A, Tuple2<A, B>> = arrow.core.Const.apply<A>(MA).run {
  this@product.product<A, B>(arg1) as arrow.core.Const<A, arrow.core.Tuple2<A, B>>
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
  "product(MA, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> Kind<Kind<ForConst, A>, Tuple2<A, B>>.product(
  MA: Monoid<A>,
  arg1: Kind<Kind<ForConst, A>, Z>
): Const<A, Tuple3<A, B, Z>> = arrow.core.Const.apply<A>(MA).run {
  this@product.product<A, B, Z>(arg1) as arrow.core.Const<A, arrow.core.Tuple3<A, B, Z>>
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
  "product(MA, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> Kind<Kind<ForConst, A>, Tuple3<A, B, C>>.product(
  MA: Monoid<A>,
  arg1: Kind<Kind<ForConst, A>, Z>
): Const<A, Tuple4<A, B, C, Z>> =
    arrow.core.Const.apply<A>(MA).run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.Const<A, arrow.core.Tuple4<A, B, C, Z>>
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
  "product(MA, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> Kind<Kind<ForConst, A>, Tuple4<A, B, C, D>>.product(
  MA: Monoid<A>,
  arg1: Kind<Kind<ForConst, A>, Z>
): Const<A, Tuple5<A, B, C, D, Z>> =
    arrow.core.Const.apply<A>(MA).run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.Const<A, arrow.core.Tuple5<A, B, C, D, Z>>
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
  "product(MA, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> Kind<Kind<ForConst, A>, Tuple5<A, B, C, D, E>>.product(
  MA: Monoid<A>,
  arg1: Kind<Kind<ForConst, A>, Z>
): Const<A, Tuple6<A, B, C, D, E, Z>> =
    arrow.core.Const.apply<A>(MA).run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.Const<A, arrow.core.Tuple6<A, B, C, D,
    E, Z>>
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
  "product(MA, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> Kind<Kind<ForConst, A>, Tuple6<A, B, C, D, E, FF>>.product(
  MA: Monoid<A>,
  arg1: Kind<Kind<ForConst, A>, Z>
): Const<A, Tuple7<A, B, C, D, E, FF, Z>> =
    arrow.core.Const.apply<A>(MA).run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.Const<A, arrow.core.Tuple7<A, B, C,
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
  "product(MA, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> Kind<Kind<ForConst, A>, Tuple7<A, B, C, D, E, FF,
    G>>.product(MA: Monoid<A>, arg1: Kind<Kind<ForConst, A>, Z>): Const<A, Tuple8<A, B, C, D, E, FF,
    G, Z>> = arrow.core.Const.apply<A>(MA).run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.Const<A, arrow.core.Tuple8<A, B,
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
  "product(MA, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> Kind<Kind<ForConst, A>, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(MA: Monoid<A>, arg1: Kind<Kind<ForConst, A>, Z>): Const<A, Tuple9<A, B, C, D, E, FF,
    G, H, Z>> = arrow.core.Const.apply<A>(MA).run {
  this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.Const<A, arrow.core.Tuple9<A,
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
  "product(MA, arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> Kind<Kind<ForConst, A>, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(MA: Monoid<A>, arg1: Kind<Kind<ForConst, A>, Z>): Const<A, Tuple10<A, B, C, D, E,
    FF, G, H, I, Z>> = arrow.core.Const.apply<A>(MA).run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.core.Const<A,
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
  "tupled(MA, arg0, arg1)",
  "arrow.core.Const.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupled(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>
): Const<A, Tuple2<A, B>> = arrow.core.Const
   .apply<A>(MA)
   .tupled<A, B>(arg0, arg1) as arrow.core.Const<A, arrow.core.Tuple2<A, B>>

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
  "tupledN(MA, arg0, arg1)",
  "arrow.core.Const.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupledN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>
): Const<A, Tuple2<A, B>> = arrow.core.Const
   .apply<A>(MA)
   .tupledN<A, B>(arg0, arg1) as arrow.core.Const<A, arrow.core.Tuple2<A, B>>

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
  "tupled(MA, arg0, arg1, arg2)",
  "arrow.core.Const.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupled(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>
): Const<A, Tuple3<A, B, C>> = arrow.core.Const
   .apply<A>(MA)
   .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.Const<A, arrow.core.Tuple3<A, B, C>>

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
  "tupledN(MA, arg0, arg1, arg2)",
  "arrow.core.Const.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupledN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>
): Const<A, Tuple3<A, B, C>> = arrow.core.Const
   .apply<A>(MA)
   .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.Const<A, arrow.core.Tuple3<A, B, C>>

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
  "tupled(MA, arg0, arg1, arg2, arg3)",
  "arrow.core.Const.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupled(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>
): Const<A, Tuple4<A, B, C, D>> = arrow.core.Const
   .apply<A>(MA)
   .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Const<A, arrow.core.Tuple4<A, B, C, D>>

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
  "tupledN(MA, arg0, arg1, arg2, arg3)",
  "arrow.core.Const.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupledN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>
): Const<A, Tuple4<A, B, C, D>> = arrow.core.Const
   .apply<A>(MA)
   .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Const<A, arrow.core.Tuple4<A, B, C,
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
  "tupled(MA, arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.Const.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupled(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>
): Const<A, Tuple5<A, B, C, D, E>> = arrow.core.Const
   .apply<A>(MA)
   .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Const<A, arrow.core.Tuple5<A,
    B, C, D, E>>

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
  "tupledN(MA, arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.Const.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupledN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>
): Const<A, Tuple5<A, B, C, D, E>> = arrow.core.Const
   .apply<A>(MA)
   .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Const<A, arrow.core.Tuple5<A,
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
  "tupled(MA, arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.Const.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupled(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>
): Const<A, Tuple6<A, B, C, D, E, FF>> = arrow.core.Const
   .apply<A>(MA)
   .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Const<A,
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
  "tupledN(MA, arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.Const.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupledN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>
): Const<A, Tuple6<A, B, C, D, E, FF>> = arrow.core.Const
   .apply<A>(MA)
   .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Const<A,
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
  "tupled(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.Const.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupled(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>
): Const<A, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Const
   .apply<A>(MA)
   .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Const<A,
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
  "tupledN(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.Const.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupledN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>
): Const<A, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Const
   .apply<A>(MA)
   .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Const<A,
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
  "tupled(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.Const.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupled(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>
): Const<A, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Const
   .apply<A>(MA)
   .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Const<A, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "tupledN(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.Const.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupledN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>
): Const<A, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Const
   .apply<A>(MA)
   .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Const<A, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "tupled(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.Const.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupled(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Kind<Kind<ForConst, A>, I>
): Const<A, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Const
   .apply<A>(MA)
   .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Const<A, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "tupledN(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.Const.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Kind<Kind<ForConst, A>, I>
): Const<A, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Const
   .apply<A>(MA)
   .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Const<A, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "tupled(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.Const.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Kind<Kind<ForConst, A>, I>,
  arg9: Kind<Kind<ForConst, A>, J>
): Const<A, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Const
   .apply<A>(MA)
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Const<A,
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
  "tupledN(MA, arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.Const.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  MA: Monoid<A>,
  arg0: Kind<Kind<ForConst, A>, A>,
  arg1: Kind<Kind<ForConst, A>, B>,
  arg2: Kind<Kind<ForConst, A>, C>,
  arg3: Kind<Kind<ForConst, A>, D>,
  arg4: Kind<Kind<ForConst, A>, E>,
  arg5: Kind<Kind<ForConst, A>, FF>,
  arg6: Kind<Kind<ForConst, A>, G>,
  arg7: Kind<Kind<ForConst, A>, H>,
  arg8: Kind<Kind<ForConst, A>, I>,
  arg9: Kind<Kind<ForConst, A>, J>
): Const<A, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Const
   .apply<A>(MA)
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Const<A,
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
  "followedBy(MA, arg1)",
  "arrow.core.followedBy"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.followedBy(MA: Monoid<A>, arg1: Kind<Kind<ForConst, A>, B>):
    Const<A, B> = arrow.core.Const.apply<A>(MA).run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Const<A, B>
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
  "apTap(MA, arg1)",
  "arrow.core.apTap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<Kind<ForConst, A>, A>.apTap(MA: Monoid<A>, arg1: Kind<Kind<ForConst, A>, B>):
    Const<A, A> = arrow.core.Const.apply<A>(MA).run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.Const<A, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.apply(MA: Monoid<A>): ConstApply<A> = object :
    arrow.core.extensions.ConstApply<A> { override fun MA(): arrow.typeclasses.Monoid<A> = MA }
