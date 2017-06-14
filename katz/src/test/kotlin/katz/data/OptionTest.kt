package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import katz.Option.Some
import katz.Option.None
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTest: UnitSpec() {

    init {

        testLaws(functorLaws<Option.F, Int, Int, Int>())

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
