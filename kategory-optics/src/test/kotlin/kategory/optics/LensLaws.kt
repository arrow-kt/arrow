package kategory.optics

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Applicative
import kategory.Eq
import kategory.Law
import kategory.compose
import kategory.exists
import kategory.identity

object LensLaws {

    inline fun <A, B, reified F> laws(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, FA: Applicative<F>) = listOf(
            Law("Lens law: get set", { lensGetSet(lens, aGen, EQA) }),
            Law("Lens law: set get", { lensSetGet(lens, aGen, bGen, EQB) }),
            Law("Lens law: is set idempotent", { lensSetIdempotent(lens, aGen, bGen, EQA) }),
            Law("Lens law: modify identity", { lensModifyIdentity(lens, aGen, bGen, EQA) }),
            Law("Lens law: compose modify", { lensComposeModify(lens, aGen, funcGen, EQA) }),
            Law("Lens law: consistent set modify", { lensConsistentSetModify(lens, aGen, bGen, EQA) }),
            Law("Lens law: consistent modify modify id", { lensConsistentModifyModifyId(lens, aGen, funcGen, EQA, FA) }),
            Law("Lens law: consistent get modify id", { lensConsistentGetModifyid(lens, aGen, EQB, FA) })
    )

    fun <A, B> lensGetSet(lens: Lens<A, B>, aGen: Gen<A>, EQA: Eq<A>) =
            forAll(aGen, { a ->
                EQA.eqv(lens.set(lens.get(a))(a), a)
            })

    fun <A, B> lensSetGet(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B>) =
            forAll(aGen, bGen, { a, b ->
                EQB.eqv(lens.get(lens.set(b)(a)), b)
            })

    fun <A, B> lensSetIdempotent(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
            forAll(aGen, bGen, { a, b ->
                EQA.eqv(lens.set(b)(lens.set(b)(a)), lens.set(b)(a))
            })

    fun <A, B> lensModifyIdentity(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
            forAll(aGen, bGen, { a, b ->
                EQA.eqv(lens.modify(::identity, a), a)
            })

    fun <A, B> lensComposeModify(lens: Lens<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>) =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                EQA.eqv(lens.modify(g, lens.modify(f, a)), lens.modify(g compose f, a))
            })

    fun <A, B> lensConsistentSetModify(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
            forAll(aGen, bGen, { a, b ->
                EQA.eqv(lens.set(b)(a), lens.modify({ b }, a))
            })

    inline fun <A, B, reified F> lensConsistentModifyModifyId(lens: Lens<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, FA: Applicative<F>) =
            forAll(aGen, funcGen, { a, f ->
                lens.modifyF(FA, { FA.pure(f(it)) }, a).exists { EQA.eqv(lens.modify(f, a), it) }
            })

    inline fun <A, B, reified F> lensConsistentGetModifyid(lens: Lens<A, B>, aGen: Gen<A>, EQB: Eq<B>, FA: Applicative<F>) =
            forAll(aGen, { a ->
                lens.modifyF(FA, { FA.pure(it) }, a).exists { EQB.eqv(lens.get(a), lens.get(it)) }
            })

}