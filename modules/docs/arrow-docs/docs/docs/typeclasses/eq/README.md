---
layout: docs
title: Eq
permalink: /docs/typeclasses/eq/
---

## Eq

The `Eq` typeclass abstracts the ability to compare two instances of any object.
Depending on your needs this comparison can be structural -the content of the object-, referential -the memory address of the object-, based on an identity -like an Id fields-, or any combination of the above.

It can be considered the typeclass equivalent of Java's `Object#equals`.

```kotlin:ank
import arrow.*
import arrow.instances.*

StringEqInstance.run { "1".eqv("2") }
```

### Main Combinators

#### F.eqv

Compares two instances of `F` and returns true if they're considered equal for this instance.
It is the opposite comparison of `neqv`.

`fun F.eqv(b: F): Boolean`


```kotlin:ank
IntEqInstance.run { 1.eqv(2) }
```

#### neqv

Compares two instances of `F` and returns true if they're not considered equal for this instance.
It is the opposite comparison of `eqv`.

`fun neqv(a: F, b: F): Boolean`

```kotlin:ank
IntEqInstance.run { 1.neqv(2) }
```

### Laws

Arrow provides [`EqLaws`]({{ '/docs/typeclasses/laws#eqlaws' | relative_url }}) in the form of test cases for internal verification of lawful instances and third party apps creating their own `Eq` instances.

#### Creating your own `Eq` instances

Eq provides one special instance that can be potentially applicable to most datatypes.
It uses kotlin's == comparison to compare any two instances.
Note that this instance will fail on many all datatypes that contain a property or field that doesn't implement structural equality, i.e. functions, typeclasses, non-data classes

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

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Eq` instances for custom datatypes.

### Data types

Most of the datatypes in Arrow that are not related to functions provide instances of the `Eq` typeclass.

- [`Id`]({{ '/docs/datatypes/id/' | relative_url }})
- [`Option`]({{ '/docs/datatypes/option/' | relative_url }})
- [`Either`]({{ '/docs/datatypes/either/' | relative_url }})
- [`Eval`]({{ '/docs/datatypes/eval/' | relative_url }})
- `TupleN`
- [`NonEmptyList`]({{ '/docs/datatypes/nonemptylist/' | relative_url }})
- [`Ior`]({{ '/docs/datatypes/ior/' | relative_url }})
- [`Const`]({{ '/docs/datatypes/const/' | relative_url }})
- [`Coproduct`]({{ '/docs/datatypes/coproduct/' | relative_url }})
- [`Try`]({{ '/docs/datatypes/try/' | relative_url }})
- [`Validated`]({{ '/docs/datatypes/validated/' | relative_url }})
- [`Free`]({{ '/docs/datatypes/free' | relative_url }})
- [`FreeApplicative`]({{ '/docs/datatypes/FreeApplicative' | relative_url }})
- [`ListK`]({{ '/docs/datatypes/listK/' | relative_url }})
- [`SequenceK`]({{ '/docs/datatypes/sequenceK/' | relative_url }})
- [`SetK`]({{ '/docs/datatypes/setK/' | relative_url }})
- [`MapK`]({{ '/docs/datatypes/mapK/' | relative_url }}) 
- [`SortedMapK`]({{ '/docs/datatypes/sortedmapK/' | relative_url }})

Additionally all instances of [`Order`]({{ '/docs/typeclasses/order' | relative_url }}) and their MTL variants implement the `Eq` typeclass directly since they are all subtypes of `Eq`
