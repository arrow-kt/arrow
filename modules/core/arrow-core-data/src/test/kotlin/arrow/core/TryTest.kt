package arrow.core

import arrow.core.extensions.`try`.apply.map
import arrow.core.extensions.`try`.eq.eq
import arrow.core.extensions.`try`.functor.functor
import arrow.core.extensions.`try`.hash.hash
import arrow.core.extensions.`try`.monadError.monadError
import arrow.core.extensions.`try`.monoid.monoid
import arrow.core.extensions.`try`.show.show
import arrow.core.extensions.`try`.traverse.traverse
import arrow.core.extensions.combine
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.monoid
import arrow.test.UnitSpec
import arrow.test.generators.`try`
import arrow.test.laws.HashLaws
import arrow.test.laws.MonadErrorLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import io.kotlintest.fail
import io.kotlintest.matchers.beTheSameInstanceAs
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.should
import io.kotlintest.shouldBe

class TryTest : UnitSpec() {

  val success = Try { "10".toInt() }
  val failure = Try { "NaN".toInt() }

  init {

    val EQ = Try.eq(Eq<Any> { a, b -> a::class == b::class }, Eq.any())

    testLaws(
      MonoidLaws.laws(Try.monoid(MO = Int.monoid()), Gen.`try`(Gen.int()), EQ),
      ShowLaws.laws(Try.show(), EQ) { Try.just(it) },
      MonadErrorLaws.laws(Try.monadError(), Eq.any(), Eq.any()),
      TraverseLaws.laws(Try.traverse(), Try.functor(), ::Success, Eq.any()),
      HashLaws.laws(Try.hash(Int.hash(), Hash.any()), Try.eq(Int.eq(), Eq.any())) { Try.just(it) }
    )

    "empty should return a Success of the empty of the inner type" {
      Success(String.monoid().run { empty() }) shouldBe Try.monoid(String.monoid()).run { empty() }
    }

    "combine two Successes should return a Success of the combine of the inners" {
      forAll { a: String, b: String ->
        String.monoid().run { Try.just(a.combine(b)) } == Try.just(a).combine(String.monoid(), Try.just(b))
      }
    }

    "combine two Failures should return the first failure" {
      val throwable1 = Exception("foo")
      val throwable2 = Exception("bar")

      Try.raiseError(throwable1) shouldBe Try.raiseError(throwable1).combine(String.monoid(), Try.raiseError(throwable2))
    }

    "combine a Success and a Failure should return Failure" {
      val throwable = Exception("foo")
      val string = "String"

      Try.raiseError(throwable) shouldBe Try.raiseError(throwable).combine(String.monoid(), Try.just(string))
      Try.raiseError(throwable) shouldBe Try.just(string).combine(String.monoid(), Try.raiseError(throwable))
    }

    "invoke of any should be success" {
      Try.invoke { 1 } shouldBe Success(1)
    }

    "invoke of exception should be failure" {
      val ex = Exception()
      Try.invoke { throw ex } shouldBe Failure(ex)
    }

    "filter evaluates predicate" {
      val failure: Try<Int> = Failure(Exception())

      Success(1).filter { true } shouldBe Success(1)
      Success(1).filter { false } shouldBe Failure(TryException.PredicateException("Predicate does not hold for 1"))
      failure.filter { true } shouldBe failure
      failure.filter { false } shouldBe failure
    }

    "failed tries to swap" {
      val ex = Exception()
      val failure: Try<Int> = Failure(ex)

      Success(1).failed() shouldBe Failure(TryException.UnsupportedOperationException("Success"))
      failure.failed() shouldBe Success(ex)
    }

    "fold should call left function on Failure" {
      Failure(Exception()).fold({ 2 }, { 3 }) shouldBe 2
    }

    "fold should call right function on Success" {
      Success(1).fold({ 2 }, { 3 }) shouldBe 3
    }

    "fold should propagate exception from Success with exception" {
      Exception().let { ex ->
        try {
          Success(1).fold({ 2 }, { throw ex })
        } catch (e: Exception) {
          ex should beTheSameInstanceAs(e)
        }
      }
    }

    "getOrDefault returns default if Failure" {
      Success(1).getOrDefault { 2 } shouldBe 1
      Failure(Exception()).getOrDefault { 2 } shouldBe 2
    }

    "getOrElse returns default if Failure" {
      val e: Throwable = Exception()

      Success(1).getOrElse { 2 } shouldBe 1
      Failure(e).getOrElse { (it shouldBe e); 2 } shouldBe 2
    }

    "orNull returns null if Failure" {
      Success(1).orNull() shouldBe 1

      val e: Throwable = Exception()
      val failure1: Try<Int> = Failure(e)
      failure1.orNull() shouldBe null
    }

    "handleErrorWith should modify Failure entity" {
      Success(1).handleErrorWith { Failure(Exception()) } shouldBe Success(1)
      Success(1).handleErrorWith { Success(2) } shouldBe Success(1)
      Failure(Exception()).handleErrorWith { Success(2) } shouldBe Success(2)
    }

    "handleError should modify Failure value" {
      Success(1).handleError { 2 } shouldBe Success(1)
      Failure(Exception()).handleError { 2 } shouldBe Success(2)
    }

    "toEither with onLeft should return Either.Right with correct right value if Try is Success" {
      Success(1).toEither { "myDomainError" } shouldBe 1.right()
    }

    "toEither with onLeft should return Either.Left with correct left value if Try is Failure" {
      Failure(Exception()).toEither { "myDomainError" } shouldBe "myDomainError".left()
    }

    "Cartesian builder should build products over homogeneous Try" {
      map(
        Success("11th"),
        Success("Doctor"),
        Success("Who")
      ) { (a, b, c) -> "$a $b $c" } shouldBe Success("11th Doctor Who")
    }

    "Cartesian builder should build products over heterogeneous Try" {
      map(
        Success(13),
        Success("Doctor"),
        Success(false)
      ) { (a, b, c) -> "${a}th $b is $c" } shouldBe Success("13th Doctor is false")
    }

    data class DoctorNotFoundException(val msg: String) : Exception()

    "Cartesian builder should build products over Failure Try" {
      map(
        Success(13),
        Failure(DoctorNotFoundException("13th Doctor is coming!")),
        Success("Who")
      ) { (a, b, @Suppress("UNUSED_DESTRUCTURED_PARAMETER_ENTRY") c) ->
        @Suppress("UNREACHABLE_CODE") "${a}th $b is $c"
      } shouldBe Failure(DoctorNotFoundException("13th Doctor is coming!"))
    }

    "show" {
      val problem = success.flatMap { x -> failure.map { y -> x / y } }
      when (problem) {
        is Success -> fail("This should not be possible")
        is Failure -> {
          // Success
        }
      }
    }

    "getOrElse" {
      success.getOrElse { 5 } shouldBe 10
      failure.getOrElse { 5 } shouldBe 5
    }

    "orElse" {
      success.orElse { Success(5) } shouldBe Success(10)
      failure.orElse { Success(5) } shouldBe Success(5)
    }

    "flatMap" {
      success.flatMap { Success(it * 2) } shouldBe Success(20)
      (failure.flatMap { Success(it * 2) }.isFailure()) shouldBe true
    }

    "map" {
      success.map { it * 2 } shouldBe Success(20)
      (failure.map { it * 2 }.isFailure()) shouldBe true
    }

    "exists" {
      (success.exists { it > 5 }) shouldBe true
      (failure.exists { it > 5 }) shouldBe false
    }

    "filter" {
      (success.filter { it > 5 }.isSuccess()) shouldBe true
      (success.filter { it < 5 }.isFailure()) shouldBe true
      (failure.filter { it > 5 }.isSuccess()) shouldBe false
    }

    "toOption" {
      (success.toOption().isDefined()) shouldBe true
      (failure.toOption().isEmpty()) shouldBe true
    }

    "success" {
      10.success() shouldBe success
    }

    "failure" {
      val ex = NumberFormatException()
      ex.failure() shouldBe Failure(ex)
    }

    "flatten" {
      (Try { success }.flatten().isSuccess()) shouldBe true
      (Try { failure }.flatten().isFailure()) shouldBe true
      (Try<Try<Int>> { throw RuntimeException("") }.flatten().isFailure()) shouldBe true
    }
  }
}
