package arrow.validation

import arrow.core.Either
import arrow.test.UnitSpec
import io.kotlintest.shouldBe

data class ExampleForValidation(val number: Int, val text: String)

class ValidationTest : UnitSpec() {

  init {

    "validationTest" {
      val d1 = Either.right(1)
      val d2 = Either.right(2)
      val d3 = Either.right(3)

      val validation = Validation(d1, d2, d3)
      (validation.hasFailures) shouldBe false
      validation.failures shouldBe listOf<String>()
    }

    "validationTestWithError" {
      val d1 = Either.right(1)
      val d2 = Either.left("Not a number")
      val d3 = Either.right(3)

      val validation = Validation(d1, d2, d3)
      (validation.hasFailures) shouldBe true
      validation.failures shouldBe listOf("Not a number")
    }

    "validate2Test" {
      val r1 = Either.right(1)
      val r2 = Either.right("blahblah")
      val l1 = Either.left("fail1")
      val l2 = Either.left("fail2")
      validate(r1, r2, ::ExampleForValidation) shouldBe Either.right(ExampleForValidation(1, "blahblah"))
      validate(r1, l2, ::ExampleForValidation) shouldBe Either.left(listOf("fail2"))
      validate(l1, l2, ::ExampleForValidation) shouldBe Either.left(listOf("fail1", "fail2"))
    }
  }
}
