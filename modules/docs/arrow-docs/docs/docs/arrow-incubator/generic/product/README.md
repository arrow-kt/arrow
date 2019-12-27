---
layout: docs-incubator
title: Generic
permalink: /docs/generic/product/
---

## Arrow Generic


`arrow-generic` provides meta programming facilities over Product types like data classes, tuples, and heterogeneous lists.

### Install

```groovy
implementation "io.arrow-kt:arrow-generic:$arrow_version"
```

### Features

#### @product

We refer to data classes, tuples, and heterogeneous lists as Product types because they all represent a container of typed values in which all those values need to be present.

That is to say that in the following data class both `balance` and `available` are properties of the `Account` class and both are typed and guaranteed to always be present within an `Account`

```kotlin
@product
data class Account(val balance: Int, val available: Int) {
  companion object
}
```

All `@product` annotated data classes must include a `companion object` so that codegen can be properly expanded as extension functions to the companion.

Because of such properties we can automatically derive interesting behaviors from our data classes by using the `@product` annotation:

#### Extensions

`@product` automatically derives instances for `Semigroup` and `Monoid` supporting recursion in declared data types. In the example below we are able to `+` two `Account` objects because the instance `Int.semigroup()` is provided by Arrow.

##### + operator

```kotlin:ank
import arrow.core.*
import arrow.generic.*

Account(1000, 900) + Account(1000, 900)
```

##### combineAll

`@product` enables also syntax over `List<Account>` to reduce `(List<Account>) -> Account` automatically based also on the `Semigroup` instance, `@product` expects already defined instances for all contained data types which for most basic primitives Arrow already provides and for custom data types can be manually generated or automatically derived by Arrow with `@product`

```kotlin:ank
listOf(Account(1000, 900), Account(1000, 900)).combineAll()
```

##### tupled

`@product` enables `Account#tupled` and `Tuple2#toAccount` extensions automatically to go back and forth between the data class values to tuple representations such as `Tuple2` with the same arity and property types as those declared in the data class for all data classes with at least 2 properties.

```kotlin:ank
Account(1000, 900).tupled()
```

```kotlin:ank
Account(1000, 900).tupledLabeled()
```

```kotlin:ank
Tuple2(1000, 900).toAccount()
```

##### toHList

`@product` enables `Account#toHList` and `HList2#toAccount` extensions automatically to go back and forth between the data class value to a heterogeneous list representation such as `HList2` with the same arity and property types as those declared in the data class regardless of the number of properties.

```kotlin:ank
Account(1000, 900).toHList()
```

```kotlin:ank
Account(1000, 900).toHListLabeled()
```

```kotlin:ank
hListOf(1000, 900).toAccount()
```

##### Applicative#mapTo___

`@product` allows us map independent values in the context of any `Applicative` capable data type straight to the data class inside the data type context

In the examples below we can observe how 2 different `Int` properties are returned inside a type constructor such as `Option`, `Try`, `IO` etc... and the automatically mapped to the shape of our `Account` data class removing all boilerplate from extracting the values from their context and returning an `Account` value in the same context.

```kotlin:ank
import arrow.core.extensions.option.applicative.applicative

val maybeBalance: Option<Int> = Option(1000)
val maybeAvailable: Option<Int> = Option(900)

Option.applicative().run {
  mapToAccount(maybeBalance, maybeAvailable)
}
```

```kotlin:ank
val maybeBalance: Option<Int> = Option(1000)
val maybeAvailable: Option<Int> = None

Option.applicative().run {  
  mapToAccount(maybeBalance, maybeAvailable)
}
```

```kotlin:ank
import arrow.core.extensions.`try`.applicative.applicative

val tryBalance: Try<Int> = Try { 1000 }
val tryAvailable: Try<Int> = Try { 900 }

Try.applicative().run {
  mapToAccount(tryBalance, tryAvailable)
}
```

```kotlin:ank
val tryBalance: Try<Int> = Try { 1000 }
val tryAvailable: Try<Int> = Try { throw RuntimeException("BOOM") }

Try.applicative().run {
  mapToAccount(tryBalance, tryAvailable)
}
```

```kotlin:ank
import arrow.fx.*
import arrow.fx.extensions.io.applicative.applicative

val asyncBalance: IO<Nothing, Int> = IO { 1000 }
val asyncAvailable: IO<Nothing, Int> = IO { 900 }

IO.applicative().run {  
  mapToAccount(asyncBalance, asyncAvailable)
}
```

#### Typeclass instances

##### Semigroup

Combine and reduce a data class based on it's internal properties reduction and combination properties as defined by their `Semigroup` instance.

```kotlin:ank
with(Account.semigroup()) {
  Account(1000, 900).combine(Account(1000, 900))
}
```

##### Monoid

Extends `Semigroup` by providing the concept of absent or empty value. It derives it's empty value based on the empty value of each one of it's contained properties.

```kotlin:ank
emptyAccount()
```

```kotlin:ank
Account.monoid().empty()
```

##### Eq

Structural equality in terms of `Eq`, a type class that represents equality.

```kotlin:ank
with(Account.eq()) {
  Account(1000, 900).eqv(Account(1000, 900))
}
```

```kotlin:ank
with(Account.eq()) {
  Account(1000, 900).neqv(Account(1000, 900))
}
```

##### Show

`toString` as a type class: `Show`

```kotlin:ank
with(Account.show()) {
  Account(1000, 900).show()
}
```

#### Creating instances for custom properties

Sometimes you may be in need of creating type class instances for custom properties that Arrow does not provide by default.

In the following example our `Car` data class contains a `maxSpeed: Speed` property for a custom type.

Arrow can auto derive `Semigroup`, `Monoid`, `Eq` and `Show` for `Car` as long as we also have instances for `Speed`.

```kotlin
data class Speed(val kmh: Int) {
  companion object
}

@product
data class Car(val mod: Int, val speed: Speed) {
  companion object
}
```

Once we attempt to compile this we would get an error similar to the one below:

```$xslt
:arrow-docs:compileKotlin: /home/raulraja/workspace/arrow/arrow/modules/docs/arrow-docs/build/generated/source/kaptKotlin/main/product.arrow.generic.car.kt: (60, 119): Unresolved reference.
```

This is because `Speed` is a data class not flagged as `@product`. Let's fix that:

```kotlin
@product
data class Speed(val kmh: Int) {
  companion object
}

@product
data class Car(val mod: Int, val speed: Speed) {
  companion object
}
```

The reason the code compiles now is that Arrow was able to complete the instance for `Car` once we proved we had one for `Speed`.

Now that `Speed` is also flagged as `@product` its `Semigroup`, `Monoid`, `Show` and `Eq` instances are available and visible in `Car`

```kotlin:ank
Speed(50) + Speed(50)
```

```kotlin:ank
Car(Speed(50)) + Car(Speed(50))
```
