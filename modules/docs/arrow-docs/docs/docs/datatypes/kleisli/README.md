---
layout: docs
title: Kleisli
permalink: /docs/datatypes/kleisli/
---

## Kleisli 

Kleisli enables composition of functions that return a monadic value, for instance an `Option<Int>` 
or a `Either<String, Int>`, without having functions take an `Option` or `Either` as a parameter.

For example we have the function `String.toInt()` which can throw a `NumberFormatException` and 
we want to do a safe conversion like this:

```kotlin:ank
 val optionIntKleisli = Kleisli { str: String ->
    if (str.toCharArray().all { it.isDigit() }) Some(str.toInt()) else None
  }

  fun String.safeToInt(): Option<Int> {
  return optionIntKleisli.run(this).ev()
}
```

Then when we use the function we have an `Option<Int>`

```kotlin:ank
"a".safeToInt()
"1".safeToInt()
```

## Functions

#### Run and Ev
The most common function you will probably use is `run()` to execute the `Kleisli` and `ev()` to get the result in the monadic context.

#### Local
This function allows doing a conversion inside the Kleisli to the original input value before the Kleisli will be executed, creating a Kleisli with the input type of the conversion

```kotlin:ank
optionIntKleisli.local { optStr :Option<String> -> optStr.getOrElse { "" } }
```

#### Map


#### FlatMap


#### Zip


#### AndThen



