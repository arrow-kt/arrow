package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

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
 * import arrow.core.Left
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
 * import arrow.core.None
 * import arrow.core.Option
 *
 * //sampleStart
 * abstract class Read<A> {
 *
 * abstract fun read(s: String): Option<A>
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object: Read<String>() {
 *     override fun read(s: String): Option<String> = Option(s)
 *    }
 *
 *   val intRead: Read<Int> =
 *    object: Read<Int>() {
 *     override fun read(s: String): Option<Int> =
 *      if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
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
 * @higherkind sealed class Validated<out E, out A> : ValidatedOf<E, A> {
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
 *   val v = Option.fromNullable(map[key])
 *   return when (v) {
 *    is Some ->
 *     when (val s = read.read(v.t)) {
 *      is Some -> s.t.valid()
 *      is None -> ConfigError.ParseConfig(key).invalid()
 *     }
 *    is None -> Validated.Invalid(ConfigError.MissingConfig(key))
 *   }
 *  }
 * }
 * //sampleEnd
 * ```
 *
 * And, as you can see, the parser runs sequentially: it first tries to get the map value and then tries to read it.
 * It's then straightforward to translate this to an Fx block:
 *
 * ```kotlin:ank
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.computations.validated
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * //sampleStart
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = validated<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }.bind()
 *     val readVal = Validated.fromOption(read.read(value)) {
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
 *   v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
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
 *   v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
 *   v1 is Validated.Valid && v2 is Validated.Invalid -> v2.toValidatedNel()
 *   v1 is Validated.Invalid && v2 is Validated.Valid -> v1.toValidatedNel()
 *   v1 is Validated.Invalid && v2 is Validated.Invalid -> Validated.Invalid(NonEmptyList(v1.e, listOf(v2.e)))
 *   else -> throw IllegalStateException("Not possible value")
 *  }
 * //sampleEnd
 * ```
 *
 * ### Improving the validation
 *
 * Kotlin says that our match is not exhaustive and we have to add `else`. To solve this, we would need to nest our when,
 * but that would complicate the code. To achieve this, Arrow provides an [arrow.typeclasses.Applicative] through the
 * `applicativeNel` function in arrow-core to unlock `tupledN`.
 * This function combines [Validated]s by accumulating errors in a tuple, which we can then map.
 * The above function can be rewritten as follows:
 *
 * ```kotlin:ank:silent
 * import arrow.core.Validated
 * import arrow.core.fix
 * import arrow.reflect.applicativeNel
 *
 * // added manually due to deps
 *
 * val v1: Validated<ConfigError, Int> = Validated.Valid(1)
 * val v2: Validated<ConfigError, Int> = Validated.Valid(2)
 *
 * //sampleStart
 * val parallelValidate = Validated.applicativeNel<ConfigError>()
 *     .tupledN(v1.toValidatedNel(), v2.toValidatedNel()).fix()
 *     .map { (a, b) -> /* combine the result */ }
 * //sampleEnd
 * ```
 *
 * Note that there are multiple `tupledN` functions with more arities, so we could easily add more parameters without worrying about
 * the function blowing up in complexity.
 *
 * ---
 *
 * Coming back to our example, when no errors are present in the configuration, we get a `ConnectionParams` wrapped in a `Valid` instance.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.computations.validated
 * import arrow.core.valid
 * import arrow.core.invalid
 * import arrow.core.NonEmptyList
 * import arrow.core.fix
 * import arrow.reflect.applicativeNel
 *
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): Option<A>
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): Option<String> = Option(s)
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Option<Int> =
 *      if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
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
 *   suspend fun <A> parse(read: Read<A>, key: String) = validated<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }.bind()
 *     val readVal = Validated.fromOption(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }.bind()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 * val parallelValidate = Validated.applicativeNel<ConfigError>()
 *
 * suspend fun main() {
 * //sampleStart
 *  val config = Config(mapOf("url" to "127.0.0.1", "port" to "1337"))
 *
 *  val valid = parallelValidate.tupledN(
 *  config.parse(Read.stringRead, "url"),
 *  config.parse(Read.intRead, "port")
 *  ).fix().map { (url, port) -> ConnectionParams(url, port) }
 * //sampleEnd
 *  println("valid = $valid")
 * }
 * ```
 *
 * But what happens when we have one or more errors? They are accumulated in a `NonEmptyList` wrapped in
 * an `Invalid` instance.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.computations.validated
 * import arrow.core.valid
 * import arrow.core.invalid
 * import arrow.core.NonEmptyList
 * import arrow.core.fix
 * import arrow.reflect.applicativeNel
 *
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): Option<A>
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): Option<String> = Option(s)
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Option<Int> =
 *      if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
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
 *   suspend fun <A> parse(read: Read<A>, key: String) = validated<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }.bind()
 *     val readVal = Validated.fromOption(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }.bind()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 * val parallelValidate = Validated.applicativeNel<ConfigError>()
 *
 * suspend fun main() {
 * //sampleStart
 * val config = Config(mapOf("wrong field" to "127.0.0.1", "port" to "not a number"))
 *
 * val valid = parallelValidate.tupledN(
 *  config.parse(Read.stringRead, "url"),
 *  config.parse(Read.intRead, "port")
 *  ).fix().map { (url, port) -> ConnectionParams(url, port) }
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
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.right
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.computations.validated
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): Option<A>
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): Option<String> = Option(s)
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Option<Int> =
 *      if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
 *    }
 *  }
 * }
 *
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = validated<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }.bind()
 *     val readVal = Validated.fromOption(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }.bind()
 *     readVal
 *   }
 * }
 *
 * sealed class ConfigError {
 *  data class MissingConfig(val field: String) : ConfigError()
 *  data class ParseConfig(val field: String) : ConfigError()
 * }
 *
 * //sampleStart
 * fun positive(field: String, i: Int): Either<ConfigError, Int> {
 *  return if (i >= 0) i.right()
 *  else ConfigError.ParseConfig(field).left()
 * }
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
 *
 * ## Alternative validation strategies to Validated: using `ApplicativeError`
 *
 * We may use `ApplicativeError` instead of `Validated` to abstract away validation strategies and raising errors in the context we are computing in.
 *
 * ```kotlin:ank
 * import arrow.Kind
 * import arrow.core.Either
 * import arrow.core.EitherPartialOf
 * import arrow.core.Nel
 * import arrow.core.NonEmptyList
 * import arrow.core.Validated
 * import arrow.core.ValidatedPartialOf
 * import arrow.core.nel
 * import arrow.typeclasses.ApplicativeError
 * import arrow.core.extensions.validated.applicativeError.applicativeError
 * import arrow.core.extensions.either.applicativeError.applicativeError
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 *
 * //sampleStart
 * sealed class ValidationError(val msg: String) {
 *  data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
 *  data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
 *  data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
 * }
 *
 * data class FormField(val label: String, val value: String)
 * data class Email(val value: String)
 *
 * sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {
 *
 *  private fun FormField.contains(needle: String): Kind<F, FormField> =
 *   if (value.contains(needle, false)) just(this)
 *   else raiseError(ValidationError.DoesNotContain(needle).nel())
 *
 *  private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
 *   if (value.length <= maxLength) just(this)
 *   else raiseError(ValidationError.MaxLength(maxLength).nel())
 *
 *  fun FormField.validateEmail(): Kind<F, Email> =
 *   map(contains("@"), maxLength(250), {
 *    Email(value)
 *   }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }
 *
 *  object ErrorAccumulationStrategy :
 *    Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))
 *
 *  object FailFastStrategy :
 *   Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())
 *
 *  companion object {
 *   infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
 *   infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
 *  }
 * }
 * //sampleEnd
 * ```
 *
 * `Rules` defines abstract behaviors that can be composed and have access to the scope of `ApplicativeError` where we can invoke `just` to lift values into the positive result and `raiseError` into the error context.
 *
 * Once we have such abstract algebra defined, we can simply materialize it to data types that support different error strategies:
 *
 *  *Error accumulation*
 *
 * ```kotlin:ank:playground
 * import arrow.Kind
 * import arrow.core.Either
 * import arrow.core.EitherPartialOf
 * import arrow.core.Nel
 * import arrow.core.NonEmptyList
 * import arrow.core.Validated
 * import arrow.core.ValidatedPartialOf
 * import arrow.core.nel
 * import arrow.typeclasses.ApplicativeError
 * import arrow.core.extensions.validated.applicativeError.applicativeError
 * import arrow.core.extensions.either.applicativeError.applicativeError
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 *
 * sealed class ValidationError(val msg: String) {
 *  data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
 *  data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
 *  data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
 * }
 *
 * data class FormField(val label: String, val value: String)
 * data class Email(val value: String)
 *
 * sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {
 *
 *  private fun FormField.contains(needle: String): Kind<F, FormField> =
 *   if (value.contains(needle, false)) just(this)
 *   else raiseError(ValidationError.DoesNotContain(needle).nel())
 *
 *  private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
 *   if (value.length <= maxLength) just(this)
 *   else raiseError(ValidationError.MaxLength(maxLength).nel())
 *
 *  fun FormField.validateEmail(): Kind<F, Email> =
 *   map(contains("@"), maxLength(250), {
 *    Email(value)
 *   }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }
 *
 *  object ErrorAccumulationStrategy :
 *    Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))
 *
 *  object FailFastStrategy :
 *   Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())
 *
 *  companion object {
 *   infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
 *   infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
 *  }
 * }
 *
 * val value =
 * //sampleStart
 *  Rules accumulateErrors {
 *    listOf(
 *      FormField("Invalid Email Domain Label", "nowhere.com"),
 *      FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this accumulates N errors
 *      FormField("Valid Email Label", "getlost@nowhere.com")
 *    ).map { it.validateEmail() }
 *    }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *  *Fail Fast*
 *
 * ```kotlin:ank:playground
 * import arrow.Kind
 * import arrow.core.Either
 * import arrow.core.EitherPartialOf
 * import arrow.core.Nel
 * import arrow.core.NonEmptyList
 * import arrow.core.Validated
 * import arrow.core.ValidatedPartialOf
 * import arrow.core.nel
 * import arrow.typeclasses.ApplicativeError
 * import arrow.core.extensions.validated.applicativeError.applicativeError
 * import arrow.core.extensions.either.applicativeError.applicativeError
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 *
 * sealed class ValidationError(val msg: String) {
 *  data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
 *  data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
 *  data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
 * }
 *
 * data class FormField(val label: String, val value: String)
 * data class Email(val value: String)
 *
 * sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {
 *
 *  private fun FormField.contains(needle: String): Kind<F, FormField> =
 *   if (value.contains(needle, false)) just(this)
 *   else raiseError(ValidationError.DoesNotContain(needle).nel())
 *
 *  private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
 *   if (value.length <= maxLength) just(this)
 *   else raiseError(ValidationError.MaxLength(maxLength).nel())
 *
 *  fun FormField.validateEmail(): Kind<F, Email> =
 *   map(contains("@"), maxLength(250), {
 *    Email(value)
 *   }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }
 *
 *  object ErrorAccumulationStrategy :
 *    Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))
 *
 *  object FailFastStrategy :
 *   Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())
 *
 *  companion object {
 *   infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
 *   infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
 *  }
 * }
 *
 * val value =
 * //sampleStart
 *  Rules failFast {
 *    listOf(
 *      FormField("Invalid Email Domain Label", "nowhere.com"),
 *      FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this fails fast
 *      FormField("Valid Email Label", "getlost@nowhere.com")
 *    ).map { it.validateEmail() }
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
 * import arrow.core.Validated
 *
 * DataType(Validated::class).tcMarkdownList()
 * ```
 */
@higherkind
sealed class Validated<out E, out A> : ValidatedOf<E, A> {

  companion object {

    fun <E, A> invalidNel(e: E): ValidatedNel<E, A> = Invalid(NonEmptyList(e, listOf()))

    fun <E, A> validNel(a: A): ValidatedNel<E, A> = Valid(a)

    /**
     * Converts an `Either<E, A>` to a `Validated<E, A>`.
     */
    fun <E, A> fromEither(e: Either<E, A>): Validated<E, A> = e.fold({ Invalid(it) }, { Valid(it) })

    /**
     * Converts an `Option<A>` to a `Validated<E, A>`, where the provided `ifNone` output value is returned as [Invalid]
     * when the specified `Option` is `None`.
     */
    inline fun <E, A> fromOption(o: Option<A>, ifNone: () -> E): Validated<E, A> =
      o.fold(
        { Invalid(ifNone()) },
        { Valid(it) }
      )

    /**
     * Converts a nullable `A?` to a `Validated<E, A>`, where the provided `ifNull` output value is returned as [Invalid]
     * when the specified value is null.
     */
    inline fun <E, A> fromNullable(value: A?, ifNull: () -> E): Validated<E, A> =
      value?.let(::Valid) ?: Invalid(ifNull())

    suspend fun <A> catch(f: suspend () -> A): Validated<Throwable, A> =
      try {
        f().valid()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalid()
      }

    suspend fun <A> catchNel(f: suspend () -> A): ValidatedNel<Throwable, A> =
      try {
        f().validNel()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalidNel()
      }
  }

  fun show(SE: Show<E>, SA: Show<A>): String = fold({
    "Invalid(${SE.run { it.show() }})"
  }, {
    "Valid(${SA.run { it.show() }})"
  })

  data class Valid<out A>(val a: A) : Validated<Nothing, A>() {
    override fun toString(): String = show(Show.any(), Show.any())
  }

  data class Invalid<out E>(val e: E) : Validated<E, Nothing>() {
    override fun toString(): String = show(Show.any(), Show.any())
  }

  inline fun <B> fold(fe: (E) -> B, fa: (A) -> B): B =
    when (this) {
      is Valid -> fa(a)
      is Invalid -> (fe(e))
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
  inline fun <EE, AA> bimap(fe: (E) -> EE, fa: (A) -> AA): Validated<EE, AA> =
    fold({ Invalid(fe(it)) }, { Valid(fa(it)) })

  /**
   * Apply a function to a Valid value, returning a new Valid value
   */
  inline fun <B> map(f: (A) -> B): Validated<E, B> =
    bimap(::identity, f)

  @Deprecated("Use mapLeft for consistency", ReplaceWith("mapLeft(f)"))
  inline fun <EE> leftMap(f: (E) -> EE): Validated<EE, A> =
    mapLeft(f)

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

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    when (this) {
      is Valid -> Eval.defer { f(this.a, lb) }
      is Invalid -> lb
    }

  fun swap(): Validated<A, E> =
    fold(::Valid, ::Invalid)
}

/**
 * Return the Valid value, or the default if Invalid
 */
inline fun <E, B> ValidatedOf<E, B>.getOrElse(default: () -> B): B =
  fix().fold({ default() }, ::identity)

/**
 * Return the Valid value, or null if Invalid
 */
fun <E, B> ValidatedOf<E, B>.orNull(): B? =
  getOrElse { null }

/**
 * Return the Valid value, or the result of f if Invalid
 */
inline fun <E, B> ValidatedOf<E, B>.valueOr(f: (E) -> B): B =
  fix().fold({ f(it) }, ::identity)

/**
 * If `this` is valid return `this`, otherwise if `that` is valid return `that`, otherwise combine the failures.
 * This is similar to [orElse] except that here failures are accumulated.
 */
inline fun <E, A> ValidatedOf<E, A>.findValid(SE: Semigroup<E>, that: () -> Validated<E, A>): Validated<E, A> =
  fix().fold(
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
inline fun <E, A> ValidatedOf<E, A>.orElse(default: () -> Validated<E, A>): Validated<E, A> =
  fix().fold(
    { default() },
    { Valid(it) }
  )

/**
 * From Apply:
 * if both the function and this value are Valid, apply the function
 */
inline fun <E, A, B> ValidatedOf<E, A>.ap(SE: Semigroup<E>, f: Validated<E, (A) -> B>): Validated<E, B> =
  fix().fold(
    { e -> f.fold({ Invalid(SE.run { e.combine(it) }) }, { Invalid(e) }) },
    { a -> f.fold(::Invalid) { Valid(it(a)) } }
  )

@Deprecated(
  "To keep API consistent with Either and Option please use `handleErrorWith` instead",
  ReplaceWith("handleErrorWith(f)")
)
inline fun <E, A> ValidatedOf<E, A>.handleLeftWith(f: (E) -> ValidatedOf<E, A>): Validated<E, A> =
  handleErrorWith(f)

inline fun <E, A> ValidatedOf<E, A>.handleErrorWith(f: (E) -> ValidatedOf<E, A>): Validated<E, A> =
  fix().fold({ f(it).fix() }, ::Valid)

inline fun <E, A> ValidatedOf<E, A>.handleError(f: (E) -> A): Validated<E, A> =
  fix().handleErrorWith { Valid(f(it)) }

fun <G, E, A, B> ValidatedOf<E, A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Validated<E, B>> = GA.run {
  fix().fold({ e -> just(Invalid(e)) }, { a -> f(a).map(::Valid) })
}

fun <G, E, A> ValidatedOf<E, Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Validated<E, A>> =
  fix().traverse(GA, ::identity)

fun <E, A> ValidatedOf<E, A>.combine(
  SE: Semigroup<E>,
  SA: Semigroup<A>,
  y: ValidatedOf<E, A>
): Validated<E, A> =
  y.fix().let { that ->
    when {
      this is Valid && that is Valid -> Valid(SA.run { a.combine(that.a) })
      this is Invalid && that is Invalid -> Invalid(SE.run { e.combine(that.e) })
      this is Invalid -> this
      else -> that
    }
  }

fun <E, A> ValidatedOf<E, A>.combineK(SE: Semigroup<E>, y: ValidatedOf<E, A>): Validated<E, A> {
  val xev = fix()
  val yev = y.fix()
  return when (xev) {
    is Valid -> xev
    is Invalid -> when (yev) {
      is Invalid -> Invalid(SE.run { xev.e.combine(yev.e) })
      is Valid -> yev
    }
  }
}

/**
 * Converts the value to an Ior<E, A>
 */
fun <E, A> ValidatedOf<E, A>.toIor(): Ior<E, A> =
  fix().fold({ Ior.Left(it) }, { Ior.Right(it) })

inline fun <A> A.valid(): Validated<Nothing, A> =
  Valid(this)

inline fun <E> E.invalid(): Validated<E, Nothing> =
  Invalid(this)

inline fun <A> A.validNel(): ValidatedNel<Nothing, A> =
  Validated.validNel(this)

inline fun <E> E.invalidNel(): ValidatedNel<E, Nothing> =
  Validated.invalidNel(this)
