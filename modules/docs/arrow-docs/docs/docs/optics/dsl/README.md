---
layout: docs
title: Syntax DSL
permalink: /docs/optics/dsl/
---

## Optics DSL

{:.beginner}
beginner

Arrow offers a Optics DSL to compose different Optics while improving ease of use and readability.
To avoid boilerplate Arrow will generate this property-like dsl using `@optics` annotation.

```kotlin
package com.example.domain

@optics data class Street(val number: Int, val name: String)
@optics data class Address(val city: String, val street: Street)
@optics data class Company(val name: String, val address: Address)
@optics data class Employee(val name: String, val company: Company?)
```

The DSL will be generated in the same package as your `data class` and can be used on the `Companion` of your class. `Companion` definition is omitted here to improve readability.

```kotlin:ank
import arrow.optics.dsl.*
import com.example.domain.*
import arrow.optics.Optional

val john = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))

val optional: Optional<Employee, String> = Employee.company.address.street.name
optional.modify(john, String::toUpperCase)
```

Arrow can also generate dsl for a `sealed class` which can be helpful to reduce boilerplate code, or improve readability.

```kotlin
package com.example.domain

@optics sealed class NetworkResult
@optics data class Success(val content: String): NetworkResult()
@optics sealed class NetworkError : NetworkResult()
@optics data class HttpError(val message: String): NetworkError()
object TimeoutError: NetworkError()
```

Let's imagine we have a function `f` of type `(HttpError) -> HttpError` and we want to invoke it on the `NetworkResult`.

```kotlin:ank
val networkResult: NetworkResult = HttpError("boom!")
val f: (String) -> String = String::toUpperCase

when (networkResult) {
  is HttpError -> networkResult.copy(f(networkResult.message))
  else -> networkResult
}
```

We can rewrite this code with our generated dsl.

```kotlin:ank
NetworkResult.networkError.httpError.message.modify(networkResult, f)
```

Arrow also supports generation of DSL for domain class that are not defined by your project.
This can be done by providing `Optics` for the desired domain classes. For example `Pair` from Kotlin's stdlib.

```kotlin
@optics data class GameBoard(val player: Player)
@optics data class Player(val name: String, val pos: Pair<Long, Long>)

@optics
fun <A, B> first(): Lens<Pair<A, B>, A> = Iso(
  get = { a: Pair<A, B> -> a.first toT a.second },
  reverseGet = { a: Tuple2<A, B> -> a.a to a.b }
) compose Tuple2.first()
```
```kotlin:ank
val board = GameBoard(Player("Simon", 1L to 20L))

fun moveForward(pos: Long) = pos.inc()

val updated = GameBoard.player.pos.first.modify(GameBoard(Player("Simon", 1L to 20L)), ::moveForward)
updated
```

Or you can also use it to create aliases to bend the DSL to your own desire.

```kotlin
@optics
fun xPos(): Lens<Player, Long> = Player.pos.first
```
```kotlin:ank
GameBoard.player.xPos.get(updated)
```

The DSL also has special support for [Each]({{ '/docs/optics/each' | relative_url }}) and [At]({{ '/docs/optics/at' | relative_url }}).

`Each` can be used to focus into a structure `S` and see all its foci `A`. Here we focus into all `Employee`s in the `Employees`.

```kotlin
@optics data class Employees(val employees: ListK<Employee>)
```

```kotlin:ank
import arrow.data.*

val jane = Employee("Jane Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))
val employees = Employees(listOf(john, jane).k())

Employees.employees.every(ListK.each()).company.address.street.name.modify(employees, String::capitalize)
```

If you are in the scope of `Each` you don't need to specify the instance.

```kotlin:ank
ListK.each<Employee>().run {
  Employees.employees.every.company.address.street.name.modify(employees, String::capitalize)
}
```

`At` can be used to focus in `A` at a given index `I` for a given structure `S`.

```kotlin
@optics data class Db(val content: MapK<Int, String>)
```

Here we focus into the value of a given key in `MapK`.

```kotlin:ank
val db = Db(mapOf(
  1 to "one",
  2 to "two",
  3 to "three"
).k())

Db.content.at(MapK.at(), 2).some.modify(db, String::reversed)
```

If you are in the scope of `At` you don't need to specify the instance.

```kotlin:ank
MapK.at<Int, String>().run {
  Db.content.at(2).some.modify(db, String::reversed)
}
```
