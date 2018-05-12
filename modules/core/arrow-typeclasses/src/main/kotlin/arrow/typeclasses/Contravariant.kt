package arrow.typeclasses

import arrow.Kind

interface Contravariant<F> : Invariant<F> {

  override fun <A, B> Kind<F, A>.imap(f: (A) -> B, fi: (B) -> A): Kind<F, B> = contramap(fi)

  fun <A, B> Kind<F, A>.contramap(f: (B) -> A): Kind<F, B>

  @Suppress("UNCHECKED_CAST")
  fun <A, B : A> Kind<F, A>.narrow(): Kind<F, B> = this as Kind<F, B>

  fun <A, B> liftContravariant(f: (A) -> B): (Kind<F, B>) -> Kind<F, A> = { /*fb: Kind<F, B> ->*/ it.contramap(f) }
}
