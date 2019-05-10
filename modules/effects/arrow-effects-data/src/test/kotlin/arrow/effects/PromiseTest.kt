package arrow.effects

import arrow.Kind
import arrow.core.Left
import arrow.core.None
import arrow.core.Some
import arrow.core.Tuple2
import arrow.effects.extensions.NonBlocking
import arrow.effects.extensions.fx.async.async
import arrow.effects.extensions.fx.concurrent.concurrent
import arrow.effects.extensions.io.async.async
import arrow.effects.extensions.io.concurrent.concurrent
import arrow.effects.suspended.fx.Fx
import arrow.effects.typeclasses.Concurrent
import arrow.test.UnitSpec
import arrow.test.generators.throwable
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import org.junit.runner.RunWith
import kotlin.coroutines.CoroutineContext

@RunWith(KotlinTestRunner::class)
class PromiseTest : UnitSpec() {

  init {

    fun <F> Concurrent<F>.tests(
      label: String,
      ctx: CoroutineContext = NonBlocking,
      EQ: Eq<Kind<F, Boolean>>,
      promise: Kind<F, Promise<F, Int>>
    ) {

      "$label - complete" {
        forAll(Gen.int()) { a ->
          promise.flatMap { p ->
            p.complete(a).flatMap {
              p.get().flatMap { aa ->
                delay { aa == a }
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - complete twice should result in Promise.AlreadyFulfilled" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          promise.flatMap { p ->
            p.complete(a).flatMap {
              p.complete(b)
                .attempt()
                .product(p.get()).flatMap { result ->
                  delay { result == Tuple2(Left(Promise.AlreadyFulfilled), a) }
                }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - tryComplete" {
        forAll(Gen.int()) { a ->
          promise.flatMap { p ->
            p.tryComplete(a).flatMap { didComplete ->
              p.get().tupleLeft(didComplete).flatMap { r ->
                delay { r == Tuple2(true, a) }
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - tryComplete twice returns false" {
        forAll(Gen.int(), Gen.int()) { a, b ->
          promise.flatMap { p ->
            p.tryComplete(a).flatMap {
              p.tryComplete(b).flatMap { didComplete ->
                p.get().tupleLeft(didComplete).flatMap { r ->
                  delay { r == Tuple2(false, a) }
                }
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - error" {
        forAll(Gen.throwable()) { error ->
          promise.flatMap { p ->
            p.error(error).flatMap {
              p.get().attempt().flatMap { r ->
                delay { r == Left(error) }
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - error twice should result in Promise.AlreadyFulfilled" {
        forAll(Gen.throwable()) { error ->
          promise.flatMap { p ->
            p.error(error).flatMap {
              p.error(RuntimeException("Boom!")).attempt()
                .product(p.get().attempt()).flatMap { r ->
                  delay { r == Tuple2(Left(Promise.AlreadyFulfilled), Left(error)) }
                }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - tryError" {
        forAll(Gen.throwable()) { error ->
          promise.flatMap { p ->
            p.tryError(error).flatMap { didError ->
              p.get().attempt()
                .tupleLeft(didError).flatMap { r ->
                  delay { r == Tuple2(true, Left(error)) }
                }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - tryError twice returns false" {
        forAll(Gen.throwable()) { error ->
          promise.flatMap { p ->
            p.tryError(error).flatMap {
              p.tryError(RuntimeException("Boom!")).flatMap { didComplete ->
                p.get().attempt()
                  .tupleLeft(didComplete).flatMap { r ->
                    delay { r == Tuple2(false, Left(error)) }
                  }
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }

      "$label - get blocks until set" {
        Ref.of(0, this@tests).flatMap { state ->
          promise.flatMap { modifyGate ->
            promise.flatMap { readGate ->
              modifyGate.get().flatMap { state.update { i -> i * 2 }.flatMap { readGate.complete(0) } }.fork().flatMap {
                state.set(1).flatMap { modifyGate.complete(0) }.fork().flatMap {
                  readGate.get().flatMap {
                    state.get().flatMap { r ->
                      delay { r == 2 }
                    }
                  }
                }
              }
            }
          }
        }.equalUnderTheLaw(just(true), EQ) shouldBe true
      }

      "$label - tryGet returns None for empty Promise" {
        promise.flatMap { p -> p.tryGet().flatMap { r -> delay { r == None } } }
          .equalUnderTheLaw(just(true), EQ) shouldBe true
      }

      "$label - tryGet returns Some for completed promise" {
        forAll(Gen.int()) { a ->
          promise.flatMap { p ->
            p.complete(a).flatMap {
              p.tryGet().flatMap { r ->
                delay { r == Some(a) }
              }
            }
          }.equalUnderTheLaw(just(true), EQ)
        }
      }
    }

    IO.concurrent().tests("IO - CancelablePromise", EQ = IO_EQ(), promise = Promise(IO.concurrent()))
    IO.concurrent().tests("IO - UncancelablePromise", EQ = IO_EQ(), promise = Promise.uncancelable(IO.async()))
    Fx.concurrent().tests("Fx - CancelablePromise", EQ = EQ(), promise = Promise(Fx.concurrent()))
    Fx.concurrent().tests("Fx - UncancelablePromise", EQ = EQ(), promise = Promise.uncancelable(Fx.async()))
  }
}
