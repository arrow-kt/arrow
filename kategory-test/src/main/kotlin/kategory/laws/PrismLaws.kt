package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.optics.Prism
import kategory.optics.modify
import kategory.optics.modifyF

object PrismLaws {

    inline fun <reified A, reified B, reified F> laws(prism: Prism<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, FA: Applicative<F>): List<Law> = listOf(
            Law("Prism law: partial round trip one way", { partialRoundTripOneWay(prism, aGen, EQA) }),
            Law("Prism law: round trip other way", { roundTripOtherWay(prism, bGen, EQB) }),
            Law("Prism law: modify identity", { modifyIdentity(prism, aGen, EQA) }),
            Law("Prism law: compose modify", { composeModify(prism, aGen, funcGen, EQA) }),
            Law("Prism law: consistent set modify", { consistentSetModify(prism, aGen, bGen, EQA) }),
            Law("Prism law: consistent get option modify id", { consistentGetOptionModifyId(prism, aGen, FA, EQB) })
    )

    inline fun <reified A, reified B> partialRoundTripOneWay(prism: Prism<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                prism.getOrModify(a).fold(::identity, prism::reverseGet).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> roundTripOtherWay(prism: Prism<A, B>, bGen: Gen<B>, EQB: Eq<B>): Unit =
            forAll(bGen, { b ->
                prism.getOption(prism.reverseGet(b)).exists {
                    it.equalUnderTheLaw(b, EQB)
                }
            })

    inline fun <reified A, reified B> modifyIdentity(prism: Prism<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                prism.modify(a, ::identity).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> composeModify(prism: Prism<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                prism.modify(prism.modify(a, f), g).equalUnderTheLaw(prism.modify(a, g compose f), EQA)
            })

    inline fun <reified A, reified B> consistentSetModify(prism: Prism<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                prism.set(a, b).equalUnderTheLaw(prism.modify(a) { b }, EQA)
            })

    inline fun <reified A, reified B, reified F> consistentGetOptionModifyId(prism: Prism<A, B>, aGen: Gen<A>, FA: Applicative<F>, EQB: Eq<B>): Unit =
            forAll(aGen, { a ->
                prism.modifyF(FA, a, { FA.pure(it) }).exists {
                    prism.getOption(it).exists { b ->
                        prism.getOption(a).exists { it.equalUnderTheLaw(b, EQB) }
                    }
                }
            })

}