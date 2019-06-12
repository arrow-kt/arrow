package arrow.effects.internal

import arrow.Kind
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.ExitCase
import kotlin.coroutines.CoroutineContext

fun <F, A, B, C> Concurrent<F>.parMap2(
  ctx: CoroutineContext,
  fa: Kind<F, A>,
  fb: Kind<F, B>,
  f: (A, B) -> C
): Kind<F, C> = ctx.run {
  tupled(startFiber(fb), startFiber(fa)).bracket(use = { (fiberB, fiberA) ->
    racePair(fiberA.join().attempt(), fiberB.join().attempt()).flatMap { pairResult ->
      pairResult.fold({ attemptedA, fiberB ->
        attemptedA.fold({ error ->
          raiseError<C>(error)
        }, { a ->
          fiberB.join().rethrow().map { b ->
            f(a, b)
          }
        })
      }, { fiberA, attemptedB ->
        attemptedB.fold({ error ->
          raiseError(error)
        }, { b ->
          fiberA.join().rethrow().map { a ->
            f(a, b)
          }
        })
      })
    }
  }, release = { (fiberA, fiberB) ->
    fiberA.cancel().followedBy(fiberB.cancel())
  })
}
