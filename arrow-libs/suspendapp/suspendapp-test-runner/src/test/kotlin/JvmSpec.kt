import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.condition.EnabledIf
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.jvm.optionals.getOrNull

@EnabledIf("enabled")
class JvmSpec : SuspendAppTest() {
  override fun prepareProcess(mode: String): ProcessBuilder {
    val command = listOf(java!!, "-jar", jar!!.pathString, mode)
    return when (mode) {
      in SIGNALLED_MODES -> {
        assumeTrue(hasPerl) { "a POSIX system with perl is required to reset inherited SIGINT/SIGTERM dispositions before signalling the JVM" }
        ProcessBuilder(listOf("perl", "-e", RESET_SIGNALS_THEN_EXEC) + command)
      }

      else -> ProcessBuilder(command)
    }
  }

  companion object {
    val java = ProcessHandle.current().info().command().getOrNull()
    val jar = System.getProperty("jvmJar")?.let(::Path)

    private val SIGNALLED_MODES = setOf("wait", "timeout")

    @JvmStatic
    fun enabled() = java != null && jar?.exists() ?: false
  }
}

// Let the JDK resolve `perl` against PATH itself, rather than re-implementing the lookup.
private val hasPerl: Boolean by lazy {
  runCatching {
    Runtime.getRuntime().exec(arrayOf("perl", "-e", "1")).waitFor() == 0
  }.getOrDefault(false)
}

// A JVM that inherits SIG_IGN for SIGINT/SIGTERM (e.g. forked from a detached Gradle daemon) never installs
// its own handlers, so the signal is silently dropped. Use perl to reset the disposition before executing the child JVM.
private const val RESET_SIGNALS_THEN_EXEC =
  $$"""$SIG{INT} = 'DEFAULT'; $SIG{TERM} = 'DEFAULT'; exec @ARGV or die "exec failed: $!""""
