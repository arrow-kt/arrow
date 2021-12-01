//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[TMap](index.md)

# TMap

[common]\
data class [TMap](index.md)&lt;[K](index.md), [V](index.md)&gt;

A [TMap](index.md) is a concurrent transactional implementation of a key value hashmap.

Based on a Hash-Array-Mapped-Trie implementation. While this does mean that a read may take up to 5 steps to be resolved (depending on how well distributed the hash function is), it also means that structural changes can be isolated and thus do not increase contention with other transactions. This effectively means concurrent access to different values is unlikely to interfere with each other.

Hash conflicts are resolved by chaining.

##  Creating a [TMap](index.md)

Depending on whether or not you are in a transaction you can use either [STM.newTMap](../new-t-map.md) or [TMap.new](-companion/new.md) to create a new [TMap](index.md).

There are a few alternatives because [TMap](index.md) can be supplied a custom hash strategy. If no argument is given it defaults to [Any.hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html).

##  Reading an element with a key

Reading from a [TMap](index.md) can be done using either [STM.lookup](../-s-t-m/lookup.md) or its alias [STM.get](../-s-t-m/get.md).

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  val result = atomically {\
    tmap.set(1, "Hello")\
    tmap[2] = "World"\
\
    tmap.lookup(1) + tmap[2]\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tmap-01.kt -->

If the key is not present [STM.lookup](../-s-t-m/lookup.md) will not retry, instead it returns null.

##  Inserting a value

Inserting can be done using either [STM.insert](../-s-t-m/insert.md) or its alias [STM.set](../-s-t-m/set.md):

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  atomically {\
    tmap.insert(1, "Hello")\
    tmap[2] = "World"\
  }\
  //sampleEnd\
}<!--- KNIT example-tmap-02.kt -->

Another option when adding elements is to use [STM.plusAssign](../-s-t-m/plus-assign.md):

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  atomically {\
    tmap += (1 to "Hello")\
    tmap += (2 to "World")\
  }\
  //sampleEnd\
}<!--- KNIT example-tmap-03.kt -->

##  Updating an existing value [TMap](index.md):

Using [STM.update](../-s-t-m/update.md) it is possible to update an existing value of a [TMap](index.md). If the value is not present it does nothing.

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  val result = atomically {\
    tmap[1] = "Hello"\
    tmap[2] = "World"\
\
    tmap.update(1) { it.reversed() }\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tmap-04.kt -->

##  Checking membership

Using [STM.member](../-s-t-m/member.md) it is possible to check if a [TMap](index.md) contains a value for a key:

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  val result = atomically {\
    tmap[1] = "Hello"\
    tmap.member(1)\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-tmap-05.kt -->

##  Removing a value from a [TMap](index.md)

Removing is done by using [STM.remove](../-s-t-m/remove.md):

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  atomically {\
    tmap[1] = "Hello"\
\
    tmap.remove(1)\
  }\
  //sampleEnd\
}<!--- KNIT example-tmap-06.kt -->

##  Where are operations like isEmpty or size?

This is a design tradeoff. It is entirely possible to track size however this usually requires one additional [TVar](../-t-var/index.md) for size and almost every operation would modify that. That will lead to contention and thus decrease performance.

Should this feature interest you and performance is not as important please open an issue. It is most certainly possible to add another version of [TMap](index.md) that keeps track of its size.

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
