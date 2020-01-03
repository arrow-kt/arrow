---
layout: docs-core
title: Foldable
permalink: /docs/arrow/typeclasses/foldable/
redirect_from:
  - /docs/typeclasses/foldable/
---

## Foldable




The Typeclass `Foldable` provide us the ability, given a type `Kind<F, A>`, to aggregate their values `A`.

`Foldable<F>` is implemented in terms of two basic methods:

- `fa.foldLeft(init, f)` eagerly folds `fa` from left-to-right.
- `fa.foldRight(init, f)` lazily folds `fa` from right-to-left.

Beyond these, it provides many other useful methods related to folding over `Kind<F, A>` values.

For the following examples, we are going to use some common imports.

```kotlin:ank:silent
import arrow.Kind
import arrow.core.*
import arrow.core.ListK
import arrow.core.k
import arrow.core.extensions.monoid
import arrow.core.extensions.listk.foldable.foldable
import arrow.core.extensions.option.foldable.foldable
import arrow.typeclasses.Foldable
```

And we'll use the same two variables to see the different behaviors of `Foldable`:

```kotlin:ank:silent
val maybeStr: Option<String> = Some("abc")
val strList: ListK<String> = listOf("a", "b", "c").k()
```

### FoldLeft
Left associative fold on `F` using the provided function.

```kotlin:ank:silent
fun <F> concatenateStringFromLeft(strKind: Kind<F, String>, FO: Foldable<F>): String =
  FO.run {
    strKind.foldLeft("str: ") { base: String, value: String -> base + value }
  }
```

```kotlin:ank
concatenateStringFromLeft(maybeStr, Option.foldable())
```

```kotlin:ank
concatenateStringFromLeft(None, Option.foldable())
```

```kotlin:ank
concatenateStringFromLeft(strList, ListK.foldable())
```

### FoldRight
Right associative lazy fold on `F` using the provided function.

This method evaluates `lb` lazily, and returns a lazy value to support laziness in a stack-safe way avoiding StackOverflows.

For more detailed information about how this method works, see the documentation for [`Eval<A>`]({{ '/docs/apidocs/arrow-core-data/arrow.core/-eval' | relative_url }}).

```kotlin:ank:silent
fun <F> concatenateStringFromRight(strKind: Kind<F, String>, FO: Foldable<F>): String =
  FO.run {
    strKind.foldRight(Eval.now("str: ")) { value: String, base: Eval<String> -> base.map { it + value } }
      .value()
  }
```

```kotlin:ank
concatenateStringFromRight(maybeStr, Option.foldable())
```

```kotlin:ank
concatenateStringFromRight(None, Option.foldable())
```

```kotlin:ank
concatenateStringFromRight(strList, ListK.foldable())
```

### Fold
Fold implemented using the given `Monoid<A>` instance.

```kotlin:ank:silent
fun <F> concatenateString(strKind: Kind<F, String>, FO: Foldable<F>): String =
  FO.run {
    "str: " + strKind.fold(String.monoid())
  }
```

```kotlin:ank
concatenateString(maybeStr, Option.foldable())
```

```kotlin:ank
concatenateString(None, Option.foldable())
```

```kotlin:ank
concatenateString(strList, ListK.foldable())
```

Alternatively, we have `combineAll`, which is an alias for fold.

```kotlin:ank:silent
fun <F> combineAllString(strKind: Kind<F, String>, FO: Foldable<F>): String =
  FO.run {
    "str: " + strKind.combineAll(String.monoid())
  }
```

```kotlin:ank
combineAllString(maybeStr, Option.foldable())
```

```kotlin:ank
combineAllString(None, Option.foldable())
```

```kotlin:ank
combineAllString(strList, ListK.foldable())
```

### ReduceLeftToOption

```kotlin:ank:silent
fun <F> reduceLeftToOption(strKind: Kind<F, String>, FO: Foldable<F>): Option<Int> =
  FO.run {
    strKind.reduceLeftToOption({ it.length }) { base: Int, value: String -> base + value.length }
  }
```

```kotlin:ank
reduceLeftToOption(maybeStr, Option.foldable())
```

```kotlin:ank
reduceLeftToOption(None, Option.foldable())
```

```kotlin:ank
reduceLeftToOption(strList, ListK.foldable())
```

### ReduceRightToOption

```kotlin:ank:silent
fun <F> reduceRightToOption(strKind: Kind<F, String>, FO: Foldable<F>): Option<Int> =
  FO.run {
    strKind.reduceRightToOption({ it.length }) { value: String, base: Eval<Int> -> base.map { it + value.length } }
      .value()
  }
```

```kotlin:ank
reduceRightToOption(maybeStr, Option.foldable())
```

```kotlin:ank
reduceRightToOption(None, Option.foldable())
```

```kotlin:ank
reduceRightToOption(strList, ListK.foldable())
```

### ReduceLeftOption
Reduce the elements of this structure down to a single value by applying the provided aggregation function in
a left-associative manner.

Return None if the structure is empty, otherwise the result of combining the cumulative left-associative result
of the f operation over all of the elements.

```kotlin:ank:silent
fun <F> getLengthFromLeft(strKind: Kind<F, String>, FO: Foldable<F>): Option<String> =
  FO.run {
    strKind.reduceLeftOption { base: String, value: String -> base + value }
  }
```

```kotlin:ank
getLengthFromLeft(maybeStr, Option.foldable())
```

```kotlin:ank
getLengthFromLeft(None, Option.foldable())
```

```kotlin:ank
getLengthFromLeft(strList, ListK.foldable())
```

### ReduceRightOption
Reduce the elements of this structure down to a single value by applying the provided aggregation function in
a right-associative manner.

Return None if the structure is empty, otherwise the result of combining the cumulative right-associative
result of the f operation over the A elements.

```kotlin:ank:silent
fun <F> getLengthFromRight(strKind: Kind<F, String>, FO: Foldable<F>): Option<String> =
  FO.run {
    strKind.reduceRightOption { value: String, base: Eval<String> -> base.map { it + value } }
      .value()
  }
```

```kotlin:ank
getLengthFromRight(maybeStr, Option.foldable())
```

```kotlin:ank
getLengthFromRight(None, Option.foldable())
```

```kotlin:ank
getLengthFromRight(strList, ListK.foldable())
```

### FoldMap
Fold implemented by mapping `A` values into `B`, and then combining them using the given `Monoid<B>` instance.

```kotlin:ank:silent
fun <F> getLenght(strKind: Kind<F, String>, FO: Foldable<F>): Int =
  FO.run {
    strKind.foldMap(Int.monoid()) { it.length }
  }
```

```kotlin:ank
getLenght(maybeStr, Option.foldable())
```

```kotlin:ank
getLenght(None, Option.foldable())
```

```kotlin:ank
getLenght(strList, ListK.foldable())
```

### Traverse_
A typed values will be mapped into `Kind<G, B>` by function `f` and combined using `Applicative#map2`.

This method is primarily useful when `<_>` represents an action or effect, and the specific `A` aspect of `Kind<G, A>` is
not otherwise needed.

```kotlin:ank:silent
import arrow.core.extensions.either.applicative.applicative

fun <F> traverse(strKind: Kind<F, String>, FO: Foldable<F>): Either<Int,Unit> =
  FO.run {
    strKind.traverse_(Either.applicative<Int>()) { Right(it.length) }
  }.fix()
```

```kotlin:ank
traverse(maybeStr, Option.foldable())
```

```kotlin:ank
traverse(None, Option.foldable())
```

```kotlin:ank
traverse(strList, ListK.foldable())
```

### Sequence_
Similar to `traverse_`, except it operates on `Kind<F, Kind<G, A>>` values, so no additional functions are needed.

```kotlin:ank:silent
import arrow.core.extensions.option.applicative.applicative

fun <F> sequence(strKind: Kind<F, Kind<ForOption, String>>, FO: Foldable<F>):Option<Unit> =
  FO.run {
    strKind.sequence_(Option.applicative())
  }.fix()

val maybeStrOpt = Some("abc".some())
val strNoneList = listOf("a".some(), None, "c".some()).k()
val strOptList = listOf("a".some(), "b".some(), "c".some()).k()
```

```kotlin:ank
sequence(maybeStrOpt, Option.foldable())
```

```kotlin:ank
sequence(None, Option.foldable())
```

```kotlin:ank
sequence(strNoneList, ListK.foldable())
```

```kotlin:ank
sequence(strOptList, ListK.foldable())
```

### Find
Find the first element matching the predicate, if one exists.

```kotlin:ank:silent
fun <F> getIfNotBlank(strKind: Kind<F, String>, FO: Foldable<F>): Option<String> =
  FO.run {
    strKind.find { it.isNotBlank() }
  }
```

```kotlin:ank
getIfNotBlank(maybeStr, Option.foldable())
```

```kotlin:ank
getIfNotBlank(None, Option.foldable())
```

```kotlin:ank
getIfNotBlank(strList, ListK.foldable())
```

### Exists
Check whether at least one element satisfies the predicate.

If there are no elements, the result is false.

```kotlin:ank:silent
fun <F> containsNotBlank(strKind: Kind<F, String>, FO: Foldable<F>): Boolean =
  FO.run {
    strKind.exists { it.isNotBlank() }
  }
```

```kotlin:ank
containsNotBlank(maybeStr, Option.foldable())
```

```kotlin:ank
containsNotBlank(None, Option.foldable())
```

```kotlin:ank
containsNotBlank(strList, ListK.foldable())
```

### ForAll
Check whether all elements satisfy the predicate.

If there are no elements, the result is true.

```kotlin:ank:silent
fun <F> isNotBlank(strKind: Kind<F, String>, FO: Foldable<F>): Boolean =
  FO.run {
    strKind.forAll { it.isNotBlank() }
  }
```

```kotlin:ank
isNotBlank(maybeStr, Option.foldable())
```

```kotlin:ank
isNotBlank(None, Option.foldable())
```

```kotlin:ank
isNotBlank(strList, ListK.foldable())
```

### IsEmpty
Returns true if there are no elements. Otherwise false.

```kotlin:ank:silent
fun <F> isFoldableEmpty(strKind: Kind<F, String>, FO: Foldable<F>): Boolean =
  FO.run {
    strKind.isEmpty()
  }
```

```kotlin:ank
isFoldableEmpty(maybeStr, Option.foldable())
```

```kotlin:ank
isFoldableEmpty(None, Option.foldable())
```

```kotlin:ank
isFoldableEmpty(strList, ListK.foldable())
```

### NonEmpty
Returns true if there is at least one element. Otherwise false.

```kotlin:ank:silent
fun <F> foldableNonEmpty(strKind: Kind<F, String>, FO: Foldable<F>): Boolean =
  FO.run {
    strKind.nonEmpty()
  }
```

```kotlin:ank
foldableNonEmpty(maybeStr, Option.foldable())
```

```kotlin:ank
foldableNonEmpty(None, Option.foldable())
```

```kotlin:ank
foldableNonEmpty(strList, ListK.foldable())
```

### Size
The size of this `Foldable`.

Note: Will not terminate for infinite-sized collections.

```kotlin:ank:silent
fun <F> foldableSize(strKind: Kind<F, String>, FO: Foldable<F>): Long =
  FO.run {
    strKind.size(Long.monoid())
  }
```

```kotlin:ank
foldableSize(maybeStr, Option.foldable())
```

```kotlin:ank
foldableSize(None, Option.foldable())
```

```kotlin:ank
foldableSize(strList, ListK.foldable())
```

### FoldMapM
Monadic folding on `F` by mapping `A` values to `Kind<G, B>`, combining the `B` values using the given `Monoid<B>` instance.

Similar to `foldM`, but using a `Monoid<B>`.

```kotlin:ank:silent
import arrow.core.extensions.option.monad.monad

fun <F> getLengthWithMonoid(strKind: Kind<F, String>, FO: Foldable<F>): Option<Int> =
  FO.run {
       strKind.foldMapM(Option.monad(), Int.monoid()) { Some(it.length) }
  }.fix()
```

```kotlin:ank
getLengthWithMonoid(maybeStr, Option.foldable())
```

```kotlin:ank
getLengthWithMonoid(None, Option.foldable())
```

```kotlin:ank
getLengthWithMonoid(strList, ListK.foldable())
```

### FoldM
Left associative monadic folding on `F`.

The default implementation of this is based on `foldL`, and thus will always fold across the entire structure.
Certain structures are able to implement this in such a way that folds can be short-circuited (not traverse the
entirety of the structure), depending on the `G` result produced at a given step.

```kotlin:ank:silent
import arrow.core.extensions.either.monad.monad

fun <F> maybeConcatenateString(strKind: Kind<F, String>, FO: Foldable<F>): Either<String,String> =
  FO.run {
    strKind.foldM(
      Either.monad<String>(),
      "str: "
    ) { base: String, value: String -> Right(base + value) }
  }.fix()
```

```kotlin:ank
maybeConcatenateString(maybeStr, Option.foldable())
```

```kotlin:ank
maybeConcatenateString(None, Option.foldable())
```

```kotlin:ank
maybeConcatenateString(strList, ListK.foldable())
```

### Get
Get the element at the index of the Foldable.

```kotlin:ank
import arrow.core.extensions.either.monad.monad
import arrow.core.extensions.either.foldable.foldable

fun foldableGet(strKind: EitherOf<String, String>): Option<String> =
  with(Either.foldable<String>()) {
    strKind.get(0)
  }

val rightStr = Either.right("abc") as Either<String, String>

foldableGet(rightStr)
```

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Foldable

TypeClass(Foldable::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Foldable)
