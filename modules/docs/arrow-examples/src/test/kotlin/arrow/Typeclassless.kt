package arrow

import arrow.TypeclasslessExamples.ScopeOne.inScopeOne
import arrow.TypeclasslessExamples.ScopeTwo.withAll
import arrow.TypeclasslessExamples.ScopeTwo.withApplicative
import arrow.core.identity
import arrow.data.ForListK
import arrow.data.ListK
import arrow.data.applicative
import arrow.data.k
import arrow.typeclasses.Applicative
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FreeSpec

class TypeclasslessExamples : FreeSpec() {

    ///////////////////////////////////
    //// BUILDING THE MACHINERY
    ///////////////////////////////////

    // Complete example of syntax using a simple fake typeclass

    interface Identity<F> {
        fun <A> identify(a: Kind<F, A>): Kind<F, A> =
                a
    }

    interface IdentifySyntax<F> {
        fun ID(): Identity<F>

        fun <A> Kind<F, A>.identify(): Kind<F, A> =
                ID().identify(this)
    }

    fun <F> Identity<F>.s() =
            object : IdentifySyntax<F> {
                override fun ID(): Identity<F> =
                        this@s
            }

    // Syntax for existing typeclass

    interface ApplicativeSyntax<F> {
        fun AP(): Applicative<F>

        fun <A> A.just(): Kind<F, A> =
                AP().just(this)

        fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = AP().run {
            this@map.map(f)
        }
    }

    fun <F> Applicative<F>.s() =
            object : ApplicativeSyntax<F> {
                override fun AP(): Applicative<F> =
                        this@s
            }

    // How to define a requirement on multiple typeclasses

    interface ApplicativeAndIdentifySyntax<F> : ApplicativeSyntax<F>, IdentifySyntax<F>

    fun <F> allSyntax(AP: Applicative<F>, ID: Identity<F>): ApplicativeAndIdentifySyntax<F> =
            object : ApplicativeAndIdentifySyntax<F>, ApplicativeSyntax<F> by AP.s(), IdentifySyntax<F> by ID.s() {}


    ///////////////////////////////////
    //// DEFINING OUR DEPENDENCIES
    ///////////////////////////////////

    // Define some trivial instances for Identify and Applicative

    val ID_LIST: Identity<ForListK> =
            object : Identity<ForListK> {}

    val AP =
            ListK.applicative()

    val ALL_SYNTAX =
            allSyntax(AP, ID_LIST)


    // Functions depending on syntax

    object ScopeOne {
        fun <F> ApplicativeSyntax<F>.inScopeOne(): Kind<F, Int> =
                1.just()
    }

    object ScopeTwo {
        fun <F> IdentifySyntax<F>.withIdentify(a: Kind<F, Int>): Kind<F, Int> =
                a.identify()

        fun <F> ApplicativeSyntax<F>.withApplicative(): Kind<F, Int> =
                1.just().map { inScopeOne() }.map { 1 }

        fun <F> ApplicativeAndIdentifySyntax<F>.withAll(): Kind<F, Int> =
                withIdentify(withApplicative()).identify().map(::identity)
    }

    // It only works inside classes if they extend the syntax, although it's inheritable!

    open class Parent<F>(ALL_SYNTAX: ApplicativeAndIdentifySyntax<F>, val nest: Parent<F>?) : ApplicativeAndIdentifySyntax<F> by ALL_SYNTAX {
        fun inClass() = withAll()

        fun ApplicativeAndIdentifySyntax<F>.insideClass() = withApplicative()

        fun insideClassNested() = nest?.insideClass() ?: insideClass()
    }

    class Child<F>(ALL_SYNTAX: ApplicativeAndIdentifySyntax<F>) : Parent<F>(ALL_SYNTAX, null) {
        fun insideClassFromChild() = insideClass()
    }

    ///////////////////////////////////
    //// USING TYPECLASSLESS STYLE!
    ///////////////////////////////////

    init {
        val expected = listOf(1).k()

        "Injection typeclassless style for functions" {
            ALL_SYNTAX.inScopeOne() shouldBe expected

            ALL_SYNTAX.withApplicative() shouldBe expected

            ALL_SYNTAX.withAll() shouldBe expected
        }

        "Injection typeclassless style for classes" {
            val child = Child(ALL_SYNTAX)

            val parent = Parent(ALL_SYNTAX, child)

            parent.inClass() shouldBe expected

            parent.run { insideClass() } shouldBe expected

            parent.insideClassNested() shouldBe expected

            child.insideClassFromChild() shouldBe expected
        }
    }
}
