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

For the following examples we are going to use some common imports 

```kotlin:ank:silent
import arrow.Kind
import arrow.core.*
import arrow.data.ListK
import arrow.data.k
import arrow.instances.monoid
import arrow.instances.listk.foldable.foldable
import arrow.instances.option.foldable.foldable
import arrow.typeclasses.Foldable
```
 
and the same two variables to see the different behaviors of `Foldable`:

```kotlin:ank:silent
val maybeStr: Option<String> = Some("abc")
val strList: ListK<String> = listOf("a", "b", "c").k()
```

### FoldLeft
Left associative fold on `F` using the provided function.

```kotlin:ank:silent
fun <F> foldLeft(strKind: Kind<F, String>, FO: Foldable<F>): String =
  with(FO) {
    strKind.foldLeft("str: ") { base: String, value: String -> base + value }
  }
```

```kotlin:ank
foldLeft(maybeStr, Option.foldable())
```

```kotlin:ank
foldLeft(None, Option.foldable())
```

```kotlin:ank
foldLeft(strList, ListK.foldable())
```

### FoldRight
Right associative lazy fold on `F` using the provided function.

This method evaluates `lb` lazily, and returns a lazy value.

For more detailed information about how this method works see the documentation for [`Eval<A>`]({{ '/docs/datatypes/eval' | relative_url }}).

```kotlin:ank:silent
fun <F> foldRight(strKind: Kind<F, String>, FO: Foldable<F>): String =
  with(FO) {
    strKind.foldRight(Eval.now("str: ")) { value: String, base: Eval<String> -> base.map { it + value } }
      .value()
  }
```

```kotlin:ank
foldRight(maybeStr, Option.foldable())
```

```kotlin:ank
foldRight(None, Option.foldable())
```

```kotlin:ank
foldRight(strList, ListK.foldable())
```

### Fold
Fold implemented using the given `Monoid<A>` instance.

```kotlin:ank:silent
fun <F> fold(strKind: Kind<F, String>, FO: Foldable<F>): String =
  with(FO) {
    "str: " + strKind.fold(String.monoid())
  }
```

```kotlin:ank
fold(maybeStr, Option.foldable())
```

```kotlin:ank
fold(None, Option.foldable())
```

```kotlin:ank
fold(strList, ListK.foldable())
```

Besides we have `combineAll` which is an alias for fold.

```kotlin:ank:silent
fun <F> combineAll(strKind: Kind<F, String>, FO: Foldable<F>): String =
  with(FO) {
    "str: " + strKind.combineAll(String.monoid())
  }
```

```kotlin:ank
combineAll(maybeStr, Option.foldable())
```

```kotlin:ank
combineAll(None, Option.foldable())
```

```kotlin:ank
combineAll(strList, ListK.foldable())
```

### ReduceLeftToOption

```kotlin:ank:silent
fun <F> reduceLeftToOption(strKind: Kind<F, String>, FO: Foldable<F>): Option<Int> =
  with(FO) {
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
  with(FO) {
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
fun <F> reduceLeftOption(strKind: Kind<F, String>, FO: Foldable<F>): Option<String> =
  with(FO) {
    strKind.reduceLeftOption { base: String, value: String -> base + value }
  }
```

```kotlin:ank
reduceLeftOption(maybeStr, Option.foldable())
```

```kotlin:ank
reduceLeftOption(None, Option.foldable())
```

```kotlin:ank
reduceLeftOption(strList, ListK.foldable())
```

### ReduceRightOption
Reduce the elements of this structure down to a single value by applying the provided aggregation function in
a right-associative manner.

Return None if the structure is empty, otherwise the result of combining the cumulative right-associative
result of the f operation over the A elements.

```kotlin:ank:silent
fun <F> reduceRightOption(strKind: Kind<F, String>, FO: Foldable<F>): Option<String> =
  with(FO) {
    strKind.reduceRightOption { value: String, base: Eval<String> -> base.map { it + value } }
      .value()
  }
```

```kotlin:ank
reduceRightOption(maybeStr, Option.foldable())
```

```kotlin:ank
reduceRightOption(None, Option.foldable())
```

```kotlin:ank
reduceRightOption(strList, ListK.foldable())
```

### FoldMap
Fold implemented by mapping A values into B and then combining them using the given `Monoid<B>` instance.

```kotlin:ank:silent
fun <F> foldMap(strKind: Kind<F, String>, FO: Foldable<F>): Int =
  with(FO) {
    strKind.foldMap(Int.monoid()) { it.length }
  }
```

```kotlin:ank
foldMap(maybeStr, Option.foldable())
```

```kotlin:ank
foldMap(None, Option.foldable())
```

```kotlin:ank
foldMap(strList, ListK.foldable())
```
  
### Traverse 
A typed values will be mapped into `G<B>` by function `f` and combined using `Applicative#map2`.

This method is primarily useful when `G<_>` represents an action or effect, and the specific `A` aspect of `G<A>` is
not otherwise needed.

```kotlin:ank:silent
import arrow.instances.either.applicative.applicative

fun <F> traverse(strKind: Kind<F, String>, FO: Foldable<F>): Either<Int,Unit> =
  with(FO) {
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

### Sequence
Similar to traverse except it operates on `F<G<A>>` values, so no additional functions are needed.

```kotlin:ank:silent
import arrow.instances.option.applicative.applicative

fun <F> sequence(strKind: Kind<F, Kind<ForOption, String>>, FO: Foldable<F>):Option<Unit> =
  with(FO) {
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
fun <F> find(strKind: Kind<F, String>, FO: Foldable<F>): Option<String> =
  with(FO) {
    strKind.find { it.isNotBlank() }
  }
```

```kotlin:ank
find(maybeStr, Option.foldable())
```

```kotlin:ank
find(None, Option.foldable())
```

```kotlin:ank
find(strList, ListK.foldable())
```

### Exists
Check whether at least one element satisfies the predicate.

If there are no elements, the result is false.

```kotlin:ank:silent
fun <F> exists(strKind: Kind<F, String>, FO: Foldable<F>): Boolean =
  with(FO) {
    strKind.exists { it.isNotBlank() }
  }
```

```kotlin:ank
exists(maybeStr, Option.foldable())
```

```kotlin:ank
exists(None, Option.foldable())
```

```kotlin:ank
exists(strList, ListK.foldable())
```

### ForAll
Check whether all elements satisfy the predicate.

If there are no elements, the result is true.

```kotlin:ank:silent
fun <F> forAll(strKind: Kind<F, String>, FO: Foldable<F>): Boolean =
  with(FO) {
    strKind.forAll { it.isNotBlank() }
  }
```

```kotlin:ank
forAll(maybeStr, Option.foldable())
```

```kotlin:ank
forAll(None, Option.foldable())
```

```kotlin:ank
forAll(strList, ListK.foldable())
```

### IsEmpty
Returns true if there are no elements. Otherwise false.

```kotlin:ank:silent
fun <F> isEmpty(strKind: Kind<F, String>, FO: Foldable<F>): Boolean =
  with(FO) {
    strKind.isEmpty()
  }
```

```kotlin:ank
isEmpty(maybeStr, Option.foldable())
```

```kotlin:ank
isEmpty(None, Option.foldable())
```

```kotlin:ank
isEmpty(strList, ListK.foldable())
```

### NonEmpty
Returns true if there is at least one element. Otherwise false.

```kotlin:ank:silent
fun <F> nonEmpty(strKind: Kind<F, String>, FO: Foldable<F>): Boolean =
  with(FO) {
    strKind.nonEmpty()
  }
```

```kotlin:ank
nonEmpty(maybeStr, Option.foldable())
```

```kotlin:ank
nonEmpty(None, Option.foldable())
```

```kotlin:ank
nonEmpty(strList, ListK.foldable())
```

### Size
The size of this `Foldable`.

This is overriden in structures that have more efficient size implementations
(e.g. Vector, Set, Map).

Note: will not terminate for infinite-sized collections.

```kotlin:ank:silent
fun <F> size(strKind: Kind<F, String>, FO: Foldable<F>): Long =
  with(FO) {
    strKind.size(Long.monoid())
  }
```

```kotlin:ank
size(maybeStr, Option.foldable())
```

```kotlin:ank
size(None, Option.foldable())
```

```kotlin:ank
size(strList, ListK.foldable())
```

### FoldMapM
Monadic folding on `F` by mapping `A` values to `G<B>`, combining the `B` values using the given `Monoid<B>` instance.

Similar to `foldM`, but using a `Monoid<B>`.

```kotlin:ank:silent
import arrow.instances.option.monad.monad

fun <F> foldMapM(strKind: Kind<F, String>, FO: Foldable<F>): Option<Int> =
  with(FO) {
       strKind.foldMapM(Option.monad(), Int.monoid()) { Some(it.length) }
  }.fix()
```

```kotlin:ank
foldMapM(maybeStr, Option.foldable())
```

```kotlin:ank
foldMapM(None, Option.foldable())
```

```kotlin:ank
foldMapM(strList, ListK.foldable())
```

### FoldM
Left associative monadic folding on `F`.

The default implementation of this is based on `foldL`, and thus will always fold across the entire structure.
Certain structures are able to implement this in such a way that folds can be short-circuited (not traverse the
entirety of the structure), depending on the `G` result produced at a given step.

```kotlin:ank:silent
import arrow.instances.either.monad.monad

fun <F> foldM(strKind: Kind<F, String>, FO: Foldable<F>): Either<String,String> =
  with(FO) {
    strKind.foldM(
      Either.monad<String>(),
      "str: "
    ) { base: String, value: String -> Right(base + value) }
  }.fix()
```

```kotlin:ank
foldM(maybeStr, Option.foldable())
```

```kotlin:ank
foldM(None, Option.foldable())
```

```kotlin:ank
foldM(strList, ListK.foldable())
```

### Get
Get the element at the index of the Foldable.

```kotlin:ank
import arrow.instances.either.monad.monad
import arrow.instances.either.foldable.foldable

fun foldableGet(strKind: EitherOf<String, String>): Option<String> =
  with(Either.foldable<String>()) {
    strKind.get(Either.monad(), 0)
  }

val rightStr = Either.right("abc") as Either<String, String>

foldableGet(rightStr)
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
