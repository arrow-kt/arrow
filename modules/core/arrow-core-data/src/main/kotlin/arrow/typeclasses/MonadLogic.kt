package arrow.typeclasses

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.fix
import arrow.core.identity

interface MonadLogic<F> : MonadPlus<F> {

  fun <A> Kind<F, A>.msplit(): Kind<F, Option<Tuple2<Kind<F, A>, A>>>

  fun <A> Kind<F, A>.interleave(fa: Kind<F, A>): Kind<F, A> =
    this.msplit().flatMap { option ->
      option.map { (fa, a) ->
        just(a).mplus(fa.interleave(fa))
      }.fold({ fa }, ::identity)
    }

  fun <A, B> Kind<F, A>.fairConjunction(ffa: (A) -> Kind<F, B>): Kind<F, B> =
    msplit().flatMap { option ->
      option.fold({ mzero<Tuple2<Kind<F, A>, A>>() }, { just(it) })
    }.flatMap { (fa, a) ->
      ffa(a).interleave(fa.flatMap(ffa))
    }

  fun <A, B> Kind<F, A>.ifte(fb: Kind<F, B>, ffa: (A) -> Kind<F, B>): Kind<F, B> =
    msplit().flatMap { option ->
      option.map { (fa, a) ->
        ffa(a).mplus(fa.flatMap { ffa(it) })
      }.fold({ fb }, ::identity)
    }

  fun <A> Kind<F, A>.once(): Kind<F, A> =
    msplit().flatMap { option ->
      option.fold({ mzero<A>() }, { just(it.b) })
    }

  fun <A> Kind<F, A>.lnot(): Kind<F, Unit> =
    once().ifte(unit()) { mzero() }
}

fun <F, A> Kind<ForOption, Tuple2<Kind<F, A>, A>>.reflect(ML: MonadLogic<F>): Kind<F, A> =
  fix().fold({ ML.mzero() }, { ML.run { just(it.b).mplus(it.a) } })
