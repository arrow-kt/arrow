package arrow.fx.coroutines.stream

import arrow.core.Either
import arrow.fx.coroutines.CancelToken
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.UnsafePromise
import arrow.fx.coroutines.andThen
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.stream.concurrent.Queue
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.experimental.ExperimentalTypeInference

interface EmitterSyntax<A> {
  fun emit(a: A): Unit
  fun emit(chunk: Chunk<A>): Unit
  fun emit(iterable: Iterable<A>): Unit
  fun emit(vararg aas: A): Unit
  fun end(): Unit
}

/**
 * Creates a Stream from the given suspended block callback, allowing to emit, set cancel effects and end the emission.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.stream.*
 *
 * //sampleStart
 * suspend fun main(): Unit =
 *   Stream.callback {
 *       emit(1)
 *       emit(2, 3, 4)
 *       end()
 *     }
 *     .toList()
 *     .let(::println) //[1, 2, 3, 4]
 * //sampleEnd
 * ```
 *
 * Note that if neither `end()` nor other limit operators such as `take(N)` are called,
 * then the Stream will never end.
 */
@UseExperimental(ExperimentalTypeInference::class)
fun <A> Stream.Companion.callback(@BuilderInference f: suspend EmitterSyntax<A>.() -> Unit): Stream<A> =
  Stream.cancellable(f.andThen { CancelToken.unit })

/**
 * Creates a cancellable Stream from the given suspended block that will evaluate the passed [CancelToken] if cancelled.
 *
 * The suspending [f] runs in an uncancellable manner, acquiring [CancelToken] as a resource.
 * If cancellation signal is received while [cb] is running, then the [CancelToken] will be triggered as soon as it's returned.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 * import arrow.fx.coroutines.stream.*
 * import java.lang.RuntimeException
 * import java.util.concurrent.Executors
 * import java.util.concurrent.Future
 *
 * typealias Callback = (List<String>?, Throwable?) -> Unit
 *
 * class GithubId
 * object GithubService {
 *   private val listeners: MutableMap<GithubId, Future<*>> = mutableMapOf()
 *   fun getUsernames(callback: Callback): GithubId {
 *     val id = GithubId()
 *     val future = Executors.newSingleThreadExecutor().run {
 *       submit {
 *         Thread.sleep(300)
 *         callback(listOf("Arrow - 1"), null)
 *         Thread.sleep(300)
 *         callback(listOf("Arrow - 2"), null)
 *         Thread.sleep(300)
 *         callback(listOf("Arrow - 3"), null)
 *         shutdown()
 *       }
 *     }
 *     listeners[id] = future
 *     return id
 *   }
 *
 *   fun unregisterCallback(id: GithubId): Unit {
 *     listeners[id]?.cancel(false)
 *     listeners.remove(id)
 *   }
 * }
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   fun getUsernames(): Stream<String> =
 *     Stream.cancellable<String> {
 *       val id = GithubService.getUsernames { names, throwable ->
 *         when {
 *           names != null -> emit(names)
 *           throwable != null -> throw throwable
 *           else -> throw RuntimeException("Null result and no exception")
 *         }
 *       }
 *       CancelToken { GithubService.unregisterCallback(id) }
 *     }.take(3)
 *
 *   val result = getUsernames()
 *     .effectTap { println(it) }
 *     .toList()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * If neither `end()` nor other limit operators such as `take(N)` are called,
 * then the Stream will never end.
 */
@UseExperimental(ExperimentalTypeInference::class)
fun <A> Stream.Companion.cancellable(@BuilderInference f: suspend EmitterSyntax<A>.() -> CancelToken): Stream<A> =
  force {
    val q = Queue.unbounded<Chunk<A>>()
    val error = UnsafePromise<Throwable>()
    val cancel = Promise<CancelToken>()

    Stream.bracketCase({
      ForkAndForget { emitterCallback(f, cancel, error, q) }
    }, { f, exit ->
      when (exit) {
        is ExitCase.Cancelled -> cancel.get().cancel.invoke()
        else -> Unit
      }
      f.cancel()
    }).flatMap {
      q.dequeue()
        .interruptWhen { Either.Left(error.join()) }
        .terminateOn { it === END }
        .flatMap(::chunk)
    }
  }

private suspend fun <A> emitterCallback(
  f: suspend EmitterSyntax<A>.() -> CancelToken,
  cancel: Promise<CancelToken>,
  error: UnsafePromise<Throwable>,
  q: Queue<Chunk<A>>
): Unit {
  val cb = { ch: Chunk<A> ->
    suspend {
      q.enqueue1(ch)
    }.startCoroutine(Continuation(EmptyCoroutineContext) { r ->
      r.fold({ Unit }, { e -> error.complete(Result.success(e)) })
    })
  }

  val emitter = object : EmitterSyntax<A> {
    override fun emit(a: A) {
      emit(Chunk.just(a))
    }

    override fun emit(chunk: Chunk<A>) {
      cb(chunk)
    }

    override fun emit(iterable: Iterable<A>) {
      cb(Chunk.iterable(iterable))
    }

    override fun emit(vararg aas: A) {
      cb(Chunk(*aas))
    }

    override fun end() {
      cb(END)
    }
  }

  guaranteeCase({
    val cancelT = emitter.f()
    cancel.complete(cancelT)
  }, { exit ->
    when (exit) {
      is ExitCase.Failure -> error.complete(Result.success(exit.failure))
      else -> Unit
    }
  })
}

private object END : Chunk<Nothing>() {
  override fun size(): Int = 0
  override fun get(i: Int): Nothing =
    throw throw RuntimeException("NULL_CHUNK[$i]")

  override fun copyToArray_(xs: Array<Any?>, start: Int) {}
  override fun splitAtChunk_(n: Int): Pair<Chunk<Nothing>, Chunk<Nothing>> =
    Pair(empty(), empty())
}
