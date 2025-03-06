import org.junit.jupiter.api.condition.EnabledIf

@EnabledIf("enabled")
class WasmJsSpec : JsSpec() {
  override val config: JsTestConfig get() = Companion.config
  companion object {
    val config = JsTestConfig("wasmJsNodeRun")

    @JvmStatic
    fun enabled() = config.validConfig()
  }
}
