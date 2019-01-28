---
layout: docs
title: Semiring
permalink: /docs/arrow/typeclasses/semiring/
redirect_from:
  - /docs/typeclasses/semiring/
---

## Semiring

{:.beginner}
beginner

The `Semiring` type class for a given type `A` extends the `Monoid` type class by adding a `combineMultiplicate` and an 
`one` function. `combineMultiplicate` also takes two values and returns a value of type `A` and guarantees to be 
associative:

```kotlin
(a.combineMultiplicate(b)).combineMultiplicate(c) == a.combineMultiplicate(b.combineMultiplicate(c))
```

The `one` value serves exactly like the `empty` function for an additive `Monoid`, just adapted for the multiplicative 
version. This forms the following law:

```kotlin
combineMultiplicate(x, one) == combineMultiplicate(one, x) == x
```

Please note that the empty function has been renamed to `zero` to get a consistent naming style inside the semiring.

Currently, `Semiring` instances are defined for all available number types.

### Examples

Here a some examples:

```kotlin:ank
import arrow.*
import arrow.core.extensions.*

Int.semiring().run { 1.combine(2) }
```

```kotlin:ank
import arrow.*
import arrow.core.extensions.*

Int.semiring().run { 2.combineMultiplicate(3) }
```

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.option.semiring.semiring

Option.semiring(Int.semiring()).run {
  Option(1).combine(Option(2))
}
```

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.option.semiring.semiring

Option.semiring(Int.semiring()).run {
  Option(2).combineMultiplicate(Option(3))
}
```

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.option.semiring.semiring

Option.semiring(Int.semiring()).run {
  Option(1).combine(None)
}
```

The type class `Semiring` also has support for the `+` `*` syntax:

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.option.semiring.semiring

Option.semiring(Int.semiring()).run {
  Option(1) + Option(2)
}
```

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.option.semiring.semiring

Option.semiring(Int.semiring()).run {
  Option(2) * Option(3)
}
```

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Semiring

TypeClass(Semiring::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Semiring)
