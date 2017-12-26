package arrow

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import arrow.Failure
import arrow.Success
import arrow.laws.EqLaws
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TryTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            functor<TryHK>() shouldNotBe null
            applicative<TryHK>() shouldNotBe null
            monad<TryHK>() shouldNotBe null
            foldable<TryHK>() shouldNotBe null
            traverse<TryHK>() shouldNotBe null
            monadError<TryHK, Throwable>() shouldNotBe null
            eq<Try<Int>>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws { Try { it } },
            MonadErrorLaws.laws(Try.monadError(), Eq.any(), Eq.any()),
            TraverseLaws.laws(Try.traverse(), Try.functor(), ::Success, Eq.any())
        )

        "invoke of any should be success" {
            Try.invoke { 1 } shouldBe Success(1)
        }

        "invoke of exception should be failure" {
            val ex = Exception()
            Try.invoke { throw ex } shouldBe Failure<Any>(ex)
        }

        "filter evaluates predicate" {
            val failure: Try<Int> = Failure(Exception())

            Success(1).filter { true } shouldBe Success(1)
            Success(1).filter { false } shouldBe Failure<Int>(TryException.PredicateException("Predicate does not hold for 1"))
            failure.filter { true } shouldBe failure
            failure.filter { false } shouldBe failure
        }

        "failed tries to swap" {
            val ex = Exception()
            val failure: Try<Int> = Failure(ex)

            Success(1).failed() shouldBe Failure<Int>(TryException.UnsupportedOperationException("Success.failed"))
            failure.failed() shouldBe Success(ex)
        }

        "fold should call left function on Failure" {
            Failure<Int>(Exception()).fold({ 2 }, { 3 }) shouldBe 2
        }

        "fold should call right function on Success" {
            Success(1).fold({ 2 }, { 3 }) shouldBe 3
        }

        "fold should call left function on Success with exception" {
            Success(1).fold({ 2 }, { throw Exception() }) shouldBe 2
        }

        "getOrElse returns default if Failure" {
            Success(1).getOrElse { 2 } shouldBe 1
            Failure<Int>(Exception()).getOrElse { 2 } shouldBe 2
        }

        "recoverWith should modify Failure entity" {
            Success(1).recoverWith { Failure<Int>(Exception()) } shouldBe Success(1)
            Success(1).recoverWith { Success(2) } shouldBe Success(1)
            Failure<Int>(Exception()).recoverWith { Success(2) } shouldBe Success(2)
        }

        "recover should modify Failure value" {
            Success(1).recover { 2 } shouldBe Success(1)
            Failure<Int>(Exception()).recover { 2 } shouldBe Success(2)
        }

        "transform applies left function for Success" {
            Success(1).transform({ Success(2) }, { Success(3) }) shouldBe Success(2)
        }

        "transform applies right function for Failure" {
            Failure<Int>(Exception()).transform({ Success(2) }, { Success(3) }) shouldBe Success(3)
        }

        "Cartesian builder should build products over homogeneous Try" {
            Try.applicative().map(
                    Success("11th"),
                    Success("Doctor"),
                    Success("Who"),
                    { (a, b, c) -> "$a $b $c" }) shouldBe Success("11th Doctor Who")
        }

        "Cartesian builder should build products over heterogeneous Try" {
            Try.applicative().map(
                    Success(13),
                    Success("Doctor"),
                    Success(false),
                    { (a, b, c) -> "${a}th $b is $c" }) shouldBe Success("13th Doctor is false")
        }

        data class DoctorNotFoundException(val msg: String) : Exception()

        "Cartesian builder should build products over Failure Try" {
            Try.applicative().map(
                    Success(13),
                    Failure<Boolean>(DoctorNotFoundException("13th Doctor is coming!")),
                    Success("Who"),
                    { (a, b, c) -> "${a}th $b is $c" }) shouldBe Failure<String>(DoctorNotFoundException("13th Doctor is coming!"))
        }

    }
}
