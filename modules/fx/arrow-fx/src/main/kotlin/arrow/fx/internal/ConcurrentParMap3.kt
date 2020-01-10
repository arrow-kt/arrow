package arrow.fx.internal

import arrow.Kind
import arrow.fx.typeclasses.Concurrent
import kotlin.coroutines.CoroutineContext

internal fun <F, A, B, C, D> Concurrent<F>.parMap3(ctx: CoroutineContext, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>, f: (A, B, C) -> D): Kind<F, D> = ctx.run {
  tupled(fb.fork(this), fa.fork(this), fc.fork(this)).bracket(use = { (fiberB, fiberA, fiberC) ->
    raceTriple(fiberA.join().attempt(), fiberB.join().attempt(), fiberC.join().attempt()).flatMap { tripleResult ->
      tripleResult.fold({ attemptedA, fiberB, fiberC ->
        attemptedA.fold({ error ->
          raiseError<D>(error)
        }, { a ->
          racePair(fiberB.join(), fiberC.join()).flatMap {
            it.fold({ attemptedB, fiberC ->
              attemptedB.fold({ error ->
                raiseError<D>(error)
              }, { b ->
                fiberC.join().rethrow().map { c ->
                  f(a, b, c)
                }
              })
            }, { fiberB, attemptedC ->
              attemptedC.fold({ error ->
                raiseError(error)
              }, { c ->
                fiberB.join().rethrow().map { b ->
                  f(a, b, c)
                }
              })
            })
          }
        })
      }, { fiberA, attemptedB, fiberC ->
        attemptedB.fold({ error ->
          raiseError<D>(error)
        }, { b ->
          racePair(fiberA.join(), fiberC.join()).flatMap {
            it.fold({ attemptedA, fiberC ->
              attemptedA.fold({ error ->
                raiseError<D>(error)
              }, { a ->
                fiberC.join().rethrow().map { c ->
                  f(a, b, c)
                }
              })
            }, { fiberA, attemptedC ->
              attemptedC.fold({ error ->
                raiseError<D>(error)
              }, { c ->
                fiberA.join().rethrow().map { a ->
                  f(a, b, c)
                }
              })
            })
          }
        })
      }, { fiberA, fiberB, c ->
        c.fold({ error ->
          raiseError<D>(error)
        }, { c ->
          racePair(fiberA.join(), fiberB.join()).flatMap {
            it.fold({ attemptedA, fiberB ->
              attemptedA.fold({ error ->
                raiseError<D>(error)
              }, { a ->
                fiberB.join().rethrow().map { b ->
                  f(a, b, c)
                }
              })
            }, { fiberA, attemptedB ->
              attemptedB.fold({ error ->
                raiseError(error)
              }, { b ->
                fiberA.join().rethrow().map { a ->
                  f(a, b, c)
                }
              })
            })
          }
        })
      })
    }
  }, release = { (fiberA, fiberB, fiberC) ->
      fiberA.cancel().followedBy(fiberB.cancel()).followedBy(fiberC.cancel())
  })
}
