import io.kotest.inspectors.shouldForAtLeastOne
import io.kotest.inspectors.shouldForNone
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
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
      .shouldForAtLeastOne { it.line shouldEndWith "IllegalStateException: BOOM!" }
  }

  @Test
  fun exit() = runTest {
    val (process, output) = execute("exit")
    process.exitValue() shouldBe 42
    output
      .filter { it.source == "stdout" }
      .shouldForNone { it.line shouldBe "resource clean complete" }
      .last().line shouldBe "Running ExitProcess"
  }

  @Test
  fun childFailure() = runTest {
    val (process, output) = execute("childfail")
    process.exitValue() shouldBe 255
    output.shouldForOne { it.line shouldBe "resource clean complete" }
      .shouldForAtLeastOne { it.line shouldEndWith "IllegalStateException: boom." }
  }

  @Test
  fun exitApp() = runTest {
    val (process, output) = execute("exitapp")
    process.exitValue() shouldBe 42
    output.shouldForOne { it.line shouldBe "resource clean complete" }
  }

  @Test
  fun childExitApp() = runTest {
    val (process, output) = execute("childexitapp")
    process.exitValue() shouldBe 2
    output.shouldForOne { it.line shouldBe "resource clean complete" }
  }

  @Test
  fun childLaunchExitApp() = runTest {
    val (process, output) = execute("childlaunchexitapp")
    process.exitValue() shouldBe 24
    output.shouldForOne { it.line shouldBe "resource clean complete" }
  }

  @Test
  fun waitAndSignalSigterm() = waitAndSignal(Signal.SIGTERM)

  @Test
  fun waitAndSignalSigint() = waitAndSignal(Signal.SIGINT)

  @Test
  fun waitAndTimeout() = runTest {
    val (process, output) = execute("timeout") {
      delay(0.5.seconds)
      sendSignal(Signal.SIGTERM)
    }

    // JVM signal handler still trumps the timeout exception and exits with Signal + 128
    // All other platforms exit due to the timeout exception with 255
    // Both outcomes are currently acceptable.
    process.exitValue() shouldBeIn listOf(255, Signal.SIGTERM.code + 128)
    output.shouldForAtLeastOne { it.line shouldContain "Timed out waiting for 5000 ms" }
      .shouldForNone { it.line shouldContain "resource clean complete" }
  }

  private fun waitAndSignal(signal: Signal, mode: String = "wait") = runTest {
    val (process, output) = execute(mode) {
      delay(0.5.seconds)
      sendSignal(signal)
    }
    process.exitValue() shouldBe signal.code + 128
    output.shouldForOne { it.line shouldBe "resource clean complete" }
  }
}
