package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.optics.Iso

object IsoLaws {

    inline fun <A, reified B> laws(iso: Iso<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, bMonoid: Monoid<B> = monoid()): List<Law> = listOf(
            Law("Iso Law: round trip one way", { roundTripOneWay(iso, aGen, EQA) }),
            Law("Iso Law: round trip other way", { roundTripOtherWay(iso, bGen, EQB) }),
            Law("Iso Law: modify identity is identity", { modifyIdentity(iso, aGen, EQA) }),
            Law("Iso Law: compose modify", { composeModify(iso, aGen, funcGen, EQA) }),
            Law("Iso Law: consitent set with modify", { consistentSetModify(iso, aGen, bGen, EQA) }),
            Law("Iso Law: consistent modify with modify identity", { consistentModifyModifyId(iso, aGen, funcGen, EQA) }),
            Law("Iso Law: consitent get with modify identity", { consitentGetModifyId(iso, aGen, EQB, bMonoid) })
    )

    fun <A, B> roundTripOneWay(iso: Iso<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                EQA.eqv(iso.reverseGet(iso.get(a)), a)
            })

    fun <A, B> roundTripOtherWay(iso: Iso<A, B>, bGen: Gen<B>, EQB: Eq<B>): Unit =
            forAll(bGen, { b ->
                EQB.eqv(iso.get(iso.reverseGet(b)), b)
            })

    fun <A, B> modifyIdentity(iso: Iso<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                EQA.eqv(iso.modify(::identity)(a), a)
            })

    fun <A, B> composeModify(iso: Iso<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                EQA.eqv(
                        iso.modify(g)(iso.modify(f)(a)),
                        iso.modify(g compose f)(a)
                )
            })

    fun <A, B> consistentSetModify(iso: Iso<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                EQA.eqv(
                        iso.set(b)(a),
                        iso.modify { b }(a)
                )
            })

    fun <A, B> consistentModifyModifyId(iso: Iso<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, { a, f ->
                EQA.eqv(
                        iso.modify(f)(a),
                        iso.modifyF(Id.functor(), { Id.pure(f(it)) }, a).value()
                )
            })

    inline fun <A, reified B> consitentGetModifyId(iso: Iso<A, B>, aGen: Gen<A>, EQB: Eq<B>, bMonoid: Monoid<B>): Unit =
            forAll(aGen, { a ->
                EQB.eqv(
                        iso.get(a),
                        iso.modifyF(Const.applicative(bMonoid), ::Const, a).value()

                )
            })

}