package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation

@Deprecated(KindDeprecation)
interface Invariant<F> {
  fun <A, B> Kind<F, A>.imap(f: (A) -> B, g: (B) -> A): Kind<F, B>
}
