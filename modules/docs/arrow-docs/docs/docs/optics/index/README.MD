---
layout: docs
title: Index
permalink: /docs/optics/index/
---

## Index

`Index` provides an [Optional]({{ '/docs/optics/optional' | relative_url }}) for a structure `S` to focus in optional `A` at a given index `I`.

### Example

If for a structure `S` the optionally focus `A` can be indexed by `I` then `Index` can create an `Optional` with focus at `S` for a given index `I`.
We can use that `Optional` to safely operate on that focus `S` i.e. operating on items in a `List` based on the index position.

```kotlin:ank
import arrow.data.*
import arrow.optics.typeclasses.*

val thirdListItemOptional = Index.index<ListKWOf<String>, Int, String>(3)

thirdListItemOptional.set(listOf("0", "1", "2", "3").k(), "newValue")
```
```kotlin:ank
thirdListItemOptional.set(listOf("0", "1", "2").k(), "newValue")
```
```kotlin:ank
thirdListItemOptional.setOption(listOf("0", "1", "2").k(), "newValue")
```

#### Creating your own `Index` instances

Arrow provides `Index` instances for some common datatypes both in Arrow and the Kotlin stdlib that can be indexed, like `ListKW` and `MapKW`.
You can look them up by calling `Index.index()`.

You may create instances of `Index` for your own datatypes which you will be able to use as demonstrated in the [example](#example) above.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Index` instances for custom datatypes.

### Instances

The following datatypes in Arrow provide instances that adhere to the `Index` typeclass.

- [String]
- [NonEmptyList]({{ '/docs/datatypes/nonemptylist' | relative_url }})
- [ListKW]({{ '/docs/datatypes/listkw' | relative_url }})
- [SequenceKW]({{ '/docs/datatypes/sequencekw' | relative_url }})
- [MapKW]({{ '/docs/datatypes/mapkw' | relative_url }})
