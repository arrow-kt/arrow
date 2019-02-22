---
layout: docs
title: Decidable
permalink: /docs/arrow/typeclasses/decidable/
redirect_from:
  - /docs/typeclasses/decidable
---

## Decidable

{:.beginner}
beginner
 
The `Decidable` is a typeclass modeling contravariant decision. `Decidable` is the contravariant version of `Alternative`.
`Decidable` basically states: Given a `Kind<F, A>` and a `Kind<F, B>` and a way to turn `C` into either `A` or `B` it gives you a `Kind<F, C>`

With decidable we can extend our serializer from the `Divide`/`Divisible` examples, by adding a decision process while serializing.
Consider this example where a type is either an int or a string.

```kotlin:ank:playground
 import arrow.Kind
 import arrow.core.Either
 import arrow.core.Tuple2
 import arrow.core.identity
 import arrow.core.right
 import arrow.typeclasses.Decidable

 // Boilerplate that @higherkind usually generates
 class ForSerializer

 fun <A> Kind<ForSerializer, A>.fix() = this as Serializer<A>
 class Serializer<A>(val func: (A) -> String) : Kind<ForSerializer, A> {
   companion object {
    fun decidable() = object : Decidable<ForSerializer> {
      override fun <A, B> Kind<ForSerializer, A>.contramap(f: (B) -> A): Kind<ForSerializer, B> =
        Serializer { fix().func(f(it)) }

      override fun <A, B, Z> divide(fa: Kind<ForSerializer, A>, fb: Kind<ForSerializer, B>, f: (Z) -> Tuple2<A, B>) =
        Serializer { z: Z ->
          val (a, b) = f(z)
          "A: ${fa.fix().func(a)}; B: ${fb.fix().func(b)}"
      }

      override fun <A> conquer(): Kind<ForSerializer, A> =
        Serializer { "EMPTY" }

      override fun <A, B, Z> choose(fa: Kind<ForSerializer, A>, fb: Kind<ForSerializer, B>, f: (Z) -> Either<A, B>): Kind<ForSerializer, Z> =
        Serializer {
          f(it).fold({
            "LEFT: " + fa.fix().func(it)
          }, {
            "RIGHT: " + fb.fix().func(it)
          })
        }
      }
   }
 }

 val stringSerializer = Serializer<String> { "STRING: $it" }
 val intSerializer = Serializer<Int> { "INT: $it" }

 fun main(args: Array<String>) {
   //sampleStart
   val stringOrInt: Serializer<Either<String, Int>> = Serializer.decidable()
      .choose<String, Int, Either<String, Int>>(stringSerializer, intSerializer, ::identity).fix()

   val stringOrIntEither = 1.right()
   val result = stringOrInt.func(stringOrIntEither)
   //sampleEnd
   println(result)
 }
```

### Main Combinators

#### choose

Constructs a `Kind<F, C>` from a `Kind<F, A>`, a `Kind<F, B>` and a function `(C) -> Either<A, B>`.
The intuition here is that the function "decides" what value to use by specifying it with the either constructed.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }}) to provide your own `Decidable` instances for custom datatypes.

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Decidable
TypeClass(Decidable::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Decidable)