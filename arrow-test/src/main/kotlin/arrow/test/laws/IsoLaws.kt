package arrow.test.laws

import arrow.core.*
import arrow.data.Const
import arrow.data.applicative
import arrow.data.value
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.optics.Iso
import arrow.optics.modify
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.typeclasses.monoid

object IsoLaws {

    inline fun <reified A, reified B> laws(iso: Iso<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, bMonoid: Monoid<B> = monoid()): List<Law> = listOf(
            Law("Iso Law: round trip one way", { roundTripOneWay(iso, aGen, EQA) }),
            Law("Iso Law: round trip other way", { roundTripOtherWay(iso, bGen, EQB) }),
            Law("Iso Law: modify identity is identity", { modifyIdentity(iso, aGen, EQA) }),
            Law("Iso Law: compose modify", { composeModify(iso, aGen, funcGen, EQA) }),
            Law("Iso Law: consitent set with modify", { consistentSetModify(iso, aGen, bGen, EQA) }),
            Law("Iso Law: consistent modify with modify identity", { consistentModifyModifyId(iso, aGen, funcGen, EQA) }),
            Law("Iso Law: consitent get with modify identity", { consitentGetModifyId(iso, aGen, EQB, bMonoid) })
    )

    inline fun <reified A, reified B> roundTripOneWay(iso: Iso<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                iso.reverseGet(iso.get(a)).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> roundTripOtherWay(iso: Iso<A, B>, bGen: Gen<B>, EQB: Eq<B>): Unit =
            forAll(bGen, { b ->
                iso.get(iso.reverseGet(b)).equalUnderTheLaw(b, EQB)
            })

    inline fun <reified A, reified B> modifyIdentity(iso: Iso<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                iso.modify(a, ::identity).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> composeModify(iso: Iso<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                iso.modify(iso.modify(a, f), g).equalUnderTheLaw(iso.modify(a, g compose f), EQA)
            })

    inline fun <reified A, reified B> consistentSetModify(iso: Iso<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                iso.set(b).equalUnderTheLaw(iso.modify(a) { b }, EQA)
            })

    inline fun <reified A, reified B> consistentModifyModifyId(iso: Iso<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, { a, f ->
                iso.modify(a, f).equalUnderTheLaw(iso.modifyF(Id.functor(), a, { Id.pure(f(it)) }).value(), EQA)
            })

    inline fun <reified A, reified B> consitentGetModifyId(iso: Iso<A, B>, aGen: Gen<A>, EQB: Eq<B>, bMonoid: Monoid<B>): Unit =
            forAll(aGen, { a ->
                iso.get(a).equalUnderTheLaw(iso.modifyF(Const.applicative(bMonoid), a, ::Const).value(), EQB)
            })

}