package arrow.core.computations

import arrow.Kind
import arrow.core.EagerBind
import arrow.core.Invalid
import arrow.core.MonadContinuation
import arrow.core.ShortCircuit
import arrow.core.SuspendMonadContinuation
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.ValidatedOf
import arrow.core.ValidatedPartialOf
import arrow.core.fix
import arrow.core.identity
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

object validated {
  fun <E, A> eager(c: suspend EagerBind<ValidatedPartialOf<E>>.() -> A): Validated<E, A> {
    val continuation: ValidatedContinuation<E, A> = ValidatedContinuation()
    return continuation.startCoroutineUninterceptedAndReturn {
      Valid(c())
    } as Validated<E, A>
  }

  suspend operator fun <E, A> invoke(c: suspend BindSyntax<ValidatedPartialOf<E>>.() -> A): Validated<E, A> =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val continuation = ValidatedSContinuation(cont as Continuation<ValidatedOf<E, A>>)
      continuation.startCoroutineUninterceptedOrReturn {
        Valid(c())
      }
    }

  internal class ValidatedSContinuation<E, A>(
    parent: Continuation<ValidatedOf<E, A>>
  ) : SuspendMonadContinuation<ValidatedPartialOf<E>, A>(parent) {
    override suspend fun <A> Kind<ValidatedPartialOf<E>, A>.bind(): A =
      fix().fold({ e -> throw ShortCircuit(e) }, ::identity)

    override fun ShortCircuit.recover(): Kind<ValidatedPartialOf<E>, A> =
      Invalid(value as E)
  }

  internal class ValidatedContinuation<E, A> : MonadContinuation<ValidatedPartialOf<E>, A>() {
    override suspend fun <A> Kind<ValidatedPartialOf<E>, A>.bind(): A =
      fix().fold({ e -> throw ShortCircuit(e) }, ::identity)

    override fun ShortCircuit.recover(): Kind<ValidatedPartialOf<E>, A> =
      Invalid(value as E)
  }
}
