import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

abstract class SuspendAppTest : ProcessProvider {
  @Test
  fun delay() = runTest {
    val (process, output) = execute("delay")
    process.exitValue() shouldBe 0
    output.shouldForOne { it.line shouldBe "resource clean complete" }
  }

  @Test
  fun fail() = runTest {
    val (process, output) = execute("fail")
    process.exitValue() shouldBe 255
    output.shouldForOne { it.line shouldBe "resource clean complete" }
      .shouldForOne { it.line shouldEndWith "IllegalStateException: BOOM!" }
  }

  @Test
  fun childFailure() = runTest {
    val (process, output) = execute("childfail")
    process.exitValue() shouldBe 255
    output.shouldForOne { it.line shouldBe "resource clean complete" }
      .shouldForOne { it.line shouldEndWith "IllegalStateException: boom." }
  }

  @Test
  fun waitAndSignalSigterm() = waitAndSignal(Signal.SIGTERM)

  @Test
  fun waitAndSignalSigint() = waitAndSignal(Signal.SIGINT)

  private fun waitAndSignal(signal: Signal) = runTest {
    val (process, output) = execute("wait") {
      delay(1.seconds)
      sendSignal(signal)
    }
    process.exitValue() shouldBe signal.code + 128
    output.shouldForOne { it.line shouldBe "resource clean complete" }
  }
}
