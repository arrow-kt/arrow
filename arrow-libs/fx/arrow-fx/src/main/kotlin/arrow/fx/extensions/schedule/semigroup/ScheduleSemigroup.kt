package arrow.fx.extensions.schedule.semigroup

import arrow.fx.IODeprecation
import arrow.fx.Schedule
import arrow.fx.Schedule.Companion
import arrow.fx.extensions.ScheduleSemigroup
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, Output> Schedule<F, Input, Output>.plus(
  OI: Semigroup<Output>,
  arg1: Schedule<F,
    Input, Output>
): Schedule<F, Input, Output> = arrow.fx.Schedule.semigroup<F, Input,
  Output>(OI).run {
  this@plus.plus(arg1) as arrow.fx.Schedule<F, Input, Output>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, Output> Schedule<F, Input, Output>.maybeCombine(
  OI: Semigroup<Output>,
  arg1: Schedule<F, Input, Output>
): Schedule<F, Input, Output> = arrow.fx.Schedule.semigroup<F,
  Input, Output>(OI).run {
  this@maybeCombine.maybeCombine(arg1) as arrow.fx.Schedule<F, Input, Output>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, Input, Output> Companion.semigroup(OI: Semigroup<Output>): ScheduleSemigroup<F,
  Input, Output> = object : arrow.fx.extensions.ScheduleSemigroup<F, Input, Output> {
  override fun
  OI(): arrow.typeclasses.Semigroup<Output> = OI
}
