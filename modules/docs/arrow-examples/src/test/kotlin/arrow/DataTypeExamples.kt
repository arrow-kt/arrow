package arrow

import arrow.Problem.*
import arrow.core.*
import arrow.core.extensions.`try`.applicative.applicative
import arrow.core.extensions.`try`.functor.functor
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.monad.binding
import io.kotlintest.matchers.Matcher
import io.kotlintest.matchers.Result
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FreeSpec
import kotlin.reflect.KClass

/*** Arrow.io documentation as runnable code ***/
class DataTypeExamples : FreeSpec() { init {

  /**
   * Option http://arrow-kt.io/docs/arrow/core/option/
   ***/
  "Option: Some or None?" - {
    val someValue: Option<Int> = Some(42)
    val noneValue: Option<Int> = None

    "getOrElse" {
      someValue.getOrElse { -1 }.shouldBe(42)
      noneValue.getOrElse { -1 }.shouldBe(-1)
    }

    "is it None?" {
      (someValue is None) shouldBe false
      (noneValue is None) shouldBe true
    }

    "When statement" {
      // Option can also be used with when statements:
      val msg = when (someValue) {
        is Some -> "ok"
        None -> "ko"
      }
      msg shouldBe "ok"
    }

    "Functor/Foldable style operations" {
      // An alternative for pattern matching is performing Functor/Foldable style operations.
      // This is possible because an option could be looked at as a collection or foldable structure with either one or zero elements.
      // One of these operations is map. This operation allows us to map the inner value to a different type while preserving the option
      someValue.map { msg -> msg / 6 } shouldBe Some(7)
      noneValue.map { msg -> msg / 6 } shouldBe None
    }

    "Fold" {
      // Fold will extract the value from the option, or provide a default if the value is None
      someValue.fold({ 1 }, { it * 3 }) shouldBe 126
      noneValue.fold({ 1 }, { it * 3 }) shouldBe 1
    }

    "Applicative" {
      // Computing over independent values
      val tuple = Option.applicative().tupled(Option(1), Option("Hello"), Option(20.0))
      tuple shouldBe Some(Tuple3(a = 1, b = "Hello", c = 20.0))

    }

    "Monad" {
      // Computing over dependent values ignoring absence
      val six = binding {
        val a = Option(1).bind()
        val b = Option(1 + a).bind()
        val c = Option(1 + b).bind()
        a + b + c
      }
      six shouldBe Some(6)

      val none = binding {
        val a = Option(1).bind()
        val b = noneValue.bind()
        val c = Option(1 + b).bind()
        a + b + c
      }
      none shouldBe None
    }

  }

  // http://arrow-kt.io/docs/arrow/core/try/
  "Try and recover" - {

    "Old school" {

      val dollars = try {
        playLottery(9)
      } catch (e: AuthorizationException) {
        0
      }
      dollars shouldBe 0
    }

    "Try { .. } " {
      val gain: Try<Int> = Try { playLottery(9) }

      gain shouldBe aFailureOfType(AuthorizationException::class)

      gain.getOrElse { 0 } shouldBe 0

    }

    "filter" {
      // If you want to perform a check on a possible success,
      // you can use filter to convert successful computations in failures if conditions aren’t met:
      val tryJackpot = Try { playLottery(10) }.filter { it > 500 }
      tryJackpot shouldBe aFailureOfType(TryException.PredicateException::class)
    }

    "Recover" {
      val gain = Try { playLottery(99) }
      gain.recover { 0 } shouldBe Try.Success(0)

      gain.recoverWith { Try { playLottery(42) } } shouldBe Try.Success(1000)
    }

    "Fold" {
      // When you want to handle both cases of the computation you can use fold.
      // With fold we provide two functions,
      // one for transforming a failure into a new value,
      // the second one to transform the success value into a new one:
      val gain = Try { playLottery(99) }
      gain.fold({ 4 }, { error("not expected") }) shouldBe 4

      val jackPot = Try { playLottery(42) }
      jackPot.fold({ error("not expected") }, { it * 100 }) shouldBe 100_000
    }

    "Functor" {
      // Transforming the value, if the computation is a success:
      val actual = Try.functor().run { Try { "3".toInt() }.map { it + 1 } }
      actual shouldBe Try.Success(4)
    }

    "Applicative" {
      // Computing over independent values:
      val tryHarder = Try.applicative().tupled(
        Try { "3".toInt() },
        Try { "5".toInt() },
        Try { "nope".toInt() }
      )
      tryHarder shouldBe aFailureOfType(NumberFormatException::class)

    }
  }

  // Either http://arrow.io/docs/arrow/core/either/
  "Either left or right" - {
    fun parse(s: String): ProblemOrInt = Try { Right(s.toInt()) }.getOrElse { Left(invalidInt) }
    fun reciprocal(i: Int): Either<Problem, Double> = when (i) {
      0 -> Left(noReciprocal)
      else -> Either.Right(1.0 / i)
    }

    fun magic(s: String): Either<Problem, String> =
      parse(s).flatMap { reciprocal(it) }.map { it.toString() }

    var either: ProblemOrInt

    "Right" {
      either = Either.right(5)
      either shouldBe Either.Right(5)
      either.getOrElse { 0 } shouldBe 5
      either.map { it + 1 } shouldBe Either.right(6)
      either.flatMap { Either.right(6) } shouldBe Either.right(6)
      either.flatMap { Left(somethingWentWRong) } shouldBe Left(somethingWentWRong)
    }

    "Left" {
      // either is right-biaised
      either = Either.Left(somethingWentWRong)
      either shouldBe Left(somethingWentWRong)
      either.getOrElse { 0 } shouldBe 0
      either.map { it + 1 } shouldBe either
      either.flatMap { Left(somethingExploded) } shouldBe either

    }

    "Either rather than exception" {
      parse("Not an number") shouldBe Left(invalidInt)
      parse("2") shouldBe Either.right(2)
    }

    "Combinators" {
      magic("0") shouldBe Left(noReciprocal)
      magic("Not a number") shouldBe Left(invalidInt)
      magic("1") shouldBe Either.right("1.0")
    }
  }
}

  fun aFailureOfType(expected: KClass<*>): Matcher<Try<Int>> = object : Matcher<Try<Int>> {
    override fun test(value: Try<Int>): Result = when (value) {
      is Success -> Result(false, "Expected a failure, got $value")
      is Failure -> {
        val javaClass = value.exception.javaClass
        Result(expected.java.isAssignableFrom(javaClass), "Expected Try.Failure(${expected.java}), got $value")
      }
    }
  }
}

private typealias ProblemOrInt = Either<Problem, Int>

private enum class Problem(val message: String) {
  somethingWentWRong("Something went wrong"),
  somethingExploded("Something somethingExploded"),
  invalidInt("This is not an integer"),
  noReciprocal("Cannot take noReciprocal of 0.")
}

private open class GeneralException : Exception()

private object NoConnectionException : GeneralException()

private object AuthorizationException : GeneralException()

fun playLottery(guess: Int): Int {
  return when (guess) {
    42 -> 1000 // jackpot
    in 10..41 -> 1
    in 0..9 -> throw AuthorizationException
    else -> throw NoConnectionException
  }
}
