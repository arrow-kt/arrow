package arrow.typeclasses

/**
 * ank_macro_hierarchy(arrow.typeclasses.Bimonad)
 */
interface Bimonad<F> : Monad<F>, Comonad<F>