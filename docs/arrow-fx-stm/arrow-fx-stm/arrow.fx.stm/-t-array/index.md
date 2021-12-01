//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[TArray](index.md)

# TArray

[common]\
data class [TArray](index.md)&lt;[A](index.md)&gt;

A [TArray](index.md) is an array of transactional variables.

##  Creating [TArray](index.md)

Similar to normal arrays there are a few ways to create a [TArray](index.md):

import arrow.fx.stm.TArray\
import arrow.fx.stm.atomically\
\
suspend fun example() {\
  //sampleStart\
  // Create a size 10 array and fill it by using the construction function.\
  TArray.new(10) { i -&gt; i * 2 }\
  // Create a size 10 array and fill it with a constant\
  TArray.new(size = 10, 2)\
  // Create an array from `vararg` arguments:\
  TArray.new(5, 2, 10, 600)\
  // Create an array from any iterable\
  TArray.new(listOf(5,4,3,2))\
  //sampleEnd\
}<!--- KNIT example-tarray-01.kt -->

##  Reading a value from the array

import arrow.fx.stm.TArray\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tarr = TArray.new(size = 10, 2)\
  val result = atomically {\
    tarr[5]\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tarray-02.kt -->

##  Setting a value in the array

import arrow.fx.stm.TArray\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tarr = TArray.new(size = 10, 2)\
  val result = atomically {\
    tarr[5] = 3\
\
    tarr[5]\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tarray-03.kt -->

##  Transform the entire array

import arrow.fx.stm.TArray\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tarr = TArray.new(size = 10, 2)\
  val result = atomically {\
    tarr.transform { it + 1 }\
  }\
  //sampleEnd\
}<!--- KNIT example-tarray-04.kt -->

##  Folding the array

import arrow.fx.stm.TArray\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tarr = TArray.new(size = 10, 2)\
  val result = atomically {\
    tarr.fold(0) { acc, v -&gt; acc + v }\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tarray-05.kt -->

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [size](size.md) | [common]<br>fun [size](size.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
