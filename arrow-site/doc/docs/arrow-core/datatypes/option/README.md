---
layout: docs-core
title: Option
permalink: /docs/arrow/core/option/
redirect_from:
  - /docs/datatypes/option/

video: 5SFTbphderE
---

## Option

{:.beginner}
beginner

[Перевод на русский](/docs/arrow/core/option/ru)

If you have worked with Java at all in the past, it is very likely that you have come across a `NullPointerException` at some time (other languages will throw similarly named errors in such a case). Usually this happens because some method returns `null` when you weren't expecting it and, thus, isn't dealing with that possibility in your client code. A value of `null` is often abused to represent an absent optional value.
Kotlin tries to solve the problem by getting rid of `null` values altogether, and providing its own special syntax [Null-safety machinery based on `?`](https://kotlinlang.org/docs/reference/null-safety.html).

Arrow models the absence of values through the `Option` datatype similar to how Scala, Haskell, and other FP languages handle optional values.

`Option<A>` is a container for an optional value of type `A`. If the value of type `A` is present, the `Option<A>` is an instance of `Some<A>`, containing the present value of type `A`. If the value is absent, the `Option<A>` is the object `None`.

```kotlin:ank
import arrow.*
import arrow.core.*

val someValue: Option<String> = Some("I am wrapped in something")
someValue
```

```kotlin:ank
val emptyValue: Option<String> = None
emptyValue
```

Let's write a function that may or may not give us a string, thus returning `Option<String>`:

```kotlin:ank:silent
fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
   if (flag) Some("Found value") else None
```

Using `getOrElse`, we can provide a default value `"No value"` when the optional argument `None` does not exist:

```kotlin:ank:silent
val value1 = maybeItWillReturnSomething(true)
val value2 = maybeItWillReturnSomething(false)
```

```kotlin:ank
value1.getOrElse { "No value" }
```

```kotlin:ank
value2.getOrElse { "No value" }
```

Creating a `Option<T>` of a `T?`. Useful for working with values that can be nullable:

```kotlin:ank
val myString: String? = "Nullable string"
val option: Option<String> = Option.fromNullable(myString)
```

Checking whether option has value:

```kotlin:ank
value1 is None
```

```kotlin:ank
value2 is None
```

Option can also be used with when statements:

```kotlin:ank
val someValue: Option<Double> = Some(20.0)
val value = when(someValue) {
   is Some -> someValue.t
   is None -> 0.0
}
value
```

```kotlin:ank
val noValue: Option<Double> = None
val value = when(noValue) {
   is Some -> noValue.t
   is None -> 0.0
}
value
```

An alternative for pattern matching is performing Functor/Foldable style operations. This is possible because an option could be looked at as a collection or foldable structure with either one or zero elements.

One of these operations is `map`. This operation allows us to map the inner value to a different type while preserving the option:

```kotlin:ank:silent
val number: Option<Int> = Some(3)
val noNumber: Option<Int> = None
val mappedResult1 = number.map { it * 1.5 }
val mappedResult2 = noNumber.map { it * 1.5 }
```

```kotlin:ank
mappedResult1
```

```kotlin:ank
mappedResult2
```

Another operation is `fold`. This operation will extract the value from the option, or provide a default if the value is `None`

```kotlin:ank
number.fold({ 1 }, { it * 3 })
```

```kotlin:ank
noNumber.fold({ 1 }, { it * 3 })
```

Arrow also adds syntax to all datatypes so you can easily lift them into the context of `Option` where needed.

```kotlin:ank
1.some()
```

```kotlin:ank
none<String>()
```

```kotlin:ank
val nullableValue: String? = null
nullableValue.toOption()
```

```kotlin:ank
val nullableValue: String? = "Hello"
nullableValue.toOption()
```

Some Iterable extensions are available, so you can maintain a friendly API syntax while avoiding null handling (`firstOrNull()`)

```kotlin:ank:silent
val myList: List<Int> = listOf(1,2,3,4)
```

```kotlin:ank
myList.firstOrNone { it == 4 }
```

```kotlin:ank
myList.firstOrNone { it == 5 }
```

Sample usage

```
fun foo() {
    val foxMap = mapOf(1 to "The", 2 to "Quick", 3 to "Brown", 4 to "Fox")

    val ugly = foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() }.toOption()
    val pretty = foxMap.entries.firstOrNone { it.key == 5 }.map { it.value.toCharArray() }

    //Do something with pretty Option
}
```

Arrow contains `Option` instances for many useful typeclasses that allow you to use and transform optional values

[`Functor`]({{ '/docs/arrow/typeclasses/functor/' | relative_url }})

Transforming the inner contents

```kotlin:ank
import arrow.typeclasses.*
import arrow.core.extensions.option.functor.*

Some(1).map { it + 1 }
```

[`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }})

Computing over independent values

```kotlin:ank
import arrow.core.extensions.option.apply.*

tupled(Some(1), Some("Hello"), Some(20.0))
```

[`Monad`]({{ '/docs/arrow/typeclasses/monad/' | relative_url }})

Computing over dependent values ignoring absence

```kotlin:ank
import arrow.core.extensions.fx

Option.fx {
  val (a) = Some(1)
  val (b) = Some(1 + a)
  val (c) = Some(1 + b)
  a + b + c
}
```

```kotlin:ank
Option.fx {
  val (x) = none<Int>()
  val (y) = Some(1 + x)
  val (z) = Some(1 + y)
  x + y + z
}
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.core.*

DataType(Option::class).tcMarkdownList()
```

## Credits

Contents partially adapted from [Scala Exercises Option Tutorial](https://www.scala-exercises.org/std_lib/options)
Originally based on the Scala Koans.
