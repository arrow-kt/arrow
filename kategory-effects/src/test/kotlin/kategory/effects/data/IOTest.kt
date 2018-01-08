package kategory.effects

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import kategory.Eq
import kategory.HK
import kategory.UnitSpec
import kategory.newCoroutineDispatcher
import kotlinx.coroutines.experimental.newSingleThreadContext
import org.junit.runner.RunWith
import kotlin.coroutines.experimental.EmptyCoroutineContext

@RunWith(KTestJUnitRunner::class)
class IOTest : UnitSpec() {
    fun <A> EQ(): Eq<HK<IOHK, A>> = Eq { a, b ->
        try {
            val unsafeRunSync = a.ev().attempt().unsafeRunSync()
            val unsafeRunSync1 = b.ev().attempt().unsafeRunSync()
            val result = unsafeRunSync == unsafeRunSync1
            if (!result) {
                println("$unsafeRunSync    <------->     $unsafeRunSync1")
            }
            result
        } catch (t: Throwable) {
            println("We exploded mate $t")
            val unsafeRunSync = a.ev().attempt().unsafeRunSync()
            print("$unsafeRunSync     <------------->     ")
            val unsafeRunSync1 = b.ev().attempt().unsafeRunSync()
            println(unsafeRunSync1)
            false
        }
    }

    fun delayedIO(name: String, delay: Long): Fiber<IOHK, Long> =
            IO.monadSuspend().bindingFiber(IO.asyncContext(), newCoroutineDispatcher("BLA $name")) {
                println("Before suspend ${ct()}")
                //IO.suspend { IO.pure(Unit) }.bind()
                println("After suspend ${ct()}")
                val a = bindIn(newSingleThreadContext(name)) {
                    println("Inside bindIn ${ct()}")
                    Thread.sleep(delay)
                    println("After sleep ${ct()}")
                    delay
                }
                println("Ready to yield")
                yields(a)
            }

    fun emptyIO(name: String, delay: Long): Fiber<IOHK, Long> =
            IO.monadSuspend().bindingFiber(IO.asyncContext(), EmptyCoroutineContext) {
                val a = bindInM(newSingleThreadContext(name)) { IO.empty() }
                yields(1L)
            }

    init {
        //testLaws(AsyncLaws.laws(IO.asyncContext(), IO.monadSuspend(), EQ<Any?>().logged(), EQ<Any?>().logged()))
        //testLaws(MonadSuspendLaws.laws(IO.monadSuspend(), IO.asyncContext(), EQ<Any?>().logged(), EQ<Any?>().logged()))

        "amazing shit" {
            IO.monadSuspend().bindingFiber(IO.asyncContext(), newCoroutineDispatcher("BLE")) {
                println("Before parallel ${ct()}")
                val result = bindParallel(delayedIO("1", 3000), delayedIO("2", 1000))
                println("After parallel ${ct()}")
                yields(result)
            }.binding.ev().unsafeRunSync().let { (a, b) -> a + b } shouldBe 4000.toLong()
        }

        /*
                "amazing races" {
                    IO.monadSuspend().bindingFiber(IO.asyncContext()) {
                        val result: Either<Long, Long> = bindRace(newCoroutineDispatcher("BLA"), delayedIO("2", 5000), delayedIO("1", 1000))
                        yields(result)
                    }.binding.ev().unsafeRunSync() shouldBe 1000.toLong().right()
                }

                        //testLaws(AsyncLaws.laws(IO.asyncContext(), IO.monadError(), EQ(), EQ()))

                                "instances can be resolved implicitly" {
                                    functor<IOHK>() shouldNotBe null
                                    applicative<IOHK>() shouldNotBe null
                                    monad<IOHK>() shouldNotBe null
                                    monadError<IOHK, Throwable>() shouldNotBe null
                                    asyncContext<IOHK>() shouldNotBe null
                                    semigroup<IOKind<Int>>() shouldNotBe null
                                    monoid<IOKind<Int>>() shouldNotBe null
                                }

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

                                    val expected = Left(exception)

                                    result shouldBe expected
                                }

                                "should yield immediate successful invoke value" {
                                    val run = IO { 1 }.unsafeRunSync()

                                    val expected = 1

                                    run shouldBe expected
                                }

                                "should yield immediate successful pure value" {
                                    val run = IO.pure(1).unsafeRunSync()

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
                                    val never = IO.runAsync<Int> { Unit }
                                    val start = System.currentTimeMillis()
                                    val received = never.unsafeRunTimed(100.milliseconds)
                                    val elapsed = System.currentTimeMillis() - start

                                    received shouldBe None
                                    (elapsed >= 100) shouldBe true
                                }

                                "should return a null value from unsafeRunTimed" {
                                    val never = IO.pure<Int?>(null)
                                    val received = never.unsafeRunTimed(100.milliseconds)

                                    received shouldBe Some(null)
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
                                                is MyException -> {
                                                }
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
                                                is MyException -> {
                                                    IO { }
                                                }
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
                                            either.fold({ throw it }, { fail("") })
                                        }.unsafeRunSync()
                                        fail("Should rethrow the exception")
                                    } catch (throwable: AssertionError) {
                                        fail("${throwable.message}")
                                    } catch (throwable: Throwable) {
                                        // Success
                                    }
                                }

                                "should map values correctly on success" {
                                    val run = IO.functor().map(IO.pure(1)) { it + 1 }.unsafeRunSync()

                                    val expected = 2

                                    run shouldBe expected
                                }

                                "should flatMap values correctly on success" {
                                    val run = IO.monad().flatMap(IO.pure(1)) { num -> IO { num + 1 } }.unsafeRunSync()

                                    val expected = 2

                                    run shouldBe expected
                                }

                                "invoke is called on every run call" {
                                    val sideEffect = SideEffect()
                                    val io = IO { sideEffect.increment(); 1 }
                                    io.unsafeRunSync()
                                    io.unsafeRunSync()

                                    sideEffect.counter shouldBe 2
                                }

                                "unsafeRunTimed times out with None result" {
                                    val never = IO.empty()
                                    val result = never.unsafeRunTimed(100.milliseconds)
                                    result shouldBe None
                                }

                                "IO.binding should for comprehend over IO" {
                                    val result = IO.monad().binding {
                                        val x = IO.pure(1).bind()
                                        val y = bind { IO { x + 1 } }
                                        yields(y)
                                    }.ev()
                                    result.unsafeRunSync() shouldBe 2
                                }
                                    */
    }

    private fun ct() = Thread.currentThread().name

}