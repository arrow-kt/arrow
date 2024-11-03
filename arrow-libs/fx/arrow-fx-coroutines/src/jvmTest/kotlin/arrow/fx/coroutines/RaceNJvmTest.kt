package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RaceNJvmTest {
  private fun race2ReturnsToOriginalContext(choose: Boolean): Unit =
      runBlocking(Dispatchers.Default) {
        val racerName = "race2"
        resourceScope {
          val pool = fixedThreadPoolContext(2, racerName)
          withContext(singleThreadContext("single")) {
            Thread.currentThread().name shouldStartWith "single"

            val racedOn =
                if (choose)
                    raceN<String, Nothing>(
                            pool, { Thread.currentThread().name }, { awaitCancellation() })
                        .swap()
                        .getOrNull()
                else
                    raceN<Nothing, String>(
                            pool, { awaitCancellation() }, { Thread.currentThread().name })
                        .getOrNull()

            racedOn shouldStartWith racerName
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }

  @Test fun race2ReturnsToOriginalContextRight() = race2ReturnsToOriginalContext(true)

  @Test fun race2ReturnsToOriginalContextLeft() = race2ReturnsToOriginalContext(false)

  private fun race2ReturnsToOriginalContextOnFailure(right: Boolean): Unit =
      runBlocking(Dispatchers.Default) {
        val racerName = "race2"

        resourceScope {
          val pool = fixedThreadPoolContext(2, racerName)
          val e = RuntimeException("Boom")
          withContext(singleThreadContext("single")) {
            Thread.currentThread().name shouldStartWith "single"
            Either.catch {
              if (right) raceN(pool, { throw e }, { awaitCancellation() }).swap().getOrNull()
              else raceN(pool, { awaitCancellation() }, { throw e }).getOrNull()
            } should leftException(e)

            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }

  @Test
  fun race2ReturnsToOriginalContextOnFailureLeft(): Unit =
      race2ReturnsToOriginalContextOnFailure(false)

  @Test
  fun race2ReturnsToOriginalContextOnFailureRight(): Unit =
      race2ReturnsToOriginalContextOnFailure(true)

  @Test
  fun firstRacerOutOf2AlwaysWinsOnASingleThread(): Unit =
      runBlocking(Dispatchers.Default) {
        resourceScope {
              val ctx = singleThreadContext("single")
              raceN(ctx, { Thread.currentThread().name }, { Thread.currentThread().name })
            }
            .swap()
            .getOrNull() shouldStartWith "single"
      }

  @Test fun race3ReturnsToOriginalContextRight(): Unit = race3ReturnsToOriginalContext(true)

  @Test fun race3ReturnsToOriginalContextMiddle(): Unit = race3ReturnsToOriginalContext(false)

  @Test fun race3ReturnsToOriginalContextLast(): Unit = race3ReturnsToOriginalContext(null)

  private fun race3ReturnsToOriginalContext(choose: Boolean?): Unit =
      runBlocking(Dispatchers.Default) {
        val racerName = "race3"
        parallelCtx(3, racerName) { single, raceCtx ->
          withContext(single) {
            Thread.currentThread().name shouldStartWith "single"

            val racedOn =
                when (choose) {
                  true ->
                      raceN(
                              raceCtx,
                              { Thread.currentThread().name },
                              { awaitCancellation() },
                              { awaitCancellation() })
                          .fold(::identity, { null }, { null })

                  false ->
                      raceN(
                              raceCtx,
                              { awaitCancellation() },
                              { Thread.currentThread().name },
                              { awaitCancellation() })
                          .fold({ null }, ::identity, { null })

                  null ->
                      raceN(
                              raceCtx,
                              { awaitCancellation() },
                              { awaitCancellation() },
                              { Thread.currentThread().name })
                          .fold({ null }, { null }, ::identity)
                }

            racedOn shouldStartWith racerName
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }

  @Test fun race3ReturnsToOriginalContextOnFailureRight(): Unit = race3ReturnsToOriginalContextOnFailure(true)
  @Test fun race3ReturnsToOriginalContextOnFailureMiddle(): Unit = race3ReturnsToOriginalContextOnFailure(false)
  @Test fun race3ReturnsToOriginalContextOnFailureLast(): Unit = race3ReturnsToOriginalContextOnFailure(null)

  private fun race3ReturnsToOriginalContextOnFailure(choose: Boolean?): Unit =
      runBlocking(Dispatchers.Default) {
        val racerName = "race3"
        val e = RuntimeException("Boom")
        parallelCtx(3, racerName) { single, raceCtx ->
          withContext(single) {
            Thread.currentThread().name shouldStartWith "single"

            Either.catch {
              when (choose) {
                true ->
                    raceN(raceCtx, { throw e }, { awaitCancellation() }, { awaitCancellation() })
                        .fold({ x: String? -> x }, { null }, { null })

                false ->
                    raceN(raceCtx, { awaitCancellation() }, { throw e }, { awaitCancellation() })
                        .fold({ null }, { x: String? -> x }, { null })

                null ->
                    raceN(raceCtx, { awaitCancellation() }, { awaitCancellation() }, { throw e })
                        .fold({ null }, { null }, { x: String? -> x })
              }
            } should leftException(e)

            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
}
