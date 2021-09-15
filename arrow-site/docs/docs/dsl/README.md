---
layout: docs-optics
title: Syntax DSL
permalink: /optics/dsl/
---

## Optics DSL


Arrow offers an Optics DSL to compose different Optics while improving ease of use and readability.
To avoid boilerplate, Arrow will generate this property-like DSL using `@optics` annotation. For Arrow Optics to generate the DSL all the annotated classes should declare a `companion object` where the optics properties will be projected as extensions.

```kotlin
package com.example.domain

@optics data class Street(val number: Int, val name: String) {
  companion object
}
@optics data class Address(val city: String, val street: Street) {
  companion object
}
@optics data class Company(val name: String, val address: Address) {
  companion object
}
@optics data class Employee(val name: String, val company: Company?) {
  companion object
}
```

The DSL will be generated in the same package as your `data class`, and can be used on the `Companion` of your class.

```kotlin:ank
import arrow.optics.dsl.*
import com.example.domain.*
import arrow.optics.Optional

val john = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))

val optional: Optional<Employee, String> = Employee.company.address.street.name
optional.modify(john, String::toUpperCase)
```

Arrow can also generate DSL for a `sealed class`, which can help reduce boilerplate code, or improve readability.

```kotlin
package com.example.domain

@optics sealed class NetworkResult {
  companion object
}
@optics data class Success(val content: String): NetworkResult() {
  companion object
}
@optics sealed class NetworkError : NetworkResult() {
  companion object
}
@optics data class HttpError(val message: String): NetworkError() {
  companion object
}
object TimeoutError: NetworkError()
```

Let's imagine we have a function `f` of type `(HttpError) -> HttpError`, and we want to invoke it on the `NetworkResult`.

```kotlin:ank
val networkResult: NetworkResult = HttpError("boom!")
val f: (String) -> String = String::toUpperCase

when (networkResult) {
  is HttpError -> networkResult.copy(f(networkResult.message))
  else -> networkResult
}
```

We can rewrite this code with our generated DSL.

```kotlin:ank
NetworkResult.networkError.httpError.message.modify(networkResult, f)
```

The DSL also has special support for [Every]({{ '/optics/every' | relative_url }}), [At]({{ '/optics/at' | relative_url }}), [Index]({{ '/optics/index' | relative_url }}), etc.

### Every

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

### At

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

### Index

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
