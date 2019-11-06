---
layout: docs-core
title: Moore
permalink: /docs/arrow/data/moore/
redirect_from:
  - /docs/datatypes/moore/
---

## Moore

{:.intermediate}

intermediate

A `Moore` machine is a [comonadic]({{ '/docs/arrow/typeclasses/comonad' | relative_url }}) data structure that holds a state and, in order to change it, we need to dispatch events of some specific type. This approach is similar to the [_Elm architecture_](https://guide.elm-lang.org/architecture/) or [_Redux_](https://redux.js.org).

For creating a `Moore` machine, we need its initial state and a `handle` function that will determine the inputs it can accept and how the state will change with each one.

```kotlin:ank
import arrow.core.*
import arrow.ui.*

fun handleRoute(route: String): Moore<String, Id<String>> = when (route) {
  "About" -> Moore(Id("About"), ::handleRoute)
  "Home" -> Moore(Id("Home"), ::handleRoute)
  else -> Moore(Id("???"), ::handleRoute)
}

val routerMoore = Moore(Id("???"), ::handleRoute)

routerMoore
    .handle("About")
    .extract()
    .extract()
```

We also have an `extract` function that returns the current state, and a `coflatMap` that transforms its type:

```kotlin:ank
routerMoore
    .coflatMap { (view) ->
      when (view.extract()) {
        "About" -> 1
        "Home" -> 2
        else -> 0
      }
    }
    .extract()
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.ui.*
import arrow.core.*

DataType(Moore::class).tcMarkdownList()
```
