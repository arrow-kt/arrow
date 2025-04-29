package arrow.continuations.ktor

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.ResourceScope
import io.ktor.server.application.*
import io.ktor.server.engine.*
import kotlinx.coroutines.delay
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Ktor [ApplicationEngine] as a [Resource]. This [Resource] will gracefully shut down the server
 * When we need to shut down a Ktor service we need to properly take into account a _grace_ period
 * where we still handle requests instead of immediately cancelling any in-flight requests.
 *
 * @param factory Application engine for processing the requests
 * @param port Server listening port. Default is set to 80
 * @param host Host address. Default is set to "0.0.0.0"
 * @param watchPaths specifies path substrings that will be watched for automatic reloading
 * @param preWait preWait a duration to wait before beginning the stop process. During this time,
 *   requests will continue to be accepted. This setting is useful to allow time for the container
 *   to be removed from the load balancer. This is disabled when `io.ktor.development=true`.
 * @param grace grace a duration during which already inflight requests are allowed to continue
 *   before the shutdown process begins.
 * @param timeout timeout a duration after which the server will be forcibly shutdown.
 * @param module Represents configured and running web application, capable of handling requests.
 */
public suspend fun <
  TEngine : ApplicationEngine,
  TConfiguration : ApplicationEngine.Configuration,
  > ResourceScope.server(
  factory: ApplicationEngineFactory<TEngine, TConfiguration>,
  port: Int = 80,
  host: String = "0.0.0.0",
  watchPaths: List<String> = listOf(WORKING_DIRECTORY_PATH),
  preWait: Duration = 30.seconds,
  grace: Duration = 500.milliseconds,
  timeout: Duration = 500.milliseconds,
  module: Application.() -> Unit = {},
): EmbeddedServer<TEngine, TConfiguration> =
  install({
    embeddedServer(factory, host = host, port = port, watchPaths = watchPaths, module = module)
      .warnAndStart()
  }) { engine, _ ->
    engine.release(preWait, grace, timeout)
  }

/**
 * Ktor [ApplicationEngine] as a [Resource]. This [Resource] will gracefully shut down the server
 * When we need to shut down a Ktor service we need to properly take into account a _grace_ period
 * where we still handle requests instead of immediately cancelling any in-flight requests.
 *
 * @param factory Application engine for processing the requests
 * @param rootConfig definition of the core configuration of the server, including modules, paths,
 *   and environment details.
 * @param preWait preWait a duration to wait before beginning the stop process. During this time,
 *   requests will continue to be accepted. This setting is useful to allow time for the container
 *   to be removed from the load balancer. This is disabled when `io.ktor.development=true`.
 * @param grace grace a duration during which already inflight requests are allowed to continue
 *   before the shutdown process begins.
 * @param timeout timeout a duration after which the server will be forcibly shutdown.
 */
public suspend fun <
  TEngine : ApplicationEngine,
  TConfiguration : ApplicationEngine.Configuration,
  > ResourceScope.server(
  factory: ApplicationEngineFactory<TEngine, TConfiguration>,
  rootConfig: ServerConfig,
  configure: TConfiguration.() -> Unit = {},
  preWait: Duration = 30.seconds,
  grace: Duration = 500.milliseconds,
  timeout: Duration = 500.milliseconds,
): EmbeddedServer<TEngine, TConfiguration> =
  install({
    embeddedServer(factory, rootConfig, configure)
      .warnAndStart()
  }) { engine, _ ->
    engine.release(preWait, grace, timeout)
  }

private suspend fun <E: EmbeddedServer<*, *>> E.warnAndStart(): E = also {
  it.warnShutdownHook()
  it.startSuspend()
}

internal expect val ktorShutdownHookEnabled: Boolean

private fun EmbeddedServer<*, *>.warnShutdownHook() {
  if (ktorShutdownHookEnabled) {
    environment.log.warn("Ktor ShutdownHook is enabled, preWait delay may be ignored. See: https://arrow-kt.io/learn/coroutines/suspendapp/ktor")
  }
}

private suspend fun EmbeddedServer<*, *>.release(
  preWait: Duration,
  grace: Duration,
  timeout: Duration,
) {
  if (!application.developmentMode) {
    environment.log.info(
      "prewait delay of ${preWait.inWholeMilliseconds}ms, turn it off using io.ktor.development=true"
    )
    delay(preWait.inWholeMilliseconds)
  }
  environment.log.info("Shutting down HTTP server...")
  stopSuspend(grace.inWholeMilliseconds, timeout.inWholeMicroseconds)
  environment.log.info("HTTP server shutdown!")
}

internal val WORKING_DIRECTORY_PATH: String = SystemFileSystem.resolve(Path(".")).toString()
