package arrow.fx.extensions.schedule.functor

import arrow.Kind
import arrow.core.Tuple2
import arrow.fx.ForSchedule
import arrow.fx.IODeprecation
import arrow.fx.Schedule
import arrow.fx.Schedule.Companion
import arrow.fx.extensions.ScheduleFunctor
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: ScheduleFunctor<Any?, Any?> = object : ScheduleFunctor<Any?, Any?>
    {}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.map(arg1: Function1<A, B>):
    Schedule<F, Input, B> = arrow.fx.Schedule.functor<F, Input>().run {
  this@map.map<A, B>(arg1) as arrow.fx.Schedule<F, Input, B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.imap(
  arg1: Function1<A, B>,
  arg2: Function1<B, A>
): Schedule<F, Input, B> = arrow.fx.Schedule.functor<F, Input>().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.fx.Schedule<F, Input, B>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<Kind<ForSchedule, F>, Input>,
    A>, Kind<Kind<Kind<ForSchedule, F>, Input>, B>> = arrow.fx.Schedule
   .functor<F, Input>()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.Kind<arrow.fx.ForSchedule, F>,
    Input>, A>, arrow.Kind<arrow.Kind<arrow.Kind<arrow.fx.ForSchedule, F>, Input>, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.void(): Schedule<F, Input, Unit> =
    arrow.fx.Schedule.functor<F, Input>().run {
  this@void.void<A>() as arrow.fx.Schedule<F, Input, kotlin.Unit>
}

@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.fproduct(arg1: Function1<A, B>):
    Schedule<F, Input, Tuple2<A, B>> = arrow.fx.Schedule.functor<F, Input>().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple2<A, B>>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.mapConst(arg1: B): Schedule<F,
    Input, B> = arrow.fx.Schedule.functor<F, Input>().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.Schedule<F, Input, B>
}

@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> A.mapConst(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, B>): Schedule<F,
    Input, A> = arrow.fx.Schedule.functor<F, Input>().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.fx.Schedule<F, Input, A>
}

@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.tupleLeft(arg1: B): Schedule<F,
    Input, Tuple2<B, A>> = arrow.fx.Schedule.functor<F, Input>().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple2<B, A>>
}

@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.tupleRight(arg1: B): Schedule<F,
    Input, Tuple2<A, B>> = arrow.fx.Schedule.functor<F, Input>().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.fx.Schedule<F, Input, arrow.core.Tuple2<A, B>>
}

@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, B, A : B> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.widen(): Schedule<F, Input, B> =
    arrow.fx.Schedule.functor<F, Input>().run {
  this@widen.widen<B, A>() as arrow.fx.Schedule<F, Input, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, Input> Companion.functor(): ScheduleFunctor<F, Input> = functor_singleton as
    arrow.fx.extensions.ScheduleFunctor<F, Input>
