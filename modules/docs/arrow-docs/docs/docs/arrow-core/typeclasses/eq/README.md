---
layout: docs-core
title: Eq
permalink: /docs/arrow/typeclasses/eq/
redirect_from:
  - /docs/typeclasses/eq/
---

## Eq




The `Eq` typeclass abstracts the ability to compare two instances of any object.
It can be considered the typeclass equivalent of Java's `Object#equals`.

Depending on your needs, this comparison can be structural (the content of the object), referential (the memory address of the object), based on an identity (like an Id fields), or any combination of these.

```kotlin:ank
import arrow.core.extensions.*

// Enable the extension functions inside Eq using run
String.eq().run {
  "1".eqv("2")
}
```

### Main Combinators

#### F.eqv

Compares two instances of `F` and returns true if they're considered equal for this instance.
It is the opposite comparison of `neqv`.

`fun F.eqv(b: F): Boolean`


```kotlin:ank
Int.eq().run { 1.eqv(2) }
```

#### neqv

Compares two instances of `F` and returns true if they're not considered equal for this instance.
It is the opposite comparison of `eqv`.

`fun neqv(a: F, b: F): Boolean`

```kotlin:ank
Int.eq().run { 1.neqv(2) }
```

### Laws

Arrow provides `EqLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `Eq` instances.

#### Creating your own `Eq` instances

Eq provides one special instance that potentially can be applicable to most datatypes.
It uses kotlin's == comparison to compare any two instances.
Note that this instance will fail on many all datatypes that contain a property or field that doesn't implement structural equality, i.e., functions, typeclasses, non-data classes.

```kotlin:ank
import arrow.core.*
import arrow.typeclasses.*

// Option is a data class with a single value
Eq.any().run { Some(1).eqv(Option.just(1)) }
```

```kotlin:ank
// Fails because the wrapped function is not evaluated for comparison
Eq.any().run { Eval.later { 1 }.eqv(Eval.later { 1 }) }
```

```kotlin:ank
// using invoke constructor
val intEq = Eq<Int> { a, b -> a == b }
```

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Eq` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Eq

TypeClass(Eq::class).dtMarkdownList()
```

Additionally, all instances of [`Order`]({{ '/docs/arrow/typeclasses/order' | relative_url }}), [`Hash`]({{ '/docs/arrow/typeclasses/hash' | relative_url }}) and their MTL variants implement the `Eq` typeclass directly since they are all subtypes of `Eq`.

ank_macro_hierarchy(arrow.typeclasses.Eq)
