import org.junit.jupiter.api.condition.EnabledIf
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

@EnabledIf("enabled")
class NativeSpec : SuspendAppTest() {
  override fun prepareProcess(mode: String): ProcessBuilder = ProcessBuilder(executable!!.pathString, mode)
    .directory(workdir?.toFile())

  companion object {
    val name = System.getProperties().stringPropertyNames()
      .firstOrNull { it.startsWith("runReleaseExecutable") && it.endsWith(".executable") }
      ?.substringBeforeLast('.')
    val executable = name?.let { System.getProperty("$it.executable") }?.let(::Path)
    val workdir = name?.let { System.getProperty("$it.workdir") }?.let(::Path)

    init {
      println("Running native tests... $executable in $workdir...")
    }

    @JvmStatic
    fun enabled() = (executable?.exists() ?: false) && (workdir?.exists() ?: false)
  }
}
