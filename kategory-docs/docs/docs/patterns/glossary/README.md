---
layout: docs
title: Functional Programming Glossary
permalink: /docs/patterns/glossary/
---

## Functional Programming Glossary

TODO: expand terms and usage

### Datatypes

A datatype is a class that encapsulates one reusable coding pattern.
These solutions have a canonical implementation that is generalised for all possible uses.

Some common patterns expressed as datatypes are absence handling with `Option`,
branching in code with `Either`,
catching exceptions with `Try`,
or interacting with the platform the program runs in using `IO`.

### Typeclasses

A typeclass is an interface representing one behavior associated with a type.
Examples of this behavior are comparison (`Eq`), composability (`Monoid`), its contents are mappable (`Functor`), or error recovery (`MonadError`).

```
interface Eq<F>: Typeclass {
  fun eqv(a: F, b: F): Boolean
}
```

What differentiates typeclasses from regular interfaces is that they are meant to be created at a global scope for a single type.
The association is done using generic parametrization rather than the usual subclassing. This means that they can be implemented for any class, even those not in the current project.

### Instances

A single implementation of a typeclass for a specific datatype or class.
Because typeclasses require generic parameters each implementation is meant to be unique for that parameter.

```kotlin
@instance
object EqIntInstance: Eq<Int> {
  override fun eqv(a: Int, b: Int): Boolean = a == b
}
```

In KΛTEGORY all typeclass instances can be looked up using a method with the same name as the typeclass.
As long as the instance is defined and exists in the global namespace the lookup will succeed.

```
val EQ_INT: Eq<Int> = eq()
```
