package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class RaceNJvmTest : ArrowFxSpec(
  spec = {
    "race2 returns to original context" {
      val racerName = "race2"
      checkAll(Arb.int(1..2)) { choose ->
        parallelCtx(2, racerName).use { (single, raceCtx) ->
          withContext(single) {
            threadName() shouldStartWith singleThreadName
            
            val racedOn = when (choose) {
              1 -> raceN(raceCtx, { threadName() }, { never<Nothing>() }).swap().orNull()
              else -> raceN(raceCtx, { never<Nothing>() }, { threadName() }).orNull()
            }
            
            racedOn shouldStartWith racerName
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }
    
    "race2 returns to original context on failure" {
      val racerName = "race2"
      
      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        parallelCtx(2, racerName).use { (single, raceCtx) ->
          withContext(single) {
            threadName() shouldStartWith singleThreadName
            
            Either.catch {
              when (choose) {
                1 -> raceN(raceCtx, { e.suspend() }, { never<Nothing>() }).swap().orNull()
                else -> raceN(raceCtx, { never<Nothing>() }, { e.suspend() }).orNull()
              }
            } should leftException(e)
            
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }
    
    "first racer out of 2 always wins on a single thread" {
      single.use { ctx ->
        raceN(ctx, { threadName() }, { threadName() })
      }.swap().orNull() shouldStartWith "single"
    }
    
    "race3 returns to original context" {
      val racerName = "race3"
      
      checkAll(Arb.int(1..3)) { choose ->
        parallelCtx(3, racerName).use { (single, raceCtx) ->
          withContext(single) {
            threadName() shouldStartWith singleThreadName
            
            val racedOn = when (choose) {
              1 ->
                raceN(raceCtx, { threadName() }, { never<Nothing>() }, { never<Nothing>() })
                  .fold(::identity, { null }, { null })
              
              2 ->
                raceN(raceCtx, { never<Nothing>() }, { threadName() }, { never<Nothing>() })
                  .fold({ null }, ::identity, { null })
              
              else ->
                raceN(raceCtx, { never<Nothing>() }, { never<Nothing>() }, { threadName() })
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
      
      checkAll(Arb.int(1..3), Arb.throwable()) { choose, e ->
        parallelCtx(3, racerName).use { (single, raceCtx) ->
          withContext(single) {
            threadName() shouldStartWith singleThreadName
            
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
            
            threadName() shouldStartWith singleThreadName
          }
        }
      }
    }
    
    "first racer out of 3 always wins on a single thread" {
      (single.use { ctx ->
        raceN(ctx, { threadName() }, { threadName() }, { threadName() })
      } as? Race3.First)?.winner shouldStartWith "single"
    }
  }
)
