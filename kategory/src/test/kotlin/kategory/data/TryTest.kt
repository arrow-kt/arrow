package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import kategory.Try.Success
import kategory.Try.Failure
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TryTest : UnitSpec() {

    init {

        testLaws(MonadErrorLaws.laws(Try, Eq()))

        "invoke of any should be success" {
            Try.invoke { 1 } shouldBe Success(1)
        }

        "invoke of exception should be failure" {
            val ex = Exception()
            Try.invoke { throw ex } shouldBe Failure<Any>(ex)
        }

        "flatMap should modify entity" {
            val failure: Try<Int> = Failure(Exception())

            Success(1).flatMap { failure } shouldBe failure
            Success(1).flatMap { Success(2) } shouldBe Success(2)
            failure.flatMap { Success(2) } shouldBe failure
        }

        "map should modify value" {
            val failure: Try<Int> = Failure(Exception())

            Success(1).map { 2 } shouldBe Success(2)
            failure.map { 2 } shouldBe failure
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
            Try.map(
                    Success("11th"),
                    Success("Doctor"),
                    Success("Who"),
                    { (a, b, c) -> "$a $b $c" }) shouldBe Success("11th Doctor Who")
        }

        "Cartesian builder should build products over heterogeneous Try" {
            Try.map(
                    Success(13),
                    Success("Doctor"),
                    Success(false),
                    { (a, b, c) -> "${a}th $b is $c" }) shouldBe Success("13th Doctor is false")
        }

        data class DoctorNotFoundException(val msg: String) : Exception()

        "Cartesian builder should build products over Failure Try" {
            Try.map(
                    Success(13),
                    Failure<Boolean>(DoctorNotFoundException("13th Doctor is coming!")),
                    Success("Who"),
                    { (a, b, c) -> "${a}th $b is $c" }) shouldBe Failure<String>(DoctorNotFoundException("13th Doctor is coming!"))
        }

        "Cartesian builder works inside for comprehensions over Try" {
            val result = Try.bindingE {
                val (x, y, z) = !Try.tupled(Try.pure(1), Try.pure(1), Try.pure(1))
                val a = Try.pure(1).bind()
                val b = bind { Try.pure(1) }
                yields(x + y + z + a + b)
            }
            result shouldBe Success(5)
        }

        "Cartesian builder works inside for comprehensions over Try with fail fast behaviour" {
            val result = Try.bindingE {
                val (x, y, z) = !Try.tupled(Try.pure(1), Try.pure(1), Try.pure(1))
                val failure1: Try<Int> = Failure(DoctorNotFoundException("13th Doctor is coming!"))
                val failure2: Try<Int> = Failure(DoctorNotFoundException("14th Doctor is not found"))
                val a = failure1.bind()
                val b = bind { failure2 }
                yields(x + y + z + a + b)
            }
            result shouldBe Failure<Int>(DoctorNotFoundException("13th Doctor is coming!"))
        }

        "Cartesian builder works inside for comprehensions over Try and raise errors" {
            val result = Try.bindingE {
                val (x, y, z) = !Try.tupled(Try.pure(1), Try.pure(1), Try.pure(1))
                val nullable: String? = null
                yields(x + y + z + nullable!!.toInt())
            }

            assert(result is Failure<Int>)
            assert((result as Failure<Int>).exception is KotlinNullPointerException)
        }
    }
}
