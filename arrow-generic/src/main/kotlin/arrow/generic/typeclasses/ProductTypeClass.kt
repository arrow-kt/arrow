package arrow.generic.typeclasses

import arrow.*
import arrow.generic.*

@typeclass
@higherkind
interface ProductTypeClass<C> : TC, ProductTypeClassKind<C> {
    /**
     * Given a type class instance for `H`, and a type class instance for a
     * product, produce a type class instance for the product prepended with `H`.
     */
    fun <H, T : HList> product(ch: HK<C, H>, ct: HK<C, T>): HK<C, HCons<H, T>>

    /**
     * The empty product.
     */
    fun emptyProduct(): HK<C, HNil>

    /**
     * Given an isomorphism between `F` and `G`, and a type class instance for `G`,
     * produce a type class instance for `F`.
     */
    fun <F, G> project(instance: () -> HK<C, G>, to: (F) -> G, from: (G) -> F): HK<C, F>
}


@typeclass
@higherkind
interface LabelledProductTypeClass<C> : TC, LabelledProductTypeClassKind<C> {
    /**
     * Given a type class instance for `H`, and a type class instance for a
     * product, produce a type class instance for the product prepended with `H`.
     */
    fun <H, T : HList> product(name: String, ch: HK<C, H>, ct: HK<C, T>): HK<C, HCons<H, T>>

    /**
     * The empty product.
     */
    fun emptyProduct(): HK<C, HNil>

    /**
     * Given an isomorphism between `F` and `G`, and a type class instance for `G`,
     * produce a type class instance for `F`.
     */
    fun <F, G> project(instance: () -> HK<C, G>, to: (F) -> G, from: (G) -> F): HK<C, F>
}
