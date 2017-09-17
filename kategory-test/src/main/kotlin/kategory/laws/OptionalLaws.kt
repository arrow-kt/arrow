package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.optics.Optional

object OptionalLaws {

    inline fun <reified A, reified B> laws(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>): List<Law> = listOf(
            Law("Optional Law: set what you get", { getOptionSet(optional, aGen, EQA) }),
            Law("Optional Law: get what you get", { getGetOption(optional, aGen, bGen, EQB) }),
            Law("Optional Law: set is idempotent", { setIdempotent(optional, aGen, bGen, EQA) }),
            Law("Optional Law: modify identity = identity", { modifyIdentity(optional, aGen, EQA) }),
            Law("Optional Law: compose modify", { composeModify(optional, aGen, funcGen, EQA) }),
            Law("Optional Law: consistent set with modify", { consistentSetModify(optional, aGen, bGen, EQA) }),
            Law("Optional Law: consistent modify with modify identity", { consistentModifyModifyId(optional, aGen, funcGen, EQA) }),
            Law("Optional Law: consistent getOption with modify identity", { consistentGetOptionModifyId(optional, aGen, EQB) })
    )

    inline fun <reified A, reified B> getOptionSet(optional: Optional<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                optional.getOrModify(a).fold(::identity, { optional.set(it)(a) }).equalUnderTheLaw(a, EQA)
            })

    inline fun <reified A, reified B> getGetOption(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B>): Unit =
            forAll(aGen, bGen, { a, b ->
                optional.getOption(optional.set(b)(a)).exists {
                    it.equalUnderTheLaw(b, EQB)
                }
            })

    inline fun <reified A, reified B> setIdempotent(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                optional.set(b)(optional.set(b)(a)).equalUnderTheLaw(optional.set(b)(a), EQA)
            })

    inline fun <reified A, reified B> modifyIdentity(optional: Optional<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                optional.modify(::identity)(a).equalUnderTheLaw(a, EQA)
            })


    inline fun <reified A, reified B> composeModify(optional: Optional<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                optional.modify(g)(optional.modify(f)(a)).equalUnderTheLaw(optional.modify(g compose f)(a), EQA)
            })

    inline fun <reified A, reified B> consistentSetModify(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                optional.set(b)(a).equalUnderTheLaw(optional.modify { b }(a), EQA)
            })

    inline fun <reified A, reified B> consistentModifyModifyId(optional: Optional<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, { a, f ->
                optional.modify(f)(a).equalUnderTheLaw(optional.modifyF(Id.applicative(), { Id.pure(f(it)) }, a).value(), EQA)
            })

    inline fun <reified A, reified B> consistentGetOptionModifyId(optional: Optional<A, B>, aGen: Gen<A>, EQB: Eq<B>): Unit =
            forAll(aGen, { a ->
                val getOption = optional.getOption(a)

                val modifyFId = optional.modifyF(Const.applicative(firstOptionMonoid<B>()), { b ->
                    Const(FirstOption(b.some()))
                }, a).value().option

                getOption.exists { b ->
                    modifyFId.exists {
                        it.equalUnderTheLaw(b, EQB)
                    }
                }
            })

    @PublishedApi internal data class FirstOption<A>(val option: Option<A>)

    @PublishedApi internal fun <A> firstOptionMonoid() = object : Monoid<FirstOption<A>> {
        override fun empty(): FirstOption<A> = FirstOption(Option.None)

        override fun combine(a: FirstOption<A>, b: FirstOption<A>): FirstOption<A> =
                if (a.option.fold({ false }, { true })) a else b
    }

}