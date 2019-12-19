---
layout: docs-incubator
title: where
permalink: /docs/aql/where/
---




## where

`where` allows filtering data from any given data source providing a predicate function of the shape `(A) -> Boolean`.
Whenever `true` is returned from an expression affecting one of the items in the data source the item is collected and included in the selection.

`where` over `List`

```kotlin:ank:playground
import arrow.aql.extensions.list.select.*
import arrow.aql.extensions.list.where.*
import arrow.aql.extensions.listk.select.select

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


`where` works with any data type that provides an instance of `FunctorFilter<F>` where `F` is the higher kinded representation of the data type. For example `ForOption` when targeting the `Option<A>` data type or `ForListK` when targeting the `List<A>` data type

Learn more about the `AQL` combinators

- [_select_](/docs/aql/select/)
- [_from_](/docs/aql/from/)
- [_where_](/docs/aql/where/)
- [_groupBy_](/docs/aql/groupby/)
- [_orderBy_](/docs/aql/orderby/)
- [_sum_](/docs/aql/sum/)
- [_union_](/docs/aql/union/)

### Supported Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.aql.*

TypeClass(Where::class).dtMarkdownList()
```




[Adapt AQL to your own _custom data types_](/docs/aql/custom/)
