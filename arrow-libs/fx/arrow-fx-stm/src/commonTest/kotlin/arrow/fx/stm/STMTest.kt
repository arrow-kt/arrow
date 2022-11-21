package arrow.fx.stm

import arrow.fx.coroutines.ArrowFxSpec
import kotlin.time.microseconds
import kotlin.time.milliseconds
import arrow.fx.coroutines.parTraverse
import arrow.fx.coroutines.parZip
import arrow.fx.stm.internal.BlockedIndefinitely
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.ExperimentalTime

@ExperimentalTime
class STMTest : ArrowFxSpec(
  spec = {
    "no-effects" {
      atomically { 10 } shouldBeExactly 10
    }
    "reading from vars".config(enabled = false) {
      checkAll(Arb.int()) { i: Int ->
        val tv = TVar.new(i)
        atomically {
          tv.read()
        } shouldBeExactly i
        tv.unsafeRead() shouldBeExactly i
      }
    }
    "reading and writing" {
      checkAll(Arb.int(), Arb.int()) { i: Int, j: Int ->
        val tv = TVar.new(i)
        atomically { tv.write(j) }
        tv.unsafeRead() shouldBeExactly j
      }
    }
    "read after a write should have the updated value" {
      checkAll(Arb.int(), Arb.int()) { i: Int, j: Int ->
        val tv = TVar.new(i)
        atomically { tv.write(j); tv.read() } shouldBeExactly j
        tv.unsafeRead() shouldBeExactly j
      }
    }
    "reading multiple variables" {
      checkAll(Arb.int(), Arb.int(), Arb.int()) { i: Int, j: Int, k: Int ->
        val v1 = TVar.new(i)
        val v2 = TVar.new(j)
        val v3 = TVar.new(k)
        atomically { v1.read() + v2.read() + v3.read() } shouldBeExactly i + j + k
        v1.unsafeRead() shouldBeExactly i
        v2.unsafeRead() shouldBeExactly j
        v3.unsafeRead() shouldBeExactly k
      }
    }
    "reading and writing multiple variables" {
      checkAll(Arb.int(), Arb.int(), Arb.int()) { i: Int, j: Int, k: Int ->
        val v1 = TVar.new(i)
        val v2 = TVar.new(j)
        val v3 = TVar.new(k)
        val sum = TVar.new(0)
        atomically {
          val s = v1.read() + v2.read() + v3.read()
          sum.write(s)
        }
        v1.unsafeRead() shouldBeExactly i
        v2.unsafeRead() shouldBeExactly j
        v3.unsafeRead() shouldBeExactly k
        sum.unsafeRead() shouldBeExactly i + j + k
      }
    }
    "retry without prior reads throws an exception" {
      shouldThrow<BlockedIndefinitely> { atomically { retry() } }
    }
    "retry should suspend forever if no read variable changes" {
      withTimeoutOrNull(500.milliseconds) {
        val tv = TVar.new(0)
        atomically {
          if (tv.read() == 0) retry()
          else 200
        }
      } shouldBe null
    }
    "a suspended transaction will resume if a variable changes" {
      val tv = TVar.new(0)
      val f = async {
        delay(500.milliseconds)
        atomically { tv.modify { it + 1 } }
      }
      atomically {
        when (val i = tv.read()) {
          0 -> retry()
          else -> i
        }
      } shouldBeExactly 1
      f.join()
    }
    "a suspended transaction will resume if any variable changes" {
      val v1 = TVar.new(0)
      val v2 = TVar.new(0)
      val v3 = TVar.new(0)
      val f = async {
        delay(500.milliseconds)
        atomically { v1.modify { it + 1 } }
        delay(500.milliseconds)
        atomically { v2.modify { it + 1 } }
        delay(500.milliseconds)
        atomically { v3.modify { it + 1 } }
      }
      atomically {
        val i = v1.read() + v2.read() + v3.read()
        check(i >= 3)
        i
      } shouldBeExactly 3
      f.join()
    }
    "retry + orElse: retry orElse t1 = t1" {
      atomically {
        stm { retry() } orElse { 10 }
      } shouldBeExactly 10
    }
    "retry + orElse: t1 orElse retry = t1" {
      atomically {
        stm { 10 } orElse { retry() }
      } shouldBeExactly 10
    }
    "retry + orElse: associativity" {
      checkAll(Arb.boolean(), Arb.boolean(), Arb.boolean()) { b1: Boolean, b2: Boolean, b3: Boolean ->
        if ((b1 || b2 || b3).not()) {
          shouldThrow<BlockedIndefinitely> {
            atomically {
              stm { stm { check(b1) } orElse { check(b2) } } orElse { check(b3) }
            }
          } shouldBe shouldThrow {
            atomically {
              stm { check(b1) } orElse { stm { check(b2) } orElse { check(b3) } }
            }
          }
        } else {
          atomically {
            stm { stm { check(b1) } orElse { check(b2) } } orElse { check(b3) }
          } shouldBe atomically {
            stm { check(b1) } orElse { stm { check(b2) } orElse { check(b3) } }
          }
        }
      }
    }
    "suspended transactions are resumed for variables accessed in orElse" {
      checkAll {
        val tv = TVar.new(0)
        val f = async {
          delay(10.microseconds)
          atomically { tv.modify { it + 1 } }
        }
        atomically {
          stm {
            when (val i = tv.read()) {
              0 -> retry()
              else -> i
            }
          } orElse { retry() }
        } shouldBeExactly 1
        f.join()
      }
    }
    "on a single variable concurrent transactions should be linear" {
      checkAll {
        val tv = TVar.new(0)
        val res = TQueue.new<Int>()

        (0..100).map {
          async {
            atomically {
              val r = tv.read().also { tv.write(it + 1) }
              res.write(r)
            }
          }
        }.joinAll()

        atomically { res.flush() } shouldBe (0..100).toList()
      }
    }
    "atomically rethrows exceptions" {
      shouldThrow<IllegalArgumentException> { atomically { throw IllegalArgumentException("Test") } }
    }
    "throwing an exceptions should void all state changes" {
      val tv = TVar.new(10)
      shouldThrow<IllegalArgumentException> {
        atomically { tv.write(30); throw IllegalArgumentException("test") }
      }
      tv.unsafeRead() shouldBeExactly 10
    }
    "catch should work as excepted" {
      val tv = TVar.new(10)
      val ex = IllegalArgumentException("test")
      atomically {
        catch({
          tv.write(30)
          throw ex
        }) { e ->
          e shouldBe ex
        }
      }
      tv.unsafeRead() shouldBeExactly 10
    }
    "concurrent example 1" {
      checkAll {
        val acc1 = TVar.new(100)
        val acc2 = TVar.new(200)
        parZip(
          {
            // transfer acc1 to acc2
            val amount = 50
            atomically {
              val acc1Balance = acc1.read()
              check(acc1Balance - amount >= 0)
              acc1.write(acc1Balance - amount)
              acc2.modify { it + 50 }
            }
          },
          {
            atomically { acc1.modify { it - 60 } }
            delay(20.milliseconds)
            atomically { acc1.modify { it + 60 } }
          },
          { _, _ -> Unit }
        )
        acc1.unsafeRead() shouldBeExactly 50
        acc2.unsafeRead() shouldBeExactly 250
      }
    }

    // TypeError: Cannot read property 'toString' of undefined
    // at ObjectLiteral_0.test(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:3661)
    // at <global>.invokeMatcher(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:19216)
    // at <global>.should(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:19212)
    // at <global>.shouldBeInRange(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:3652)
    // at STMTransaction.f(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:261217)
    // at commit.doResume(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:270552)
    // at commit.CoroutineImpl.resumeWith(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:118697)
    // at CancellableContinuationImpl.DispatchedTask.run(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:174593)
    // at WindowMessageQueue.MessageQueue.process(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:177985)
    // at <global>.<unknown>(/var/folders/x5/6r18d9w52c7czy6zh5m1spvw0000gn/T/_karma_webpack_624630/commons.js:177940)
    "concurrent example 2".config(enabled = false) {
      checkAll {
        val tq = TQueue.new<Int>()
        parZip(
          {
            // producers
            (0..4).parTraverse {
              for (i in (it * 20 + 1)..(it * 20 + 20)) {
                atomically { tq.write(i) }
              }
            }
          },
          {
            val collected = mutableSetOf<Int>()
            for (i in 1..100) {
              // consumer
              atomically {
                tq.read().also { it shouldBeInRange (1..100) }
              }.also { collected.add(it) }
            }
            // verify that we got 100 unique numbers
            collected.size shouldBeExactly 100
          }
        ) { _, _ -> Unit }
        // the above only finishes if the consumer reads at least 100 values, this here is just to make sure there are no leftovers
        atomically { tq.flush() } shouldBe emptyList()
      }
    }
  }
)
