package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import kategory.Option.None
import kategory.Option.Some
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTest : UnitSpec() {

    init {

        testLaws(MonadLaws.laws(Option, Eq.any()))
        testLaws(TraverseLaws.laws(Option, Option, ::Some, Eq.any()))
        testLaws(MonoidKLaws.laws(
                OptionMonoidK(),
                Option.applicative(),
                Option(1),
                Eq.any(),
                Eq.any()))

        "map should modify value" {
            Some(12).map { "flower" } shouldBe Some("flower")
            None.map { "flower" } shouldBe None
        }

        "flatMap should modify entity" {
            Some(1).flatMap { None } shouldBe None
            Some(1).flatMap { Some("something") } shouldBe Some("something")
            None.flatMap { Some("something") } shouldBe None
        }

        "getOrElse should return value" {
            Some(12).getOrElse { 17 } shouldBe 12
            None.getOrElse { 17 } shouldBe 17
        }

        "exits should evaluate value" {
            val none: Option<Int> = None

            Some(12).exists { it > 10 } shouldBe true
            Some(7).exists { it > 10 } shouldBe false
            none.exists { it > 10 } shouldBe false
        }

        "fold should return default value on None" {
            val exception = Exception()
            val result: Option<String> = None
            result.fold(
                    { exception },
                    { fail("Some should not be called") }
            ) shouldBe exception
        }

        "fold should call function on Some" {
            val value = "Some value"
            val result: Option<String> = Some(value)
            result.fold(
                    { fail("None should not be called") },
                    { value }
            ) shouldBe value
        }

        "fromNullable should work for both null and non-null values of nullable types" {
            forAll { a: Int? ->
                // This seems to be generating only non-null values, so it is complemented by the next test
                val o: Option<Int> = Option.fromNullable(a)
                if (a == null) o == None else o == Some(a)
            }
        }

        "fromNullable should return none for null values of nullable types" {
            val a: Int? = null
            Option.fromNullable(a) shouldBe None
        }

        "Option.monad.flatMap should be consistent with Option#flatMap" {
            forAll { a: Int ->
                val x = { b: Int -> Option(b * a) }
                val option = Option(a)
                option.flatMap(x) == Option.flatMap(option, x)
            }
        }

        "Option.functor.void should return Unit" {
            forAll { a: Int ->
                Option.void(Option(a)) == Some(Unit)
            }
        }

        "Option.functor.as should change its value" {
            forAll { a: Int ->
                Option.`as`(Option("1"), a) == Some(a)
            }
        }

        "Option.functor.fproduct should return a tuple of the current value and the transformed" {
            forAll { a: Int ->
                Option.fproduct(Option(a), { it + 1 }) == Some(Tuple2(a, a + 1))
            }
        }

        "Option.functor.tupleLeft should return a tuple the current value and the value passed" {
            forAll { a: Int ->
                Option.tupleLeft(Option(a), a + 1) == Some(Tuple2(a + 1, a))
            }
        }

        "Option.functor.tupleRight should return a tuple the current value and the value passed" {
            forAll { a: Int ->
                Option.tupleRigth(Option(a), a + 1) == Some(Tuple2(a, a + 1))
            }
        }

        "Option.functor.widen should cast the result to other super type" {
            val x: Option<Any> = Option.widen(Option("1")).ev()

            x should { x -> x is Option<Any> }
        }

        "Option.monad.binding should for comprehend over option" {
            val result = Option.binding {
                val x = !Option(1)
                val y = Option(1).bind()
                val z = bind { Option(1) }
                yields(x + y + z)
            }
            result shouldBe Option(3)
        }

        "Cartesian builder should build products over option" {
            Option.map(Option(1), Option("a"), Option(true), { (a, b, c) ->
                "$a $b $c"
            }) shouldBe Option("1 a true")
        }

        "Cartesian builder works inside for comprehensions" {
            val result = Option.binding {
                val (x, y, z) = !Option.tupled(Option(1), Option(1), Option(1))
                val a = Option(1).bind()
                val b = bind { Option(1) }
                yields(x + y + z + a + b)
            }
            result shouldBe Option(5)
        }

    }

}
