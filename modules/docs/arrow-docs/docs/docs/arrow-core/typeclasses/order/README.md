---
layout: docs-core
title: Order
permalink: /docs/arrow/typeclasses/order/
redirect_from:
  - /docs/typeclasses/order/
---

## Order




The `Order` typeclass abstracts the ability to compare two instances of any object and determine their total order.
Depending on your needs, this comparison can be structural (the content of the object), referential (the memory address of the object), based on an identity (like an Id field), or any combination of these.

It can be considered the typeclass equivalent of Java's `Comparable`.

### Main Combinators

#### F#compare

`fun F.compare(b: F): Int`

Compare [a] with [b]. Returns an Int whose sign is:
  * negative if `x < y`
  * zero     if `x = y`
  * positive if `x > y`

```kotlin:ank
import arrow.*
import arrow.typeclasses.*
import arrow.core.extensions.*

Int.order().run { 1.compare(2) }
```

Additionally, `Order` overloads operators `>`, `<`, `<=`, and `>=`, following the Kotlin `compareTo` convention for every type where an `Order` instance exists.

```kotlin:ank
Int.order().run { 1 > 2 }
```  

#### F#lte / F#lt

Lesser than or equal to defines total order in a set, it compares two elements, and returns true if they're equal or the first is lesser than the second.
It is the opposite of `gte`.

```kotlin:ank

Int.order().run {
  1.lte(2)
}
```

#### F#gte / F#gt

Greater than or equal compares two elements and returns true if they're equal or the first is lesser than the second.
It is the opposite of `lte`.

```kotlin:ank
Int.order().run {
  1.gte(2)
}
```

#### F#max / F#min

Compares two elements and respectively returns the maximum or minimum in respect to their order.

```kotlin:ank
Int.order().run {
  1.min(2)
}
```
```kotlin:ank
Int.order().run {
  1.max(2)
}
```

#### F#sort

Sorts the elements in a `Tuple2`.

```kotlin:ank
Int.order().run {
  1.sort(2)
}
```

### Laws

Arrow provides `OrderLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `Order` instances.

#### Creating your own `Order` instances

Order has a constructor to create an `Order` instance from a compare function `(F, F) -> Int`.

```kotlin:ank

Order { a: Int, b: Int -> b - a }.run {
  1.lt(2)
}
```

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Order` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Order

TypeClass(Order::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Order)
