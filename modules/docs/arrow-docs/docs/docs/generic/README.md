---
layout: docs
title: Generic
permalink: /docs/generic/
---

## Arrow Generic

`arrow-generic` provides meta programming facilities over Product types like data classes, tuples, and heterogeneous lists; and Coproduct types like sealed classes.

### Install 

```groovy
compile 'io.arrow-kt:arrow-generic:$arrow_version'
```

### Features

#### @product

We refer to data classes, tuples, and heterogeneous lists as Product types because they all represent a container of typed values in which all those values need to be present.

That is to say that in the following data class both `balance` and `available` are properties of the `Account` class are typed and guaranteed to always be present.

```kotlin
@product
data class Account(val balance: Int, val available: Int)
```

Because of such properties we can automatically derive interesting behaviors from our data classes by using the `@product` annotation:

#### Extensions

##### + operator

```kotlin:ank
import docs.*

Account(1000, 900) + Account(1000, 900)
```

##### combineAll

```kotlin:ank
listOf(Account(1000, 900), Account(1000, 900)).combineAll()
```

##### tupled

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

Map independent values in the context of any `Applicative` capable data type straight to the data class inside the data type context

```kotlin:ank
val maybeBalance: Option<Int> = Option(1000)
val maybeAvailable: Option<Int> = Option(900)

Option.applicative() { 
  mapToPerson(maybeBalance, maybeAvailable)
}
```

```kotlin:ank
val maybeBalance: Option<Int> = Option(1000)
val maybeAvailable: Option<Int> = None

Option.applicative() { 
  mapToPerson(maybeBalance, maybeAvailable)
}
```

```kotlin:ank
val tryBalance: Try<Int> = Try { 1000 }
val tryAvailable: Try<Int> = Try { throw RuntimeException("BOOM") }

Try.applicative() { 
  mapToPerson(tryBalance, tryAvailable)
}
```

#### Typeclass instances

##### Semigroup 

```kotlin:ank
Account.semigroup() {
  Account(1000, 900).combine(Account(1000, 900))
}
```

##### Monoid 

```kotlin:ank
emptyAccount()
```

```kotlin:ank
Account.monoid().empty()
```

##### Eq 

```kotlin:ank
Account.eq() {
  Account(1000, 900).eqv(Account(1000, 900))
}
```

```kotlin:ank
Account.eq() {
  Account(1000, 900).neqv(Account(1000, 900))
}
```

##### Show 

```kotlin:ank
Account.show() {
  Account(1000, 900).show()
}
```

#### Creating instances for custom properties

Sometimes you may be in need of creating type class for custom properties that Arrow does not provide by default.

In the following example our `Car` data class contains a `maxSpeed: Speed` property for a custom type.

We will go through the basics of enabling auto derivation of `Semigroup`, `Monoid`, `Eq` and `Show` for `Car` as long as we also have instances for `Speed`.

```kotlin
data class Speed(val kmh: Int)

@product
data class Car(val mod: Int, val speed: Speed)
```