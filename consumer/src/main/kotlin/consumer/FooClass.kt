package consumer

import arrow.not
import arrow.Kind
import arrow.`*`
import arrow.extension

/** HigherKinds **/
sealed class Option<out A> {
  object None : Option<Nothing>()
  data class Some<out A>(val value: A) : Option<A>()

  fun <B> map(f: (A) -> B): Option<B> =
    when (this) {
      None -> None
      is Some -> Some(f(value))
    }

  fun <B> flatMap(f: (A) -> Option<B>): Option<B> =
    when (this) {
      None -> None
      is Some -> f(value)
    }
}

sealed class Either<out A, out B> {
  class Left<out A> : Either<A, Nothing>()
  class Right<out B>(val value: B) : Either<Nothing, B>()
}

class Kleisli<out F, out D, out A>

/** Type Classes **/

interface Functor<F> {
  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
  fun <A, B> Kind<F, A>.flatMap(f: (A) -> Kind<F, B>): Kind<F, B>
}

//@extension fun functorForOptionFun(): Functor<ForOption> = object : Functor<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}
//
//@extension val FunctorForOptionVal: Functor<ForOption> = object : Functor<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}
//
//@extension class FunctorForOptionClass: Functor<ForOption> {
//  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
//    (this as Option<A>).map(f)
//}

@extension
object FunctorForOption : Functor<ForOption> {
  override fun <A, B> OptionOf<A>.map(f: (A) -> B): Option<B> =
    (this as Option<A>).map(f)

  override fun <A, B> OptionOf<A>.flatMap(f: (A) -> OptionOf<B>): Option<B> =
    (this as Option<A>).flatMap(f)
}

fun <F> Kind<F, Int>.addOne(FF: Functor<F> = `*`): Kind<F, Int> =
  map { it + 1 }

class Service {
  fun <F> Kind<F, Int>.addOne(FF: Functor<F> = `*`): Kind<F, Int> =
    map { it + 1 }
}

fun testConversionSimple(): Option<Int> =
  Option.Some(!Option.Some(!Option.Some(!Option.Some(1))))

fun testConversionInfix(): Option<Int> =
  Option.Some(!Option.Some(1) + !Option.Some(1))

/**
 BLOCK_BODY
  VAR name:x type:kotlin.Int [val]
    CALL 'public final fun not <A> (): A of arrow.not declared in arrow' type=kotlin.Int origin=EXCL
      <A>: kotlin.Int
      $receiver: CONSTRUCTOR_CALL 'public constructor <init> (value: A of consumer.Option.Some) [primary] declared in consumer.Option.Some' type=consumer.Option.Some<kotlin.Int> origin=null
        <class: A>: kotlin.Int
        value: CONST Int type=kotlin.Int value=1
  RETURN type=kotlin.Nothing from='public final fun testConversion (): consumer.Option<kotlin.Int> declared in consumer'
    CONSTRUCTOR_CALL 'public constructor <init> (value: A of consumer.Option.Some) [primary] declared in consumer.Option.Some' type=consumer.Option.Some<kotlin.Int> origin=null
      <class: A>: kotlin.Int
      value: GET_VAR 'val x: kotlin.Int [val] declared in consumer.testConversion' type=kotlin.Int origin=null
 */
fun testConversion(): Option<Int> {
  val x = !Option.Some(1)
  return Option.Some(x)
}

/*
BLOCK_BODY
  RETURN type=kotlin.Nothing from='public final fun testConversionFlatMap (): consumer.Option<kotlin.Int> declared in consumer'
    CALL 'public final fun flatMap <B> (f: kotlin.Function1<A of consumer.Option.Some, consumer.Option<B of consumer.Option.Some.flatMap>>): consumer.Option<B of consumer.Option.Some.flatMap> declared in consumer.Option.Some' type=consumer.Option<kotlin.Int> origin=null
      <B>: kotlin.Int
      $this: CONSTRUCTOR_CALL 'public constructor <init> (value: A of consumer.Option.Some) [primary] declared in consumer.Option.Some' type=consumer.Option.Some<kotlin.Int> origin=null
        <class: A>: kotlin.Int
        value: CONST Int type=kotlin.Int value=1
      f: BLOCK type=kotlin.Function1<kotlin.Int, consumer.Option.Some<kotlin.Int>> origin=LAMBDA
        FUN LOCAL_FUNCTION_FOR_LAMBDA name:<anonymous> visibility:local modality:FINAL <> (x:kotlin.Int) returnType:consumer.Option.Some<kotlin.Int>
          VALUE_PARAMETER name:x index:0 type:kotlin.Int
          BLOCK_BODY
            VAR name:x type:kotlin.Int [val]
              GET_VAR 'x: kotlin.Int declared in consumer.testConversionFlatMap.<anonymous>' type=kotlin.Int origin=null
            RETURN type=kotlin.Nothing from='local final fun <anonymous> (x: kotlin.Int): consumer.Option.Some<kotlin.Int> declared in consumer.testConversionFlatMap'
              CONSTRUCTOR_CALL 'public constructor <init> (value: A of consumer.Option.Some) [primary] declared in consumer.Option.Some' type=consumer.Option.Some<kotlin.Int> origin=null
                <class: A>: kotlin.Int
                value: GET_VAR 'val x: kotlin.Int [val] declared in consumer.testConversionFlatMap.<anonymous>' type=kotlin.Int origin=null
        FUNCTION_REFERENCE 'local final fun <anonymous> (x: kotlin.Int): consumer.Option.Some<kotlin.Int> declared in consumer.testConversionFlatMap' type=kotlin.Function1<kotlin.Int, consumer.Option.Some<kotlin.Int>> origin=LAMBDA
 */
fun testConversionFlatMap(): Option<Int> =
  Option.Some(1).flatMap { x ->
    val x = x
    Option.Some(x)
  }

