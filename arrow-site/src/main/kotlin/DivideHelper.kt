package com.example.domain

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.higherkind
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible

@higherkind
class Serializer<A>(val func: (A) -> String) : Kind<ForSerializer, A> {
  companion object {
    fun divide() = object : SerializerDivide {}
    fun divisible() = object : SerializerDivisible {}
    fun decidable() = object : SerializerDecidable {}
  }
}

interface SerializerDivide : Divide<ForSerializer> {
  override fun <A, B> Kind<ForSerializer, A>.contramap(f: (B) -> A): Kind<ForSerializer, B> =
    Serializer { this@contramap.fix().func(f(it)) }

  override fun <A, B, Z> divide(fa: Kind<ForSerializer, A>, fb: Kind<ForSerializer, B>, f: (Z) -> Tuple2<A, B>) =
    Serializer { z: Z ->
      val (a, b) = f(z)
      "A: ${fa.fix().func(a)}; B: ${fb.fix().func(b)}"
    }
}

interface SerializerDivisible : Divisible<ForSerializer>, SerializerDivide {
  override fun <A> conquer(): Kind<ForSerializer, A> = Serializer { "EMPTY" }
}

interface SerializerDecidable : Decidable<ForSerializer>, SerializerDivisible {
  override fun <A, B, Z> choose(fa: Kind<ForSerializer, A>, fb: Kind<ForSerializer, B>, f: (Z) -> Either<A, B>): Kind<ForSerializer, Z> =
    Serializer {
      f(it).fold({
        "LEFT: " + fa.fix().func(it)
      }, {
        "RIGHT: " + fb.fix().func(it)
      })
    }
}
