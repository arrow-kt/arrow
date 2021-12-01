//[arrow-core](../../../index.md)/[arrow.typeclasses](../index.md)/[Semiring](index.md)

# Semiring

[common]\
interface [Semiring](index.md)&lt;[A](index.md)&gt;

The [Semiring](index.md) type class for a given type A combines both a commutative additive [Monoid](../-monoid/index.md) and a multiplicative [Monoid](../-monoid/index.md). It requires the multiplicative [Monoid](../-monoid/index.md) to distribute over the additive one. The operations of the multiplicative [Monoid](../-monoid/index.md) have been renamed to [one](one.md) and [combineMultiplicate](combine-multiplicate.md) for easier use.

The [one](one.md) function serves exactly like the empty function for an additive [Monoid](../-monoid/index.md), just adapted for the multiplicative version. This forms the following law:

Please note that the empty function has been renamed to [zero](zero.md) to get a consistent naming style inside the semiring.

Currently, [Semiring](index.md) instances are defined for all available number types.

###  Examples

Here a some examples:

import arrow.typeclasses.Semiring\
\
fun main(args: Array&lt;String&gt;) {\
  val result =\
  //sampleStart\
  Semiring.int().run { 1.combine(2) }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-semiring-01.kt -->import arrow.typeclasses.Semiring\
\
fun main(args: Array&lt;String&gt;) {\
  val result =\
  //sampleStart\
  Semiring.int().run { 2.combineMultiplicate(3) }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-semiring-02.kt -->

The type class Semiring also has support for the +* syntax:

import arrow.typeclasses.Semiring\
\
fun main(args: Array&lt;String&gt;) {\
  val result =\
  //sampleStart\
  Semiring.int().run {\
     1 + 2\
  }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-semiring-03.kt -->import arrow.typeclasses.Semiring\
\
fun main(args: Array&lt;String&gt;) {\
  val result =\
  //sampleStart\
  Semiring.int().run {\
     2 * 3\
  }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-semiring-04.kt -->

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [combine](combine.md) | [common]<br>abstract fun [A](index.md).[combine](combine.md)(b: [A](index.md)): [A](index.md) |
| [combineMultiplicate](combine-multiplicate.md) | [common]<br>abstract fun [A](index.md).[combineMultiplicate](combine-multiplicate.md)(b: [A](index.md)): [A](index.md)<br>Multiplicatively combine two [A](index.md) values. |
| [maybeCombineAddition](maybe-combine-addition.md) | [common]<br>open fun [A](index.md)?.[maybeCombineAddition](maybe-combine-addition.md)(b: [A](index.md)?): [A](index.md)<br>Maybe additively combine two [A](index.md) values. |
| [maybeCombineMultiplicate](maybe-combine-multiplicate.md) | [common]<br>open fun [A](index.md)?.[maybeCombineMultiplicate](maybe-combine-multiplicate.md)(b: [A](index.md)?): [A](index.md)<br>Maybe multiplicatively combine two [A](index.md) values. |
| [one](one.md) | [common]<br>abstract fun [one](one.md)(): [A](index.md)<br>A one value for this A |
| [plus](plus.md) | [common]<br>open operator fun [A](index.md).[plus](plus.md)(b: [A](index.md)): [A](index.md) |
| [times](times.md) | [common]<br>open operator fun [A](index.md).[times](times.md)(b: [A](index.md)): [A](index.md) |
| [zero](zero.md) | [common]<br>abstract fun [zero](zero.md)(): [A](index.md)<br>A zero value for this A |
