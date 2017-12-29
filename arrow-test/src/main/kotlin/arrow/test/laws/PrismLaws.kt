package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.core.*
import arrow.data.*
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.optics.Prism
import arrow.optics.modify

object PrismLaws {

    inline fun <reified A, reified B> laws(prism: Prism<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, EQOptionB: Eq<Option<B>>): List<Law> = listOf(
            Law("Prism law: partial round trip one way", { partialRoundTripOneWay(prism, aGen, EQA) }),
            Law("Prism law: round trip other way", { roundTripOtherWay(prism, bGen, EQB) }),
            Law("Prism law: modify identity", { modifyIdentity(prism, aGen, EQA) }),
            Law("Prism law: compose modify", { composeModify(prism, aGen, funcGen, EQA) }),
            Law("Prism law: consistent set modify", { consistentSetModify(prism, aGen, bGen, EQA) }),
            Law("Prism law: consistent modify with modifyF Id", { consistentModifyModifyFId(prism, aGen, funcGen, EQA) }),
            Law("Prism law: consistent get option modify id", { consistentGetOptionModifyId(prism, aGen, EQOptionB) })
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

    inline fun <reified A, reified B> consistentModifyModifyFId(prism: Prism<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, { a, f ->
                prism.modifyF(Id.applicative(), a, { Id.pure(f(it)) }).value().equalUnderTheLaw(prism.modify(a, f), EQA)
            })

    inline fun <reified A, reified B> consistentGetOptionModifyId(prism: Prism<A, B>, aGen: Gen<A>, EQOptionB: Eq<Option<B>>): Unit =
            forAll(aGen, { a ->
                prism.modifyF(Const.applicative(object : Monoid<Option<B>> {
                    override fun combine(a: Option<B>, b: Option<B>): Option<B> = a.orElse { b }

                    override fun empty(): Option<B> = None
                }), a, { Const(Some(it)) }).value().equalUnderTheLaw(prism.getOption(a), EQOptionB)
            })

}