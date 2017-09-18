package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.optics.Lens

object LensLaws {

    inline fun <reified A, reified B, reified F> laws(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, FA: Applicative<F>) = listOf(
            Law("Lens law: get set", { lensGetSet(lens, aGen, EQA) }),
            Law("Lens law: set get", { lensSetGet(lens, aGen, bGen, EQB) }),
            Law("Lens law: is set idempotent", { lensSetIdempotent(lens, aGen, bGen, EQA) }),
            Law("Lens law: modify identity", { lensModifyIdentity(lens, aGen, bGen, EQA) }),
            Law("Lens law: compose modify", { lensComposeModify(lens, aGen, funcGen, EQA) }),
            Law("Lens law: consistent set modify", { lensConsistentSetModify(lens, aGen, bGen, EQA) }),
            Law("Lens law: consistent modify modify id", { lensConsistentModifyModifyId(lens, aGen, funcGen, EQA, FA) }),
            Law("Lens law: consistent get modify id", { lensConsistentGetModifyid(lens, aGen, EQB, FA) })
    )

    inline fun <reified A, reified B> lensGetSet(lens: Lens<A, B>, aGen: Gen<A>, EQA: Eq<A>) =
            forAll(aGen, { a ->
                lens.set(lens.get(a))(a).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> lensSetGet(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B>) =
            forAll(aGen, bGen, { a, b ->
                lens.get(lens.set(b)(a)).equalUnderTheLaw(b, EQB)
            })

    inline fun <reified A, reified B> lensSetIdempotent(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
            forAll(aGen, bGen, { a, b ->
                lens.set(b)(lens.set(b)(a)).equalUnderTheLaw(lens.set(b)(a), EQA)
            })

    inline fun <reified A, reified B> lensModifyIdentity(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
            forAll(aGen, bGen, { a, b ->
                lens.modify(::identity, a).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> lensComposeModify(lens: Lens<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>) =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                lens.modify(g, lens.modify(f, a)).equalUnderTheLaw(lens.modify(g compose f, a), EQA)
            })

    inline fun <reified A, reified B> lensConsistentSetModify(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
            forAll(aGen, bGen, { a, b ->
                lens.set(b)(a).equalUnderTheLaw(lens.modify({ b }, a), EQA)
            })

    inline fun <reified A, reified B, reified F> lensConsistentModifyModifyId(lens: Lens<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, FA: Applicative<F>) =
            forAll(aGen, funcGen, { a, f ->
                lens.modifyF(FA, { FA.pure(f(it)) }, a).exists {
                    it.equalUnderTheLaw(lens.modify(f, a), EQA)
                }
            })

    inline fun <reified A, reified B, reified F> lensConsistentGetModifyid(lens: Lens<A, B>, aGen: Gen<A>, EQB: Eq<B>, FA: Applicative<F>) =
            forAll(aGen, { a ->
                lens.modifyF(FA, { FA.pure(it) }, a).exists {
                    lens.get(it).equalUnderTheLaw(lens.get(a), EQB)
                }
            })

}