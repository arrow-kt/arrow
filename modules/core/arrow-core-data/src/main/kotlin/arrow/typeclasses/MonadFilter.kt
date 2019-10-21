package arrow.typeclasses

import arrow.Kind
import arrow.core.Option
import kotlin.coroutines.startCoroutine

/**
 * ank_macro_hierarchy(arrow.typeclasses.MonadFilter)
 */
interface MonadFilter<F> : Monad<F>, FunctorFilter<F> {

  override val fx: MonadFilterFx<F>
    get() = object : MonadFilterFx<F> {
      override val MF: MonadFilter<F> = this@MonadFilter
    }

  fun <A> empty(): Kind<F, A>

  override fun <A, B> Kind<F, A>.filterMap(f: (A) -> Option<B>): Kind<F, B> =
    this.flatMap { a -> f(a).fold({ empty<B>() }, { just(it) }) }

  @Deprecated(
    "`bindingFilter` is getting renamed to `fx` for consistency with the Arrow Fx system. Use the Fx extensions for comprehensions",
    ReplaceWith("fx.monadFilter(c)")
  )
  fun <B> bindingFilter(c: suspend MonadFilterSyntax<F>.() -> B): Kind<F, B> {
    val continuation = MonadFilterContinuation<F, B>(this)
    val wrapReturn: suspend MonadFilterSyntax<F>.() -> Kind<F, B> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }
}

interface MonadFilterFx<F> : MonadFx<F> {
  val MF: MonadFilter<F>
  override val M: Monad<F> get() = MF

  /**
   * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
   * A coroutine is initiated and inside [MonadContinuation] suspended yielding to [flatMap]. Once all the flatMap binds are completed
   * the underlying monad is returned from the act of executing the coroutine
   */
  fun <A> monadFilter(c: suspend MonadFilterSyntax<F>.() -> A): Kind<F, A> {
    val continuation = MonadFilterContinuation<F, A>(MF)
    val wrapReturn: suspend MonadFilterSyntax<F>.() -> Kind<F, A> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }
}
