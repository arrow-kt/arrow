package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.fx.Ref
import arrow.typeclasses.MonadError

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.MonadDefer)
 *
 * The context required to defer evaluating a safe computation.
 **/
interface MonadDefer<F, E> : MonadError<F, E>, Bracket<F, E> {

  fun <A> defer(fe: (Throwable) -> E, fa: () -> Kind<F, A>): Kind<F, A>

  fun <A> later(fe: (Throwable) -> E, f: () -> A): Kind<F, A> =
    defer(fe) {
      try {
        just(f())
      } catch (t: Throwable) {
        t.raiseNonFatal<A>(fe)
      }
    }

  fun <A> later(fa: Kind<F, A>): Kind<F, A> =
    defer({ throw it }) { fa }

  fun lazy(): Kind<F, Unit> =
    later({ throw it }, { })
}


fun <F, A> MonadDefer<F, Throwable>.later(f: () -> A): Kind<F, A> =
  later(::identity, f)

fun <F, A> MonadDefer<F, Throwable>.laterOrRaise(f: () -> Either<Throwable, A>): Kind<F, A> =
  defer(::identity) { f().fold({ raiseError<A>(it) }, { just(it) }) }

/**
 * Creates a [Ref] to purely manage mutable state, initialized by the function [f]
 */
fun <F, A> MonadDefer<F, Throwable>.ref(f: () -> A): Kind<F, Ref<F, A>> = Ref(this, f)

val throwPolicy: (Throwable) -> Nothing = { throw it }


fun <F, A> MonadDefer<F, Throwable>.defer(fa: () -> Kind<F, A>): Kind<F, A> =
  defer(::identity, fa)
