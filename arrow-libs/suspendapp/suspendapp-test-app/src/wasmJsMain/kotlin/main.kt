fun main() =
  app(getEnv("TASK"))

fun getEnv(name: String): String? = js("process.env[name]")
