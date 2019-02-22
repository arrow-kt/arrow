---
layout: docs
title: Divisible
permalink: /docs/arrow/typeclasses/divisible/
redirect_from:
  - /docs/typeclasses/divisible
---

## Divisible

{:.beginner}
beginner

`Divisible` extends upon `Divide` by providing an empty method called `conquer`.
`conquer` is useful to prove identiy laws when working with `Divisible` instances.

Extending the serializer example from `Divide` `conquer` would simply serialize data to an empty string.

```kotlin:ank:playground
import arrow.typeclasses.divide
import arrow.Kind
import arrow.core.Tuple2
import arrow.core.toT

// Boilerplate that @higherkind usually generates
class ForSerializer
fun <A> Kind<ForSerializer, A>.fix() = this as Serializer<A>

class Serializer<A>(val func: (A) -> String): Kind<ForSerializer, A> {
   companion object {
     fun divisible() = object: Divide<ForSerializer> {
       override fun <A, B> Kind<ForSerializer, A>.contramap(f: (B) -> A): Kind<ForSerializer, B> =
         Serializer { fix().func(f(it)) }
       override fun <A, B, Z> divide(fa: Kind<ForSerializer, A>, fb: Kind<ForSerializer, B>, f: (Z) -> Tuple2<A, B>) =
         Serializer { z: Z ->
           val (a, b) = f(z)
           "A: ${fa.fix().func(a)}; B: ${fb.fix().func(b)}"
         }
        override fun <A> conquer(): Kind<ForSerializer, A> =
          Serializer { a: A ->
            ""
          }
     }
   }
}

fun main(args: Array<String>) {
  //sampleStart
   val emptySerializer: Serializer<Int> = Serializer.divisble().conquer().fix()

  val result = emptySerializer.func(1)
  //sampleEnd
  println(result)
}
```

### Main Combinators

#### conquer

Construct an empty value for any `Kind<F, A>`.

### Laws

Arrow provides `DivisibleLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `Divisible` instances.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Divisible` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Divisible
TypeClass(Divisible::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Divisible)