package arrow

import arrow.Problem.invalidInt
import arrow.Problem.noReciprocal
import arrow.Problem.somethingExploded
import arrow.Problem.somethingWentWRong
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.handleError
import arrow.core.handleErrorWith
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/*** Arrow.io documentation as runnable code ***/
class DataTypeExamples : StringSpec() {

  init {
    /**
     * Option http://arrow-kt.io/docs/apidocs/arrow-core/arrow.core/-option/
     ***/
    val someValue: Option<Int> = Some(42)
    val noneValue: Option<Int> = None

    "Option: Some or None? - getOrElse" {
      someValue.getOrElse { -1 }.shouldBe<Int, Int>(42)
      noneValue.getOrElse { -1 }.shouldBe(-1)
    }

    "Option: Some or None? - is it None?" {
      (someValue is None) shouldBe false
      (noneValue is None) shouldBe true
    }

    "Option: Some or None? - When statement" {
      // Option can also be used with when statements:
      val msg = when (someValue) {
        is Some -> "ok"
        None -> "ko"
      }
      msg shouldBe "ok"
    }

    "Option: Some or None? - Functor/Foldable style operations" {
      // An alternative for pattern matching is performing Functor/Foldable style operations.
      // This is possible because an option could be looked at as a collection or foldable structure with either one or zero elements.
      // One of these operations is map. This operation allows us to map the inner value to a different type while preserving the option
      someValue.map { msg -> msg / 6 } shouldBe Some(7)
      noneValue.map { msg -> msg / 6 } shouldBe None
    }

    "Option: Some or None? - Fold" {
      // Fold will extract the value from the option, or provide a default if the value is None
      someValue.fold({ 1 }, { it * 3 }) shouldBe 126
      noneValue.fold({ 1 }, { it * 3 }) shouldBe 1
    }

    // Either http://arrow.io/docs/apidocs/arrow-core/arrow.core/-either/
    fun parse(s: String): ProblemOrInt = try {
      Right(s.toInt())
    } catch (e: Throwable) {
      Left(invalidInt)
    }

    fun reciprocal(i: Int): Either<Problem, Double> = when (i) {
      0 -> Left(noReciprocal)
      else -> Right(1.0 / i)
    }

    fun magic(s: String): Either<Problem, String> =
      parse(s).flatMap { reciprocal(it) }.map { it.toString() }

//            var

    "Either left or right - Right" {
      val either: ProblemOrInt = Right(5)
      either shouldBe Right(5)
      either.getOrElse { 0 } shouldBe 5
      either.map { it + 1 } shouldBe Right(6)
      either.flatMap { Right(6) } shouldBe Right(6)
      either.flatMap { Left(somethingWentWRong) } shouldBe Left(somethingWentWRong)
    }

    "Either left or right - Left" {
      // either is right-biased
      val either: ProblemOrInt = Either.Left(somethingWentWRong)
      either shouldBe Left(somethingWentWRong)
      either.getOrElse { 0 } shouldBe 0
      either.map { it + 1 } shouldBe either
      either.flatMap { Left(somethingExploded) } shouldBe either
    }

    "Either left or right - Either rather than exception" {
      parse("Not an number") shouldBe Left(invalidInt)
      parse("2") shouldBe Right(2)
    }

    "Either left or right - Combinators" {
      magic("0") shouldBe Left(noReciprocal)
      magic("Not a number") shouldBe Left(invalidInt)
      magic("1") shouldBe Right("1.0")
    }

    "Either left or right - Fold" {
      // When you want to handle both cases of the computation you can use fold.
      // With fold we provide two functions,
      // one for transforming a failure into a new value,
      // the second one to transform the success value into a new one:
      val gain = Either.catch { playLottery(99) }
      gain.fold({ 4 }, { error("not expected") }) shouldBe 4

      val jackPot = Either.catch { playLottery(42) }
      jackPot.fold({ error("not expected") }, { it * 100 }) shouldBe 100_000
    }

    "Try and recover -Old school" {
      val dollars = try {
        playLottery(9)
      } catch (e: AuthorizationException) {
        0
      }
      dollars shouldBe 0
    }

    "Try and recover -Either.catch { .. } " {
      val gain: Either<Throwable, Int> = Either.catch { playLottery(9) }

      gain shouldBe Left(AuthorizationException)

      gain.getOrElse { 0 } shouldBe 0
    }

    "Try and recover -Recover" {
      val gain = Either.catch { playLottery(99) }
      gain.handleError { 0 } shouldBe Right(0)
      gain.handleErrorWith {
        try {
          Right(playLottery(42))
        } catch (e: Throwable) {
          Either.Left(e)
        }
      } shouldBe Right(1000)
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
