package arrow.effects.internal

import arrow.effects.IO
import arrow.effects.IOOf
import kotlin.coroutines.CoroutineContext

internal fun <A> IOForkedStart(fa: IOOf<A>, ctx: CoroutineContext): IO<A> =
  IO.ContinueOn(IO.unit, ctx).flatMap { fa }



