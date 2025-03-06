import org.junit.jupiter.api.condition.EnabledIf
import kotlin.io.path.absolutePathString

@EnabledIf("enabled")
class WasmJsSpec : JsSpec() {
  override val config: JsTestConfig get() = Companion.config
  companion object {
    val config = JsTestConfig("wasmJsNodeRun")
    @JvmStatic
    fun enabled() = config.validConfig()
  }
}
