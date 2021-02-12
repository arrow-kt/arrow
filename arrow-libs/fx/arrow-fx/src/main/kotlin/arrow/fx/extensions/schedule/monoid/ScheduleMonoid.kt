package arrow.fx.extensions.schedule.monoid

import arrow.fx.IODeprecation
import arrow.fx.Schedule
import arrow.fx.Schedule.Companion
import arrow.fx.extensions.ScheduleMonoid
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, Output> Collection<Schedule<F, Input, Output>>.combineAll(
  OI: Monoid<Output>,
  MF: Monad<F>
): Schedule<F, Input, Output> = arrow.fx.Schedule.monoid<F, Input,
    Output>(OI, MF).run {
  this@combineAll.combineAll() as arrow.fx.Schedule<F, Input, Output>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, Output> combineAll(
  OI: Monoid<Output>,
  MF: Monad<F>,
  arg0: List<Schedule<F, Input, Output>>
): Schedule<F, Input, Output> = arrow.fx.Schedule
   .monoid<F, Input, Output>(OI, MF)
   .combineAll(arg0) as arrow.fx.Schedule<F, Input, Output>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, Input, Output> Companion.monoid(OI: Monoid<Output>, MF: Monad<F>): ScheduleMonoid<F,
    Input, Output> = object : arrow.fx.extensions.ScheduleMonoid<F, Input, Output> { override fun
    OI(): arrow.typeclasses.Monoid<Output> = OI

  override fun MF(): arrow.typeclasses.Monad<F> = MF }
