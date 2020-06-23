package arrow.fx.coroutines.stream

/**
 * DSL boundary to access terminal operators
 *
 * Terminal operators consume the stream
 */ // TODO report inline results in Exception in thread "main" java.lang.VerifyError: Bad type on operand stack
/* inline */ class TerminalOps<O>(private val s: Stream<O>) {

  suspend fun toList(): List<O> =
    compiler(mutableListOf()) { acc, ch -> acc.apply { addAll(ch.toList()) } }

  suspend fun toSet(): Set<O> =
    compiler(mutableSetOf()) { acc, ch -> acc.apply { addAll(ch.toList()) } }

  suspend fun drain(): Unit =
    foldChunks(Unit) { _, _ -> Unit }

  /**
   * Compiles this stream in to a value,
   * returning `null` if the stream emitted no values and returning the
   * last value emitted if values were emitted.
   *
   * When this method has returned, the stream has not begun execution -- this method simply
   * compiles the stream down to the target effect type.
   */
  suspend fun lastOrNull(): O? =
    foldChunks<O?>(null) { acc, c -> c.lastOrNull() ?: acc }

  /**
   * Compiles this stream in to a value,
   * raising a `NoSuchElementException` if the stream emitted no values
   * and returning the last value emitted otherwise.
   *
   * When this method has returned, the stream has not begun execution -- this method simply
   * compiles the stream down to the target effect type.
   */
  suspend fun lastOrError(): O =
    lastOrNull() ?: throw NoSuchElementException()

  /**
   * Compiles this stream in to a value by folding
   * the output chunks together, starting with the provided `init` and combining the
   * current value with each output chunk.
   *
   * When this method has returned, the stream has not begun execution -- this method simply
   * compiles the stream down to the target effect type.
   */
  suspend fun <B> foldChunks(init: B, f: (B, Chunk<O>) -> B): B =
    compiler(init, f)

  val resource: ResourceTerminalOps<O> =
    ResourceTerminalOps(s)

  private suspend fun <B> compiler(init: () -> B, foldChunk: (B, Chunk<O>) -> B): B =
    s.asPull.compiler(init.invoke(), foldChunk)

  private suspend fun <B> compiler(init: B, foldChunk: (B, Chunk<O>) -> B): B =
    s.asPull.compiler(init, foldChunk)
}
