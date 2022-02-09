---
layout: docs-optics
title: OptionalGetter
permalink: /optics/optional_getter/
---

## OptionalGetter

An `OptionalGetter` is an optic that allows focusing on an optional value. It is an intermediate step between [`Fold`]({{ '/optics/fold/' | relative_url }}), which focuses on 0 to N elements, and [`Getter`]({{ '/optics/getter/' | relative_url }}), which focuses in exactly one element. Some people refer to `OptionalGetter`s as `AffineFold`s.

The main function in `OptionalGetter` is `getOrModify: (S) -> Either<S, A>`, which allows us to get the focus OR return the original value, if the optic does not match. You can also use a version with `getOption: (S) -> Option<A>`, as exemplified in this optic to focus on the head of a list:

```kotlin
import arrow.core.*
import arrow.optics.*

val optionalHead: OptionalGetter<List<Int>, Int> = OptionalGetter(
    getOption = { list -> list.firstOrNull().toOption() }
)
```

### Filtering

The main use of `OptionalGetter` is to filter out unwanted elements. Here is an example in which we combine a `Traversal` focusing on every element in the list, with a filter to exclude those which are smaller than 0.

```kotlin
import arrow.optics.Traversal
import arrow.optics.Optional

val positiveNumbers = Traversal.list<Int>() compose OptionalGetter.filter { it >= 0 }

positiveNumbers.getAll(listOf(1, 2, -3, 4, -5)) == listOf(1, 2, 4)
```
