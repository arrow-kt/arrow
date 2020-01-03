---
layout: docs-core
title: Hash
permalink: /docs/arrow/typeclasses/hash/
redirect_from:
  - /docs/typeclasses/hash/
---

## Hash




The `Hash` typeclass abstracts the ability to compute the hash of any object.  
It can be considered the typeclass equivalent of Java's `Object#hashcode` and Kotlin's `Any#hashCode`.  

A hash function is a mapping of arbitrary data (`F`) to an output set of fixed size (`Int`). The result, a hash value, is most commonly used in collections like HashTable as a lookup value.

```kotlin:ank:playground
import arrow.core.extensions.*

fun main(args: Array<String>) {
  //sampleStart
  // Enable the extension functions inside Hash using run
  val result = String.hash().run { "1".hash() }
  //sampleEnd
  println(result)
}
```

### Main Combinators

#### F.hash

Computes a hash of an instance of `F`.

`fun F.hash(): Int`

```kotlin:ank:playground
import arrow.core.extensions.*

fun main(args: Array<String>) {
  //sampleStart
  // Enable the extension functions inside Hash using run
  val result = String.hash().run { "MyString".hash() }
  //sampleEnd
  println(result)
}
```

### Laws

Arrow provides [`HashLaws`][hash_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own `Hash` instances.

#### Creating your own `Hash` instances

Hash contains a special instance that delegates to Kotlin's `Any?.hashCode()` function in all cases. This may be sufficient for most use cases, but will fail for data types that contain functions, typeclasses, or non-data classes.
It also might make more sense to provide your own instance in some cases; for example, when there is already a unique property on an object.
Hash has a constructor to create a `Hash` instance from any function `(F) -> Int`.

```kotlin:ank:playground
import arrow.typeclasses.Hash

data class User(val id: String, val name: String)
val user = User("MyId", "MyName")

fun main(args: Array<String>) {
  //sampleStart
  // This is fine
  val result = Hash.any().run { user.hash() }
  //sampleEnd
  println(result)
}
```

```kotlin:ank:playground
import arrow.typeclasses.Hash

data class User(val id: String, val name: String)
val user = User("MyId", "MyName")

fun main(args: Array<String>) {
  //sampleStart  
  // This might be better because id usually is a unique value in itself
  val userHash = Hash<User> { u -> u.id.hashCode() }
  val result = userHash.run { user.hash() }
  //sampleEnd
  println(result)
}
```

```kotlin:ank:playground
import arrow.core.*
import arrow.typeclasses.Hash

fun main(args: Array<String>) {
  //sampleStart
  // This will return false because it's not evaluated for hashing
  val result = Hash.any().run { Eval.later { 1 }.hash() == Eval.later { 1 }.hash() }
  //sampleEnd
  println(result)
}
```

```kotlin:ank:playground
import arrow.typeclasses.Hash

fun main(args: Array<String>) {
  //sampleStart
  // using invoke constructor
  val stringHash = Hash<String> { a -> a.hashCode() }
  val result = stringHash.run { "MyString".hash() }
  //sampleEnd
  println(result)
}
```

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Hash` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Hash
TypeClass(Hash::class).dtMarkdownList()
```

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

```kotlin:ank:outFile(diagram.nomnol)
import arrow.typeclasses.Hash
TypeClass(Hash::class).hierarchyGraph()
```

[hash_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/HashLaws.kt
