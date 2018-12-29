package arrow.data

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

fun <F, A, B> EitherTOf<F, A, B>.value(): Kind<F, Either<A, B>> = fix().value()

/**
 * [EitherT]`<F, A, B>` is a light wrapper on an `F<`[Either]`<A, B>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [EitherT] is a monad transformer for [Either].
 */
@higherkind
data class EitherT<F, A, B>(private val value: Kind<F, Either<A, B>>) : EitherTOf<F, A, B>, EitherTKindedJ<F, A, B> {

  companion object {

    operator fun <F, A, B> invoke(value: Kind<F, Either<A, B>>): EitherT<F, A, B> =
      EitherT(value)

    fun <F, A, B> just(MF: Applicative<F>, b: B): EitherT<F, A, B> =
      right(MF, b)

    fun <F, L, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> EitherTOf<F, L, Either<A, B>>): EitherT<F, L, B> =
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

    fun <F, A, B> right(MF: Applicative<F>, b: B): EitherT<F, A, B> =
      EitherT(MF.just(Right(b)))

    fun <F, A, B> left(MF: Applicative<F>, a: A): EitherT<F, A, B> =
      EitherT(MF.just(Left(a)))

    fun <F, A, B> fromEither(AP: Applicative<F>, value: Either<A, B>): EitherT<F, A, B> =
      EitherT(AP.just(value))

    fun <F, A, B> liftF(FF: Functor<F>, fa: Kind<F, B>): EitherT<F, A, B> = FF.run {
      EitherT(fa.map(::Right))
    }

  }

  fun value(): Kind<F, Either<A, B>> = value

  inline fun <C> fold(FF: Functor<F>, crossinline l: (A) -> C, crossinline r: (B) -> C): Kind<F, C> = FF.run {
    value().map { either -> either.fold(l, r) }
  }

  fun <C> flatMap(MF: Monad<F>, f: (B) -> EitherTOf<F, A, C>): EitherT<F, A, C> =
    flatMapF(MF) { it -> f(it).value() }

  fun <C> flatMapF(MF: Monad<F>, f: (B) -> Kind<F, Either<A, C>>): EitherT<F, A, C> = MF.run {
    EitherT(value.flatMap { either -> either.fold({ MF.just(Left(it)) }, { f(it) }) })
  }

  fun <C> cata(FF: Functor<F>, l: (A) -> C, r: (B) -> C): Kind<F, C> =
    fold(FF, l, r)

  fun <C> liftF(FF: Functor<F>, fa: Kind<F, C>): EitherT<F, A, C> = FF.run {
    EitherT(fa.map { Right(it) })
  }

  fun <C> semiflatMap(MF: Monad<F>, f: (B) -> Kind<F, C>): EitherT<F, A, C> =
    flatMap(MF) { liftF(MF, f(it)) }

  fun <C> map(FF: Functor<F>, f: (B) -> C): EitherT<F, A, C> = FF.run {
    EitherT(value.map { it.map(f) })
  }

  fun <C> mapLeft(FF: Functor<F>, f: (A) -> C): EitherT<F, C, B> = FF.run {
    EitherT(value.map { it.mapLeft(f) })
  }

  fun exists(FF: Functor<F>, p: (B) -> Boolean): Kind<F, Boolean> = FF.run {
    value.map { it.exists(p) }
  }

  fun <C, D> transform(FF: Functor<F>, f: (Either<A, B>) -> Either<C, D>): EitherT<F, C, D> = FF.run {
    EitherT(value.map { f(it) })
  }

  fun <C> subflatMap(FF: Functor<F>, f: (B) -> Either<A, C>): EitherT<F, A, C> =
    transform(FF) { it.flatMap(f = f) }

  fun toOptionT(FF: Functor<F>): OptionT<F, B> = FF.run {
    OptionT(value.map { it.toOption() })
  }

  fun combineK(MF: Monad<F>, y: EitherTOf<F, A, B>): EitherT<F, A, B> = MF.run {
    EitherT(value.flatMap {
      when (it) {
        is Either.Left -> y.value()
        is Either.Right -> just(it)
      }
    })
  }

  fun <C> ap(AF: Applicative<F>, ff: EitherTOf<F, A, (B) -> C>): EitherT<F, A, C> =
    EitherT(AF.map(ff.value(), value) { (a, b) ->
      b.flatMap { bb ->
        a.map { f -> f(bb) }
      }
    })

}
