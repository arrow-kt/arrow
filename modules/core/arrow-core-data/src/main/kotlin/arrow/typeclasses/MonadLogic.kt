package arrow.typeclasses

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.fix

/**
 *  MonadLog is a typeclass that extends a MonadPlus.
 */
interface MonadLogic<F> : MonadPlus<F> {

  fun <A> Kind<F, A>.splitM(): Kind<F, Option<Tuple2<Kind<F, A>, A>>>

  fun <A> Kind<F, A>.interleave(other: Kind<F, A>): Kind<F, A> =
    this.splitM().flatMap { option ->
      option.fold({ other }, { (fa, a) -> just(a).plusM(other.interleave(fa)) })
    }

  fun <A, B> Kind<F, A>.unweave(ffa: (A) -> Kind<F, B>): Kind<F, B> =
    splitM().flatMap { option ->
      option.fold({ zeroM<Tuple2<Kind<F, A>, A>>() }, { just(it) })
    }.flatMap { (fa, a) ->
      ffa(a).interleave(fa.unweave(ffa))
    }

  fun <A, B> Kind<F, A>.ifThen(fb: Kind<F, B>, ffa: (A) -> Kind<F, B>): Kind<F, B> =
    splitM().flatMap { option ->
      option.fold({ fb }, { (fa, a) -> ffa(a).plusM(fa.flatMap(ffa)) })
    }

  fun <A> Kind<F, A>.once(): Kind<F, A> =
    splitM().flatMap { option ->
      option.fold({ zeroM<A>() }, { just(it.b) })
    }

  fun <A> Kind<F, A>.voidIfValue(): Kind<F, Unit> =
    once().ifThen(unit()) { zeroM() }
}

// TODO: this should be direct part of the MonadLogic typeclass (https://github.com/arrow-kt/arrow/pull/2047#discussion_r378201777).
//  Couldn't add it due to issues with the generated code. Should be reworked when arrow-meta is available.
fun <F, A> Kind<ForOption, Tuple2<Kind<F, A>, A>>.reflect(ML: MonadLogic<F>): Kind<F, A> =
  fix().fold({ ML.zeroM() }, { ML.run { just(it.b).plusM(it.a) } })
