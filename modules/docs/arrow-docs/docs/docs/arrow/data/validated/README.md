---
layout: docs
title: Validated
permalink: /docs/arrow/data/validated/
redirect_from:
  - /docs/datatypes/validated/
video: ONwoUsHBJHg
---

## Validated

{:.beginner}
beginner

Imagine you are filling out a web form to sign up for an account. You input your username and
password and submit. Response comes back saying your username can't have dashes in it,
so you make some changes and resubmit. Can't have special characters either. Change, resubmit.
Passwords need to have at least one capital letter. Change, resubmit. Password needs to have at least one number.

Or perhaps you're reading from a configuration file. One could imagine the configuration library
you're using returns a `Try`, or maybe a `Either`. Your parsing may look something like:

```kotlin:ank
import arrow.core.Either
import arrow.core.Left
import arrow.core.flatMap

//sampleStart
data class ConnectionParams(val url: String, val port: Int)

fun <A> config(key: String): Either<String, A> = Left(key)

config<String>("url").flatMap { url ->
 config<Int>("port").map { ConnectionParams(url, it) }
}
//sampleEnd
```

You run your program and it says key "url" not found, turns out the key was "endpoint". So
you change your code and re-run. Now it says the "port" key was not a well-formed integer.

It would be nice to have all of these errors reported simultaneously. That the username can't
have dashes can be validated separately from it not having special characters, as well as
from the password needing to have certain requirements. A misspelled (or missing) field in
a config can be validated separately from another field not being well-formed.

Enter `Validated`.

## Parallel Validation

Our goal is to report any and all errors across independent bits of data. For instance, when
we ask for several pieces of configuration, each configuration field can be validated separately
from one another. How then do we enforce that the data we are working with is independent?
We ask for both of them up front.

As our running example, we will look at config parsing. Our config will be represented by a
`Map<String, String>`. Parsing will be handled by a `Read` type class - we provide instances only
for `String` and `Int` for brevity.

```kotlin:ank
import arrow.core.None
import arrow.core.Option

//sampleStart
abstract class Read<A> {

abstract fun read(s: String): Option<A>

 companion object {

  val stringRead: Read<String> =
   object: Read<String>() {
    override fun read(s: String): Option<String> = Option(s)
   }

  val intRead: Read<Int> =
   object: Read<Int>() {
    override fun read(s: String): Option<Int> =
     if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
   }
 }
}
//sampleEnd
```

Then we enumerate our errors—when asking for a config value, one of two things can go wrong:
the field is missing, or it is not well-formed with regards to the expected type.

```kotlin:ank
sealed class ConfigError {
 data class MissingConfig(val field: String): ConfigError()
 data class ParseConfig(val field: String): ConfigError()
}
```

We need a data type that can represent either a successful value (a parsed configuration), or an error.
It would look like the following, which Arrow provides in `arrow.Validated`:

```kotlin
@higherkind sealed class Validated<out E, out A> : ValidatedOf<E, A> {
 data class Valid<out A>(val a: A) : Validated<Nothing, A>()
 data class Invalid<out E>(val e: E) : Validated<E, Nothing>()
}
```

Now we are ready to write our parser.

```kotlin:ank
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.valid
import arrow.core.invalid

//sampleStart
data class Config(val map: Map<String, String>) {
 fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> {
  val v = Option.fromNullable(map[key])
  return when (v) {
   is Some ->
    when (val s = read.read(v.t)) {
     is Some -> s.t.valid()
     is None -> ConfigError.ParseConfig(key).invalid()
    }
   is None -> Validated.Invalid(ConfigError.MissingConfig(key))
  }
 }
}
//sampleEnd
```

Everything is in place to write the parallel validator. Recall that we can only do parallel
validation if each piece is independent. How do we enforce the data is independent? By
asking for all of it up front. Let's start with two pieces of data.

```kotlin:ank
import arrow.core.Validated
//sampleStart
fun <E, A, B, C> parallelValidate(v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<E, C> =
 when {
  v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
  v1 is Validated.Valid && v2 is Validated.Invalid -> v2
  v1 is Validated.Invalid && v2 is Validated.Valid -> v1
  v1 is Validated.Invalid && v2 is Validated.Invalid -> TODO()
  else -> TODO()
 }
//sampleEnd
```

We've run into a problem. In the case where both have errors, We want to report both. we
don't have a way to combine ConfigErrors. But as clients, we can change our Validated
values where the error can be combined, say, a `List<ConfigError>`.We are going to use a
`NonEmptyList<ConfigError>`—the NonEmptyList statically guarantees we have at least one value,
which aligns with the fact that if we have an Invalid, then we most certainly have at least one error.
This technique is so common there is a convenient method on `Validated` called `toValidatedNel`
that turns any `Validated<E, A>` value to a `Validated<NonEmptyList<E>, A>`. Additionally, the
type alias `ValidatedNel<E, A>` is provided.

Time to parse.

```kotlin:ank
import arrow.core.NonEmptyList
import arrow.core.Validated
//sampleStart
fun <E, A, B, C> parallelValidate
  (v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<NonEmptyList<E>, C> =
 when {
  v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
  v1 is Validated.Valid && v2 is Validated.Invalid -> v2.toValidatedNel()
  v1 is Validated.Invalid && v2 is Validated.Valid -> v1.toValidatedNel()
  v1 is Validated.Invalid && v2 is Validated.Invalid -> Validated.Invalid(NonEmptyList(v1.e, listOf(v2.e)))
  else -> throw IllegalStateException("Not possible value")
 }
//sampleEnd
```

Kotlin says that our match is not exhaustive and we have to add `else`.

When no errors are present in the configuration, we get a `ConnectionParams` wrapped in a `Valid` instance.

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.valid
import arrow.core.invalid

data class ConnectionParams(val url: String, val port: Int)

abstract class Read<A> {
 abstract fun read(s: String): Option<A>

 companion object {

  val stringRead: Read<String> =
   object : Read<String>() {
    override fun read(s: String): Option<String> = Option(s)
   }

  val intRead: Read<Int> =
   object : Read<Int>() {
    override fun read(s: String): Option<Int> =
     if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
   }
 }
}

sealed class ConfigError {
 data class MissingConfig(val field: String) : ConfigError()
 data class ParseConfig(val field: String) : ConfigError()
}

data class Config(val map: Map<String, String>) {
 fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> {
  val v = Option.fromNullable(map[key])
  return when (v) {
   is Some ->
    when (val s = read.read(v.t)) {
     is Some -> s.t.valid()
     is None -> ConfigError.ParseConfig(key).invalid()
    }
   is None -> Validated.Invalid(ConfigError.MissingConfig(key))
  }
 }
}

fun <E, A, B, C> parallelValidate(v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<E, C> =
 when {
  v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
  v1 is Validated.Valid && v2 is Validated.Invalid -> v2
  v1 is Validated.Invalid && v2 is Validated.Valid -> v1
  v1 is Validated.Invalid && v2 is Validated.Invalid -> TODO()
  else -> TODO()
 }
//sampleStart
 val config = Config(mapOf("url" to "127.0.0.1", "port" to "1337"))

 val valid = parallelValidate(
 config.parse(Read.stringRead, "url"),
 config.parse(Read.intRead, "port")
 ) { url, port -> ConnectionParams(url, port) }
//sampleEnd
fun main() {
 println("valid = $valid")
}
```

But what happens when having one or more errors? They are accumulated in a `NonEmptyList` wrapped in
an `Invalid` instance.

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import arrow.core.valid
import arrow.core.invalid

data class ConnectionParams(val url: String, val port: Int)

abstract class Read<A> {
 abstract fun read(s: String): Option<A>

 companion object {

  val stringRead: Read<String> =
   object : Read<String>() {
    override fun read(s: String): Option<String> = Option(s)
   }

  val intRead: Read<Int> =
   object : Read<Int>() {
    override fun read(s: String): Option<Int> =
     if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
   }
 }
}

sealed class ConfigError {
 data class MissingConfig(val field: String) : ConfigError()
 data class ParseConfig(val field: String) : ConfigError()
}

data class Config(val map: Map<String, String>) {
 fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> {
  val v = Option.fromNullable(map[key])
  return when (v) {
   is Some ->
    when (val s = read.read(v.t)) {
     is Some -> s.t.valid()
     is None -> ConfigError.ParseConfig(key).invalid()
    }
   is None -> Validated.Invalid(ConfigError.MissingConfig(key))
  }
 }
}

fun <E, A, B, C> parallelValidate(v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<E, C> =
 when {
  v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
  v1 is Validated.Valid && v2 is Validated.Invalid -> v2
  v1 is Validated.Invalid && v2 is Validated.Valid -> v1
  v1 is Validated.Invalid && v2 is Validated.Invalid -> TODO()
  else -> TODO()
 }
//sampleStart
val config = Config(mapOf("url" to "127.0.0.1", "port" to "not a number"))

val valid = parallelValidate(
 config.parse(Read.stringRead, "url"),
 config.parse(Read.intRead, "port")
 ) { url, port -> ConnectionParams(url, port) }
 //sampleEnd
fun main() {
 println("valid = $valid")
}
```

## Sequential Validation

If you do want error accumulation but occasionally run into places where sequential validation is needed,
then Validated provides `withEither` method to allow you to temporarily turn a Validated
instance into an Either instance and apply it to a function.

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.None
import arrow.core.Option
import arrow.core.right
import arrow.core.Some
import arrow.core.Validated
import arrow.core.valid
import arrow.core.invalid

abstract class Read<A> {
 abstract fun read(s: String): Option<A>

 companion object {

  val stringRead: Read<String> =
   object : Read<String>() {
    override fun read(s: String): Option<String> = Option(s)
   }

  val intRead: Read<Int> =
   object : Read<Int>() {
    override fun read(s: String): Option<Int> =
     if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
   }
 }
}

data class Config(val map: Map<String, String>) {
 fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> {
  val v = Option.fromNullable(map[key])
  return when (v) {
   is Some ->
    when (val s = read.read(v.t)) {
     is Some -> s.t.valid()
     is None -> ConfigError.ParseConfig(key).invalid()
    }
   is None -> Validated.Invalid(ConfigError.MissingConfig(key))
  }
 }
}
sealed class ConfigError {
 data class MissingConfig(val field: String) : ConfigError()
 data class ParseConfig(val field: String) : ConfigError()
}

//sampleStart
fun positive(field: String, i: Int): Either<ConfigError, Int> {
 return if (i >= 0) i.right()
 else ConfigError.ParseConfig(field).left()
}

val config = Config(mapOf("house_number" to "-42"))

val houseNumber = config.parse(Read.intRead, "house_number").withEither { either ->
 either.flatMap { positive("house_number", it) }
}
//sampleEnd
fun main() {
 println(houseNumber)
}

```

## Alternative validation strategies to Validated: using `ApplicativeError`

We may use `ApplicativeError` instead of `Validated` to abstract away validation strategies and raising errors in the context we are computing in.

```kotlin:ank
import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedPartialOf
import arrow.core.nel
import arrow.typeclasses.ApplicativeError
import arrow.core.extensions.validated.applicativeError.applicativeError
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.extensions.nonemptylist.semigroup.semigroup

//sampleStart
sealed class ValidationError(val msg: String) {
 data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
 data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
 data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
}

data class FormField(val label: String, val value: String)
data class Email(val value: String)

sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {

 private fun FormField.contains(needle: String): Kind<F, FormField> =
  if (value.contains(needle, false)) just(this)
  else raiseError(ValidationError.DoesNotContain(needle).nel())

 private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
  if (value.length <= maxLength) just(this)
  else raiseError(ValidationError.MaxLength(maxLength).nel())

 fun FormField.validateEmail(): Kind<F, Email> =
  map(contains("@"), maxLength(250), {
   Email(value)
  }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }

 object ErrorAccumulationStrategy :
   Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))

 object FailFastStrategy :
  Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())

 companion object {
  infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
  infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
 }
}
//sampleEnd
```

`Rules` defines abstract behaviors that can be composed and have access to the scope of `ApplicativeError` where we can invoke `just` to lift values in to the positive result and `raiseError` into the error context.

Once we have such abstract algebra defined we can simply materialize it to data types that support different error strategies:

*Error accumulation*

```kotlin:ank:playground
import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedPartialOf
import arrow.core.nel
import arrow.typeclasses.ApplicativeError
import arrow.core.extensions.validated.applicativeError.applicativeError
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.extensions.nonemptylist.semigroup.semigroup

sealed class ValidationError(val msg: String) {
 data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
 data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
 data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
}

data class FormField(val label: String, val value: String)
data class Email(val value: String)

sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {

 private fun FormField.contains(needle: String): Kind<F, FormField> =
  if (value.contains(needle, false)) just(this)
  else raiseError(ValidationError.DoesNotContain(needle).nel())

 private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
  if (value.length <= maxLength) just(this)
  else raiseError(ValidationError.MaxLength(maxLength).nel())

 fun FormField.validateEmail(): Kind<F, Email> =
  map(contains("@"), maxLength(250), {
   Email(value)
  }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }

 object ErrorAccumulationStrategy :
   Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))

 object FailFastStrategy :
  Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())

 companion object {
  infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
  infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
 }
}

val value =
//sampleStart
 Rules accumulateErrors {
   listOf(
     FormField("Invalid Email Domain Label", "nowhere.com"),
     FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this accumulates N errors
     FormField("Valid Email Label", "getlost@nowhere.com")
   ).map { it.validateEmail() }
   }
//sampleEnd
fun main() {
 println(value)
}
```
*Fail Fast*

```kotlin:ank:playground
import arrow.Kind
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.ValidatedPartialOf
import arrow.core.nel
import arrow.typeclasses.ApplicativeError
import arrow.core.extensions.validated.applicativeError.applicativeError
import arrow.core.extensions.either.applicativeError.applicativeError
import arrow.core.extensions.nonemptylist.semigroup.semigroup

sealed class ValidationError(val msg: String) {
 data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
 data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
 data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
}

data class FormField(val label: String, val value: String)
data class Email(val value: String)

sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {

 private fun FormField.contains(needle: String): Kind<F, FormField> =
  if (value.contains(needle, false)) just(this)
  else raiseError(ValidationError.DoesNotContain(needle).nel())

 private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
  if (value.length <= maxLength) just(this)
  else raiseError(ValidationError.MaxLength(maxLength).nel())

 fun FormField.validateEmail(): Kind<F, Email> =
  map(contains("@"), maxLength(250), {
   Email(value)
  }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }

 object ErrorAccumulationStrategy :
   Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))

 object FailFastStrategy :
  Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())

 companion object {
  infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
  infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
 }
}

val value =
//sampleStart
 Rules failFast {
   listOf(
     FormField("Invalid Email Domain Label", "nowhere.com"),
     FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this fails fast
     FormField("Valid Email Label", "getlost@nowhere.com")
   ).map { it.validateEmail() }
 }
//sampleEnd
fun main() {
 println(value)
}
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.DataType
import arrow.reflect.tcMarkdownList
import arrow.core.Validated

DataType(Validated::class).tcMarkdownList()
```
