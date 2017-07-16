package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IOTest : UnitSpec() {

    init {

        testLaws(MonadLaws.laws(IO, object : Eq<HK<IO.F, Int>> {
            override fun eqv(a: HK<IO.F, Int>, b: HK<IO.F, Int>): Boolean =
                a.ev().unsafeRunSync() == b.ev().unsafeRunSync()
        }))

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
            val result: Either<Throwable, Nothing> = ioa.attempt().unsafeRunSync()

            val expected = Either.Left(exception)

            result shouldBe expected
        }

        "should yield immediate successful invoke value" {
            val run = IO { 1 }.unsafeRunSync()

            val expected = 1

            run shouldBe expected
        }

        "should yield immediate successful just value" {
            val run = IO.just(1).unsafeRunSync()

            val expected = 1

            run shouldBe expected
        }

        "should yield immediate successful pure value" {
            val run = IO.pure(1).unsafeRunSync()

            val expected = 1

            run shouldBe expected
        }

        "should throw immediate failure by raiseError" {
            try {
                IO.raiseError<Int>(MyException()).unsafeRunSync()
                fail("")
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

        "should return a null value from unsafeRunTimed" {
            val never = IO.pure<Int?>(null)
            val received = never.unsafeRunTimed(100.milliseconds)

            received shouldBe Option.Some(null)
        }

        "should return a null value from unsafeRunSync" {
            val value = IO.pure<Int?>(null).unsafeRunSync()

            value shouldBe null
        }

        "should complete when running a pure value with unsafeRunAsync" {
            val expected = 0
            IO.pure(expected).unsafeRunAsync { either ->
                either.fold({ fail("") }, { it shouldBe expected })
            }
        }


        "should complete when running a return value with unsafeRunAsync" {
            val expected = 0
            IO { expected }.unsafeRunAsync { either ->
                either.fold({ fail("") }, { it shouldBe expected })
            }
        }

        "should return an error when running an exception with unsafeRunAsync" {
            IO.raiseError<Int>(MyException()).unsafeRunAsync { either ->
                either.fold({
                    when (it) {
                        is MyException -> {}
                        else -> fail("Should only throw MyException")
                    }
                }, { fail("") })
            }
        }

        "should return exceptions within main block with unsafeRunAsync" {
            val exception = MyException()
            val ioa = IO<Int> { throw exception }
            ioa.unsafeRunAsync { either ->
                either.fold({ it shouldBe exception }, { fail("") })
            }
        }

        "should not catch exceptions within run block with unsafeRunAsync" {
            try {
                val exception = MyException()
                val ioa = IO<Int> { throw exception }
                ioa.unsafeRunAsync { either ->
                    either.fold({ throw exception }, { fail("") })
                }
            } catch (myException: MyException) {
                // Success
            } catch (throwable: Throwable) {
                fail("Should only throw MyException")
            }
        }

        "should complete when running a pure value with runAsync" {
            val expected = 0
            IO.pure(expected).runAsync { either ->
                either.fold({ fail("") }, { IO { it shouldBe expected } })
            }
        }


        "should complete when running a return value with runAsync" {
            val expected = 0
            IO { expected }.runAsync { either ->
                either.fold({ fail("") }, { IO { it shouldBe expected } })
            }
        }

        "should return an error when running an exception with runAsync" {
            IO.raiseError<Int>(MyException()).runAsync { either ->
                either.fold({
                    when (it) {
                        is MyException -> { IO { } }
                        else -> fail("Should only throw MyException")
                    }
                }, { fail("") })
            }
        }

        "should return exceptions within main block with runAsync" {
            val exception = MyException()
            val ioa = IO<Int> { throw exception }
            ioa.runAsync { either ->
                either.fold({ IO { it shouldBe exception } }, { fail("") })
            }
        }

        "should catch exceptions within run block with runAsync" {
            try {
                val exception = MyException()
                val ioa = IO<Int> { throw exception }
                ioa.runAsync { either ->
                    either.fold({ throw exception }, { fail("") })
                }
            } catch (throwable: Throwable) {
                fail("Should catch any exception")
            }
        }

        "should map values correctly on success" {
            val run = IO.map(IO.pure(1)) { it + 1 }.unsafeRunSync()

            val expected = 2

            run shouldBe expected
        }

        "should flatMap values correctly on success" {
            val run = IO.flatMap(IO.pure(1)) { num -> IO { num + 1 } }.unsafeRunSync()

            val expected = 2

            run shouldBe expected
        }

        "IO.binding should for comprehend over IO" {
            val result = IO.binding {
                val x = IO.pure(1).bind()
                val y = !IO { x + 1 }
                val z = bind { IO { y + 1 } }
                yields(z)
            }.ev()
            result.unsafeRunSync() shouldBe 3
        }
    }
}