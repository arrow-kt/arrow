package arrow.test.laws

import arrow.core.*
import arrow.data.Const
import arrow.data.applicative
import arrow.data.value
import arrow.optics.Optional
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object OptionalLaws {

    inline fun <reified A, reified B> laws(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQOptionB: Eq<Option<B>>): List<Law> = listOf(
            Law("Optional Law: set what you get", { optional.getOptionSet(aGen, EQA) }),
            Law("Optional Law: set what you get", { optional.setGetOption(aGen, bGen, EQOptionB) }),
            Law("Optional Law: set is idempotent", { optional.setIdempotent(aGen, bGen, EQA) }),
            Law("Optional Law: modify identity = identity", { optional.modifyIdentity(aGen, EQA) }),
            Law("Optional Law: compose modify", { optional.composeModify(aGen, funcGen, EQA) }),
            Law("Optional Law: consistent set with modify", { optional.consistentSetModify(aGen, bGen, EQA) }),
            Law("Optional Law: consistent modify with modify identity", { optional.consistentModifyModifyId(aGen, funcGen, EQA) }),
            Law("Optional Law: consistent getOption with modify identity", { optional.consistentGetOptionModifyId(aGen, EQOptionB) })
    )

    fun <A, B> Optional<A, B>.getOptionSet(aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                getOrModify(a).fold(::identity, { set(a, it) })
                        .equalUnderTheLaw(a, EQA)
            })

    fun <A, B> Optional<A, B>.setGetOption(aGen: Gen<A>, bGen: Gen<B>, EQOptionB: Eq<Option<B>>): Unit =
            forAll(aGen, bGen, { a, b ->
                getOption(set(a, b))
                        .equalUnderTheLaw(getOption(a).map { _ -> b }, EQOptionB)
            })

    fun <A, B> Optional<A, B>.setIdempotent(aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                set(set(a, b), b)
                        .equalUnderTheLaw(set(a, b), EQA)
            })

    fun <A, B> Optional<A, B>.modifyIdentity(aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                modify(a, ::identity)
                        .equalUnderTheLaw(a, EQA)
            })

    fun <A, B> Optional<A, B>.composeModify(aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                modify(modify(a, f), g)
                        .equalUnderTheLaw(modify(a, g compose f), EQA)
            })

    fun <A, B> Optional<A, B>.consistentSetModify(aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                set(a, b)
                        .equalUnderTheLaw(modify(a) { b }, EQA)
            })

    fun <A, B> Optional<A, B>.consistentModifyModifyId(aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, { a, f ->
                modify(a, f)
                        .equalUnderTheLaw(modifyF(Id.applicative(), a, { Id.pure(f(it)) }).value(), EQA)
            })

    fun <A, B> Optional<A, B>.consistentGetOptionModifyId(aGen: Gen<A>, EQOptionB: Eq<Option<B>>): Unit {
        val firstMonoid = object : Monoid<FirstOption<B>> {
            override fun empty(): FirstOption<B> = FirstOption(None)
            override fun FirstOption<B>.combine(b: FirstOption<B>): FirstOption<B> = if (option.fold({ false }, { true })) this else b
        }

        forAll(aGen, { a ->
            modifyF(Const.applicative(firstMonoid), a, { b ->
                Const(FirstOption(Some(b)))
            }).value().option.equalUnderTheLaw(getOption(a), EQOptionB)
        })
    }

    @PublishedApi
    internal data class FirstOption<A>(val option: Option<A>)

}