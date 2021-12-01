//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Validated](index.md)

# Validated

[common]\
sealed class [Validated](index.md)&lt;out [E](index.md), out [A](index.md)&gt;

Imagine you are filling out a web form to sign up for an account. You input your username and password, then submit. A response comes back saying your username can't have dashes in it, so you make some changes, then resubmit. You can't have special characters either. Change, resubmit. Password needs to have at least one capital letter. Change, resubmit. Password needs to have at least one number.

Or perhaps you're reading from a configuration file. One could imagine the configuration library you're using returns an Either. Your parsing may look something like:

import arrow.core.Either\
import arrow.core.Either.Left\
import arrow.core.flatMap\
\
//sampleStart\
data class ConnectionParams(val url: String, val port: Int)\
\
fun &lt;A&gt; config(key: String): Either&lt;String, A&gt; = Left(key)\
\
config&lt;String&gt;("url").flatMap { url -&gt;\
 config&lt;Int&gt;("port").map { ConnectionParams(url, it) }\
}\
//sampleEnd<!--- KNIT example-validated-01.kt -->

You run your program and it says key "url" not found. Turns out the key was "endpoint." So you change your code and re-run. Now it says the "port" key was not a well-formed integer.

It would be nice to have all of these errors reported simultaneously. The username's inability to have dashes can be validated separately from it not having special characters, as well as from the password needing to have certain requirements. A misspelled (or missing) field in a config can be validated separately from another field not being well-formed.

#  Enter Validated.

##  Parallel Validation

Our goal is to report any and all errors across independent bits of data. For instance, when we ask for several pieces of configuration, each configuration field can be validated separately from one another. How then do we ensure that the data we are working with is independent? We ask for both of them up front.

As our running example, we will look at config parsing. Our config will be represented by a Map&lt;String, String&gt;. Parsing will be handled by a Read type class - we provide instances only for String and Int for brevity.

//sampleStart\
abstract class Read&lt;A&gt; {\
\
abstract fun read(s: String): A?\
\
 companion object {\
\
  val stringRead: Read&lt;String&gt; =\
   object: Read&lt;String&gt;() {\
    override fun read(s: String): String? = s\
   }\
\
  val intRead: Read&lt;Int&gt; =\
   object: Read&lt;Int&gt;() {\
    override fun read(s: String): Int? =\
     if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null\
   }\
 }\
}\
//sampleEnd<!--- KNIT example-validated-02.kt -->

Then we enumerate our errors. When asking for a config value, one of two things can go wrong: The field is missing, or it is not well-formed with regards to the expected type.

sealed class ConfigError {\
 data class MissingConfig(val field: String): ConfigError()\
 data class ParseConfig(val field: String): ConfigError()\
}<!--- KNIT example-validated-03.kt -->

We need a data type that can represent either a successful value (a parsed configuration), or an error. It would look like the following, which Arrow provides in arrow.Validated:

sealed class Validated&lt;out E, out A&gt; {\
 data class Valid&lt;out A&gt;(val a: A) : Validated&lt;Nothing, A&gt;()\
 data class Invalid&lt;out E&gt;(val e: E) : Validated&lt;E, Nothing&gt;()\
}<!--- KNIT example-validated-04.kt -->

Now we are ready to write our parser.

import arrow.core.None\
import arrow.core.Option\
import arrow.core.Some\
import arrow.core.Validated\
import arrow.core.valid\
import arrow.core.invalid\
\
//sampleStart\
data class Config(val map: Map&lt;String, String&gt;) {\
 fun &lt;A&gt; parse(read: Read&lt;A&gt;, key: String): Validated&lt;ConfigError, A&gt; {\
  val v = map[key]\
  return when (v) {\
   null -&gt; Validated.Invalid(ConfigError.MissingConfig(key))\
   else -&gt;\
    when (val s = read.read(v)) {\
     null -&gt; ConfigError.ParseConfig(key).invalid()\
     else -&gt; s.valid()\
    }\
  }\
 }\
}\
//sampleEnd<!--- KNIT example-validated-05.kt -->

And, as you can see, the parser runs sequentially: it first tries to get the map value and then tries to read it. It's then straightforward to translate this to an effect block. We use here the either block which includes syntax to obtain A from values of Validated&lt;*, A&gt; through the arrow.core.computations.EitherEffect.invoke

import arrow.core.Validated\
import arrow.core.computations.either\
import arrow.core.valid\
import arrow.core.invalid\
\
//sampleStart\
data class Config(val map: Map&lt;String, String&gt;) {\
  suspend fun &lt;A&gt; parse(read: Read&lt;A&gt;, key: String) = either&lt;ConfigError, A&gt; {\
    val value = Validated.fromNullable(map[key]) {\
      ConfigError.MissingConfig(key)\
    }.bind()\
    val readVal = Validated.fromNullable(read.read(value)) {\
      ConfigError.ParseConfig(key)\
    }.bind()\
    readVal\
  }\
}\
//sampleEnd<!--- KNIT example-validated-06.kt -->

Everything is in place to write the parallel validator. Remember that we can only do parallel validation if each piece is independent. How do we ensure the data is independent? By asking for all of it up front. Let's start with two pieces of data.

import arrow.core.Validated\
//sampleStart\
fun &lt;E, A, B, C&gt; parallelValidate(v1: Validated&lt;E, A&gt;, v2: Validated&lt;E, B&gt;, f: (A, B) -&gt; C): Validated&lt;E, C&gt; {\
 return when {\
  v1 is Validated.Valid &amp;&amp; v2 is Validated.Valid -&gt; Validated.Valid(f(v1.value, v2.value))\
  v1 is Validated.Valid &amp;&amp; v2 is Validated.Invalid -&gt; v2\
  v1 is Validated.Invalid &amp;&amp; v2 is Validated.Valid -&gt; v1\
  v1 is Validated.Invalid &amp;&amp; v2 is Validated.Invalid -&gt; TODO()\
  else -&gt; TODO()\
 }\
}\
//sampleEnd<!--- KNIT example-validated-07.kt -->

We've run into a problem. In the case where both have errors, we want to report both. We don't have a way to combine ConfigErrors. But, as clients, we can change our Validated values where the error can be combined, say, a List&lt;ConfigError&gt;. We are going to use a NonEmptyList&lt;ConfigError&gt;—the NonEmptyList statically guarantees we have at least one value, which aligns with the fact that, if we have an Invalid, then we most certainly have at least one error. This technique is so common there is a convenient method on Validated called toValidatedNel that turns any Validated&lt;E, A&gt; value to a Validated&lt;NonEmptyList&lt;E&gt;, A&gt;. Additionally, the type alias ValidatedNel&lt;E, A&gt; is provided.

Time to validate:

import arrow.core.NonEmptyList\
import arrow.core.Validated\
//sampleStart\
fun &lt;E, A, B, C&gt; parallelValidate\
  (v1: Validated&lt;E, A&gt;, v2: Validated&lt;E, B&gt;, f: (A, B) -&gt; C): Validated&lt;NonEmptyList&lt;E&gt;, C&gt; =\
 when {\
  v1 is Validated.Valid &amp;&amp; v2 is Validated.Valid -&gt; Validated.Valid(f(v1.value, v2.value))\
  v1 is Validated.Valid &amp;&amp; v2 is Validated.Invalid -&gt; v2.toValidatedNel()\
  v1 is Validated.Invalid &amp;&amp; v2 is Validated.Valid -&gt; v1.toValidatedNel()\
  v1 is Validated.Invalid &amp;&amp; v2 is Validated.Invalid -&gt; Validated.Invalid(NonEmptyList(v1.value, listOf(v2.value)))\
  else -&gt; throw IllegalStateException("Not possible value")\
 }\
//sampleEnd<!--- KNIT example-validated-08.kt -->

###  Improving the validation

Kotlin says that our match is not exhaustive and we have to add else. To solve this, we would need to nest our when, but that would complicate the code. To achieve this, Arrow provides [zip](../zip.md). This function combines [Validated](index.md)s by accumulating errors in a tuple, which we can then map. The above function can be rewritten as follows:

import arrow.core.Validated\
import arrow.core.validNel\
import arrow.core.zip\
import arrow.typeclasses.Semigroup\
\
//sampleStart\
val parallelValidate =\
   1.validNel().zip(Semigroup.nonEmptyList&lt;ConfigError&gt;(), 2.validNel())\
    { a, b -&gt; /* combine the result */}\
//sampleEnd<!--- KNIT example-validated-09.kt -->

Note that there are multiple zip functions with more arities, so we could easily add more parameters without worrying about the function blowing up in complexity.

When working with NonEmptyList in the Invalid side, there is no need to supply Semigroup as shown in the example above.

import arrow.core.Validated\
import arrow.core.validNel\
import arrow.core.zip\
\
//sampleStart\
val parallelValidate =\
  1.validNel().zip(2.validNel())\
    { a, b -&gt; /* combine the result */}\
//sampleEnd<!--- KNIT example-validated-10.kt -->

Coming back to our example, when no errors are present in the configuration, we get a ConnectionParams wrapped in a Valid instance.

import arrow.core.Validated\
import arrow.core.computations.either\
import arrow.core.valid\
import arrow.core.invalid\
import arrow.core.NonEmptyList\
import arrow.typeclasses.Semigroup\
\
data class ConnectionParams(val url: String, val port: Int)\
\
abstract class Read&lt;A&gt; {\
 abstract fun read(s: String): A?\
\
 companion object {\
\
  val stringRead: Read&lt;String&gt; =\
   object : Read&lt;String&gt;() {\
    override fun read(s: String): String? = s\
   }\
\
  val intRead: Read&lt;Int&gt; =\
   object : Read&lt;Int&gt;() {\
    override fun read(s: String): Int? =\
     if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null\
   }\
 }\
}\
\
sealed class ConfigError {\
 data class MissingConfig(val field: String) : ConfigError()\
 data class ParseConfig(val field: String) : ConfigError()\
}\
\
data class Config(val map: Map&lt;String, String&gt;) {\
  suspend fun &lt;A&gt; parse(read: Read&lt;A&gt;, key: String) = either&lt;ConfigError, A&gt; {\
    val value = Validated.fromNullable(map[key]) {\
      ConfigError.MissingConfig(key)\
    }.bind()\
    val readVal = Validated.fromNullable(read.read(value)) {\
      ConfigError.ParseConfig(key)\
    }.bind()\
    readVal\
  }.toValidatedNel()\
}\
\
\
suspend fun main() {\
//sampleStart\
 val config = Config(mapOf("url" to "127.0.0.1", "port" to "1337"))\
\
 val valid = config.parse(Read.stringRead, "url").zip(\
   Semigroup.nonEmptyList&lt;ConfigError&gt;(),\
   config.parse(Read.intRead, "port")\
 ) { url, port -&gt; ConnectionParams(url, port) }\
//sampleEnd\
 println("valid = $valid")\
}<!--- KNIT example-validated-11.kt -->

But what happens when we have one or more errors? They are accumulated in a NonEmptyList wrapped in an Invalid instance.

import arrow.core.Validated\
import arrow.core.computations.either\
import arrow.core.valid\
import arrow.core.invalid\
import arrow.core.NonEmptyList\
import arrow.typeclasses.Semigroup\
\
data class ConnectionParams(val url: String, val port: Int)\
\
abstract class Read&lt;A&gt; {\
 abstract fun read(s: String): A?\
\
 companion object {\
\
  val stringRead: Read&lt;String&gt; =\
   object : Read&lt;String&gt;() {\
    override fun read(s: String): String? = s\
   }\
\
  val intRead: Read&lt;Int&gt; =\
   object : Read&lt;Int&gt;() {\
    override fun read(s: String): Int? =\
     if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null\
   }\
 }\
}\
\
sealed class ConfigError {\
 data class MissingConfig(val field: String) : ConfigError()\
 data class ParseConfig(val field: String) : ConfigError()\
}\
\
data class Config(val map: Map&lt;String, String&gt;) {\
  suspend fun &lt;A&gt; parse(read: Read&lt;A&gt;, key: String) = either&lt;ConfigError, A&gt; {\
    val value = Validated.fromNullable(map[key]) {\
      ConfigError.MissingConfig(key)\
    }.bind()\
    val readVal = Validated.fromNullable(read.read(value)) {\
      ConfigError.ParseConfig(key)\
    }.bind()\
    readVal\
  }.toValidatedNel()\
}\
\
suspend fun main() {\
//sampleStart\
val config = Config(mapOf("wrong field" to "127.0.0.1", "port" to "not a number"))\
\
val valid = config.parse(Read.stringRead, "url").zip(\
 Semigroup.nonEmptyList&lt;ConfigError&gt;(),\
 config.parse(Read.intRead, "port")\
) { url, port -&gt; ConnectionParams(url, port) }\
//sampleEnd\
 println("valid = $valid")\
}<!--- KNIT example-validated-12.kt -->

##  Sequential Validation

If you do want error accumulation, but occasionally run into places where sequential validation is needed, then Validated provides a couple of methods that can be used.

### andThen

The andThen method is similar to flatMap. In case of a valid instance it will pass the valid value into the supplied function that in turn returns a Validated instance

import arrow.core.Validated\
import arrow.core.computations.either\
import arrow.core.valid\
import arrow.core.invalid\
\
abstract class Read&lt;A&gt; {\
 abstract fun read(s: String): A?\
\
 companion object {\
\
  val stringRead: Read&lt;String&gt; =\
   object : Read&lt;String&gt;() {\
    override fun read(s: String): String? = s\
   }\
\
  val intRead: Read&lt;Int&gt; =\
   object : Read&lt;Int&gt;() {\
    override fun read(s: String): Int? =\
     if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null\
   }\
 }\
}\
\
data class Config(val map: Map&lt;String, String&gt;) {\
  suspend fun &lt;A&gt; parse(read: Read&lt;A&gt;, key: String) = either&lt;ConfigError, A&gt; {\
    val value = Validated.fromNullable(map[key]) {\
      ConfigError.MissingConfig(key)\
    }.bind()\
    val readVal = Validated.fromNullable(read.read(value)) {\
      ConfigError.ParseConfig(key)\
    }.bind()\
    readVal\
  }.toValidatedNel()\
}\
\
sealed class ConfigError {\
 data class MissingConfig(val field: String) : ConfigError()\
 data class ParseConfig(val field: String) : ConfigError()\
}\
\
//sampleStart\
val config = Config(mapOf("house_number" to "-42"))\
\
suspend fun main() {\
  val houseNumber = config.parse(Read.intRead, "house_number").andThen { number -&gt;\
    if (number &gt;= 0) Validated.Valid(number)\
    else Validated.Invalid(ConfigError.ParseConfig("house_number"))\
}\
//sampleEnd\
 println(houseNumber)\
}<!--- KNIT example-validated-13.kt -->

### withEither

The withEither method to allow you to temporarily turn a Validated instance into an Either instance and apply it to a function.

import arrow.core.Either\
import arrow.core.flatMap\
import arrow.core.left\
import arrow.core.right\
import arrow.core.Validated\
import arrow.core.computations.either\
import arrow.core.valid\
import arrow.core.invalid\
\
abstract class Read&lt;A&gt; {\
 abstract fun read(s: String): A?\
\
 companion object {\
\
  val stringRead: Read&lt;String&gt; =\
   object : Read&lt;String&gt;() {\
    override fun read(s: String): String? = s\
   }\
\
  val intRead: Read&lt;Int&gt; =\
   object : Read&lt;Int&gt;() {\
    override fun read(s: String): Int? =\
     if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null\
   }\
 }\
}\
\
data class Config(val map: Map&lt;String, String&gt;) {\
  suspend fun &lt;A&gt; parse(read: Read&lt;A&gt;, key: String) = either&lt;ConfigError, A&gt; {\
    val value = Validated.fromNullable(map[key]) {\
      ConfigError.MissingConfig(key)\
    }.bind()\
    val readVal = Validated.fromNullable(read.read(value)) {\
      ConfigError.ParseConfig(key)\
    }.bind()\
    readVal\
  }.toValidatedNel()\
}\
\
sealed class ConfigError {\
 data class MissingConfig(val field: String) : ConfigError()\
 data class ParseConfig(val field: String) : ConfigError()\
}\
\
//sampleStart\
fun positive(field: String, i: Int): Either&lt;ConfigError, Int&gt; =\
 if (i &gt;= 0) i.right()\
 else ConfigError.ParseConfig(field).left()\
\
val config = Config(mapOf("house_number" to "-42"))\
\
suspend fun main() {\
  val houseNumber = config.parse(Read.intRead, "house_number").withEither { either -&gt;\
    either.flatMap { positive("house_number", it) }\
  }\
//sampleEnd\
 println(houseNumber)\
}<!--- KNIT example-validated-14.kt -->

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
| [Invalid](-invalid/index.md) | [common]<br>data class [Invalid](-invalid/index.md)&lt;out [E](-invalid/index.md)&gt;(value: [E](-invalid/index.md)) : [Validated](index.md)&lt;[E](-invalid/index.md), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)&gt; |
| [Valid](-valid/index.md) | [common]<br>data class [Valid](-valid/index.md)&lt;out [A](-valid/index.md)&gt;(value: [A](-valid/index.md)) : [Validated](index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [A](-valid/index.md)&gt; |

## Functions

| Name | Summary |
|---|---|
| [all](all.md) | [common]<br>inline fun [all](all.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [bifoldLeft](bifold-left.md) | [common]<br>inline fun &lt;[B](bifold-left.md)&gt; [bifoldLeft](bifold-left.md)(c: [B](bifold-left.md), fe: ([B](bifold-left.md), [E](index.md)) -&gt; [B](bifold-left.md), fa: ([B](bifold-left.md), [A](index.md)) -&gt; [B](bifold-left.md)): [B](bifold-left.md) |
| [bifoldMap](bifold-map.md) | [common]<br>inline fun &lt;[B](bifold-map.md)&gt; [bifoldMap](bifold-map.md)(MN: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[B](bifold-map.md)&gt;, g: ([E](index.md)) -&gt; [B](bifold-map.md), f: ([A](index.md)) -&gt; [B](bifold-map.md)): [B](bifold-map.md) |
| [bimap](bimap.md) | [common]<br>inline fun &lt;[EE](bimap.md), [B](bimap.md)&gt; [bimap](bimap.md)(fe: ([E](index.md)) -&gt; [EE](bimap.md), fa: ([A](index.md)) -&gt; [B](bimap.md)): [Validated](index.md)&lt;[EE](bimap.md), [B](bimap.md)&gt;<br>From arrow.typeclasses.Bifunctor, maps both types of this Validated. |
| [bitraverse](bitraverse.md) | [common]<br>inline fun &lt;[EE](bitraverse.md), [B](bitraverse.md)&gt; [bitraverse](bitraverse.md)(fe: ([E](index.md)) -&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[EE](bitraverse.md)&gt;, fa: ([A](index.md)) -&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](bitraverse.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Validated](index.md)&lt;[EE](bitraverse.md), [B](bitraverse.md)&gt;&gt; |
| [bitraverseEither](bitraverse-either.md) | [common]<br>inline fun &lt;[EE](bitraverse-either.md), [B](bitraverse-either.md), [C](bitraverse-either.md)&gt; [bitraverseEither](bitraverse-either.md)(fe: ([E](index.md)) -&gt; [Either](../-either/index.md)&lt;[EE](bitraverse-either.md), [B](bitraverse-either.md)&gt;, fa: ([A](index.md)) -&gt; [Either](../-either/index.md)&lt;[EE](bitraverse-either.md), [C](bitraverse-either.md)&gt;): [Either](../-either/index.md)&lt;[EE](bitraverse-either.md), [Validated](index.md)&lt;[B](bitraverse-either.md), [C](bitraverse-either.md)&gt;&gt; |
| [bitraverseNullable](bitraverse-nullable.md) | [common]<br>inline fun &lt;[B](bitraverse-nullable.md), [C](bitraverse-nullable.md)&gt; [bitraverseNullable](bitraverse-nullable.md)(fe: ([E](index.md)) -&gt; [B](bitraverse-nullable.md)?, fa: ([A](index.md)) -&gt; [C](bitraverse-nullable.md)?): [Validated](index.md)&lt;[B](bitraverse-nullable.md), [C](bitraverse-nullable.md)&gt;? |
| [bitraverseOption](bitraverse-option.md) | [common]<br>inline fun &lt;[B](bitraverse-option.md), [C](bitraverse-option.md)&gt; [bitraverseOption](bitraverse-option.md)(fe: ([E](index.md)) -&gt; [Option](../-option/index.md)&lt;[B](bitraverse-option.md)&gt;, fa: ([A](index.md)) -&gt; [Option](../-option/index.md)&lt;[C](bitraverse-option.md)&gt;): [Option](../-option/index.md)&lt;[Validated](index.md)&lt;[B](bitraverse-option.md), [C](bitraverse-option.md)&gt;&gt; |
| [exist](exist.md) | [common]<br>inline fun [exist](exist.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is this Valid and matching the given predicate |
| [findOrNull](find-or-null.md) | [common]<br>inline fun [findOrNull](find-or-null.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [A](index.md)? |
| [fold](fold.md) | [common]<br>inline fun &lt;[B](fold.md)&gt; [fold](fold.md)(fe: ([E](index.md)) -&gt; [B](fold.md), fa: ([A](index.md)) -&gt; [B](fold.md)): [B](fold.md) |
| [foldLeft](fold-left.md) | [common]<br>inline fun &lt;[B](fold-left.md)&gt; [foldLeft](fold-left.md)(b: [B](fold-left.md), f: ([B](fold-left.md), [A](index.md)) -&gt; [B](fold-left.md)): [B](fold-left.md)<br>apply the given function to the value with the given B when valid, otherwise return the given B |
| [foldMap](fold-map.md) | [common]<br>inline fun &lt;[B](fold-map.md)&gt; [foldMap](fold-map.md)(MB: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[B](fold-map.md)&gt;, f: ([A](index.md)) -&gt; [B](fold-map.md)): [B](fold-map.md) |
| [isEmpty](is-empty.md) | [common]<br>fun [isEmpty](is-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isNotEmpty](is-not-empty.md) | [common]<br>fun [isNotEmpty](is-not-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [map](map.md) | [common]<br>inline fun &lt;[B](map.md)&gt; [map](map.md)(f: ([A](index.md)) -&gt; [B](map.md)): [Validated](index.md)&lt;[E](index.md), [B](map.md)&gt;<br>Apply a function to a Valid value, returning a new Valid value |
| [mapLeft](map-left.md) | [common]<br>inline fun &lt;[EE](map-left.md)&gt; [mapLeft](map-left.md)(f: ([E](index.md)) -&gt; [EE](map-left.md)): [Validated](index.md)&lt;[EE](map-left.md), [A](index.md)&gt;<br>Apply a function to an Invalid value, returning a new Invalid value. Or, if the original valid was Valid, return it. |
| [swap](swap.md) | [common]<br>fun [swap](swap.md)(): [Validated](index.md)&lt;[A](index.md), [E](index.md)&gt; |
| [tap](tap.md) | [common]<br>inline fun [tap](tap.md)(f: ([A](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Validated](index.md)&lt;[E](index.md), [A](index.md)&gt;<br>The given function is applied as a fire and forget effect if this is Valid. When applied the result is ignored and the original Validated value is returned |
| [tapInvalid](tap-invalid.md) | [common]<br>inline fun [tapInvalid](tap-invalid.md)(f: ([E](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Validated](index.md)&lt;[E](index.md), [A](index.md)&gt;<br>The given function is applied as a fire and forget effect if this is Invalid. When applied the result is ignored and the original Validated value is returned |
| [toEither](to-either.md) | [common]<br>fun [toEither](to-either.md)(): [Either](../-either/index.md)&lt;[E](index.md), [A](index.md)&gt;<br>Converts the value to an Either |
| [toList](to-list.md) | [common]<br>fun [toList](to-list.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt;<br>Convert this value to a single element List if it is Valid, otherwise return an empty List |
| [toOption](to-option.md) | [common]<br>fun [toOption](to-option.md)(): [Option](../-option/index.md)&lt;[A](index.md)&gt;<br>Returns Valid values wrapped in Some, and None for Invalid values |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [toValidatedNel](to-validated-nel.md) | [common]<br>fun [toValidatedNel](to-validated-nel.md)(): [ValidatedNel](../index.md#682410975%2FClasslikes%2F-1961959459)&lt;[E](index.md), [A](index.md)&gt;<br>Lift the Invalid value into a NonEmptyList. |
| [traverse](traverse.md) | [common]<br>inline fun &lt;[B](traverse.md)&gt; [traverse](traverse.md)(fa: ([A](index.md)) -&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](traverse.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Validated](index.md)&lt;[E](index.md), [B](traverse.md)&gt;&gt; |
| [traverseEither](traverse-either.md) | [common]<br>inline fun &lt;[EE](traverse-either.md), [B](traverse-either.md)&gt; [traverseEither](traverse-either.md)(fa: ([A](index.md)) -&gt; [Either](../-either/index.md)&lt;[EE](traverse-either.md), [B](traverse-either.md)&gt;): [Either](../-either/index.md)&lt;[EE](traverse-either.md), [Validated](index.md)&lt;[E](index.md), [B](traverse-either.md)&gt;&gt; |
| [traverseNullable](traverse-nullable.md) | [common]<br>inline fun &lt;[B](traverse-nullable.md)&gt; [traverseNullable](traverse-nullable.md)(fa: ([A](index.md)) -&gt; [B](traverse-nullable.md)?): [Validated](index.md)&lt;[E](index.md), [B](traverse-nullable.md)&gt;? |
| [traverseOption](traverse-option.md) | [common]<br>inline fun &lt;[B](traverse-option.md)&gt; [traverseOption](traverse-option.md)(fa: ([A](index.md)) -&gt; [Option](../-option/index.md)&lt;[B](traverse-option.md)&gt;): [Option](../-option/index.md)&lt;[Validated](index.md)&lt;[E](index.md), [B](traverse-option.md)&gt;&gt; |
| [void](void.md) | [common]<br>fun [void](void.md)(): [Validated](index.md)&lt;[E](index.md), [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt;<br>Discards the [A](index.md) value inside [Validated](index.md) signaling this container may be pointing to a noop or an effect whose return value is deliberately ignored. The singleton value [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) serves as signal. |
| [withEither](with-either.md) | [common]<br>inline fun &lt;[EE](with-either.md), [B](with-either.md)&gt; [withEither](with-either.md)(f: ([Either](../-either/index.md)&lt;[E](index.md), [A](index.md)&gt;) -&gt; [Either](../-either/index.md)&lt;[EE](with-either.md), [B](with-either.md)&gt;): [Validated](index.md)&lt;[EE](with-either.md), [B](with-either.md)&gt;<br>Convert to an Either, apply a function, convert back. This is handy when you want to use the Monadic properties of the Either type. |

## Properties

| Name | Summary |
|---|---|
| [isInvalid](is-invalid.md) | [common]<br>val [isInvalid](is-invalid.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [isValid](is-valid.md) | [common]<br>val [isValid](is-valid.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

## Inheritors

| Name |
|---|
| [Validated](-valid/index.md) |
| [Validated](-invalid/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [andThen](../and-then.md) | [common]<br>inline fun &lt;[E](../and-then.md), [A](../and-then.md), [B](../and-then.md)&gt; [Validated](index.md)&lt;[E](../and-then.md), [A](../and-then.md)&gt;.[andThen](../and-then.md)(f: ([A](../and-then.md)) -&gt; [Validated](index.md)&lt;[E](../and-then.md), [B](../and-then.md)&gt;): [Validated](index.md)&lt;[E](../and-then.md), [B](../and-then.md)&gt;<br>Apply a function to a Valid value, returning a new Validation that may be valid or invalid |
| [attempt](../attempt.md) | [common]<br>fun &lt;[E](../attempt.md), [A](../attempt.md)&gt; [Validated](index.md)&lt;[E](../attempt.md), [A](../attempt.md)&gt;.[attempt](../attempt.md)(): [Validated](index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [Either](../-either/index.md)&lt;[E](../attempt.md), [A](../attempt.md)&gt;&gt; |
| [bind](../../arrow.core.computations/-result-effect/bind.md) | [common]<br>fun &lt;[A](../../arrow.core.computations/-result-effect/bind.md)&gt; [Validated](index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [A](../../arrow.core.computations/-result-effect/bind.md)&gt;.[bind](../../arrow.core.computations/-result-effect/bind.md)(): [A](../../arrow.core.computations/-result-effect/bind.md) |
| [bisequence](../bisequence.md) | [common]<br>fun &lt;[E](../bisequence.md), [A](../bisequence.md)&gt; [Validated](index.md)&lt;[Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[E](../bisequence.md)&gt;, [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](../bisequence.md)&gt;&gt;.[bisequence](../bisequence.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Validated](index.md)&lt;[E](../bisequence.md), [A](../bisequence.md)&gt;&gt; |
| [bisequenceEither](../bisequence-either.md) | [common]<br>fun &lt;[E](../bisequence-either.md), [A](../bisequence-either.md), [B](../bisequence-either.md)&gt; [Validated](index.md)&lt;[Either](../-either/index.md)&lt;[E](../bisequence-either.md), [A](../bisequence-either.md)&gt;, [Either](../-either/index.md)&lt;[E](../bisequence-either.md), [B](../bisequence-either.md)&gt;&gt;.[bisequenceEither](../bisequence-either.md)(): [Either](../-either/index.md)&lt;[E](../bisequence-either.md), [Validated](index.md)&lt;[A](../bisequence-either.md), [B](../bisequence-either.md)&gt;&gt; |
| [bisequenceNullable](../bisequence-nullable.md) | [common]<br>fun &lt;[A](../bisequence-nullable.md), [B](../bisequence-nullable.md)&gt; [Validated](index.md)&lt;[A](../bisequence-nullable.md)?, [B](../bisequence-nullable.md)?&gt;.[bisequenceNullable](../bisequence-nullable.md)(): [Validated](index.md)&lt;[A](../bisequence-nullable.md), [B](../bisequence-nullable.md)&gt;? |
| [bisequenceOption](../bisequence-option.md) | [common]<br>fun &lt;[A](../bisequence-option.md), [B](../bisequence-option.md)&gt; [Validated](index.md)&lt;[Option](../-option/index.md)&lt;[A](../bisequence-option.md)&gt;, [Option](../-option/index.md)&lt;[B](../bisequence-option.md)&gt;&gt;.[bisequenceOption](../bisequence-option.md)(): [Option](../-option/index.md)&lt;[Validated](index.md)&lt;[A](../bisequence-option.md), [B](../bisequence-option.md)&gt;&gt; |
| [combine](../combine.md) | [common]<br>fun &lt;[E](../combine.md), [A](../combine.md)&gt; [Validated](index.md)&lt;[E](../combine.md), [A](../combine.md)&gt;.[combine](../combine.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../combine.md)&gt;, SA: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[A](../combine.md)&gt;, y: [Validated](index.md)&lt;[E](../combine.md), [A](../combine.md)&gt;): [Validated](index.md)&lt;[E](../combine.md), [A](../combine.md)&gt; |
| [combineAll](../combine-all.md) | [common]<br>fun &lt;[E](../combine-all.md), [A](../combine-all.md)&gt; [Validated](index.md)&lt;[E](../combine-all.md), [A](../combine-all.md)&gt;.[combineAll](../combine-all.md)(MA: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[A](../combine-all.md)&gt;): [A](../combine-all.md) |
| [combineK](../combine-k.md) | [common]<br>fun &lt;[E](../combine-k.md), [A](../combine-k.md)&gt; [Validated](index.md)&lt;[E](../combine-k.md), [A](../combine-k.md)&gt;.[combineK](../combine-k.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../combine-k.md)&gt;, y: [Validated](index.md)&lt;[E](../combine-k.md), [A](../combine-k.md)&gt;): [Validated](index.md)&lt;[E](../combine-k.md), [A](../combine-k.md)&gt; |
| [compareTo](../compare-to.md) | [common]<br>operator fun &lt;[E](../compare-to.md) : [Comparable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)&lt;[E](../compare-to.md)&gt;, [A](../compare-to.md) : [Comparable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)&lt;[A](../compare-to.md)&gt;&gt; [Validated](index.md)&lt;[E](../compare-to.md), [A](../compare-to.md)&gt;.[compareTo](../compare-to.md)(other: [Validated](index.md)&lt;[E](../compare-to.md), [A](../compare-to.md)&gt;): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [findValid](../find-valid.md) | [common]<br>inline fun &lt;[E](../find-valid.md), [A](../find-valid.md)&gt; [Validated](index.md)&lt;[E](../find-valid.md), [A](../find-valid.md)&gt;.[findValid](../find-valid.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../find-valid.md)&gt;, that: () -&gt; [Validated](index.md)&lt;[E](../find-valid.md), [A](../find-valid.md)&gt;): [Validated](index.md)&lt;[E](../find-valid.md), [A](../find-valid.md)&gt;<br>If this is valid return this, otherwise if that is valid return that, otherwise combine the failures. This is similar to [orElse](../or-else.md) except that here failures are accumulated. |
| [fold](../fold.md) | [common]<br>fun &lt;[E](../fold.md), [A](../fold.md)&gt; [Validated](index.md)&lt;[E](../fold.md), [A](../fold.md)&gt;.[fold](../fold.md)(MA: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[A](../fold.md)&gt;): [A](../fold.md) |
| [getOrElse](../get-or-else.md) | [common]<br>inline fun &lt;[E](../get-or-else.md), [A](../get-or-else.md)&gt; [Validated](index.md)&lt;[E](../get-or-else.md), [A](../get-or-else.md)&gt;.[getOrElse](../get-or-else.md)(default: () -&gt; [A](../get-or-else.md)): [A](../get-or-else.md)<br>Return the Valid value, or the default if Invalid |
| [handleError](../handle-error.md) | [common]<br>inline fun &lt;[E](../handle-error.md), [A](../handle-error.md)&gt; [Validated](index.md)&lt;[E](../handle-error.md), [A](../handle-error.md)&gt;.[handleError](../handle-error.md)(f: ([E](../handle-error.md)) -&gt; [A](../handle-error.md)): [Validated](index.md)&lt;[Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html), [A](../handle-error.md)&gt; |
| [handleErrorWith](../handle-error-with.md) | [common]<br>inline fun &lt;[E](../handle-error-with.md), [A](../handle-error-with.md)&gt; [Validated](index.md)&lt;[E](../handle-error-with.md), [A](../handle-error-with.md)&gt;.[handleErrorWith](../handle-error-with.md)(f: ([E](../handle-error-with.md)) -&gt; [Validated](index.md)&lt;[E](../handle-error-with.md), [A](../handle-error-with.md)&gt;): [Validated](index.md)&lt;[E](../handle-error-with.md), [A](../handle-error-with.md)&gt; |
| [leftWiden](../left-widen.md) | [common]<br>fun &lt;[EE](../left-widen.md), [E](../left-widen.md) : [EE](../left-widen.md), [A](../left-widen.md)&gt; [Validated](index.md)&lt;[E](../left-widen.md), [A](../left-widen.md)&gt;.[leftWiden](../left-widen.md)(): [Validated](index.md)&lt;[EE](../left-widen.md), [A](../left-widen.md)&gt; |
| [orElse](../or-else.md) | [common]<br>inline fun &lt;[E](../or-else.md), [A](../or-else.md)&gt; [Validated](index.md)&lt;[E](../or-else.md), [A](../or-else.md)&gt;.[orElse](../or-else.md)(default: () -&gt; [Validated](index.md)&lt;[E](../or-else.md), [A](../or-else.md)&gt;): [Validated](index.md)&lt;[E](../or-else.md), [A](../or-else.md)&gt;<br>Return this if it is Valid, or else fall back to the given default. The functionality is similar to that of [findValid](../find-valid.md) except for failure accumulation, where here only the error on the right is preserved and the error on the left is ignored. |
| [orNone](../or-none.md) | [common]<br>fun &lt;[E](../or-none.md), [A](../or-none.md)&gt; [Validated](index.md)&lt;[E](../or-none.md), [A](../or-none.md)&gt;.[orNone](../or-none.md)(): [Option](../-option/index.md)&lt;[A](../or-none.md)&gt; |
| [orNull](../or-null.md) | [common]<br>fun &lt;[E](../or-null.md), [A](../or-null.md)&gt; [Validated](index.md)&lt;[E](../or-null.md), [A](../or-null.md)&gt;.[orNull](../or-null.md)(): [A](../or-null.md)?<br>Return the Valid value, or null if Invalid |
| [redeem](../redeem.md) | [common]<br>inline fun &lt;[E](../redeem.md), [A](../redeem.md), [B](../redeem.md)&gt; [Validated](index.md)&lt;[E](../redeem.md), [A](../redeem.md)&gt;.[redeem](../redeem.md)(fe: ([E](../redeem.md)) -&gt; [B](../redeem.md), fa: ([A](../redeem.md)) -&gt; [B](../redeem.md)): [Validated](index.md)&lt;[E](../redeem.md), [B](../redeem.md)&gt; |
| [replicate](../replicate.md) | [common]<br>fun &lt;[E](../replicate.md), [A](../replicate.md)&gt; [Validated](index.md)&lt;[E](../replicate.md), [A](../replicate.md)&gt;.[replicate](../replicate.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../replicate.md)&gt;, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Validated](index.md)&lt;[E](../replicate.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](../replicate.md)&gt;&gt;<br>fun &lt;[E](../replicate.md), [A](../replicate.md)&gt; [Validated](index.md)&lt;[E](../replicate.md), [A](../replicate.md)&gt;.[replicate](../replicate.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../replicate.md)&gt;, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MA: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[A](../replicate.md)&gt;): [Validated](index.md)&lt;[E](../replicate.md), [A](../replicate.md)&gt; |
| [sequence](../sequence.md) | [common]<br>fun &lt;[E](../sequence.md), [A](../sequence.md)&gt; [Validated](index.md)&lt;[E](../sequence.md), [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](../sequence.md)&gt;&gt;.[sequence](../sequence.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Validated](index.md)&lt;[E](../sequence.md), [A](../sequence.md)&gt;&gt; |
| [sequenceEither](../sequence-either.md) | [common]<br>fun &lt;[E](../sequence-either.md), [A](../sequence-either.md), [B](../sequence-either.md)&gt; [Validated](index.md)&lt;[A](../sequence-either.md), [Either](../-either/index.md)&lt;[E](../sequence-either.md), [B](../sequence-either.md)&gt;&gt;.[sequenceEither](../sequence-either.md)(): [Either](../-either/index.md)&lt;[E](../sequence-either.md), [Validated](index.md)&lt;[A](../sequence-either.md), [B](../sequence-either.md)&gt;&gt; |
| [sequenceNullable](../sequence-nullable.md) | [common]<br>fun &lt;[A](../sequence-nullable.md), [B](../sequence-nullable.md)&gt; [Validated](index.md)&lt;[A](../sequence-nullable.md), [B](../sequence-nullable.md)?&gt;.[sequenceNullable](../sequence-nullable.md)(): [Validated](index.md)&lt;[A](../sequence-nullable.md), [B](../sequence-nullable.md)&gt;? |
| [sequenceOption](../sequence-option.md) | [common]<br>fun &lt;[A](../sequence-option.md), [B](../sequence-option.md)&gt; [Validated](index.md)&lt;[A](../sequence-option.md), [Option](../-option/index.md)&lt;[B](../sequence-option.md)&gt;&gt;.[sequenceOption](../sequence-option.md)(): [Option](../-option/index.md)&lt;[Validated](index.md)&lt;[A](../sequence-option.md), [B](../sequence-option.md)&gt;&gt; |
| [toIor](../to-ior.md) | [common]<br>fun &lt;[E](../to-ior.md), [A](../to-ior.md)&gt; [Validated](index.md)&lt;[E](../to-ior.md), [A](../to-ior.md)&gt;.[toIor](../to-ior.md)(): [Ior](../-ior/index.md)&lt;[E](../to-ior.md), [A](../to-ior.md)&gt;<br>Converts the value to an Ior |
| [valueOr](../value-or.md) | [common]<br>inline fun &lt;[E](../value-or.md), [A](../value-or.md)&gt; [Validated](index.md)&lt;[E](../value-or.md), [A](../value-or.md)&gt;.[valueOr](../value-or.md)(f: ([E](../value-or.md)) -&gt; [A](../value-or.md)): [A](../value-or.md)<br>Return the Valid value, or the result of f if Invalid |
| [widen](../widen.md) | [common]<br>fun &lt;[E](../widen.md), [B](../widen.md), [A](../widen.md) : [B](../widen.md)&gt; [Validated](index.md)&lt;[E](../widen.md), [A](../widen.md)&gt;.[widen](../widen.md)(): [Validated](index.md)&lt;[E](../widen.md), [B](../widen.md)&gt;<br>Given [A](../widen.md) is a sub type of [B](../widen.md), re-type this value from Validated to Validated |
| [zip](../zip.md) | [common]<br>fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, fb: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;): [Validated](index.md)&lt;[E](../zip.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](../zip.md), [B](../zip.md)&gt;&gt;<br>inline fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md), [Z](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, b: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;, f: ([A](../zip.md), [B](../zip.md)) -&gt; [Z](../zip.md)): [Validated](index.md)&lt;[E](../zip.md), [Z](../zip.md)&gt;<br>inline fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md), [C](../zip.md), [Z](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, b: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;, c: [Validated](index.md)&lt;[E](../zip.md), [C](../zip.md)&gt;, f: ([A](../zip.md), [B](../zip.md), [C](../zip.md)) -&gt; [Z](../zip.md)): [Validated](index.md)&lt;[E](../zip.md), [Z](../zip.md)&gt;<br>inline fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [Z](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, b: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;, c: [Validated](index.md)&lt;[E](../zip.md), [C](../zip.md)&gt;, d: [Validated](index.md)&lt;[E](../zip.md), [D](../zip.md)&gt;, f: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md)) -&gt; [Z](../zip.md)): [Validated](index.md)&lt;[E](../zip.md), [Z](../zip.md)&gt;<br>inline fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [Z](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, b: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;, c: [Validated](index.md)&lt;[E](../zip.md), [C](../zip.md)&gt;, d: [Validated](index.md)&lt;[E](../zip.md), [D](../zip.md)&gt;, e: [Validated](index.md)&lt;[E](../zip.md), [EE](../zip.md)&gt;, f: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md)) -&gt; [Z](../zip.md)): [Validated](index.md)&lt;[E](../zip.md), [Z](../zip.md)&gt;<br>inline fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [FF](../zip.md), [Z](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, b: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;, c: [Validated](index.md)&lt;[E](../zip.md), [C](../zip.md)&gt;, d: [Validated](index.md)&lt;[E](../zip.md), [D](../zip.md)&gt;, e: [Validated](index.md)&lt;[E](../zip.md), [EE](../zip.md)&gt;, ff: [Validated](index.md)&lt;[E](../zip.md), [FF](../zip.md)&gt;, f: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [FF](../zip.md)) -&gt; [Z](../zip.md)): [Validated](index.md)&lt;[E](../zip.md), [Z](../zip.md)&gt;<br>inline fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [F](../zip.md), [G](../zip.md), [Z](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, b: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;, c: [Validated](index.md)&lt;[E](../zip.md), [C](../zip.md)&gt;, d: [Validated](index.md)&lt;[E](../zip.md), [D](../zip.md)&gt;, e: [Validated](index.md)&lt;[E](../zip.md), [EE](../zip.md)&gt;, ff: [Validated](index.md)&lt;[E](../zip.md), [F](../zip.md)&gt;, g: [Validated](index.md)&lt;[E](../zip.md), [G](../zip.md)&gt;, f: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [F](../zip.md), [G](../zip.md)) -&gt; [Z](../zip.md)): [Validated](index.md)&lt;[E](../zip.md), [Z](../zip.md)&gt;<br>inline fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [Z](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, b: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;, c: [Validated](index.md)&lt;[E](../zip.md), [C](../zip.md)&gt;, d: [Validated](index.md)&lt;[E](../zip.md), [D](../zip.md)&gt;, e: [Validated](index.md)&lt;[E](../zip.md), [EE](../zip.md)&gt;, ff: [Validated](index.md)&lt;[E](../zip.md), [F](../zip.md)&gt;, g: [Validated](index.md)&lt;[E](../zip.md), [G](../zip.md)&gt;, h: [Validated](index.md)&lt;[E](../zip.md), [H](../zip.md)&gt;, f: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md)) -&gt; [Z](../zip.md)): [Validated](index.md)&lt;[E](../zip.md), [Z](../zip.md)&gt;<br>inline fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [Z](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, b: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;, c: [Validated](index.md)&lt;[E](../zip.md), [C](../zip.md)&gt;, d: [Validated](index.md)&lt;[E](../zip.md), [D](../zip.md)&gt;, e: [Validated](index.md)&lt;[E](../zip.md), [EE](../zip.md)&gt;, ff: [Validated](index.md)&lt;[E](../zip.md), [F](../zip.md)&gt;, g: [Validated](index.md)&lt;[E](../zip.md), [G](../zip.md)&gt;, h: [Validated](index.md)&lt;[E](../zip.md), [H](../zip.md)&gt;, i: [Validated](index.md)&lt;[E](../zip.md), [I](../zip.md)&gt;, f: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md)) -&gt; [Z](../zip.md)): [Validated](index.md)&lt;[E](../zip.md), [Z](../zip.md)&gt;<br>inline fun &lt;[E](../zip.md), [A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md), [Z](../zip.md)&gt; [Validated](index.md)&lt;[E](../zip.md), [A](../zip.md)&gt;.[zip](../zip.md)(SE: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../zip.md)&gt;, b: [Validated](index.md)&lt;[E](../zip.md), [B](../zip.md)&gt;, c: [Validated](index.md)&lt;[E](../zip.md), [C](../zip.md)&gt;, d: [Validated](index.md)&lt;[E](../zip.md), [D](../zip.md)&gt;, e: [Validated](index.md)&lt;[E](../zip.md), [EE](../zip.md)&gt;, ff: [Validated](index.md)&lt;[E](../zip.md), [F](../zip.md)&gt;, g: [Validated](index.md)&lt;[E](../zip.md), [G](../zip.md)&gt;, h: [Validated](index.md)&lt;[E](../zip.md), [H](../zip.md)&gt;, i: [Validated](index.md)&lt;[E](../zip.md), [I](../zip.md)&gt;, j: [Validated](index.md)&lt;[E](../zip.md), [J](../zip.md)&gt;, f: ([A](../zip.md), [B](../zip.md), [C](../zip.md), [D](../zip.md), [EE](../zip.md), [F](../zip.md), [G](../zip.md), [H](../zip.md), [I](../zip.md), [J](../zip.md)) -&gt; [Z](../zip.md)): [Validated](index.md)&lt;[E](../zip.md), [Z](../zip.md)&gt; |
