
actual fun exitProcess(code: Int): Nothing {
  js("process.exit(code);")
  error("non-exiting process.exit")
}
