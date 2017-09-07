package kategory.optics

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Applicative
import kategory.Eq
import kategory.Law
import kategory.compose
import kategory.exists
import kategory.identity

object PrismLaws {

    inline fun <A, B, reified F> laws(prism: Prism<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, FA: Applicative<F>): List<Law> = listOf(
            Law("Prism law: partial round trip one way", { partialRoundTripOneWay(prism, aGen, EQA) }),
            Law("Prism law: round trip other way", { roundTripOtherWay(prism, bGen, EQB) }),
            Law("Prism law: modify identity", { modifyIdentity(prism, aGen, EQA) }),
            Law("Prism law: compose modify", { composeModify(prism, aGen, funcGen, EQA) }),
            Law("Prism law: consistent set modify", { consistentSetModify(prism, aGen, bGen, EQA) }),
            Law("Prism law: consistent get option modify id", { consistentGetOptionModifyId(prism, aGen, FA, EQB) })
    )

    fun <A, B> partialRoundTripOneWay(prism: Prism<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                EQA.eqv(prism.getOrModify(a).fold(::identity, prism::reverseGet), a)
            })

    fun <A, B> roundTripOtherWay(prism: Prism<A, B>, bGen: Gen<B>, EQB: Eq<B>): Unit =
            forAll(bGen, { b ->
                prism.getOption(prism.reverseGet(b)).exists { EQB.eqv(it, b) }
            })

    fun <A, B> modifyIdentity(prism: Prism<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                EQA.eqv(prism.modify(::identity)(a), a)
            })

    fun <A, B> composeModify(prism: Prism<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                EQA.eqv(prism.modify(g)(prism.modify(f)(a)), prism.modify(g compose f)(a))
            })

    fun <A, B> consistentSetModify(prism: Prism<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                EQA.eqv(prism.set(b)(a), prism.modify { b }(a))
            })

    inline fun <A, B, reified F> consistentGetOptionModifyId(prism: Prism<A, B>, aGen: Gen<A>, FA: Applicative<F>, EQB: Eq<B>): Unit =
            forAll(aGen, { a ->
                prism.modifyF(FA, { FA.pure(it) }, a).exists { prism.getOption(it).exists { b -> prism.getOption(a).exists { EQB.eqv(b, it) } } }
            })

}