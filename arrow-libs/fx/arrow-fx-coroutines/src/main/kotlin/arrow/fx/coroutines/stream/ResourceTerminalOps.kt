package arrow.fx.coroutines.stream

import arrow.fx.coroutines.Resource

/**
 * DSL boundary to access terminal operators as resources
 *
 * Terminal operators consume the stream
 */ // TODO report inline results in Exception in thread "main" java.lang.VerifyError: Bad type on operand stack
/* inline */ class ResourceTerminalOps<O>(private val s: Stream<O>) {

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
