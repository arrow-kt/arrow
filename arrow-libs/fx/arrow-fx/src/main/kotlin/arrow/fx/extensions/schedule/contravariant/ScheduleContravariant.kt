package arrow.fx.extensions.schedule.contravariant

import arrow.Kind
import arrow.fx.ForSchedule
import arrow.fx.IODeprecation
import arrow.fx.Schedule.Companion
import arrow.fx.extensions.ScheduleContravariant
import arrow.typeclasses.Conested
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
internal val contravariant_singleton: ScheduleContravariant<Any?, Any?> = object :
    ScheduleContravariant<Any?, Any?> {}

@JvmName("contramap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Output, A, B> Kind<Conested<Kind<ForSchedule, F>, Output>, A>.contramap(
  arg1: Function1<B,
A>
): Kind<Conested<Kind<ForSchedule, F>, Output>, B> = arrow.fx.Schedule.contravariant<F,
    Output>().run {
  this@contramap.contramap<A, B>(arg1) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.Kind<arrow.fx.ForSchedule, F>, Output>, B>
}

@JvmName("lift1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Output, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Conested<Kind<ForSchedule, F>,
    Output>, B>, Kind<Conested<Kind<ForSchedule, F>, Output>, A>> = arrow.fx.Schedule
   .contravariant<F, Output>()
   .lift<A, B>(arg0) as
    kotlin.Function1<arrow.Kind<arrow.typeclasses.Conested<arrow.Kind<arrow.fx.ForSchedule, F>,
    Output>, B>, arrow.Kind<arrow.typeclasses.Conested<arrow.Kind<arrow.fx.ForSchedule, F>, Output>,
    A>>

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Output, A, B> Kind<Conested<Kind<ForSchedule, F>, Output>, A>.imap(
  arg1: Function1<A, B>,
  arg2: Function1<B, A>
): Kind<Conested<Kind<ForSchedule, F>, Output>, B> =
    arrow.fx.Schedule.contravariant<F, Output>().run {
  this@imap.imap<A, B>(arg1, arg2) as
    arrow.Kind<arrow.typeclasses.Conested<arrow.Kind<arrow.fx.ForSchedule, F>, Output>, B>
}

@JvmName("narrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun <F, Output, A, B : A> Kind<Conested<Kind<ForSchedule, F>, Output>, A>.narrow():
    Kind<Conested<Kind<ForSchedule, F>, Output>, B> = arrow.fx.Schedule.contravariant<F,
    Output>().run {
  this@narrow.narrow<A, B>() as
    arrow.Kind<arrow.typeclasses.Conested<arrow.Kind<arrow.fx.ForSchedule, F>, Output>, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun <F, Output> Companion.contravariant(): ScheduleContravariant<F, Output> =
    contravariant_singleton as arrow.fx.extensions.ScheduleContravariant<F, Output>
