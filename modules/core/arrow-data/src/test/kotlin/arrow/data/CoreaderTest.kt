package arrow.data

import arrow.core.*
import arrow.instances.id.comonad.comonad
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class CoreaderTest : UnitSpec() {
  init {
    with(Coreader) {

      "map should return mapped value" {
        forAll { num: Int ->
          invoke<Int, Int> { it -> it * 2 }.map { it -> it * 3 }
            .runId(num) == num * 6
        }
      }

      "flatMap should map over the inner value" {
        forAll { num: Int ->
          invoke<Int, Int> { it -> it * 2 }.flatMap { a -> Coreader.just<Int, Int>(Id.comonad(), a * 3) }
            .runId(num) == num * 6
        }
      }

      "bimap should map over both sides of the run function" {
        forAll { num: Int ->
          invoke<Int, Int> { it -> it * 2 }.bimap({ a: String -> Integer.parseInt(a) }, { it.toString() })
            .runId("$num") == "${num * 2}"
        }
      }

      "lmap should map over the left side of the function" {
        forAll { num: Int ->
          invoke<Int, Int> { it -> it * 2 }.lmap { a: String -> Integer.parseInt(a) }
            .runId("$num") == num * 2
        }
      }

      "contramapValue" {
        forAll { num: Int ->
          invoke<Int, Int> { it -> it * 2 }.contramapValue { a: IdOf<Int> -> Id(a.value() * 3) }
            .runId(num) == num * 6
        }
      }

      "reader should lift a reader from any (A) -> B function" {
        val r = { x: Int -> Id(x * 2) }.coreader()
        r::class.java shouldBe Cokleisli::class.java
        r.runId(2).value() shouldBe 4
      }

      "andThen should continue sequence" {
        val cokleisli: Cokleisli<ForId, Int, Int> = invoke(::identity)

        cokleisli.andThen(Id(3)).run(Id(0)) shouldBe 3

        cokleisli.andThen(invoke<Int, Int> { it + 1 }).run(Id(0)) shouldBe 1
      }
    }
  }
}
