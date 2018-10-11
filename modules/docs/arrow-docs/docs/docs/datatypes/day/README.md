---
layout: docs
title: Day
permalink: /docs/datatypes/day
---

## Day

When building user interfaces it is common to have two screens side by side evolving their states independently. In order to implement this behavior we can use `Day`.

`Day` is a [`comonadic`]({{ /docs/typeclasses/comonad | relative_url }}) data structure which holds two `Comonads` and a rendering function for both states.

```kotlin:ank
import arrow.core.*
import arrow.data.*

val renderHtml = { left: String, right: Int -> """     
    |<div>                                             
    | <p>$left</p>                                     
    | <p>$right</p>                                    
    |</div>                                            
  """.trimMargin()                                     
}                                                      
val day = Day(Id.just("Hello"), Id.just(0), renderHtml)
day.extract(Id.comonad(), Id.comonad())
```

## Available Instances

* [Comonad]({{ '/docs/typeclasses/comonad' | relative_url }})
* [Functor]({{ '/docs/typeclasses/functor' | relative_url }})
* [Applicative]({{ '/docs/typeclasses/applicative' | relative_url }})