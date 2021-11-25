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