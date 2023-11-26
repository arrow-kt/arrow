package arrow.fx.stm.compose

import arrow.fx.coroutines.parZip
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalTime
class STMTest {

  @Test fun noEffects() = runTest {
    atomically { 10 } shouldBeExactly 10
  }

  @Test fun readingFromVars() = runTest {
    checkAll(Arb.int()) { i: Int ->
      val tv = TVar.new(i)
      atomically {
        tv.read()
      } shouldBeExactly i
      tv.unsafeRead() shouldBeExactly i
    }
  }

  @Test fun readingAndWriting() = runTest {
    checkAll(Arb.int(), Arb.int()) { i: Int, j: Int ->
      val tv = TVar.new(i)
      atomically { tv.write(j) }
      tv.unsafeRead() shouldBeExactly j
    }
  }

  @Test fun readAfterAWriteShouldHaveTheUpdatedValue() = runTest {
    checkAll(Arb.int(), Arb.int()) { i: Int, j: Int ->
      val tv = TVar.new(i)
      atomically { tv.write(j); tv.read() } shouldBeExactly j
      tv.unsafeRead() shouldBeExactly j
    }
  }

  @Test fun readingMultipleVariables() = runTest {
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

  @Test fun readingAndWritingMultipleVariables() = runTest {
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

  @Test fun retryWithoutPriorReadsThrowsAnException() = runTest {
    shouldThrow<BlockedIndefinitely> { atomically { retry() } }
  }

  @Test fun retryShouldSuspendForeverIfNoReadVariableChanges() = runTest {
    withTimeoutOrNull(500.milliseconds) {
      val tv = TVar.new(0)
      atomically {
        if (tv.read() == 0) retry()
        else 200
      }
    } shouldBe null
  }

  @Test fun aSuspendedTransactionWillResumeIfAVariableChanges() = runTest {
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

  @Test fun aSuspendedTransactionWillResumeIfAnyVariableChanges() = runTest {
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

  @Test fun retryOrElseRetryOrElseT1T1() = runTest {
    atomically {
      stm { retry() } orElse { 10 }
    } shouldBeExactly 10
  }

  @Test fun retryOrElseT1OrElseRetryT1() = runTest {
    atomically {
      stm { 10 } orElse { retry() }
    } shouldBeExactly 10
  }

  @Test fun retryOrElseAssociativity() = runTest {
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

  @Test fun suspendedTransactionsAreResumedForVariablesAccessedInOrElse() = runTest {
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

  /*
  @Test fun onASingleVariableConcurrentTransactionsShouldBeLinear() = runTest {
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

   */

  @Test fun atomicallyRethrowsExceptions() = runTest {
    shouldThrow<IllegalArgumentException> { atomically { throw IllegalArgumentException("Test") } }
  }

  @Test fun throwingAnExceptionsShouldVoidAllStateChanges() = runTest {
    val tv = TVar.new(10)
    shouldThrow<IllegalArgumentException> {
      atomically { tv.write(30); throw IllegalArgumentException("test") }
    }
    tv.unsafeRead() shouldBeExactly 10
  }

  @Test fun catchShouldWorkAsExcepted() = runTest {
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

  @Test fun concurrentExample1() = runTest {
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
      { _, _ -> }
    )
    acc1.unsafeRead() shouldBeExactly 50
    acc2.unsafeRead() shouldBeExactly 250
  }
}
