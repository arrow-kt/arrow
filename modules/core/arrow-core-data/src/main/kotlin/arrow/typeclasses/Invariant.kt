package arrow.typeclasses

import arrow.Kind

/**
 * ank_macro_hierarchy(arrow.typeclasses.Invariant)
 */
interface Invariant<F> {
    fun <A, B> Kind<F, A>.imap(f: (A) -> B, g: (B) -> A): Kind<F, B>
}
