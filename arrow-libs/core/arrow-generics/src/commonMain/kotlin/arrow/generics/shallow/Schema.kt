package arrow.generics.shallow

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import arrow.generics.shallow.functions.Function
import arrow.generics.shallow.functions.Function1
import arrow.generics.shallow.functions.Function2

public typealias Schema<Repr> = SumSchema<Repr>
public typealias SchemasOf = Done
public typealias Schema1<T, A, Rest> = Or<T, Field<A, End>, Rest>
public typealias Schema2<T, A, B, Rest> = Or<T, Field<A, Field<B, End>>, Rest>


public sealed interface SumSchema<out Repr : SumRepr>
public data class Or<A : Any, out Fields : ProductRepr, out Rest : SumRepr>(
  val klass: KClass<A>,
  val just: (args: Array<out Any?>) -> A,
  val fields: ProductSchema<Fields>,
  val rest: SumSchema<Rest>
) : SumSchema<Choice<Fields, Rest>>

public sealed interface ProductSchema<out Repr : ProductRepr>

public data class And<T, out A, out Rest : ProductRepr>(
  val field: Property<T, A>,
  val rest: ProductSchema<Rest>
) : ProductSchema<Field<A, Rest>> {
  public fun <B> and(field: Property<T, B>): And<T, B, Field<A, Rest>> =
    And(field, this)
}

public sealed class ArgumentList<out Repr : ProductRepr>

public data class Arg<out A, out Rest : ProductRepr>(
  val value: A,
  val rest: ArgumentList<Rest>
) : ArgumentList<Field<A, Rest>>()

public object Done : SumSchema<End>, ProductSchema<End>, ArgumentList<End>()

// utility functions

public fun <Repr : ProductRepr> ArgumentList<Repr>.toList(): List<Any?> = when (this) {
  is Arg<*, *> -> listOf(value) + rest.toList()
  Done -> emptyList()
}

public fun <T : Any, Repr : SumRepr> Schema<Repr>.introduce(from: T): Or<*, *, *>? = when (this) {
  is Or<*, *, *> -> if (klass.isInstance(from)) this else rest.introduce(from)
  Done -> null
}

public fun <A : Any, Fields : ProductRepr, Rest : SumRepr> Or<A, Fields, Rest>.build(
  args: ArgumentList<Fields>
): A = just(args.toList().toTypedArray())

public data class Property<T, out A>(
  val name: String,
  @PublishedApi
  internal val invoke: (T) -> A
) {
  public fun unsafeInvoke(t: Any?): A =
    invoke.invoke(t as T)
}

public inline fun <reified A : Any, B, Rest : SumRepr> SumSchema<Rest>.schema(
  noinline function: (Nothing) -> A,
  field: KProperty1<A, B>
): Or<A, Field<B, End>, Rest> =
  Or(A::class, Function1(function)::f, And(Property(field.name, field), Done), this)

public inline fun <reified A : Any, Fields : ProductRepr, Rest : SumRepr> SumSchema<Rest>.schema(
  noinline function: (Nothing, Nothing) -> A,
  fields: ProductSchema<Fields>
): Or<A, Fields, Rest> =
  Or(A::class, Function2(function)::f, fields, this)

public operator fun <T, A, B> KProperty1<T, A>.div(next: KProperty1<T, B>): And<T, A, Field<B, End>> =
  And(Property(this.name, this), And(Property(next.name, next), Done))

public operator fun <T, A, B, Rest : ProductRepr> And<T, A, Rest>.div(p: KProperty1<T, B>): And<T, B, Field<A, Rest>> =
  And(Property(p.name, p), this)

public operator fun <T, A, Rest : ProductRepr> KProperty1<T, A>.div(rest: ProductSchema<Rest>): And<T, A, Rest> =
  And(Property(name, this), rest)


