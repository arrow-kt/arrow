---
layout: docs
title: Foldable
permalink: /docs/typeclasses/foldable/
---

## Foldable

{:.intermediate}
intermediate

The Typeclass `Foldable` provide us the ability of, given a type `F<A>`, fold their values `<A>`.

`Foldable<F>` is implemented in terms of two basic methods:

- `foldLeft(fa, b)(f)` eagerly folds `fa` from left-to-right.
- `foldRight(fa, b)(f)` lazily folds `fa` from right-to-left.

Beyond these it provides many other useful methods related to folding over `F<A>` values.

### FoldLeft
Left associative fold on `F` using the provided function.

```kotlin
```

### FoldRight
Right associative lazy fold on `F` using the provided function.

This method evaluates `lb` lazily, and returns a lazy value.

For more detailed information about how this method works see the documentation for [`Eval<A>`]({{ '/docs/datatypes/eval' | relative_url }}).

```kotlin
```

### Fold
Fold implemented using the given `Monoid<A>` instance.

```kotlin
```

Besides we have `combineAll` which is an alias for fold.

```kotlin
```

### ReduceLeftToOption

```kotlin
```

### ReduceRightToOption

```kotlin
```

### ReduceLeftOption
Reduce the elements of this structure down to a single value by applying the provided aggregation function in
a left-associative manner.

Return None if the structure is empty, otherwise the result of combining the cumulative left-associative result
of the f operation over all of the elements.

```kotlin
```

### ReduceRightOption
Reduce the elements of this structure down to a single value by applying the provided aggregation function in
a right-associative manner.

Return None if the structure is empty, otherwise the result of combining the cumulative right-associative
result of the f operation over the A elements.

```kotlin
```

### FoldMap
Fold implemented by mapping A values into B and then combining them using the given `Monoid<B>` instance.

```kotlin
```

### Find
Find the first element matching the predicate, if one exists.

```kotlin
```

### Exists
Check whether at least one element satisfies the predicate.

If there are no elements, the result is false.

```kotlin
```

### ForAll
Check whether all elements satisfy the predicate.

If there are no elements, the result is true.

```kotlin
```

### IsEmpty
Returns true if there are no elements. Otherwise false.

```kotlin
```

### NonEmpty
```kotlin
```

### Size
The size of this `Foldable`.

This is overriden in structures that have more efficient size implementations
(e.g. Vector, Set, Map).

Note: will not terminate for infinite-sized collections.

```kotlin
```

### FoldMapM
Monadic folding on `F` by mapping `A` values to `G<B>`, combining the `B` values using the given `Monoid<B>` instance.

Similar to `foldM`, but using a `Monoid<B>`.
```kotlin
```

### FoldM
Left associative monadic folding on `F`.

The default implementation of this is based on `foldL`, and thus will always fold across the entire structure.
Certain structures are able to implement this in such a way that folds can be short-circuited (not traverse the
entirety of the structure), depending on the `G` result produced at a given step.

```kotlin
```

### Get
Get the element at the index of the Foldable.

```kotlin
```

### Data Types

The following data types in Arrow provide instances that adhere to the `Foldable` type class.

- [Id]({{ '/docs/datatypes/id' | relative_url }})
- [Ior]({{ '/docs/datatypes/ior' | relative_url }})
- [NonEmptyList]({{ '/docs/datatypes/nonemptylist' | relative_url }})
- [Option]({{ '/docs/datatypes/option' | relative_url }})
- [OptionT]({{ '/docs/datatypes/optiont' | relative_url }})
- [SequenceK]({{ '/docs/datatypes/sequencek' | relative_url }})
- [SetK]({{ '/docs/datatypes/setk' | relative_url }})
- [Try]({{ '/docs/datatypes/try' | relative_url }})
- [Validated]({{ '/docs/datatypes/validated' | relative_url }})
- [Either]({{ '/docs/datatypes/either' | relative_url }})
- [EitherT]({{ '/docs/datatypes/eithert' | relative_url }})
