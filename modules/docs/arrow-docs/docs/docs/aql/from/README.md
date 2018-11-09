---
layout: docs
title: from
permalink: /docs/aql/from/
---

## from

{:.beginner}
beginner

AQL does not require of a `from` clause to operate because the data source with the shape `Kind<F, A>` is already used as the initial point to compose a query.

The example below shows what a classic SQL statement looks like in AQL.

__SQL__
```roomsql
select * from list
```

__AQL__

{: data-executable='true'}
```kotlin:ank
import arrow.aql.instances.list.select.*
import arrow.aql.instances.listk.select.select
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
