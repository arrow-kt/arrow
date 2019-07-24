package arrow.fx

import arrow.Kind
import arrow.core.Failure
import arrow.core.Left
import arrow.core.Option
import arrow.core.Right
import arrow.core.Try
import arrow.core.left
import arrow.core.none
import arrow.core.right
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.io.unsafeRun.unsafeRun
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.UnsafeRun
import arrow.test.UnitSpec
import arrow.unsafe
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicReference

@ObsoleteCoroutinesApi
@Suppress("RedundantSuspendModifier")
@RunWith(KotlinTestRunner::class)
class EffectsSuspendDSLTests() : UnitSpec() {

  private val ctxA = newSingleThreadContext("A")
  private val ctxB = newSingleThreadContext("B")

  init {

    /**
     * Effectful programs are allowed to run at the edge of the world inside
     * explicitly user denoted unsafe blocks.
     */
    "Running effects requires an explicit `unsafe` context" {
      /**
       * A pure expression is defined in the environment
       */
      val helloWorld: String =
        "Hello World"

      /**
       * side effects always are `suspended`.
       * This prevents them from running in the environment without an
       * effectful continuation in scope
       */
      suspend fun printHello(): Unit =
        println(helloWorld)

      /**
       * An `fx` block encapsulates the composition of an effectful program
       * and allows side-effects flagged as `suspended` to bind and compose as long as
       * they are declared within an `effect` block.
       *
       * Effect blocks suspend side effect in the monadic computation of the runtime
       * data type which it needs to be at least able to provide a `MonadDefer` extension.
       */
      val program: IO<String> = IO.fx {
        helloWorld
      }
      unsafe { runBlocking { program } } shouldBe helloWorld
    }

    "Direct syntax for concurrent operations" {
      val textContext = newCountingThreadFactory("test", 6) // We fork 4x in the test below, so this is the size of our pool.
        .asCoroutineContext()

      suspend fun getThreadName(): String = Thread.currentThread().name

      val program = IO.fx {
        // note how the receiving value is typed in the environment and not inside IO despite being effectful and non-blocking parallel computations
        val result: List<String> = !textContext.parMapN(
          effect { getThreadName() },
          effect { getThreadName() }
        ) { a, b -> listOf(a, b) }
        result
      }
      unsafe { runBlocking { program } }.distinct().size shouldBe 2
    }

    "raiseError" {
      shouldThrow<TestError> {
        fxTest {
          IO.fx {
            !TestError.raiseError<Int>()
          }
        }
      }
    }

    "handleError" {
      fxTest {
        IO.fx {
          !handleError({ throw TestError }) { 1 }
        }
      } shouldBe 1
    }

    "Option.getOrRaiseError success case" {
      fxTest {
        IO.fx {
          !Option(1).getOrRaiseError { throw TestError }
        }
      } shouldBe 1
    }

    "Option.getOrRaiseError error case" {
      shouldThrow<TestError> {
        fxTest {
          IO.fx {
            !none<Int>().getOrRaiseError { throw TestError }
          }
        }
      }
    }

    "Either.getOrRaiseError success case" {
      fxTest {
        IO.fx {
          !1.right().getOrRaiseError { throw TestError }
        }
      } shouldBe 1
    }

    "Either.getOrRaiseError error case" {
      shouldThrow<TestError> {
        fxTest {
          IO.fx {
            !1.left().getOrRaiseError { throw TestError }
          }
        }
      }
    }

    "Try.getOrRaiseError success case" {
      fxTest {
        IO.fx {
          !Try { 1 }.getOrRaiseError { throw TestError }
        }
      } shouldBe 1
    }

    "Try.getOrRaiseError error case" {
      shouldThrow<TestError> {
        fxTest {
          IO.fx {
            !Failure(TestError).getOrRaiseError { throw TestError }
          }
        }
      }
    }

    "attempt success" {
      fxTest {
        IO.fx {
          !attempt { 1 }
        }
      } shouldBe Right(1)
    }

    "attempt failure" {
      fxTest {
        IO.fx {
          !attempt { throw TestError }
        }
      } shouldBe Left(TestError)
    }

    "suspend () -> A ≅ Kind<F, A> isomorphism" {
      fxTest {
        IO.fx {
          val (suspendedValue) = suspend { 1 }.effect()
          val (ioValue) = IO.just(1)
          suspendedValue == ioValue
        }
      } shouldBe true
    }

    "asyncCallback" {
      val result = 1
      fxTest {
        IO.fx {
          val asyncResult = !async<Int> { cb ->
            cb(Right(result))
          }
          asyncResult
        }
      } shouldBe result
    }

    "continueOn" {
      fxTest {
        IO.fx {
          continueOn(ctxA)
          val contextA = !effect { Thread.currentThread().name }
          continueOn(ctxB)
          val contextB = !effect { Thread.currentThread().name }
          contextA != contextB
        }
      } shouldBe true
    }

    "CoroutineContext.defer" {
      fxTest {
        IO.fx {
          val contextA = !effect(ctxA) { Thread.currentThread().name }
          val contextB = !effect(ctxB) { Thread.currentThread().name }
          contextA != contextB
        }
      } shouldBe true
    }

    "bracketCase success" {
      val msg: AtomicReference<Int> = AtomicReference(0)
      val const = 1
      fxTest {
        IO.fx {
          !bracketCase(
            f = { const },
            release = { n, exit -> msg.set(const) },
            use = { it }
          )
        }
      }
      msg.get() shouldBe const
    }

    /** broken in master, release behavior is off */
    "bracketCase failure" {
      val msg: AtomicReference<Int> = AtomicReference(0)
      val const = 1
      shouldThrow<TestError> {
        fxTest {
          IO.fx {
            !bracketCase(
              f = { const },
              release = { n, exit -> msg.set(const) },
              use = { throw TestError }
            )
          }
        }
      }
      msg.get() shouldBe const
    }

    "startFiber" {
      val const = 1
      fxTest {
        IO.fx {
          val fiber = !NonBlocking.startFiber(effect { const })
          val (n) = fiber.join()
          n
        }
      } shouldBe const
    }

    "List.flatTraverse syntax" {
      fxTest {
        IO.fx {
          val result = !listOf(
            suspend { 1 },
            suspend { 2 },
            suspend { 3 }
          ).flatTraverse {
            listOf(it * 2)
          }
          result
        }
      } shouldBe listOf(2, 4, 6)
    }

    "List.traverse syntax" {
      fxTest {
        IO.fx {
          !listOf(
            suspend { 1 },
            suspend { 2 },
            suspend { 3 }
          ).sequence()
        }
      } shouldBe listOf(1, 2, 3)
    }

    "List.sequence syntax" {
      fxTest {
        IO.fx {
          !listOf(
            suspend { 1 },
            suspend { 2 },
            suspend { 3 }
          ).sequence()
        }
      } shouldBe listOf(1, 2, 3)
    }

    "List.parTraverse syntax" {
      val main = Thread.currentThread().name
      fxTest {
        IO.fx {
          val result = !NonBlocking.parSequence(listOf(
            effect { Thread.currentThread().name },
            effect { Thread.currentThread().name },
            effect { Thread.currentThread().name }
          ))
          result.any {
            it == main
          }
        }
      } shouldBe false
    }

    "List.parSequence syntax" {
      val main = Thread.currentThread().name
      fxTest {
        IO.fx {
          val result = !NonBlocking.parSequence(listOf(
            effect { Thread.currentThread().name },
            effect { Thread.currentThread().name },
            effect { Thread.currentThread().name }
          ))
          result.any {
            it == main
          }
        }
      } shouldBe false
    }

    "FX supports polymorphism" {
      val const = 1

      suspend fun sideEffect(): Int =
        const

      fun <F> Concurrent<F>.program(): Kind<F, Int> =
        fx.concurrent { !effect { sideEffect() } }

      fun <F> UnsafeRun<F>.main(fx: Concurrent<F>): Int =
        unsafe { runBlocking { fx.program() } }

      IO.unsafeRun().main(IO.concurrent()) shouldBe const
    }

    "(suspend () -> A) <-> Kind<F, A>" {
      val done = "done"
      suspend fun sideEffect(): String {
        println("Boom!")
        return done
      }
      fxTest {
        IO.fx {
          val (appliedPureEffect1: String) = effect { sideEffect() }
          val appliedPureEffect2: String = !effect { sideEffect() }
          appliedPureEffect1
        }
      } shouldBe done
    }
  }
}

fun <A> fxTest(f: () -> IO<A>): A =
  unsafe { runBlocking(f) }

object TestError : Throwable()
