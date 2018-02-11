package arrow.typeclasses.continuations

import arrow.typeclasses.MonadError
import kotlin.coroutines.experimental.RestrictsSuspension

@RestrictsSuspension
interface BindingCatchContinuation<F, E, A> : MonadError<F, E>, BindingContinuation<F, A>
