---
layout: docs
title: Hash
permalink: /docs/typeclasses/hash/
---

## Hash

{:.beginner}
beginner

The `Hash` typeclass abstracts the ability to compute the hash of any object.
It can be considered the typeclass equivalent of Java's `Object#hashcode` and Kotlin's `Any#hashCode`.

```kotlin:ank
import arrow.instances.*

// Enable the extension functions inside Hash using run
String.hash().run {
  "1".hash()
}
```

### Main Combinators

#### F.hash

Computes a hash of an instance of `F`.

`fun F.hash(): Int`


```kotlin:ank
String.hash().run { "MyString".hash() }
```

### Laws

Arrow provides [`HashLaws`][hash_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own `Hash` instances.

#### Creating your own `Hash` instances

Hash contains a special instance that delegates to Kotlin's `Any?.hashCode()` function in all cases. This may be sufficient for most use cases, but will fail for data types that contain functions, typeclasses or non-data classes.
It also might make more sense to provide your own instance in for some cases, for example when there is already a unique property on an object.
Hash has a constructor to create a `Hash` instance from any function `(F) -> Int`.

```kotlin:ank
import arrow.typeclasses.Hash
data class User(val id: String, val name: String)
val user = User("MyId", "MyName")

// This is fine
Hash.any().run { user.hash() }
```

```kotlin:ank
// This might be better because id usually is a unique value in itself
val userHash = Hash<User> { u -> u.id.hashCode() }
userHash.run { user.hash() }
```

```kotlin:ank
import arrow.core.*

// This will fail because it's not evaluated for hashing
Hash.any().run { Eval.later { 1 }.hash() == Eval.later { 1 }.hash() }
```

```kotlin:ank
// using invoke constructor
val stringHash = Hash<String> { a -> a.hashCode() }
```

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Hash` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*

TypeClass(Hash::class).dtMarkdownList()
```

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

```kotlin:ank:outFile(diagram.nomnol)
TypeClass(Hash::class).hierarchyGraph()
```

[hash_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/HashLaws.kt