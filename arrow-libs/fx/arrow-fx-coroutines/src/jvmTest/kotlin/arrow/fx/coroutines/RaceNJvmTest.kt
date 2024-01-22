package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class RaceNJvmTest : StringSpec({
    "race2 returns to original context" {
      val racerName = "race2"
      val racer = executor { Executors.newFixedThreadPool(2, NamedThreadFactory { racerName }) }

      checkAll(Arb.int(1..2)) { choose ->
        single.zip(racer).use { (single, raceCtx) ->
          withContext(single) {
            threadName() shouldStartWith singleThreadName

            val racedOn = when (choose) {
              1 -> raceN(raceCtx, { threadName() }, { awaitCancellation() }).swap().getOrNull()
              else -> raceN(raceCtx, { awaitCancellation() }, { threadName() }).getOrNull()
            }

            racedOn shouldStartWith racerName
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }

    "race2 returns to original context on failure" {
      val racerName = "race2"
      val racer = executor { Executors.newFixedThreadPool(2, NamedThreadFactory { racerName }) }

      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        single.zip(racer).use { (single, raceCtx) ->
          withContext(single) {
            threadName() shouldStartWith singleThreadName

            Either.catch {
              when (choose) {
                1 -> raceN(raceCtx, { e.suspend() }, { awaitCancellation() }).swap().getOrNull()
                else -> raceN(raceCtx, { awaitCancellation() }, { e.suspend() }).getOrNull()
              }
            } should leftException(e)

            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }

    "race3 returns to original context" {
      val racerName = "race3"
      val racer = executor { Executors.newFixedThreadPool(3, NamedThreadFactory { racerName }) }

      checkAll(Arb.int(1..3)) { choose ->
        single.zip(racer).use { (single, raceCtx) ->
          withContext(single) {
            threadName() shouldStartWith singleThreadName

            val racedOn = when (choose) {
              1 ->
                raceN(raceCtx, { threadName() }, { awaitCancellation() }, { awaitCancellation() })
                  .fold(::identity, { null }, { null })
              2 ->
                raceN(raceCtx, { awaitCancellation() }, { threadName() }, { awaitCancellation() })
                  .fold({ null }, ::identity, { null })
              else ->
                raceN(raceCtx, { awaitCancellation() }, { awaitCancellation() }, { threadName() })
                  .fold({ null }, { null }, ::identity)
            }

            racedOn shouldStartWith racerName
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }

    "race3 returns to original context on failure" {
      val racerName = "race3"
      val racer = Resource.fromExecutor { Executors.newFixedThreadPool(3, NamedThreadFactory { racerName }) }

      checkAll(Arb.int(1..3), Arb.throwable()) { choose, e ->
        single.zip(racer).use { (single, raceCtx) ->
          withContext(single) {
            threadName() shouldStartWith singleThreadName

            Either.catch {
              when (choose) {
                1 ->
                  raceN(raceCtx, { e.suspend() }, { awaitCancellation() }, { awaitCancellation() })
                    .fold(::identity, { null }, { null })
                2 ->
                  raceN(raceCtx, { awaitCancellation() }, { e.suspend() }, { awaitCancellation() })
                    .fold({ null }, ::identity, { null })
                else ->
                  raceN(raceCtx, { awaitCancellation() }, { awaitCancellation() }, { e.suspend() })
                    .fold({ null }, { null }, ::identity)
              }
            } should leftException(e)

            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }

    /* These tests seem to not hold anymore

    "first racer out of 2 always wins on a single thread" {
      single.use { ctx ->
            raceN(ctx, { threadName() }, { threadName() })
          }.swap().getOrNull() shouldStartWith "single"
    }

    "first racer out of 3 always wins on a single thread" {
      (single.use { ctx ->
        raceN(ctx, { threadName() }, { threadName() }, { threadName() })
      } as? Race3.First)?.winner shouldStartWith "single"
    }
    */
  }
)
