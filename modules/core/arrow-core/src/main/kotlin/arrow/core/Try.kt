package arrow.core

import arrow.higherkind

typealias Failure<A> = Try.Failure<A>
typealias Success<A> = Try.Success<A>

/**
 * The `Try` type represents a computation that may either result in an exception, or return a
 * successfully computed value.
 *
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/util/Try.scala
 */
@higherkind
sealed class Try<out A> : TryOf<A> {

  companion object {

    fun <A> just(a: A): Try<A> = Success(a)

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> TryOf<Either<A, B>>): Try<B> {
      val ev: Try<Either<A, B>> = f(a).fix()
      return when (ev) {
        is Failure -> Failure<B>(ev.exception).fix()
        is Success -> {
          val b: Either<A, B> = ev.value
          when (b) {
            is Either.Left -> tailRecM(b.a, f)
            is Either.Right -> Success(b.b)
          }
        }
      }

    }

    inline operator fun <A> invoke(f: () -> A): Try<A> =
      try {
        Success(f())
      } catch (e: Throwable) {
        Failure(e)
      }

    fun <A> raise(e: Throwable): Try<A> = Failure(e)

  }

  @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("getOrElse { ifEmpty }"))
  operator fun invoke() = get()

  fun <B> ap(ff: TryOf<(A) -> B>): Try<B> = ff.fix().flatMap { f -> map(f) }.fix()

  /**
   * Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
   */
  inline fun <B> flatMap(crossinline f: (A) -> TryOf<B>): Try<B> = fold({ raise(it) }, { f(it).fix() })

  /**
   * Maps the given function to the value from this `Success` or returns this if this is a `Failure`.
   */
  inline fun <B> map(crossinline f: (A) -> B): Try<B> = fold({ Failure(it) }, { Success(f(it)) })

  /**
   * Converts this to a `Failure` if the predicate is not satisfied.
   */
  inline fun filter(crossinline p: Predicate<A>): Try<A> =
    fold(
      { Failure(it) },
      { if (p(it)) Success(it) else Failure(TryException.PredicateException("Predicate does not hold for $it")) }
    )

  /**
   * Inverts this `Try`. If this is a `Failure`, returns its exception wrapped in a `Success`.
   * If this is a `Success`, returns a `Failure` containing an `UnsupportedOperationException`.
   */
  fun failed(): Try<Throwable> =
    fold(
      { Success(it) },
      { Failure(TryException.UnsupportedOperationException("Success")) }
    )

  /**
   * Applies `ifFailure` if this is a `Failure` or `ifSuccess` if this is a `Success`.
   */
  inline fun <B> fold(ifFailure: (Throwable) -> B, ifSuccess: (A) -> B): B =
    when (this) {
      is Failure -> ifFailure(exception)
      is Success -> ifSuccess(value)
    }

  abstract fun isFailure(): Boolean

  abstract fun isSuccess(): Boolean

  @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("fold({ Unit }, f)"))
  fun foreach(f: (A) -> Unit) {
    if (isSuccess()) f(get())
  }

  @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("map { f(it); it }"))
  fun onEach(f: (A) -> Unit): Try<A> = map {
    f(it)
    it
  }

  fun exists(predicate: Predicate<A>): Boolean = fold({ false }, { predicate(it) })

  @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("getOrElse { ifEmpty }"))
  abstract fun get(): A

  @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("map { body(it); it }"))
  fun onSuccess(body: (A) -> Unit): Try<A> {
    foreach(body)
    return this
  }

  @Deprecated(DeprecatedUnsafeAccess, ReplaceWith("fold ({ Try { body(it); it }}, { Try.just(it) })"))
  fun onFailure(body: (Throwable) -> Unit): Try<A> = when (this) {
    is Success -> this
    is Failure -> {
      body(exception)
      this
    }
  }

  fun toOption(): Option<A> = fold({ None }, { Some(it) })

  fun toEither(): Either<Throwable, A> = fold({ Left(it) }, { Right(it) })

  fun <B> foldLeft(initial: B, operation: (B, A) -> B): B = this.fix().fold({ initial }, { operation(initial, it) })

  fun <B> foldRight(initial: Eval<B>, operation: (A, Eval<B>) -> Eval<B>): Eval<B> = this.fix().fold({ initial }, { operation(it, initial) })

  /**
   * The `Failure` type represents a computation that result in an exception.
   */
  data class Failure<out A>(val exception: Throwable) : Try<A>() {
    override fun isFailure(): Boolean = true

    override fun isSuccess(): Boolean = false

    override fun get(): A {
      throw exception
    }
  }

  /**
   * The `Success` type represents a computation that return a successfully computed value.
   */
  data class Success<out A>(val value: A) : Try<A>() {
    override fun isFailure(): Boolean = false

    override fun isSuccess(): Boolean = true

    override fun get(): A = value
  }
}

sealed class TryException(override val message: String) : kotlin.Exception(message) {
  data class PredicateException(override val message: String) : TryException(message)
  data class UnsupportedOperationException(override val message: String) : TryException(message)
}

/**
 * Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
 *
 * ''Note:'': This will throw an exception if it is not a success and default throws an exception.
 */
fun <B> TryOf<B>.getOrDefault(default: () -> B): B = fix().fold({ default() }, ::identity)

/**
 * Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
 *
 * ''Note:'': This will throw an exception if it is not a success and default throws an exception.
 */
fun <B> TryOf<B>.getOrElse(default: (Throwable) -> B): B = fix().fold(default, ::identity)

/**
 * Returns the value from this `Success` or null if this is a `Failure`.
 */
fun <B> TryOf<B>.orNull(): B? = getOrElse { null }

fun <B, A : B> TryOf<A>.orElse(f: () -> TryOf<B>): Try<B> = when (this.fix()) {
  is Try.Success -> this.fix()
  is Try.Failure -> f().fix()
}

/**
 * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
 * This is like `flatMap` for the exception.
 */
fun <B> TryOf<B>.recoverWith(f: (Throwable) -> TryOf<B>): Try<B> = fix().fold({ f(it).fix() }, { Success(it) })

@Deprecated(DeprecatedAmbiguity, ReplaceWith("recoverWith(f)"))
fun <A> TryOf<A>.rescue(f: (Throwable) -> TryOf<A>): Try<A> = fix().recoverWith(f)

/**
 * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
 * This is like map for the exception.
 */
fun <B> TryOf<B>.recover(f: (Throwable) -> B): Try<B> = fix().fold({ Success(f(it)) }, { Success(it) })

@Deprecated(DeprecatedAmbiguity, ReplaceWith("recover(f)"))
fun <A> TryOf<A>.handle(f: (Throwable) -> A): Try<A> = fix().recover(f)

/**
 * Completes this `Try` by applying the function `ifFailure` to this if this is of type `Failure`,
 * or conversely, by applying `ifSuccess` if this is a `Success`.
 */
fun <A, B> TryOf<A>.transform(ifSuccess: (A) -> TryOf<B>, ifFailure: (Throwable) -> TryOf<B>): Try<B> = fix().fold({ ifFailure(it).fix() }, { fix().flatMap(ifSuccess) })

fun <A> (() -> A).try_(): Try<A> = Try(this)

fun <T> TryOf<TryOf<T>>.flatten(): Try<T> = fix().flatMap(::identity)