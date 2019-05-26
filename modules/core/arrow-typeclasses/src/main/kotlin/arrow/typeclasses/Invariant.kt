package arrow.core.typeclasses

import arrow.Kind

/**
 * ank_macro_hierarchy(arrow.core.typeclasses.Invariant)
 */
interface Invariant<F> {
    fun <A, B> Kind<F, A>.imap(f: (A) -> B, g: (B) -> A): Kind<F, B>
}
