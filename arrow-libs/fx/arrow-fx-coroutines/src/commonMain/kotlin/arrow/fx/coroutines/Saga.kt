@file:OptIn(ExperimentalContracts::class)

package arrow.fx.coroutines

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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
 *   sagaScope {
 *     val order = saga({ createOrder() }) { deleteOrder(it) }
 *     val payment = saga { createPayment(order) }, ::deletePayment)
 *     payment.awaitSuccess()
 *   }
 * }
 * ```
 */
public typealias Saga<A> = Resource<A>

/**
 *  _Install_ a [compensation] that runs on failure of [this] saga.
 */
@ResourceDSL
public fun ResourceScope.compensate(compensation: suspend (Throwable) -> Unit) {
  onRelease { exit -> exit.errorOrNull?.let { compensation(it) } }
}

/**
 * The Saga builder which exposes the [ResourceScope]. The `saga` builder uses the suspension
 * system to run actions, and automatically register their compensating actions.
 *
 * When the resulting [Saga] fails it will run all the required compensating actions, also when the
 * [Saga] gets cancelled it will respect its compensating actions before returning.
 *
 * By doing so we can guarantee that any transactional like operations made by the [Saga] will
 * guarantee that it results in the correct state.
 */
@Suppress("NOTHING_TO_INLINE")
@ScopeDSL
public inline fun <A> saga(noinline block: Saga<A>): Saga<A> = resource(block)


/**
 * Runs the [Saga] turning it into a [suspend] effect that results in [A]. If the saga
 * fails then all compensating actions are guaranteed to run. When a compensating action fails it
 * will be ignored, and the other compensating actions will continue to be run, then the failure will
 * be rethrown.
 */
@ScopeDSL
public suspend inline fun <A> sagaScope(action: Saga<A>): A {
  contract {
    callsInPlace(action, InvocationKind.EXACTLY_ONCE)
  }
  return resourceScope(action)
}

/**
 * Run an [action] to produce a value of type [A] and _install_ a [compensation] to undo the
 * action.
 */
@ResourceDSL
public suspend inline fun <A> ResourceScope.saga(
  action: suspend () -> A,
  crossinline compensation: suspend (A) -> Unit,
): A {
  contract {
    callsInPlace(action, InvocationKind.EXACTLY_ONCE)
  }
  return action().also { a -> compensate { compensation(a) } }
}
