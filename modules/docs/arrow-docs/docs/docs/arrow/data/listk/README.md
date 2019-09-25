---
layout: docs
title: ListK
permalink: /docs/arrow/data/listk/
redirect_from:
  - /docs/datatypes/listk/
---
## ListK

{:.beginner}
beginner

ListK wraps over the platform `List` type to make it a [`type constructor`]({{ '/docs/patterns/glossary/#type-constructors' | relative_url }}).

It can be created from Kotlin List type with a convenient `k()` function.

```kotlin:ank:playground
import arrow.core.k

val value =
//sampleStart
    listOf(1, 2, 3).k()
//sampleEnd
fun main() {
    println(value)
}
```

For most use cases you will never use `ListK` directly but `List` directly with the extension functions that Arrow projects over it.

ListK implements operators from many useful typeclasses.

The @extension type class processor expands all type class combinators that `ListK` provides automatically over `List`

For instance, it has `combineK` from the [`SemigroupK`]({{ '/docs/arrow/typeclasses/semigroupk/' | relative_url }}) typeclass.

It can be used to cheaply combine two lists:

```kotlin:ank:playground
import arrow.core.extensions.list.semigroupK.combineK

//sampleStart
val hello = listOf('h', 'e', 'l', 'l', 'o')
val commaSpace = listOf(',', ' ')
val world = listOf('w', 'o', 'r', 'l', 'd')

val combinedList = hello.combineK(commaSpace).combineK(world)
//sampleEnd
fun main() {
    println("combinedList = $combinedList")
}
```

The functions `traverse` and `sequence` come from [`Traverse`]({{ '/docs/arrow/typeclasses/traverse/' |  relative_url }}).

Traversing a list creates a new container [`Kind<F, A>`]({{ '/docs/patterns/glossary/#type-constructors' |  relative_url }}) by combining the result of a function applied to each element:

```kotlin:ank:playground
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.list.traverse.traverse
import arrow.core.extensions.option.applicative.applicative

//sampleStart
val numbers = listOf(Math.random(), Math.random(), Math.random())
val traversedList = numbers.traverse(Option.applicative(), { if (it > 0.5) Some(it) else None })
//sampleEnd
fun main() {
    println("traversedList $traversedList")
}
```

and complements the convenient function `sequence()` that converts a list of `ListK<Kind<F, A>>` into a `Kind<F, ListK<A>>`:

```kotlin:ank:playground
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.option.applicative.applicative

//sampleStart
val requests = listOf(Some(Math.random()), Some(Math.random()), Some(Math.random()))
val sequenceList = requests.sequence(Option.applicative())
//sampleEnd
fun main() {
    println("sequenceList = $sequenceList")
}
```

If you want to aggregate the elements of a list into any other value you can use `foldLeft` and `foldRight` from [`Foldable`]({{ '/docs/arrow/typeclasses/foldable' |  relative_url }}).

Folding a list into a new value, `String` in this case, starting with an initial value and a combine function:

```kotlin:ank:playground
import arrow.core.k
import arrow.core.extensions.list.foldable.foldLeft
val value =
//sampleStart
    listOf('a', 'b', 'c', 'd', 'e').k().foldLeft("-> ") { x, y -> x + y }
//sampleEnd
fun main() {
    println(value)
}
```

Or you can apply a list of transformations using `ap` from [`Applicative`]({{ '/docs/arrow/typeclasses/applicative/' | relative_url }}).

```kotlin:ank:playground
import arrow.core.extensions.list.apply.ap

val value =
//sampleStart
    listOf(1, 2, 3).ap(listOf({ x: Int -> x + 10 }, { x: Int -> x * 2 }))
//sampleEnd
fun main() {
    println(value)
}
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.DataType
import arrow.reflect.tcMarkdownList
import arrow.core.ListK

DataType(ListK::class).tcMarkdownList()
```
