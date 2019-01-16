package arrow.effects

import arrow.core.Continuation
import arrow.core.Either
import arrow.effects.extensions.io.concurrent.invoke
import arrow.effects.typeclasses.fx
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.startCoroutine

fun helloWorld(): String =
  "Hello World"

suspend fun printHello(): Unit =
  println(helloWorld())

val program: IO<String> = fx {
  effect { printHello() }
  helloWorld()
}

@RestrictsSuspension
object unsafe {

  operator fun <A> invoke(f: suspend unsafe.() -> A): A {
    val c = UnsafeContinuation<A>()
    f.startCoroutine(this, c)
    return c.result.get()
  }

}

suspend fun <A> unsafe.runBlocking(fa: () -> IO<A>): A = fa().unsafeRunSync()
suspend fun <A> unsafe.runNonBlocking(fa: () -> IO<A>, cb: (Either<Throwable, A>) -> Unit): Unit = fa().unsafeRunAsync(cb)

private class UnsafeContinuation<A>(
  val result: AtomicReference<A> = AtomicReference()
) : Continuation<A> {

  override fun resume(value: A) {
    result.set(value)
  }

  override fun resumeWithException(exception: Throwable) {
    throw exception
  }

  override val context: CoroutineContext = EmptyCoroutineContext
}

@RunWith(KTestJUnitRunner::class)
class EffectsSuspendDSLTests : UnitSpec() {

  init {
    "Suspended algebras can be composed and interpreted" {
      unsafe { runBlocking { program } } shouldBe helloWorld()
    }
  }

}
