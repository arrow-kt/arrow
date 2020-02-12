package arrow.typeclasses

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.fix
import arrow.core.identity

interface MonadLogic<F> : MonadPlus<F> {

  fun <A> Kind<F, A>.splitM(): Kind<F, Option<Tuple2<Kind<F, A>, A>>>

  fun <A> Kind<F, A>.interleave(fa: Kind<F, A>): Kind<F, A> =
    this.splitM().flatMap { option ->
      option.map { (fa, a) ->
        just(a).plusM(fa.interleave(fa))
      }.fold({ fa }, ::identity)
    }

  fun <A, B> Kind<F, A>.unweave(ffa: (A) -> Kind<F, B>): Kind<F, B> =
    splitM().flatMap { option ->
      option.fold({ zeroM<Tuple2<Kind<F, A>, A>>() }, { just(it) })
    }.flatMap { (fa, a) ->
      ffa(a).interleave(fa.flatMap(ffa))
    }

  fun <A, B> Kind<F, A>.ifThen(fb: Kind<F, B>, ffa: (A) -> Kind<F, B>): Kind<F, B> =
    splitM().flatMap { option ->
      option.map { (fa, a) ->
        ffa(a).plusM(fa.flatMap { ffa(it) })
      }.fold({ fb }, ::identity)
    }

  fun <A> Kind<F, A>.once(): Kind<F, A> =
    splitM().flatMap { option ->
      option.fold({ zeroM<A>() }, { just(it.b) })
    }

  fun <A> Kind<F, A>.unitIfValue(): Kind<F, Unit> =
    once().ifThen(unit()) { zeroM() }
}

fun <F, A> Kind<ForOption, Tuple2<Kind<F, A>, A>>.reflect(ML: MonadLogic<F>): Kind<F, A> =
  fix().fold({ ML.zeroM() }, { ML.run { just(it.b).plusM(it.a) } })
