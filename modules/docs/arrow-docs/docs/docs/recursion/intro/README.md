---
layout: docs
title: Intro to Recursion Schemes
permalink: /docs/recursion/intro/
---

## Recursion Schemes

Recursion schemes are an abstraction for structured recursion that ensure runtime safety and provide
powerful abstractions for recursive data types.

#### Arbitrary recursion

The traditional definition of a linked list uses arbitrary recursion.

```kotlin
// The generic type parameter is omitted for simplicity
sealed class IntList
object Nil : IntList()
data class Cons(val head: Int, val tail: IntList) : IntList()
```

Here, `Nil` is the empty list and `Cons` is an element plus another list. Instances of this can be created
by chaining `Cons` constructors.

```kotlin:ank
import arrow.*
import arrow.core.*
import arrow.typeclasses.*
import arrow.recursion.*

Cons(3, Cons(2, Cons(1, Nil)))
```

However, it would be nicer to have a function to do this for us. We'll call it `downFrom`.

```kotlin:ank
fun downFrom(i: Int): IntList =
  if (i <= 0) Nil
  else Cons(i, downFrom(i - 1))

downFrom(3)
```

We can also use arbitrary recursion to do computation with this data structure. For example, we might want
to multiply every element in the list.

```kotlin:ank
fun multiply(list: IntList): Int =
  when (list) {
    Nil -> 1
    is Cons -> list.head * multiply(list.tail)
  }

multiply(downFrom(3))
```

Both the `downFrom` and `multiply` functions are arbitrarily recursive functions; they both call themselves
in their definition. `IntList` itself is an arbitrarily recursive type for the same reason. However, we can
do better.

#### Folds and unfolds

`fold` is a familiar function to most functional programmers, and is used whenever a collection needs to be
collapsed into one element. The `multiply` function above can be implemented much more simply using `fold`.

```kotlin:ank
fun <A> fold(list: IntList, onNil: A, onCons: (Int, A) -> A): A =
  when (list) {
    Nil -> onNil
    is Cons -> onCons(list.head, fold(list.tail, onNil, onCons))
  }

fun multiply(list: IntList): Int = fold(list, 1) { a, b -> a * b }
```

`unfold` is the less commonly known opposite of fold; it takes in an initial element and generates a
collection. The `downFrom` function can be implemented much more simply using `unfold`.

```kotlin:ank
fun <A> unfold(init: A, produce: (A) -> Option<Tuple2<Int, A>>): IntList =
  produce(init).fold(
    { Nil },
    { (elem, next) -> Cons(elem, unfold(next, produce)) }
  )

fun downFrom(i: Int): IntList =
  unfold(i) {
    if (it <= 0) None
    else Some((it - 1).let { it toT it })
  }
```

The new implementations of `downFrom` and `multiply` use structured recursion. Here, `fold` and `unfold` are
actually recursion schemes; they abstract out the recursion from the business logic of the function.

#### Generalized folds and unfolds

Lists are not the only data structure that can be folded and unfolded. Binary trees can also use this
pattern.

```kotlin
sealed class IntTree
data class Leaf(val value: Int) : IntTree()
data class Node(val left: IntTree, val right: IntTree) : IntTree()
```

```kotlin:ank
fun <A> fold(tree: IntTree, onLeaf: (Int) -> A, onNode: (A, A) -> A): A =
  when (tree) {
    is Leaf -> onLeaf(tree.value)
    is Node -> onNode(fold(tree.left, onLeaf, onNode), fold(tree.right, onLeaf, onNode))
  }

fun <A> unfold(init: A, produce: (A) -> Either<Int, Tuple2<A, A>>): IntTree =
  produce(init).fold(
    { Leaf(it) },
    { (left, right) -> Node(unfold(left, produce), unfold(right, produce)) }
  )
```

`fold` and `unfold` can be implemented for any recursive data structure, even complex ones like expression
trees. However, it requires a lot of boilerplate and creates a lot of complexity. Luckily for us, recursion
schemes allow us to solve this problem.

#### Recursive type parameters

The solution to this is a bit strange. First, we must define a type called `IntListPattern`, where the
recursive type is replaced with a type parameter.

```kotlin
@higherkind sealed class IntListPattern<out A> : IntListPatternOf<A> { companion object }
object NilPattern : IntListPattern<Nothing>()
@higherkind data class ConsPattern<out A>(val head: Int, val tail: A) : IntListPattern<A>()
```

While this type may look useless at first, it turns out that our original `IntList` and `IntListPattern<IntList>`
are isomorphic -- that is, they can easily be converted from one to another. For that matter, `IntList` is
also isomorphic to `IntListPattern<IntListPattern<IntList>>`. In fact, applying `IntListPattern` to itself
infinitely yields the original `IntList` type.

```kotlin
typealias IntList = IntListPattern<ListPattern<ListPattern<...>>>
```

Of course, this is not possible in Kotlin. However, we can use the `Fix` type to emulate this.

```kotlin:ank
import arrow.recursion.data.*

typealias IntFixList = Fix<ForIntListPattern>
```

We do this so we can define a `Functor` instance for `IntListPattern`, allowing us to traverse into
the structure.

```kotlin
@instance(IntListPattern::class)
interface IntListPatternFunctorInstance : Functor<ForIntListPattern> {
  override fun <A, B> IntListPatternOf<A>.map(f: (A) -> B): IntListPatternOf<B> {
    val lp = fix()
    return when (lp) {
      NilPattern -> NilPattern
      is ConsPattern -> ConsPattern(lp.head, f(lp.tail))
    }
  }
}
```

Now we can implement `fold` and `unfold` for any `Fix<F>`, where `F` is a `Functor` (and hence for any
recursive data structure).

#### Recursive and Corecursive

The `Recursive` typeclass provides `cata`, and the `Corecursive` typeclass provides `ana`. which are very
similar to fold and unfold. We can use them to rewrite our `multiply` and `downFrom` functions.

```kotlin:ank
// typealias Algebra<F, A> = (Kind<F, A>) -> A
val multiply: Algebra<ForIntListPattern, Eval<Int>> = { l ->
  val list = l.fix()
  when (list) {
    NilPattern -> Eval.now(1)
    is ConsPattern -> list.tail.map { it * list.head }
  }
}

// typealias Coalgebra<F, A> = (A) -> Kind<F, A>
val downFrom: Coalgebra<ForIntListPattern, Int> = { i ->
  if (i <= 0) NilPattern
  else (i - 1).let { ConsPattern(it, it) }
}

fun multiply(list: IntFixList): Int = Fix.recursive().run {
  IntListPattern.functor().cata(list, multiply)
}

fun downFrom(i: Int): IntFixList = Fix.recursive().run {
  IntListPattern.functor().ana(i, downFrom).fix()
}
```

#### General recursion

So far, we've generalized `fold` and `unfold` into `cata` and `ana` which are recursion schemes for
destroying and creating any recursive structure. By combining these, we can create a recursion scheme for
general recursion.

```kotlin:ank
fun factorial(i: Int): Int = IntListPattern.functor().hylo(multiply, downFrom, i)
```

Hylo is the composition of cata and ana, allowing us to get rid of the intermediate data structure.
