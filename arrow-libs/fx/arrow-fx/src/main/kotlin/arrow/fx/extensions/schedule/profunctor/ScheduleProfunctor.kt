package arrow.fx.extensions.schedule.profunctor

import arrow.Kind
import arrow.fx.ForSchedule
import arrow.fx.IODeprecation
import arrow.fx.Schedule
import arrow.fx.Schedule.Companion
import arrow.fx.extensions.ScheduleProfunctor
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
internal val profunctor_singleton: ScheduleProfunctor<Any?> = object : ScheduleProfunctor<Any?> {}

@JvmName("dimap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, A, B, C, D> Kind<Kind<Kind<ForSchedule, F>, A>, B>.dimap(
  arg1: Function1<C, A>,
  arg2: Function1<B, D>
): Schedule<F, C, D> = arrow.fx.Schedule.profunctor<F>().run {
  this@dimap.dimap<A, B, C, D>(arg1, arg2) as arrow.fx.Schedule<F, C, D>
}

@JvmName("lmap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, A, B, C> Kind<Kind<Kind<ForSchedule, F>, A>, B>.lmap(arg1: Function1<C, A>): Schedule<F, C,
  B> = arrow.fx.Schedule.profunctor<F>().run {
  this@lmap.lmap<A, B, C>(arg1) as arrow.fx.Schedule<F, C, B>
}

@JvmName("rmap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, A, B, D> Kind<Kind<Kind<ForSchedule, F>, A>, B>.rmap(arg1: Function1<B, D>): Schedule<F, A,
  D> = arrow.fx.Schedule.profunctor<F>().run {
  this@rmap.rmap<A, B, D>(arg1) as arrow.fx.Schedule<F, A, D>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F> Companion.profunctor(): ScheduleProfunctor<F> = profunctor_singleton as
  arrow.fx.extensions.ScheduleProfunctor<F>
