package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.NonFatal
import arrow.fx.Ref
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadFx
import kotlin.coroutines.startCoroutine

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.MonadDefer)
 *
 * The context required to defer evaluating a safe computation.
 **/
interface MonadDefer<F, E> : MonadError<F, E>, Bracket<F, E> {

  fun <A> defer(fa: () -> Kind<F, A>): Kind<F, A>

  fun <A> later(f: () -> A): Kind<F, A> =
    defer {
      try {
        just(f())
      } catch (t: Throwable) {
        t.raiseThrowableNonFatal<A>()
      }
    }

  fun <A> later(fa: Kind<F, A>): Kind<F, A> =
    defer { fa }

  fun lazy(): Kind<F, Unit> =
    later { }

  fun <A> Throwable.raiseThrowableNonFatal(): Kind<F, A> =
    @Suppress("UNCHECKED_CAST")
    if (NonFatal(this)) raiseError(this as E) else throw this

  fun <A> laterOrRaise(f: () -> Either<E, A>): Kind<F, A> =
    defer { f().fold(::raiseError, ::just) }

  /**
   * Creates a [Ref] to purely manage mutable state, initialized by the function [f]
   */
  fun <A> ref(f: () -> A): Kind<F, Ref<F, A>> = Ref(this, f)

  override val fx: MonadDeferFx<F, E>
    get() = object : MonadDeferFx<F, E> {
      override val ME: MonadDefer<F, E> = this@MonadDefer
    }
}

interface MonadDeferFx<F, E> : MonadFx<F> {
  val ME: MonadDefer<F, E>
  override val M: Monad<F> get() = ME
  fun <A> monadError(c: suspend MonadDeferSyntax<F, E>.() -> A): Kind<F, A> {
    val continuation = MonadDeferContinuation<F, A, E>(ME)
    val wrapReturn: suspend MonadDeferSyntax<F, E>.() -> Kind<F, A> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }
}
