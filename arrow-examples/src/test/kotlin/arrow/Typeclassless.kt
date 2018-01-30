package arrow

import arrow.TypeclasslessExamples.One.thing3
import arrow.TypeclasslessExamples.Two.thing4
import arrow.data.*
import arrow.typeclasses.Applicative
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FreeSpec

class TypeclasslessExamples : FreeSpec() {

    // Syntaxis

    interface ApplicativeSyntaxis<F> {
        fun AP(): Applicative<F>

        fun <A> A.pure(): HK<F, A> =
                AP().pure(this)

        fun <A, B> HK<F, A>.map(f: (A) -> B): HK<F, B> =
                AP().map(this, f)
    }

    fun <F> Applicative<F>.s() =
            object : ApplicativeSyntaxis<F> {
                override fun AP(): Applicative<F> =
                        this@s
            }

    // Easy to write syntaxis with fake typeclass

    interface IdentifySyntaxis<F> {
        fun <A> HK<F, A>.identify(): HK<F, A>
    }

    // Combined typeclass requirements

    interface ApplicativeAndIdentifySyntaxis<F> : ApplicativeSyntaxis<F>, IdentifySyntaxis<F>

    fun <F> funversable(AP: Applicative<F>, ID: IdentifySyntaxis<F>): ApplicativeAndIdentifySyntaxis<F> =
            (object : ApplicativeAndIdentifySyntaxis<F>, ApplicativeSyntaxis<F> by AP.s(), IdentifySyntaxis<F> by ID {})

    // Trivial Identify instance

    val ID_LIST = object : IdentifySyntaxis<ListKWHK> {
        override fun <A> HK<ListKWHK, A>.identify(): HK<ListKWHK, A> =
                this.ev()
    }

    // Client code

    object One {
        fun <F> ApplicativeSyntaxis<F>.thing3(): HK<F, Int> =
                1.pure()
    }

    object Two {
        fun <F> IdentifySyntaxis<F>.thing(a: HK<F, Int>): HK<F, Int> =
                a.identify()

        fun <F> ApplicativeSyntaxis<F>.thing2(): HK<F, Int> =
                1.pure().map { thing3() }.map { 1 }

        fun <F> ApplicativeAndIdentifySyntaxis<F>.thing4(): HK<F, Int> =
                thing(thing2()).identify()
    }

    init {
        "Injection typeclassless style" {
            funversable(ListKW.applicative(), ID_LIST).thing4() shouldBe listOf(1).k()
        }

        // FIXME:  Eldritch horror
        "Transform receiver to regular function" {
            fun <A, R> toFun(f: A.() -> R): (A) -> R =
                    f

            toFun<ApplicativeAndIdentifySyntaxis<ListKWHK>, HK<ListKWHK, Int>> { thing4() }(funversable(ListKW.applicative(), ID_LIST))
        }
    }
}