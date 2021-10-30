import arrow.core.Either
import arrow.core.Ior
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class IorSpec : StringSpec({
  "Accumulates" {
    ior(Semigroup.string()) {
      val one = Ior.Both("Hello", 1).bind()
      val two = Ior.Both(", World!", 2).bind()
      one + two
    } shouldBe Ior.Both("Hello, World!", 3)
  }

  "Accumulates with Either" {
    ior(Semigroup.string()) {
      val one = Ior.Both("Hello", 1).bind()
      val two: Int = Either.Left(", World!").bind()
      one + two
    } shouldBe Ior.Left("Hello, World!")
  }
})
