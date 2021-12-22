---
layout: docs-optics
title: At
permalink: /optics/at/
---

## At

`At` provides a [Lens]({{ '/optics/lens' | relative_url }}) for a structure `S` to focus in `A` at a given index `I`.

### Example

If, for a structure `S`, the focus `A` can be indexed by `I`, then `At` can create an `Lens` with focus at `S` for a given index `I`.
We can use that `Lens` to operate on that focus `S` to get, set, and modify the focus at a given index `I`.

A `MapK<Int, String>` can be indexed by its keys `Int`, but not for every index where an entry can be found.

```kotlin
import arrow.core.*
import arrow.optics.typeclasses.At

val mapAt = At.map<Int, String>().at(2)

val map = mapOf(
            1 to "one",
            2 to "two",
            3 to "three"
    )

mapAt.set(map, "new value".some())
```

By setting an empty value for a key, we delete that entry by removing the value.

```kotlin
mapAt.set(map, none())
```

#### Creating your own `At` instances

Arrow provides `At` instances for some common datatypes in Arrow that can be indexed. You can look them up by calling `At.at()`.

You may create instances of `At` for your own datatypes which you will be able to use as demonstrated in the [example](#example) above.

See [Deriving and creating custom typeclass]({{ '/patterns/glossary' | relative_url }}) to provide your own `At` instances for custom datatypes.
