package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.optics.Setter

object SetterLaws {

    inline fun <reified A, reified B> laws(setter: Setter<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>) = listOf(
            Law("Setter law: set is idempotent", { setIdempotent(setter, aGen, bGen, EQA) }),
            Law("Setter law: modify identity", { modifyIdentity(setter, aGen, bGen, EQA) }),
            Law("Setter law: compose modify", { composeModify(setter, aGen, EQA, funcGen) }),
            Law("Setter law: consistent set modify", { consistentSetModify(setter, aGen, bGen, EQA) })
    )

    inline fun <reified A, reified B> setIdempotent(setter: Setter<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit = forAll(aGen, bGen, { a, b ->
        setter.set(b)(setter.set(b)(a)).equalUnderTheLaw(setter.set(b)(a), EQA)
    })

    inline fun <reified A, reified B> modifyIdentity(setter: Setter<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit = forAll(aGen, bGen, { a, b ->
        setter.modify(::identity)(a).equalUnderTheLaw(a, EQA)
    })

    inline fun <reified A, reified B> composeModify(setter: Setter<A, B>, aGen: Gen<A>, EQA: Eq<A>, funcGen: Gen<(B) -> B>): Unit = forAll(aGen, funcGen, funcGen, { a, f, g ->
        setter.modify(g)(setter.modify(f)(a)).equalUnderTheLaw(setter.modify(g compose f)(a), EQA)
    })

    inline fun <reified A, reified B> consistentSetModify(setter: Setter<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit = forAll(aGen, bGen, { a, b ->
        setter.modify { b }(a).equalUnderTheLaw(setter.set(b)(a), EQA)
    })

}