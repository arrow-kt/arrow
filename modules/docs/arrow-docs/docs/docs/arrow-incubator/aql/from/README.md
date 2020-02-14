---
layout: docs-incubator
title: from
permalink: /aql/from/
---

## from




AQL does not require of a `from` clause to operate because the data source with the shape `Kind<F, A>` is already used as the initial point to compose a query.

The example below shows what a classic SQL statement looks like in AQL.

__SQL__
```roomsql
select * from list
```

__AQL__

```kotlin:ank:playground
import arrow.aql.extensions.list.select.*
import arrow.aql.extensions.listk.select.select
fun main(args: Array<String>) {
//sampleStart
val result: List<Int> =
  listOf(1, 2, 3).query { // `listOf(1, 2, 3)` is what the source of data and what we use as `from`
    select { this }
  }.value()
//sampleEnd
println(result)
}
```

Learn more about the `AQL` combinators

- [_select_]({{'/aql/select/' | relative_url }})
- [_from_]({{'/aql/select/' | relative_url }})
- [_where_]({{'/aql/where/' | relative_url }})
- [_groupBy_]({{'/aql/groupby/' | relative_url }})
- [_orderBy_]({{'/aql/orderby/' | relative_url }})
- [_sum_]({{'/aql/sum/' | relative_url }})
- [_union_]({{'/aql/union/' | relative_url }})

### Supported Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.aql.*

TypeClass(From::class).dtMarkdownList()
```




[Adapt AQL to your own _custom data types_]({{'/aql/custom/' | relative_url }})
