package arrow.fx.coroutines.stream

/**
 * A stream transformation can be represented as a function from stream to stream.
 * This means that `Pipe` is also an alias for `fun Stream<I>.name(): Stream<O>`.
 *
 * Pipes are typically applied with the `through` operation on `Stream`.
 *
 * This is useful when you want to expose a `Stream` transformation from a data type.
 * This can not conveniently be done with extension functions.
 *
 * i.e. [arrow.fx.coroutines.stream.concurrent.Queue.dequeueBatch]
 */
@Deprecated("Pipe is deprecated as part of Stream deprecation.")
typealias Pipe<I, O> = (Stream<I>) -> Stream<O>

@Deprecated("Pipe is deprecated as part of Stream deprecation.")
fun <I, O> Pipe(pipe: (Stream<I>) -> Stream<O>): Pipe<I, O> = pipe

/** Transforms the left input of the given `Pipe2` using a `Pipe`. */
@Deprecated("Pipe is deprecated as part of Stream deprecation.")
fun <I, O, I1, O2> Pipe<I, O>.attachLeft(p: Pipe2<O, I1, O2>): Pipe2<I, I1, O2> =
  { l, r -> p(this(l), r) }

/** Transforms the right input of the given `Pipe2` using a `Pipe`. */
@Deprecated("Pipe is deprecated as part of Stream deprecation.")
fun <I, O, I0, O2> Pipe<I, O>.attachRight(p: Pipe2<I0, O, O2>): Pipe2<I0, I, O2> =
  { l, r -> p(l, this(r)) }

/**
 * A stream transformation that combines two streams in to a single stream,
 * represented as a function from two streams to a single stream.
 *
 * Since we cannot define extension functions with multiple subscribers,
 * `Pipe2` is not an alias for extension functions but allows more powerful transformations.
 *
 * `Pipe2`s are typically applied with the `through` operation on `Stream`.
 */
@Deprecated("Pipe is deprecated as part of Stream deprecation.")
typealias Pipe2<I, I2, O> = (Stream<I>, Stream<I2>) -> Stream<O>

@Deprecated("Pipe is deprecated as part of Stream deprecation.")
fun <I, I2, O> Pipe2(pipe: (Stream<I>, Stream<I2>) -> Stream<O>): Pipe2<I, I2, O> = pipe
