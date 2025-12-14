package arrow.fx.stm

import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TMVarTest {

  @Test fun emptyCreatesAnEmptyTMVar() = runTest {
    val t1 = TMVar.empty<Int>()
    atomically { t1.tryTake() } shouldBe null
    val t2 = atomically { newEmptyTMVar<Int>() }
    atomically { t2.tryTake() } shouldBe null
  }

  @Test fun newCreatesAFilledTMVar() = runTest {
    val t1 = TMVar.new(100)
    atomically { t1.take() } shouldBe 100
    val t2 = atomically { newTMVar(10) }
    atomically { t2.take() } shouldBe 10
  }

  @Test fun takeLeavesTheTMVarEmpty() = runTest {
    val tm = TMVar.new(500)
    atomically { tm.take() } shouldBeExactly 500
    atomically { tm.tryTake() } shouldBe null
  }

  @Test fun isEmptyIsTryReadEqualsNull() = runTest {
    checkAll(Arb.int().orNull()) { i ->
      val tm = when (i) {
        null -> TMVar.empty()
        else -> TMVar.new(i)
      }
      atomically { tm.isEmpty() } shouldBe
        atomically { tm.tryRead() == null }
      atomically { tm.isEmpty() } shouldBe
        (i == null)
    }
  }

  @Test fun isEmptyIsTryReadUnequalsNull() = runTest {
    checkAll(Arb.int().orNull()) { i ->
      val tm = when (i) {
        null -> TMVar.empty()
        else -> TMVar.new(i)
      }
      atomically { tm.isNotEmpty() } shouldBe
        atomically { tm.tryRead() != null }
      atomically { tm.isNotEmpty() } shouldBe
        (i != null)
    }
  }

  @Test fun takeRetriesOnEmpty() = runTest {
    val tm = TMVar.empty<Int>()
    atomically {
      stm { tm.take().let { true } } orElse { false }
    } shouldBe false
    atomically { tm.tryTake() } shouldBe null
  }

  @Test fun tryTakeBehavesLikeTakeIfThereIsAValue() = runTest {
    val tm = TMVar.new(100)
    atomically {
      tm.tryTake()
    } shouldBe 100
    atomically { tm.tryTake() } shouldBe null
  }

  @Test fun tryTakeReturnsNullOnEmpty() = runTest {
    val tm = TMVar.empty<Int>()
    atomically { tm.tryTake() } shouldBe null
  }

  @Test fun readRetriesOnEmpty() = runTest {
    val tm = TMVar.empty<Int>()
    atomically {
      stm { tm.read().let { true } } orElse { false }
    } shouldBe false
    atomically { tm.tryTake() } shouldBe null
  }

  @Test fun readReturnsTheValueIfNotEmptyAndDoesNotRemoveIt() = runTest {
    val tm = TMVar.new(10)
    atomically {
      tm.read()
    } shouldBe 10
    atomically { tm.tryTake() } shouldBe 10
  }

  @Test fun tryReadBehavesLikeReadIfThereIsAValue() = runTest {
    val tm = TMVar.new(100)
    atomically { tm.tryRead() } shouldBe
      atomically { tm.read() }
    atomically { tm.tryTake() } shouldBe 100
  }

  @Test fun tryReadReturnsNullIfThereIsNoValue() = runTest {
    val tm = TMVar.empty<Int>()
    atomically { tm.tryRead() } shouldBe null
    atomically { tm.tryTake() } shouldBe null
  }

  @Test fun putRetriesIfThereIsAlreadyAValue() = runTest {
    val tm = TMVar.new(5)
    atomically {
      stm { tm.put(100).let { true } } orElse { false }
    } shouldBe false
    atomically { tm.tryTake() } shouldBe 5
  }

  @Test fun putReplacesTheValueIfItWasEmpty() = runTest {
    val tm = TMVar.empty<Int>()
    atomically {
      tm.put(100)
      tm.tryTake()
    } shouldBe 100
  }

  @Test fun tryPutBehavesLikePutIfThereIsNoValue() = runTest {
    val tm = TMVar.empty<Int>()
    atomically {
      val _ = tm.tryPut(100)
      tm.tryTake()
    } shouldBe atomically {
      tm.put(100)
      tm.tryTake()
    }
    atomically { tm.tryPut(30) } shouldBe true
    atomically { tm.tryTake() } shouldBe 30
  }

  @Test fun tryPutReturnsFalseIfThereIsAlreadyAValue() = runTest {
    val tm = TMVar.new(30)
    atomically { tm.tryPut(20) } shouldBe false
    atomically { tm.tryTake() } shouldBe 30
  }

  @Test fun swapReplacesTheCurrentValueOnlyIfItIsNotNull() = runTest {
    val tm = TMVar.new(30)
    atomically { tm.swap(25) } shouldBeExactly 30
    atomically { tm.take() } shouldBeExactly 25
  }

  @Test fun swapShouldRetryIfThereIsNoValue() = runTest {
    val tm = TMVar.empty<Int>()
    atomically {
      stm { tm.swap(10).let { true } } orElse { false }
    } shouldBe false
    atomically { tm.tryTake() } shouldBe null
  }
}
