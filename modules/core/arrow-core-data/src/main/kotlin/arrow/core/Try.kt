package arrow.core

import arrow.higherkind

typealias Failure = Try.Failure
typealias Success<A> = Try.Success<A>

/**
 *
 * ank_macro_hierarchy(arrow.core.Option)
 *
 * {:.beginner}
 * beginner
 *
 * Arrow has [lots of different types of error handling and reporting](http://arrow-kt.io/docs/patterns/error_handling/), which allows you to choose the best strategy for your situation.
 *
 * For example, we have `Option` to model the absence of a value, or `Either` to model the return of a function as a type that may have been successful, or may have failed.
 *
 * On the other hand, we have `Try`, which represents a computation that can result in an `A` result (as long as the computation is successful) or in an exception if something has gone wrong.
 *
 * That is, there are only two possible implementations of `Try`: a `Try` instance where the operation has been successful, which is represented as `Success<A>`; or a `Try` instance where the computation has failed with a `Throwable`, which is represented as `Failure`.
 *
 * With just this explanation you might think that we are talking about an `Either<Throwable, A>`, and you are not wrong. `Try` can be implemented in terms of `Either`, but its use cases are very different.
 *
 * If we know that an operation could result in a failure, for example, because it is code from a library over which we have no control, or better yet, some method from the language itself. We can use `Try` as a substitute for the well-known `try-catch`, allowing us to rise to all its goodness.
 *
 * The following example represents the typical case when consuming Java code, where domain errors are represented with exceptions.
 *
 * ```kotlin:ank
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 * ```
 *
 * The traditional way to control this would be to use a `try-catch` block, as we have said before:
 *
 * ```kotlin:ank:playground
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 *
 * fun exceptionExample() {
 *  //sampleStart
 *  try {
 *    getLotteryNumbers()
 *  } catch (e: NoConnectionException) {
 *    println("No Connection Exception")
 *  } catch (e: AuthorizationException) {
 *    println("Authorization Exception")
 *  }
 *  //sampleEnd
 * }
 * fun main() {
 *  exceptionExample()
 * }
 * ```
 *
 * However, we could use `Try` to retrieve the computation result in a much cleaner way:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 *
 * val lotteryTry =
 * //sampleStart
 *  Try { getLotteryNumbers() }
 * //sampleEnd
 * fun main() {
 *  println(lotteryTry)
 * }
 * ```
 *
 * By using `getOrDefault` we can give a default value to return, when the computation fails, similar to what we can also do with `Option` when there is no value:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 * import arrow.core.getOrDefault
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 *
 * val lotteryTry = Try { getLotteryNumbers() }
 * val value =
 * //sampleStart
 *  lotteryTry.getOrDefault { emptyList() }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * If the underlying failure is useful to determine the default value, `getOrElse` can be used:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 * import arrow.core.getOrElse
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 *
 * val lotteryTry = Try { getLotteryNumbers() }
 * val value =
 * //sampleStart
 *  lotteryTry.getOrElse { ex: Throwable -> emptyList() }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * `getOrElse` can generally be used anywhere `getOrDefault` is used, ignoring the exception if it's not needed:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 * import arrow.core.getOrElse
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 *
 * val lotteryTry = Try { getLotteryNumbers() }
 * val value =
 * //sampleStart
 *  lotteryTry.getOrElse { emptyList() }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * If you want to perform a check on a possible success, you can use `filter` to convert successful computations in failures if conditions aren't met:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 *
 * val lotteryTry = Try { getLotteryNumbers() }
 * val value =
 * //sampleStart
 *  lotteryTry.filter {
 *    it.size < 4
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * We can also use `handleError` which allow us to recover from a particular error (we receive the error and have to return a new value):
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 * import arrow.core.handleError
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 *
 * val lotteryTry = Try { getLotteryNumbers() }
 * val value =
 * //sampleStart
 *  lotteryTry.handleError { exception ->
 *    emptyList()
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 * Or if you have another different computation that can also fail, you can use `handleErrorWith` to recover from an error (as you do with `handleError`, but in this case, returning a new `Try`):
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 * import arrow.core.handleErrorWith
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 * fun getLotteryNumbers(source: Source): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 * //sampleStart
 * enum class Source {
 *  CACHE, NETWORK
 * }
 *
 * val value = Try { getLotteryNumbers(Source.NETWORK) }.handleErrorWith {
 *  Try { getLotteryNumbers(Source.CACHE) }
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * When you want to handle both cases of the computation you can use `fold`. With `fold` we provide two functions, one for transforming a failure into a new value, the second one to transform the success value into a new one:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 *
 * val lotteryTry = Try { getLotteryNumbers() }
 * val value =
 * //sampleStart
 *  lotteryTry.fold(
 *    { emptyList<String>() },
 *    { it.filter { it.toIntOrNull() != null } })
 * //sampleEnd
 * fun main() {
 *   println(value)
 * }
 * ```
 *
 * When using Try, it is a common scenario to convert the returned `Try<Throwable, DomainObject>` instance to `Either<DomainError, DomainObject>`. One can use `toEither`, and than call `mapLeft` to achieve this goal:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 * //sampleStart
 *  sealed class DomainError(val message: String, val cause: Throwable) {
 *  class GeneralError(message: String, cause: Throwable) : DomainError(message, cause)
 *  class NoConnectionError(message: String, cause: Throwable) : DomainError(message, cause)
 *  class AuthorizationError(message: String, cause: Throwable) : DomainError(message, cause)
 * }
 *
 * val value =
 *  Try {
 *    getLotteryNumbersFromCloud()
 *  }.toEither()
 *   .mapLeft {
 *    DomainError
 *    .NoConnectionError("Failed to fetch lottery numbers from cloud", it)
 *  }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * As the codebase grows, it is easy to recognize, that this pattern reoccurs everywhere when `Try` to `Either` conversion is being used.
 *
 * To help this problem, `Try` has a convenient `toEither` implementation, which takes an `onLeft: (Throwable) -> B` parameter. If the result of the conversion from `Try` to `Either` fails, the supplied `onLeft` argument is called to supply domain specific value for the left (error) branch. Using this version, the code can be simplified to the one below:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 *
 * open class GeneralException: Exception()
 *
 * class NoConnectionException: GeneralException()
 *
 * class AuthorizationException: GeneralException()
 *
 * fun checkPermissions() {
 *  throw AuthorizationException()
 * }
 *
 * fun getLotteryNumbersFromCloud(): List<String> {
 *  throw NoConnectionException()
 * }
 *
 * fun getLotteryNumbers(): List<String> {
 *  checkPermissions()
 *
 *  return getLotteryNumbersFromCloud()
 * }
 *
 *  sealed class DomainError(val message: String, val cause: Throwable) {
 *  class GeneralError(message: String, cause: Throwable) : DomainError(message, cause)
 *  class NoConnectionError(message: String, cause: Throwable) : DomainError(message, cause)
 *  class AuthorizationError(message: String, cause: Throwable) : DomainError(message, cause)
 * }
 *
 * val value =
 * //sampleStart
 *  Try {
 *   getLotteryNumbersFromCloud()
 *  }.toEither {
 *     DomainError.NoConnectionError("Failed to fetch lottery numbers from cloud", it)
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * Lastly, Arrow contains `Try` instances for many useful typeclasses that allows you to use and transform fallibale values:
 *
 * [Functor](/docs/arrow/typeclasses/functor/)
 *
 * Transforming the value, if the computation is a success:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Try
 *
 * val value =
 * //sampleStart
 *  Try { "3".toInt() }.map { it + 1 }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * [Applicative](/docs/arrow/typeclasses/applicative/)
 *
 * Computing over independent values:
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.`try`.apply.tupled
 * import arrow.core.Try
 *
 * val value =
 * //sampleStart
 *  tupled(Try { "3".toInt() }, Try { "5".toInt() }, Try { "nope".toInt() })
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * [Monad](/docs/arrow/typeclasses/monad/)
 *
 * Computing over dependent values ignoring failure:
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.fx
 * import arrow.core.Try
 *
 * val value =
 * //sampleStart
 *  Try.fx {
 *    val (a) = Try { "3".toInt() }
 *    val (b) = Try { "4".toInt() }
 *    val (c) = Try { "5".toInt() }
 *    a + b + c
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.fx
 * import arrow.core.Try
 *
 * val value =
 * //sampleStart
 *  Try.fx {
 *    val (a) = Try { "none".toInt() }
 *    val (b) = Try { "4".toInt() }
 *    val (c) = Try { "5".toInt() }
 *    a + b + c
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * Computing over dependent values that are automatically lifted to the context of `Try`:
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.fx
 * import arrow.core.Try
 *
 * val value =
 * //sampleStart
 *  Try.fx {
 *    val a = "none".toInt()
 *    val b = "4".toInt()
 *    val c = "5".toInt()
 *    a + b + c
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ### Supported type classes
 *
 * ```kotlin:ank:replace
 * import arrow.reflect.DataType
 * import arrow.reflect.tcMarkdownList
 * import arrow.core.Try
 *
 * DataType(Try::class).tcMarkdownList()
 * ```
 *
 */
@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("Either<Throwable, A>")
)
@higherkind
sealed class Try<out A> : TryOf<A> {

  companion object {

    @Deprecated(
      "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
      ReplaceWith("Either.just(a)")
    )
    fun <A> just(a: A): Try<A> = Success(a)

    @Deprecated(
      "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
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
      "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
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
      "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
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
    "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
    ReplaceWith("Left")
  )
  data class Failure(val exception: Throwable) : Try<Nothing>() {
    override fun isFailure(): Boolean = true

    override fun isSuccess(): Boolean = false
  }

  @Deprecated(
    "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
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
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("EitherOf<*, B>.getOrElse(default)")
)
fun <B> TryOf<B>.getOrDefault(default: () -> B): B = fix().fold({ default() }, ::identity)

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("EitherOf<*, B>.getOrElse(default)")
)
fun <B> TryOf<B>.getOrElse(default: (Throwable) -> B): B = fix().fold(default, ::identity)

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("EitherOf<*, B>.orNull()")
)
fun <B> TryOf<B>.orNull(): B? = getOrElse { null }

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("EitherOf<A, B>.getOrHandle(default)")
)
fun <B, A : B> TryOf<A>.orElse(f: () -> TryOf<B>): Try<B> = when (fix()) {
  is Try.Success -> fix()
  is Try.Failure -> f().fix()
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("EitherOf<A, B>.handleErrorWith(f)")
)
fun <B> TryOf<B>.handleError(f: (Throwable) -> B): Try<B> = fix().fold({ Success(f(it)) }, { Success(it) })

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("EitherOf<A, B>.handleErrorWith(f)")
)
fun <B> TryOf<B>.handleErrorWith(f: (Throwable) -> TryOf<B>): Try<B> = fix().fold({ f(it).fix() }, { Success(it) })

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("Either.catch(this)")
)
fun <A> (() -> A).try_(): Try<A> = Try(this)

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("A.right()")
)
fun <A> A.success(): Try<A> = Success(this)

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or an effect handler like IO",
  ReplaceWith("A.left()")
)
fun Throwable.failure(): Try<Nothing> = Failure(this)

fun <T> TryOf<TryOf<T>>.flatten(): Try<T> = fix().flatMap(::identity)
