package arrow.integrations.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import arrow.core.Right
import arrow.core.Some
import arrow.core.extensions.eq
import arrow.core.internal.AtomicRefW
import arrow.core.test.UnitSpec
import arrow.core.test.generators.throwable
import arrow.core.test.laws.equalUnderTheLaw
import arrow.fx.IO
import arrow.fx.IOResult
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.effectMap
import arrow.fx.flatMap
import arrow.fx.onCancel
import arrow.fx.test.eq.eqK
import arrow.fx.typeclasses.milliseconds
import arrow.fx.typeclasses.seconds
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlinx.coroutines.newSingleThreadContext

class ExtensionsKtTest : UnitSpec() {

  private val ctx = newSingleThreadContext("all")
  private val eqK = IO.eqK<Nothing>()

  init {

    // --------------- unsafeRunScoped ---------------

    "should rethrow exceptions within run block with unsafeRunScoped" {
      forAll(Gen.throwable()) { e ->
        try {
          val scope = TestLifecycleOwner()

          val ioa = IO<Int> { throw e }

          ioa.unsafeRunScoped(scope) { result ->
            result.fold({ throw it }, { fail("") }, { fail("") })
          }
          fail("Should rethrow the exception")
        } catch (throwable: Throwable) {
          throwable == e
        }
      }
    }

    "unsafeRunScoped should cancel correctly" {
      forAll(Gen.int()) { i ->
        IO.fx<Nothing, Int> {
          val scope = TestLifecycleOwner()
          val promise = !Promise<Int>()
          !IO.effect {
            IO.cancellable<Nothing, Unit> { promise.complete(i) }.unsafeRunScoped(scope) { }
          }
          !IO.effect { scope.cancel() }
          !promise.get()
        }.equalUnderTheLaw(IO.just(i), IO.eqK<Nothing>(timeout = 500.milliseconds).liftEq(Int.eq()))
      }
    }

    "unsafeRunScoped can cancel even for infinite asyncs" {
      IO.fx<Nothing, Int> {
        val scope = TestLifecycleOwner()
        val promise = !Promise<Int>()
        !IO.effect {
          IO(ctx) { -1 }.flatMap { IO.never }.onCancel(promise.complete(1)).unsafeRunScoped(scope) { }
        }
        !IO.sleep(500.milliseconds).effectMap { scope.cancel() }
        !promise.get()
      }.unsafeRunTimed(2.seconds) shouldBe Some(Right(1))
    }

    "should complete when running a pure value with unsafeRunScoped" {
      forAll(Gen.int()) { i ->
        val scope = TestLifecycleOwner()
        IO.async<Nothing, Int> { cb ->
          IO.just(i).unsafeRunScoped(scope) { result ->
            result.fold({ fail("") }, { fail("") }, { cb(IOResult.Success(it)) })
          }
        }.equalUnderTheLaw(IO.just(i), eqK.liftEq(Int.eq()))
      }
    }

    "unsafeRunScoped doesn't start if scope is cancelled" {
      forAll(Gen.int()) { i ->
        val scope = TestLifecycleOwner()
        val ref = AtomicRefW<Int?>(i)
        scope.cancel()
        IO { ref.value = null }.unsafeRunScoped(scope) {}
        ref.value == i
      }
    }
  }
}

private class TestLifecycleOwner : LifecycleOwner {
  private val registry = LifecycleRegistry(this)

  init {
    registry.currentState = State.CREATED
  }

  override fun getLifecycle(): Lifecycle = registry

  fun cancel() {
    registry.currentState = State.DESTROYED
  }

  fun reanimate() {
    registry.currentState = State.CREATED
  }
}
