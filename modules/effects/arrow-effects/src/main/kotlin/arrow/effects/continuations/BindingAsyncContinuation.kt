package arrow.effects.continuations

import arrow.effects.Async
import arrow.typeclasses.continuations.BindingCatchContinuation
import kotlin.coroutines.experimental.RestrictsSuspension

@RestrictsSuspension
interface BindingAsyncContinuation<F, E, A> : Async<F, E>, BindingCatchContinuation<F, E, A>
