package arrow.effects.typeclasses

import arrow.Kind
import arrow.effects.typeclasses.suspended.ConcurrentSyntax
import arrow.typeclasses.Continuation
import arrow.typeclasses.MonadContinuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension

typealias Fx<F> = ConcurrentCancellableContinuation<F, *>

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class ConcurrentCancellableContinuation<F, A>(val CF: Concurrent<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadDeferCancellableContinuation<F, A>(CF), Concurrent<F> by CF, ConcurrentSyntax<F> {
  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> =
    bindingCancellable { c() }.a
}

class SyncContinuation<A : Any> : Continuation<A> {

  lateinit var result: A

  override fun resume(value: A) {
    result = value
  }

  override fun resumeWithException(exception: Throwable) {
    throw exception
  }

  override val context: CoroutineContext = EmptyCoroutineContext
}

/*
Arrow Fx : Bringing Typed FP to the masses.

Arrow Fx is direct syntax for Effects and Typed Functional Programming in Kotlin.

In the most recent years patterns like Tagless Final have become popular in the typed FP community in languages
like Haskell, Scala, Kotlin and even Java.

Arrow Fx takes tagless final and in general effectful programming over `IO` style monads further by:
- Providing compile-time guarantees that declared suspended effects don't compile/run in the environment uncontrolled,
- Eliminating all the syntactic noise of parametric `F` algebras by providing direct syntax in the environment with effect control over continuations.
- Auto-binding kinded results in FP Type class combinators eliminating the need to unwrap via do notation or monadic comprehensions.

In this talk we will look at some of the Kotlin and Arrow features and how Kotlin, despite some of its type system limitations
can bring FP to the masses by enabling direct style for effectful programming.
*/
