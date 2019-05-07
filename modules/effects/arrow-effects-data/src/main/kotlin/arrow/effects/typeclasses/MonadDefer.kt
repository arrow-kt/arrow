package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.typeclasses.MonadThrow

/**
 * ank_macro_hierarchy(arrow.effects.typeclasses.MonadDefer)
 *
 * The context required to defer evaluating a safe computation.
 **/
interface MonadDefer<F> : MonadThrow<F>, Bracket<F, Throwable> {

  fun <A> defer(fa: () -> Kind<F, A>): Kind<F, A>

  fun <A> delay(f: () -> A): Kind<F, A> =
    defer {
      try {
        just(f())
      } catch (t: Throwable) {
        t.raiseNonFatal<A>()
      }
    }

  fun <A> delay(fa: Kind<F, A>): Kind<F, A> = defer { fa }

  @Deprecated("Use delay instead",
    ReplaceWith("delay(f)", "arrow.effects.typeclasses.MonadDefer"))
  operator fun <A> invoke(f: () -> A): Kind<F, A> =
    defer {
      try {
        just(f())
      } catch (t: Throwable) {
        t.raiseNonFatal<A>()
      }
    }

  fun lazy(): Kind<F, Unit> = delay { }

  fun <A> delayOrRaise(f: () -> Either<Throwable, A>): Kind<F, A> =
    defer { f().fold({ raiseError<A>(it) }, { just(it) }) }

  @Deprecated("Use delayOrRaise instead",
          ReplaceWith("delayOrRaise(f)", "arrow.effects.typeclasses.MonadDefer"))
  fun <A> deferUnsafe(f: () -> Either<Throwable, A>): Kind<F, A> = delayOrRaise(f)

}
