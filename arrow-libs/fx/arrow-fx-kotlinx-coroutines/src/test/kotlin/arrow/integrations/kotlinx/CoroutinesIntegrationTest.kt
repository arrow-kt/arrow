package arrow.integrations.kotlinx

import arrow.Kind
import arrow.core.Either
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.eq
import arrow.fx.internal.AtomicRefW
import arrow.core.right
import arrow.core.test.UnitSpec
import arrow.core.test.generators.throwable
import arrow.core.test.laws.equalUnderTheLaw
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.exitcase.eq.eq
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.bracket.onCancel
import arrow.fx.extensions.io.concurrent.waitFor
import arrow.fx.fix
import arrow.fx.handleErrorWith
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.milliseconds
import arrow.fx.typeclasses.seconds
import arrow.fx.test.laws.shouldBeEq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
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
        IO.fx {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val promise = Promise<Int>().bind()
          effect {
            scope.launch {
              IO.cancellable<Unit> { promise.complete(i) }.suspendCancellable()
            }
          }.bind()
          effect { scope.cancel() }.bind()
          promise.get().bind()
        }.equalUnderTheLaw(IO.just(i), IO.eqK(500.milliseconds).liftEq(Int.eq()))
      }
    }

    "suspendCancellable can cancel even for infinite asyncs" {
      IO.async { cb: (Either<Throwable, Int>) -> Unit ->
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        scope.launch {
          IO.async<Int> { }
            .onCancel(IO { cb(1.right()) })
            .suspendCancellable()
        }
        IO.sleep(500.milliseconds)
          .unsafeRunAsync { scope.cancel() }
      }.equalUnderTheLaw(IO.just(1), IO.eqK(2.seconds).liftEq(Int.eq()))
    }

    // --------------- unsafeRunScoped ---------------

    "should rethrow exceptions within run block with unsafeRunScoped" {
      forAll(Gen.throwable()) { e ->
        try {
          val scope = TestCoroutineScope(TestCoroutineDispatcher())

          val ioa = IO<Int> { throw e }

          ioa.unsafeRunScoped(scope) { either ->
            either.fold({ throw it }, { fail("") })
          }
          fail("Should rethrow the exception")
        } catch (throwable: Throwable) {
          throwable == e
        }
      }
    }

    "unsafeRunScoped should cancel correctly" {
      forAll(Gen.int()) { i ->
        IO.fx {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val promise = Promise<Int>().bind()
          effect {
            IO.cancellable<Unit> { promise.complete(i) }.unsafeRunScoped(scope) { }
          }.bind()
          effect { scope.cancel() }.bind()
          promise.get().bind()
        }.equalUnderTheLaw(IO.just(i), IO.eqK(500.milliseconds).liftEq(Int.eq()))
      }
    }

    "unsafeRunScoped can cancel even for infinite asyncs" {
      IO.fx {
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        val promise = Promise<Int>().bind()
        effect {
          IO(all) { -1 }.flatMap { IO.async<Int> { } }.onCancel(promise.complete(1)).unsafeRunScoped(scope) { }
        }.bind()
        sleep(500.milliseconds).effectMap { scope.cancel() }.bind()
        promise.get().bind()
      }.equalUnderTheLaw(IO.just(1), IO.eqK(2.seconds).liftEq(Int.eq()))
    }

    "should complete when running a pure value with unsafeRunScoped" {
      forAll(Gen.int()) { i ->
        val scope = TestCoroutineScope(TestCoroutineDispatcher())
        IO.async<Int> { cb ->
          IO.just(i).unsafeRunScoped(scope) { either ->
            either.fold({ fail("") }, { cb(it.right()) })
          }
        }.equalUnderTheLaw(IO.just(i), IO.eqK().liftEq(Int.eq()))
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
      IO.fx {
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        val promise = Promise<Int>().bind()

        val (_, _) = IO.never.onCancel(promise.complete(1)).forkScoped(scope).bind()
        sleep(500.milliseconds).effectMap { scope.cancel() }.bind()
        promise.get().bind()
      }.shouldBeEq(IO.just(1), IO.eqK().liftEq(Int.eq()))
    }

    "forkScoped should complete when running a pure value" {
      forAll(Gen.int()) { i ->
        IO.fx {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val (join, _) = IO.effect { i }.forkScoped(scope).bind()
          join.bind()
        }.equalUnderTheLaw(IO.just(i), IO.eqK().liftEq(Int.eq()))
      }
    }

    "forkScoped should cancel correctly" {
      IO.fx {
        val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
        val startLatch = Promise<Unit>().bind()
        val promise = Promise<ExitCase<Throwable>>().bind()

        IO.unit.bracketCase(
          use = { startLatch.complete(Unit).followedBy(IO.never) },
          release = { _, ex -> promise.complete(ex) }
        ).forkScoped(scope).bind()

        startLatch.get().bind()

        effect { scope.cancel() }.bind()

        promise.get().bind()
      }.equalUnderTheLaw(IO.just(ExitCase.Cancelled), IO.eqK().liftEq(ExitCase.eq(Eq.any())))
    }

    "forkScoped doesn't start if scope is cancelled" {
      forAll(Gen.int()) { i ->
        IO.fx {
          val scope = TestCoroutineScope(Job() + TestCoroutineDispatcher())
          val ref = AtomicRefW<Int?>(i)
          scope.cancel()
          IO {
            ref.value = null
          }.forkScoped(scope).bind()

          ref.value
        }.equalUnderTheLaw(IO.just<Int?>(i), IO.eqK().liftEq(Int.eq().nullable()))
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

  internal fun IO.Companion.eqK(timeout: Duration = 5.seconds) = object : EqK<ForIO> {
    override fun <A> Kind<ForIO, A>.eqK(other: Kind<ForIO, A>, EQ: Eq<A>): Boolean =
      Either.eq(Eq.any(), EQ).run {
        IO.applicative().mapN(fix().attempt(), other.attempt()) { (a, b) -> a.eqv(b) }
          .waitFor(timeout)
          .unsafeRunSync()
      }
  }
}
