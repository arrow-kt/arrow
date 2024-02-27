package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class RaceNJvmTest {
    @Test fun race2ReturnsToOriginalContext() = runTestUsingDefaultDispatcher {
      val racerName = "race2"
      checkAll(Arb.int(1..2)) { choose ->
        resourceScope {
          val pool = fixedThreadPoolContext(2, racerName)
          withContext(singleThreadContext("single")) {
            Thread.currentThread().name shouldStartWith "single"
            
            val racedOn = when (choose) {
              1 -> raceN<String, Nothing>(pool, { Thread.currentThread().name }, { awaitCancellation() }).swap().getOrNull()
              else -> raceN<Nothing, String>(pool, { awaitCancellation() }, { Thread.currentThread().name }).getOrNull()
            }
            
            racedOn shouldStartWith racerName
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }

    @Test fun race2ReturnsToOriginalContextOnFailure() = runTestUsingDefaultDispatcher {
      val racerName = "race2"
      
      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        resourceScope {
          val pool = fixedThreadPoolContext(2, racerName)
          withContext(singleThreadContext("single")) {
            Thread.currentThread().name shouldStartWith "single"
            
            Either.catch {
              when (choose) {
                1 -> raceN(pool, { throw e }, { awaitCancellation() }).swap().getOrNull()
                else -> raceN(pool, { awaitCancellation() }, { throw e }).getOrNull()
              }
            } should leftException(e)
            
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
    
    @Test fun firstRacerOutOf2AlwaysWinsOnASingleThread() = runTestUsingDefaultDispatcher {
      resourceScope {
        val ctx = singleThreadContext("single")
        raceN(ctx, { Thread.currentThread().name }, { Thread.currentThread().name })
      }.swap().getOrNull() shouldStartWith "single"
    }
    
    @Test fun race3ReturnsToOriginalContext() = runTestUsingDefaultDispatcher {
      val racerName = "race3"

      checkAll(Arb.int(1..3)) { choose ->
        parallelCtx(3, racerName) { single, raceCtx ->
          withContext(single) {
            Thread.currentThread().name shouldStartWith "single"
            
            val racedOn = when (choose) {
              1 ->
                raceN(raceCtx, { Thread.currentThread().name }, { awaitCancellation() }, { awaitCancellation() })
                  .fold(::identity, { null }, { null })
              
              2 ->
                raceN(raceCtx, { awaitCancellation() }, { Thread.currentThread().name }, { awaitCancellation() })
                  .fold({ null }, ::identity, { null })
              
              else ->
                raceN(raceCtx, { awaitCancellation() }, { awaitCancellation() }, { Thread.currentThread().name })
                  .fold({ null }, { null }, ::identity)
            }
            
            racedOn shouldStartWith racerName
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
    
    @Test fun race3ReturnsToOriginalContextOnFailure() = runTestUsingDefaultDispatcher {
      val racerName = "race3"
      
      checkAll(Arb.int(1..3), Arb.throwable()) { choose, e ->
        parallelCtx(3, racerName) { single, raceCtx ->
          withContext(single) {
            Thread.currentThread().name shouldStartWith "single"
            
            Either.catch {
              when (choose) {
                1 ->
                  raceN(raceCtx, { throw e }, { awaitCancellation() }, { awaitCancellation() })
                    .fold({ x: String? -> x }, { null }, { null })
                
                2 ->
                  raceN(raceCtx, { awaitCancellation() }, { throw e }, { awaitCancellation() })
                    .fold({ null }, { x: String? -> x }, { null })
                
                else ->
                  raceN(raceCtx, { awaitCancellation() }, { awaitCancellation() }, { throw e })
                    .fold({ null }, { null }, { x: String? -> x })
              }
            } should leftException(e)
            
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
  }
