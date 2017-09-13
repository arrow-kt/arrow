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

    fun <A, B> getOptionSet(optional: Optional<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                EQA.eqv(
                        optional.getOrModify(a).fold(::identity, { optional.set(it)(a) }),
                        a
                )
            })

    fun <A, B> getGetOption(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B>): Unit =
            forAll(aGen, bGen, { a, b ->
                optional.getOption(optional.set(b)(a)).exists { EQB.eqv(it, b) }
            })

    fun <A, B> setIdempotent(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                EQA.eqv(
                        optional.set(b)(optional.set(b)(a)),
                        optional.set(b)(a)
                )
            })

    fun <A, B> modifyIdentity(optional: Optional<A, B>, aGen: Gen<A>, EQA: Eq<A>): Unit =
            forAll(aGen, { a ->
                EQA.eqv(
                        optional.modify(::identity)(a),
                        a
                )
            })


    fun <A, B> composeModify(optional: Optional<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, funcGen, { a, f, g ->
                EQA.eqv(
                        optional.modify(g)(optional.modify(f)(a)),
                        optional.modify(g compose f)(a)
                )
            })

    fun <A, B> consistentSetModify(optional: Optional<A, B>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
            forAll(aGen, bGen, { a, b ->
                EQA.eqv(
                        optional.set(b)(a),
                        optional.modify { b }(a)
                )
            })

    fun <A, B> consistentModifyModifyId(optional: Optional<A, B>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
            forAll(aGen, funcGen, { a, f ->
                EQA.eqv(
                        optional.modify(f)(a),
                        optional.modifyF(Id.applicative(), { Id.pure(f(it)) }, a).value()
                )
            })

    inline fun <reified A, B> consistentGetOptionModifyId(optional: Optional<A, B>, aGen: Gen<A>, EQB: Eq<B>): Unit =
            forAll(aGen, { a ->
                val getOption = optional.getOption(a)

                val modifyFId = optional.modifyF(Const.applicative(firstOptionMonoid<B>()), { b ->
                    Const(FirstOption(b.some()))
                }, a).value().option

                getOption.exists { b ->
                    modifyFId.exists {
                        EQB.eqv(b, it)
                    }
                }
            })

    @PublishedApi internal data class FirstOption<A>(val option: Option<A>)

    @PublishedApi internal fun <A> firstOptionMonoid() = object : Monoid<FirstOption<A>> {
        override fun empty(): FirstOption<A> = FirstOption(Option.None)

        override fun combine(a: FirstOption<A>, b: FirstOption<A>): FirstOption<A> =
                if (a.option.isDefined) a else b
    }

}