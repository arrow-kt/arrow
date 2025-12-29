@file:OptIn(ExperimentalTypeInference::class, ExperimentalStdlibApi::class, ExperimentalContracts::class)
@file:Suppress("API_NOT_AVAILABLE", "RESERVED_MEMBER_INSIDE_VALUE_CLASS")

package arrow.core

import arrow.core.raise.RaiseAccumulate
import arrow.core.raise.either
import arrow.core.raise.mapOrAccumulate
import arrow.core.raise.withError
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmExposeBoxed
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmStatic

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
@JvmInline
public value class NonEmptySet<out E> @PotentiallyUnsafeNonEmptyOperation internal constructor(
  @PublishedApi internal val elements: Set<E>
) : Set<E> by elements, NonEmptyCollection<E> {
  public constructor(first: E, rest: Iterable<E>) : this(buildNonEmptySet<E, _>(rest.collectionSizeOrDefault(10) + 1) {
    add(first)
    addAll(rest)
    this
  }.elements)

  public override operator fun plus(elements: Iterable<@UnsafeVariance E>): NonEmptySet<E> = buildNonEmptySet(size + elements.collectionSizeOrDefault(10)) {
    addAll(this@NonEmptySet)
    addAll(elements)
    this
  }

  public override operator fun plus(element: @UnsafeVariance E): NonEmptySet<E> = buildNonEmptySet(size + 1) {
    addAll(elements)
    add(element)
    this
  }

  override fun isEmpty(): Boolean = false

  @JvmExposeBoxed @Suppress("USELESS_JVM_EXPOSE_BOXED")
  public fun toSet(): Set<E> = elements

  override fun toString(): String = elements.toString()

  override fun equals(other: Any?): Boolean =
    elements == other

  override fun hashCode(): Int =
    elements.hashCode()

  public override fun distinct(): NonEmptyList<E> = toNonEmptyList()

  public companion object {
    @JvmStatic @JvmExposeBoxed
    public fun <E> of(head: E, vararg t: E): NonEmptySet<E> =
      nonEmptySetOf(head, *t)

    @JvmStatic @JvmExposeBoxed
    public fun <E> of(values: Iterable<E>): NonEmptySet<E> =
      values.toNonEmptySetOrThrow()
  }
}

public inline fun <Error, E, T> NonEmptySet<E>.mapOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<Error, NonEmptySet<T>> = either {
  withError({ it.reduce(combine) }) { mapOrAccumulate(this@mapOrAccumulate, transform) }
}

public inline fun <Error, E, T> NonEmptySet<E>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(E) -> T
): Either<NonEmptyList<Error>, NonEmptySet<T>> = either {
  mapOrAccumulate(this@mapOrAccumulate, transform)
}

public fun <E> nonEmptySetOf(first: E, vararg rest: E): NonEmptySet<E> =
  NonEmptySet(first, rest.asIterable())

/**
 * Returns a [NonEmptySet] that contains a **copy** of the elements in [this].
 */
public fun <T> Iterable<T>.toNonEmptySetOrNull(): NonEmptySet<T>? = MonotoneMutableSet<T>(collectionSizeOrDefault(10)).run {
  addAll(this@toNonEmptySetOrNull)
  if (isNonEmpty()) asNonEmptySet() else null
}

/**
 * Returns a [NonEmptySet] that contains a **copy** of the elements in [this].
 */
public fun <T> Iterable<T>.toNonEmptySetOrNone(): Option<NonEmptySet<T>> =
  toNonEmptySetOrNull().toOption()

/**
 * Returns a [NonEmptySet] that contains a **copy** of the elements in [this].
 */
public fun <T> Iterable<T>.toNonEmptySetOrThrow(): NonEmptySet<T> = toNonEmptySetOrNull() ?: throw IllegalArgumentException("Cannot create NonEmptySet from empty Iterable")

@Deprecated("Same as Iterable extension", level = DeprecationLevel.HIDDEN)
public fun <E> Set<E>.toNonEmptySetOrNull(): NonEmptySet<E>? =
  (this as Iterable<E>).toNonEmptySetOrNull()

@Deprecated("Same as Iterable extension", level = DeprecationLevel.HIDDEN)
public fun <E> Set<E>.toNonEmptySetOrNone(): Option<NonEmptySet<E>> =
  toNonEmptySetOrNull().toOption()

/**
 * Returns a [NonEmptySet] that wraps the given [this], avoiding an additional copy.
 *
 * Any modification made to [this] will also be visible through the returned [NonEmptySet].
 * You are responsible for keeping the non-emptiness invariant at all times.
 */
@PotentiallyUnsafeNonEmptyOperation
public fun <T> Set<T>.wrapAsNonEmptySetOrThrow(): NonEmptySet<T> = wrapAsNonEmptySetOrNull() ?: throw IllegalArgumentException("Cannot create NonEmptySet from empty Set")

/**
 * Returns a [NonEmptySet] that wraps the given [this], avoiding an additional copy.
 *
 * Any modification made to [this] will also be visible through the returned [NonEmptySet].
 * You are responsible for keeping the non-emptiness invariant at all times.
 */
@PotentiallyUnsafeNonEmptyOperation
public fun <T> Set<T>.wrapAsNonEmptySetOrNull(): NonEmptySet<T>? = when {
  isEmpty() -> null
  else -> NonEmptySet(this)
}

/**
 * A mutable list that can only grow by adding elements.
 * Removing elements is not supported to preserve monotonicity.
 */
@SubclassOptInRequired(PotentiallyUnsafeNonEmptyOperation::class)
public interface MonotoneMutableSet<E>: MonotoneMutableCollection<E>, Set<E> {
  public companion object {
    // linked by default to match mutableSetOf behavior
    public operator fun <E> invoke(): MonotoneMutableSet<E> = Impl(isLinked = true)
    public operator fun <E> invoke(initialCapacity: Int): MonotoneMutableSet<E> = Impl(initialCapacity, isLinked = true)
    public operator fun <E> invoke(elements: Collection<E>): MonotoneMutableSet<E> = Impl(elements, isLinked = true)

    public fun <E> hash(): MonotoneMutableSet<E> = Impl(isLinked = false)
    public fun <E> hash(initialCapacity: Int): MonotoneMutableSet<E> = Impl(initialCapacity, isLinked = false)
    public fun <E> hash(elements: Collection<E>): MonotoneMutableSet<E> = Impl(elements, isLinked = false)
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  private class Impl<E> private constructor(private val underlying: MutableSet<E>) : MonotoneMutableSet<E>, NonEmptyCollection<E>, Set<E> by underlying {
    constructor(isLinked: Boolean) : this(if(isLinked) LinkedHashSet() else HashSet())
    constructor(initialCapacity: Int, isLinked: Boolean) : this(if(isLinked) LinkedHashSet(initialCapacity) else HashSet(initialCapacity))
    constructor(elements: Collection<E>, isLinked: Boolean) : this(if(isLinked) LinkedHashSet(elements) else HashSet(elements))

    override fun isEmpty(): Boolean = underlying.isEmpty()

    override fun _addAll(elements: Collection<E>) = underlying.addAll(elements)

    override fun _add(element: E) = underlying.add(element)

    override fun plus(element: E) = buildNonEmptySet<E, _>(size + 1) {
      addAll(this@Impl)
      add(element)
      this
    }

    override fun plus(elements: Iterable<E>) = buildNonEmptySet<E, _>(size) {
      addAll(this@Impl)
      addAll(elements)
      this
    }
  }
}

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
public fun <L, E> L.asNonEmptySet(): NonEmptySet<E> where L : Set<E>, L : NonEmptyCollection<E> = NonEmptySet(this)

public inline fun <E, L> buildNonEmptySet(
  builderAction: MonotoneMutableSet<E>.() -> L
): NonEmptySet<E> where L : MonotoneMutableSet<E>, L : NonEmptyCollection<E> {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableSet()).asNonEmptySet()
}

public inline fun <E, L> buildNonEmptySet(
  capacity: Int,
  builderAction: MonotoneMutableSet<E>.() -> L
): NonEmptySet<E> where L : MonotoneMutableSet<E>, L : NonEmptyCollection<E> {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
  return builderAction(MonotoneMutableSet(capacity)).asNonEmptySet()
}
