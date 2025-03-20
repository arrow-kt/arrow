import io.kotest.assertions.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun interface ProcessProvider {
  fun prepareProcess(mode: String): ProcessBuilder
}

data class OutputLine(val line: String, val source: String)

suspend fun ProcessProvider.execute(
  mode: String,
  timeout: Duration = 10.seconds,
  interact: suspend Process.() -> Unit = {},
) = withContext(Dispatchers.IO) {
  val process = prepareProcess(mode).start()

  println("Started: ${process.info()} ($mode)")

  @OptIn(ExperimentalCoroutinesApi::class)
  val outputChannel = produce {
    launch { process.inputReader().useLines { lines -> lines.forEach { send(OutputLine(it, "stdout")) } } }
    launch { process.errorReader().useLines { lines -> lines.forEach { send(OutputLine(it, "stderr")) } } }
  }

  val output = async {
    outputChannel.consumeAsFlow()
      .onEach { println("[${this@execute}:$mode:${it.source}] ${it.line}") }
      .toList()
  }

  val interaction = launch { process.interact() }
  if (!process.waitFor(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)) {
    process.destroyForcibly()
    process.waitFor()
    fail("process didn't finish")
  }
  interaction.cancel()

  process to output.await()
}

fun Process.sendSignal(signal: Signal) {
  Runtime.getRuntime().exec(arrayOf("kill", "-$signal", pid().toString())).waitFor()
}

enum class Signal(val code: Int) {
  SIGINT(2),
  SIGTERM(15),
}
