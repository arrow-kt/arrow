package arrow.fx.coroutines.stream

/**
 * Runs all the effects of this [Stream] and collects all emitted values into a [List].
 * If the [Stream] doesn't emit any values it returns [emptyList].
 *
 * This a terminal operator, meaning this functions `suspend`s until the [Stream] finishes.
 * If any errors are raised while streaming, it's thrown from this `suspend` scope.
 */
suspend fun <O> Stream<O>.toList(): List<O> =
  compiler(mutableListOf()) { acc, ch -> acc.apply { addAll(ch.toList()) } }

/**
 * Runs all the effects of this [Stream] and collects all emitted values into a [Set].
 * If the [Stream] doesn't emit any values it returns [emptySet].
 *
 * This a terminal operator, meaning this functions `suspend`s until the [Stream] finishes.
 * If any errors are raised while streaming, it's thrown from this `suspend` scope.
 */
suspend fun <O> Stream<O>.toSet(): Set<O> =
  compiler(mutableSetOf()) { acc, ch -> acc.apply { addAll(ch.toList()) } }

/**
 * Runs all the effects of this [Stream] and ignores all emitted values.
 *
 * This a terminal operator, meaning this functions `suspend`s until the [Stream] finishes.
 * If any errors are raised while streaming, it's thrown from this `suspend` scope.
 */
suspend fun <O> Stream<O>.drain(): Unit =
  foldChunks(Unit) { _, _ -> Unit }

/**
 * Runs the first effect of this [Stream], and returns `null` if the stream emitted a value
 * and returns the value if emitted.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.stream.*
 *
 * //sampleStart
 * suspend fun main(): Unit =
 *   Stream.range(0..1000)
 *     .firstOrNull()
 *     .let(::println) // 0
 * //sampleEnd
 * ```
 *
 * This a terminal operator, meaning this functions `suspend`s until the [Stream] finishes.
 * If any errors are raised while streaming, it's thrown from this `suspend` scope.
 */
suspend fun <O> Stream<O>.firstOrNull(): O? =
  take(1).foldChunks<O, O?>(null) { acc, c ->
    acc ?: c.firstOrNull()
  }

/**
 * Runs the first effect of this [Stream], raising a [NoSuchElementException] if the stream emitted no values
 * and returns the value if emitted.
 *
 * This a terminal operator, meaning this functions `suspend`s until the [Stream] finishes.
 * If any errors are raised while streaming, it's thrown from this `suspend` scope.
 */
suspend fun <O> Stream<O>.firstOrError(): O? =
  firstOrNull() ?: throw NoSuchElementException()

/**
 * Runs all the effects of this [Stream], and returns `null` if the stream emitted no values
 * and returning the last value emitted if values were emitted.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.stream.*
 *
 * //sampleStart
 * suspend fun main(): Unit =
 *   Stream(1, 2, 3)
 *     .lastOrNull()
 *     .let(::println) // 3
 * //sampleEnd
 * ```
 *
 * This a terminal operator, meaning this functions `suspend`s until the [Stream] finishes.
 * If any errors are raised while streaming, it's thrown from this `suspend` scope.
 */
suspend fun <O> Stream<O>.lastOrNull(): O? =
  foldChunks<O, O?>(null) { acc, c -> c.lastOrNull() ?: acc }

/**
 * Runs all the effects of this [Stream], raising a [NoSuchElementException] if the stream emitted no values
 * and returning the last value emitted otherwise.
 *
 * This a terminal operator, meaning this functions `suspend`s until the [Stream] finishes.
 * If any errors are raised while streaming, it's thrown from this `suspend` scope.
 */
suspend fun <O> Stream<O>.lastOrError(): O =
  lastOrNull() ?: throw NoSuchElementException()

/**
 * Folds all the effects of this stream in to a value by folding
 * the output chunks together, starting with the provided [init] and combining the
 * current value with each output chunk using [f]
 *
 * This a terminal operator, meaning this functions `suspend`s until the [Stream] finishes.
 * If any errors are raised while streaming, it's thrown from this `suspend` scope.
 */
suspend fun <O, B> Stream<O>.foldChunks(init: B, f: (B, Chunk<O>) -> B): B =
  compiler(init, f)

private suspend fun <O, B> Stream<O>.compiler(init: B, foldChunk: (B, Chunk<O>) -> B): B =
  asPull.compiler(init, foldChunk)
