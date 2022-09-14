package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import kotlinx.coroutines.withContext

class RaceNJvmTest : ArrowFxSpec(
  spec = {
    "race2 returns to original context" {
      val racerName = "race2"
      checkAll(Arb.int(1..2)) { choose ->
        resourceScope {
          val pool = fixedThreadPoolContext(2, racerName)
          withContext(singleThreadContext("single")) {
            Thread.currentThread().name shouldStartWith "single"
            
            val racedOn = when (choose) {
              1 -> raceN<String, Nothing>(pool, { Thread.currentThread().name }, { never() }).swap().orNull()
              else -> raceN<Nothing, String>(pool, { never() }, { Thread.currentThread().name }).orNull()
            }
            
            racedOn shouldStartWith racerName
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
    
    "race2 returns to original context on failure" {
      val racerName = "race2"
      
      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        resourceScope {
          val pool = fixedThreadPoolContext(2, racerName)
          withContext(singleThreadContext("single")) {
            Thread.currentThread().name shouldStartWith "single"
            
            Either.catch {
              when (choose) {
                1 -> raceN<Nothing, Nothing>(pool, { e.suspend() }, { never() }).swap().orNull()
                else -> raceN<Nothing, Nothing>(pool, { never() }, { e.suspend() }).orNull()
              }
            } should leftException(e)
            
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
    
    "first racer out of 2 always wins on a single thread" {
      resourceScope {
        val ctx = singleThreadContext("single")
        raceN(ctx, { Thread.currentThread().name }, { Thread.currentThread().name })
      }.swap().orNull() shouldStartWith "single"
    }
    
    "race3 returns to original context" {
      val racerName = "race3"
      
      checkAll(Arb.int(1..3)) { choose ->
        parallelCtx(3, racerName) { single, raceCtx ->
          withContext(single) {
            Thread.currentThread().name shouldStartWith "single"
            
            val racedOn = when (choose) {
              1 ->
                raceN(raceCtx, { Thread.currentThread().name }, { never<Nothing>() }, { never<Nothing>() })
                  .fold(::identity, { null }, { null })
              
              2 ->
                raceN(raceCtx, { never<Nothing>() }, { Thread.currentThread().name }, { never<Nothing>() })
                  .fold({ null }, ::identity, { null })
              
              else ->
                raceN(raceCtx, { never<Nothing>() }, { never<Nothing>() }, { Thread.currentThread().name })
                  .fold({ null }, { null }, ::identity)
            }
            
            racedOn shouldStartWith racerName
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
    
    "race3 returns to original context on failure" {
      val racerName = "race3"
      
      checkAll(Arb.int(1..3), Arb.throwable()) { choose, e ->
        parallelCtx(3, racerName) { single, raceCtx ->
          withContext(single) {
            Thread.currentThread().name shouldStartWith "single"
            
            Either.catch {
              when (choose) {
                1 ->
                  raceN(raceCtx, { e.suspend() }, { never<Nothing>() }, { never<Nothing>() })
                    .fold(::identity, { null }, { null })
                
                2 ->
                  raceN(raceCtx, { never<Nothing>() }, { e.suspend() }, { never<Nothing>() })
                    .fold({ null }, ::identity, { null })
                
                else ->
                  raceN(raceCtx, { never<Nothing>() }, { never<Nothing>() }, { e.suspend() })
                    .fold({ null }, { null }, ::identity)
              }
            } should leftException(e)
            
            Thread.currentThread().name shouldStartWith "single"
          }
        }
      }
    }
    
    "first racer out of 3 always wins on a single thread" {
      resourceScope {
        val ctx = singleThreadContext("single")
        raceN(
          ctx,
          { Thread.currentThread().name },
          { Thread.currentThread().name },
          { Thread.currentThread().name }) as? Race3.First
      }?.winner shouldStartWith "single"
    }
  }
)
