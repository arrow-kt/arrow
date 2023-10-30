package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test

class RaceNJvmTest {
    @Test fun race2ReturnsToOriginalContext() = runTest {
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

    @Test fun race2ReturnsToOriginalContextOnFailure() = runTest {
      val racerName = "race2"
      
      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        resourceScope {
          val pool = fixedThreadPoolContext(2, racerName)
          withContext(singleThreadContext("single")) {
            Thread.currentThread().name shouldStartWith "single"
            
            Either.catch {
              when (choose) {
                1 -> raceN(pool, { e.suspend() }, { awaitCancellation() }).swap().getOrNull()
                else -> raceN(pool, { awaitCancellation() }, { e.suspend() }).getOrNull()
              }
            } should leftException(e)
            
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
    
    @Test fun firstRacerOutOf2AlwaysWinsOnASingleThread() = runTest {
      resourceScope {
        val ctx = singleThreadContext("single")
        raceN(ctx, { Thread.currentThread().name }, { Thread.currentThread().name })
      }.swap().getOrNull() shouldStartWith "single"
    }
    
    @Test fun race3ReturnsToOriginalContext() = runTest {
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
    
    @Test fun race3ReturnsToOriginalContextOnFailure() = runTest {
      val racerName = "race3"
      
      checkAll(Arb.int(1..3), Arb.throwable()) { choose, e ->
        parallelCtx(3, racerName) { single, raceCtx ->
          withContext(single) {
            Thread.currentThread().name shouldStartWith "single"
            
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
            
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
  }
