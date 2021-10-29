package arrow.generics.shallow.schema

import arrow.generics.shallow.* // ktlint-disable no-wildcard-imports
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

public typealias Schema<Repr> = SumSchema<Repr>
public sealed interface SumSchema<out Repr : SumRepr>
public data class Or<A : Any, out Fields : ProductRepr, out Rest : SumRepr>(
  val klass: KClass<A>,
  val fields: ProductSchema<Fields>,
  val rest: SumSchema<Rest>
) : SumSchema<Choice<Fields, Rest>>

public sealed interface ProductSchema<out Repr : ProductRepr>
public data class And<out A, out Rest : ProductRepr>(
  val field: KCallable<A>,
  val rest: ProductSchema<Rest>
) : ProductSchema<Field<A, Rest>>

public sealed interface ArgumentList<out Repr : ProductRepr>
public data class Arg<out A : Any, out Rest : ProductRepr>(
  val value: A,
  val rest: ArgumentList<Rest>
) : ArgumentList<Field<A, Rest>>

public object Done : SumSchema<End>, ProductSchema<End>, ArgumentList<End>

// utility functions

public fun <Repr : ProductRepr> ArgumentList<Repr>.toList(): List<Any> = when (this) {
  is Arg<*, *> -> listOf(value) + rest.toList()
  Done -> emptyList<Any>()
}

public fun <T : Any, Repr : SumRepr> Schema<Repr>.introduce(from: T): Or<*, *, *>? = when (this) {
  is Or<*, *, *> -> if (klass.isInstance(from)) this else rest.introduce(from)
  Done -> null
}

public fun <A : Any, Fields : ProductRepr, Rest : SumRepr> Or<A, Fields, Rest>.build(
  args: ArgumentList<Fields>
): A = klass.primaryConstructor!!.call(*args.toList().toTypedArray())

public fun <T, A, Rest : ProductRepr> And<A, Rest>.get(from: T): A =
  field.call(from)
