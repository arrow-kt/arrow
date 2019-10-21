package arrow.fx

import arrow.core.Left
import arrow.core.None
import arrow.core.Some
import arrow.core.Tuple2
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.extensions.io.apply.product
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.functor.tupleLeft
import arrow.fx.extensions.io.monad.flatMap
import arrow.fx.extensions.io.monadDefer.monadDefer
import arrow.test.UnitSpec
import arrow.test.generators.throwable
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class PromiseTest : UnitSpec() {

  init {

    fun tests(
      label: String,
      ctx: CoroutineContext = Dispatchers.Default,
      promise: IO<Promise<ForIO, Int>>
    ) {

      "$label - complete" {
        forAll(Gen.int()) { a ->
          promise.flatMap { p ->
            p.complete(a).flatMap {
              p.get()
            }
          }.unsafeRunSync() == a
        }
      }

      "$label - complete twice should result in Promise.AlreadyFulfilled" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          promise.flatMap { p ->
            p.complete(a).flatMap {
              p.complete(b)
                .attempt()
                .product(p.get())
            }
          }.unsafeRunSync() == Tuple2(Left(Promise.AlreadyFulfilled), a)
        }
      }

      "$label - tryComplete" {
        forAll(Gen.int()) { a ->
          promise.flatMap { p ->
            p.tryComplete(a).flatMap { didComplete ->
              p.get().tupleLeft(didComplete)
            }
          }.unsafeRunSync() == Tuple2(true, a)
        }
      }

      "$label - tryComplete twice returns false" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          promise.flatMap { p ->
            p.tryComplete(a).flatMap {
              p.tryComplete(b).flatMap { didComplete ->
                p.get().tupleLeft(didComplete)
              }
            }
          }.unsafeRunSync() == Tuple2(false, a)
        }
      }

      "$label - error" {
        forAll(Gen.throwable()) { error ->
          promise.flatMap { p ->
            p.error(error).flatMap {
              p.get().attempt()
            }
          }.unsafeRunSync() == Left(error)
        }
      }

      "$label - error twice should result in Promise.AlreadyFulfilled" {
        forAll(Gen.throwable()) { error ->
          promise.flatMap { p ->
            p.error(error).flatMap {
              p.error(RuntimeException("Boom!")).attempt()
                .product(p.get().attempt())
            }
          }.unsafeRunSync() == Tuple2(Left(Promise.AlreadyFulfilled), Left(error))
        }
      }

      "$label - tryError" {
        forAll(Gen.throwable()) { error ->
          promise.flatMap { p ->
            p.tryError(error).flatMap { didError ->
              p.get().attempt()
                .tupleLeft(didError)
            }
          }.unsafeRunSync() == Tuple2(true, Left(error))
        }
      }

      "$label - tryError twice returns false" {
        forAll(Gen.throwable()) { error ->
          promise.flatMap { p ->
            p.tryError(error).flatMap {
              p.tryError(RuntimeException("Boom!")).flatMap { didComplete ->
                p.get().attempt()
                  .tupleLeft(didComplete)
              }
            }
          }.unsafeRunSync() == Tuple2(false, Left(error))
        }
      }

      "$label - get blocks until set" {
        Ref(IO.monadDefer(), 0).flatMap { state ->
          promise.flatMap { modifyGate ->
            promise.flatMap { readGate ->
              modifyGate.get().flatMap { state.update { i -> i * 2 }.flatMap { readGate.complete(0) } }.fork(ctx).flatMap {
                state.set(1).flatMap { modifyGate.complete(0) }.fork(ctx).flatMap {
                  readGate.get().flatMap {
                    state.get()
                  }
                }
              }
            }
          }
        }.unsafeRunSync() shouldBe 2
      }

      "$label - tryGet returns None for empty Promise" {
        promise.flatMap { p -> p.tryGet() }.unsafeRunSync() shouldBe None
      }

      "$label - tryGet returns Some for completed promise" {
        forAll(Gen.int()) { a ->
          promise.flatMap { p ->
            p.complete(a).flatMap {
              p.tryGet()
            }
          }.unsafeRunSync() == Some(a)
        }
      }
    }

    tests("CancelablePromise", promise = Promise<ForIO, Int>(IO.concurrent()).fix())
    tests("UncancelablePromise", promise = Promise.uncancelable<ForIO, Int>(IO.async()).fix())
  }
}
