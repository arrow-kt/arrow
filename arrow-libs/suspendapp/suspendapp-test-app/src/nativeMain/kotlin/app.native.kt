import kotlin.system.exitProcess

actual fun exitProcess(code: Int): Nothing = exitProcess(code)
