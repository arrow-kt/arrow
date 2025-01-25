package arrow.resilience.ktor.client

import arrow.atomic.AtomicInt
import arrow.resilience.CircuitBreaker
import arrow.resilience.Schedule
import arrow.resilience.retry
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.isSaved
import io.ktor.client.plugins.HttpRequestRetryEvent
import io.ktor.client.plugins.HttpRetryEventData
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.KtorDsl
import kotlinx.coroutines.CompletableJob

@KtorDsl
public class ArrowResilienceConfig {
  public var schedule: Schedule<Throwable, *> = Schedule.identity()
  public var circuitBreaker: CircuitBreaker? = null
}

@Suppress("INVISIBLE_REFERENCE")
public val ArrowResilience: ClientPlugin<ArrowResilienceConfig> = createClientPlugin(
  "ArrowResilience",
  ::ArrowResilienceConfig
) {
  on(Send) { request ->
    val retryCount = AtomicInt(0)
    pluginConfig.schedule.retry {
      val protected: suspend (suspend () -> HttpClientCall) -> HttpClientCall =
        when (val circuitBreaker = pluginConfig.circuitBreaker) {
          null -> { x -> x() }
          else -> { x -> circuitBreaker.protectOrThrow(x) }
        }
      protected {
        val subRequest = prepareRequest(request)
        try {
          proceed(subRequest).also { call ->
            call.response.throwOnInvalidResponseBody()
            val data = HttpRetryEventData(subRequest, retryCount.incrementAndGet(), call.response, null)
            client.monitor.raise(HttpRequestRetryEvent, data)
          }
        } catch (e: Throwable) {
          val data = HttpRetryEventData(subRequest, retryCount.incrementAndGet(), null, e)
          client.monitor.raise(HttpRequestRetryEvent, data)
          throw e
        }
      }
    }
  }
}

// taken from HttpRequestRetry plugin
// https://github.com/ktorio/ktor/blob/main/ktor-client/ktor-client-core/common/src/io/ktor/client/plugins/HttpRequestRetry.kt
private fun prepareRequest(request: HttpRequestBuilder): HttpRequestBuilder {
  val subRequest = HttpRequestBuilder().takeFrom(request)
  request.executionContext.invokeOnCompletion { cause ->
    val subRequestJob = subRequest.executionContext as CompletableJob
    if (cause == null) {
      subRequestJob.complete()
    } else {
      subRequestJob.completeExceptionally(cause)
    }
  }
  return subRequest
}

@OptIn(InternalAPI::class)
private suspend fun HttpResponse.throwOnInvalidResponseBody(): Boolean {
  // wait for saved content to pass through intermediate processing
  // if the encoding is wrong, then this will throw an exception
  return isSaved && rawContent.awaitContent()
}
