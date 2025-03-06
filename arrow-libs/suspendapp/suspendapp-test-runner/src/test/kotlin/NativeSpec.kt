import org.junit.jupiter.api.condition.EnabledIf
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

@EnabledIf("enabled")
class NativeSpec : SuspendAppTest() {
  override fun prepareProcess(mode: String): ProcessBuilder =
    ProcessBuilder(executable!!.pathString, mode)
      .directory(workdir?.toFile())

  companion object {
    val name = System.getProperties().stringPropertyNames()
      .first { it.startsWith("runReleaseExecutable") && it.endsWith(".executable") }
      .substringBeforeLast('.')
    val executable = System.getProperty("$name.executable")?.let(::Path)
    val workdir = System.getProperty("$name.workdir")?.let(::Path)

    @JvmStatic
    fun enabled() = (executable?.exists() ?: false) && (workdir?.exists() ?: false)
  }
}
