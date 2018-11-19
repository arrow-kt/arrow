package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.Async
import arrow.typeclasses.ApplicativeError

interface Semaphore<F> {

  val available: Kind<F, Long>

  val count: Kind<F, Long>

  fun acquireN(n: Long): Kind<F, Unit>

  val acquire: Kind<F, Unit>
    get() = acquireN(1)

  fun tryAcquireN(n: Long): Kind<F, Boolean>

  val tryAcquire: Kind<F, Boolean>
    get() = tryAcquireN(1)

  fun releaseN(n: Long): Kind<F, Unit>

  val release: Kind<F, Unit>
    get() = releaseN(1)

  fun <A> withPermit(t: Kind<F, A>): Kind<F, A>

  companion object {
    fun <F> uncancelable(n: Long, AS: Async<F>): Kind<F, Semaphore<F>> = AS.run {
      assertNonNegative(n, AS).flatMap {
        Ref.of<F, State<F>>(Right(n), AS).map { ref -> AsyncSemaphore(ref, AS) }
      }
    }
  }

}

// A semaphore is either empty, and there are number of outstanding acquires (Left)
// or it is non-empty, and there are n permits available (Right)
private typealias State<F> = Either<List<Tuple2<Long, Promise<F, Unit>>>, Long>

private fun <F> assertNonNegative(n: Long, AE: ApplicativeError<F, Throwable>): Kind<F, Unit> =
  if (n < 0) AE.raiseError(IllegalArgumentException("n must be nonnegative, was: $n")) else AE.just(Unit)

internal class AsyncSemaphore<F>(private val state: Ref<F, State<F>>, private val AS: Async<F>) : Semaphore<F>, Async<F> by AS {

  private val mkGate: Kind<F, Promise<F, Unit>> = Promise.uncancelable(AS)
  private fun awaitGate(entry: Tuple2<Long, Promise<F, Unit>>): Kind<F, Unit> = entry.b.get

  override val available: Kind<F, Long>
    get() = state.get().map { eith ->
      eith.fold({ 0L }, ::identity)
    }

  override val count: Kind<F, Long>
    get() = state.get().map { eith ->
      eith.fold({ it.map { (a, _) -> a }.sum().unaryMinus() }, ::identity)
    }

  override fun acquireN(n: Long): Kind<F, Unit> =
    assertNonNegative(n, AS).flatMap {
      if (n == 0L) just(Unit)
      else mkGate.flatMap { gate ->
        state.modify { old ->
          val u = old.fold({ waiting ->
            Left(waiting + listOf(n toT gate))
          }, { m ->
            if (n <= m) Right(m - n)
            else Left(listOf((n - m) toT gate))
          })

          u toT u
        }.flatMap { eith ->
          eith.fold({ waiting ->
            val entry = waiting.lastOrNone().getOrElse { throw RuntimeException("Semaphore has empty waiting queue rather than 0 count") }
            awaitGate(entry)
          }, {
            just(Unit)
          })
        }
      }
    }

  override fun tryAcquireN(n: Long): Kind<F, Boolean> =
    assertNonNegative(n, AS).flatMap { _ ->
      if (n == 0L) AS.just(true)
      else state.modify { old ->
        val u = old.fold({ Left(it) }, { m ->
          if (m >= n) Right(m - n) else Right(m)
        })

        u toT Tuple2(old, u)
      }.map { (previous, now) ->
        now.fold({ false }, { n ->
          previous.fold({ false }, { m ->
            n != m
          })
        })
      }
    }

  override fun releaseN(n: Long): Kind<F, Unit> =
    assertNonNegative(n, AS).flatMap {
      if (n == 0L) just(Unit)
      else state.modify { old ->
        val u = old.fold({ waiting ->
          var m = n
          var waiting2 = waiting
          while (waiting2.isNotEmpty() && m > 0) {
            val (k, gate) = waiting2.first()
            if (k > m) {
              waiting2 = listOf(Tuple2(k - m, gate)) + waiting2.drop(1)
              m = 0
            } else {
              m -= k
              waiting2 = waiting2.drop(1)
            }
          }
          if (waiting2.isNotEmpty()) Left(waiting2)
          else Right(m)
        }, { m ->
          Right(m + n)
        })

        Tuple2(u, Tuple2(old, u))
      }.flatMap { (previous, now) ->
        previous.fold({ waiting ->
          val newSize = now.fold({ it.size }, { 0 })
          val released = waiting.size - newSize
          waiting.take(released).foldRight(just(Unit)) { hd, tl -> open(hd.b).flatMap { tl } }
        }, { just(Unit) })
      }
    }

  private fun open(gate: Promise<F, Unit>): Kind<F, Unit> = gate.complete(Unit)

  override fun <A> withPermit(t: Kind<F, A>): Kind<F, A> =
    acquire.bracket({ release }, { t })

}
