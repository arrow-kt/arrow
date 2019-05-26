package arrow.core.typeclasses

/**
 * ank_macro_hierarchy(arrow.core.typeclasses.Bimonad)
 */
interface Bimonad<F> : Monad<F>, Comonad<F>
