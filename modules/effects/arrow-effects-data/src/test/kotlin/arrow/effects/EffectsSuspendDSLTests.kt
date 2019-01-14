package arrow.effects

import arrow.effects.typeclasses.fx
import arrow.effects.extensions.io.concurrent.invoke
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

fun helloWorld(): String =
  "Hello World"

suspend fun printHelloWorld(): Unit =
  println(helloWorld())

val program: IO<Unit> = fx {
  effect { printHelloWorld() } // compiles and suspends all side effects in the context of IO
}

@RunWith(KTestJUnitRunner::class)
class EffectsSuspendDSLTests : UnitSpec() {

  init {
    "Suspended algebras can be composed and interpreted" {
      program.unsafeRunSync() shouldBe Unit
    }
  }

}
