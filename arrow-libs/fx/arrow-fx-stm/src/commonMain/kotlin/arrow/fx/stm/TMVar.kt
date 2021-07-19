package arrow.fx.stm

public fun <A> STM.newTMVar(a: A): TMVar<A> = TMVar<A>(newTVar(Option.Some(a)))
public fun <A> STM.newEmptyTMVar(): TMVar<A> = TMVar<A>(newTVar(Option.None))

/**
 * A [TMVar] is a mutable reference that can either be empty or hold a value.
 *
 * The main use for [TMVar] is as a synchronization primitive as it can be used to force other transactions
 *  to wait until a [TMVar] is full.
 *
 * ## Creating a [TMVar]:
 *
 * As usual with [STM] types there are two equal sets of operators for creating them, one that can be used inside
 *  and one for use outside of transactions:
 * - [TMVar.new] and [STM.newTMVar] create a new filled [TMVar]
 * - [TMVar.empty] and [STM.newEmptyTMVar] create an empty [TMVar]
 *
 * ## Reading the content of a [TMVar]
 *
 * Taking the value out of a [TMVar]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmvar = TMVar.new(10)
 *   val result = atomically {
 *     tmvar.take()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("New value ${atomically { tmvar.tryTake() } }")
 * }
 * ```
 *
 * Should the [TMVar] be empty at the time of calling [STM.take], it will call [STM.retry], suspend and wait for another transaction to
 *  put a value back.
 *
 * This behaviour can be avoided by using [STM.tryTake] instead:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmvar = TMVar.empty<Int>()
 *   val result = atomically {
 *     tmvar.tryTake()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("New value ${atomically { tmvar.tryTake() } }")
 * }
 * ```
 *
 * Another effect of using either [STM.take] or [STM.tryTake] is that the [TMVar] will be empty after a successful call. Alternatively if you just
 *  want to read without emptying you can use either [STM.read] or [STM.tryRead] which will not remove the value.
 *  As with [STM.take] [STM.read] will fail and retry the transaction should the [TMVar] be empty, whereas [STM.tryTake] and [STM.tryRead] will return null instead.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmvar = TMVar.new(30)
 *   val result = atomically {
 *     tmvar.read()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("New value ${atomically { tmvar.tryTake() } }")
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmvar = TMVar.empty<Int>()
 *   val result = atomically {
 *     tmvar.tryRead()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * ## Setting the value of a [TMVar]:
 *
 * Changing the value of an empty [TMVar]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmvar = TMVar.empty<Int>()
 *   atomically {
 *     tmvar.put(20)
 *   }
 *   //sampleEnd
 *   println("New value ${atomically { tmvar.tryTake() } }")
 * }
 * ```
 *
 * Should the [TMVar] be full, [STM.put] will call [STM.retry] and wait for another transaction to empty the [TMVar] again.
 *  This can be avoided by using [STM.tryPut] instead. [STM.tryPut] returns whether or not the operation was successful.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmvar = TMVar.new(20)
 *   val result = atomically {
 *     tmvar.tryPut(30)
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("New value ${atomically { tmvar.tryTake() } }")
 * }
 * ```
 *
 * Another common pattern is to swap the value of a [TMVar], returning the old value and setting a new one:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmvar = TMVar.new(30)
 *   val result = atomically {
 *     tmvar.swap(40)
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("New value ${atomically { tmvar.tryTake() } }")
 * }
 * ```
 *
 * ## Checking if a [TMVar] is empty:
 *
 * Checking if a [TMVar] is empty or not can be done by either using [STM.isEmpty] or [STM.isNotEmpty]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TMVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tmvar = TMVar.empty<Int>()
 *   val result = atomically {
 *     tmvar.isEmpty()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * > Because the state of a transaction is constant there can never be a race condition between checking if a `TMVar` is empty and subsequent
 *  reads in the *same* transaction.
 *
 */
public data class TMVar<A> internal constructor(internal val v: TVar<Option<A>>) {
  public companion object {
    public suspend fun <A> new(a: A): TMVar<A> = TMVar<A>(TVar.new(Option.Some(a)))
    public suspend fun <A> empty(): TMVar<A> = TMVar<A>(TVar.new(Option.None))
  }
}

/**
 * ADT to avoid problems with `TMVar<A?>` because kotlin can't differ `A?` from `A??` so using a nullable
 *  reference inside `TMVar` does not work.
 *
 * TODO: This may benefit from using Any? and a reference object instead as this will never be nested.
 */
internal sealed class Option<out A> {
  data class Some<A>(val a: A) : Option<A>()
  object None : Option<Nothing>()
}
