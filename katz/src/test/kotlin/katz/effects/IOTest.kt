package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IOTest : UnitSpec() {

    init {
        "should defer evaluation until run" {
            var run = false
            val ioa = IO { run = true }
            run shouldEqual false
            ioa.unsafeRunSync()
            run shouldEqual true
        }

        class MyException : Exception()

        "should catch exceptions within main block" {
            val exception = MyException()
            val ioa = IO { throw exception }
            val result: Option<Either<Throwable, Nothing>> = ioa.attempt().unsafeRunSync()

            val expected = Option.Some(Either.Left(exception))

            result shouldBe expected
        }

        "should yield immediate successful invoke value" {
            val run = IO { 1 }.unsafeRunSync()

            val expected = Option.Some(1)

            run shouldBe expected
        }

        "should yield immediate successful just value" {
            val run = IO.just(1).unsafeRunSync()

            val expected = Option.Some(1)

            run shouldBe expected
        }

        "should yield immediate successful pure value" {
            val run = IO.pure(1).unsafeRunSync()

            val expected = Option.Some(1)

            run shouldBe expected
        }

        "should throw immediate failure by raiseError" {
            try {
                IO.raiseError<Int>(MyException()).unsafeRunSync()
                fail("Should throw MyException")
            } catch (myException: MyException) {
                // Success
            } catch (throwable: Throwable) {
                fail("Should only throw MyException")
            }
        }

        "should time out on unending unsafeRunTimed" {
            val never = IO.async<Int> { Unit }
            val start = System.currentTimeMillis()
            val received = never.unsafeRunTimed(100.milliseconds)
            val elapsed = System.currentTimeMillis() - start

            received shouldBe Option.None
            (elapsed >= 100) shouldBe true
        }
    }
}