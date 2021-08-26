package arrow.fx.stm

import arrow.fx.stm.internal.STMTransaction
import arrow.fx.stm.internal.alterHamtWithHash
import arrow.fx.stm.internal.lookupHamtWithHash

/**
 * # Consistent and safe concurrent state updates
 *
 * Software transactional memory, or STM, is an abstraction for concurrent state modification.
 * With [STM] one can write code that concurrently accesses state and that can easily be composed without
 *  exposing details of how it ensures safety guarantees.
 * Programs running within an [STM] transaction will neither deadlock nor have race-conditions.
 *
 * > The api of [STM] is based on the haskell package [stm](https://hackage.haskell.org/package/stm) and the implementation is based on the GHC implementation for fine-grained locks.
 *
 * The base building blocks of [STM] are [TVar]'s and the primitives [retry], [orElse] and [catch].
 *
 * ## STM Datastructures
 *
 * There are several datastructures built on top of [TVar]'s already provided out of the box:
 * - [TQueue]: A transactional mutable queue
 * - [TMVar]: A mutable transactional variable that may be empty
 * - [TSet], [TMap]: Transactional Set and Map
 * - [TArray]: Array of [TVar]'s
 * - [TSemaphore]: Transactional semaphore
 * - [TVar]: A transactional mutable variable
 *
 * All of these structures (excluding [TVar]) are built upon [TVar]'s and the [STM] primitives and implementing other
 *  datastructures with [STM] can be done by composing the existing structures.
 *
 * ## Reading and writing to concurrent state:
 *
 * In order to modify transactional datastructures we have to be inside the [STM] context. This is achieved either by defining our
 *  functions with [STM] as the receiver or using [stm] to create lambda functions with [STM] as the receiver.
 *
 * Running a transaction is then done using [atomically]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.atomically
 * import arrow.fx.stm.TVar
 * import arrow.fx.stm.STM
 *
 * //sampleStart
 * fun STM.transfer(from: TVar<Int>, to: TVar<Int>, amount: Int): Unit {
 *   withdraw(from, amount)
 *   deposit(to, amount)
 * }
 *
 * fun STM.deposit(acc: TVar<Int>, amount: Int): Unit {
 *   val current = acc.read()
 *   acc.write(current + amount)
 *   // or the shorthand acc.modify { it + amount }
 * }
 *
 * fun STM.withdraw(acc: TVar<Int>, amount: Int): Unit {
 *   val current = acc.read()
 *   if (current - amount >= 0) acc.write(current + amount)
 *   else throw IllegalStateException("Not enough money in the account!")
 * }
 * //sampleEnd
 *
 * suspend fun main() {
 *   val acc1 = TVar.new(500)
 *   val acc2 = TVar.new(300)
 *   println("Balance account 1: ${acc1.unsafeRead()}")
 *   println("Balance account 2: ${acc2.unsafeRead()}")
 *   println("Performing transaction")
 *   atomically { transfer(acc1, acc2, 50) }
 *   println("Balance account 1: ${acc1.unsafeRead()}")
 *   println("Balance account 2: ${acc2.unsafeRead()}")
 * }
 * ```
 * This example shows a banking service moving money from one account to the other with [STM].
 * Should the first account not have enough money we throw an exception. This code is guaranteed to never deadlock and to never
 *  produce an invalid state by committing after the read state has changed concurrently.
 *
 *  > Note: A transaction that sees an invalid state (a [TVar] that was read has been changed concurrently) will restart and try again.
 *   This usually means we rerun the function entirely, therefore it is recommended to keep transactions small and to never use code that
 *   has side-effects inside. However no kotlin interface can actually keep you from doing side effects inside STM.
 *   Using side-effects such as access to resources, logging or network access comes with severe disadvantages:
 *   - Transactions may be aborted at any time so accessing resources may never trigger finalizers
 *   - Transactions may rerun an arbitrary amount of times before finishing and thus all effects will rerun.
 *
 * ## Retrying manually
 *
 * It is sometimes beneficial to manually abort the current transaction if, for example, an invalid state has been read. E.g. a [TQueue] had no elements to read.
 *  The aborted transaction will automatically restart once any previously accessed variable has changed.
 *
 * This is achieved by the primitive [retry]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.atomically
 * import arrow.fx.stm.TVar
 * import arrow.fx.stm.STM
 * import kotlinx.coroutines.runBlocking
 * import kotlinx.coroutines.async
 * import kotlinx.coroutines.delay
 *
 * //sampleStart
 * fun STM.transfer(from: TVar<Int>, to: TVar<Int>, amount: Int): Unit {
 *   withdraw(from, amount)
 *   deposit(to, amount)
 * }
 *
 * fun STM.deposit(acc: TVar<Int>, amount: Int): Unit {
 *   val current = acc.read()
 *   acc.write(current + amount)
 *   // or the shorthand acc.modify { it + amount }
 * }
 *
 * fun STM.withdraw(acc: TVar<Int>, amount: Int): Unit {
 *   val current = acc.read()
 *   if (current - amount >= 0) acc.write(current + amount)
 *   else retry() // we now retry if there is not enough money in the account
 *   // this can also be achieved by using `check(current - amount >= 0); acc.write(it + amount)`
 * }
 * //sampleEnd
 *
 * fun main(): Unit = runBlocking {
 *   val acc1 = TVar.new(0)
 *   val acc2 = TVar.new(300)
 *   println("Balance account 1: ${acc1.unsafeRead()}")
 *   println("Balance account 2: ${acc2.unsafeRead()}")
 *   async {
 *     println("Sending money - Searching")
 *     delay(2000)
 *     println("Sending money - Found some")
 *     atomically { acc1.write(100_000_000) }
 *   }
 *   println("Performing transaction")
 *   atomically {
 *     println("Trying to transfer")
 *     transfer(acc1, acc2, 50)
 *   }
 *   println("Balance account 1: ${acc1.unsafeRead()}")
 *   println("Balance account 2: ${acc2.unsafeRead()}")
 * }
 * ```
 *
 * Here in this (silly) example we changed `withdraw` to use [retry] and thus wait until enough money is in the account, which after
 *  a few seconds just happens to be the case.
 *
 * [retry] can be used to implement a lot of complex transactions and many datastructures like [TMVar] or [TQueue] use to to great effect.
 *
 * ## Branching with [orElse]
 *
 * [orElse] is another important primitive which allows a user to detect if a branch called [retry] and then use a fallback instead.
 *  If the fallback retries as well the whole transaction retries.
 *
 * ```kotlin:ank:playground
 * import kotlinx.coroutines.runBlocking
 * import arrow.fx.stm.atomically
 * import arrow.fx.stm.TVar
 * import arrow.fx.stm.STM
 * import arrow.fx.stm.stm
 *
 * //sampleStart
 * fun STM.transaction(v: TVar<Int>): Int? =
 *   stm {
 *     val result = v.read()
 *     check(result in 0..10)
 *     result
 *   } orElse { null }
 * //sampleEnd
 *
 * fun main(): Unit = runBlocking {
 *   val v = TVar.new(100)
 *   println("Value is ${v.unsafeRead()}")
 *   atomically { transaction(v) }
 *     .also { println("Transaction returned $it") }
 *   println("Set value to 5")
 *   println("Value is ${v.unsafeRead()}")
 *   atomically { v.write(5) }
 *   atomically { transaction(v) }
 *     .also { println("Transaction returned $it") }
 * }
 * ```
 *
 * This example uses [stm] which is a helper just like the stdlib function [suspend] to ease use of an infix function like [orElse].
 * In this transaction, when the value inside the variable is not in the correct range, the transaction retries (due to [check] calling [retry]).
 * If it is in the correct range it simply returns the value. [orElse] here intercepts a call to [retry] and executes the alternative which simply returns null.
 *
 * ## Exceptions
 *
 * Throwing inside [STM] will let the exception bubble up to either a [catch] handler or to [atomically] which will rethrow it.
 *
 * > Note: Using `try {...} catch (e: Exception) {...}` is not encouraged because any state change inside `try` will not be undone when
 *   an exception occurs! The recommended way of catching exceptions is to use [catch] which properly rolls back the transaction!
 *
 * Further reading:
 * - [Composable memory transactions, by Tim Harris, Simon Marlow, Simon Peyton Jones, and Maurice Herlihy, in ACM Conference on Principles and Practice of Parallel Programming 2005.](https://www.microsoft.com/en-us/research/publication/composable-memory-transactions/)
 */
// TODO Explore this https://dl.acm.org/doi/pdf/10.1145/2976002.2976020 when benchmarks are set up
public interface STM {
  /**
   * Abort and retry the current transaction.
   *
   * Aborts the transaction and suspends until any of the accessed [TVar]'s changed, after which the transaction will restart.
   * Since all other datastructures are built upon [TVar]'s this automatically extends to those structures as well.
   *
   * The main use for this is to abort once the transaction has hit an invalid state or otherwise needs to wait for changes.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.atomically
   * import arrow.fx.stm.stm
   *
   * suspend fun main() {
   *   //sampleStart
   *   val result = atomically {
   *     stm { retry() } orElse { "Alternative" }
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   */
  public fun retry(): Nothing

  /**
   * Run the given transaction and fallback to the other one if the first one calls [retry].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.atomically
   * import arrow.fx.stm.stm
   *
   * suspend fun main() {
   *   //sampleStart
   *   val result = atomically {
   *     stm { retry() } orElse { "Alternative" }
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   */
  public infix fun <A> (STM.() -> A).orElse(other: STM.() -> A): A

  /**
   * Run [f] and handle any exception thrown with [onError].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val result = atomically {
   *     catch({ throw Throwable() }) { e -> "caught" }
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   */
  public fun <A> catch(f: STM.() -> A, onError: STM.(Throwable) -> A): A

  /**
   * Read the value from a [TVar].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TVar
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tvar = TVar.new(10)
   *   val result = atomically {
   *     tvar.read()
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   *
   * This comes with a few guarantees:
   * - Any given [TVar] is only ever read once during a transaction.
   * - When committing the transaction the value read has to be equal to the current value otherwise the
   *   transaction will retry
   */
  public fun <A> TVar<A>.read(): A

  /**
   * Set the value of a [TVar].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TVar
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tvar = TVar.new(10)
   *   val result = atomically {
   *     tvar.write(20)
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   *
   * Similarly to [read] this comes with a few guarantees:
   * - For multiple writes to the same [TVar] in a transaction only the last will actually be performed
   * - When committing the value inside the [TVar], at the time of calling [write], has to be the
   *   same as the current value otherwise the transaction will retry
   */
  public fun <A> TVar<A>.write(a: A): Unit

  /**
   * Modify the value of a [TVar]
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TVar
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tvar = TVar.new(10)
   *   val result = atomically {
   *     tvar.modify { it * 2 }
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   *
   * `modify(f) = write(f(read()))`
   */
  public fun <A> TVar<A>.modify(f: (A) -> A): Unit = write(f(read()))

  /**
   * Swap the content of the [TVar]
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TVar
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tvar = TVar.new(10)
   *   val result = atomically {
   *     tvar.swap(20)
   *   }
   *   //sampleEnd
   *   println("Result $result")
   *   println("New value ${tvar.unsafeRead()}")
   * }
   * ```
   *
   * @return The previous value stored inside the [TVar]
   */
  public fun <A> TVar<A>.swap(a: A): A = read().also { write(a) }

  /**
   * Create a new [TVar] inside a transaction, because [TVar.new] is not possible inside [STM] transactions.
   */
  public fun <A> newTVar(a: A): TVar<A> = TVar(a)

  // -------- TMVar
  /**
   * Read the value from a [TMVar] and empty it.
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
   * This retries if the [TMVar] is empty and leaves the [TMVar] empty if it succeeded.
   *
   * @see TMVar.tryTake for a version that does not retry.
   * @see TMVar.read for a version that does not remove the value after reading.
   */
  public fun <A> TMVar<A>.take(): A = when (val ret = v.read()) {
    is Option.Some -> ret.a.also { v.write(Option.None) }
    Option.None -> retry()
  }

  /**
   * Put a value into an empty [TMVar].
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
   * This retries if the [TMVar] is not empty.
   *
   * For a version of [TMVar.put] that does not retry see [TMVar.tryPut]
   */
  public fun <A> TMVar<A>.put(a: A): Unit = when (v.read()) {
    is Option.Some -> retry()
    Option.None -> v.write(Option.Some(a))
  }

  /**
   * Read a value from a [TMVar] without removing it.
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
   * This retries if the [TMVar] is empty but does not take the value out if it succeeds.
   *
   * @see TMVar.tryRead for a version that does not retry.
   * @see TMVar.take for a version that leaves the [TMVar] empty after reading.
   */
  public fun <A> TMVar<A>.read(): A = when (val ret = v.read()) {
    is Option.Some -> ret.a
    Option.None -> retry()
  }

  /**
   * Same as [TMVar.take] except it returns null if the [TMVar] is empty and thus never retries.
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
   */
  public fun <A> TMVar<A>.tryTake(): A? = when (val ret = v.read()) {
    is Option.Some -> ret.a.also { v.write(Option.None) }
    Option.None -> null
  }

  /**
   * Same as [TMVar.put] except that it returns true or false if was successful or it retried.
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
   * This function never retries.
   *
   * @see TMVar.put for a function that retries if the [TMVar] is not empty.
   */
  public fun <A> TMVar<A>.tryPut(a: A): Boolean = when (v.read()) {
    is Option.Some -> false
    Option.None -> true.also { v.write(Option.Some(a)) }
  }

  /**
   * Same as [TMVar.read] except that it returns null if the [TMVar] is empty and thus never retries.
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
   * @see TMVar.read for a function that retries if the [TMVar] is empty.
   * @see TMVar.tryTake for a function that leaves the [TMVar] empty after reading.
   */
  public fun <A> TMVar<A>.tryRead(): A? = when (val ret = v.read()) {
    is Option.Some -> ret.a
    Option.None -> null
  }

  /**
   * Check if a [TMVar] is empty. This function never retries.
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
   */
  public fun <A> TMVar<A>.isEmpty(): Boolean = v.read() is Option.None

  /**
   * Check if a [TMVar] is not empty. This function never retries.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TMVar
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tmvar = TMVar.empty<Int>()
   *   val result = atomically {
   *     tmvar.isNotEmpty()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   *
   * > Because the state of a transaction is constant there can never be a race condition between checking if a `TMVar` is empty and subsequent
   *  reads in the *same* transaction.
   */
  public fun <A> TMVar<A>.isNotEmpty(): Boolean =
    isEmpty().not()

  /**
   * Swap the content of a [TMVar] or retry if it is empty.
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
   */
  public fun <A> TMVar<A>.swap(a: A): A = when (val ret = v.read()) {
    is Option.Some -> ret.a.also { v.write(Option.Some(a)) }
    Option.None -> retry()
  }

  // -------- TSemaphore
  /**
   * Returns the currently available number of permits in a [TSemaphore].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSemaphore
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tsem = TSemaphore.new(5)
   *   val result = atomically {
   *     tsem.available()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   *   println("Permits remaining ${atomically { tsem.available() }}")
   * }
   * ```
   *
   * This function never retries.
   */
  public fun TSemaphore.available(): Int =
    v.read()

  /**
   * Acquire 1 permit from a [TSemaphore].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSemaphore
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tsem = TSemaphore.new(5)
   *   atomically {
   *     tsem.acquire()
   *   }
   *   //sampleEnd
   *   println("Permits remaining ${atomically { tsem.available() }}")
   * }
   * ```
   *
   * This function will retry if there are no permits available.
   *
   * @see TSemaphore.tryAcquire for a version that does not retry.
   */
  public fun TSemaphore.acquire(): Unit =
    acquire(1)

  /**
   * Acquire [n] permit from a [TSemaphore].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSemaphore
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tsem = TSemaphore.new(5)
   *   atomically {
   *     tsem.acquire(3)
   *   }
   *   //sampleEnd
   *   println("Permits remaining ${atomically { tsem.available() }}")
   * }
   * ```
   *
   * This function will retry if there are less than [n] permits available.
   *
   * @see TSemaphore.tryAcquire for a version that does not retry.
   */
  public fun TSemaphore.acquire(n: Int): Unit {
    val curr = v.read()
    check(curr - n >= 0)
    v.write(curr - n)
  }

  /**
   * Like [TSemaphore.acquire] except that it returns whether or not acquisition was successful.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSemaphore
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tsem = TSemaphore.new(0)
   *   val result = atomically {
   *     tsem.tryAcquire()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   *   println("Permits remaining ${atomically { tsem.available() }}")
   * }
   * ```
   *
   * This function never retries.
   *
   * @see TSemaphore.acquire for a version that retries if there are not enough permits.
   */
  public fun TSemaphore.tryAcquire(): Boolean =
    tryAcquire(1)

  /**
   * Like [TSemaphore.acquire] except that it returns whether or not acquisition was successful.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSemaphore
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tsem = TSemaphore.new(0)
   *   val result = atomically {
   *     tsem.tryAcquire(3)
   *   }
   *   //sampleEnd
   *   println("Result $result")
   *   println("Permits remaining ${atomically { tsem.available() }}")
   * }
   * ```
   *
   * This function never retries.
   *
   * @see TSemaphore.acquire for a version that retries if there are not enough permits.
   */
  public fun TSemaphore.tryAcquire(n: Int): Boolean =
    stm { acquire(n); true } orElse { false }

  /**
   * Release a permit back to the [TSemaphore].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSemaphore
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tsem = TSemaphore.new(5)
   *   atomically {
   *     tsem.release()
   *   }
   *   //sampleEnd
   *   println("Permits remaining ${atomically { tsem.available() }}")
   * }
   * ```
   *
   * This function never retries.
   */
  public fun TSemaphore.release(): Unit =
    v.write(v.read() + 1)

  /**
   * Release [n] permits back to the [TSemaphore].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSemaphore
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tsem = TSemaphore.new(5)
   *   atomically {
   *     tsem.release(2)
   *   }
   *   //sampleEnd
   *   println("Permits remaining ${atomically { tsem.available() }}")
   * }
   * ```
   *
   * [n] must be non-negative.
   *
   * This function never retries.
   */
  public fun TSemaphore.release(n: Int): Unit = when (n) {
    0 -> Unit
    1 -> release()
    else ->
      if (n < 0) throw IllegalArgumentException("Cannot decrease permits using release(n). n was negative: $n")
      else v.write(v.read() + n)
  }

  // TQueue
  /**
   * Append an element to the [TQueue].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   atomically {
   *     tq.write(2)
   *   }
   *   //sampleEnd
   *   println("Items in queue ${atomically { tq.flush() }}")
   * }
   * ```
   *
   * This function never retries.
   */
  public fun <A> TQueue<A>.write(a: A): Unit =
    writes.modify { it.cons(a) }

  /**
   * Append an element to the [TQueue]. Alias for [STM.write].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   atomically {
   *     tq += 2
   *   }
   *   //sampleEnd
   *   println("Items in queue ${atomically { tq.flush() }}")
   * }
   * ```
   *
   * This function never retries.
   */
  public operator fun <A> TQueue<A>.plusAssign(a: A): Unit = write(a)

  /**
   * Remove the front element from the [TQueue] or retry if the [TQueue] is empty.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   val result = atomically {
   *     tq.write(2)
   *     tq.read()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   *   println("Items in queue ${atomically { tq.flush() }}")
   * }
   * ```
   *
   * @see TQueue.tryRead for a version that does not retry.
   * @see TQueue.peek for a version that does not remove the element.
   */
  public fun <A> TQueue<A>.read(): A {
    val xs = reads.read()
    return if (xs.isNotEmpty()) reads.write(xs.tail()).let { xs.head() }
    else {
      val ys = writes.read()
      if (ys.isEmpty()) retry()
      else {
        writes.write(PList.Nil)
        val reversed = ys.reverse()
        reads.write(reversed.tail())
        reversed.head()
      }
    }
  }

  /**
   * Same as [TQueue.read] except it returns null if the [TQueue] is empty.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   val result = atomically {
   *     tq.tryRead()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   *   println("Items in queue ${atomically { tq.flush() }}")
   * }
   * ```
   *
   * This function never retries.
   */
  public fun <A> TQueue<A>.tryRead(): A? =
    (stm { read() } orElse { null })

  /**
   * Drains all entries of a [TQueue] into a single list.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   val result = atomically {
   *     tq.write(2)
   *     tq.write(4)
   *
   *     tq.flush()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   *   println("Items in queue ${atomically { tq.flush() }}")
   * }
   * ```
   *
   * This function never retries.
   */
  public fun <A> TQueue<A>.flush(): List<A> {
    val xs = reads.read().also { if (it.isNotEmpty()) reads.write(PList.Nil) }
    val ys = writes.read().also { if (it.isNotEmpty()) writes.write(PList.Nil) }
    return xs.toList() + ys.reverse().toList()
  }

  /**
   * Read the front element of a [TQueue] without removing it.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   val result = atomically {
   *     tq.write(2)
   *
   *     tq.peek()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   *   println("Items in queue ${atomically { tq.flush() }}")
   * }
   * ```
   *
   * This function retries if the [TQueue] is empty.
   *
   * @see TQueue.read for a version that removes the front element.
   * @see TQueue.tryPeek for a version that does not retry.
   */
  public fun <A> TQueue<A>.peek(): A =
    read().also { writeFront(it) }

  /**
   * Same as [TQueue.peek] except it returns null if the [TQueue] is empty.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   val result = atomically {
   *     tq.tryPeek()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   *   println("Items in queue ${atomically { tq.flush() }}")
   * }
   * ```
   *
   * This function never retries.
   *
   * @see TQueue.tryRead for a version that removes the front element
   * @see TQueue.peek for a version that retries if the [TQueue] is empty.
   */
  public fun <A> TQueue<A>.tryPeek(): A? =
    tryRead()?.also { writeFront(it) }

  /**
   * Prepend an element to the [TQueue].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   atomically {
   *     tq.write(1)
   *     tq.writeFront(2)
   *   }
   *   //sampleEnd
   *   println("Items in queue ${atomically { tq.flush() }}")
   * }
   * ```
   *
   * Mainly used to implement [TQueue.peek] and since this writes to the read variable of a [TQueue] excessive use
   *  can lead to contention on consumers. Prefer appending to a [TQueue] if possible.
   *
   * This function never retries.
   */
  public fun <A> TQueue<A>.writeFront(a: A): Unit =
    reads.read().let { reads.write(it.cons(a)) }

  /**
   * Check if a [TQueue] is empty.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   val result = atomically {
   *     tq.isEmpty()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   *
   * This function never retries.
   *
   * > This function has to access both [TVar]'s and thus may lead to increased contention, use sparingly.
   */
  public fun <A> TQueue<A>.isEmpty(): Boolean =
    reads.read().isEmpty() && writes.read().isEmpty()

  /**
   * Check if a [TQueue] is not empty.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   val result = atomically {
   *     tq.isNotEmpty()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   *
   * This function never retries.
   *
   * > This function has to access both [TVar]'s and thus may lead to increased contention, use sparingly.
   */
  public fun <A> TQueue<A>.isNotEmpty(): Boolean =
    reads.read().isNotEmpty() || writes.read().isNotEmpty()

  /**
   * Filter a [TQueue], removing all elements for which [pred] returns false.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   atomically {
   *     tq.write(0)
   *     tq.removeAll { it != 0 }
   *   }
   *   //sampleEnd
   *   println("Items in queue ${atomically { tq.flush() }}")
   * }
   * ```
   *
   * This function never retries.
   *
   * > This function has to access both [TVar]'s and thus may lead to increased contention, use sparingly.
   */
  public fun <A> TQueue<A>.removeAll(pred: (A) -> Boolean): Unit {
    reads.modify { it.filter(pred) }
    writes.modify { it.filter(pred) }
  }

  /**
   * Return the current number of elements in a [TQueue]
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TQueue
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tq = TQueue.new<Int>()
   *   val result = atomically {
   *     tq.size()
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   *
   * This function never retries.
   *
   * > This function has to access both [TVar]'s and thus may lead to increased contention, use sparingly.
   */
  public fun <A> TQueue<A>.size(): Int = reads.read().size() + writes.read().size()

  // -------- TArray
  /**
   * Read a variable from the [TArray].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TArray
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tarr = TArray.new(size = 10, 2)
   *   val result = atomically {
   *     tarr[5]
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   *
   * Throws if [i] is out of bounds.
   *
   * This function never retries.
   */
  public operator fun <A> TArray<A>.get(i: Int): A =
    v[i].read()

  /**
   * Set a variable in the [TArray].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TArray
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tarr = TArray.new(size = 10, 2)
   *   val result = atomically {
   *     tarr[5] = 3
   *
   *     tarr[5]
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   *
   * Throws if [i] is out of bounds.
   *
   * This function never retries.
   */
  public operator fun <A> TArray<A>.set(i: Int, a: A): Unit =
    v[i].write(a)

  /**
   * Modify each element in a [TArray] by applying [f].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TArray
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tarr = TArray.new(size = 10, 2)
   *   val result = atomically {
   *     tarr.transform { it + 1 }
   *   }
   *   //sampleEnd
   * }
   * ```
   *
   * This function never retries.
   */
  public fun <A> TArray<A>.transform(f: (A) -> A): Unit =
    v.forEach { it.modify(f) }

  /**
   * Fold a [TArray] to a single value.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TArray
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tarr = TArray.new(size = 10, 2)
   *   val result = atomically {
   *     tarr.fold(0) { acc, v -> acc + v }
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   *
   * This function never retries.
   */
  public fun <A, B> TArray<A>.fold(init: B, f: (B, A) -> B): B =
    v.fold(init) { acc, v -> f(acc, v.read()) }

  // -------- TMap
  /**
   * Check if a key [k] is in the map
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TMap
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tmap = TMap.new<Int, String>()
   *   atomically {
   *     tmap[1] = "Hello"
   *
   *     tmap.remove(1)
   *   }
   *   //sampleEnd
   * }
   * ```
   *
   * This function never retries.
   */
  public fun <K, V> TMap<K, V>.member(k: K): Boolean =
    lookup(k) != null

  /**
   * Lookup a value at the specific key [k]
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TMap
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tmap = TMap.new<Int, String>()
   *   val result = atomically {
   *     tmap[1] = "Hello"
   *     tmap[2] = "World"
   *
   *     tmap.lookup(1)
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   *
   * > If the key is not present [STM.lookup] will not retry, instead it returns `null`.
   */
  public fun <K, V> TMap<K, V>.lookup(k: K): V? =
    lookupHamtWithHash(hamt, hashFn(k)) { it.first == k }?.second

  /**
   * Alias of [STM.lookup]
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TMap
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tmap = TMap.new<Int, String>()
   *   val result = atomically {
   *     tmap[1] = "Hello"
   *     tmap[2] = "World"
   *
   *     tmap[2]
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   *
   * > If the key is not present [STM.get] will not retry, instead it returns `null`.
   */
  public operator fun <K, V> TMap<K, V>.get(k: K): V? = lookup(k)

  /**
   * Add a key value pair to the map
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TMap
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tmap = TMap.new<Int, String>()
   *   atomically {
   *     tmap.insert(10, "Hello")
   *   }
   *   //sampleEnd
   * }
   * ```
   */
  public fun <K, V> TMap<K, V>.insert(k: K, v: V): Unit {
    alterHamtWithHash(hamt, hashFn(k), { it.first == k }) { k to v }
  }

  /**
   * Alias for [STM.insert]
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TMap
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tmap = TMap.new<Int, String>()
   *   atomically {
   *     tmap[1] = "Hello"
   *   }
   *   //sampleEnd
   * }
   * ```
   */
  public operator fun <K, V> TMap<K, V>.set(k: K, v: V): Unit = insert(k, v)

  /**
   * Add a key value pair to the map
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TMap
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tmap = TMap.new<Int, String>()
   *   atomically {
   *     tmap += (1 to "Hello")
   *   }
   *   //sampleEnd
   * }
   * ```
   */
  public operator fun <K, V> TMap<K, V>.plusAssign(kv: Pair<K, V>): Unit = insert(kv.first, kv.second)

  /**
   * Update a value at a key if it exists.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TMap
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tmap = TMap.new<Int, String>()
   *   val result = atomically {
   *     tmap[2] = "Hello"
   *     tmap.update(2) { it.reversed() }
   *     tmap[2]
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   */
  public fun <K, V> TMap<K, V>.update(k: K, fn: (V) -> V): Unit {
    alterHamtWithHash(hamt, hashFn(k), { it.first == k }) { it?.second?.let(fn)?.let { k to it } }
  }

  /**
   * Remove a key value pair from a map
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TMap
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tmap = TMap.new<Int, String>()
   *   atomically {
   *     tmap[1] = "Hello"
   *     tmap.remove(1)
   *   }
   *   //sampleEnd
   * }
   * ```
   */
  public fun <K, V> TMap<K, V>.remove(k: K): Unit {
    alterHamtWithHash(hamt, hashFn(k), { it.first == k }) { null }
  }

  // -------- TSet
  /**
   * Check if an element is already in the set
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSet
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tset = TSet.new<String>()
   *   val result = atomically {
   *     tset.insert("Hello")
   *     tset.member("Hello")
   *   }
   *   //sampleEnd
   *   println("Result $result")
   * }
   * ```
   */
  public fun <A> TSet<A>.member(a: A): Boolean =
    lookupHamtWithHash(hamt, hashFn(a)) { it == a } != null

  /**
   * Adds an element to the set.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSet
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tset = TSet.new<String>()
   *   atomically {
   *     tset.insert("Hello")
   *   }
   *   //sampleEnd
   * }
   * ```
   */
  public fun <A> TSet<A>.insert(a: A): Unit {
    alterHamtWithHash(hamt, hashFn(a), { it == a }) { a }
  }

  /**
   * Adds an element to the set. Alias of [STM.insert].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSet
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tset = TSet.new<String>()
   *   atomically {
   *     tset += "Hello"
   *   }
   *   //sampleEnd
   * }
   * ```
   */
  public operator fun <A> TSet<A>.plusAssign(a: A): Unit = insert(a)

  /**
   * Remove an element from the set.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.stm.TSet
   * import arrow.fx.stm.atomically
   *
   * suspend fun main() {
   *   //sampleStart
   *   val tset = TSet.new<String>()
   *   atomically {
   *     tset.insert("Hello")
   *     tset.remove("Hello")
   *   }
   *   //sampleEnd
   * }
   * ```
   */
  public fun <A> TSet<A>.remove(a: A): Unit {
    alterHamtWithHash(hamt, hashFn(a), { it == a }) { null }
  }
}

/**
 * Helper to create stm blocks that can be run with [STM.orElse]
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.atomically
 * import arrow.fx.stm.stm
 *
 * suspend fun main() {
 *   //sampleStart
 *   val i = 4
 *   val result = atomically {
 *     stm {
 *       if (i == 4) retry()
 *       "Not 4"
 *     } orElse { "4" }
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * Equal to [suspend] just with an [STM] receiver.
 */
public inline fun <A> stm(noinline f: STM.() -> A): STM.() -> A = f

/**
 * Retry if [b] is false otherwise does nothing.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.atomically
 * import arrow.fx.stm.stm
 *
 * suspend fun main() {
 *   //sampleStart
 *   val i = 4
 *   val result = atomically {
 *     stm {
 *       check(i > 5) // This calls retry and aborts if i <= 5
 *       "Larger than 5"
 *     } orElse { "Smaller than or equal to 5" }
 *   }
 *   //sampleEnd
 *   println("Result $result")
 * }
 * ```
 *
 * `check(b) = if (b.not()) retry() else Unit`
 */
public fun STM.check(b: Boolean): Unit = if (b.not()) retry() else Unit

/**
 * Run a transaction to completion.
 *
 * This comes with the guarantee that, at the time of committing the transaction, all read variables have a consistent state
 *  (they have not changed after the first read). Otherwise the transaction will be aborted and run again.
 *
 * Note that only reads and writes inside a single transaction have this guarantee.
 * Code that calls [atomically] as follows will again be subject to race conditions:
 * `atomically { v.read() }.let { atomically { v.write(it + 1) } }`. Because those are separate transactions the value inside `v` might change
 *  between transactions! The only safe way is to do it in one go: `atomically { v.write(v.read() + 1) }`
 *
 * Transactions that only read or access completely disjoint set of [TVar]'s will be able to commit in parallel as [STM] in arrow
 *  uses an approach the locks only modified [TVar]'s on commit. Only calls to [STM.write] need to be synchronized, however the performance of [STM] is still
 *  heavily linked to the amount of [TVar]'s accessed so it is good practice to keep transactions short.
 *
 * Keeping transactions short has another benefit which comes from another drawback of [STM]:
 * There is no notion of fairness when it comes to transactions. The fastest transaction always wins.
 * This can be problematic if a large number of small transactions starve out a larger transaction by forcing it to retry a lot.
 * In practice this rarely happens, however to avoid such a scenario it is recommended to keep transactions small.
 *
 * This may suspend if [STM.retry] is called and no accessed [TVar] changed. It will then resume automatically after any accessed [TVar] changed.
 *
 * Rethrows all exceptions not caught by inside [f]. Remember to use [STM.catch] to handle exceptions as `try {} catch` will not handle transaction
 *  state properly!
 */
public suspend fun <A> atomically(f: STM.() -> A): A = STMTransaction(f).commit()
