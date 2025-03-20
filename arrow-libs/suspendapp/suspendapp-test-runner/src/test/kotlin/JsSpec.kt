import org.junit.jupiter.api.condition.EnabledIf
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists

@EnabledIf("enabled")
open class JsSpec : SuspendAppTest() {
  open val config: JsTestConfig get() = Companion.config

  override fun prepareProcess(mode: String): ProcessBuilder = ProcessBuilder(config.executable!!.absolutePathString(), config.entrypoint!!.absolutePathString())
    .directory(config.workdir?.toFile())
    .apply { environment()["TASK"] = mode }

  companion object {
    val config = JsTestConfig("jsNodeRun")

    @JvmStatic
    fun enabled() = config.validConfig()
  }
}

open class JsTestConfig(name: String) {
  val executable = System.getProperty("$name.executable")?.let(::Path)
  val entrypoint = System.getProperty("$name.entrypoint")?.let(::Path)
  val workdir = System.getProperty("$name.workdir")?.let(::Path)
  fun validConfig(): Boolean = (executable?.exists() ?: false) && (entrypoint?.exists() ?: false) && (workdir?.exists() ?: false)
}
