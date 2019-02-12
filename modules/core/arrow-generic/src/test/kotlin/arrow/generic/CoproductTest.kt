package arrow.generic

import arrow.core.*
import arrow.generic.coproduct2.Coproduct2
import arrow.generic.coproduct2.cop
import arrow.generic.coproduct2.coproductOf
import arrow.generic.coproduct2.fold
import arrow.generic.coproduct2.select
import arrow.generic.coproduct22.Coproduct22
import arrow.generic.coproduct3.*
import arrow.test.UnitSpec
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class CoproductTest : UnitSpec() {

    init {

        "Coproducts should be generated up to 22" {
            class Proof2(f: Coproduct2<Unit, Unit>) {
                val x = f
            }

            class Proof22(f: Coproduct22<Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit, Unit>) {
                val x = f
            }
        }

        "select should return None if value isn't correct type" {
            val coproduct2 = "String".cop<String, Long>()

            coproduct2.select<Long>() shouldBe None
        }

        "select returns Some if value is correct type" {
            val coproduct2 = "String".cop<String, Long>()

            coproduct2.select<String>() shouldBe Some("String")
        }

        "coproductOf(A) should equal cop<A, B>()" {
            "String".cop<String, Long>() shouldBe coproductOf<String, Long>("String")
        }

        "Coproduct2 fold" {
            val coproduct2 = 100L.cop<Long, Int>()

            coproduct2.fold(
                    { "Long$it" },
                    { "Int$it" }
            ) shouldBe "Long100"
        }

        "Coproduct3 should handle multiple nullable types" {
            val value: String? = null
            val coproduct3 = value.cop<Long, Float?, String?>()

            coproduct3.select<Long>() shouldBe None
            coproduct3.select<Float?>() shouldBe None
            coproduct3.select<String?>() shouldBe None

            coproduct3.fold(
                    { "First" },
                    { "Second" },
                    { "Third" }
            ) shouldBe "Third"
        }

        "Coproduct3 should handle multiple types with generics" {
            val value: Option<String> = "String".some()
            val coproduct3 = value.cop<Option<Long>, Option<Float>, Option<String>>()

            coproduct3.select<Option<Long>>() shouldBe None
            coproduct3.select<Option<Float>>() shouldBe None
            coproduct3.select<Option<String>>() shouldBe value.some()

            coproduct3.fold(
                    { "First" },
                    { "Second" },
                    { "Third" }
            ) shouldBe "Third"
        }

        "cop replacement functions are equivalent" {
            val firstValue = "String"
            val secondValue = 100L
            val thirdValue = none<String>()

            firstValue.cop<String, Long, Option<String>>() shouldBe firstValue.first<String, Long, Option<String>>()
            secondValue.cop<String, Long, Option<String>>() shouldBe secondValue.second<String, Long, Option<String>>()
            thirdValue.cop<String, Long, Option<String>>() shouldBe thirdValue.third<String, Long, Option<String>>()
        }

        "types can be inferred with the extension constructors" {
            fun typeInferenceTest(input: String?): Coproduct3<String, Int, Option<String>> {
                return when {
                    input == null -> none<String>().third()
                    input.length > 100 -> input.length.second()
                    else -> input.first()
                }
            }

            typeInferenceTest(null) shouldBe Third<String, Int, Option<String>>(none())
        }

        "types can be inferred with the ADT constructors" {
            fun typeInferenceTest(input: String?): Coproduct3<String, Int, Option<String>> {
                return when {
                    input == null -> Third(none())
                    input.length > 100 -> Second(input.length)
                    else -> First(input)
                }
            }

            typeInferenceTest(null) shouldBe none<String>().third<String, Int, Option<String>>()
        }

        "Coproduct data classes should be available" {
            val firstValue = "String"
            val secondValue = 100L
            val thirdValue = none<String>()

            First<String, Long, Option<String>>(firstValue) shouldBe firstValue.first<String, Long, Option<String>>()
            Second<String, Long, Option<String>>(secondValue) shouldBe secondValue.second<String, Long, Option<String>>()
            Third<String, Long, Option<String>>(thirdValue) shouldBe thirdValue.third<String, Long, Option<String>>()
        }
    }
}