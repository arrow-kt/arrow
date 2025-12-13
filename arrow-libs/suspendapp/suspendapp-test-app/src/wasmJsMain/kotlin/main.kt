fun main() =
  app(getEnv("TASK"))

@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
fun getEnv(name: String): String? = js("process.env[name]")
