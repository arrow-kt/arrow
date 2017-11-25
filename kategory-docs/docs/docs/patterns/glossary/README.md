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

Some common patterns expressed as datatypes are absence handling with [`Option`]({{ '/docs/datatypes/option' | relative_url }}),
branching in code with [`Either`]({{ '/docs/datatypes/either' | relative_url }}),
catching exceptions with [`Try`]({{ '/docs/datatypes/try' | relative_url }}),
or interacting with the platform the program runs in using [`IO`]({{ '/docs/effects/io' | relative_url }}).

### Typeclasses

A typeclass is an interface representing one behavior associated with a type.
Examples of this behavior are comparison ([`Eq`]({{ '/docs/typeclasses/eq' | relative_url }})),
composability ([`Monoid`]({{ '/docs/typeclasses/monoid' | relative_url }})),
its contents are mappable ([`Functor`]({{ '/docs/typeclasses/functor' | relative_url }})),
or error recovery ([`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }})).

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
