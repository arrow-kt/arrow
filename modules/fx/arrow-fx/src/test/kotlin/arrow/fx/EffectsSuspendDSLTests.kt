package arrow.fx

import arrow.Kind
import arrow.core.Left
import arrow.core.Right
import arrow.core.internal.AtomicIntW
import arrow.core.identity
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.io.unsafeRun.unsafeRun
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.UnsafeRun
import arrow.test.UnitSpec
import arrow.unsafe
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext

@ObsoleteCoroutinesApi
@Suppress("RedundantSuspendModifier")
class EffectsSuspendDSLTests : UnitSpec() {

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
          !effect { throw TestError }.handleError { 1 }
        }
      } shouldBe 1
    }

    "attempt success" {
      fxTest {
        IO.fx {
          !effect { 1 }.attempt()
        }
      } shouldBe Right(1)
    }

    "attempt failure" {
      fxTest {
        IO.fx {
          !effect { throw TestError }.attempt()
        }
      } shouldBe Left(TestError)
    }

    "suspend () -> A ≅ Kind<F, A> isomorphism" {
      fxTest {
        IO.fx {
          val (suspendedValue) = effect { suspend { 1 }() }
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
      val msg = AtomicIntW(0)
      val const = 1
      fxTest {
        IO.fx {
          !effect { const }.bracketCase(
            release = { n, exit -> effect { msg.value = const } },
            use = { effect { it } }
          )
        }
      }
      msg.value shouldBe const
    }

    /** broken in master, release behavior is off */
    "bracketCase failure" {
      val msg = AtomicIntW(0)
      val const = 1
      shouldThrow<TestError> {
        fxTest {
          IO.fx {
            !effect { const }.bracketCase(
              release = { n, exit -> effect { msg.value = const } },
              use = { effect { throw TestError } }
            )
          }
        }
      }
      msg.value shouldBe const
    }

    "fork" {
      val const = 1
      fxTest {
        IO.fx {
          val fiber = !effect { const }.fork(dispatchers().default())
          val (n) = fiber.join()
          n
        }
      } shouldBe const
    }

    "List.traverse syntax" {
      fxTest {
        IO.fx {
          !listOf(
            effect { 1 },
            effect { 2 },
            effect { 3 }
          ).parTraverse(::identity)
        }
      } shouldBe listOf(1, 2, 3)
    }

    "List.sequence syntax" {
      fxTest {
        IO.fx {
          !listOf(
            effect { 1 },
            effect { 2 },
            effect { 3 }
          ).parSequence()
        }
      } shouldBe listOf(1, 2, 3)
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
