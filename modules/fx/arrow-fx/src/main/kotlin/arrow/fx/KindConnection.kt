package arrow.fx

import arrow.Kind
import arrow.fx.internal.JavaCancellationException
import arrow.fx.typeclasses.MonadDefer
import arrow.typeclasses.Applicative
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

typealias CancelToken<F> = Kind<F, Unit>

enum class OnCancel { ThrowCancellationException, Silent;

  companion object {
    val CancellationException = ConnectionCancellationException
  }
}

object ConnectionCancellationException : JavaCancellationException("User cancellation")

/**
 * Connection for kinded type [F].
 *
 * A connection is represented by a composite of [cancel] functions,
 * [cancel] is idempotent and all methods are thread-safe & atomic.
 *
 * The cancellation functions are maintained in a stack and executed in a FIFO order.
 */
sealed class KindConnection<F> {

  /**
   * Cancels all work represented by this reference.
   *
   * Guaranteed idempotency - calling it multiple times should have the same side-effect as calling it only
   * once. Implementations of this method should also be thread-safe.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val conn = IOConnection()
   *
   *   conn.push(IO { println("I get executed on cancellation") })
   *
   *   conn.cancel().fix().unsafeRunSync()
   *   conn.cancel().fix().unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   */
  abstract fun cancel(): CancelToken<F>

  /**
   * Check if the [KindConnection] is canceled
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val conn = IOConnection()
   *
   *   val isNotCanceled = conn.isCanceled()
   *
   *   conn.cancel().fix().unsafeRunSync()
   *
   *   val isCanceled = conn.isCanceled()
   *   //sampleEnd
   *   println("isNotCanceled: $isNotCanceled, isCanceled: $isCanceled")
   * }
   * ```
   *
   * @see isNotCanceled
   */
  abstract fun isCanceled(): Boolean

  /**
   * Check if the [KindConnection] is not canceled
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val conn = IOConnection()
   *
   *   val isNotCanceled = conn.isNotCanceled()
   *
   *   conn.cancel().fix().unsafeRunSync()
   *
   *   val isCanceled = conn.isNotCanceled()
   *   //sampleEnd
   *   println("isNotCanceled: $isNotCanceled, isCanceled: $isCanceled")
   * }
   * ```
   *
   * @see isCanceled
   */
  fun isNotCanceled(): Boolean = !isCanceled()

  /**
   * Pushes a cancellation function, or token, meant to cancel and cleanup resources.
   * These functions are kept inside a stack, and executed in FIFO order on cancellation.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val conn = IOConnection()
   *
   *   conn.push(IO { println("I get executed on cancellation") })
   *
   *   conn.cancel().fix().unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   */
  abstract fun push(token: CancelToken<F>): Unit

  /**
   * Pushes a number of cancellation function, or tokens, meant to cancel and cleanup resources.
   * These functions are kept inside a stack, and executed in FIFO order on cancellation.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val conn = IOConnection()
   *
   *   conn.push(
   *     IO { println("I get executed on cancellation first") },
   *     IO { println("I get executed on cancellation second") },
   *     IO { println("I get executed on cancellation third") }
   *   )
   *
   *   conn.cancel().fix().unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   */
  abstract fun push(vararg token: CancelToken<F>): Unit

  /**
   * Pushes a pair of [KindConnection] on the stack, which on cancellation will get trampolined. This is useful in
   * race for example, because combining a whole collection of tasks, two by two, can lead to building a
   * cancelable that's stack unsafe.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val conn = IOConnection()
   *
   *   conn.pushPair(
   *     IOConnection().apply { push(IO { println("Connection A is getting cancelled") }) },
   *     IOConnection().apply { push(IO { println("Connection B is getting cancelled") }) }
   *   )
   *
   *   conn.cancel().fix().unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   */
  fun pushPair(lh: KindConnection<F>, rh: KindConnection<F>): Unit =
    push(lh.cancel(), rh.cancel())

  /**
   * Pushes a pair of [KindConnection] on the stack, which on cancellation will get trampolined. This is useful in
   * race for example, because combining a whole collection of tasks, two by two, can lead to building a
   * cancelable that's stack unsafe.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val conn = IOConnection()
   *
   *   conn.pushPair(
   *     IO { println("Connection A is getting cancelled") },
   *     IO { println("Connection B is getting cancelled") }
   *   )
   *
   *   conn.cancel().fix().unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   */
  fun pushPair(lh: CancelToken<F>, rh: CancelToken<F>): Unit =
    push(lh, rh)

  /**
   * Pops a cancelable reference from the FIFO stack of references for this connection.
   * A cancelable reference is meant to cancel and cleanup resources.
   *
   * @return the cancelable reference that was removed.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val conn = IOConnection()
   *
   *   conn.push(IO { println("I was put first on the cancel stack") })
   *   conn.push(IO { println("I was put second on the cancel stack") })
   *   conn.pop()
   *
   *   val second = conn.cancel().fix().unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   **/
  abstract fun pop(): CancelToken<F>

  /**
   * Tries to reset an [KindConnection], from a cancelled state, back to a pristine state, but only if possible.
   *
   * @return true on success, false if there was a race condition (i.e. the connection wasn't cancelled) or if
   * the type of the connection cannot be reactivated.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val conn = IOConnection()
   *
   *   conn.cancel().fix().unsafeRunSync()
   *   val isCanceled = conn.isCanceled()
   *   val couldReactive = conn.tryReactivate()
   *
   *   val isReactivated = conn.isCanceled()
   *   //sampleEnd
   * }
   * ```
   **/
  abstract fun tryReactivate(): Boolean

  companion object {

    /**
     * Construct a [KindConnection] for a kind [F] based on [MonadDefer].
     *
     * ```kotlin:ank:playground
     * import arrow.fx.*
     * import arrow.fx.extensions.io.monadDefer.monadDefer
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val conn: IOConnection = KindConnection(IO.monadDefer()) { it.fix().unsafeRunAsync { } }
     *   //sampleEnd
     * }
     * ```
     **/
    operator fun <F> invoke(MD: MonadDefer<F>, run: (CancelToken<F>) -> Unit): KindConnection<F> =
      DefaultKindConnection(MD, run)

    /**
     * Construct an uncancelable [KindConnection] for a kind [F] based on [MonadDefer].
     *
     * ```kotlin:ank:playground
     * import arrow.fx.*
     * import arrow.fx.extensions.io.applicative.applicative
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val conn: IOConnection = KindConnection.uncancelable(IO.applicative())
     *   //sampleEnd
     * }
     * ```
     **/
    fun <F> uncancelable(FA: Applicative<F>): KindConnection<F> = Uncancelable(FA)
  }

  /**
   * [KindConnection] reference that cannot be canceled.
   */
  private class Uncancelable<F>(FA: Applicative<F>) : KindConnection<F>(), Applicative<F> by FA {
    override fun cancel(): CancelToken<F> = unit()
    override fun isCanceled(): Boolean = false
    override fun push(token: CancelToken<F>) = Unit
    override fun push(vararg token: CancelToken<F>) = Unit
    override fun pop(): CancelToken<F> = unit()
    override fun tryReactivate(): Boolean = true
    override fun toString(): String = "UncancelableConnection"
  }

  /**
   * Default [KindConnection] implementation.
   */
  private class DefaultKindConnection<F>(private val MD: MonadDefer<F>, val run: (CancelToken<F>) -> Unit) : KindConnection<F>(), MonadDefer<F> by MD {
    private val state: AtomicRef<List<CancelToken<F>>?> = atomic(emptyList())

    override fun cancel(): CancelToken<F> = defer {
      state.getAndSet(null).let { stack ->
        when {
          stack == null || stack.isEmpty() -> unit()
          else -> stack.cancelAll()
        }
      }
    }

    override fun isCanceled(): Boolean = state.value == null

    override tailrec fun push(token: CancelToken<F>): Unit = when (val list = state.value) {
      null -> run(token) // If connection is already cancelled cancel token immediately.
      else -> if (!state.compareAndSet(list, listOf(token) + list)) push(token) else Unit
    }

    override fun push(vararg token: CancelToken<F>): Unit =
      push(token.toList().cancelAll())

    override tailrec fun pop(): CancelToken<F> {
      val state = state.value
      return when {
        state == null || state.isEmpty() -> unit()
        else -> if (!this.state.compareAndSet(state, state.drop(1))) pop()
        else state.first()
      }
    }

    override fun tryReactivate(): Boolean =
      state.compareAndSet(null, emptyList())

    private fun List<CancelToken<F>>.cancelAll(): CancelToken<F> = defer {
      // TODO this blocks forever if any `CancelToken<F>` doesn't terminate. Requires `fork`/`start` to avoid.
      fold(unit()) { acc, f -> f.flatMap { acc } }
    }

    override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> = MD.run {
      this@ap.ap(ff)
    }

    override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = MD.run {
      this@map.map(f)
    }

    override fun toString(): String = "KindConnection(state = ${state.value})"
  }
}
