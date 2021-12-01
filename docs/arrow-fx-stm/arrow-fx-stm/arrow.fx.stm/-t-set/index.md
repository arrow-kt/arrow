//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[TSet](index.md)

# TSet

[common]\
data class [TSet](index.md)&lt;[A](index.md)&gt;

A [TSet](index.md) is a concurrent transactional implementation of a hashset.

Based on a Hash-Array-Mapped-Trie implementation. While this does mean that a read may take up to 5 steps to be resolved (depending on how well distributed the hash function is), it also means that structural changes can be isolated and thus do not increase contention with other transactions. This effectively means concurrent access to different values is unlikely to interfere with each other.

Hash conflicts are resolved by chaining.

##  Creating a [TSet](index.md)

Depending on whether or not you are in a transaction you can use either [STM.newTSet](../new-t-set.md) or [TSet.new](-companion/new.md) to create a new [TSet](index.md).

There are a few alternatives because [TSet](index.md) can be supplied a custom hash strategy. If no argument is given it defaults to [Any.hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html).

##  Adding elements to the set

Adding an element can be achieved by using either [STM.insert](../-s-t-m/insert.md) or its alias [STM.plusAssign](../-s-t-m/plus-assign.md):

import arrow.fx.stm.TSet\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tset = TSet.new&lt;String&gt;()\
  atomically {\
    tset.insert("Hello")\
    tset += "World"\
  }\
  //sampleEnd\
}<!--- KNIT example-tset-01.kt -->

##  Removing an element from the set

import arrow.fx.stm.TSet\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tset = TSet.new&lt;String&gt;()\
  atomically {\
    tset.insert("Hello")\
    tset.remove("Hello")\
  }\
  //sampleEnd\
}<!--- KNIT example-tset-02.kt -->

##  Checking for membership

import arrow.fx.stm.TSet\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tset = TSet.new&lt;String&gt;()\
  val result = atomically {\
    tset.insert("Hello")\
    tset.member("Hello")\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tset-03.kt -->

##  Where are operations like isEmpty or size?

This is a design tradeoff. It is entirely possible to track size however this usually requires one additional [TVar](../-t-var/index.md) for size and almost every operation would modify that. That will lead to contention and thus decrease performance.

Should this feature interest you and performance is not as important please open an issue. It is most certainly possible to add another version of [TSet](index.md) that keeps track of its size.

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
