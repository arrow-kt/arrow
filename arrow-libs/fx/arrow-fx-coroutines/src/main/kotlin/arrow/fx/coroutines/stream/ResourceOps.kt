package arrow.fx.coroutines.stream

import arrow.fx.coroutines.Resource

/** Opens DSL to consume [Stream] as a [Resource]. */
fun <O> Stream<O>.asResource(): ResourceOps<O> =
  ResourceOps(this)

/**
 * DSL boundary to access terminal operators as a [Resource]
 * Allows for consume a [Stream] as a [Resource],
 * meaning the root scope of the [Stream] remains open until [Resource.use] returns.
 *
 * This allows for safe consumption of streaming resources in terminal operators,
 * and inside the [Resource.use] combinator.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.stream.*
 * import arrow.fx.coroutines.Atomic
 *
 * class Logger {
 *   private val state = Atomic.unsafe(emptyList<String>())
 *
 *   suspend fun log(msg: String): Unit =
 *     state.update { it + msg }
 *
 *   suspend fun dumpLog(): Unit =
 *     println(state.get())
 * }
 *
 * fun openFileWithName(name: String): String =
 *   "File($name)"
 *
 * //sampleStart
 * suspend fun main(): Unit {
 *   val logger = Logger()
 *
 *   Stream.bracket({ openFileWithName("a") }, { name -> logger.log("finalizing: $name") })
 *     .append { Stream.bracket({ openFileWithName("b") }, { name -> logger.log("finalizing: $name") }) }
 *     .asResource()
 *     .lastOrError()
 *     .use { last -> logger.log("Using $last") }
 *
 *   logger.dumpLog() // [finalizing: File(a), Using File(b), finalizing: File(b)]
 * }
 * //sampleEnd
 * ```
 *
 * As you can see here, we can `use` the `last` streamed `File` before it gets closed by the Stream.
 * Since we consumed the Stream as `asResource().lastOrError()`, this extends the last scope to the returned `Resource`,
 * so we can safely `use` it and the `Stream` still properly closes all resources opened with `bracket`.
 */
class ResourceOps<O>(private val s: Stream<O>) {

  suspend fun toList(): Resource<List<O>> =
    compiler(mutableListOf()) { acc, ch -> acc.apply { addAll(ch.toList()) } }

  suspend fun drain(): Resource<Unit> =
    foldChunks(Unit) { _, _ -> Unit }

  /**
   * Compiles this stream in to a value,
   * returning `null` if the stream emitted no values and returning the
   * last value emitted if values were emitted.
   *
   * When this method has returned, the stream has not begun execution -- this method simply
   * compiles the stream down to the target effect type.
   */
  suspend fun lastOrNull(): Resource<O?> =
    foldChunks<O?>(null) { acc, c -> c.lastOrNull() ?: acc }

  /**
   * Compiles this stream in to a value,
   * raising a `NoSuchElementException` if the stream emitted no values
   * and returning the last value emitted otherwise.
   *
   * When this method has returned, the stream has not begun execution -- this method simply
   * compiles the stream down to the target effect type.
   */
  suspend fun lastOrError(): Resource<O> =
    lastOrNull().flatMap { o ->
      Resource.defer {
        Resource.just(o ?: throw NoSuchElementException())
      }
    }

  /**
   * Compiles this stream in to a value by folding
   * the output chunks together, starting with the provided `init` and combining the
   * current value with each output chunk.
   *
   * When this method has returned, the stream has not begun execution -- this method simply
   * compiles the stream down to the target effect type.
   */
  suspend fun <B> foldChunks(init: B, f: (B, Chunk<O>) -> B): Resource<B> =
    compiler(init, f)

  private suspend fun <B> compiler(init: B, foldChunk: (B, Chunk<O>) -> B): Resource<B> =
    Resource({ Scope.newRoot() }, { scope, ex -> scope.close(ex).fold({ throw it }, { Unit }) })
      .flatMap { scope ->
        Resource.defer {
          Resource.just(compile(s.asPull, scope, true, init, foldChunk))
        }
      }
}
