---
layout: docs-core
title: Store
permalink: /docs/arrow/ui/store/
redirect_from:
  - /docs/datatypes/store/
---

## Store





`Store` is a data structure that holds a state and a function for extracting a representation of it.

If we think in a component oriented fashion when building user interfaces, this datatype is the most basic unit.

This structure is also a [`Comonad`]({{ '/docs/arrow/typeclasses/comonad' | relative_url }}) because it represents a lazy unfolding of all possible states of our user interface.

```kotlin:ank
import arrow.ui.*

val store = Store(0) { "The current value is: $it" }
store.extract()
```

If we want to change the initial state of the store, we have a `move` method:

```kotlin:ank
val newStore = store.move(store.state + 1)
newStore.extract()
```

We also have two methods from `Comonad`:

* `extract` for rendering the current state.

* `coflatMap` for replacing the representation type in each future state.

```kotlin:ank
import arrow.core.*

val tupleStore = store.coflatMap { it: Store<Int, String> -> Tuple2("State", it.state) }
tupleStore.extract()
```

And, as a `Comonad` is also a `Functor`, we have `map`, which allows us to transform the state representation:

```kotlin:ank
val upperCaseStore = store.map { it: String -> it.toUpperCase() }
upperCaseStore.extract()
```
