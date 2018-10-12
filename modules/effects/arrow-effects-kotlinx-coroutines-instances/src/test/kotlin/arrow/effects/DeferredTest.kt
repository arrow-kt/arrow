package arrow.effects

import arrow.Kind
import arrow.core.Option
import arrow.core.Try
import arrow.data.ListK
import arrow.data.NonEmptyList
import arrow.data.k
import arrow.effects.deferredk.async.async
import arrow.instances.`try`.functor.functor
import arrow.instances.`try`.traverse.traverse
import arrow.instances.listk.functor.functor
import arrow.instances.listk.traverse.traverse
import arrow.instances.nonemptylist.functor.functor
import arrow.instances.nonemptylist.traverse.traverse
import arrow.instances.option.functor.functor
import arrow.instances.option.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.generators.genIntSmall
import arrow.test.laws.AsyncLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Traverse
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class DeferredKTest : UnitSpec() {
  fun <A> EQ(): Eq<Kind<ForDeferredK, A>> = Eq { a, b ->
    a.unsafeAttemptSync() == b.unsafeAttemptSync()
  }

  suspend fun <F, A> checkAwaitAll(FF: Functor<F>, T: Traverse<F>, v: Kind<F, A>) = FF.run {
    v.map { DeferredK { it } }.awaitAll(T) == v
  }

  init {
    testLaws(AsyncLaws.laws(DeferredK.async(), EQ(), EQ()))

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

    class MyException : Exception()

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
      val ioa = DeferredK<Int>(GlobalScope, Unconfined, CoroutineStart.DEFAULT) { throw exception }
      ioa.unsafeRunAsync { either ->
        either.fold({ it shouldBe exception }, { fail("") })
      }
    }

    "should not catch exceptions within run block with unsafeRunAsync" {
      try {
        val exception = MyException()
        val ioa = DeferredK<Int>(GlobalScope, Unconfined, CoroutineStart.DEFAULT) { throw exception }
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
      DeferredK(GlobalScope, Unconfined, CoroutineStart.DEFAULT) { expected }.runAsync { either ->
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
      val ioa = DeferredK<Int>(GlobalScope, Unconfined, CoroutineStart.DEFAULT) { throw exception }
      ioa.runAsync { either ->
        either.fold({ DeferredK { it shouldBe exception } }, { fail("") })
      }
    }

    "should catch exceptions within run block with runAsync" {
      try {
        val exception = MyException()
        val ioa = DeferredK<Int>(GlobalScope, Unconfined, CoroutineStart.DEFAULT) { throw exception }
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

    "should catch exceptions within run block with runAsyncCancellable" {
      try {
        val exception = MyException()
        val ioa = DeferredK<Int>(GlobalScope, Unconfined, CoroutineStart.DEFAULT) { throw exception }
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
      forAll(Gen.string(), Gen.list(Gen.string())) { x, xs ->
        runBlocking {
          checkAwaitAll(ListK.functor(), ListK.traverse(), xs.k()) &&
            checkAwaitAll(NonEmptyList.functor(), NonEmptyList.traverse(), NonEmptyList(x, xs)) &&
            checkAwaitAll(Option.functor(), Option.traverse(), Option.just(x)) &&
            checkAwaitAll(Try.functor(), Try.traverse(), Try.just(x))
        }
      }
    }
  }
}
