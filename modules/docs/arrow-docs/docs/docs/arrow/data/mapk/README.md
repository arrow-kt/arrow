---
layout: docs
title: MapK
permalink: /docs/arrow/data/mapk/
redirect_from:
  - /docs/datatypes/mapk/
---

## MapK

{:.beginner}
beginner

MapK is an Arrow wrapper over Kotlin Map type. The main goal is to make it a [type constructor](/docs/patterns/glossary/#type-constructors)
and to work with Map in more functional way.

It can be created with calling `k()` function on Map

```kotlin:ank
import arrow.core.*

    val arrowMap: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k() // MapK(map={one=1, two=2})
```

You can modify each map entry value and get another MapK with use of `map(..)` function:
```kotlin:ank
    val modifiedMap = arrowMap.map { entryValue -> entryValue.plus(10) } // MapK(map={one=11, two=12})
```

Sometimes you need to "map" one Map to another. You are able to do so with `map2(..)` function:
```kotlin:ank
    val firstBag = mapOf("eggs" to 5, "milk" to 1).k()
    val secondBag = mapOf("eggs" to 6, "cheese" to 1).k()

    val eggsBag = firstBag.map2(secondBag) { firstBagMatch, secondBagMatch ->     // If there are matching keys
        firstBagMatch + secondBagMatch     // you can modify the value of output MapK
    }
    // eggsBag = MapK(map={eggs=11})
```
`map2Eval` does pretty much the same as `map2`, but result `KMap` will be wrapped in [`Eval`](https://arrow-kt.io/docs/arrow/core/eval/#eval) type.

`ap` function is used when you want to apply map of transformations from `Map<K, (A)-> B>` to `Map<K,A>`, for example:
```kotlin:ank
    val map1: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()
    
    val f1 = { i: Int -> "f1 to \"$i\"" }
    val f2 = { i: Int -> "f2 to \"$i\"" }
    val map2: MapK<String, (Int) -> String> = mapOf("one" to f1, "two" to f2).k()

    val apResult = map1.ap(map2) // MapK(map={one=f1 to "1", two=f2 to "2"})
``` 
`ap2` acts like `map2` to `map`

In most cases you would like to use `flatMap` function which flattens source map, accepts `(A) -> MapK<K,B>` functor and returns `MapK<K,Z>`
```kotlin:ank
    val map1: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()
    val map2: MapK<String, String> = mapOf("one" to "ONE", "three" to "three").k()

    val flattened = map1.flatMap { map2 } // MapK(map={one=ONE})
```

`foldLeft` and `foldRight` are used for element aggregation:
```kotlin:ank
    val map1: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()

    val foldLeft: String = map1.foldLeft("one") { entry, tuple -> entry + tuple} // "one12"
    val foldRight: Eval<String> =                                                // foldRight.value() = "1 2 one", since it is Eval
        map1.foldRight(Eval.just("one")) { entry, eval ->
            Eval.just("$entry ${eval.value()}")
        }
```

// TODO: `traverse` function, can't find Option.applicative