---
layout: docs-optics
title: Index
permalink: /docs/optics/index/
---

## Index


`Index` provides an [Optional]({{ '/docs/optics/optional' | relative_url }}) for a structure `S` to focus in optional `A` at a given index `I`.

### Example

If, for a structure `S`, the optionally focus `A` can be indexed by `I`, then `Index` can create an `Optional` with focus at `S` for a given index `I`.
We can use that `Optional` to safely operate on that focus `S` (i.e., operating on items in a `List` based on the index position).

```kotlin:ank
import arrow.core.*
import arrow.optics.typeclasses.*
import arrow.optics.extensions.listk.index.*

val thirdListItemOptional = ListK.index<String>().index(3)

thirdListItemOptional.set(listOf("0", "1", "2", "3").k(), "newValue")
```
```kotlin:ank
thirdListItemOptional.set(listOf("0", "1", "2").k(), "newValue")
```
```kotlin:ank
thirdListItemOptional.setOption(listOf("0", "1", "2").k(), "newValue")
```

#### Creating your own `Index` instances

Arrow provides `Index` instances for some common datatypes in both Arrow and the Kotlin stdlib that can be indexed, like `ListK` and `MapK`.
You can look them up by calling `Index.index()`.

You may create instances of `Index` for your own datatypes, which you will be able to use as demonstrated in the [example](#example) above.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Index` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.optics.typeclasses.*

TypeClass(Index::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.optics.typeclasses.Index)
