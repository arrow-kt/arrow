package arrow.continuations.ktor

internal actual val ktorShutdownHookEnabled: Boolean =
  System.getProperty("io.ktor.server.engine.ShutdownHook", "true") == "true"
