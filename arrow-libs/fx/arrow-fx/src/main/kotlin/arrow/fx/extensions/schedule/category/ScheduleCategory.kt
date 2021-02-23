package arrow.fx.extensions.schedule.category

import arrow.Kind
import arrow.fx.ForSchedule
import arrow.fx.IODeprecation
import arrow.fx.Schedule
import arrow.fx.Schedule.Companion
import arrow.fx.extensions.ScheduleCategory
import arrow.typeclasses.Monad
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("compose")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, A, B, C> Kind<Kind<Kind<ForSchedule, F>, B>, C>.compose(
  MM: Monad<F>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, A>, B>
): Schedule<F, A, C> =
  arrow.fx.Schedule.category<F>(MM).run {
    this@compose.compose<A, B, C>(arg1) as arrow.fx.Schedule<F, A, C>
  }

@JvmName("andThen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, A, B, C> Kind<Kind<Kind<ForSchedule, F>, A>, B>.andThen(
  MM: Monad<F>,
  arg1: Kind<Kind<Kind<ForSchedule, F>, B>, C>
): Schedule<F, A, C> =
  arrow.fx.Schedule.category<F>(MM).run {
    this@andThen.andThen<A, B, C>(arg1) as arrow.fx.Schedule<F, A, C>
  }

@JvmName("id")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, A> id(MM: Monad<F>): Schedule<F, A, A> = arrow.fx.Schedule
  .category<F>(MM)
  .id<A>() as arrow.fx.Schedule<F, A, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F> Companion.category(MM: Monad<F>): ScheduleCategory<F> = object :
  arrow.fx.extensions.ScheduleCategory<F> { override fun MM(): arrow.typeclasses.Monad<F> = MM }
