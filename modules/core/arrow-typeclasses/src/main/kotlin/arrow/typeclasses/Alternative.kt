package arrow.typeclasses

/**
 * ank_macro_hierarchy(arrow.typeclasses.Alternative)
 */
interface Alternative<F> : Applicative<F>, MonoidK<F>
