package arrow.scoped

/**
 * The saga design pattern is a way to manage data consistency across microservices in distributed
 * transaction scenarios. A [ScopingScope] is useful when you need to manage data in a consistent manner
 * across services in distributed transaction scenarios. Or when you need to compose multiple
 * [action] with a [compensation] that needs to run in a transaction like style.
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
 * The [saga] DSL removes all the boilerplate of manually having to facilitate this
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
 * suspend fun main() = scoped {
 *   val order = saga({ createOrder() }) { deleteOrder(it) }
 *   val payment = saga { createPayment(order) }, ::deletePayment)
 *   payment.awaitSuccess()
 * }
 * ```
 */
// TODO should this be implemented on ScopingScope
//   Or should ScopingScope be used to implement a separate SagaScope??
public suspend fun <A> ScopingScope.saga(
  action: suspend () -> A,
  compensation: suspend (A, Throwable?) -> Unit
): A = action().also { a ->
  closing { e ->
    when (e) {
      null -> compensation(a, null)
      else -> compensation(a, e)
    }
  }
}
