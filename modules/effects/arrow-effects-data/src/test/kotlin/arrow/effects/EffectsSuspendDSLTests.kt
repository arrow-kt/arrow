package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.extensions.io.fx.fx
import arrow.effects.extensions.io.unsafeRun.runBlocking
import arrow.effects.extensions.io.unsafeRun.unsafeRun
import arrow.effects.typeclasses.UnsafeRun
import arrow.effects.typeclasses.suspended.concurrent.Fx
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
      val program: IO<String> = fx {
        helloWorld
      }
      unsafe { runBlocking { program } } shouldBe helloWorld
    }

    "Direct syntax for concurrent operations" {
      suspend fun getThreadName(): String =
        Thread.currentThread().name

      val program = fx {
        // note how the receiving value is typed in the environment and not inside IO despite being effectful and
        // non-blocking parallel computations
        val result: List<String> = ! NonBlocking.parMapN(
          effect { getThreadName() },
          effect { getThreadName() }
        ) { a, b -> listOf(a, b) }
        effect { println(result) }
        result
      }
      unsafe { runBlocking { program } }.distinct().size shouldBe 2
    }

    "raiseError" {
      shouldThrow<TestError> {
        fxTest {
          fx {
            !TestError.raiseError<Int>()
          }
        }
      }
    }

    "handleError" {
      fxTest {
        fx {
          !handleError({ throw TestError }) { 1 }
        }
      } shouldBe 1
    }

    "Option.getOrRaiseError success case" {
      fxTest {
        fx {
          !Option(1).getOrRaiseError { throw TestError }
        }
      } shouldBe 1
    }

    "Option.getOrRaiseError error case" {
      shouldThrow<TestError> {
        fxTest {
          fx {
            !none<Int>().getOrRaiseError { throw TestError }
          }
        }
      }
    }

    "Either.getOrRaiseError success case" {
      fxTest {
        fx {
          !1.right().getOrRaiseError { throw TestError }
        }
      } shouldBe 1
    }

    "Either.getOrRaiseError error case" {
      shouldThrow<TestError> {
        fxTest {
          fx {
            !1.left().getOrRaiseError { throw TestError }
          }
        }
      }
    }

    "Try.getOrRaiseError success case" {
      fxTest {
        fx {
          !Try { 1 }.getOrRaiseError { throw TestError }
        }
      } shouldBe 1
    }

    "Try.getOrRaiseError error case" {
      shouldThrow<TestError> {
        fxTest {
          fx {
            !Failure(TestError).getOrRaiseError { throw TestError }
          }
        }
      }
    }

    "attempt success" {
      fxTest {
        fx {
          !attempt { 1 }
        }
      } shouldBe Right(1)
    }

    "attempt failure" {
      fxTest {
        fx {
          !attempt { throw TestError }
        }
      } shouldBe Left(TestError)
    }

    "suspend () -> A ≅ Kind<F, A> isomorphism" {
      fxTest {
        fx {
          val (suspendedValue) = suspend { 1 }.effect()
          val (ioValue) = IO.just(1)
          suspendedValue == ioValue
        }
      } shouldBe true
    }

    "asyncCallback" {
      val result = 1
      fxTest {
        fx {
          val asyncResult = !async<Int> { cb ->
            cb(Right(result))
          }
          asyncResult
        }
      } shouldBe result
    }

    "continueOn" {
      fxTest {
        fx {
          continueOn(newSingleThreadContext("A"))
          val contextA = !effect { Thread.currentThread().name }
          continueOn(newSingleThreadContext("B"))
          val contextB = !effect { Thread.currentThread().name }
          contextA != contextB
        }
      } shouldBe true
    }

    "CoroutineContext.defer" {
      fxTest {
        fx {
          val contextA = !newSingleThreadContext("A").effect { Thread.currentThread().name }
          val contextB = !newSingleThreadContext("B").effect { Thread.currentThread().name }
          contextA != contextB
        }
      } shouldBe true
    }

    "bracketCase success" {
      val msg: AtomicReference<Int> = AtomicReference(0)
      val const = 1
      fxTest {
        fx {
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
          fx {
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
        fx {
          val fiber = !NonBlocking.startFiber(effect { const })
          val (n) = fiber.join()
          n
        }
      } shouldBe const
    }

    "List.flatTraverse syntax" {
      fxTest {
        fx {
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
        fx {
          !listOf(
            suspend { 1 },
            suspend { 2 },
            suspend { 3 }
          ).traverse(::effectIdentity)
        }
      } shouldBe listOf(1, 2, 3)
    }

    "List.sequence syntax" {
      fxTest {
        fx {
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
        fx {
          val result = !NonBlocking.parTraverse(
            listOf(
              effect { Thread.currentThread().name },
              effect { Thread.currentThread().name },
              effect { Thread.currentThread().name }
            ),
            ::identity
          )
          result.any {
            it == main
          }
        }
      } shouldBe false
    }

    "List.parSequence syntax" {
      val main = Thread.currentThread().name
      fxTest {
        fx {
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

      fun <F> Fx<F>.program(): Kind<F, Int> =
        fx { !effect { sideEffect() } }

      fun <F> UnsafeRun<F>.main(fx: Fx<F>): Int =
        unsafe { runBlocking { fx.program() } }

      IO.unsafeRun().main(IO.fx()) shouldBe const
    }

    "(suspend () -> A) <-> Kind<F, A>" {
      val done = "done"
      suspend fun sideEffect(): String {
        println("Boom!")
        return done
      }
      fxTest {
        fx {
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

