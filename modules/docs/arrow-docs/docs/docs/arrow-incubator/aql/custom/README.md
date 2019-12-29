---
layout: docs-incubator
title: Adapting custom data types for AQL
permalink: /docs/aql/custom/
---

## Adapting custom data types for AQL




If you are a library author or you simply wish to provide AQL `query` support to your custom data types you can easily do so by simply implementing the type classes required for AQL to automatically derive a AQL DSL for you custom data type.

In the example below we will create a minimal data holder data type that can conform to the @higherkind contract. That is a type with one or more type arguments.

## Creating a __custom data type__

For simplicity the type in the example below is called `Box` and it mirrors the `Option` data type in terms of features. `Box` has two possible states `Empty` and `Full`. The `Empty` state represents a missing value whereas the `Full<A>` state represents a `Box` that has an `A` value contained within.

```kotlin
/**
* Represents a box that can be empty or contain a value
*/
@higherkind 
sealed class Box<out A> : BoxOf<A> {

  object Empty : Box<Nothing>()

  data class Full<A>(val value: A) : Box<A>()

  companion object {
    fun <A> empty(): Box<A> = Empty
  }
  
}
```

Once we have our box defined and annotated as a `@higherkind` we can define the instances that will activate the AQL DSL over all values of our custom type `Box<A>`.

## Enabling __select__ on a custom data type

In order to activate [`select`]() statement we need to provide an instance for the `Functor` type class and the `Select` type class:

```kotlin
import arrow.typeclasses.Functor
import arrow.aql.box.functor.functor

@extension
interface BoxFunctor : Functor<ForBox> {
  override fun <A, B> BoxOf<A>.map(f: (A) -> B): Box<B> =
    when (val box = fix()) {
      Box.Empty -> Box.empty()
      is Box.Full -> Box.Full(f(box.value))
    }
}

@extension
interface BoxSelect : Select<ForBox> {
  override fun functor(): Functor<ForBox> = Box.functor()
}
```

AQL delegates operations to the `Functor` of your choosing.
Once we have this two instance we are able to `select` data from our `Box<A>` values.

```kotlin:ank:playground
import arrow.aql.*
import arrow.aql.box.select.*
fun main(args: Array<String>) {
//sampleStart
val result: Box<Int> = 
  Box.Full(1).query {
    select { this + 1 }
  }.value()
//sampleEnd
println(result)
}
```

[More on __select__](/docs/aql/select/)

## Enabling __where__ on a custom data type

In order to activate [`where`]() statement we need to provide an instance for the `FunctorFilter` type class and the `Where` type class:

```kotlin
import arrow.typeclasses.FunctorFilter
import arrow.aql.box.functor.functorFilter

@extension
interface BoxFunctorFilter : FunctorFilter<ForBox>, BoxFunctor {
  override fun <A, B> BoxOf<A>.filterMap(f: (A) -> Option<B>): Box<B> =
    when (val box = fix()) {
      Box.Empty -> Box.empty()
      is Box.Full -> f(box.value).fold(
        { Box.empty<B>() },
        { Box.Full(it) }
      )
    }
}

@extension
interface BoxWhere : Where<ForBox> {
  override fun functorFilter(): FunctorFilter<ForBox> = Box.functorFilter()
}
```

AQL delegates operations for `Where` to the `FunctorFilter` of your choosing.
Once we have this two instance we are able to us `where` to filter data from our `Box<A>` values.

```kotlin:ank:playground
import arrow.aql.box.select.*
import arrow.aql.box.where.*

data class Student(val name: String, val age: Int)

val john = Student("John", 20)

fun main(args: Array<String>) {
//sampleStart
val result: Box<String> =
  Box.Full(john).query {
    select { name } where { age > 50 }
  }.value()
//sampleEnd
println(result)
}
```

```kotlin:ank:playground
import arrow.aql.box.select.*
import arrow.aql.box.where.*

data class Student(val name: String, val age: Int)

val john = Student("John", 20)

fun main(args: Array<String>) {
//sampleStart
val result: Box<String> =
  Box.Full(john).query {
    select { name } where { age > 10 }
  }.value()
//sampleEnd
println(result)
}
```

[More on __where__](/docs/aql/where/)

## Enabling __count, groupBy, orderBy, sum & union__ on a custom data type

In order to activate __count, groupBy, orderBy, sum & union__ we need to provide an instance for the `Foldable` type class and the one instance for each one of the keyword we want to support :

```kotlin
import arrow.typeclasses.Foldable
import arrow.aql.box.foldable.foldable

@extension
interface BoxFoldable : Foldable<ForBox> {
  override fun <A, B> BoxOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().let {
      when (it) {
        is Box.Full -> f(b, it.value)
        Box.Empty -> b
      }
    }

  override fun <A, B> BoxOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().let {
      when (it) {
        is Box.Full -> f(it.value, lb)
        Box.Empty -> lb
      }
    }
}

@extension
interface BoxGroupBy : GroupBy<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}

@extension
interface BoxCount : Count<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}

@extension
interface BoxSum : Sum<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}

@extension
interface BoxOrderBy : OrderBy<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}

@extension
interface BoxUnion : Union<ForBox> {
  override fun foldable(): Foldable<ForBox> = Box.foldable()
}
```

AQL delegates operations to the `Foldable` of your choosing.
Once we have these instances we are able to use many of the combinators available in AQL to transform date from our `Box<A>` values.

### count

```kotlin:ank:playground
import arrow.aql.*
import arrow.aql.box.select.*
import arrow.aql.box.count.*

fun main(args: Array<String>) {
//sampleStart
val result =
  Box.Full(1).query {
    selectAll().count()
  }.value()
//sampleEnd
println(result)
}
```

### groupBy

```kotlin:ank:playground
import arrow.aql.*
import arrow.aql.box.select.*
import arrow.aql.box.groupBy.*
import arrow.aql.extensions.id.select.*

fun main(args: Array<String>) {
//sampleStart
val result =
  Box.Full(1).query {
    selectAll() groupBy { "selected" } 
  }.value()
//sampleEnd
println(result)
}
```

[More on __groupBy__](/docs/aql/groupby/)

### orderBy

```kotlin:ank:playground
import arrow.aql.*
import arrow.aql.box.select.*
import arrow.aql.box.orderBy.*
import arrow.aql.extensions.id.select.*
import arrow.aql.Ord
import arrow.core.extensions.order

fun main(args: Array<String>) {
//sampleStart
val result =
  Box.Full(1).query {
    selectAll() orderBy Ord.Asc(Int.order())
  }.value()
//sampleEnd
println(result)
}
```

[More on __orderBy__](/docs/aql/orderby/)

### sum

```kotlin:ank:playground
import arrow.aql.*
import arrow.aql.box.select.*
import arrow.aql.box.sum.*

fun main(args: Array<String>) {
//sampleStart
val result =
  Box.Full(1).query {
    selectAll() sum { this.toLong() }
  }.value()
//sampleEnd
println(result)
}
```

[More on __sum__](/docs/aql/sum/)

### union

```kotlin:ank:playground
import arrow.aql.*
import arrow.aql.box.select.query
import arrow.aql.box.select.selectAll
import arrow.aql.box.union.union
import arrow.aql.extensions.list.select.value
import arrow.aql.box.where.where

fun main(args: Array<String>) {
//sampleStart
val composition =
  Box.Full(1).query {
    selectAll() where { this == 1 }
  } union Box.Full(2).query {
    selectAll() where { this == 2 }
  }
//sampleEnd
println(composition.value())
}
```

[More on __union__](/docs/aql/union/)

Learn more about the `AQL` combinators

- [_select_](/docs/aql/select/)
- [_from_](/docs/aql/from/)
- [_where_](/docs/aql/where/)
- [_groupBy_](/docs/aql/groupby/)
- [_orderBy_](/docs/aql/orderby/)
- [_sum_](/docs/aql/sum/)
- [_union_](/docs/aql/union/)