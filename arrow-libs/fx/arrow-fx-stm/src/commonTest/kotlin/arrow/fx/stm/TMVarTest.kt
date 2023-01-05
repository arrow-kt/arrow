package arrow.fx.stm

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll

class TMVarTest : StringSpec({
    "empty creates an empty TMVar" {
      val t1 = TMVar.empty<Int>()
      atomically { t1.tryTake() } shouldBe null
      val t2 = atomically { newEmptyTMVar<Int>() }
      atomically { t2.tryTake() } shouldBe null
    }
    "new creates a filled TMVar" {
      val t1 = TMVar.new(100)
      atomically { t1.take() } shouldBe 100
      val t2 = atomically { newTMVar(10) }
      atomically { t2.take() } shouldBe 10
    }
    "take leaves the TMVar empty" {
      val tm = TMVar.new(500)
      atomically { tm.take() } shouldBeExactly 500
      atomically { tm.tryTake() } shouldBe null
    }
    "isEmpty = tryRead == null" {
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
    "isEmpty = tryRead != null" {
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
    "take retries on empty" {
      val tm = TMVar.empty<Int>()
      atomically {
        stm { tm.take().let { true } } orElse { false }
      } shouldBe false
      atomically { tm.tryTake() } shouldBe null
    }
    "tryTake behaves like take if there is a value" {
      val tm = TMVar.new(100)
      atomically {
        tm.tryTake()
      } shouldBe 100
      atomically { tm.tryTake() } shouldBe null
    }
    "tryTake returns null on empty" {
      val tm = TMVar.empty<Int>()
      atomically { tm.tryTake() } shouldBe null
    }
    "read retries on empty" {
      val tm = TMVar.empty<Int>()
      atomically {
        stm { tm.read().let { true } } orElse { false }
      } shouldBe false
      atomically { tm.tryTake() } shouldBe null
    }
    "read returns the value if not empty and does not remove it" {
      val tm = TMVar.new(10)
      atomically {
        tm.read()
      } shouldBe 10
      atomically { tm.tryTake() } shouldBe 10
    }
    "tryRead behaves like read if there is a value" {
      val tm = TMVar.new(100)
      atomically { tm.tryRead() } shouldBe
        atomically { tm.read() }
      atomically { tm.tryTake() } shouldBe 100
    }
    "tryRead returns null if there is no value" {
      val tm = TMVar.empty<Int>()
      atomically { tm.tryRead() } shouldBe null
      atomically { tm.tryTake() } shouldBe null
    }
    "put retries if there is already a value" {
      val tm = TMVar.new(5)
      atomically {
        stm { tm.put(100).let { true } } orElse { false }
      } shouldBe false
      atomically { tm.tryTake() } shouldBe 5
    }
    "put replaces the value if it was empty" {
      val tm = TMVar.empty<Int>()
      atomically {
        tm.put(100)
        tm.tryTake()
      } shouldBe 100
    }
    "tryPut behaves like put if there is no value" {
      val tm = TMVar.empty<Int>()
      atomically {
        tm.tryPut(100)
        tm.tryTake()
      } shouldBe atomically {
        tm.put(100)
        tm.tryTake()
      }
      atomically { tm.tryPut(30) } shouldBe true
      atomically { tm.tryTake() } shouldBe 30
    }
    "tryPut returns false if there is already a value" {
      val tm = TMVar.new(30)
      atomically { tm.tryPut(20) } shouldBe false
      atomically { tm.tryTake() } shouldBe 30
    }
    "swap replaces the current value only if it is not null" {
      val tm = TMVar.new(30)
      atomically { tm.swap(25) } shouldBeExactly 30
      atomically { tm.take() } shouldBeExactly 25
    }
    "swap should retry if there is no value" {
      val tm = TMVar.empty<Int>()
      atomically {
        stm { tm.swap(10).let { true } } orElse { false }
      } shouldBe false
      atomically { tm.tryTake() } shouldBe null
    }
  }
)
