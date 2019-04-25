package arrow.generic

import arrow.Kind

interface ProductTypeClass<C> {
  /**
   * Given a type class instance for `H`, and a type class instance for a
   * product, produce a type class instance for the product prepended with `H`.
   */
  fun <H, T : HList> product(ch: Kind<C, H>, ct: Kind<C, T>): Kind<C, HCons<H, T>>

  /**
   * The empty product.
   */
  fun emptyProduct(): Kind<C, HNil>

  /**
   * Given an isomorphism between `F` and `G`, and a type class instance for `G`,
   * produce a type class instance for `F`.
   */
  fun <F, G> project(instance: () -> Kind<C, G>, to: (F) -> G, from: (G) -> F): Kind<C, F>
}

interface ProductTypeClassCompanion<C> {

  val typeClass: ProductTypeClass<C>

  fun deriveHNil(): Kind<C, HNil> =
    typeClass.emptyProduct()

  fun <H, T : HList> deriveHCons(ch: Kind<C, H>, ct: Kind<C, T>): Kind<C, HCons<H, T>> =
    typeClass.product(ch, ct)

  fun <F, G> deriveInstance(gen: Generic<F, G>, cg: Kind<C, G>): Kind<C, F> =
    typeClass.project({ cg }, gen::to, gen::from)
}

/**
 * A type class abstracting over the `product` operation of type classes over
 * types of kind `*`, as well as deriving instances using an isomorphism.
 * Refines ProductTypeClass with the addition of runtime `String` labels
 * corresponding to the names of the product elements.
 */
interface LabeledProductTypeClass<C> {
  /**
   * Given a type class instance for `H`, and a type class instance for a
   * product, produce a type class instance for the product prepended with `H`.
   */
  fun <H, T : HList> product(name: String, ch: Kind<C, H>, ct: Kind<C, T>): Kind<C, HCons<H, T>>

  /**
   * The empty product.
   */
  fun emptyProduct(): Kind<C, HNil>

  /**
   * Given an isomorphism between `F` and `G`, and a type class instance for `G`,
   * produce a type class instance for `F`.
   */
  fun <F, G> project(instance: () -> Kind<C, G>, to: (F) -> G, from: (G) -> F): Kind<C, F>
}
