package arrow.resilience

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.nonFatalOrThrow
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException


/**
 * The saga design pattern is a way to manage data consistency across microservices in distributed
 * transaction scenarios. A [Saga] is useful when you need to manage data in a consistent manner
 * across services in distributed transaction scenarios. Or when you need to compose multiple
 * `actions` with a `compensation` that needs to run in a transaction like style.
 *
 * For example, let's say that we have the following domain types `Order`, `Payment`.
 *
 * ```kotlin
 * data class Order(val id: UUID, val amount: Long)
 * data class Payment(val id: UUID, val orderId: UUID)
 * ```
 *
 * The creation of an `Order` can only remain when a payment has been made. In SQL, you might run
 * this inside a transaction, which can automatically roll back the creation of the `Order` when the
 * creation of the Payment fails.
 *
 * When you need to do this across distributed services, or a multiple atomic references, etc. You
 * need to manually facilitate the rolling back of the performed actions, or compensating actions.
 *
 * The [Saga] type, and [saga] DSL remove all the boilerplate of manually having to facilitate this
 * with a convenient suspending DSL.
 *
 * ```kotlin
 * data class Order(val id: UUID, val amount: Long)
 * suspend fun createOrder(): Order = Order(UUID.randomUUID(), 100L)
 * suspend fun deleteOrder(order: Order): Unit = println("Deleting $order")
 *
 * data class Payment(val id: UUID, val orderId: UUID)
 * suspend fun createPayment(order: Order): Payment = Payment(UUID.randomUUID(), order.id)
 * suspend fun deletePayment(payment: Payment): Unit = println("Deleting $payment")
 *
 * suspend fun Payment.awaitSuccess(): Unit = throw RuntimeException("Payment Failed")
 *
 * suspend fun main() {
 *   saga {
 *     val order = saga({ createOrder() }) { deleteOrder(it) }
 *     val payment = saga { createPayment(order) }, ::deletePayment)
 *     payment.awaitSuccess()
 *   }.transact()
 * }
 * ```
 */
public typealias Saga<A> = suspend SagaScope.() -> A

/** DSL that enables the [Saga] pattern in a `suspend` DSL. */
@SagaDSLMarker
public interface SagaScope {

  /**
   * Run an [action] to produce a value of type [A] and _install_ a [compensation] to undo the
   * action.
   */
  @SagaDSLMarker
  public suspend fun <A> saga(
    action: suspend SagaActionStep.() -> A,
    compensation: suspend (A) -> Unit,
  ): A

  /** Executes a [Saga] and returns its value [A] */
  public suspend fun <A> Saga<A>.bind(): A = invoke(this@SagaScope)

  /** Invoke a [Saga] and returns its value [A] */
  public suspend operator fun <A> Saga<A>.invoke(): A = invoke(this@SagaScope)
}

/**
 * The Saga builder which exposes the [SagaScope.bind]. The `saga` builder uses the suspension
 * system to run actions, and automatically register their compensating actions.
 *
 * When the resulting [Saga] fails it will run all the required compensating actions, also when the
 * [Saga] gets cancelled it will respect its compensating actions before returning.
 *
 * By doing so we can guarantee that any transactional like operations made by the [Saga] will
 * guarantee that it results in the correct state.
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <A> saga(noinline block: suspend SagaScope.() -> A): Saga<A> = block

/** Create a lazy [Saga] that will only run when the [Saga] is invoked. */
public fun <A> saga(
  action: suspend SagaActionStep.() -> A,
  compensation: suspend (A) -> Unit
): Saga<A> = saga { saga(action, compensation) }

/**
 * Transact runs the [Saga] turning it into a [suspend] effect that results in [A]. If the saga
 * fails then all compensating actions are guaranteed to run. When a compensating action failed it
 * will be ignored, and the other compensating actions will continue to be run.
 */
public suspend fun <A> Saga<A>.transact(): A {
  val builder = SagaBuilder()
  return guaranteeCase({ invoke(builder) }) { res ->
    when (res) {
      null -> builder.totalCompensation()
      else -> Unit
    }
  }
}

/** DSL Marker for the SagaEffect DSL */
@DslMarker public annotation class SagaDSLMarker

/**
 * Marker object to protect [SagaScope.saga] from calling [SagaScope.bind] in its `action` step.
 */
@SagaDSLMarker
public object SagaActionStep

// Internal implementation of the `saga { }` builder.
@PublishedApi
internal class SagaBuilder(
  private val stack: Atomic<List<suspend () -> Unit>> = Atomic(emptyList())
) : SagaScope {

  @SagaDSLMarker
  override suspend fun <A> saga(
    action: suspend SagaActionStep.() -> A,
    compensation: suspend (A) -> Unit
  ): A =
    guaranteeCase({ action(SagaActionStep) }) { res ->
      // This action failed, so we have no compensate to push on the stack
      // the compensation stack will run in the `transact` stage, this is just the builder
      when (res) {
        null -> Unit
        else -> stack.update(
          function = { listOf(suspend { compensation(res) }) + it },
          transform = { _, new -> new }
        )
      }
    }

  @PublishedApi
  internal suspend fun totalCompensation() {
    stack
      .get()
      .fold<suspend () -> Unit, Throwable?>(null) { acc, finalizer ->
        try {
          finalizer()
          acc
        } catch (e: Throwable) {
          e.nonFatalOrThrow()
          acc?.apply { addSuppressed(e) } ?: e
        }
      }
      ?.let { throw it }
  }
}

private suspend fun <A> guaranteeCase(
  fa: suspend () -> A,
  finalizer: suspend (value: A?) -> Unit
): A {
  val res =
    try {
      fa()
    } catch (e: CancellationException) {
      runReleaseAndRethrow(e) { finalizer(null) }
    } catch (t: Throwable) {
      runReleaseAndRethrow(t) { finalizer(null) }
    }
  withContext(NonCancellable) { finalizer(res) }
  return res
}

private suspend fun runReleaseAndRethrow(original: Throwable, f: suspend () -> Unit): Nothing {
  try {
    withContext(NonCancellable) { f() }
  } catch (e: Throwable) {
    original.addSuppressed(e.nonFatalOrThrow())
  }
  throw original
}
