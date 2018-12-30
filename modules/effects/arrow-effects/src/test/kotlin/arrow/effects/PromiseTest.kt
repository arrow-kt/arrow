package arrow.effects

import arrow.core.*
import arrow.effects.instances.io.async.async
import arrow.effects.instances.io.bracket.guarantee
import arrow.effects.instances.io.concurrent.concurrent
import arrow.effects.instances.io.monad.binding
import arrow.effects.instances.io.monad.flatMap
import arrow.effects.instances.io.monadDefer.monadDefer
import arrow.test.UnitSpec
import arrow.test.generators.genThrowable
import arrow.test.laws.equalUnderTheLaw
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlinx.coroutines.Dispatchers
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PromiseTest : UnitSpec() {

  init {
    fun tests(label: String, promise: () -> IOOf<Promise<ForIO, Int>>): Unit {
      "$label - complete" {
        forAll(Gen.int()) { i ->
          promise().flatMap { p ->
            p.complete(i).flatMap {
              p.get
            }
          }.equalUnderTheLaw(IO.just(i), EQ())
        }
      }

      "$label - complete blocks"{
        fun loop(times: Int): IO<Boolean> {
          fun foreverAsync(i: Int): IO<Unit> =
            if (i == 512) IO.async<Unit> { _, cb -> cb(Right(Unit)) }.flatMap { foreverAsync(0) }
            else IO.unit.flatMap { foreverAsync(i + 1) }

          val task = binding {
            val p = Promise.uncancelable<ForIO, Unit>(IO.async()).bind()
            val latch = Promise.uncancelable<ForIO, Unit>(IO.async()).bind()
            val fb = latch.complete(Unit).flatMap { p.get.flatMap { foreverAsync(0) } }.startF(Dispatchers.Default).bind()
            latch.get.bind()
            p.complete(Unit).guarantee(fb.cancel()).bind()
            true
          }

          return task.flatMap { r ->
            if (times > 0) loop(times - 1)
            else IO.just(r)
          }
        }

        loop(100).unsafeRunSync() shouldBe true
      }

      "$label - complete twice results in AlreadyFulfilled" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          binding {
            val p = promise().bind()
            p.complete(a).bind()
            p.complete(b).bind()
            p.get.bind()
          }.equalUnderTheLaw(IO.raiseError(Promise.AlreadyFulfilled), EQ())
        }
      }

      "$label - get blocks until set" {
        binding {
          val state = Ref.of(0, IO.monadDefer()).bind()
          val modifyGate = promise().bind()
          val readGate = promise().bind()
          modifyGate.get.flatMap { state.update { i -> i * 2 }.flatMap { readGate.complete(1) } }.startF(Dispatchers.Default).bind()
          state.set(1).flatMap { modifyGate.complete(1) }.startF(Dispatchers.Default).bind()
          readGate.get.bind()
          state.get().bind()
        }.unsafeRunSync() shouldBe 2
      }

      "$label - tryGet before completing" {
        promise().flatMap { p ->
          p.tryGet
        }.equalUnderTheLaw(IO.just(None), EQ())
      }

      "$label - tryGet after completing" {
        forAll(Gen.int()) { i ->
          promise().flatMap { p ->
            p.complete(i).flatMap {
              p.tryGet
            }
          }.equalUnderTheLaw(IO.just(Some(i)), EQ())
        }
      }

      "$label - tryComplete returns true and sets value when not completed" {
        forAll(Gen.int()) { i ->
          binding {
            val p = promise().bind()
            p.tryComplete(i).bind() toT p.get.bind()
          }.equalUnderTheLaw(IO.just(true toT i), EQ())
        }
      }

      "$label - tryComplete returns false if already completed" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          binding {
            val p = promise().bind()
            p.complete(a).bind()
            p.tryComplete(b).bind() toT p.get.bind()
          }.equalUnderTheLaw(IO.just(false toT a), EQ())
        }
      }

      "$label - error is raised when not completed" {
        val error = RuntimeException("Boom")
        promise().flatMap { p ->
          p.error(error)
        }.equalUnderTheLaw(IO.raiseError(error), EQ())
      }

      "$label - error after completion results in AlreadyFulfilled" {
        forAll(Gen.int(), genThrowable()) { i, t ->
          binding {
            val p = promise().bind()
            p.complete(i).bind()
            p.error(t).bind()
            p.get.bind()
          }.equalUnderTheLaw(IO.raiseError(Promise.AlreadyFulfilled), EQ())
        }
      }

      "$label - tryError returns false if already completed" {
        forAll(Gen.int(), genThrowable()) { i, t ->
          binding {
            val p = promise().bind()
            p.complete(i).bind()
            p.tryError(t).bind() toT p.get.bind()
          }.equalUnderTheLaw(IO.just(false toT i), EQ())
        }
      }

      "$label - tryError raises error if not completed" {
        forAll(genThrowable()) { t ->
          binding {
            val p = promise().bind()
            p.tryError(t).bind()
            p.get.bind()
          }.equalUnderTheLaw(IO.raiseError(t), EQ())
        }
      }

    }


    tests("UncancelablePromise") { Promise.uncancelable(IO.async()) }
    tests("CancelablePromise") { Promise(IO.concurrent()) }

    "CancelablePromise - supports cancellation of get" {
      Promise<ForIO, Unit>(IO.concurrent()).flatMap { p ->
        p.get
      }
        .unsafeRunAsyncCancellable { }
        .invoke()
    }

  }

}

