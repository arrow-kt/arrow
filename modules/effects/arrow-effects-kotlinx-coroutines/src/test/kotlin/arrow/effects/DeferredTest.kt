package arrow.effects

import arrow.Kind
import arrow.test.UnitSpec
import arrow.test.generators.genIntSmall
import arrow.test.laws.AsyncLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Unconfined
import kotlinx.coroutines.experimental.runBlocking
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class DeferredKWTest : UnitSpec() {
    fun <A> EQ(): Eq<Kind<ForDeferredKW, A>> = Eq { a, b ->
        a.unsafeAttemptSync() == b.unsafeAttemptSync()
    }

    init {
        testLaws(AsyncLaws.laws(DeferredKW.async(), EQ(), EQ()))

        "instances can be resolved implicitly"{
            functor<ForDeferredKW>() shouldNotBe null
            applicative<ForDeferredKW>() shouldNotBe null
            monad<ForDeferredKW>() shouldNotBe null
            applicativeError<ForDeferredKW, Throwable>() shouldNotBe null
            monadError<ForDeferredKW, Throwable>() shouldNotBe null
            monadSuspend<ForDeferredKW>() shouldNotBe null
            async<ForDeferredKW>() shouldNotBe null
            effect<ForDeferredKW>() shouldNotBe null
        }

        "DeferredKW is awaitable" {
            forAll(genIntSmall(), genIntSmall(), genIntSmall(), { x: Int, y: Int, z: Int ->
                runBlocking {
                    val a = DeferredKW { x }.await()
                    val b = DeferredKW { y + a }.await()
                    val c = DeferredKW { z + b }.await()
                    c
                } == x + y + z
            })
        }

        "should complete when running a pure value with unsafeRunAsync" {
            val expected = 0
            DeferredKW.pure(expected).unsafeRunAsync { either ->
                either.fold({ fail("") }, { it shouldBe expected })
            }
        }

        class MyException : Exception()

        "should return an error when running an exception with unsafeRunAsync" {
            DeferredKW.raiseError<Int>(MyException()).unsafeRunAsync { either ->
                either.fold({
                    when (it) {
                        is MyException -> {
                        }
                        else -> fail("Should only throw MyException")
                    }
                }, { fail("") })
            }
        }

        "should return exceptions within main block with unsafeRunAsync" {
            val exception = MyException()
            val ioa = DeferredKW<Int>(Unconfined, CoroutineStart.DEFAULT) { throw exception }
            ioa.unsafeRunAsync { either ->
                either.fold({ it shouldBe exception }, { fail("") })
            }
        }

        "should not catch exceptions within run block with unsafeRunAsync" {
            try {
                val exception = MyException()
                val ioa = DeferredKW<Int>(Unconfined, CoroutineStart.DEFAULT) { throw exception }
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
            DeferredKW.pure(expected).runAsync { either ->
                either.fold({ fail("") }, { DeferredKW { it shouldBe expected } })
            }
        }


        "should complete when running a return value with runAsync" {
            val expected = 0
            DeferredKW(Unconfined, CoroutineStart.DEFAULT) { expected }.runAsync { either ->
                either.fold({ fail("") }, { DeferredKW { it shouldBe expected } })
            }
        }

        "should return an error when running an exception with runAsync" {
            DeferredKW.raiseError<Int>(MyException()).runAsync { either ->
                either.fold({
                    when (it) {
                        is MyException -> {
                            DeferredKW { }
                        }
                        else -> fail("Should only throw MyException")
                    }
                }, { fail("") })
            }
        }

        "should return exceptions within main block with runAsync" {
            val exception = MyException()
            val ioa = DeferredKW<Int>(Unconfined, CoroutineStart.DEFAULT) { throw exception }
            ioa.runAsync { either ->
                either.fold({ DeferredKW { it shouldBe exception } }, { fail("") })
            }
        }

        "should catch exceptions within run block with runAsync" {
            try {
                val exception = MyException()
                val ioa = DeferredKW<Int>(Unconfined, CoroutineStart.DEFAULT) { throw exception }
                ioa.runAsync { either ->
                    either.fold({ throw it }, { fail("") })
                }.unsafeRunSync()
                fail("Should rethrow the exception")
            } catch (throwable: AssertionError) {
                fail("${throwable.message}")
            } catch (throwable: Throwable) {
                // Success
            }
        }
    }
}
