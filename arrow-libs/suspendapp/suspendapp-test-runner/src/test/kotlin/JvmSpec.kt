import org.junit.jupiter.api.condition.EnabledIf
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.jvm.optionals.getOrNull

@EnabledIf("enabled")
class JvmSpec : SuspendAppTest() {
  override fun prepareProcess(mode: String) = ProcessBuilder(java, "-jar", jar!!.pathString, mode)

  companion object {
    val java = ProcessHandle.current().info().command().getOrNull()
    val jar = System.getProperty("jvmJar")?.let(::Path)

    @JvmStatic
    fun enabled() = java != null && jar?.exists() ?: false
  }
}
