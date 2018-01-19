package arrow.typeclasses

import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

import arrow.typeclasses.*
import arrow.*
import arrow.core.None
import arrow.core.Option
import arrow.core.monoid
import arrow.data.ListKW
import arrow.data.k
import arrow.data.semigroup
import arrow.syntax.monoid.empty
import arrow.syntax.monoid.*


/**
 * See [http://arrow-kt.io/docs/typeclasses/semigroup/](http://arrow-kt.io/docs/typeclasses/semigroup/)
 *
 * A [Semigroup] for some given type A has a single operation (which we will call `combine`),
 * which takes two values of type A, and returns a value of type A.
 *
 * [Monoid] extends the Semigroup type class, adding an empty method to semigroup’s combine.
 * The `empty` method must return a value that when combined with any other instance of that type returns the other instance
 **/
class SemigroupMonoidExample : StringSpec() { init {


    "Semigroups must be associative" {
        val semiGroup = semigroup<Int>()
        forAll { i: Int, j: Int ->
            semiGroup.combine(i, j) == semiGroup.combine(j, i)
        }
    }

    "Empty method" {

        monoid<Int>().empty() shouldBe 0
        monoid<String>().empty() shouldBe ""

        //  The empty method must return a value that when combined with any other instance of that type returns the other instance, i.e.
        val Monoid: Monoid<String> = monoid<String>()
        forAll { s: String ->
            Monoid.combine(s, Monoid.empty()) == s
        }
    }

    "combine()" {
        semigroup<Int>().combine(1, 2) shouldBe 3

        ListKW.semigroup<Int>().combine(
            listOf(1, 2, 3).k(),
            listOf(4, 5, 6).k()
        ) shouldBe listOf(1, 2, 3, 4, 5, 6).k()

        Option.monoid<Int>().combine(
            Option(1),
            Option(2)
        ) shouldBe Option(3)


        Option.monoid<Int>().combine(
            Option(1),
            None
        ) shouldBe None

    }

    "combineAll()" {
        listOf("Λ", "R", "R", "O", "W").combineAll() shouldBe "ΛRROW"
    }

}
}