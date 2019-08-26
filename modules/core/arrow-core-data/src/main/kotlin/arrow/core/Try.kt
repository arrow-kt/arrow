package arrow.core

import arrow.higherkind

typealias Failure = Try.Failure
typealias Success<A> = Try.Success<A>

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("Either<Throwable, A>")
)
@higherkind
sealed class Try<out A> : TryOf<A> {

  companion object {

    @Deprecated(
      "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
      ReplaceWith("Either.just(a)")
    )
    fun <A> just(a: A): Try<A> = Success(a)

    @Deprecated(
      "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
      ReplaceWith("Either.tailRecM(a, f)")
    )
    tailrec fun <A, B> tailRecM(a: A, f: (A) -> TryOf<Either<A, B>>): Try<B> {
      val ev: Try<Either<A, B>> = f(a).fix()
      return when (ev) {
        is Failure -> Failure(ev.exception).fix()
        is Success -> {
          val b: Either<A, B> = ev.value
          when (b) {
            is Either.Left -> tailRecM(b.a, f)
            is Either.Right -> Success(b.b)
          }
        }
      }
    }

    @Deprecated(
      "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
      ReplaceWith("Either.catch(f)")
    )
    inline operator fun <A> invoke(f: () -> A): Try<A> =
      try {
        Success(f())
      } catch (e: Throwable) {
        if (NonFatal(e)) {
          Failure(e)
        } else {
          throw e
        }
      }

    @Deprecated(
      "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
      ReplaceWith("Either.raiseError(e)")
    )
    fun raiseError(e: Throwable): Try<Nothing> = Failure(e)
  }

  fun <B> ap(ff: TryOf<(A) -> B>): Try<B> = ff.fix().flatMap { f -> map(f) }.fix()

  /**
   * Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
   */
  fun <B> flatMap(f: (A) -> TryOf<B>): Try<B> =
    when (this) {
      is Failure -> this
      is Success -> f(value).fix()
    }

  /**
   * Maps the given function to the value from this `Success` or returns this if this is a `Failure`.
   */
  fun <B> map(f: (A) -> B): Try<B> =
    flatMap { Success(f(it)) }

  /**
   * Converts this to a `Failure` if the predicate is not satisfied.
   */
  fun filter(p: Predicate<A>): Try<A> =
    flatMap { if (p(it)) Success(it) else Failure(TryException.PredicateException("Predicate does not hold for $it")) }

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

  fun exists(predicate: Predicate<A>): Boolean = fold({ false }, { predicate(it) })

  fun toOption(): Option<A> = fold({ None }, { Some(it) })

  fun toEither(): Either<Throwable, A> = fold({ Left(it) }, { Right(it) })

  /**
   * Convenient method to solve a common scenario when using [Try]. The created [Try] object is often
   * converted to [Either], and right after [Either.mapLeft] is called to translate the [Throwable] to a
   * domain specific error object.<br>
   *
   * To make it easier this method takes an [onLeft] error domain object supplier, which does the conversion to domain error
   * in the same time as conversion to [Either] occurs.<br>
   *
   * So instead of
   * ```
   * Try {
   *    dangerousOperation()
   * }.toEither()
   *    .mapLeft { Error.ServerError("This really went wrong", it) }
   * // Left(a=Error.ServerError@3ada9e34)
   * ```
   * One can write
   * ```
   * Try {
   *    dangerousOperation()
   * }.toEither {
   *    Error.ServerError("This really went wrong", it)
   * }
   * // Left(a=Error.ServerError@4a5a3234)
   * ```
   */
  fun <B> toEither(onLeft: (Throwable) -> B): Either<B, A> = this.toEither().fold({ onLeft(it).left() }, { it.right() })

  fun <B> foldLeft(initial: B, operation: (B, A) -> B): B = fix().fold({ initial }, { operation(initial, it) })

  fun <B> foldRight(initial: Eval<B>, operation: (A, Eval<B>) -> Eval<B>): Eval<B> = fix().fold({ initial }, { operation(it, initial) })

  @Deprecated(
    "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
    ReplaceWith("Left")
  )
  data class Failure(val exception: Throwable) : Try<Nothing>() {
    override fun isFailure(): Boolean = true

    override fun isSuccess(): Boolean = false
  }

  @Deprecated(
    "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
    ReplaceWith("Right")
  )
  data class Success<out A>(val value: A) : Try<A>() {
    override fun isFailure(): Boolean = false

    override fun isSuccess(): Boolean = true
  }
}

sealed class TryException(override val message: String) : Exception(message) {
  data class PredicateException(override val message: String) : TryException(message)
  data class UnsupportedOperationException(override val message: String) : TryException(message)
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherOf<*, B>.getOrElse(default)")
)
fun <B> TryOf<B>.getOrDefault(default: () -> B): B = fix().fold({ default() }, ::identity)

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherOf<*, B>.getOrElse(default)")
)
fun <B> TryOf<B>.getOrElse(default: (Throwable) -> B): B = fix().fold(default, ::identity)

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherOf<*, B>.orNull()")
)
fun <B> TryOf<B>.orNull(): B? = getOrElse { null }

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherOf<A, B>.getOrHandle(default)")
)
fun <B, A : B> TryOf<A>.orElse(f: () -> TryOf<B>): Try<B> = when (fix()) {
  is Try.Success -> fix()
  is Try.Failure -> f().fix()
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherOf<A, B>.handleErrorWith(f)")
)
fun <B> TryOf<B>.handleError(f: (Throwable) -> B): Try<B> = fix().fold({ Success(f(it)) }, { Success(it) })

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherOf<A, B>.handleErrorWith(f)")
)
fun <B> TryOf<B>.handleErrorWith(f: (Throwable) -> TryOf<B>): Try<B> = fix().fold({ f(it).fix() }, { Success(it) })

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("Either.catch(this)")
)
fun <A> (() -> A).try_(): Try<A> = Try(this)

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("A.right()")
)
fun <A> A.success(): Try<A> = Success(this)

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("A.left()")
)
fun Throwable.failure(): Try<Nothing> = Failure(this)

fun <T> TryOf<TryOf<T>>.flatten(): Try<T> = fix().flatMap(::identity)
