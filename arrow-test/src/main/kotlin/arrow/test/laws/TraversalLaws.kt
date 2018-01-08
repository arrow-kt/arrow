package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.core.Option
import arrow.core.compose
import arrow.core.identity
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.optics.Traversal
import arrow.optics.modify
import arrow.syntax.monoid.combineAll

object TraversalLaws {

    inline fun <reified A, reified B : Any> laws(traversal: Traversal<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>) = listOf(
            Law("Traversal law: head option", { headOption(traversal, aGen, EQB) }),
            Law("Traversal law: modify get all", { modifyGetAll(traversal, aGen, funcGen, EQB) }),
            Law("Traversal law: set is idempotent", { setIdempotent(traversal, aGen, bGen, EQA) }),
            Law("Traversal law: modify identity", { modifyIdentity(traversal, aGen, EQA) }),
            Law("Traversal law: compose modify", { composeIdentity(traversal, aGen, funcGen, EQA) })
    )

    inline fun <reified A, reified B : Any> headOption(traversal: Traversal<A, B>, aGen: Gen<A>, EQB: Eq<B>): Unit =
            forAll(aGen, { a ->
                traversal.headOption(a).exists { b ->
                    Option.fromNullable(traversal.getAll(a)
                            .firstOrNull())
                            .exists {
                                b.equalUnderTheLaw(it, EQB)
                            }
                }
            })

    inline fun <reified A, reified B> modifyGetAll(traversal: Traversal<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQB: Eq<B>): Unit =
            forAll(aGen, funcGen, { a, f ->
                traversal.getAll(traversal.modify(a, f))
                        .zip(traversal.getAll(a).map(f), { b1, b2 -> EQB.eqv(b1, b2) })
                        .combineAll(object : Monoid<Boolean> {
                            override fun empty(): Boolean = true
                            override fun combine(a: Boolean, b: Boolean): Boolean = a && b
                        })
            })

    inline fun <reified A, reified B> setIdempotent(traversal: Traversal<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                traversal.set(traversal.set(a, b), b)
                        .equalUnderTheLaw(traversal.set(a, b), EQA)
            })

    inline fun <reified A, reified B> modifyIdentity(traversal: Traversal<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                traversal.modify(a, ::identity).equalUnderTheLaw(a, EQA)
            })


    inline fun <reified A, reified B> composeIdentity(traversal: Traversal<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                traversal.modify(traversal.modify(a, f), g)
                        .equalUnderTheLaw(traversal.modify(a, g compose f), EQA)
            })

}