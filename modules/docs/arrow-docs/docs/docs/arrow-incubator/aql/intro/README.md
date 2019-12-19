---
layout: docs-incubator
title: The Λrrow Query Language
permalink: /docs/aql/intro/
---




## The Λrrow Query Language

The **Λ**rrow **Q**uery **L**anguage is a SQL inspired language that works over all data types from which you may want to select and transform data.

This includes heterogeneous use cases such as [selecting data from async computations](#), [transforming and filtering collections](#), [traversing and querying JSON trees](#) and much more.

The generality and level of abstractions of **AQL** comes from the fact that it relies on the [Core FP Type Classes] which model in a generic way using higher kinded types the rules of composition used when describing computation and data transformations.
 These transformations such as folds, reductions, monad binding and in general most of the popular patterns used in Typed Functional Programming are polymorphic and operate over types of the shape `Kind<F, A>`. That is _for all_ `F` that may contain a value of `A` provided it satisfies the typed constrains as expressed in the type classes behind each of the AQL combinators.

It's easier done than said. Observe how the same expression below is able to query different data types without changes in the language used to do so in a type safe and elegant fashion:

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

This is possible because each one of the SQL like operations have a direct dependency to
the combinators we frequently use in the Functional Type Classes. In this case **Select** is a [Type Class]() which delegates selection to the [`Functor`]() instance that all `Option`, `List`, `Eval` and many other data types that can provide a `map` function are able to implement.

Learn more about the `AQL` combinators

- [_select_](/docs/aql/select/)
- [_from_](/docs/aql/from/)
- [_where_](/docs/aql/where/)
- [_groupBy_](/docs/aql/groupby/)
- [_orderBy_](/docs/aql/orderby/)
- [_sum_](/docs/aql/sum/)
- [_union_](/docs/aql/union/)




[Adapt AQL to your own _custom data types_](/docs/aql/custom/)