package arrow.fx.extensions.schedule.applicative

import arrow.Kind
import arrow.fx.ForSchedule
import arrow.fx.IODeprecation
import arrow.fx.Schedule
import arrow.fx.Schedule.Companion
import arrow.fx.extensions.ScheduleApplicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Int
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A> A.just(MF: Monad<F>): Schedule<F, Input, A> = arrow.fx.Schedule.applicative<F,
  Input>(MF).run {
  this@just.just<A>() as arrow.fx.Schedule<F, Input, A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input> unit(MF: Monad<F>): Schedule<F, Input, Unit> = arrow.fx.Schedule
  .applicative<F, Input>(MF)
  .unit() as arrow.fx.Schedule<F, Input, kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A, B> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.map(
  MF: Monad<F>,
  arg1: Function1<A,
    B>
): Schedule<F, Input, B> = arrow.fx.Schedule.applicative<F, Input>(MF).run {
  this@map.map<A, B>(arg1) as arrow.fx.Schedule<F, Input, B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.replicate(MF: Monad<F>, arg1: Int):
  Schedule<F, Input, List<A>> = arrow.fx.Schedule.applicative<F, Input>(MF).run {
  this@replicate.replicate<A>(arg1) as arrow.fx.Schedule<F, Input, kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A> Kind<Kind<Kind<ForSchedule, F>, Input>, A>.replicate(
  MF: Monad<F>,
  arg1: Int,
  arg2: Monoid<A>
): Schedule<F, Input, A> = arrow.fx.Schedule.applicative<F, Input>(MF).run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.fx.Schedule<F, Input, A>
}

@JvmName("just")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A> just(MF: Monad<F>, a: A): Schedule<F, Input, A> = arrow.fx.Schedule
  .applicative<F, Input>(MF)
  .just<A>(a) as arrow.fx.Schedule<F, Input, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, Input> Companion.applicative(MF: Monad<F>): ScheduleApplicative<F, Input> = object :
  arrow.fx.extensions.ScheduleApplicative<F, Input> {
  override fun MF():
    arrow.typeclasses.Monad<F> = MF
}
