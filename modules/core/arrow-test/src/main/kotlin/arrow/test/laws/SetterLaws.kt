package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.core.compose
import arrow.core.identity
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.optics.Setter
import arrow.typeclasses.eq

object SetterLaws {

    inline fun <reified A, reified B> laws(setter: Setter<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A> = eq()) = listOf(
            Law("Setter law: set is idempotent", { setIdempotent(setter, aGen, bGen, EQA) }),
            Law("Setter law: modify identity", { modifyIdentity(setter, aGen, EQA) }),
            Law("Setter law: compose modify", { composeModify(setter, aGen, EQA, funcGen) }),
            Law("Setter law: consistent set modify", { consistentSetModify(setter, aGen, bGen, EQA) })
    )

    inline fun <reified A, reified B> setIdempotent(setter: Setter<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit = forAll(aGen, bGen, { a, b ->
        setter.set(setter.set(a, b), b).equalUnderTheLaw(setter.set(a, b), EQA)
    })

    inline fun <reified A, reified B> modifyIdentity(setter: Setter<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit = forAll(aGen, { a ->
        setter.modify(a, ::identity).equalUnderTheLaw(a, EQA)
    })

    inline fun <reified A, reified B> composeModify(setter: Setter<A, B>, aGen: Gen<A>, EQA: Eq<A>, funcGen: Gen<(B) -> B>): Unit = forAll(aGen, funcGen, funcGen, { a, f, g ->
        setter.modify(setter.modify(a, f), g).equalUnderTheLaw(setter.modify(a, g compose f), EQA)
    })

    inline fun <reified A, reified B> consistentSetModify(setter: Setter<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit = forAll(aGen, bGen, { a, b ->
        setter.modify(a) { b }.equalUnderTheLaw(setter.set(a, b), EQA)
    })

}