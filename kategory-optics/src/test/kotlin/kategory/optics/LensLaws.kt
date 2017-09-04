package kategory.optics

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Applicative
import kategory.Eq
import kategory.Law
import kategory.Lens
import kategory.compose
import kategory.exists
import kategory.identity

object LensLaws {

    inline fun <A, B, reified F> laws(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, FA: Applicative<F>): List<Law> = listOf(
            Law("getSet", { forAll(aGen, { a -> EQA.eqv(lens.set(lens.get(a))(a), a) }) }),
            Law("setGet", { forAll(aGen, bGen, { a, b -> EQB.eqv(lens.get(lens.set(b)(a)), b) }) }),
            Law("setIdempotent", { forAll(aGen, bGen, { a, b -> EQA.eqv(lens.set(b)(lens.set(b)(a)), lens.set(b)(a)) }) }),
            Law("modifyIdentity", { forAll(aGen, bGen, { a, b -> EQA.eqv(lens.modify(::identity, a), a) }) }),
            Law("composeModify", { forAll(aGen, funcGen, funcGen, { a, f, g -> EQA.eqv(lens.modify(g, lens.modify(f, a)), lens.modify(g compose f, a)) }) }),
            Law("consistentSetModify", { forAll(aGen, bGen, { a, b -> EQA.eqv(lens.set(b)(a), lens.modify({ b }, a)) }) }),
            Law("consistentModifyModifyId", { forAll(aGen, funcGen, { a, f -> lens.modifyF(FA, { FA.pure(f(it)) }, a).exists { EQA.eqv(lens.modify(f, a), it) } }) }),
            Law("consistentGetModifyId", { forAll(aGen, { a -> lens.modifyF(FA, { FA.pure(it) }, a).exists { EQB.eqv(lens.get(a), lens.get(it)) } }) })
    )

}