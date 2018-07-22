package arrow.effects

import arrow.Kind
import arrow.core.Option
import arrow.core.eq
import arrow.effects.typeclasses.seconds
import arrow.test.UnitSpec
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import kotlinx.coroutines.experimental.newSingleThreadContext
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IOTest : UnitSpec() {
  val EQ_OPTION = Option.eq(Eq.any())

  fun <A> EQ(): Eq<Kind<ForIO, A>> {
    return Eq { a, b ->
      EQ_OPTION.run {
        a.fix().attempt().unsafeRunTimed(60.seconds).eqv(b.fix().attempt().unsafeRunTimed(60.seconds))
      }
    }
  }

  private fun makePar(num: Int) =
    IO.async<Int> { cc ->
      IO.unit.continueOn(newSingleThreadContext("$num"))
        .map(::hey)
        .map {
          val delay = (Math.random() * 100).toLong()
          Thread.sleep(delay)
          delay
        }
        .map {
          println("I am $num from ${Thread.currentThread().name} after $it ms delay")
          num
        }.unsafeRunAsync(cc)
    }

  private fun <A> hey(a: A): A {
    println("I am $a in ${Thread.currentThread().name}"); return a
  }

  init {
    "test parallel" {
      IO.parMap5(newSingleThreadContext("all"), IO.just(50).map(::hey), IO {
        Thread.sleep(101)
        1
      }.map(::hey), makePar(3), makePar(4), IO.just(0).map(::hey))
      { _, _, _, _, _ ->
        hey(-1)
        100
      }.unsafeRunSync()
    }

    //    testLaws(AsyncLaws.laws(IO.async(), EQ(), EQ()))
    //
    //    "should defer evaluation until run" {
    //      var run = false
    //      val ioa = IO { run = true }
    //      run shouldEqual false
    //      ioa.unsafeRunSync()
    //      run shouldEqual true
    //    }
    //
    //    class MyException : Exception()
    //
    //    "should catch exceptions within main block" {
    //      val exception = MyException()
    //      val ioa = IO { throw exception }
    //      val result: Either<Throwable, Nothing> = ioa.attempt().unsafeRunSync()
    //
    //      val expected = Left(exception)
    //
    //      result shouldBe expected
    //    }
    //
    //    "should yield immediate successful invoke value" {
    //      val run = IO { 1 }.unsafeRunSync()
    //
    //      val expected = 1
    //
    //      run shouldBe expected
    //    }
    //
    //    "should yield immediate successful pure value" {
    //      val run = IO.just(1).unsafeRunSync()
    //
    //      val expected = 1
    //
    //      run shouldBe expected
    //    }
    //
    //    "should yield immediate successful pure value" {
    //      val run = IO.just(1).unsafeRunSync()
    //
    //      val expected = 1
    //
    //      run shouldBe expected
    //    }
    //
    //    "should throw immediate failure by raiseError" {
    //      try {
    //        IO.raiseError<Int>(MyException()).unsafeRunSync()
    //        fail("")
    //      } catch (myException: MyException) {
    //        // Success
    //      } catch (throwable: Throwable) {
    //        fail("Should only throw MyException")
    //      }
    //    }
    //
    //    "should time out on unending unsafeRunTimed" {
    //      val never = IO.async<Int> { Unit }
    //      val start = System.currentTimeMillis()
    //      val received = never.unsafeRunTimed(100.milliseconds)
    //      val elapsed = System.currentTimeMillis() - start
    //
    //      received shouldBe None
    //      (elapsed >= 100) shouldBe true
    //    }
    //
    //    "should return a null value from unsafeRunTimed" {
    //      val never = IO.just<Int?>(null)
    //      val received = never.unsafeRunTimed(100.milliseconds)
    //
    //      received shouldBe Some(null)
    //    }
    //
    //    "should return a null value from unsafeRunSync" {
    //      val value = IO.just<Int?>(null).unsafeRunSync()
    //
    //      value shouldBe null
    //    }
    //
    //    "should complete when running a pure value with unsafeRunAsync" {
    //      val expected = 0
    //      IO.just(expected).unsafeRunAsync { either ->
    //        either.fold({ fail("") }, { it shouldBe expected })
    //      }
    //    }
    //
    //
    //    "should complete when running a return value with unsafeRunAsync" {
    //      val expected = 0
    //      IO { expected }.unsafeRunAsync { either ->
    //        either.fold({ fail("") }, { it shouldBe expected })
    //      }
    //    }
    //
    //    "should return an error when running an exception with unsafeRunAsync" {
    //      IO.raiseError<Int>(MyException()).unsafeRunAsync { either ->
    //        either.fold({
    //          when (it) {
    //            is MyException -> {
    //            }
    //            else -> fail("Should only throw MyException")
    //          }
    //        }, { fail("") })
    //      }
    //    }
    //
    //    "should return exceptions within main block with unsafeRunAsync" {
    //      val exception = MyException()
    //      val ioa = IO<Int> { throw exception }
    //      ioa.unsafeRunAsync { either ->
    //        either.fold({ it shouldBe exception }, { fail("") })
    //      }
    //    }
    //
    //    "should not catch exceptions within run block with unsafeRunAsync" {
    //      try {
    //        val exception = MyException()
    //        val ioa = IO<Int> { throw exception }
    //        ioa.unsafeRunAsync { either ->
    //          either.fold({ throw exception }, { fail("") })
    //        }
    //      } catch (myException: MyException) {
    //        // Success
    //      } catch (throwable: Throwable) {
    //        fail("Should only throw MyException")
    //      }
    //    }
    //
    //    "should complete when running a pure value with runAsync" {
    //      val expected = 0
    //      IO.just(expected).runAsync { either ->
    //        either.fold({ fail("") }, { IO { it shouldBe expected } })
    //      }
    //    }
    //
    //
    //    "should complete when running a return value with runAsync" {
    //      val expected = 0
    //      IO { expected }.runAsync { either ->
    //        either.fold({ fail("") }, { IO { it shouldBe expected } })
    //      }
    //    }
    //
    //    "should return an error when running an exception with runAsync" {
    //      IO.raiseError<Int>(MyException()).runAsync { either ->
    //        either.fold({
    //          when (it) {
    //            is MyException -> {
    //              IO { }
    //            }
    //            else -> fail("Should only throw MyException")
    //          }
    //        }, { fail("") })
    //      }
    //    }
    //
    //    "should return exceptions within main block with runAsync" {
    //      val exception = MyException()
    //      val ioa = IO<Int> { throw exception }
    //      ioa.runAsync { either ->
    //        either.fold({ IO { it shouldBe exception } }, { fail("") })
    //      }
    //    }
    //
    //    "should catch exceptions within run block with runAsync" {
    //      try {
    //        val exception = MyException()
    //        val ioa = IO<Int> { throw exception }
    //        ioa.runAsync { either ->
    //          either.fold({ throw it }, { fail("") })
    //        }.unsafeRunSync()
    //        fail("Should rethrow the exception")
    //      } catch (throwable: AssertionError) {
    //        fail("${throwable.message}")
    //      } catch (throwable: Throwable) {
    //        // Success
    //      }
    //    }
    //
    //    with(IO.monad()) {
    //
    //      "should map values correctly on success" {
    //        val run = IO.just(1).map() { it + 1 }.unsafeRunSync()
    //
    //        val expected = 2
    //
    //        run shouldBe expected
    //      }
    //
    //      "should flatMap values correctly on success" {
    //        val run = just(1).flatMap { num -> IO { num + 1 } }.unsafeRunSync()
    //
    //        val expected = 2
    //
    //        run shouldBe expected
    //      }
    //    }
    //
    //    "invoke is called on every run call" {
    //      val sideEffect = SideEffect()
    //      val io = IO { sideEffect.increment(); 1 }
    //      io.unsafeRunSync()
    //      io.unsafeRunSync()
    //
    //      sideEffect.counter shouldBe 2
    //    }
    //
    //    "unsafeRunTimed times out with None result" {
    //      val never = IO.async<Int> { }
    //      val result = never.unsafeRunTimed(100.milliseconds)
    //      result shouldBe None
    //    }
    //
    //    "IO.binding should for comprehend over IO" {
    //      val result = IO.monad().binding {
    //        val x = IO.just(1).bind()
    //        val y = bind { IO { x + 1 } }
    //        y
    //      }.fix()
    //      result.unsafeRunSync() shouldBe 2
    //    }
  }
}
