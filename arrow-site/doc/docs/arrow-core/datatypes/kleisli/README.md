---
layout: docs-core
title: Kleisli
permalink: /docs/arrow/data/kleisli/
redirect_from:
  - /docs/datatypes/kleisli/
video: vAdhMJWnBUI
---

## Kleisli

{:.intermediate}
intermediate

Kleisli enables the composition of functions that return a monadic value, for instance, an `Option<Int>` or an `Either<String, Int>`, without having functions take an `Option` or `Either` as a parameter.

For example, we have the function `String.toInt()` that can throw a `NumberFormatException`, and we want to do a safe conversion like this:

```kotlin:ank:silent
import arrow.core.*
import arrow.mtl.Kleisli

val optionIntKleisli = Kleisli { str: String ->
  if (str.toCharArray().all { it.isDigit() }) Some(str.toInt()) else None
}

fun String.safeToInt(): Option<Int> {
  return optionIntKleisli.run(this).fix()
}
```

Then, when we use the function, we have an `Option<Int>`

```kotlin:ank
"a".safeToInt()
```
```kotlin:ank
"1".safeToInt()
```

## Functions

#### Local
This function allows conversions inside the Kleisli to the original input value before the Kleisli will be executed, creating a Kleisli with the input type of the conversion

```kotlin:ank
optionIntKleisli.local { optStr :Option<String> -> optStr.getOrElse { "0" } }.run(None)
```

#### Ap
The `ap` function transforms the `Kleisli` into another `Kleisli` with a function as a output value.

```kotlin:ank
import arrow.mtl.fix
import arrow.core.extensions.option.applicative.*
import arrow.core.extensions.option.monad.*

val intToDouble = {number:Int -> number.toDouble()}

val optionIntDoubleKleisli = Kleisli { str: String ->
  if (str.toCharArray().all { it.isDigit() }) Some(intToDouble) else None
}

optionIntKleisli.ap(Option.applicative(), optionIntDoubleKleisli).fix().run("1")
```

#### Map
The `map` function transforms the `Kleisli` output value.

```kotlin:ank
optionIntKleisli.map(Option.applicative()) { output -> output + 1 }.fix().run("1")
```

#### FlatMap
`flatMap` is useful to map the `Kleisli` output into another kleisli

```kotlin:ank
import arrow.mtl.fix

val optionDoubleKleisli = Kleisli { str: String ->
  if (str.toCharArray().all { it.isDigit() }) Some(str.toDouble()) else None
}

optionIntKleisli.flatMap(Option.monad(), { optionDoubleKleisli }).fix().run("1")
```


#### AndThen
You can use `andThen` to compose with another kleisli

```kotlin:ank
import arrow.mtl.fix

val optionFromOptionKleisli = Kleisli { number: Int ->
   Some(number+1)
}

optionIntKleisli.andThen(Option.monad(), optionFromOptionKleisli).fix().run("1")
```

with another function

```kotlin:ank
optionIntKleisli.andThen(Option.monad(), { number: Int -> Some(number+1) }).fix().run("1")
```

or to replace the `Kleisli` result

```kotlin:ank
optionIntKleisli.andThen(Option.monad(), Some(0)).fix().run("1")
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.mtl.*
import arrow.core.*

DataType(Kleisli::class).tcMarkdownList()
```
