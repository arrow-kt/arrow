package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

/**
 * [OptionT]`<F, A>` is a light wrapper on an `F<`[Option]`<A>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [OptionT] is a monad transformer for [Option].
 */
@higherkind
data class OptionT<F, A>(val value: Kind<F, Option<A>>) : OptionTOf<F, A>, OptionTKindedJ<F, A> {

  companion object {

    operator fun <F, A> invoke(value: Kind<F, Option<A>>): OptionT<F, A> = OptionT(value)

    inline fun <F, A> just(AF: Applicative<F>, a: A): OptionT<F, A> = OptionT(AF.just(Some(a)))

    inline fun <F> none(AF: Applicative<F>): OptionT<F, Nothing> = OptionT(AF.just(None))

    inline fun <F, A> fromOption(AF: Applicative<F>, value: Option<A>): OptionT<F, A> =
      OptionT(AF.just(value))

    fun <F, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> OptionTOf<F, Either<A, B>>): OptionT<F, B> = MF.run {
      OptionT(tailRecM(a, {
        f(it).fix().value.map({
          it.fold({
            Right<Option<B>>(None)
          }, {
            it.map { Some(it) }
          })
        })
      }))
    }

  }

  inline fun <B> fold(FF: Functor<F>, crossinline default: () -> B, crossinline f: (A) -> B): Kind<F, B> = FF.run {
    value.map({ option -> option.fold(default, f) })
  }

  inline fun <B> cata(FF: Functor<F>, crossinline default: () -> B, crossinline f: (A) -> B): Kind<F, B> = fold(FF, default, f)

  fun <B> ap(MF: Monad<F>, ff: OptionTOf<F, (A) -> B>): OptionT<F, B> = ff.fix().flatMap(MF, { f -> map(MF, f) })

  inline fun <B> flatMap(MF: Monad<F>, crossinline f: (A) -> OptionT<F, B>): OptionT<F, B> = flatMapF(MF, { it -> f(it).value })

  inline fun <B> flatMapF(MF: Monad<F>, crossinline f: (A) -> Kind<F, Option<B>>): OptionT<F, B> = MF.run {
    OptionT(value.flatMap({ option -> option.fold({ just(None) }, f) }))
  }

  fun <B> liftF(FF: Functor<F>, fa: Kind<F, B>): OptionT<F, B> = FF.run {
    OptionT(fa.map({ Some(it) }))
  }

  inline fun <B> semiflatMap(MF: Monad<F>, crossinline f: (A) -> Kind<F, B>): OptionT<F, B> = flatMap(MF, { option -> liftF(MF, f(option)) })

  inline fun <B> map(FF: Functor<F>, crossinline f: (A) -> B): OptionT<F, B> = FF.run {
    OptionT(value.map({ it.map(f) }))
  }

  fun getOrElse(FF: Functor<F>, default: () -> A): Kind<F, A> = FF.run { value.map({ it.getOrElse(default) }) }

  inline fun getOrElseF(MF: Monad<F>, crossinline default: () -> Kind<F, A>): Kind<F, A> = MF.run {
    value.flatMap({ it.fold(default, { just(it) }) })
  }

  inline fun filter(FF: Functor<F>, crossinline p: (A) -> Boolean): OptionT<F, A> = FF.run {
    OptionT(value.map({ it.filter(p) }))
  }

  inline fun forall(FF: Functor<F>, crossinline p: (A) -> Boolean): Kind<F, Boolean> = FF.run {
    value.map({ it.forall(p) })
  }

  fun isDefined(FF: Functor<F>): Kind<F, Boolean> = FF.run {
    value.map({ it.isDefined() })
  }

  fun isEmpty(FF: Functor<F>): Kind<F, Boolean> = FF.run {
    value.map({ it.isEmpty() })
  }

  inline fun orElse(MF: Monad<F>, crossinline default: () -> OptionT<F, A>): OptionT<F, A> = orElseF(MF, { default().value })

  inline fun orElseF(MF: Monad<F>, crossinline default: () -> Kind<F, Option<A>>): OptionT<F, A> = MF.run {
    OptionT(value.flatMap {
      when (it) {
        is Some<A> -> MF.just(it)
        is None -> default()
      }
    })
  }

  inline fun <B> transform(FF: Functor<F>, crossinline f: (Option<A>) -> Option<B>): OptionT<F, B> = FF.run {
    OptionT(value.map({ f(it) }))
  }

  inline fun <B> subflatMap(FF: Functor<F>, crossinline f: (A) -> Option<B>): OptionT<F, B> = transform(FF, { it.flatMap(f) })

  fun <R> toLeft(FF: Functor<F>, default: () -> R): EitherT<F, A, R> =
    EitherT(cata(FF, { Right(default()) }, { Left(it) }))

  fun <L> toRight(FF: Functor<F>, default: () -> L): EitherT<F, L, A> =
    EitherT(cata(FF, { Left(default()) }, { Right(it) }))
}

inline fun <F, A, B> OptionTOf<F, A>.mapFilter(FF: Functor<F>, crossinline f: (A) -> Option<B>): OptionT<F, B> = FF.run {
  OptionT(fix().value.map({ it.flatMap(f) }))
}

fun <F, A> OptionTOf<F, A>.value(): Kind<F, Option<A>> = this.fix().value
