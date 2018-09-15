package arrow.effects.typeclasses

import arrow.Kind
import arrow.typeclasses.MonadError

interface Bracket<F, E> : MonadError<F, E> {

  fun <F, A, B> Kind<F, A>.bracketCase(release: (A, ExitCase<E>) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B>

  fun <A, B> Kind<F, A>.bracket(release: (A) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B> =
    bracketCase({ a, _ -> release(a) }, use)

  fun <A> Kind<F, A>.uncancelable(): Kind<F, A> =
    bracket({ just<Unit>(Unit) }, { just(it) })

  fun <A> Kind<F, A>.guarantee(finalizer: Kind<F, Unit>): Kind<F, A> =
    bracket({ _ -> finalizer }, { _ -> this })

  fun <A> Kind<F, A>.guaranteeCase(finalizer: (ExitCase<E>) -> Kind<F, Unit>): Kind<F, A> =
    bracketCase({ _, e -> finalizer(e) }, { _ -> this })
}
