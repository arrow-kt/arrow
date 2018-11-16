package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.deferredk.async.async
import arrow.effects.typeclasses.ExitCase
import arrow.effects.deferredk.monad.flatMap
import arrow.effects.deferredk.concurrent.concurrent
import arrow.instances.`try`.functor.functor
import arrow.instances.`try`.traverse.traverse
import arrow.instances.option.functor.functor
import arrow.instances.option.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.generators.genIntSmall
import arrow.test.laws.AsyncLaws
import arrow.test.laws.shouldBe
import arrow.test.laws.throwableEq
import arrow.test.laws.ConcurrentLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Traverse
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Unconfined
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.lang.AssertionError
import java.util.concurrent.TimeUnit

@RunWith(KTestJUnitRunner::class)
class DeferredKTest : UnitSpec() {
  fun <A> EQ(): Eq<Kind<ForDeferredK, A>> = Eq { a, b ->
    val other = b.unsafeAttemptSync()
    a.unsafeAttemptSync().fold(
      { eA -> other.fold({ eB -> throwableEq().run { eA.eqv(eB) } }, { false }) },
      { a -> other.fold({ false }, { b -> a == b }) })
  }

  suspend fun <F, A> checkAwaitAll(FF: Functor<F>, T: Traverse<F>, v: Kind<F, A>) = FF.run {
    v.map { DeferredK { it } }.awaitAll(T) == v
  }

  init {
    testLaws(ConcurrentLaws.laws(DeferredK.concurrent(), EQ(), EQ(), EQ()))

    "DeferredK is awaitable" {
      forAll(genIntSmall(), genIntSmall(), genIntSmall()) { x: Int, y: Int, z: Int ->
        runBlocking {
          val a = DeferredK { x }.await()
          val b = DeferredK { y + a }.await()
          val c = DeferredK { z + b }.await()
          c
        } == x + y + z
      }
    }

    "should complete when running a pure value with unsafeRunAsync" {
      val expected = 0
      DeferredK.just(expected).unsafeRunAsync { either ->
        either.fold({ fail("") }, { it shouldBe expected })
      }
    }

    class MyException(message: String = "") : Exception(message)

    "should return an error when running an exception with unsafeRunAsync" {
      DeferredK.raiseError<Int>(MyException()).unsafeRunAsync { either ->
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
      val ioa = DeferredK<Int>(Unconfined, GlobalScope, CoroutineStart.DEFAULT) { throw exception }
      ioa.unsafeRunAsync { either ->
        either.fold({
          it.shouldBe(exception, throwableEq())
        }, { fail("") })
      }
    }

    "should not catch exceptions within run block with unsafeRunAsync" {
      try {
        val exception = MyException()
        val ioa = DeferredK<Int>(Unconfined, GlobalScope, CoroutineStart.DEFAULT) { throw exception }
        ioa.unsafeRunAsync { either ->
          either.fold({ throw exception }, { fail("") })
        }
        fail("Should rethrow the exception")
      } catch (myException: MyException) {
        // Success
      } catch (throwable: Throwable) {
        fail("Should only throw MyException")
      }
    }

    "should complete when running a pure value with runAsync" {
      val expected = 0
      DeferredK.just(expected).runAsync { either ->
        either.fold({ fail("") }, { DeferredK { it shouldBe expected } })
      }
    }


    "should complete when running a return value with runAsync" {
      val expected = 0
      DeferredK(Unconfined, GlobalScope, CoroutineStart.DEFAULT) { expected }.runAsync { either ->
        either.fold({ fail("") }, { DeferredK { it shouldBe expected } })
      }
    }

    "should return an error when running an exception with runAsync" {
      DeferredK.raiseError<Int>(MyException()).runAsync { either ->
        either.fold({
          when (it) {
            is MyException -> {
              DeferredK { }
            }
            else -> fail("Should only throw MyException")
          }
        }, { fail("") })
      }
    }

    "should return exceptions within main block with runAsync" {
      val exception = MyException()
      val ioa = DeferredK<Int>(Unconfined, GlobalScope, CoroutineStart.DEFAULT) { throw exception }
      ioa.runAsync { either ->
        either.fold({ DeferredK { it shouldBe exception } }, { fail("") })
      }
    }

    "should catch exceptions within run block with runAsync" {
      try {
        val exception = MyException()
        val ioa = DeferredK<Int>(Unconfined, GlobalScope, CoroutineStart.DEFAULT) { throw MyException() }
        ioa.runAsync { either ->
          either.fold({ throw MyException() }, { fail("") })
        }.unsafeRunSync()
        fail("Should rethrow the exception")
      } catch (myException: MyException) {
        // Success
      }
    }

    "should catch exceptions within run block with runAsyncCancellable" {
      try {
        val exception = MyException()
        val ioa = DeferredK<Int>(Unconfined, GlobalScope, CoroutineStart.DEFAULT) { throw exception }
        ioa.runAsyncCancellable { either ->
          either.fold({ throw it }, { fail("") })
        }.unsafeRunSync()
        fail("Should rethrow the exception")
      } catch (throwable: AssertionError) {
        fail("${throwable.message}")
      } catch (throwable: Throwable) {
        // Success
      }
    }

    "awaitAll called on a Traverse instance of Kind<F, DeferredK<T>> should return a Traverse instance of Kind<F, T>" {
      forAll(Gen.string()) { x ->
        runBlocking {
          checkAwaitAll(Option.functor(), Option.traverse(), Option.just(x)) &&
            checkAwaitAll(Try.functor(), Try.traverse(), Try.just(x))
        }
      }
    }

    "DeferredK bracket cancellation should release resource with cancel exit status" {
      runBlocking {
        lateinit var ec: ExitCase<Throwable>
        val countDownLatch = CountDownLatch(1)
        DeferredK.just(Unit)
          .bracketCase(
            use = { DeferredK.async<Nothing>(Unconfined) { _, _ -> } },
            release = { _, exitCase ->
              DeferredK {
                ec = exitCase
                countDownLatch.countDown()
              }
            }
          ).unsafeRunAsyncCancellable { }
          .invoke()

        countDownLatch.await(50, TimeUnit.MILLISECONDS)
        ec shouldBe ExitCase.Cancelled
      }
    }

    "DeferredK should cancel KindConnection on dispose" {
      runBlocking {
        val promise = Promise.unsafeUncancelable<ForDeferredK, Unit>(DeferredK.async())
        DeferredK.async<Unit>(ctx = Unconfined) { conn, _ ->
          conn.push(promise.complete(Unit))
        }.unsafeRunAsyncCancellable { }
          .invoke()
        promise.get.await()
      }
    }

    "KindConnection can cancel upstream" {
      Promise.uncancelable<ForDeferredK, Unit>(DeferredK.async()).flatMap { latch ->
        DeferredK.async<Unit> { conn, cb ->
          conn.push(latch.complete(Unit))
          cb(Right(Unit))
        }.flatMap {
          DeferredK.async<Unit> { conn, _ ->
            conn.cancel().fix().unsafeRunAsync { }
          }
        }.unsafeRunAsyncCancellable { }

        latch.get
      }
    }

    "DeferredK async should be cancellable" {
      Promise.uncancelable<ForDeferredK, Unit>(DeferredK.async())
        .flatMap { latch ->
          DeferredK {
            val d =
              DeferredK.async<Unit> { _, _ -> }
                .apply { invokeOnCompletion { e -> if (e is CancellationException) latch.complete(Unit).unsafeRunAsync { } } }
            d.start()
            d.cancelAndJoin()
          }.flatMap { latch.get }
        }.unsafeRunSync() shouldBe Unit
    }

  }
}
