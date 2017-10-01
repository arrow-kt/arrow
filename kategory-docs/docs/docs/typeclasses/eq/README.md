---
layout: docs
title: Eq
permalink: /docs/typeclasses/eq/
---

## Eq

The `Eq` typeclass abstracts the ability to compare two instances of any object.
Depending on your needs this comparison can be structural -the content of the object-, referential -the memory address of the object-, based on an identity -like an Id fields-, or any combination of the above.

It can be considered the typeclass equivatent of Java's `Object#equals`.

### Main Combinators

#### eqv

Compares two instances of F and returns true if they're considered equal for this instance.
It is the opposite comparison of `neqv`.

`fun eqv(a: F, b: F): Boolean`

```kotlin:ank
Option.pure(1) // Option(1)
```

#### neqv

Compares two instances of F and returns true if they're not considered equal for this instance.
It is the opposite comparison of `eqv`.

`fun neqv(a: F, b: F): Boolean`

```kotlin:ank
Option.pure(1) // Option(1)
```

### Syntax

#### HK<F, A>#eqv

Lift a value into the computational context of a type constructor

```kotlin:ank
1.eqv(2)
```

#### HK<F, A>#neqv

Apply a function inside the type constructor's context

```kotlin:ank
1.neqv(2)
```

### Laws

Kategory provides [`EqLaws`](/docs/typeclasses/laws#eqlaws) in the form of test cases for internal verification of lawful instances and third party apps creating their own `Eq` instances.

#### Creating your own `Eq` instances

Eq provides one special instance that can be potentially applicable to most datatypes.
It uses kotlin's == comparison to compare any two instances.
Note that this instance will fail on many all datatypes that contain a property or field that doesn't implement structural equality, i.e. functions, typeclasses, non-data classes

```kotlin:ank
Eq.any().eqv(1, 2)
```

```kotlin:ank
Eq.any().eqv(Either.right(1), Either.pure(1))
```

```kotlin:ank
Eq.any().eqv(IO{ 1 }, IO{ 1 })
```

See [Deriving and creating custom typeclass] to provide your own `Eq` instances for custom datatypes.

### Data types

The following datatypes in Kategory provide instances that adhere to the `Eq` typeclass.

- [Free](/docs/datatypes/free)
- [FreeApplicative](/docs/datatypes/FreeApplicative)

Additionally all instances of [`Order`](/docs/_docs/typeclasses/order) and their MTL variants implement the `Eq` typeclass directly
since they are all subtypes of `Eq`
