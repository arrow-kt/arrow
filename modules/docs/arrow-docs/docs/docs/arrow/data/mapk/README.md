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

It is possible to transform map entry value type:
```kotlin:ank
    val map1: MapK<String, Int> = mapOf("one" to 1, "two" to 2).k()
    val map2: MapK<String, (Int) -> String> = mapOf("one" to { i: Int -> "String \"$i\"" }).k()

    val apResult = map1.ap(map2) // MapK(map={one=String "1"})
```