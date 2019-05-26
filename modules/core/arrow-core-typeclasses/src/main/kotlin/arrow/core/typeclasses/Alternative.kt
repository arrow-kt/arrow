package arrow.core.typeclasses

/**
 * ank_macro_hierarchy(arrow.core.typeclasses.Alternative)
 */
interface Alternative<F> : Applicative<F>, MonoidK<F>
