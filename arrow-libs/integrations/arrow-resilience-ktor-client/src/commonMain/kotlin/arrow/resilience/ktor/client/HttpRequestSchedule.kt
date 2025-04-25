package arrow.resilience.ktor.client

import arrow.resilience.Schedule
import arrow.resilience.ScheduleStep
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.events.EventDefinition
import io.ktor.util.AttributeKey
import io.ktor.utils.io.KtorDsl
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * A plugin that enables the client to retry failed requests according to [arrow.resilience.Schedule].
 * The default retry policy is 3 retries with exponential jitter'ed delay.
 *
 * Typical usages which shows the default configuration:
 *
 * ```kotlin
 * // use predefined retry policies
 * install(HttpRequestSchedule) {
 *   fun <A> delay() = Schedule.exponential<A>(2.seconds).jittered()
 *
 *   repeat(delay<HttpResponse>.doWhile { request, duration -> request.status.value in 500..599 })
 *   retry(delay<Throwable>().and(Schedule.recurs(3)))
 * }
 *
 * // use custom policies
 * install(HttpRequestSchedule) {
 *   fun <A> delay() = Schedule.spaced<A>(3.seconds)
 *
 *   retry(delay<Throwable>().doWhile { exception, _ -> exception is NetworkError })
 *   repeat(delay<HttpRequest>().doWhile { request, _ -> !response.status.isSuccess() })
 *   modifyRequest { it.headers.append("X_RETRY_COUNT", retryCount.toString()) }
 * }
 * ```
 */
public val HttpRequestSchedule: ClientPlugin<HttpRequestScheduleConfiguration> =
  createClientPlugin("HttpRequestSchedule", ::HttpRequestScheduleConfiguration) {
    fun prepareRequest(request: HttpRequestBuilder): HttpRequestBuilder {
      val subRequest = HttpRequestBuilder().takeFrom(request)
      request.executionContext.invokeOnCompletion { cause ->
        val subRequestJob = subRequest.executionContext as CompletableJob
        if (cause == null) subRequestJob.complete()
        else subRequestJob.completeExceptionally(cause)
      }
      return subRequest
    }

    on(Send) { request ->
      var retryCount = 0

      val modifyRequest: ModifyRequestPerRequest =
        request.attributes.getOrNull(ModifyRequestPerRequestAttributeKey) ?: pluginConfig.modifyRequest

      var repeatStep: ScheduleStep<HttpResponse, *> =
        request.attributes.getOrNull(RepeatPerRequestAttributeKey)?.step ?: pluginConfig.repeatSchedule.step

      var retryStep: ScheduleStep<Throwable, *> =
        request.attributes.getOrNull(RetryPerRequestAttributeKey)?.step ?: pluginConfig.retrySchedule.step

      var call: HttpClientCall
      var lastRetryData: RetryEventData? = null

      while (true) {
        val subRequest = prepareRequest(request)

        val retryData = try {
          if (lastRetryData != null) {
            modifyRequest(ModifyRequestContext(request, lastRetryData), subRequest)
          }
          call = proceed(subRequest)
          when (val decision = repeatStep(call.response)) {
            is Schedule.Decision.Continue -> {
              if (decision.delay != Duration.ZERO) delay(decision.delay)
              repeatStep = decision.step
            }

            is Schedule.Decision.Done -> break
          }
          RetryEventData.Response(subRequest, ++retryCount, call.response)
        } catch (cause: Throwable) {
          when (val decision = retryStep(cause)) {
            is Schedule.Decision.Continue -> {
              if (decision.delay != Duration.ZERO) delay(decision.delay)
              retryStep = decision.step
            }

            is Schedule.Decision.Done -> throw cause
          }
          RetryEventData.Failure(subRequest, ++retryCount, cause)
        }

        lastRetryData = retryData
        client.monitor.raise(HttpRequestScheduleEvent, lastRetryData)
      }
      call
    }
}

/**
 * Contains [HttpRequestSchedule] configurations settings.
 */
@KtorDsl
public class HttpRequestScheduleConfiguration {
  internal var repeatSchedule: Schedule<HttpResponse, *> = Schedule.recurs(0)
  internal var retrySchedule: Schedule<Throwable, *> = Schedule.recurs(0)
  internal var modifyRequest: ModifyRequestPerRequest = ModifyRequestPerRequest { _, _ -> }

  init {
    @Suppress("MagicNumber")
    repeatSchedule =
      Schedule.exponential<HttpResponse>(2.seconds).jittered()
        .doWhile { request, _ -> request.status.value in 500..599 }

    @Suppress("MagicNumber")
    retrySchedule =
      Schedule.exponential<Throwable>(2.seconds).jittered()
        .and(Schedule.recurs(3))
  }

  /**
   * Repeat the request according to the provided Schedule,
   * the output of the schedule is ignored.
   */
  public fun <A> repeat(schedule: Schedule<HttpResponse, A>) {
    repeatSchedule = schedule
  }

  public fun <A> retry(schedule: Schedule<Throwable, A>) {
    retrySchedule = schedule
  }

  public fun modifyRequest(block: suspend ModifyRequestContext.(HttpRequestBuilder) -> Unit) {
    modifyRequest = ModifyRequestPerRequest(block)
  }
}

public data class ModifyRequestContext(val original: HttpRequestBuilder, val lastRetryEventData: RetryEventData) {
  public val request: HttpRequestBuilder = lastRetryEventData.request
  public val retryCount: Int = lastRetryEventData.retryCount

  public fun responseOrNull(): HttpResponse? = when (lastRetryEventData) {
    is RetryEventData.Failure -> null
    is RetryEventData.Response -> lastRetryEventData.response
  }

  public fun exceptionOrNull(): Throwable? = when (lastRetryEventData) {
    is RetryEventData.Response -> null
    is RetryEventData.Failure -> lastRetryEventData.exception
  }
}

/** Occurs on request retry. */
public val HttpRequestScheduleEvent: EventDefinition<RetryEventData> = EventDefinition()

public sealed interface RetryEventData {
  public val request: HttpRequestBuilder
  public val retryCount: Int

  public fun responseOrNull(): HttpResponse? = (this as? Response)?.response
  public fun exceptionOrNull(): Throwable? = (this as? Failure)?.exception

  public data class Response(
    public override val request: HttpRequestBuilder,
    public override val retryCount: Int,
    public val response: HttpResponse
  ) : RetryEventData {
    override fun hashCode(): Int {
      var result = request.hashCode()
      result = 31 * result + retryCount
      result = 31 * result + response.hashCode()
      return result
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Response) return false
      return request == other.request && retryCount == other.retryCount && response == other.response
    }
  }

  public data class Failure(
    public override val request: HttpRequestBuilder,
    public override val retryCount: Int,
    public val exception: Throwable
  ) : RetryEventData {
    override fun hashCode(): Int {
      var result = request.hashCode()
      result = 31 * result + retryCount
      result = 31 * result + exception.hashCode()
      return result
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Failure) return false
      return request == other.request && retryCount == other.retryCount && exception == other.exception
    }
  }
}

public fun interface ModifyRequestPerRequest {
  public suspend operator fun invoke(requestContext: ModifyRequestContext, request: HttpRequestBuilder)
}

/**
 * Configures the [HttpRequestSchedule] plugin on a per-request level.
 */
public fun HttpRequestBuilder.schedule(block: HttpRequestScheduleConfiguration.() -> Unit) {
  val configuration = HttpRequestScheduleConfiguration().apply(block)
  attributes.put(RepeatPerRequestAttributeKey, configuration.repeatSchedule)
  attributes.put(RetryPerRequestAttributeKey, configuration.retrySchedule)
  attributes.put(ModifyRequestPerRequestAttributeKey, configuration.modifyRequest)
}

@Suppress("PrivatePropertyName")
private val RepeatPerRequestAttributeKey =
  AttributeKey<Schedule<HttpResponse, *>>("RepeatPerRequestAttributeKey")

@Suppress("PrivatePropertyName")
private val RetryPerRequestAttributeKey =
  AttributeKey<Schedule<Throwable, *>>("RetryPerRequestAttributeKey")

@Suppress("PrivatePropertyName")
private val ModifyRequestPerRequestAttributeKey =
  AttributeKey<ModifyRequestPerRequest>("ModifyRequestPerRequestAttributeKey")
