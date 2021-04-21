package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.core.Either.Left
import arrow.core.Either.Right

typealias ValidatedNel<E, A> = Validated<Nel<E>, A>
typealias Valid<A> = Validated.Valid<A>
typealias Invalid<E> = Validated.Invalid<E>

/**
 *
 *
 * Imagine you are filling out a web form to sign up for an account. You input your username and
 * password, then submit. A response comes back saying your username can't have dashes in it,
 * so you make some changes, then resubmit. You can't have special characters either. Change, resubmit.
 * Password needs to have at least one capital letter. Change, resubmit. Password needs to have at least one number.
 *
 * Or perhaps you're reading from a configuration file. One could imagine the configuration library
 * you're using returns an `Either`. Your parsing may look something like:
 *
 * ```kotlin:ank
 * import arrow.core.Either
 * import arrow.core.Either.Left
 * import arrow.core.flatMap
 *
 * //sampleStart
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * fun <A> config(key: String): Either<String, A> = Left(key)
 *
 * config<String>("url").flatMap { url ->
 *  config<Int>("port").map { ConnectionParams(url, it) }
 * }
 * //sampleEnd
 * ```
 *
 * You run your program and it says key "url" not found. Turns out the key was "endpoint." So
 * you change your code and re-run. Now it says the "port" key was not a well-formed integer.
 *
 * It would be nice to have all of these errors reported simultaneously. The username's inability to
 * have dashes can be validated separately from it not having special characters, as well as
 * from the password needing to have certain requirements. A misspelled (or missing) field in
 * a config can be validated separately from another field not being well-formed.
 *
 * # Enter `Validated`.
 *
 * ## Parallel Validation
 *
 * Our goal is to report any and all errors across independent bits of data. For instance, when
 * we ask for several pieces of configuration, each configuration field can be validated separately
 * from one another. How then do we ensure that the data we are working with is independent?
 * We ask for both of them up front.
 *
 * As our running example, we will look at config parsing. Our config will be represented by a
 * `Map<String, String>`. Parsing will be handled by a `Read` type class - we provide instances only
 * for `String` and `Int` for brevity.
 *
 * ```kotlin:ank
 * //sampleStart
 * abstract class Read<A> {
 *
 * abstract fun read(s: String): A?
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object: Read<String>() {
 *     override fun read(s: String): String? = s
 *    }
 *
 *   val intRead: Read<Int> =
 *    object: Read<Int>() {
 *     override fun read(s: String): Int? =
 *      if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
 *    }
 *  }
 * }
 * //sampleEnd
 * ```
 *
 * Then we enumerate our errors. When asking for a config value, one of two things can go wrong:
 * The field is missing, or it is not well-formed with regards to the expected type.
 *
 * ```kotlin:ank
 * sealed class ConfigError {
 *  data class MissingConfig(val field: String): ConfigError()
 *  data class ParseConfig(val field: String): ConfigError()
 * }
 * ```
 *
 * We need a data type that can represent either a successful value (a parsed configuration), or an error.
 * It would look like the following, which Arrow provides in `arrow.Validated`:
 *
 * ```kotlin
 * sealed class Validated<out E, out A> {
 *  data class Valid<out A>(val a: A) : Validated<Nothing, A>()
 *  data class Invalid<out E>(val e: E) : Validated<E, Nothing>()
 * }
 * ```
 *
 * Now we are ready to write our parser.
 *
 * ```kotlin:ank
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * //sampleStart
 * data class Config(val map: Map<String, String>) {
 *  fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> {
 *   val v = map[key]
 *   return when (v) {
 *    null -> Validated.Invalid(ConfigError.MissingConfig(key))
 *    else ->
 *     when (val s = read.read(v)) {
 *      null -> ConfigError.ParseConfig(key).invalid()
 *      else -> s.valid()
 *     }
 *   }
 *  }
 * }
 * //sampleEnd
 * ```
 *
 * And, as you can see, the parser runs sequentially: it first tries to get the map value and then tries to read it.
 * It's then straightforward to translate this to an effect block. We use here the `either` block which includes syntax
 * to obtain `A` from values of `Validated<*, A>` through the [arrow.core.computations.EitherEffect.invoke]
 *
 * ```kotlin:ank
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * //sampleStart
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }.bind()
 *     val readVal = Validated.fromNullable(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }.bind()
 *     readVal
 *   }
 * }
 * //sampleEnd
 * ```
 *
 * Everything is in place to write the parallel validator. Remember that we can only do parallel
 * validation if each piece is independent. How do we ensure the data is independent? By
 * asking for all of it up front. Let's start with two pieces of data.
 *
 * ```kotlin:ank
 * import arrow.core.Validated
 * //sampleStart
 * fun <E, A, B, C> parallelValidate(v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<E, C> {
 *  return when {
 *   v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.value, v2.value))
 *   v1 is Validated.Valid && v2 is Validated.Invalid -> v2
 *   v1 is Validated.Invalid && v2 is Validated.Valid -> v1
 *   v1 is Validated.Invalid && v2 is Validated.Invalid -> TODO()
 *   else -> TODO()
 *  }
 * }
 * //sampleEnd
 * ```
 *
 * We've run into a problem. In the case where both have errors, we want to report both. We
 * don't have a way to combine ConfigErrors. But, as clients, we can change our Validated
 * values where the error can be combined, say, a `List<ConfigError>`. We are going to use a
 * `NonEmptyList<ConfigError>`—the NonEmptyList statically guarantees we have at least one value,
 * which aligns with the fact that, if we have an Invalid, then we most certainly have at least one error.
 * This technique is so common there is a convenient method on `Validated` called `toValidatedNel`
 * that turns any `Validated<E, A>` value to a `Validated<NonEmptyList<E>, A>`. Additionally, the
 * type alias `ValidatedNel<E, A>` is provided.
 *
 * Time to validate:
 *
 * ```kotlin:ank
 * import arrow.core.NonEmptyList
 * import arrow.core.Validated
 * //sampleStart
 * fun <E, A, B, C> parallelValidate
 *   (v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<NonEmptyList<E>, C> =
 *  when {
 *   v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.value, v2.value))
 *   v1 is Validated.Valid && v2 is Validated.Invalid -> v2.toValidatedNel()
 *   v1 is Validated.Invalid && v2 is Validated.Valid -> v1.toValidatedNel()
 *   v1 is Validated.Invalid && v2 is Validated.Invalid -> Validated.Invalid(NonEmptyList(v1.value, listOf(v2.value)))
 *   else -> throw IllegalStateException("Not possible value")
 *  }
 * //sampleEnd
 * ```
 *
 * ### Improving the validation
 *
 * Kotlin says that our match is not exhaustive and we have to add `else`. To solve this, we would need to nest our when,
 * but that would complicate the code. To achieve this, Arrow provides [zip].
 * This function combines [Validated]s by accumulating errors in a tuple, which we can then map.
 * The above function can be rewritten as follows:
 *
 * ```kotlin:ank:silent
 * import arrow.core.Validated
 * import arrow.core.validNel
 * import arrow.core.zip
 * import arrow.typeclasses.Semigroup
 *
 * //sampleStart
 * val parallelValidate =
 *    1.validNel().zip(Semigroup.nonEmptyList<ConfigError>(), 2.validNel())
 *     { a, b -> /* combine the result */ }
 * //sampleEnd
 * ```
 *
 * Note that there are multiple `zip` functions with more arities, so we could easily add more parameters without worrying about
 * the function blowing up in complexity.
 *
 * When working with `NonEmptyList` in the `Invalid` side, there is no need to supply `Semigroup` as shown in the example above.
 *
 * ```kotlin:ank:silent
 * import arrow.core.Validated
 * import arrow.core.validNel
 * import arrow.core.zip
 *
 * //sampleStart
 * val parallelValidate =
 *   1.validNel().zip(2.validNel())
 *     { a, b -> /* combine the result */ }
 * //sampleEnd
 * ```
 *
 * ---
 *
 * Coming back to our example, when no errors are present in the configuration, we get a `ConnectionParams` wrapped in a `Valid` instance.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 * import arrow.core.NonEmptyList
 * import arrow.typeclasses.Semigroup
 *
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): A?
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): String? = s
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Int? =
 *      if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
 *    }
 *  }
 * }
 *
 * sealed class ConfigError {
 *  data class MissingConfig(val field: String) : ConfigError()
 *  data class ParseConfig(val field: String) : ConfigError()
 * }
 *
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }.bind()
 *     val readVal = Validated.fromNullable(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }.bind()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 *
 * suspend fun main() {
 * //sampleStart
 *  val config = Config(mapOf("url" to "127.0.0.1", "port" to "1337"))
 *
 *  val valid = config.parse(Read.stringRead, "url").zip(
 *    Semigroup.nonEmptyList<ConfigError>(),
 *    config.parse(Read.intRead, "port")
 *  ) { url, port -> ConnectionParams(url, port) }
 * //sampleEnd
 *  println("valid = $valid")
 * }
 * ```
 *
 * But what happens when we have one or more errors? They are accumulated in a `NonEmptyList` wrapped in
 * an `Invalid` instance.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 * import arrow.core.NonEmptyList
 * import arrow.typeclasses.Semigroup
 *
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): A?
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): String? = s
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Int? =
 *      if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
 *    }
 *  }
 * }
 *
 * sealed class ConfigError {
 *  data class MissingConfig(val field: String) : ConfigError()
 *  data class ParseConfig(val field: String) : ConfigError()
 * }
 *
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }.bind()
 *     val readVal = Validated.fromNullable(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }.bind()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 * suspend fun main() {
 * //sampleStart
 * val config = Config(mapOf("wrong field" to "127.0.0.1", "port" to "not a number"))
 *
 * val valid = config.parse(Read.stringRead, "url").zip(
 *  Semigroup.nonEmptyList<ConfigError>(),
 *  config.parse(Read.intRead, "port")
 * ) { url, port -> ConnectionParams(url, port) }
 * //sampleEnd
 *  println("valid = $valid")
 * }
 * ```
 *
 * ## Sequential Validation
 *
 * If you do want error accumulation, but occasionally run into places where sequential validation is needed,
 * then Validated provides a `withEither` method to allow you to temporarily turn a Validated
 * instance into an Either instance and apply it to a function.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.core.flatMap
 * import arrow.core.left
 * import arrow.core.right
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): A?
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): String? = s
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Int? =
 *      if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
 *    }
 *  }
 * }
 *
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }.bind()
 *     val readVal = Validated.fromNullable(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }.bind()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 * sealed class ConfigError {
 *  data class MissingConfig(val field: String) : ConfigError()
 *  data class ParseConfig(val field: String) : ConfigError()
 * }
 *
 * //sampleStart
 * fun positive(field: String, i: Int): Either<ConfigError, Int> =
 *  if (i >= 0) i.right()
 *  else ConfigError.ParseConfig(field).left()
 *
 * val config = Config(mapOf("house_number" to "-42"))
 *
 * suspend fun main() {
 *   val houseNumber = config.parse(Read.intRead, "house_number").withEither { either ->
 *     either.flatMap { positive("house_number", it) }
 *   }
 * //sampleEnd
 *  println(houseNumber)
 * }
 *
 * ```
 */
sealed class Validated<out E, out A> {

  companion object {

    @JvmStatic
    fun <E, A> invalidNel(e: E): ValidatedNel<E, A> = Invalid(NonEmptyList(e, listOf()))

    @JvmStatic
    fun <E, A> validNel(a: A): ValidatedNel<E, A> = Valid(a)

    /**
     * Converts an `Either<E, A>` to a `Validated<E, A>`.
     */
    @JvmStatic
    fun <E, A> fromEither(e: Either<E, A>): Validated<E, A> = e.fold({ Invalid(it) }, { Valid(it) })

    /**
     * Converts an `Option<A>` to a `Validated<E, A>`, where the provided `ifNone` output value is returned as [Invalid]
     * when the specified `Option` is `None`.
     */
    @JvmStatic
    inline fun <E, A> fromOption(o: Option<A>, ifNone: () -> E): Validated<E, A> =
      o.fold(
        { Invalid(ifNone()) },
        { Valid(it) }
      )

    /**
     * Converts a nullable `A?` to a `Validated<E, A>`, where the provided `ifNull` output value is returned as [Invalid]
     * when the specified value is null.
     */
    @JvmStatic
    inline fun <E, A> fromNullable(value: A?, ifNull: () -> E): Validated<E, A> =
      value?.let(::Valid) ?: Invalid(ifNull())

    @JvmStatic
    @JvmName("tryCatch")
    inline fun <A> catch(f: () -> A): Validated<Throwable, A> =
      try {
        f().valid()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalid()
      }

    @JvmStatic
    @JvmName("tryCatch")
    inline fun <E, A> catch(recover: (Throwable) -> E, f: () -> A): Validated<E, A> =
      catch(f).mapLeft(recover)

    @JvmStatic
    inline fun <A> catchNel(f: () -> A): ValidatedNel<Throwable, A> =
      try {
        f().validNel()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalidNel()
      }

    /**
     * Lifts a function `A -> B` to the [Validated] structure.
     *
     * `A -> B -> Validated<E, A> -> Validated<E, B>`
     *
     * ```kotlin:ank:playground:extension
     * import arrow.core.*
     *
     * fun main(args: Array<String>) {
     *   val result =
     *   //sampleStart
     *   Validated.lift { s: CharSequence -> "$s World" }("Hello".valid())
     *   //sampleEnd
     *   println(result)
     * }
     * ```
     */
    @JvmStatic
    inline fun <E, A, B> lift(crossinline f: (A) -> B): (Validated<E, A>) -> Validated<E, B> =
      { fa -> fa.map(f) }

    /**
     * Lifts two functions to the Bifunctor type.
     *
     * ```kotlin:ank
     * import arrow.core.*
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val f = Validated.lift(String::toUpperCase, Int::inc)
     *   val res1 = f("test".invalid())
     *   val res2 = f(1.valid())
     *   //sampleEnd
     *   println("res1: $res1")
     *   println("res2: $res2")
     * }
     * ```
     */
    @JvmStatic
    inline fun <A, B, C, D> lift(
      crossinline fl: (A) -> C,
      crossinline fr: (B) -> D
    ): (Validated<A, B>) -> Validated<C, D> =
      { fa -> fa.bimap(fl, fr) }
  }

  /**
   * Discards the [A] value inside [Validated] signaling this container may be pointing to a noop
   * or an effect whose return value is deliberately ignored. The singleton value [Unit] serves as signal.
   *
   * ```kotlin:ank:playground
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello World".valid().void()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun void(): Validated<E, Unit> =
    map { Unit }

  inline fun <B> traverse(fa: (A) -> Iterable<B>): List<Validated<E, B>> =
    fold({ emptyList() }, { a -> fa(a).map { Valid(it) } })

  inline fun <EE, B> traverseEither(fa: (A) -> Either<EE, B>): Either<EE, Validated<E, B>> =
    when (this) {
      is Valid -> fa(this.value).map { Valid(it) }
      is Invalid -> this.right()
    }

  inline fun <B> bifoldLeft(
    c: B,
    fe: (B, E) -> B,
    fa: (B, A) -> B
  ): B =
    fold({ fe(c, it) }, { fa(c, it) })

  @Deprecated(FoldRightDeprecation)
  inline fun <B> bifoldRight(
    c: Eval<B>,
    fe: (E, Eval<B>) -> Eval<B>,
    fa: (A, Eval<B>) -> Eval<B>
  ): Eval<B> =
    fold({ fe(it, c) }, { fa(it, c) })

  inline fun <B> bifoldMap(MN: Monoid<B>, g: (E) -> B, f: (A) -> B) = MN.run {
    bifoldLeft(MN.empty(), { c, b -> c.combine(g(b)) }) { c, a -> c.combine(f(a)) }
  }

  inline fun <EE, B> bitraverse(fe: (E) -> Iterable<EE>, fa: (A) -> Iterable<B>): List<Validated<EE, B>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  inline fun <EE, B, C> bitraverseEither(
    fe: (E) -> Either<EE, B>,
    fa: (A) -> Either<EE, C>
  ): Either<EE, Validated<B, C>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  inline fun <B> foldMap(MB: Monoid<B>, f: (A) -> B): B =
    fold({ MB.empty() }, f)

  override fun toString(): String = fold(
    { "Validated.Invalid($it)" },
    { "Validated.Valid($it)" }
  )

  data class Valid<out A>(val value: A) : Validated<Nothing, A>() {
    override fun toString(): String = "Validated.Valid($value)"

    companion object {
      @PublishedApi
      internal val unit: Validated<Nothing, Unit> =
        Validated.Valid(Unit)
    }
  }

  data class Invalid<out E>(val value: E) : Validated<E, Nothing>() {
    override fun toString(): String = "Validated.Invalid($value)"
  }

  inline fun <B> fold(fe: (E) -> B, fa: (A) -> B): B =
    when (this) {
      is Valid -> fa(value)
      is Invalid -> (fe(value))
    }

  val isValid =
    fold({ false }, { true })
  val isInvalid =
    fold({ true }, { false })

  /**
   * Is this Valid and matching the given predicate
   */
  inline fun exist(predicate: (A) -> Boolean): Boolean =
    fold({ false }, predicate)

  inline fun findOrNull(predicate: (A) -> Boolean): A? =
    when (this) {
      is Valid -> if (predicate(this.value)) this.value else null
      is Invalid -> null
    }

  inline fun all(predicate: (A) -> Boolean): Boolean =
    fold({ true }, predicate)

  fun isEmpty(): Boolean = isInvalid

  fun isNotEmpty(): Boolean = isValid

  /**
   * Converts the value to an Either<E, A>
   */
  fun toEither(): Either<E, A> =
    fold(::Left, ::Right)

  /**
   * Returns Valid values wrapped in Some, and None for Invalid values
   */
  fun toOption(): Option<A> =
    fold({ None }, ::Some)

  /**
   * Convert this value to a single element List if it is Valid,
   * otherwise return an empty List
   */
  fun toList(): List<A> =
    fold({ listOf() }, ::listOf)

  /** Lift the Invalid value into a NonEmptyList. */
  fun toValidatedNel(): ValidatedNel<E, A> =
    fold({ invalidNel(it) }, ::Valid)

  /**
   * Convert to an Either, apply a function, convert back. This is handy
   * when you want to use the Monadic properties of the Either type.
   */
  inline fun <EE, B> withEither(f: (Either<E, A>) -> Either<EE, B>): Validated<EE, B> =
    fromEither(f(toEither()))

  /**
   * From [arrow.typeclasses.Bifunctor], maps both types of this Validated.
   *
   * Apply a function to an Invalid or Valid value, returning a new Invalid or Valid value respectively.
   */
  inline fun <EE, B> bimap(fe: (E) -> EE, fa: (A) -> B): Validated<EE, B> =
    fold({ Invalid(fe(it)) }, { Valid(fa(it)) })

  /**
   * Apply a function to a Valid value, returning a new Valid value
   */
  inline fun <B> map(f: (A) -> B): Validated<E, B> =
    bimap(::identity, f)

  /**
   * Apply a function to an Invalid value, returning a new Invalid value.
   * Or, if the original valid was Valid, return it.
   */
  inline fun <EE> mapLeft(f: (E) -> EE): Validated<EE, A> =
    bimap(f, ::identity)

  /**
   * apply the given function to the value with the given B when
   * valid, otherwise return the given B
   */
  inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    fold({ b }, { f(b, it) })

  @Deprecated(FoldRightDeprecation)
  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    when (this) {
      is Valid -> Eval.defer { f(this.value, lb) }
      is Invalid -> lb
    }

  fun swap(): Validated<A, E> =
    fold(::Valid, ::Invalid)
}

fun <E, A, B> Validated<E, A>.zip(SE: Semigroup<E>, fb: Validated<E, B>): Validated<E, Pair<A, B>> =
  zip(SE, fb, ::Pair)

inline fun <E, A, B, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  f: (A, B) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, _, _, _, _, _, _, _, _ ->
    f(a, b)
  }

inline fun <E, A, B, C, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  f: (A, B, C) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    c,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, _, _, _, _, _, _, _ ->
    f(a, b, c)
  }

inline fun <E, A, B, C, D, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  f: (A, B, C, D) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    c,
    d,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, d, _, _, _, _, _, _ ->
    f(a, b, c, d)
  }

inline fun <E, A, B, C, D, EE, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  f: (A, B, C, D, EE) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    c,
    d,
    e,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, d, e, _, _, _, _, _ ->
    f(a, b, c, d, e)
  }

inline fun <E, A, B, C, D, EE, FF, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, FF>,
  f: (A, B, C, D, EE, FF) -> Z
): Validated<E, Z> =
  zip(
    SE,
    b,
    c,
    d,
    e,
    ff,
    Valid.unit,
    Valid.unit,
    Valid.unit,
    Valid.unit
  ) { a, b, c, d, e, ff, _, _, _, _ ->
    f(a, b, c, d, e, ff)
  }

inline fun <E, A, B, C, D, EE, F, G, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  f: (A, B, C, D, EE, F, G) -> Z
): Validated<E, Z> =
  zip(SE, b, c, d, e, ff, g, Valid.unit, Valid.unit, Valid.unit) { a, b, c, d, e, ff, g, _, _, _ ->
    f(a, b, c, d, e, ff, g)
  }

inline fun <E, A, B, C, D, EE, F, G, H, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  h: Validated<E, H>,
  f: (A, B, C, D, EE, F, G, H) -> Z
): Validated<E, Z> =
  zip(SE, b, c, d, e, ff, g, h, Valid.unit, Valid.unit) { a, b, c, d, e, ff, g, h, _, _ ->
    f(a, b, c, d, e, ff, g, h)
  }

inline fun <E, A, B, C, D, EE, F, G, H, I, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  h: Validated<E, H>,
  i: Validated<E, I>,
  f: (A, B, C, D, EE, F, G, H, I) -> Z
): Validated<E, Z> =
  zip(SE, b, c, d, e, ff, g, h, i, Valid.unit) { a, b, c, d, e, ff, g, h, i, _ ->
    f(a, b, c, d, e, ff, g, h, i)
  }

inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> Validated<E, A>.zip(
  SE: Semigroup<E>,
  b: Validated<E, B>,
  c: Validated<E, C>,
  d: Validated<E, D>,
  e: Validated<E, EE>,
  ff: Validated<E, F>,
  g: Validated<E, G>,
  h: Validated<E, H>,
  i: Validated<E, I>,
  j: Validated<E, J>,
  f: (A, B, C, D, EE, F, G, H, I, J) -> Z
): Validated<E, Z> =
  if (this is Validated.Valid && b is Validated.Valid && c is Validated.Valid && d is Validated.Valid && e is Validated.Valid && ff is Validated.Valid && g is Validated.Valid && h is Validated.Valid && i is Validated.Valid && j is Validated.Valid) {
    Validated.Valid(f(this.value, b.value, c.value, d.value, e.value, ff.value, g.value, h.value, i.value, j.value))
  } else SE.run {
    var accumulatedError: Any? = EmptyValue
    accumulatedError =
      if (this@zip is Validated.Invalid) this@zip.value else accumulatedError
    accumulatedError =
      if (b is Validated.Invalid) emptyCombine(accumulatedError, b.value) else accumulatedError
    accumulatedError =
      if (c is Validated.Invalid) emptyCombine(accumulatedError, c.value) else accumulatedError
    accumulatedError =
      if (d is Validated.Invalid) emptyCombine(accumulatedError, d.value) else accumulatedError
    accumulatedError =
      if (e is Validated.Invalid) emptyCombine(accumulatedError, e.value) else accumulatedError
    accumulatedError =
      if (ff is Validated.Invalid) emptyCombine(accumulatedError, ff.value) else accumulatedError
    accumulatedError =
      if (g is Validated.Invalid) emptyCombine(accumulatedError, g.value)else accumulatedError
    accumulatedError =
      if (h is Validated.Invalid) emptyCombine(accumulatedError, h.value) else accumulatedError
    accumulatedError =
      if (i is Validated.Invalid) emptyCombine(accumulatedError, i.value) else accumulatedError
    accumulatedError =
      if (j is Validated.Invalid) emptyCombine(accumulatedError, j.value) else accumulatedError

    Validated.Invalid(accumulatedError as E)
  }

inline fun <E, A, B, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  f: (A, B) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, f)

inline fun <E, A, B, C, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  f: (A, B, C) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, f)

inline fun <E, A, B, C, D, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  f: (A, B, C, D) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, f)

inline fun <E, A, B, C, D, EE, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  f: (A, B, C, D, EE) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, f)

inline fun <E, A, B, C, D, EE, FF, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, FF>,
  f: (A, B, C, D, EE, FF) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, f)

inline fun <E, A, B, C, D, EE, F, G, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  f: (A, B, C, D, EE, F, G) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, f)

inline fun <E, A, B, C, D, EE, F, G, H, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  h: ValidatedNel<E, H>,
  f: (A, B, C, D, EE, F, G, H) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, h, f)

inline fun <E, A, B, C, D, EE, F, G, H, I, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  h: ValidatedNel<E, H>,
  i: ValidatedNel<E, I>,
  f: (A, B, C, D, EE, F, G, H, I) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, h, i, f)

inline fun <E, A, B, C, D, EE, F, G, H, I, J, Z> ValidatedNel<E, A>.zip(
  b: ValidatedNel<E, B>,
  c: ValidatedNel<E, C>,
  d: ValidatedNel<E, D>,
  e: ValidatedNel<E, EE>,
  ff: ValidatedNel<E, F>,
  g: ValidatedNel<E, G>,
  h: ValidatedNel<E, H>,
  i: ValidatedNel<E, I>,
  j: ValidatedNel<E, J>,
  f: (A, B, C, D, EE, F, G, H, I, J) -> Z
): ValidatedNel<E, Z> =
  zip(Semigroup.nonEmptyList(), b, c, d, e, ff, g, h, i, j, f)

/**
 * Given [A] is a sub type of [B], re-type this value from Validated<E, A> to Validated<E, B>
 *
 * ```kotlin:ank:playground:extension
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val string: Validated<Int, String> = "Hello".invalid()
 *   val chars: Validated<Int, CharSequence> =
 *     string.widen<Int, CharSequence, String>()
 *   //sampleEnd
 *   println(chars)
 * }
 * ```
 */
fun <E, B, A : B> Validated<E, A>.widen(): Validated<E, B> =
  this

fun <EE, E : EE, A> Validated<E, A>.leftWiden(): Validated<EE, A> =
  this

fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int): Validated<E, List<A>> =
  if (n <= 0) emptyList<A>().valid()
  else this.zip(SE, replicate(SE, n - 1)) { a, xs -> listOf(a) + xs }

fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int, MA: Monoid<A>): Validated<E, A> =
  if (n <= 0) MA.empty().valid()
  else this@replicate.zip(SE, replicate(SE, n - 1, MA)) { a, xs -> MA.run { a + xs } }

fun <E, A> Validated<Iterable<E>, Iterable<A>>.bisequence(): List<Validated<E, A>> =
  bitraverse(::identity, ::identity)

fun <E, A, B> Validated<Either<E, A>, Either<E, B>>.bisequenceEither(): Either<E, Validated<A, B>> =
  bitraverseEither(::identity, ::identity)

fun <E, A> Validated<E, A>.fold(MA: Monoid<A>): A = MA.run {
  foldLeft(empty()) { acc, a -> acc.combine(a) }
}

fun <E, A> Validated<E, A>.combineAll(MA: Monoid<A>): A =
  fold(MA)

fun <E, A> Validated<E, Iterable<A>>.sequence(): List<Validated<E, A>> =
  traverse(::identity)

fun <E, A, B> Validated<A, Either<E, B>>.sequenceEither(): Either<E, Validated<A, B>> =
  traverseEither(::identity)

operator fun <E : Comparable<E>, A : Comparable<A>> Validated<E, A>.compareTo(other: Validated<E, A>): Int =
  fold(
    { l1 -> other.fold({ l2 -> l1.compareTo(l2) }, { -1 }) },
    { r1 -> other.fold({ 1 }, { r2 -> r1.compareTo(r2) }) }
  )

/**
 * Return the Valid value, or the default if Invalid
 */
inline fun <E, A> Validated<E, A>.getOrElse(default: () -> A): A =
  fold({ default() }, ::identity)

/**
 * Return the Valid value, or null if Invalid
 */
fun <E, A> Validated<E, A>.orNull(): A? =
  getOrElse { null }

/**
 * Return the Valid value, or the result of f if Invalid
 */
inline fun <E, A> Validated<E, A>.valueOr(f: (E) -> A): A =
  fold({ f(it) }, ::identity)

/**
 * If `this` is valid return `this`, otherwise if `that` is valid return `that`, otherwise combine the failures.
 * This is similar to [orElse] except that here failures are accumulated.
 */
inline fun <E, A> Validated<E, A>.findValid(SE: Semigroup<E>, that: () -> Validated<E, A>): Validated<E, A> =
  fold(
    { e ->
      that().fold(
        { ee -> Invalid(SE.run { e.combine(ee) }) },
        { Valid(it) }
      )
    },
    { Valid(it) }
  )

/**
 * Return this if it is Valid, or else fall back to the given default.
 * The functionality is similar to that of [findValid] except for failure accumulation,
 * where here only the error on the right is preserved and the error on the left is ignored.
 */
inline fun <E, A> Validated<E, A>.orElse(default: () -> Validated<E, A>): Validated<E, A> =
  fold(
    { default() },
    { Valid(it) }
  )

inline fun <E, A> Validated<E, A>.handleErrorWith(f: (E) -> Validated<E, A>): Validated<E, A> =
  when (this) {
    is Validated.Valid -> this
    is Validated.Invalid -> f(this.value)
  }

inline fun <E, A> Validated<E, A>.handleError(f: (E) -> A): Validated<Nothing, A> =
  when (this) {
    is Validated.Valid -> this
    is Validated.Invalid -> Valid(f(this.value))
  }

inline fun <E, A, B> Validated<E, A>.redeem(fe: (E) -> B, fa: (A) -> B): Validated<E, B> =
  when (this) {
    is Validated.Valid -> map(fa)
    is Validated.Invalid -> Valid(fe(this.value))
  }

fun <E, A> Validated<E, A>.attempt(): Validated<Nothing, Either<E, A>> =
  map { Right(it) }.handleError { Left(it) }

fun <E, A> Validated<E, A>.combine(
  SE: Semigroup<E>,
  SA: Semigroup<A>,
  y: Validated<E, A>
): Validated<E, A> =
  when {
    this is Valid && y is Valid -> Valid(SA.run { value.combine(y.value) })
    this is Invalid && y is Invalid -> Invalid(SE.run { value.combine(y.value) })
    this is Invalid -> this
    else -> y
  }

fun <E, A> Validated<E, A>.combineK(SE: Semigroup<E>, y: Validated<E, A>): Validated<E, A> {
  return when (this) {
    is Valid -> this
    is Invalid -> when (y) {
      is Invalid -> Invalid(SE.run { this@combineK.value.combine(y.value) })
      is Valid -> y
    }
  }
}

/**
 * Converts the value to an Ior<E, A>
 */
fun <E, A> Validated<E, A>.toIor(): Ior<E, A> =
  fold({ Ior.Left(it) }, { Ior.Right(it) })

inline fun <A> A.valid(): Validated<Nothing, A> =
  Valid(this)

inline fun <E> E.invalid(): Validated<E, Nothing> =
  Invalid(this)

inline fun <A> A.validNel(): ValidatedNel<Nothing, A> =
  Validated.validNel(this)

inline fun <E> E.invalidNel(): ValidatedNel<E, Nothing> =
  Validated.invalidNel(this)
