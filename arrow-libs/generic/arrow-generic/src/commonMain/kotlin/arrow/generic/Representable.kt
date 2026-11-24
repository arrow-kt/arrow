package arrow.generic

import arrow.core.Either
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * The representation of a type is defined as a sum tagged with the classes of all sealed inheritors,
 * where each choice comes with a product representing the primary properties of that inheritor.
 */
public typealias Representation = Generic.Sum<Generic.Product<*, KProperty1<*, *>>, KClass<*>>

private fun <T, A> T.property(property: KProperty1<T, A>): Tagged<A, KProperty1<T, A>> = Tagged(property, property.get(this))

/**
 * Creates the representation of an object or class without primary properties.
 */
public fun <T> T.properties(): Repr.Nil = nil

/**
 * Creates the representation of a class with one primary property [a].
 */
public fun <T, A> T.properties(a: KProperty1<T, A>): Repr.And<A, Repr.Nil> =
  property(a) and nil

/**
 * Creates the representation of a class with two primary properties [a], [b].
 */
public fun <T, A, B> T.properties(a: KProperty1<T, A>, b: KProperty1<T, B>): Repr.And<A, Repr.And<B, Repr.Nil>> =
  property(a) and properties(b)

/**
 * Creates the representation of a class with three primary properties [a], [b], [c].
 */
public fun <T, A, B, C> T.properties(a: KProperty1<T, A>, b: KProperty1<T, B>, c: KProperty1<T, C>): Repr.And<A, Repr.And<B, Repr.And<C, Repr.Nil>>> =
  property(a) and properties(b, c)

/**
 * Tags the representation of a class.
 */
public inline fun <T, reified R> T.withClass(): Tagged<T, KClass<*>> = Tagged(R::class, this)

/**
 * Defines the conversion from [T] to its generic representation [R], and vice versa.
 */
public interface Repr<T, R : Representation> {
  public fun T.toGeneric(): R
  public fun R.fromGeneric(): T

  public typealias And<Value, Rest> = Generic.And<*, KProperty1<*, *>, Value, KProperty1<*, *>, Rest>
  public typealias Nil = Generic.Nil
  public typealias Or<Value, Rest> = Generic.Or<Generic.Product<*, KProperty1<*, *>>, KClass<*>, Value, KClass<*>, Rest>
  public typealias Void = Generic.Void
}

public object Representables {
  public typealias PairRepr<A, B> = Repr.Or<Repr.And<A, Repr.And<B, Repr.Nil>>, Repr.Void>
  public fun <A, B> pair(): Repr<Pair<A, B>, PairRepr<A, B>> = object : Repr<Pair<A, B>, PairRepr<A, B>> {
    override fun Pair<A, B>.toGeneric(): PairRepr<A, B> =
      properties(Pair<A, B>::first, Pair<A, B>::second).withClass<_, Pair<A, B>>().`this`()

    override fun PairRepr<A, B>.fromGeneric(): Pair<A, B> = when (this) {
      is Generic.Or.This -> Pair(this.value.value, this.value.rest.value)
      is Generic.Or.That -> this.that.absurd()
    }
  }

  public typealias EitherRepr<A, B> = Repr.Or<Repr.And<A, Repr.Nil>, Repr.Or<Repr.And<B, Repr.Nil>, Repr.Void>>
  public fun <A, B> either(): Repr<Either<A, B>, EitherRepr<A, B>> = object : Repr<Either<A, B>, EitherRepr<A, B>> {
    override fun Either<A, B>.toGeneric(): EitherRepr<A, B> = when (this) {
      is Either.Left<A> -> properties(Either.Left<A>::value).withClass<_, Either.Left<A>>().`this`()
      is Either.Right<B> -> properties(Either.Right<B>::value).withClass<_, Either.Right<B>>().`this`().that()
    }

    override fun EitherRepr<A, B>.fromGeneric(): Either<A, B> = when (this) {
      is Generic.Or.This -> Either.Left(this.value.value)
      is Generic.Or.That -> when (this.that) {
        is Generic.Or.This -> Either.Right(this.that.value.value)
        is Generic.Or.That -> this.that.that.absurd()
      }
    }
  }
}
