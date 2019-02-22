---
layout: docs
title: Divide
permalink: /docs/arrow/typeclasses/divide/
redirect_from:
  - /docs/typeclasses/divide
---

## Divide

{:.beginner}
beginner

`Divide` is a typeclass that models the divide part of divide and conquer.
`Divide` basically states: Given a `Kind<F, A>` and a `Kind<F, B>` and a way to turn `C` into an `A` and `B` it provides you a `Kind<F, C>`

A useful example is deriving serializers for a datatype from simpler serializers

Here we can easily construct a serializer for a `Tuple<A, B>` because we already have a serializer for `A` and `B`. Since most data classes can be expressed with tuples writing serializers by combining them like this is trivial.

```kotlin:ank:playground
import arrow.typeclasses.divide
import arrow.Kind
import arrow.core.Tuple2
import arrow.core.toT

data class User(val name: String, val age: Int)

// Boilerplate that @higherkind usually generates
class ForSerializer
fun <A> Kind<ForSerializer, A>.fix() = this as Serializer<A>

class Serializer<A>(val func: (A) -> String): Kind<ForSerializer, A> {
   companion object {
     fun divide() = object: Divide<ForSerializer> {
       override fun <A, B> Kind<ForSerializer, A>.contramap(f: (B) -> A): Kind<ForSerializer, B> =
         Serializer { fix().func(f(it)) }
       override fun <A, B, Z> divide(fa: Kind<ForSerializer, A>, fb: Kind<ForSerializer, B>, f: (Z) -> Tuple2<A, B>) =
         Serializer { z: Z ->
           val (a, b) = f(z)
           "A: ${fa.fix().func(a)}; B: ${fb.fix().func(b)}"
         }
     }
   }
}

val stringSerializer = Serializer<String> { "STRING: $it" }
val intSerializer = Serializer<Int> { "INT: $it" }

fun main(args: Array<String>) {
  //sampleStart
   val userSerializer: Serializer<User> = Serializer.divide().divide(
     stringSerializer,
     intSerializer
  ) { user: User ->
     user.name toT user.age
  }.fix()

  val user = User("John", 31)

  val result = userSerializer.func(user)
  //sampleEnd
  println(result)
}
```

### Main Combinators

#### divide

Derive a value of `Kind<F, C>` from a `Kind<F, A>`, a `Kind<F, B>` and a function `(C) -> Tuple2<A, B>`.

### Laws

Arrow provides `DivideLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `Divide` instances.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Divide` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Divide
TypeClass(Divide::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Divide)