package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

fun <F, A> OptionTOf<F, A>.value(): Kind<F, Option<A>> = this.fix().value()

/**
 * [OptionT]`<F, A>` is a light wrapper on an `F<`[Option]`<A>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [OptionT] is a monad transformer for [Option].
 */
@higherkind
data class OptionT<F, A>(private val value: Kind<F, Option<A>>) : OptionTOf<F, A>, OptionTKindedJ<F, A> {

  companion object {

    operator fun <F, A> invoke(value: Kind<F, Option<A>>): OptionT<F, A> = OptionT(value)

    fun <F, A> just(AF: Applicative<F>, a: A): OptionT<F, A> = OptionT(AF.just(Some(a)))

    fun <F> none(AF: Applicative<F>): OptionT<F, Nothing> = OptionT(AF.just(None))

    fun <F, A> fromOption(AF: Applicative<F>, value: Option<A>): OptionT<F, A> =
      OptionT(AF.just(value))

    fun <F, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> OptionTOf<F, Either<A, B>>): OptionT<F, B> =
      OptionT(MF.tailRecM(a) { aa ->
        MF.run {
          f(aa).value().map {
            it.fold({
              Right<Option<B>>(None)
            }, { ab ->
              ab.map(::Some)
            })
          }
        }
      })

    fun <F, A> liftF(FF: Functor<F>, fa: Kind<F, A>): OptionT<F, A> = FF.run {
      OptionT(fa.map { Some(it) })
    }

  }

  fun value(): Kind<F, Option<A>> = value

  inline fun <B> fold(FF: Functor<F>, crossinline default: () -> B, crossinline f: (A) -> B): Kind<F, B> = FF.run {
    value().map { option -> option.fold(default, f) }
  }

  fun <B> cata(FF: Functor<F>, default: () -> B, f: (A) -> B): Kind<F, B> = fold(FF, default, f)

  fun <B> ap(AF: Applicative<F>, ff: OptionTOf<F, (A) -> B>): OptionT<F, B> =
    OptionT(AF.map(ff.value(), value) { (a, b) ->
      b.flatMap { bb ->
        a.map { f -> f(bb) }
      }
    })

  fun <B> flatMap(MF: Monad<F>, f: (A) -> OptionTOf<F, B>): OptionT<F, B> = flatMapF(MF) { f(it).value() }

  fun <B> flatMapF(MF: Monad<F>, f: (A) -> Kind<F, Option<B>>): OptionT<F, B> = MF.run {
    OptionT(value.flatMap { option -> option.fold({ just(None) }, f) })
  }

  fun <B> liftF(FF: Functor<F>, fa: Kind<F, B>): OptionT<F, B> = FF.run {
    OptionT(fa.map { Some(it) })
  }

  fun <B> semiflatMap(MF: Monad<F>, f: (A) -> Kind<F, B>): OptionT<F, B> = flatMap(MF) { option -> liftF(MF, f(option)) }

  fun <B> map(FF: Functor<F>, f: (A) -> B): OptionT<F, B> = FF.run {
    OptionT(value.map { it.map(f) })
  }

  fun getOrElse(FF: Functor<F>, default: () -> A): Kind<F, A> = FF.run { value.map { it.getOrElse(default) } }

  fun getOrElseF(MF: Monad<F>, default: () -> Kind<F, A>): Kind<F, A> = MF.run {
    value.flatMap { it.fold(default, ::just) }
  }

  fun filter(FF: Functor<F>, p: (A) -> Boolean): OptionT<F, A> = FF.run {
    OptionT(value.map { it.filter(p) })
  }

  fun forall(FF: Functor<F>, p: (A) -> Boolean): Kind<F, Boolean> = FF.run {
    value.map { it.forall(p) }
  }

  fun isDefined(FF: Functor<F>): Kind<F, Boolean> = FF.run {
    value.map(Option<A>::isDefined)
  }

  fun isEmpty(FF: Functor<F>): Kind<F, Boolean> = FF.run {
    value.map(Option<A>::isEmpty)
  }

  fun orElse(MF: Monad<F>, default: () -> OptionT<F, A>): OptionT<F, A> = orElseF(MF) { default().value }

  fun orElseF(MF: Monad<F>, default: () -> Kind<F, Option<A>>): OptionT<F, A> = MF.run {
    OptionT(value.flatMap {
      when (it) {
        is Some<A> -> MF.just(it)
        is None -> default()
      }
    })
  }

  fun <B> transform(FF: Functor<F>, f: (Option<A>) -> Option<B>): OptionT<F, B> = FF.run {
    OptionT(value.map(f))
  }

  fun <B> subflatMap(FF: Functor<F>, f: (A) -> OptionOf<B>): OptionT<F, B> = transform(FF) { it.flatMap(f) }

  fun <R> toLeft(FF: Functor<F>, default: () -> R): EitherT<F, A, R> =
    EitherT(cata(FF, { Right(default()) }, { Left(it) }))

  fun <L> toRight(FF: Functor<F>, default: () -> L): EitherT<F, L, A> =
    EitherT(cata(FF, { Left(default()) }, { Right(it) }))
}

fun <F, A, B> OptionTOf<F, A>.mapFilter(FF: Functor<F>, f: (A) -> OptionOf<B>): OptionT<F, B> = FF.run {
  OptionT(value().map { it.flatMap(f) })
}
