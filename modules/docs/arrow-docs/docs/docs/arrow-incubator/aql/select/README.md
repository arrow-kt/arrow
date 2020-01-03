---
layout: docs-incubator
title: select
permalink: /docs/aql/select/
---




## select

`select` allows obtaining and transforming data from any data source containing `A` given a function `(A) -> B` where `A` denotes the input type and `B` the transformed type.

`select` over `List`

```kotlin:ank:playground
import arrow.aql.extensions.list.select.*
import arrow.aql.extensions.listk.select.select
fun main(args: Array<String>) {
//sampleStart
val result: List<Int> =
  listOf(1, 2, 3).query {
    select { this + 1 }
  }.value()
//sampleEnd
println(result)
}
```

`select` over `Option`

```kotlin:ank:playground
import arrow.core.Option
import arrow.aql.extensions.option.select.*

fun main(args: Array<String>) {
//sampleStart
val result: Option<Int> =
  Option(1).query {
    select { this * 10 }
  }.value()
//sampleEnd
println(result)
}
```

`select` over `Sequence`

```kotlin:ank:playground
import arrow.aql.extensions.sequence.select.*
import arrow.aql.extensions.sequencek.select.select

fun main(args: Array<String>) {
//sampleStart
val result: List<Int> =
  sequenceOf(1, 2, 3, 4).query {
    select { this * 10 }
  }.value().toList()
//sampleEnd
  println(result)
}
```


`select` works with any data type that provides an instance of `Functor<F>` where `F` is the higher kinded representation of the data type. For example `ForOption` when targeting the `Option<A>` data type or `ForListK` when targeting the `List<A>` data type

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

TypeClass(Select::class).dtMarkdownList()
```




[Adapt AQL to your own _custom data types_](/docs/aql/custom/)
