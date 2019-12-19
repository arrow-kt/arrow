---
layout: docs-core
title: Day
permalink: /docs/arrow/ui/day/
redirect_from:
  - /docs/datatypes/day/
---

## Day

When building user interfaces, it is common to have two screens side by side evolving their states independently. In order to implement this behavior, we can use `Day`.

`Day` is a [`comonadic`]({{ '/docs/arrow/typeclasses/comonad' | relative_url }}) data structure that holds two `Comonads` and a rendering function for both states.

```kotlin:ank
import arrow.core.*
import arrow.ui.*
import arrow.core.extensions.id.comonad.*

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
