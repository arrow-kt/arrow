---
layout: docs
title: Intro to Recursion Schemes
permalink: /docs/recursion/intro/
---

## Recursion Schemes

{:.advanced}
advanced

Recursion schemes are an abstraction for structured recursion that ensure runtime safety and provide
powerful abstractions for recursive datatypes.

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

However, it would be nicer to have a function to do this for us. We can use arbitrary recursion to do this.
We'll call the function `downFrom`.

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
in their definition. `IntList` itself is an arbitrarily recursive type for the same reason. However, there
is a problem with arbitrary recursion: it is impossible to guarantee that an arbitrarily recursive function
is stack safe.

#### Folds and unfolds

`fold` is a familiar function to most functional programmers, and is used whenever a collection needs to be
collapsed into a single element. The `multiply` function above can be implemented much more simply using `fold`.

```kotlin:ank
fun <A> fold(list: IntList, onNil: A, onCons: (Int, A) -> A): A =
  when (list) {
    Nil -> onNil
    is Cons -> onCons(list.head, fold(list.tail, onNil, onCons))
  }

fun multiply(list: IntList): Int = fold(list, 1) { a, b -> a * b }
```

`unfold` is the less commonly known opposite of fold; it takes in an initial element and generates a
collection (you may recognize it as the `generateT` functions in the kotlin standard library). The
`downFrom` function can be implemented much more simply using `unfold`.

```kotlin:ank
fun <A> unfold(init: A, produce: (A) -> Option<Tuple2<Int, A>>): IntList =
  produce(init).fold(
    { Nil },
    { (elem, next) -> Cons(elem, unfold(next, produce)) }
  )

fun downFrom(i: Int): IntList =
  unfold(i) { x ->
    if (x <= 0) None
    else Some(x toT (x - 1))
  }
```

The new implementations of `downFrom` and `multiply` use structured recursion. Here, `fold` and `unfold` are
actually recursion schemes; they abstract out the recursion from the business logic of the function. Though
the implementations above are not stack safe, they can easily be made so (at the cost of some readability),
and any functions implemented with them would also become stack safe.

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

In fact, `fold` and `unfold` can be implemented for any recursive data structure, even complex ones like
expression trees. However, it requires a lot of boilerplate and creates a lot of complexity. Luckily for us,
Arrow's recursion schemes allow us to solve this problem.

#### Recursive type parameters

The solution to this initially seems a bit strange. First, we must define a type's pattern, where
the recursive type is replaced with a type parameter.

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
typealias IntList = IntListPattern<IntListPattern<IntListPattern<...>>>
```

Of course, this is not possible in Kotlin. However, we can use the `Fix` datatype (a type level recursion scheme)
to emulate this.

```kotlin:ank
import arrow.recursion.data.*

typealias IntFixList = Fix<ForIntListPattern>
```

So why do this? We can now define a [Functor]({{ '/docs/arrow/typeclasses/functor' | relative_url }}) instance for 
`IntListPattern`, allowing us to traverse into the structure.

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

This can be used to implement `fold` and `unfold` for any `Fix<F>`, where `F` is a `Functor` (and hence
for any recursive data structure) by using `map` to recursively descend into the structure.

#### Recursive and Corecursive

The [Recursive]({{ '/docs/recursion/recursive' | relative_url }}) typeclass provides `cata`, and the 
[Corecursive]({{ '/docs/recursion/recursive' | relative_url }}) typeclass provides `ana`, which are very
similar to fold and unfold.

```kotlin
typealias Algebra<F, A> = (Kind<F, A>) -> A     // fold
typealias Coalgebra<F, A> = (A) -> Kind<F, A>   // unfold

fun <F, A> Functor<F>.cata(f: Fix<F>, alg: Algebra<F, Eval<A>>): A
fun <F, A> Functor<F>.ana(a: A, coalg: Coalgebra<F, A>): Fix<F>
```

We can use them to rewrite our `multiply` and `downFrom` functions.

```kotlin:ank
import arrow.recursion.instances.fix.recursive.*

// We extract these functions out for later use
val multiply: Algebra<ForIntListPattern, Eval<Int>> = { l ->
  val list = l.fix()
  when (list) {
    NilPattern -> Eval.now(1)
    is ConsPattern -> list.tail.map { it * list.head }
  }
}

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

Here we use the `IntListPattern` functor to model recursion which resembles a stack; we unfold a list as
we traverse deeper into the call structure, then fold a list as we evaluate the result. This allows us
to effectively model any recursive computation with recursion schemes, making them consistant and stack
safe.

#### Typeclasses

- [Recursive]({{ '/docs/recursion/recursive' | relative_url }})
- [Corecursive]({{ '/docs/recursion/recursive' | relative_url }})
- [Birecursive]({{ '/docs/recursion/recursive' | relative_url }})

#### Datatypes

- [Fix]({{ 'docs/recursion/fix' | relative_url }})
- [Mu]({{ 'docs/recursion/mu' | relative_url }})
- [Nu]({{ 'docs/recursion/nu' | relative_url }})

## Credits

Tutorial partially adapted from [Peeling the Banana: Recursion Schemes from First Principles by Zainab Ali](https://www.youtube.com/watch?v=XZ9nPZbaYfE)

Contents partially adapted from [Katalyst](https://github.com/aedans/Katalyst.git)
