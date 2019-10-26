---
layout: docs
title: Option
permalink: /docs/arrow/core/option/
redirect_from:
  - /docs/datatypes/option/

video: 5SFTbphderE
---

## Option

{:.beginner}
beginner

If you have worked with Java at all in the past, it is very likely that you have come across a `NullPointerException` at some time (other languages will throw similarly named errors in such a case). Usually this happens because some method returns `null` when you were not expecting it and thus not dealing with that possibility in your client code. A value of `null` is often abused to represent an absent optional value.
Kotlin tries to solve the problem by getting rid of `null` values altogether and providing its own special syntax [Null-safety machinery based on `?`](https://kotlinlang.org/docs/reference/null-safety.html).

Arrow models the absence of values through the `Option` datatype similar to how Scala, Haskell and other FP languages handle optional values.

`Option<A>` is a container for an optional value of type `A`. If the value of type `A` is present, the `Option<A>` is an instance of `Some<A>`, containing the present value of type `A`. If the value is absent, the `Option<A>` is the object `None`.

```kotlin:ank:playground
import arrow.core.Option
import arrow.core.Some
import arrow.core.none

//sampleStart
val someValue: Option<String> = Some("I am wrapped in something")
val emptyValue: Option<String> = none()
//sampleEnd
fun main() {
 println("value = $someValue")
 println("emptyValue = $emptyValue")
}
```

Let's write a function that may or not give us a string, thus returning `Option<String>`:

```kotlin:ank
import arrow.core.None
import arrow.core.Option
import arrow.core.Some

//sampleStart
fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 if (flag) Some("Found value") else None
//sampleEnd
```

Using `getOrElse` we can provide a default value `"No value"` when the optional argument `None` does not exist:

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse

fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 if (flag) Some("Found value") else None

val value1 =
//sampleStart
 maybeItWillReturnSomething(true)
    .getOrElse { "No value" }
//sampleEnd
fun main() {
 println(value1)
}
```

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse

fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 if (flag) Some("Found value") else None

val value2 =
//sampleStart
 maybeItWillReturnSomething(false)
  .getOrElse { "No value" }
//sampleEnd
fun main() {
 println(value2)
}
```

Checking whether option has value:

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some

fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
 if (flag) Some("Found value") else None

 //sampleStart
val valueSome = maybeItWillReturnSomething(true) is None
val valueNone = maybeItWillReturnSomething(false) is None
//sampleEnd
fun main() {
 println("valueSome = $valueSome")
 println("valueNone = $valueNone")
}
```
Creating a `Option<T>` of a `T?`. Useful for working with values that can be nullable:

```kotlin:ank:playground
import arrow.core.Option


//sampleStart
val myString: String? = "Nullable string"
val option: Option<String> = Option.fromNullable(myString)
//sampleEnd
fun main () {
 println("option = $option")
}
```

Option can also be used with when statements:

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some

//sampleStart
val someValue: Option<Double> = Some(20.0)
val value = when(someValue) {
 is Some -> someValue.t
 is None -> 0.0
}
//sampleEnd
fun main () {
 println("value = $value")
}
```

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some

//sampleStart
val noValue: Option<Double> = None
val value = when(noValue) {
 is Some -> noValue.t
 is None -> 0.0
}
//sampleEnd
fun main () {
 println("value = $value")
}
```

An alternative for pattern matching is performing Functor/Foldable style operations. This is possible because an option could be looked at as a collection or foldable structure with either one or zero elements.

One of these operations is `map`. This operation allows us to map the inner value to a different type while preserving the option:

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some

//sampleStart
val number: Option<Int> = Some(3)
val noNumber: Option<Int> = None
val mappedResult1 = number.map { it * 1.5 }
val mappedResult2 = noNumber.map { it * 1.5 }
//sampleEnd
fun main () {
 println("number = $number")
 println("noNumber = $noNumber")
 println("mappedResult1 = $mappedResult1")
 println("mappedResult2 = $mappedResult2")
}
```
Another operation is `fold`. This operation will extract the value from the option, or provide a default if the value is `None`

```kotlin:ank:playground
import arrow.core.Option
import arrow.core.Some

val fold =
//sampleStart
 Some(3).fold({ 1 }, { it * 3 })
//sampleEnd
fun main () {
 println(fold)
}
```

```kotlin:ank:playground
import arrow.core.Option
import arrow.core.none

val fold =
//sampleStart
 none<Int>().fold({ 1 }, { it * 3 })
//sampleEnd
fun main () {
 println(fold)
}
```

Arrow also adds syntax to all datatypes so you can easily lift them into the context of `Option` where needed.

```kotlin:ank:playground
import arrow.core.some

//sampleStart
 val some = 1.some()
 val none = none<String>()
//sampleEnd
fun main () {
 println("some = $some")
 println("none = $none")
}
```

```kotlin:ank:playground
import arrow.core.toOption

//sampleStart
val nullString: String? = null
val valueFromNull = nullString.toOption()

val helloString: String? = "Hello"
val valueFromStr = helloString.toOption()
//sampleEnd
fun main () {
 println("valueFromNull = $valueFromNull")
 println("valueFromStr = $valueFromStr")
}
```

Some Iterable extensions are available, so you can maintain a friendly API syntax while avoiding null handling (`firstOrNull()`)

```kotlin:ank:playground
import arrow.core.firstOrNone

//sampleStart
val myList: List<Int> = listOf(1,2,3,4)

val first4 = myList.firstOrNone { it == 4 }
val first5 = myList.firstOrNone { it == 5 }
//sampleEnd
fun main () {
 println("first4 = $first4")
 println("first5 = $first5")
}
```

Sample usage

```kotlin:ank:playground
import arrow.core.firstOrNone
import arrow.core.toOption

//sampleStart
val foxMap = mapOf(1 to "The", 2 to "Quick", 3 to "Brown", 4 to "Fox")

val ugly = foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() }.toOption()
val pretty = foxMap.entries.firstOrNone { it.key == 5 }.map { it.value.toCharArray() }
//sampleEnd
fun main() {
 println("ugly = $ugly")
 println("pretty = $pretty")
}
```

Arrow contains `Option` instances for many useful typeclasses that allows you to use and transform optional values

[`Functor`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }})

Transforming the inner contents

```kotlin:ank:playground
import arrow.core.Some

fun main() {
val value =
 //sampleStart
   Some(1).map { it + 1 }
 //sampleEnd
 println(value)
}
```

[`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }})

Computing over independent values

```kotlin:ank:playground
import arrow.core.Some
import arrow.core.extensions.option.apply.tupled

 val value =
//sampleStart
 tupled(Some(1), Some("Hello"), Some(20.0))
//sampleEnd
fun main() {
 println(value)
}
```

[`Monad`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }})

Computing over dependent values ignoring absence

```kotlin:ank:playground
import arrow.core.extensions.fx
import arrow.core.Some
import arrow.core.Option

val value =
//sampleStart
 Option.fx {
 val (a) = Some(1)
 val (b) = Some(1 + a)
 val (c) = Some(1 + b)
 a + b + c
}
//sampleEnd
fun main() {
 println(value)
}
```

```kotlin:ank:playground
import arrow.core.extensions.fx
import arrow.core.Some
import arrow.core.none
import arrow.core.Option

val value =
//sampleStart
 Option.fx {
   val (x) = none<Int>()
   val (y) = Some(1 + x)
   val (z) = Some(1 + y)
   x + y + z
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
import arrow.core.Option

DataType(Option::class).tcMarkdownList()
```

## Credits

Contents partially adapted from [Scala Exercises Option Tutorial](https://www.scala-exercises.org/std_lib/options)
Originally based on the Scala Koans.
