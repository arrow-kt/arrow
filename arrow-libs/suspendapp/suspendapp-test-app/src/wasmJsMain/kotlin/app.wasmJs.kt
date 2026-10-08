@file:OptIn(ExperimentalWasmJsInterop::class)

actual fun exitProcess(code: Int): Nothing {
  jsExit(code)
  error("did not exit")
}

@Suppress("unused")
private fun jsExit(code: Int) {
  js("process.exit(code);")
}
