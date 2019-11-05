package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

fun <A, F, B> EitherTOf<A, F, B>.value(): Kind<F, Either<A, B>> = fix().value()

/**
 * [EitherT]`<A, F, B>` is a light wrapper on an `F<`[Either]`<A, B>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [EitherT] is a monad transformer for [Either].
 */
@higherkind
data class EitherT<A, F, B>(private val value: Kind<F, Either<A, B>>) : EitherTOf<A, F, B>, EitherTKindedJ<A, F, B> {

  companion object {

    operator fun <A, F, B> invoke(value: Kind<F, Either<A, B>>): EitherT<A, F, B> =
      EitherT(value)

    fun <A, F, B> just(MF: Applicative<F>, b: B): EitherT<A, F, B> =
      right(MF, b)

    fun <L, F, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> EitherTOf<L, F, Either<A, B>>): EitherT<L, F, B> =
      EitherT(MF.tailRecM(a) {
        val value = f(it).value()
        MF.run {
          value.map { recursionControl ->
            when (recursionControl) {
              is Either.Left -> Right(Left(recursionControl.a))
              is Either.Right -> {
                val b: Either<A, B> = recursionControl.b
                when (b) {
                  is Either.Left -> Left(b.a)
                  is Either.Right -> Right(Right(b.b))
                }
              }
            }
          }
        }
      })

    fun <A, F, B> right(MF: Applicative<F>, b: B): EitherT<A, F, B> =
      EitherT(MF.just(Right(b)))

    fun <A, F, B> left(MF: Applicative<F>, a: A): EitherT<A, F, B> =
      EitherT(MF.just(Left(a)))

    fun <A, F, B> fromEither(AP: Applicative<F>, value: Either<A, B>): EitherT<A, F, B> =
      EitherT(AP.just(value))

    fun <A, F, B> liftF(FF: Functor<F>, fa: Kind<F, B>): EitherT<A, F, B> = FF.run {
      EitherT(fa.map(::Right))
    }
  }

  fun value(): Kind<F, Either<A, B>> = value

  inline fun <C> fold(FF: Functor<F>, crossinline l: (A) -> C, crossinline r: (B) -> C): Kind<F, C> = FF.run {
    value().map { either -> either.fold(l, r) }
  }

  fun <C> flatMap(MF: Monad<F>, f: (B) -> EitherTOf<A, F, C>): EitherT<A, F, C> =
    flatMapF(MF) { f(it).value() }

  fun <C> flatMapF(MF: Monad<F>, f: (B) -> Kind<F, Either<A, C>>): EitherT<A, F, C> = MF.run {
    EitherT(value.flatMap { either -> either.fold({ MF.just(Left(it)) }, { f(it) }) })
  }

  fun <C> cata(FF: Functor<F>, l: (A) -> C, r: (B) -> C): Kind<F, C> =
    fold(FF, l, r)

  fun <C> liftF(FF: Functor<F>, fa: Kind<F, C>): EitherT<A, F, C> = FF.run {
    EitherT(fa.map { Right(it) })
  }

  fun <C> semiflatMap(MF: Monad<F>, f: (B) -> Kind<F, C>): EitherT<A, F, C> =
    flatMap(MF) { liftF(MF, f(it)) }

  fun <C> map(FF: Functor<F>, f: (B) -> C): EitherT<A, F, C> = FF.run {
    EitherT(value.map { it.map(f) })
  }

  fun <C> mapLeft(FF: Functor<F>, f: (A) -> C): EitherT<C, F, B> = FF.run {
    EitherT(value.map { it.mapLeft(f) })
  }

  fun exists(FF: Functor<F>, p: (B) -> Boolean): Kind<F, Boolean> = FF.run {
    value.map { it.exists(p) }
  }

  fun <C, D> transform(FF: Functor<F>, f: (Either<A, B>) -> Either<C, D>): EitherT<C, F, D> = FF.run {
    EitherT(value.map { f(it) })
  }

  fun <C> subflatMap(FF: Functor<F>, f: (B) -> Either<A, C>): EitherT<A, F, C> =
    transform(FF) { it.flatMap(f = f) }

  fun toOptionT(FF: Functor<F>): OptionT<F, B> = FF.run {
    OptionT(value.map { it.toOption() })
  }

  fun combineK(MF: Monad<F>, y: EitherTOf<A, F, B>): EitherT<A, F, B> = MF.run {
    EitherT(value.flatMap {
      when (it) {
        is Either.Left -> y.value()
        is Either.Right -> just(it)
      }
    })
  }

  fun <C> ap(AF: Applicative<F>, ff: EitherTOf<A, F, (B) -> C>): EitherT<A, F, C> =
    EitherT(AF.map(ff.value(), value) { (a, b) ->
      b.flatMap { bb ->
        a.map { f -> f(bb) }
      }
    })
}
