package arrow.resilience.ktor.client

import arrow.resilience.CircuitBreaker
import arrow.resilience.CircuitBreaker.OpeningStrategy.Count
import arrow.resilience.CircuitBreaker.OpeningStrategy.SlidingWindow
import io.ktor.client.HttpClient
import io.ktor.client.call.save
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.util.AttributeKey
import io.ktor.utils.io.KtorDsl
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlinx.coroutines.CompletableJob

/**
 * A plugin that enables the client to work through a [CircuitBreaker],
 * when the remote service gets overloaded the [CircuitBreaker] will _open_ and now allow any traffic to pass through.
 *
 * Typical usages which shows the default configuration:
 *
 * ```kotlin
 * install(HttpCircuitBreaker) {
 *   circuitBreaker(
 *     resetTimeout = 5.seconds,
 *     windowDuration = 5.seconds,
 *     maxFailures = 10
 *   )
 * }
 * ```
 */
public class HttpCircuitBreaker internal constructor(configuration: Configuration) {

  private val circuitBreaker: CircuitBreaker = configuration.breaker

  /**
   * Contains [HttpCircuitBreaker] configurations settings.
   */
  @OptIn(ExperimentalTime::class)
  @KtorDsl
  public class Configuration {
    internal var breaker: CircuitBreaker

    init {
      @Suppress("MagicNumber")
      breaker = CircuitBreaker(5.seconds, SlidingWindow(TimeSource.Monotonic, 5.seconds, 10))
    }

    public fun circuitBreaker(resetTimeout: Duration, windowDuration: Duration, maxFailures: Int) {
      breaker = CircuitBreaker(resetTimeout, SlidingWindow(TimeSource.Monotonic, windowDuration, maxFailures))
    }

    public fun circuitBreaker(resetTimeout: Duration, maxFailures: Int) {
      breaker = CircuitBreaker(resetTimeout, Count(maxFailures))
    }
  }

  internal fun intercept(client: HttpClient) {
    client.plugin(HttpSend).intercept { request ->
      circuitBreaker.protectOrThrow {
        val call = execute(prepareRequest(request))
        val response = call.save().response
        val status = response.status
        if (request.expectSuccess && !status.isSuccess()) {
          val text = response.bodyAsText()
          throw when (status.value) {
            in 300..399 -> RedirectResponseException(response, text)
            in 400..499 -> ClientRequestException(response, text)
            in 500..599 -> ServerResponseException(response, text)
            else -> ResponseException(response, text)
          }
        }
        call
      }
    }
  }

  private fun prepareRequest(request: HttpRequestBuilder): HttpRequestBuilder {
    val subRequest = HttpRequestBuilder().takeFrom(request)
    request.executionContext.invokeOnCompletion { cause ->
      val subRequestJob = subRequest.executionContext as CompletableJob
      if (cause == null) subRequestJob.complete()
      else subRequestJob.completeExceptionally(cause)
    }
    return subRequest
  }

  public companion object Plugin : HttpClientPlugin<Configuration, HttpCircuitBreaker> {
    override val key: AttributeKey<HttpCircuitBreaker> = AttributeKey("CircuitBreakerFeature")

    // TODO LOG CircuitBreaker events
//    public val HttpRequestScheduleEvent: EventDefinition<RetryEventData> = EventDefinition()

    override fun prepare(block: Configuration.() -> Unit): HttpCircuitBreaker {
      val configuration = Configuration().apply(block)
      return HttpCircuitBreaker(configuration)
    }

    override fun install(plugin: HttpCircuitBreaker, scope: HttpClient) {
      plugin.intercept(scope)
    }
  }
}
