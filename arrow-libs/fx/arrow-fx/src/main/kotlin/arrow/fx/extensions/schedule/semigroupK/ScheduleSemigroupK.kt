package arrow.fx.extensions.schedule.semigroupK

import arrow.Kind
import arrow.fx.ForSchedule
import arrow.fx.IODeprecation
import arrow.fx.Schedule
import arrow.fx.Schedule.Companion
import arrow.fx.extensions.ScheduleSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: ScheduleSemigroupK<Any?, Any?> = object :
    ScheduleSemigroupK<Any?, Any?> {}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A> Kind<Kind<Kind<ForSchedule, F>, Input>,
    A>.combineK(arg1: Kind<Kind<Kind<ForSchedule, F>, Input>, A>): Schedule<F, Input, A> =
    arrow.fx.Schedule.semigroupK<F, Input>().run {
  this@combineK.combineK<A>(arg1) as arrow.fx.Schedule<F, Input, A>
}

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Input, A> algebra(): Semigroup<Kind<Kind<Kind<ForSchedule, F>, Input>, A>> =
    arrow.fx.Schedule
   .semigroupK<F, Input>()
   .algebra<A>() as
    arrow.typeclasses.Semigroup<arrow.Kind<arrow.Kind<arrow.Kind<arrow.fx.ForSchedule, F>, Input>,
    A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, Input> Companion.semigroupK(): ScheduleSemigroupK<F, Input> = semigroupK_singleton as
    arrow.fx.extensions.ScheduleSemigroupK<F, Input>
