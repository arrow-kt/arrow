package arrow.integrations.kotlinx

import arrow.core.Right
import arrow.core.Some
import arrow.core.extensions.eq
import arrow.fx.internal.AtomicRefW
import arrow.core.test.UnitSpec
import arrow.core.test.generators.throwable
import arrow.core.test.laws.equalUnderTheLaw
import arrow.fx.IO
import arrow.fx.IOResult
import arrow.fx.onCancel
import arrow.fx.bracketCase
import arrow.fx.extensions.exitcase2.eq.eq
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.effectMap
import arrow.fx.extensions.io.monad.followedBy
import arrow.fx.handleErrorWith
import arrow.fx.flatMap
import arrow.fx.onCancel
import arrow.fx.handleErrorWith
import arrow.fx.typeclasses.ExitCase2
import arrow.fx.typeclasses.milliseconds
import arrow.fx.typeclasses.seconds
import arrow.fx.unsafeRunAsync
import arrow.fx.test.eq
import arrow.fx.test.laws.shouldBeEq
import arrow.typeclasses.Eq
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.TestCoroutineScope
import arrow.fx.test.eq.eqK

@ObsoleteCoroutinesApi
@Suppress("IMPLICIT_NOTHING_AS_TYPE_PARAMETER")
@UseExperimental(ExperimentalCoroutinesApi::class)
class CoroutinesIntegrationTest : UnitSpec() {

  private val other = newSingleThreadContext("other")
  private val all = newSingleThreadContext("all")

  init {
    // --------------- suspendCancellable ---------------

    "suspendedCancellable should throw" {
      forAll(Gen.throwable()) { expected ->
        val ceh = TestCoroutineExceptionHandler()
        val scope = TestCoroutineScope(ceh + TestCoroutineDispatcher())

        scope.launch {
          IO { throw expected }.suspendCancellable()
        }

        val actual = ceh.uncaughtExceptions[0]
        // suspendCancellableCoroutine copy and re-throws the exception so we need to compare the type
        // see https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/src/internal/StackTraceRecovery.kt#L68
        actual::class == expected::class
      }
    }

    "suspendedCancellable can handle errors through IO" {
      forAll(Gen.throwable(), Gen.int()) { e, expected ->
        val ceh = TestCoroutineExceptionHandler()
        val scope = TestCoroutineScope(ceh + TestCoroutineDispatcher())

        scope.launch {
          val actual = IO { throw e }.handleErrorWith { IO.just(expected) }.suspendCancellable()

          actual shouldBe expected
        }

        ceh.uncaughtExceptions.isEmpty()
      }
    }

    "suspendedCancellable should resume with right block" {
      forAll(Gen.int()) { i ->
        val ceh = TestCoroutineExceptionHandler()
        val scope = TestCoroutineScope(ceh + TestCoroutineDispatcher())
        scope.launch {
          val first = IO { i + 1 }.suspendCancellable()
          val second = IO { first + 1 }.suspendCancellable()
          val third = IO { second + 1 }.suspendCancellable()

          third shouldBe i + 3
        }

        ceh.uncaughtExceptions.isEmpty()
      }
    }

    "suspendCancellable doesn't start if scope is cancelled" {
      forAll(Gen.int()) { i ->
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        val ref = AtomicRefW<Int?>(i)
        scope.cancel()
        scope.launch {
          IO { ref.value = null }.suspendCancellable()
        }

        ref.value == i
      }
    }

    "scope cancellation should cancel suspendedCancellable IO" {
      forAll(Gen.int()) { i ->
        IO.fx<Nothing, Int> {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val promise = !Promise<Int>()
          !IO.effect {
            scope.launch {
              IO.cancellable<Nothing, Unit> { promise.complete(i) }.suspendCancellable()
            }
          }
          !IO.effect { scope.cancel() }
          !promise.get()
        }.equalUnderTheLaw(IO.just(i), IO.eqK<Nothing>(timeout = 500.milliseconds).liftEq(Int.eq()))
      }
    }

    "suspendCancellable can cancel even for infinite asyncs" {
      IO.async { cb: (IOResult<Nothing, Int>) -> Unit ->
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        scope.launch {
          IO.never
            .onCancel(IO { cb(IOResult.Success(1)) })
            .suspendCancellable()
        }
        IO.sleep(500.milliseconds)
          .unsafeRunAsync { scope.cancel() }
      }.equalUnderTheLaw(IO.just(1), IO.eqK<Nothing>(timeout = 2.seconds).liftEq(Int.eq()))
    }

    // --------------- unsafeRunScoped ---------------

    "should rethrow exceptions within run block with unsafeRunScoped" {
      forAll(Gen.throwable()) { e ->
        try {
          val scope = TestCoroutineScope(TestCoroutineDispatcher())

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
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
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
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        val promise = !Promise<Int>()
        !IO.effect {
          IO(all) { -1 }.flatMap { IO.never }.onCancel(promise.complete(1)).unsafeRunScoped(scope) { }
        }
        !IO.sleep(500.milliseconds).effectMap { scope.cancel() }
        !promise.get()
      }.unsafeRunTimed(2.seconds) shouldBe Some(Right(1))
    }

    "should complete when running a pure value with unsafeRunScoped" {
      forAll(Gen.int()) { i ->
        val scope = TestCoroutineScope(TestCoroutineDispatcher())
        IO.async<Nothing, Int> { cb ->
          IO.just(i).unsafeRunScoped(scope) { result ->
            result.fold({ fail("") }, { fail("") }, { cb(IOResult.Success(it)) })
          }
        }.equalUnderTheLaw(IO.just(i), IO.eqK<Nothing>().liftEq(Int.eq()))
      }
    }

    "unsafeRunScoped doesn't start if scope is cancelled" {
      forAll(Gen.int()) { i ->
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        val ref = AtomicRefW<Int?>(i)
        scope.cancel()
        IO { ref.value = null }.unsafeRunScoped(scope) {}
        ref.value == i
      }
    }

    // --------------- forkScoped ---------------

    "forkScoped can cancel even for infinite asyncs" {
      IO.fx<Nothing, Int> {
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        val promise = !Promise<Int>()

        val (_, _) = !IO.never.onCancel(promise.complete(1)).forkScoped(scope)
        !IO.sleep(500.milliseconds).effectMap { scope.cancel() }
        !promise.get()
      }.shouldBeEq(IO.just(1), IO.eqK<Nothing>().liftEq(Int.eq()))
    }

    "forkScoped should complete when running a pure value" {
      forAll(Gen.int()) { i ->
        IO.fx<Nothing, Int> {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val (join, _) = !IO.effect { i }.forkScoped(scope)
          !join
        }.equalUnderTheLaw(IO.just(i), IO.eqK<Nothing>().liftEq(Int.eq()))
      }
    }

    "forkScoped should cancel correctly" {
      IO.fx<Nothing, ExitCase2<Nothing>> {
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        val startLatch = !Promise<Unit>()
        val promise = !Promise<ExitCase2<Nothing>>()

        !IO.unit.bracketCase(
          use = { startLatch.complete(Unit).followedBy(IO.never) },
          release = { _, ex -> promise.complete(ex) }
        ).forkScoped(scope)

        !startLatch.get()

        !IO.effect { scope.cancel() }

        !promise.get()
      }.equalUnderTheLaw(IO.just(ExitCase2.Cancelled), IO.eqK<Nothing>().liftEq(ExitCase2.eq<Nothing>(Eq.any(), Eq.any())))
    }

    "forkScoped doesn't start if scope is cancelled" {
      forAll(Gen.int()) { i ->
        IO.fx<Nothing, Int?> {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val ref = AtomicRefW<Int?>(i)
          scope.cancel()
          !IO {
            ref.value = null
          }.forkScoped(scope)

          ref.value
        }.equalUnderTheLaw(IO.just<Int?>(i), IO.eqK<Nothing>().liftEq(Int.eq().nullable()))
      }
    }
  }
}

// TODO move to Arrow Core
fun <A> Eq<A>.nullable(): Eq<A?> = Eq { a, b ->
  a?.let { aa ->
    b?.let { bb ->
      aa.eqv(bb)
    } ?: false
  } ?: b?.let { false } ?: true
}
