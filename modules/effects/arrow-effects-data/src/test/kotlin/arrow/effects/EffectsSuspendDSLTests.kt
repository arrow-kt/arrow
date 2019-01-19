package arrow.effects

import arrow.core.Option
import arrow.core.left
import arrow.core.none
import arrow.core.right
import arrow.effects.extensions.io.concurrent.invoke
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.test.UnitSpec
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlinx.coroutines.Dispatchers
import org.junit.runner.RunWith

@Suppress("RedundantSuspendModifier")
@RunWith(KotlinTestRunner::class)
class EffectsSuspendDSLTests : UnitSpec() {

  init {

    /**
     * Effectful programs are allowed to run at the edge of the world inside
     * explicitly user denoted unsafe blocks.
     */
    "Running effects requires an explicit `unsafe` context" {
      /**
       * A pure expression is defined in the environment
       */
      fun helloWorld(): String =
        "Hello World"

      /**
       * side effects always are `suspended`.
       * This prevents them from running in the environment without an
       * effectful continuation in scope
       */
      suspend fun printHello(): Unit =
        println(helloWorld())

      /**
       * An `fx` block encapsulates the composition of an effectful program
       * and allows side-effects flagged as `suspended` to bind and compose as long as
       * they are declared within an `effect` block.
       *
       * Effect blocks suspend side effect in the monadic computation of the runtime
       * data type which it needs to be at least able to provide a `MonadDefer` extension.
       */
      val program: IO<String> = fx {
        effect { printHello() }
        helloWorld()
      }
      unsafe { runBlocking { program } } shouldBe helloWorld()
    }

    "Direct syntax for concurrent operations" {
      suspend fun getThreadName(): String =
        Thread.currentThread().name

      val program = fx {
        // note how the receiving value is typed in the environment and not inside IO despite being effectful and
        // non-blocking parallel computations
        val result: List<String> = parMap(
          Dispatchers.Default,
          { getThreadName() },
          { getThreadName() }
        ) { a, b ->
          listOf(a, b)
        }
        effect { println(result) }
        result
      }
      unsafe { runBlocking { program } }.distinct().size shouldBe 2
    }

    "raiseError" {
      shouldThrow<TestError> {
        fxTest {
          fx {
            TestError.raiseError<Int>()
          }
        }
      }
    }

    "handleError" {
      fxTest {
        fx {
          handleError({ throw TestError }) { 1 }
        }
      } shouldBe 1
    }

    "Option.getOrRaiseError success case" {
      fxTest {
        fx {
          Option(1).getOrRaiseError { throw TestError }
        }
      } shouldBe 1
    }

    "Option.getOrRaiseError error case" {
      shouldThrow<TestError> {
        fxTest {
          fx {
            none<Int>().getOrRaiseError { throw TestError }
          }
        }
      }
    }

    "Either.getOrRaiseError success case" {
      fxTest {
        fx {
          1.right().getOrRaiseError { throw TestError }
        }
      } shouldBe 1
    }

    "Either.getOrRaiseError error case" {
      shouldThrow<TestError> {
        fxTest {
          fx {
            1.left().getOrRaiseError { throw TestError }
          }
        }
      }
    }

  }
}

fun <A> fxTest(f: () -> IO<A>): A =
  unsafe { runBlocking(f) }

object TestError : Throwable()