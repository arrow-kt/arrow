package arrow.core.extensions.tuple2.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForTuple2
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.Tuple2Apply
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
internal val apply_singleton: Tuple2Apply<Any?> = object : Tuple2Apply<Any?> {}

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
    "Tuple2(a, arg1.b(this.b))", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.ap(arg1: Kind<Kind<ForTuple2, F>, Function1<A, B>>):
  Tuple2<F, B> = arrow.core.Tuple2.apply<F>().run {
  this@ap.ap<A, B>(arg1) as arrow.core.Tuple2<F, B>
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
    "arg1.map { Tuple2(a, it.b(this.b)) }", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.apEval(
  arg1: Eval<Kind<Kind<ForTuple2, F>, Function1<A,
    B>>>
): Eval<Kind<Kind<ForTuple2, F>, B>> = arrow.core.Tuple2.apply<F>().run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForTuple2, F>,
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
    "arg1.map { Tuple2(this.a, arg2(Tuple2(this.b, it.b))) }", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, Z> Kind<Kind<ForTuple2, F>, A>.map2Eval(
  arg1: Eval<Kind<Kind<ForTuple2, F>, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<Kind<ForTuple2, F>, Z>> =
  arrow.core.Tuple2.apply<F>().run {
    this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
      arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForTuple2, F>, Z>>
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
    "Tuple2(arg0.a, arg2(Tuple2(arg0.b, arg1.b)))",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, Z> map(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg2(Tuple2(arg0.b, arg1.b)))",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, Z> mapN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg3(Tuple3(arg0.b, arg1.b, arg2.b)))",
    "arrow.core.Tuple3"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, Z> map(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg3(Tuple3(arg0.b, arg1.b, arg2.b)))",
    "arrow.core.Tuple3", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, Z> mapN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg4(Tuple4(arg0.b, arg1.b, arg2.b, arg3.b)))",
    "arrow.core.Tuple4", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, Z> map(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg4(Tuple4(arg0.b, arg1.b, arg2.b, arg3.b)))",
    "arrow.core.Tuple4", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, Z> mapN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg5(Tuple5(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b)))",
    "arrow.core.Tuple5", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, Z> map(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg5(Tuple5(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b)))",
    "arrow.core.Tuple5", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, Z> mapN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg6(Tuple6(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b)))",
    "arrow.core.Tuple6", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, Z> map(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg6(Tuple6(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b)))",
    "arrow.core.Tuple6", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg7(Tuple7(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b)))",
    "arrow.core.Tuple7", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg7(Tuple7(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b)))",
    "arrow.core.Tuple7", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg8(Tuple8(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b)))",
    "arrow.core.Tuple8", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg8(Tuple8(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b)))",
    "arrow.core.Tuple8", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg9(Tuple9(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b, arg8.b)))",
    "arrow.core.Tuple9", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Kind<Kind<ForTuple2, F>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg9(Tuple9(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b, arg8.b)))",
    "arrow.core.Tuple9", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Kind<Kind<ForTuple2, F>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg10(Tuple10(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b, arg8.b, arg9.b)))",
    "arrow.core.Tuple10", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Kind<Kind<ForTuple2, F>, I>,
  arg9: Kind<Kind<ForTuple2, F>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Tuple2<F, Z>

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
    "Tuple2(arg0.a, arg10(Tuple10(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b, arg8.b, arg9.b)))",
    "arrow.core.Tuple10", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Kind<Kind<ForTuple2, F>, I>,
  arg9: Kind<Kind<ForTuple2, F>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2
  .apply<F>()
  .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Tuple2<F, Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("Tuple2(this.a, arg2(Tuple2(this.b, arg1.b)))", "arrow.core.Tuple2"),
  DeprecationLevel.WARNING
)
fun <F, A, B, Z> Kind<Kind<ForTuple2, F>, A>.map2(
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Tuple2<F, Z> = arrow.core.Tuple2.apply<F>().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.Tuple2<F, Z>
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
    "Tuple2(this.a, Pair(this.b, arg1.b))", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.product(arg1: Kind<Kind<ForTuple2, F>, B>): Tuple2<F,
  Tuple2<A, B>> = arrow.core.Tuple2.apply<F>().run {
  this@product.product<A, B>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<A, B>>
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
    "Tuple2(this.a, Triple(this.b.a, this.b.b, arg1.b))", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, Z> Kind<Kind<ForTuple2, F>, Tuple2<A, B>>.product(arg1: Kind<Kind<ForTuple2, F>, Z>):
  Tuple2<F, Tuple3<A, B, Z>> = arrow.core.Tuple2.apply<F>().run {
  this@product.product<A, B, Z>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple3<A, B, Z>>
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
    "Tuple2(this.a, Tuple4(this.b.a, this.b.b, this.b.c, arg1.b))",
    "arrow.core.Tuple4", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, Z> Kind<Kind<ForTuple2, F>, Tuple3<A, B, C>>.product(
  arg1: Kind<Kind<ForTuple2, F>,
    Z>
): Tuple2<F, Tuple4<A, B, C, Z>> = arrow.core.Tuple2.apply<F>().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple4<A, B, C, Z>>
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
    "Tuple2(this.a, Tuple5(this.b.a, this.b.b, this.b.c, this.b.d, arg1.b))",
    "arrow.core.Tuple5", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, Z> Kind<Kind<ForTuple2, F>, Tuple4<A, B, C,
  D>>.product(arg1: Kind<Kind<ForTuple2, F>, Z>): Tuple2<F, Tuple5<A, B, C, D, Z>> =
  arrow.core.Tuple2.apply<F>().run {
    this@product.product<A, B, C, D, Z>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple5<A, B, C, D,
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
    "Tuple2(this.a, Tuple6(this.b.a, this.b.b, this.b.c, this.b.d, this.b.e, arg1.b))",
    "arrow.core.Tuple6", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, Z> Kind<Kind<ForTuple2, F>, Tuple5<A, B, C, D,
  E>>.product(arg1: Kind<Kind<ForTuple2, F>, Z>): Tuple2<F, Tuple6<A, B, C, D, E, Z>> =
  arrow.core.Tuple2.apply<F>().run {
    this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple6<A, B, C, D,
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
    "Tuple2(this.a, Tuple7(this.b.a, this.b.b, this.b.c, this.b.d, this.b.e, this.b.f, arg1.b))",
    "arrow.core.Tuple7", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, Z> Kind<Kind<ForTuple2, F>, Tuple6<A, B, C, D, E,
  FF>>.product(arg1: Kind<Kind<ForTuple2, F>, Z>): Tuple2<F, Tuple7<A, B, C, D, E, FF, Z>> =
  arrow.core.Tuple2.apply<F>().run {
    this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple7<A, B,
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
    "Tuple2(this.a, Tuple8(this.b.a, this.b.b, this.b.c, this.b.d, this.b.e, this.b.f, this.b.g, arg1.b))",
    "arrow.core.Tuple8", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, Z> Kind<Kind<ForTuple2, F>, Tuple7<A, B, C, D, E, FF,
  G>>.product(arg1: Kind<Kind<ForTuple2, F>, Z>): Tuple2<F, Tuple8<A, B, C, D, E, FF, G, Z>> =
  arrow.core.Tuple2.apply<F>().run {
    this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple8<A,
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
    "Tuple2(this.a, Tuple9(this.b.a, this.b.b, this.b.c, this.b.d, this.b.e, this.b.f, this.b.g, this.b.h, arg1.b))",
    "arrow.core.Tuple9", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, Z> Kind<Kind<ForTuple2, F>, Tuple8<A, B, C, D, E, FF, G,
  H>>.product(arg1: Kind<Kind<ForTuple2, F>, Z>): Tuple2<F, Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  arrow.core.Tuple2.apply<F>().run {
    this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.Tuple2<F,
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
    "Tuple2(this.a, Tuple10(this.b.a, this.b.b, this.b.c, this.b.d, this.b.e, this.b.f, this.b.g, this.b.h, this.b.i, arg1.b))",
    "arrow.core.Tuple10", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, I, Z> Kind<Kind<ForTuple2, F>, Tuple9<A, B, C, D, E, FF, G, H,
  I>>.product(arg1: Kind<Kind<ForTuple2, F>, Z>): Tuple2<F, Tuple10<A, B, C, D, E, FF, G, H, I,
  Z>> = arrow.core.Tuple2.apply<F>().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.core.Tuple2<F,
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
    "Tuple2(arg0.a, Tuple2(arg0.b, arg1.b))",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> tupled(arg0: Kind<Kind<ForTuple2, F>, A>, arg1: Kind<Kind<ForTuple2, F>, B>):
  Tuple2<F, Tuple2<A, B>> = arrow.core.Tuple2
  .apply<F>()
  .tupled<A, B>(arg0, arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<A, B>>

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
    "Tuple2(arg0.a, Tuple2(arg0.b, arg1.b))",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> tupledN(arg0: Kind<Kind<ForTuple2, F>, A>, arg1: Kind<Kind<ForTuple2, F>, B>):
  Tuple2<F, Tuple2<A, B>> = arrow.core.Tuple2
  .apply<F>()
  .tupledN<A, B>(arg0, arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<A, B>>

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
    "Tuple2(arg0.a, Tuple3(arg0.b, arg1.b, arg2.b))",
    "arrow.core.Tuple3", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C> tupled(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>
): Tuple2<F, Tuple3<A, B, C>> = arrow.core.Tuple2
  .apply<F>()
  .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.Tuple2<F, arrow.core.Tuple3<A, B, C>>

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
    "Tuple2(arg0.a, Tuple3(arg0.b, arg1.b, arg2.b))",
    "arrow.core.Tuple3", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C> tupledN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>
): Tuple2<F, Tuple3<A, B, C>> = arrow.core.Tuple2
  .apply<F>()
  .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.Tuple2<F, arrow.core.Tuple3<A, B, C>>

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
    "Tuple2(arg0.a, Tuple4(arg0.b, arg1.b, arg2.b, arg3.b))",
    "arrow.core.Tuple4", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D> tupled(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>
): Tuple2<F, Tuple4<A, B, C, D>> = arrow.core.Tuple2
  .apply<F>()
  .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Tuple2<F, arrow.core.Tuple4<A, B, C,
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
    "Tuple2(arg0.a, Tuple4(arg0.b, arg1.b, arg2.b, arg3.b))",
    "arrow.core.Tuple4", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D> tupledN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>
): Tuple2<F, Tuple4<A, B, C, D>> = arrow.core.Tuple2
  .apply<F>()
  .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Tuple2<F, arrow.core.Tuple4<A, B, C,
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
    "Tuple2(arg0.a, Tuple5(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b))",
    "arrow.core.Tuple5", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E> tupled(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>
): Tuple2<F, Tuple5<A, B, C, D, E>> = arrow.core.Tuple2
  .apply<F>()
  .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Tuple2<F, arrow.core.Tuple5<A,
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
    "Tuple2(arg0.a, Tuple5(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b))",
    "arrow.core.Tuple5", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E> tupledN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>
): Tuple2<F, Tuple5<A, B, C, D, E>> = arrow.core.Tuple2
  .apply<F>()
  .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Tuple2<F,
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
    "Tuple2(arg0.a, Tuple6(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b))",
    "arrow.core.Tuple6", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF> tupled(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>
): Tuple2<F, Tuple6<A, B, C, D, E, FF>> = arrow.core.Tuple2
  .apply<F>()
  .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Tuple2<F,
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
    "Tuple2(arg0.a, Tuple6(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b))",
    "arrow.core.Tuple6", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF> tupledN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>
): Tuple2<F, Tuple6<A, B, C, D, E, FF>> = arrow.core.Tuple2
  .apply<F>()
  .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Tuple2<F,
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
    "Tuple2(arg0.a, Tuple7(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b))",
    "arrow.core.Tuple7", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G> tupled(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>
): Tuple2<F, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Tuple2
  .apply<F>()
  .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Tuple2<F,
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
    "Tuple2(arg0.a, Tuple7(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b))",
    "arrow.core.Tuple7", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>
): Tuple2<F, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Tuple2
  .apply<F>()
  .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Tuple2<F,
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
    "Tuple2(arg0.a, Tuple8(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b))",
    "arrow.core.Tuple8", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>
): Tuple2<F, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Tuple2
  .apply<F>()
  .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.Tuple2<F, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
    "Tuple2(arg0.a, Tuple8(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b))",
    "arrow.core.Tuple8", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>
): Tuple2<F, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Tuple2
  .apply<F>()
  .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.Tuple2<F, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
    "Tuple2(arg0.a, Tuple9(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b, arg8.b))",
    "arrow.core.Tuple9", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Kind<Kind<ForTuple2, F>, I>
): Tuple2<F, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Tuple2
  .apply<F>()
  .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.Tuple2<F, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
    "Tuple2(arg0.a, Tuple9(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b, arg8.b))",
    "arrow.core.Tuple9", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Kind<Kind<ForTuple2, F>, I>
): Tuple2<F, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Tuple2
  .apply<F>()
  .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.Tuple2<F, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
    "Tuple2(arg0.a, Tuple10(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b, arg8.b, arg9.b))",
    "arrow.core.Tuple10", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Kind<Kind<ForTuple2, F>, I>,
  arg9: Kind<Kind<ForTuple2, F>, J>
): Tuple2<F, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Tuple2
  .apply<F>()
  .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Tuple2<F,
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
    "Tuple2(arg0.a, Tuple10(arg0.b, arg1.b, arg2.b, arg3.b, arg4.b, arg5.b, arg6.b, arg7.b, arg8.b, arg9.b))",
    "arrow.core.Tuple10", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<Kind<ForTuple2, F>, A>,
  arg1: Kind<Kind<ForTuple2, F>, B>,
  arg2: Kind<Kind<ForTuple2, F>, C>,
  arg3: Kind<Kind<ForTuple2, F>, D>,
  arg4: Kind<Kind<ForTuple2, F>, E>,
  arg5: Kind<Kind<ForTuple2, F>, FF>,
  arg6: Kind<Kind<ForTuple2, F>, G>,
  arg7: Kind<Kind<ForTuple2, F>, H>,
  arg8: Kind<Kind<ForTuple2, F>, I>,
  arg9: Kind<Kind<ForTuple2, F>, J>
): Tuple2<F, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Tuple2
  .apply<F>()
  .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Tuple2<F,
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
    "Tuple2(this.a, arg1.b)",
    "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.followedBy(arg1: Kind<Kind<ForTuple2, F>, B>): Tuple2<F,
  B> = arrow.core.Tuple2.apply<F>().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Tuple2<F, B>
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
  ReplaceWith("this"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.apTap(arg1: Kind<Kind<ForTuple2, F>, B>): Tuple2<F, A> =
  arrow.core.Tuple2.apply<F>().run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.Tuple2<F, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Apply typeclasses is deprecated. Use concrete methods on Pair")
inline fun <F> Companion.apply(): Tuple2Apply<F> = apply_singleton as
  arrow.core.extensions.Tuple2Apply<F>
