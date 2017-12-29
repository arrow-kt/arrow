package arrow

import arrow.core.*
import arrow.data.*
import arrow.free.instances.*
import arrow.instances.applicative
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.optics.*

object OptionalLaws {

    inline fun <reified A, reified B> laws(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, EQOptionB: Eq<Option<B>>): List<Law> = listOf(
            Law("Optional Law: set what you get", { getOptionSet(optional, aGen, EQA) }),
            Law("Optional Law: get what you get", { getGetOption(optional, aGen, bGen, EQB) }),
            Law("Optional Law: set is idempotent", { setIdempotent(optional, aGen, bGen, EQA) }),
            Law("Optional Law: modify identity = identity", { modifyIdentity(optional, aGen, EQA) }),
            Law("Optional Law: compose modify", { composeModify(optional, aGen, funcGen, EQA) }),
            Law("Optional Law: consistent set with modify", { consistentSetModify(optional, aGen, bGen, EQA) }),
            Law("Optional Law: consistent modify with modify identity", { consistentModifyModifyId(optional, aGen, funcGen, EQA) }),
            Law("Optional Law: consistent getOption with modify identity", { consistentGetOptionModifyId(optional, aGen, EQOptionB) })
    )

    inline fun <reified A, reified B> getOptionSet(optional: Optional<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                optional.getOrModify(a).fold(::identity, { optional.set(a, it) }).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> getGetOption(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B>): Unit =
            forAll(aGen, bGen, { a, b ->
                optional.getOption(optional.set(a, b)).exists {
                    it.equalUnderTheLaw(b, EQB)
                }
            })

    inline fun <reified A, reified B> setIdempotent(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                optional.set(optional.set(a, b), b).equalUnderTheLaw(optional.set(a, b), EQA)
            })

    inline fun <reified A, reified B> modifyIdentity(optional: Optional<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                optional.modify(a, ::identity).equalUnderTheLaw(a, EQA)
            })


    inline fun <reified A, reified B> composeModify(optional: Optional<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                optional.modify(optional.modify(a, f), g).equalUnderTheLaw(optional.modify(a, g compose f), EQA)
            })

    inline fun <reified A, reified B> consistentSetModify(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                optional.set(a, b).equalUnderTheLaw(optional.modify(a) { b }, EQA)
            })

    inline fun <reified A, reified B> consistentModifyModifyId(optional: Optional<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, { a, f ->
                optional.modify(a, f).equalUnderTheLaw(optional.modifyF(Id.applicative(), a, { Id.pure(f(it)) }).value(), EQA)
            })

    inline fun <reified A, reified B> consistentGetOptionModifyId(optional: Optional<A, B>, aGen: Gen<A>, EQOptionB: Eq<Option<B>>): Unit {
        val firstMonoid = object : Monoid<FirstOption<B>> {
            override fun empty(): FirstOption<B> = FirstOption(None)
            override fun combine(a: FirstOption<B>, b: FirstOption<B>): FirstOption<B> = if (a.option.fold({ false }, { true })) a else b
        }

        forAll(aGen, { a ->
            optional.modifyF(Const.applicative(firstMonoid), a, { b ->
                Const(FirstOption(Some(b)))
            }).value().option.equalUnderTheLaw(optional.getOption(a), EQOptionB)
        })
    }

    @PublishedApi internal data class FirstOption<A>(val option: Option<A>)

}