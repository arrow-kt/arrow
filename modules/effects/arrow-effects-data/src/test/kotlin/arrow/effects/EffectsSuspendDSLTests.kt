package arrow.effects

import arrow.effects.extensions.io.concurrent.invoke
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.test.UnitSpec
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import kotlinx.coroutines.Dispatchers
import org.junit.runner.RunWith
import kotlin.coroutines.EmptyCoroutineContext

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
  }

}
