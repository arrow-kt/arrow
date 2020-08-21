package arrow.fx.coroutines.stream

import arrow.core.Either
import arrow.core.identity
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.Platform
import arrow.fx.coroutines.Token
import arrow.fx.coroutines.nonFatalOrThrow
import arrow.fx.coroutines.stream.Pull.Eval.CloseScope
import arrow.fx.coroutines.stream.Pull.Eval.OpenScope
import arrow.fx.coroutines.stream.Pull.EvalView
import arrow.fx.coroutines.stream.Pull.Result
import arrow.fx.coroutines.stream.Pull.Result.Pure
import arrow.fx.coroutines.stream.Pull.Result.Fail
import arrow.fx.coroutines.stream.Pull.Result.Interrupted

sealed class Pull<out O, out R> {

  abstract fun <P> mapOutput(f: (O) -> P): Pull<P, R>

  /** Returns a pull with the result wrapped in `Right`, or an error wrapped in `Left` if the pull has failed. */
  fun attempt(): Pull<O, Either<Throwable, R>> =
    map { r -> Either.Right(r) }
      .handleErrorWith { t -> Pure(Either.Left(t)) }

  /** Replaces the result of this pull with [Unit]. */
  fun void(): Pull<O, Unit> =
    map { Unit }

  fun asHandler(e: Throwable): Pull<O, R> =
    when (val res: ViewL<O, R> = ViewL(this)) {
      is Pure -> Fail(e)
      is Fail -> Fail(Platform.composeErrors(e, res.error))
      is Interrupted<*> ->
        Interrupted(res.context, res.deferredError?.let { Platform.composeErrors(e, it) })
      is EvalView<*, *, *> ->
        res.next(Fail(e)) as Pull<O, R>
      else -> throw RuntimeException("Exhaustive when exception")
    }

  companion object {
    val done: Pull<Nothing, Unit> = just(Unit)

    fun <O> done(): Pull<O, Unit> = done

    fun raiseError(e: Throwable): Pull<Nothing, Nothing> =
      Fail(e)

    fun <R> just(r: R): Pull<Nothing, R> =
      Pure(r)

    fun <R> effect(effect: suspend () -> R): Pull<Nothing, R> =
      Eval.Effect(effect)

    fun <R> bracketCase(acquire: suspend () -> R, release: suspend (R, ExitCase) -> Unit): Pull<Nothing, R> =
      Eval.Acquire(acquire, release)

    fun <O> output1(value: O): Pull<O, Unit> =
      Eval.Output(Chunk.just(value))

    fun <O> output(chunk: Chunk<O>): Pull<O, Unit> =
      Eval.Output(chunk)

    fun <O, R> defer(fr: () -> Pull<O, R>): Pull<O, R> =
      object : Bind<O, Unit, R>(Result.unit) {
        override fun cont(r: Result<Unit>): Pull<O, R> =
          fr.invoke()
      }

    /**
     * Repeatedly uses the output of the pull as input for the next step of the pull.
     * Halts when a step terminates with `null` or `Pull.raiseError`.
     */
    fun <O, R> loop(r: R, using: (R) -> Pull<O, R?>): Pull<O, R?> =
      using(r).flatMap { rr ->
        rr?.let { loop(it, using) } ?: just(null)
      }

    /**
     * Gets the current scope, allowing manual leasing or interruption.
     * This is a low-level method and generally should not be used by user code.
     */
    internal val getScope: Pull<Nothing, Scope> =
      Eval.GetScope

    /**
     * Wraps supplied pull in new scope, that will be opened before this pull is evaluated
     * and closed once this pull either finishes its evaluation or when it fails.
     */
    fun <O> scope_(
      s: Pull<O, Unit>,
      interruptible: Boolean
    ): Pull<O, Unit> =
      OpenScope(interruptible).flatMap { scopeId ->
        s.transformWith { res ->
          when (res) {
            is Pure -> CloseScope(scopeId, null, ExitCase.Completed)
            is Interrupted<*> /* & res.context is Token */ ->
              when (val ctx = res.context) {
                is Token -> CloseScope(scopeId, Pair(ctx, res.deferredError), ExitCase.Cancelled)
                else -> throw RuntimeException("Impossible context: $ctx")
              }
            is Fail -> CloseScope(scopeId, null, ExitCase.Failure(res.error)).transformWith { res2 ->
              when (res2) {
                is Pure -> Fail(res.error)
                is Fail -> Fail(Platform.composeErrors(res.error, res2.error))
                is Interrupted<*> ->
                  throw RuntimeException("Impossible, cannot interrupt when closing failed scope: $scopeId, ${res2.context}, ${res2.deferredError?.message}")
              }
            }
          }
        }
      }

    fun <O, A, B> bracketCase(
      acquire: Pull<O, A>,
      use: (A) -> Pull<O, B>,
      release: (A, ExitCase) -> Pull<O, Unit>
    ): Pull<O, B> = acquire.flatMap { a ->
      val used = try {
        use(a)
      } catch (e: Throwable) {
        Fail(e.nonFatalOrThrow())
      }

      used.transformWith { result: Result<B> ->
        release(a, result.asExitCase()).transformWith { released ->
          when {
            result is Fail && released is Fail -> Fail(Platform.composeErrors(result.error, released.error))
            released is Fail -> released
            else -> result
          }
        }
      }
    }
  }

  /**
   * Models either a [Result] or [EvalView] without wrapping
   */
  /*sealed interface */
  internal interface ViewL<out O, out R>

  sealed class Result<out R> : Pull<Nothing, R>(), ViewL<Nothing, R> {
    override fun <P> mapOutput(f: (Nothing) -> P): Pull<P, R> = this

    fun asExitCase(): ExitCase =
      when (this) {
        is Pure -> ExitCase.Completed
        is Fail -> ExitCase.Failure(this.error)
        is Interrupted<*> -> ExitCase.Cancelled
      }

    data class Pure<R>(val r: R) : Result<R>() {
      override fun toString(): String = "Pull.Pure($r)"
    }

    data class Fail(val error: Throwable) : Result<Nothing>() {
      override fun toString(): String = "Pull.Fail($error)"
    }

    /**
     * Signals that Pull evaluation was interrupted.
     *
     * @param context Any user specific context that needs to be captured during interruption
     *                for eventual resume of the operation.
     *
     * @param deferredError Any errors, accumulated during resume of the interruption.
     *                      Instead throwing errors immediately during interruption,
     *                      signalling of the errors may be deferred until the Interruption resumes.
     */ // Flatten this into Scope with a promise for suspension backpressure?
    data class Interrupted<X>(val context: X, val deferredError: Throwable?) : Result<Nothing>() {
      override fun toString(): String =
        "Pull.Interrupted($context, $deferredError)"
    }

    inline fun <B> map(f: (R) -> B): Result<B> =
      when (this) {
        is Pure -> try {
          Pure(f(this.r))
        } catch (e: Throwable) {
          Fail(e.nonFatalOrThrow())
        }
        is Fail -> this
        is Interrupted<*> -> this
      }

    companion object {
      val unit: Result<Unit> = Pure(Unit)

      fun <R> fromEither(either: Either<Throwable, R>): Result<R> =
        either.fold({ Fail(it) }, { Pure(it) })
    }
  }

  /** `Eval<O, R>` is a Generalised Algebraic Data Type (GADT)
   * of atomic instructions that can be evaluated in the effect `suspend`
   * to generate by-product outputs of type `O`.
   *
   * Each operation also generates an output of type `R` that is used
   * as control information for the rest of the interpretation or compilation.
   */
  internal sealed class Eval<out A, out B> : Pull<A, B>() {
    class Output<O>(val values: Chunk<O>) : Eval<O, Unit>() {
      override fun <P> mapOutput(f: (O) -> P): Pull<P, Unit> =
        defer {
          try {
            Output(values.map(f))
          } catch (e: Throwable) {
            Fail(e.nonFatalOrThrow())
          }
        }

      override fun toString(): String =
        "Output($values)"
    }

    /**
     * Steps through the stream, providing either `uncons` or `stepLeg`.
     * Yields to head in form of chunk, then id of the scope that was active after step evaluated and tail of the `stream`.
     *
     * @param stream Stream to step
     * @param scopeId If scope has to be changed before this step is evaluated, id of the scope must be supplied
     */
    internal class Step<X>(val pull: Pull<X, Unit>, val token: Token?) :
      Eval<Nothing, Triple<Chunk<X>, Token, Pull<X, Unit>>?>() {
      override fun <P> mapOutput(f: (Nothing) -> P): Step<X> = this
    }

    class Effect<R>(val value: suspend () -> R) : Eval<Nothing, R>() {
      override fun <P> mapOutput(f: (Nothing) -> P): Pull<P, R> = this
    }

    class Acquire<R>(val resource: suspend () -> R, val release: suspend (R, ExitCase) -> Unit) : Eval<Nothing, R>() {
      override fun <P> mapOutput(f: (Nothing) -> P): Pull<P, R> = this
    }

    class OpenScope(val isInterruptible: Boolean) : Eval<Nothing, Token>() {
      override fun <P> mapOutput(f: (Nothing) -> P): Pull<P, Token> = this
    }

    // `InterruptedScope` contains id of the scope currently being interrupted
    // together with any errors accumulated during interruption process
    class CloseScope(
      val scopeId: Token,
      val interruptedScope: Pair<Token, Throwable?>?,
      val exitCase: ExitCase
    ) : Eval<Nothing, Unit>() {
      override fun <P> mapOutput(f: (Nothing) -> P): Pull<P, Unit> = this
    }

    object GetScope : Eval<Nothing, Scope>() {
      override fun <P> mapOutput(f: (Nothing) -> P): Pull<P, Scope> = this
    }
  }

  abstract class Bind<O, X, R>(val step: Pull<O, X>) : Pull<O, R>() {
    abstract fun cont(r: Result<X>): Pull<O, R>
    open fun delegate(): Bind<O, X, R> = this

    override fun <P> mapOutput(f: (O) -> P): Pull<P, R> =
      when (val res = ViewL(this)) {
        is EvalView<*, *, *> -> object : Bind<P, Any?, R>(res.step.mapOutput(f as (Any?) -> P)) {
          override fun cont(r: Result<Any?>): Pull<P, R> =
            res.next(r as Result<Nothing>).mapOutput(f as (Any?) -> P) as Pull<P, R>
        }
        is Result<*> -> res as Pull<P, R>
        else -> throw RuntimeException("Exhaustive when exception")
      }

    override fun toString(): String = "Pull.Bind($step)"
  }

  internal abstract class EvalView<O, X, R>(val step: Eval<O, X>) : ViewL<O, R> {
    open fun next(r: Result<X>): Pull<O, R> =
      r as Pull<O, R>
  }

  override fun toString(): String =
    "Pull(...)"
}

/**
 * Creates a scope that may be interrupted by calling scope#interrupt.
 */
fun <O> Pull<O, Unit>.interruptScope(): Pull<O, Unit> =
  Pull.scope_(this, true)

fun <O> Pull<O, Unit>.scope(): Pull<O, Unit> =
  Pull.scope_(this, false)

internal suspend fun <O, B> Pull<O, Unit>.compiler(init: B, foldChunk: (B, Chunk<O>) -> B): B =
  arrow.fx.coroutines.bracketCase(
    { Scope.newRoot() },
    { scope -> compile(this, scope, false, init, foldChunk) },
    { scope, ec -> scope.close(ec).fold({ throw it }, { Unit }) }
  )

fun <O, R> Pull<O, Pull<O, R>>.flatten(): Pull<O, R> =
  flatMap(::identity)

fun <O, O2 : O, R, R2> Pull<O, R>.flatMap(f: (R) -> Pull<O2, R2>): Pull<O2, R2> =
  object : Pull.Bind<O2, R, R2>(this@flatMap as Pull<O2, R>) {
    override fun cont(r: Result<R>): Pull<O2, R2> =
      when (r) {
        is Pure -> try {
          f(r.r)
        } catch (e: Throwable) {
          Fail(e.nonFatalOrThrow())
        }
        is Fail -> r
        is Interrupted<*> -> r
      }
  }

fun <O, R> Pull<O, R>.append(post: () -> Pull<O, R>): Pull<O, R> =
  object : Pull.Bind<O, R, R>(this@append) {
    override fun cont(r: Result<R>): Pull<O, R> =
      when (r) {
        is Pure -> post.invoke()
        is Fail -> r
        is Interrupted<*> -> r
      }
  }

fun <O, R, R2> Pull<O, R>.transformWith(f: (Result<R>) -> Pull<O, R2>): Pull<O, R2> =
  object : Pull.Bind<O, R, R2>(this@transformWith) {
    override fun cont(r: Result<R>): Pull<O, R2> =
      try {
        f(r)
      } catch (e: Throwable) {
        Fail(e.nonFatalOrThrow())
      }
  }

fun <O, R, R2> Pull<O, R>.map(f: (R) -> R2): Pull<O, R2> =
  object : Pull.Bind<O, R, R2>(this@map) {
    override fun cont(r: Result<R>): Pull<O, R2> =
      r.map(f)
  }

fun <O, R> Pull<O, R>.handleErrorWith(f: (Throwable) -> Pull<O, R>): Pull<O, R> =
  object : Pull.Bind<O, R, R>(this@handleErrorWith) {
    override fun cont(r: Result<R>): Pull<O, R> =
      when (r) {
        is Fail ->
          try {
            f(r.error)
          } catch (e: Throwable) {
            Fail(e.nonFatalOrThrow())
          }
        is Pure -> r
        is Interrupted<*> -> r
      }
  }

data class PullUncons<O>(val head: Chunk<O>, val tail: Pull<O, Unit>)
data class PullUncons1<O>(val head: O, val tail: Pull<O, Unit>)

fun <O> Pull<O, Unit>.unconsOrNull(): Pull<Nothing, PullUncons<O>?> =
  Pull.Eval.Step(this, null).map {
    it?.let { (h, _, t) -> PullUncons(h, t) }
  }

fun <O, B> Pull<O, B>.cons(c: Chunk<O>): Pull<O, B> =
  if (c.isEmpty()) this else Pull.output(c).flatMap { this }

fun <O, B> Pull<O, B>.cons(o: O): Pull<O, B> =
  Pull.output1(o).flatMap { this }

fun <O> Pull<O, Unit>.uncons1OrNull(): Pull<Nothing, PullUncons1<O>?> =
  unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(null)
      else -> {
        if (uncons.head.isEmpty()) uncons.tail.uncons1OrNull()
        else Pull.just(PullUncons1(uncons.head[0], uncons.tail.cons(uncons.head.tail())))
      }
    }
  }

/**
 * Like [unconsOrNull], but returns a chunk of no more than [n] elements.
 *
 * `Pull.just(null)` is returned if the end of the source stream is reached,
 *  or a negative chunk size is requested.
 */
fun <O> Pull<O, Unit>.unconsLimitOrNull(n: Int): Pull<Nothing, PullUncons<O>?> =
  unconsOrNull().flatMap { uncons ->
    when {
      uncons == null || n <= 0 -> Pull.just(null)
      uncons.head.size() < n -> Pull.just(uncons)
      else -> {
        val (out, rem) = uncons.head.splitAt(n)
        Pull.just(PullUncons(out, uncons.tail.cons(rem)))
      }
    }
  }

/**
 * Like [uncons], but returns a chunk of exactly `n` elements, splitting chunk as necessary.
 *
 * if `allowFewer` is `true` then `Pull.just` is returned if the end of the source stream is reached.
 */
fun <O> Pull<O, Unit>.unconsN(n: Int, allowFewer: Boolean = false): Pull<Nothing, PullUncons<O>?> {
  fun go(
    acc: Chunk.Queue<O>,
    n: Int,
    pull: Pull<O, Unit>
  ): Pull<Nothing, PullUncons<O>?> =
    pull.unconsOrNull().flatMap { uncons ->
      when (uncons) {
        null ->
          if (allowFewer && acc.isNotEmpty()) Pull.just(PullUncons(acc.toChunk(), Pull.done))
          else Pull.just(null)
        else -> {
          if (uncons.head.size() < n) go(acc.enqueue(uncons.head), n - uncons.head.size(), uncons.tail)
          else if (uncons.head.size() == n) Pull.just(PullUncons(acc.enqueue(uncons.head).toChunk(), uncons.tail))
          else {
            val (pfx, sfx) = uncons.head.splitAt(n)
            Pull.just(PullUncons((acc.enqueue(pfx)).toChunk(), uncons.tail.cons(sfx)))
          }
        }
      }
    }

  return if (n <= 0) Pull.just(PullUncons(Chunk.empty(), this))
  else go(Chunk.Queue.empty(), n, this)
}

/** Like [unconsOrNull] but skips over empty chunks, pulling until it can emit the first non-empty chunk. */
fun <O> Pull<O, Unit>.unconsNonEmptyOrNull(): Pull<Nothing, PullUncons<O>?> =
  unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(null)
      else ->
        if (uncons.head.isEmpty()) uncons.tail.unconsNonEmptyOrNull()
        else Pull.just(uncons)
    }
  }

/**
 * Drops the first `n` elements read by this [Pull] and returns
 */
fun <O> Pull<O, Unit>.drop(n: Long): Pull<O, Unit> {
  fun <O> Pull<O, Unit>.drop(n: Long): Pull<O, Pull<O, Unit>> =
    if (n <= 0) Pull.just(this)
    else unconsOrNull().flatMap { uncons ->
      when (uncons) {
        null -> Pull.just(Pull.done)
        else -> {
          val m = uncons.head.size()
          when {
            m < n -> uncons.tail.drop(n - m)
            m == n.toInt() -> Pull.just(uncons.tail)
            else -> Pull.just(uncons.tail.cons(uncons.head.drop(n.toInt())))
          }
        }
      }
    }

  return drop(n).flatten()
}

/** Like [dropWhile], but drops the first value which tests false. */
fun <O> Pull<O, Unit>.dropThrough(p: (O) -> Boolean): Pull<O, Unit> =
  dropWhile_(p, true).flatten()

/**
 * Drops elements of the this stream until the predicate `p` fails, and returns the new stream.
 * If defined, the first element of the returned stream will fail `p`.
 */
fun <O> Pull<O, Unit>.dropWhile(p: (O) -> Boolean): Pull<O, Unit> =
  dropWhile_(p, false).flatten()

fun <O> Pull<O, Unit>.dropWhile_(p: (O) -> Boolean, dropFailure: Boolean): Pull<O, Pull<O, Unit>> =
  unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(Pull.done)
      else -> {
        uncons.head.indexOfFirst { o -> !p(o) }?.let { idx ->
          val toDrop = if (dropFailure) idx + 1 else idx
          Pull.just(uncons.tail.cons(uncons.head.drop(toDrop)))
        } ?: uncons.tail.dropWhile_(p, dropFailure)
      }
    }
  }

/** Emits the first `n` elements of the input. */
fun <O> Pull<O, Unit>.take(n: Long): Pull<O, Pull<O, Unit>> =
  if (n <= 0) Pull.just(Pull.done)
  else unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(Pull.done)
      else -> {
        val m = uncons.head.size()
        when {
          m < n -> Pull.output(uncons.head).flatMap { uncons.tail.take(n - m) }
          m == n.toInt() -> Pull.output(uncons.head).map { uncons.tail }
          else -> {
            val (pfx, sfx) = uncons.head.splitAt(n.toInt())
            Pull.output(pfx).map { uncons.tail.cons(sfx) }
          }
        }
      }
    }
  }

/**
 * Repeatedly invokes `using`, running the resultant `Pull` each time, halting when a pull
 * returns `null` instead of `nextStream`.
 */
fun <O, B> Pull<O, Unit>.repeat(using: (Pull<O, Unit>) -> Pull<B, Pull<O, Unit>?>): Pull<B, Unit> =
  Pull.loop(this, using).void()

/** Emits the last `n` elements of the input. */
fun <O> Pull<O, Unit>.takeLast(n: Int): Pull<Nothing, Chunk.Queue<O>> {
  fun go(acc: Chunk.Queue<O>, s: Pull<O, Unit>): Pull<Nothing, Chunk.Queue<O>> =
    s.unconsN(n, true).flatMap { uncons ->
      when (uncons) {
        null -> Pull.just(acc)
        else -> go(acc.drop(uncons.head.size()).enqueue(uncons.head), uncons.tail)
      }
    }

  return if (n <= 0) Pull.just(Chunk.Queue.empty()) else go(Chunk.Queue.empty(), this)
}

fun <O> Pull<O, Unit>.takeThrough(p: (O) -> Boolean): Pull<O, Pull<O, Unit>> =
  takeWhile_(p, true)

fun <O> Pull<O, Unit>.takeWhile(p: (O) -> Boolean): Pull<O, Pull<O, Unit>> =
  takeWhile_(p, false)

fun <O> Pull<O, Unit>.takeWhile_(p: (O) -> Boolean, takeFailure: Boolean): Pull<O, Pull<O, Unit>> =
  unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(Pull.done)
      else -> {
        val index = uncons.head.indexOfFirst { o -> !p(o) }
        if (index != null) {
          val toTake = if (takeFailure) index + 1 else index
          val (pfx, sfx) = uncons.head.splitAt(toTake)
          Pull.output(pfx).flatMap { Pull.just(uncons.tail.cons(sfx)) }
        } else Pull.output(uncons.head).flatMap { uncons.tail.takeWhile_(p, takeFailure) }
      }
    }
  }

/** Reads a single element from the input and emits it to the output. */
fun <O> Pull<O, Unit>.echo1(): Pull<O, Pull<O, Unit>?> =
  uncons1OrNull().flatMap { uncons1 ->
    when (uncons1) {
      null -> Pull.just(null)
      else -> Pull.output1(uncons1.head).map { uncons1.tail }
    }
  }

/** Reads the next available chunk from the input and emits it to the output. */
fun <O> Pull<O, Unit>.echoChunk(): Pull<O, Pull<O, Unit>?> =
  unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(null)
      else -> Pull.output(uncons.head).map { uncons.tail }
    }
  }

/** Like [unconsN], but leaves the buffered input unconsumed. */
fun <O> Pull<O, Unit>.fetchN(n: Int): Pull<O, Pull<O, Unit>?> =
  unconsN(n).map { unconsN ->
    unconsN?.let { (hd, tl) ->
      tl.cons(hd)
    }
  }

/** Awaits the next available element where the predicate returns true. */
fun <O> Pull<O, Unit>.firstOrNull(f: (O) -> Boolean): Pull<Nothing, PullUncons1<O>?> =
  unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(null)
      else -> when (val idx = uncons.head.indexOfFirst(f)) {
        null -> uncons.tail.firstOrNull(f)
        else -> {
          if (idx + 1 < uncons.head.size()) {
            val rem = uncons.head.drop(idx + 1)
            Pull.just(PullUncons1(uncons.head[idx], uncons.tail.cons(rem)))
          } else Pull.just(PullUncons1(uncons.head[idx], uncons.tail))
        }
      }
    }
  }

/** Writes a single `true` value if all input matches the predicate, `false` otherwise. */
fun <O> Pull<O, Unit>.forall(p: (O) -> Boolean): Pull<Nothing, Boolean> =
  unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(true)
      else -> when (uncons.head.indexOfFirst { o -> !p(o) }) {
        null -> uncons.tail.forall(p)
        else -> Pull.just(false)
      }
    }
  }

/** Like [uncons] but does not consume the chunk (i.e., the chunk is pushed back). */
fun <O> Pull<O, Unit>.peek(): Pull<Nothing, PullUncons<O>?> =
  unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(null)
      else -> Pull.just(PullUncons(uncons.head, uncons.tail.cons(uncons.head)))
    }
  }

/** Like [uncons1] but does not consume the element (i.e., the element is pushed back). */
fun <O> Pull<O, Unit>.peek1(): Pull<Nothing, PullUncons1<O>?> =
  uncons1OrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.just(null)
      else -> Pull.just(PullUncons1(uncons.head, uncons.tail.cons(uncons.head)))
    }
  }

/**
 * Folds all inputs using an initial value `z` and supplied binary operator, and writes the final
 * result to the output of the supplied `Pull` when the stream has no more values.
 */
fun <O, O2> Pull<O, Unit>.fold(initial: O2, f: (O2, O) -> O2): Pull<Nothing, O2> =
  unconsOrNull().flatMap {
    when (it) {
      null -> Pull.just(initial)
      else -> {
        val acc = it.head.fold(initial, f)
        it.tail.fold(acc, f)
      }
    }
  }

/**
 * Folds all inputs using the supplied binary operator, and writes the final result to the output of
 * the supplied `Pull` when the stream has no more values.
 */
fun <O> Pull<O, Unit>.fold1(f: (O, O) -> O): Pull<Nothing, O?> =
  uncons1OrNull().flatMap { uncons1 ->
    when (uncons1) {
      null -> Pull.just(null)
      else -> {
        uncons1.tail.fold(uncons1.head, f)
      }
    }
  }

fun <O, O2> Pull<O, Unit>.flatMapOutput(f: (O) -> Pull<O2, Unit>): Pull<O2, Unit> =
  unconsOrNull().flatMap { uncons ->
    when (uncons) {
      null -> Pull.done
      else -> when {
        uncons.tail is Pure &&
          uncons.head.size() == 1 -> f(uncons.head[0])
        else -> {
          fun go(idx: Int): Pull<O2, Unit> =
            if (idx == uncons.head.size()) uncons.tail.flatMapOutput(f)
            else f(uncons.head[idx]).transformWith { res ->
              when (res) {
                is Pure -> go(idx + 1)
                is Fail -> Fail(res.error)
                is Interrupted<*> ->
                  interruptBoundary(uncons.tail, res.context as Token, res.deferredError)
                    .flatMapOutput(f)
              }
            }

          go(0)
        }
      }
    }
  }

/**
 * Interpret this `Pull` to produce a `Stream`, introducing a scope.
 *
 * May only be called on pulls which return a `Unit` result type. Use `p.void.stream` to explicitly
 * ignore the result type of the pull.
 */
fun <O> Pull<O, Unit>.stream(): Stream<O> =
  Stream(scope())

/**
 * Like `scan` but `f` is applied to each chunk of the source stream.
 * The resulting chunk is emitted and the result of the chunk is used in the
 * next invocation of `f`. The final state value is returned as the result of the pull.
 */
fun <O, S, O2> Pull<O, Unit>.scanChunks(init: S, f: (S, Chunk<O>) -> Pair<S, Chunk<O2>>): Pull<O2, S> =
  scanChunksOpt(init) { s -> { c: Chunk<O> -> f(s, c) } }

/**
 * More general version of `scanChunks` where the current state (i.e., `S`) can be inspected
 * to determine if another chunk should be pulled or if the pull should terminate.
 * Termination is signaled by returning `None` from `f`. Otherwise, a function which consumes
 * the next chunk is returned wrapped in `Some`. The final state value is returned as the
 * result of the pull.
 */
fun <O, S, O2> Pull<O, Unit>.scanChunksOpt(
  init: S,
  f: (S) -> ((Chunk<O>) -> Pair<S, Chunk<O2>>)?
): Pull<O2, S> {
  fun go(acc: S, s: Pull<O, Unit>): Pull<O2, S> =
    when (val res = f(acc)) {
      null -> Pull.just(acc)
      else -> s.unconsOrNull().flatMap { uncons ->
        when (uncons) {
          null -> Pull.just(acc)
          else -> {
            val (s2, c) = res.invoke(uncons.head)
            Pull.output(c).flatMap { go(s2, uncons.tail) }
          }
        }
      }
    }

  return go(init, this)
}

/**
 * Like `uncons`, but instead of performing normal `uncons`, this will
 * run the stream up to the first chunk available.
 * Useful when zipping multiple streams (legs) into one stream.
 * Assures that scopes are correctly held for each stream `leg`
 * independently of scopes from other legs.
 *
 * If you are not pulling from multiple streams, consider using `uncons`.
 */
fun <O> Pull<O, Unit>.stepLeg(): Pull<Nothing, StepLeg<O>?> =
  Pull.getScope.flatMap { scope ->
    StepLeg(Chunk.empty(), scope.id, this).stepLeg()
  }

/**
 * When merging multiple streams, this represents step of one leg.
 *
 * It is common to `uncons`, however unlike `uncons`, it keeps track
 * of stream scope independently of the main scope of the stream.
 *
 * This assures, that after each next `stepLeg` each Stream `leg` keeps its scope
 * when interpreting.
 *
 * Usual scenarios is to first invoke `stream.pull.stepLeg` and then consume whatever is
 * available in `leg.head`. If the next step is required `leg.stepLeg` will yield next `Leg`.
 *
 * Once the stream will stop to be interleaved (merged), then `stream` allows to return to normal stream
 * invocation.
 *
 */
class StepLeg<O> internal constructor(
  val head: Chunk<O>,
  private val scopeId: Token,
  private val next: Pull<O, Unit>
) {

  /**
   * Converts this leg back to regular stream. Scope is updated to the scope associated with this leg.
   * Note that when this is invoked, no more interleaving legs are allowed, and this must be very last
   * leg remaining.
   *
   * Note that resulting stream won't contain the `head` of this leg.
   */
  fun stream(): Stream<O> =
    pull().stream()

  /**
   * Converts this leg back to regular pull. Scope is updated to the scope associated with this leg.
   * Note that when this is invoked, no more interleaving legs are allowed, and this must be very last
   * leg remaining.
   *
   * Note that resulting pull won't contain the `head` of this leg.
   */
  fun pull(): Pull<O, Unit> =
    Pull.loop(setHead(Chunk.empty())) { leg ->
      Pull.output(leg.head).flatMap { leg.stepLeg() }
    }.void()

  /** Replaces head of this leg. Useful when the head was not fully consumed. */
  fun setHead(nextHead: Chunk<O>): StepLeg<O> =
    StepLeg(nextHead, scopeId, next)

  /** Provides an `uncons`-like operation on this leg of the stream. */
  fun stepLeg(): Pull<Nothing, StepLeg<O>?> =
    Pull.Eval.Step(next, scopeId).map { step ->
      step?.let { (h, id, t) ->
        StepLeg(h, id, t)
      }
    }
}

/** Construct a [Pull.ViewL] in a stack-safe way. */
internal tailrec fun <O, Z> ViewL(free: Pull<O, Z>): Pull.ViewL<O, Z> =
  when (free) {
    is Result -> free
    is Pull.Eval -> object : EvalView<O, Any?, Z>(free as Pull.Eval<O, Any?>) {}
    is Pull.Bind<*, *, *> ->
      when (val b = free.step) {
        is Result<*> -> ViewL(free.cont(b as Result<Nothing>)) as Pull.ViewL<O, Z>
        is Pull.Eval -> object : EvalView<O, Any?, Z>(b as Pull.Eval<O, Any?>) {
          override fun next(r: Result<Any?>): Pull<O, Z> =
            free.cont(r as Result<Nothing>) as Pull<O, Z>
        }
        is Pull.Bind<*, *, *> -> {
          val nb = object : Pull.Bind<O, Any?, Z>(b.step as Pull<O, Any?>) {
            private val bdel: Bind<O, Any?, Z> = free.delegate() as Bind<O, Any?, Z>
            override fun cont(zr: Result<Any?>): Pull<O, Z> =
              object : Pull.Bind<O, Any?, Z>(b.cont(zr as Result<Nothing>) as Pull<O, Any?>) {
                override fun delegate(): Bind<O, Any?, Z> = bdel
                override fun cont(yr: Result<Any?>): Pull<O, Z> =
                  delegate().cont(yr)
              }
          }
          ViewL(nb)
        }
      }
  }
