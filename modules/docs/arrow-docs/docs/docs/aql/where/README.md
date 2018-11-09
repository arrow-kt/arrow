---
layout: docs
title: where
permalink: /docs/aql/where/
---

{:.beginner}
beginner

## where

`where` allows filtering data from any given data source providing a predicate function of the shape `(A) -> Boolean`.
Whenever `true` is returned from an expression affecting one of the items in the data source the item is collected and included in the selection.

`where` over `List`
{: data-executable='true'}
```kotlin:ank
import arrow.aql.instances.list.select.*
import arrow.aql.instances.list.where.*
import arrow.aql.instances.listk.select.select

data class Student(val name: String, val age: Int)

val john = Student("John", 30)
val jane = Student("Jane", 32)
val jack = Student("Jack", 32)

fun main(args: Array<String>) {
//sampleStart
val result: List<String> = 
  listOf(john, jane, jack).query {
    select { name } where { age > 20 }
  }.value()
//sampleEnd
println(result)
}
```

{:.intermediate}
intermediate

`where` works with any data type that provides an instance of `FunctorFilter<F>` where `F` is the higher kinded representation of the data type. For example `ForOption` when targeting the `Option<A>` data type or `ForListK` when targeting the `List<A>` data type
