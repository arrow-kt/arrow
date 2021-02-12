package arrow.fx.extensions.schedule.apply

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
import arrow.fx.ForSchedule
import arrow.fx.IODeprecation
import arrow.fx.Schedule
import arrow.fx.Schedule.Companion
import arrow.fx.extensions.ScheduleAppy
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
internal val apply_singleton: ScheduleAppy<Any?, Any?> = object : ScheduleAppy<Any?, Any?> {}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.ap(
  arg1: Kind<Kind<Kind<ForSchedule,
    F>, Input>, Function1<A, B>>
): Schedule<F, Input, B> = arrow.fx.Schedule.apply<F, Input>().run {
  this@ap.ap<A, B>(arg1) as arrow.fx.Schedule<F, Input, B>
}

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>,
  A>.apEval(arg1: Eval<Kind<Kind<Kind<ForSchedule, F>, Input>, Function1<A, B>>>):
  Eval<Kind<Kind<Kind<ForSchedule, F>, Input>, B>> = arrow.fx.Schedule.apply<F, Input>().run {
  this@apEval.apEval<A, B>(arg1) as
    arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.Kind<arrow.fx.ForSchedule, F>, Input>, B>>
}

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, Z> Kind<Kind<Kind<ForSchedule, F>, Input>,
  A>.map2Eval(
    arg1: Eval<Kind<Kind<Kind<ForSchedule, F>, Input>, B>>,
    arg2: Function1<Tuple2<A, B>, Z>
  ): Eval<Kind<Kind<Kind<ForSchedule, F>, Input>, Z>> = arrow.fx.Schedule.apply<F,
  Input>().run {
  this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
    arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.Kind<arrow.fx.ForSchedule, F>, Input>, Z>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, Z> map(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .map<A, B, Z>(arg0, arg1, arg2) as arrow.fx.Schedule<F, Input, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, Z> mapN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.fx.Schedule<F, Input, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, Z> map(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.fx.Schedule<F, Input, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, Z> mapN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.fx.Schedule<F, Input, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, Z> map(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.fx.Schedule<F, Input, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, Z> mapN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.fx.Schedule<F, Input, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, Z> map(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.fx.Schedule<F, Input, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, Z> mapN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.fx.Schedule<F, Input, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, Z> map(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.fx.Schedule<F,
  Input, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.fx.Schedule<F,
  Input, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.fx.Schedule<F, Input, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.fx.Schedule<F, Input, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.fx.Schedule<F, Input, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.fx.Schedule<F, Input, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Kind<Kind<Kind<ForSchedule, F>, Input>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.fx.Schedule<F, Input, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Kind<Kind<Kind<ForSchedule, F>, Input>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.fx.Schedule<F, Input, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Kind<Kind<Kind<ForSchedule, F>, Input>, I>,
  arg9: Kind<Kind<Kind<ForSchedule, F>, Input>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.fx.Schedule<F,
  Input, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Kind<Kind<Kind<ForSchedule, F>, Input>, I>,
  arg9: Kind<Kind<Kind<ForSchedule, F>, Input>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Schedule<F, Input, Z> = arrow.fx.Schedule
  .apply<F, Input>()
  .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.fx.Schedule<F,
  Input, Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, Z> Kind<Kind<Kind<ForSchedule, F>, Input>,
  A>.map2(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>, arg2: Function1<Tuple2<A, B>, Z>):
  Schedule<F, Input, Z> = arrow.fx.Schedule.apply<F, Input>().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.fx.Schedule<F, Input, Z>
}

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>,
  A>.product(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>): Schedule<F, Input, Tuple2<A, B>> =
  arrow.fx.Schedule.apply<F, Input>().run {
    this@product.product<A, B>(arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple2<A, B>>
  }

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, Z> Kind<Kind<Kind<ForSchedule, F>, Input>, Tuple2<A,
  B>>.product(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, Z>): Schedule<F, Input, Tuple3<A, B,
  Z>> = arrow.fx.Schedule.apply<F, Input>().run {
  this@product.product<A, B, Z>(arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple3<A, B, Z>>
}

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, Z> Kind<Kind<Kind<ForSchedule, F>, Input>, Tuple3<A, B,
  C>>.product(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, Z>): Schedule<F, Input, Tuple4<A, B,
  C, Z>> = arrow.fx.Schedule.apply<F, Input>().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple4<A, B, C,
    Z>>
}

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, Z> Kind<Kind<Kind<ForSchedule, F>, Input>, Tuple4<A, B, C,
  D>>.product(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, Z>): Schedule<F, Input, Tuple5<A, B,
  C, D, Z>> = arrow.fx.Schedule.apply<F, Input>().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple5<A, B,
    C, D, Z>>
}

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, Z> Kind<Kind<Kind<ForSchedule, F>, Input>, Tuple5<A, B, C, D,
  E>>.product(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, Z>): Schedule<F, Input, Tuple6<A, B,
  C, D, E, Z>> = arrow.fx.Schedule.apply<F, Input>().run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple6<A,
    B, C, D, E, Z>>
}

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, Z> Kind<Kind<Kind<ForSchedule, F>, Input>, Tuple6<A, B, C, D, E,
  FF>>.product(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, Z>): Schedule<F, Input, Tuple7<A, B,
  C, D, E, FF, Z>> = arrow.fx.Schedule.apply<F, Input>().run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.fx.Schedule<F, Input,
    arrow.core.Tuple7<A, B, C, D, E, FF, Z>>
}

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, Z> Kind<Kind<Kind<ForSchedule, F>, Input>, Tuple7<A, B, C, D,
  E, FF, G>>.product(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, Z>): Schedule<F, Input,
  Tuple8<A, B, C, D, E, FF, G, Z>> = arrow.fx.Schedule.apply<F, Input>().run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.fx.Schedule<F, Input,
    arrow.core.Tuple8<A, B, C, D, E, FF, G, Z>>
}

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, Z> Kind<Kind<Kind<ForSchedule, F>, Input>, Tuple8<A, B, C,
  D, E, FF, G, H>>.product(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, Z>): Schedule<F, Input,
  Tuple9<A, B, C, D, E, FF, G, H, Z>> = arrow.fx.Schedule.apply<F, Input>().run {
  this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.fx.Schedule<F, Input,
    arrow.core.Tuple9<A, B, C, D, E, FF, G, H, Z>>
}

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, I, Z> Kind<Kind<Kind<ForSchedule, F>, Input>, Tuple9<A, B,
  C, D, E, FF, G, H, I>>.product(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, Z>): Schedule<F,
  Input, Tuple10<A, B, C, D, E, FF, G, H, I, Z>> = arrow.fx.Schedule.apply<F, Input>().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.fx.Schedule<F, Input,
    arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
}

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> tupled(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>
): Schedule<F, Input, Tuple2<A, B>> =
  arrow.fx.Schedule
    .apply<F, Input>()
    .tupled<A, B>(arg0, arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple2<A, B>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> tupledN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>
): Schedule<F, Input, Tuple2<A, B>> =
  arrow.fx.Schedule
    .apply<F, Input>()
    .tupledN<A, B>(arg0, arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple2<A, B>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C> tupled(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>
): Schedule<F, Input, Tuple3<A, B, C>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupled<A, B, C>(arg0, arg1, arg2) as arrow.fx.Schedule<F, Input, arrow.core.Tuple3<A, B, C>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C> tupledN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>
): Schedule<F, Input, Tuple3<A, B, C>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.fx.Schedule<F, Input, arrow.core.Tuple3<A, B, C>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D> tupled(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>
): Schedule<F, Input, Tuple4<A, B, C, D>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.fx.Schedule<F, Input, arrow.core.Tuple4<A,
  B, C, D>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D> tupledN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>
): Schedule<F, Input, Tuple4<A, B, C, D>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.fx.Schedule<F, Input, arrow.core.Tuple4<A,
  B, C, D>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E> tupled(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>
): Schedule<F, Input, Tuple5<A, B, C, D, E>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.fx.Schedule<F, Input,
  arrow.core.Tuple5<A, B, C, D, E>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E> tupledN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>
): Schedule<F, Input, Tuple5<A, B, C, D, E>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.fx.Schedule<F, Input,
  arrow.core.Tuple5<A, B, C, D, E>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF> tupled(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>
): Schedule<F, Input, Tuple6<A, B, C, D, E, FF>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.fx.Schedule<F, Input,
  arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF> tupledN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>
): Schedule<F, Input, Tuple6<A, B, C, D, E, FF>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.fx.Schedule<F, Input,
  arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G> tupled(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>
): Schedule<F, Input, Tuple7<A, B, C, D, E, FF, G>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.fx.Schedule<F,
  Input, arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>
): Schedule<F, Input, Tuple7<A, B, C, D, E, FF, G>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.fx.Schedule<F,
  Input, arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>
): Schedule<F, Input, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.fx.Schedule<F, Input, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>
): Schedule<F, Input, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.fx.Schedule<F, Input, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Kind<Kind<Kind<ForSchedule, F>, Input>, I>
): Schedule<F, Input, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.fx.Schedule<F, Input, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Kind<Kind<Kind<ForSchedule, F>, Input>, I>
): Schedule<F, Input, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.fx.Schedule<F, Input, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Kind<Kind<Kind<ForSchedule, F>, Input>, I>,
  arg9: Kind<Kind<Kind<ForSchedule, F>, Input>, J>
): Schedule<F, Input, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.fx.Schedule<F, Input,
  arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<Kind<Kind<ForSchedule, F>, Input>, A>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>,
  arg2: Kind<Kind<Kind<ForSchedule, F>, Input>, C>,
  arg3: Kind<Kind<Kind<ForSchedule, F>, Input>, D>,
  arg4: Kind<Kind<Kind<ForSchedule, F>, Input>, E>,
  arg5: Kind<Kind<Kind<ForSchedule, F>, Input>, FF>,
  arg6: Kind<Kind<Kind<ForSchedule, F>, Input>, G>,
  arg7: Kind<Kind<Kind<ForSchedule, F>, Input>, H>,
  arg8: Kind<Kind<Kind<ForSchedule, F>, Input>, I>,
  arg9: Kind<Kind<Kind<ForSchedule, F>, Input>, J>
): Schedule<F, Input, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.fx.Schedule
  .apply<F, Input>()
  .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.fx.Schedule<F, Input,
  arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>,
  A>.followedBy(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>): Schedule<F, Input, B> =
  arrow.fx.Schedule.apply<F, Input>().run {
    this@followedBy.followedBy<A, B>(arg1) as arrow.fx.Schedule<F, Input, B>
  }

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>,
  A>.apTap(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>): Schedule<F, Input, A> =
  arrow.fx.Schedule.apply<F, Input>().run {
    this@apTap.apTap<A, B>(arg1) as arrow.fx.Schedule<F, Input, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, Input> Companion.apply(): ScheduleAppy<F, Input> = apply_singleton as
  arrow.fx.extensions.ScheduleAppy<F, Input>
