@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import arrow.core.EmptyValue
import arrow.core.EmptyValue.unbox
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.collectionSizeOrDefault
import arrow.core.toNonEmptyListOrNull
import arrow.core.toNonEmptySetOrNull
import kotlin.contracts.ExperimentalContracts
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

@RaiseDSL
public inline fun <Error, A> IorRaise<Error>.forEachAccumulating(
  iterable: Iterable<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulating(iterable.iterator(), combine, block)

@RaiseDSL
public inline fun <Error, A> IorRaise<Error>.forEachAccumulating(
  sequence: Sequence<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulating(sequence.iterator(), combine, block)

@RaiseDSL
public inline fun <Error, A> IorRaise<Error>.forEachAccumulating(
  iterator: Iterator<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulatingImpl(iterator, combine) { item, _ -> block(item) }

@PublishedApi @JvmSynthetic
internal inline fun <Error, A> IorRaise<Error>.forEachAccumulatingImpl(
  iterator: Iterator<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference block: RaiseAccumulate<Error>.(item: A, hasErrors: Boolean) -> Unit
) {
  var error: Any? = EmptyValue
  for (item in iterator) {
    recover<NonEmptyList<Error>, Unit>({
      block(RaiseAccumulate(this), item, error != EmptyValue)
    }) { errors ->
      error = EmptyValue.combine(error, errors.reduce(combine), combine)
    }
  }
  return if (error === EmptyValue) Unit else accumulate(unbox<Error>(error))
}

@RaiseDSL
public inline fun <Error, A> IorRaise<NonEmptyList<Error>>.forEachAccumulating(
  iterable: Iterable<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulating(iterable.iterator(), block)

@RaiseDSL
public inline fun <Error, A> IorRaise<NonEmptyList<Error>>.forEachAccumulating(
  sequence: Sequence<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulating(sequence.iterator(), block)

@RaiseDSL
public inline fun <Error, A> IorRaise<NonEmptyList<Error>>.forEachAccumulating(
  iterator: Iterator<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulatingImpl(iterator) { item, _ -> block(item) }

/**
 * Allows to change what to do once the first error is raised.
 * Used to provide more performant [mapOrAccumulate].
 */
@PublishedApi @JvmSynthetic
internal inline fun <Error, A> IorRaise<NonEmptyList<Error>>.forEachAccumulatingImpl(
  iterator: Iterator<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(item: A, hasErrors: Boolean) -> Unit
) {
  val error: MutableList<Error> = mutableListOf()
  for (item in iterator) {
    recover({
      block(RaiseAccumulate(this), item, error.isNotEmpty())
    }) {
      error.addAll(it)
    }
  }
  error.toNonEmptyListOrNull()?.let(::accumulate)
}

/**
 * Transform every element of [iterable] using the given [transform], or accumulate all the occurred errors using [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> IorRaise<Error>.mapOrAccumulate(
  iterable: Iterable<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = buildList(iterable.collectionSizeOrDefault(10)) {
  forEachAccumulatingImpl(iterable.iterator(), combine) { item, hasErrors ->
    transform(item).also { if (!hasErrors) add(it) }
  }
}

/**
 * Transform every element of [iterable] using the given [transform], or accumulate all the occurred errors using the
 * error combiner from the [IorRaise] receiver.
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
@JvmName("mapOrAccumulateUsingScope")
public inline fun <Error, A, B> IorRaise<Error>.mapOrAccumulate(
  iterable: Iterable<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = mapOrAccumulate(iterable, combineError, transform)

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [iterable].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> IorRaise<NonEmptyList<Error>>.mapOrAccumulate(
  iterable: Iterable<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = buildList(iterable.collectionSizeOrDefault(10)) {
  forEachAccumulatingImpl(iterable.iterator()) { item, hasErrors ->
    transform(item).also { if (!hasErrors) add(it) }
  }
}

/**
 * Transform every element of [sequence] using the given [transform], or accumulate all the occurred errors using [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> IorRaise<Error>.mapOrAccumulate(
  sequence: Sequence<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = buildList {
  forEachAccumulatingImpl(sequence.iterator(), combine) { item, hasErrors ->
    transform(item).also { if (!hasErrors) add(it) }
  }
}

/**
 * Transform every element of [sequence] using the given [transform], or accumulate all the occurred errors using the
 * error combiner from the [IorRaise] receiver.
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
@JvmName("mapOrAccumulateUsingScope")
public inline fun <Error, A, B> IorRaise<Error>.mapOrAccumulate(
  sequence: Sequence<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = mapOrAccumulate(sequence, combineError, transform)

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [sequence].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> IorRaise<NonEmptyList<Error>>.mapOrAccumulate(
  sequence: Sequence<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = buildList {
  forEachAccumulatingImpl(sequence.iterator()) { item, hasErrors ->
    transform(item).also { if (!hasErrors) add(it) }
  }
}

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [NonEmptyList].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> IorRaise<NonEmptyList<Error>>.mapOrAccumulate(
  nonEmptyList: NonEmptyList<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): NonEmptyList<B> = requireNotNull(mapOrAccumulate(nonEmptyList.all, transform).toNonEmptyListOrNull())

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [NonEmptySet].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> IorRaise<NonEmptyList<Error>>.mapOrAccumulate(
  nonEmptySet: NonEmptySet<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): NonEmptySet<B> = buildSet(nonEmptySet.size) {
  forEachAccumulatingImpl(nonEmptySet.iterator()) { item, hasErrors ->
    transform(item).also { if (!hasErrors) add(it) }
  }
}.toNonEmptySetOrNull()!!

@RaiseDSL
public inline fun <K, Error, A, B> IorRaise<Error>.mapOrAccumulate(
  map: Map<K, A>,
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(Map.Entry<K, A>) -> B
): Map<K, B> = buildMap(map.size) {
  forEachAccumulatingImpl(map.entries.iterator(), combine) { item, hasErrors ->
    transform(item).also { if (!hasErrors) put(item.key, it) }
  }
}

@RaiseDSL
public inline fun <K, Error, A, B> IorRaise<NonEmptyList<Error>>.mapOrAccumulate(
  map: Map<K, A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(Map.Entry<K, A>) -> B
): Map<K, B> = buildMap(map.size) {
  forEachAccumulatingImpl(map.entries.iterator()) { item, hasErrors ->
    transform(item).also { if (!hasErrors) put(item.key, it) }
  }
}
