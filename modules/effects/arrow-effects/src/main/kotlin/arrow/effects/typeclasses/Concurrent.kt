package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.left
import arrow.core.right

interface Concurrent<F> : Async<F> {

  fun <A> Kind<F, A>.startF(): Kind<F, Fiber<F, A>>

  fun <A, B> racePair(lh: Kind<F, A>, rh: Kind<F, B>): Kind<F, Either<Tuple2<A, Fiber<F, B>>, Tuple2<Fiber<F, A>, B>>>

  fun <A, B> race(lh: Kind<F, A>, rh: Kind<F, B>): Kind<F, Either<A, B>> =
    racePair(lh, rh).map {
      it.fold({ (a, _) ->
        a.left()
      }, { (_, b) ->
        b.right()
      })
    }


  //TODO blocked by Async#asyncF (https://github.com/arrow-kt/arrow/issues/1124)
  //fun <A> cancelable(cb: ((Either<Throwable, A>) -> Unit) -> Kind<F, Unit>): Kind<F, A> =
}