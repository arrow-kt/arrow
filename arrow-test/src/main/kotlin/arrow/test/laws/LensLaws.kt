package arrow.test.laws

import arrow.core.Id
import arrow.typeclasses.Eq
import arrow.core.compose
import arrow.core.functor
import arrow.core.identity
import arrow.core.value
import arrow.data.Const
import arrow.data.applicative
import arrow.data.value
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.optics.Lens
import arrow.optics.modify
import arrow.typeclasses.Monoid
import arrow.typeclasses.eq
import arrow.typeclasses.monoid

object LensLaws {

    inline fun <reified A, reified B> laws(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A> = eq(), EQB: Eq<B>, MB: Monoid<B> = monoid()) = listOf(
            Law("Lens law: get set", { lensGetSet(lens, aGen, EQA) }),
            Law("Lens law: set get", { lensSetGet(lens, aGen, bGen, EQB) }),
            Law("Lens law: is set idempotent", { lensSetIdempotent(lens, aGen, bGen, EQA) }),
            Law("Lens law: modify identity", { lensModifyIdentity(lens, aGen, EQA) }),
            Law("Lens law: compose modify", { lensComposeModify(lens, aGen, funcGen, EQA) }),
            Law("Lens law: consistent set modify", { lensConsistentSetModify(lens, aGen, bGen, EQA) }),
            Law("Lens law: consistent modify modify id", { lensConsistentModifyModifyId(lens, aGen, funcGen, EQA) }),
            Law("Lens law: consistent get modify id", { lensConsistentGetModifyid(lens, aGen, EQB, MB) })
    )

    inline fun <reified A, reified B> lensGetSet(lens: Lens<A, B>, aGen: Gen<A>, EQA: Eq<A>) =
            forAll(aGen, { a ->
                lens.set(a, lens.get(a)).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> lensSetGet(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B>) =
            forAll(aGen, bGen, { a, b ->
                lens.get(lens.set(a, b)).equalUnderTheLaw(b, EQB)
            })

    inline fun <reified A, reified B> lensSetIdempotent(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
            forAll(aGen, bGen, { a, b ->
                lens.set(lens.set(a, b), b).equalUnderTheLaw(lens.set(a, b), EQA)
            })

    inline fun <reified A, reified B> lensModifyIdentity(lens: Lens<A, B>, aGen: Gen<A>, EQA: Eq<A>) =
            forAll(aGen, { a ->
                lens.modify(a, ::identity).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> lensComposeModify(lens: Lens<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>) =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                lens.modify(lens.modify(a, f), g).equalUnderTheLaw(lens.modify(a, g compose f), EQA)
            })

    inline fun <reified A, reified B> lensConsistentSetModify(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
            forAll(aGen, bGen, { a, b ->
                lens.set(a, b).equalUnderTheLaw(lens.modify(a) { b }, EQA)
            })

    inline fun <reified A, reified B> lensConsistentModifyModifyId(lens: Lens<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>) =
            forAll(aGen, funcGen, { a, f ->
                lens.modify(a, f)
                        .equalUnderTheLaw(lens.modifyF(Id.functor(), a, { Id.pure(f(it)) }).value(), EQA)
            })

    inline fun <reified A, reified B> lensConsistentGetModifyid(lens: Lens<A, B>, aGen: Gen<A>, EQB: Eq<B>, MA: Monoid<B>) =
            forAll(aGen, { a ->
                lens.get(a)
                        .equalUnderTheLaw(lens.modifyF(Const.applicative(MA), a, ::Const).value(), EQB)
            })

}