---
layout: docs-incubator
title: groupBy
permalink: /docs/aql/groupby/
---




## groupBy

`groupBy` allows grouping data from a data source into different keys resulting in a map of keys and values.

`groupBy` over `List`

{:data-executable='true'}
```kotlin:ank
import arrow.aql.extensions.list.select.*
import arrow.aql.extensions.list.where.*
import arrow.aql.extensions.list.groupBy.*
import arrow.aql.extensions.listk.select.selectAll
import arrow.aql.extensions.id.select.value

data class Student(val name: String, val age: Int)

val john = Student("John", 30)
val jane = Student("Jane", 32)
val jack = Student("Jack", 32)

fun main(args: Array<String>) {
//sampleStart
val result =
  listOf(john, jane, jack).query {
    selectAll() where { age > 30 } groupBy { age }
  }.value()
//sampleEnd
println(result)
}
```


`groupBy` works with any data type that provides an instance of `Foldable<F>` where `F` is the higher kinded representation of the data type. For example `ForOption` when targeting the `Option<A>` data type or `ForListK` when targeting the `List<A>` data type

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

TypeClass(GroupBy::class).dtMarkdownList()
```




[Adapt AQL to your own _custom data types_](/docs/aql/custom/)
