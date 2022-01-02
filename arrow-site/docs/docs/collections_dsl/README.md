---
layout: docs-optics
title: Syntax DSL
permalink: /optics/collections_dsl/
---

## Optics DSL for Collections

The Optics DSL has special support for optics that refer to elements in a collection.

### [Every]({{ '/optics/every' | relative_url }})

`Every` can be used to focus into a structure `S` and see all its foci `A`. Here, we focus into all `Employee`s in the `Employees`.

```kotlin
@optics data class Employees(val employees: List<Employee>) {
  companion object
}
```

```kotlin:ank
import arrow.optics.Every

val jane = Employee("Jane Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))
val employees = Employees(listOf(john, jane))

Employees.employees.every(Every.list<Employee>()).company.address.street.name.modify(employees, String::capitalize)
```

If you are in the scope of `Each`, you don't need to specify the instance.

```kotlin:ank
Every.list<Employee>().run {
  Employees.employees.every.company.address.street.name.modify(employees, String::capitalize)
}
```

### [At]({{ '/optics/at' | relative_url }})

`At` can be used to focus in `A` at a given index `I` for a given structure `S`.

```kotlin
@optics data class Db(val content: Map<Int, String>) {
  companion object
}
```

Here we focus into the value of a given key in `MapK`.

```kotlin:ank
import arrow.optics.typeclasses.At

val db = Db(mapOf(
  1 to "one",
  2 to "two",
  3 to "three"
))

Db.content.at(At.map(), 2).some.modify(db, String::reversed)
```

If you are in the scope of `At`, you don't need to specify the instance.

```kotlin:ank
At.map<Int, String>().run {
  Db.content.at(2).some.modify(db, String::reversed)
}
```

### [Index]({{ '/optics/index' | relative_url }})

`Index` can be used to operate on a structure `S` that can index `A` by an index `I` (i.e., a `List<Employee>` by its index position or a `Map<K, V>` by its keys `K`).


```kotlin:ank
import arrow.optics.typeclasses.Index

val updatedJohn = Employees.employees.index(Index.list(), 0).company.address.street.name.modify(employees, String::capitalize)
updatedJohn
```

In the scope of `Index`, you don't need to specify the instance, so we can enable `operator fun get` syntax.

```kotlin:ank
Index.list<Employee>().run {
  Employees.employees[0].company.address.street.name.getOrNull(updatedJohn)
}
```

Since [Index]({{ '/optics/index' | relative_url }}) returns an [Optional]({{ '/optics/optional' | relative_url }}), `index` and `[]` are safe operations.

```kotlin:ank
Index.list<Employee>().run {
  Employees.employees[2].company.address.street.name.getOrNull(employees)
}
```
