---
layout: docs-core
title: MapK
permalink: /docs/arrow/data/mapk/
redirect_from:
  - /docs/datatypes/mapk/
---

## MapK

{:.beginner}
beginner

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.core.*

DataType(MapK::class).tcMarkdownList()
```

`MapK` is an Arrow wrapper over Kotlin `Map` type. The main goal is to make it a [type constructor]({{ '/docs/patterns/glossary/#type-constructors' | relative_url }})
and to work with `Map` in more functional way.

It can be created by calling `k()` function on Map: 

```kotlin:ank
import arrow.core.MapK
import arrow.core.k
import arrow.core.Eval

val arrowMap: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()
```

You can modify each map entry value and get another MapK with use of `map(..)` function:

```kotlin:ank
val arrowMap: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()
val modifiedMap: MapK<String, Int> = arrowMap.map { entryValue -> entryValue.plus(10) }
modifiedMap
```

Sometimes you need to "map" one Map to another. You are able to do so with `map2(..)` function:

```kotlin:ank
val firstBag: MapK<String, Int> = mapOf("eggs" to 5, "milk" to 1).k()
val secondBag: MapK<String, Int> = mapOf("eggs" to 6, "cheese" to 1).k()

val eggsBag: MapK<String, Int> = firstBag.map2(secondBag) { firstBagMatch, secondBagMatch ->
  // If there are matching keys
  firstBagMatch + secondBagMatch  // you can modify the value of output MapK
}
```

`map2Eval` does pretty much the same as `map2`, but result `KMap` will be wrapped in [`Eval`](https://arrow-kt.io/docs/arrow/core/eval/#eval) type.

`ap` function is used when you want to apply map of transformations from `Map<K, (A)-> B>` to `Map<K,A>`. For example:

```kotlin:ank
val map1: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()

val f1: (Int) -> String = { i: Int -> "f1 to \"$i\"" }
val f2: (Int) -> String = { i: Int -> "f2 to \"$i\"" }
val map2: MapK<String, (Int) -> String> = mapOf("one" to f1, "two" to f2).k()

val apResult = map1.ap(map2)
apResult
``` 
`ap2` acts like `map2` to `map`

In most cases, you want to use `flatMap` function, which flattens source map, accepts `(A) -> MapK<K,B>` functor, and returns `MapK<K,Z>`

```kotlin:ank
val map1: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()
val map2: MapK<String, String> = mapOf("one" to "ONE", "three" to "three").k()

val flattened: MapK<String, String> = map1.flatMap { map2 }
flattened
```

`foldLeft` and `foldRight` are used for element aggregation:

```kotlin:ank:playground
import arrow.core.Eval
import arrow.core.MapK
import arrow.core.k

fun main() {
  //sampleStart
  val map1: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()

  val foldLeft: String = map1.foldLeft("one") { entry, tuple -> entry + tuple }
  val foldRight: Eval<String> =
    map1.foldRight(Eval.just("one")) { entry, eval ->
      Eval.just("$entry ${eval.value()}")
    }
  //sampleEnd
  println(foldLeft)
  println(foldRight)
}
```

You can also traverse `MapK` data structure performing an action on each element:

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import arrow.core.k
import arrow.core.some

fun main() {
  //sampleStart
  val optionMap = mapOf(1.some() to "one", 2.some() to "two", None to "none").k()
    .traverse(Option.applicative()) { value ->
      when (value) {
        "one", "two", "none" -> Some(value)
        else -> None
      }
    }.fix()
  //sampleEnd
  println(optionMap)
}
``` 

TODO: Add link to `Traverse` docs when it's ready https://github.com/arrow-kt/arrow/pull/1534
