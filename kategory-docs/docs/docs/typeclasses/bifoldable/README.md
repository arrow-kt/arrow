---
layout: docs
title: Bifoldable
permalink: /docs/typeclasses/bifoldable/
---

## Bifoldable

Bifoldable is a generalisation of [`Foldable`]({{ '/docs/typeclasses/foldable' | relative_url }}) for structures containing multiple types, where [`Foldable`]({{ '/docs/typeclasses/foldable' | relative_url }}) can just fold for a single type.

### Main Combinators

#### bifoldLeft

Combines the elements of types A and B in a single structure to a type C, with left associativity. 
It requires a function to map from A and B to a common type C, from an starting value.

#### bifoldRight

Combines the elements of types A and B in a single structure to a type C.
To enable right associativity with stack safety it uses [`Eval`]({{ '/docs/datatypes/eval' | relative_url }}).
It requires a function to map from A and B to a common type C, from an starting value.

#### bifoldMap

Combines the elements of types A and B of a single structure using a Monoid.
It requires a function to map from A and B to the Monoid.

### Laws

Kategory provides [`BifoldableLaws`][bifoldable_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own `Bifoldable` instances.

### Data types

The following datatypes in Kategory provide instances that adhere to the `BifoldableLaws` typeclass.

[ComposedBifoldable]()

[bifoldable_laws_source]: https://github.com/kategory/kategory/blob/master/kategory-test/src/main/kotlin/kategory/laws/FunctorLaws.kt