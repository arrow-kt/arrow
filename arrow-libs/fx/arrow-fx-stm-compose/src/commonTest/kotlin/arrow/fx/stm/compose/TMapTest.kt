package arrow.fx.stm.compose

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TMapTest {

  @Test fun insertValues() = runTest {
    checkAll(Arb.int(), Arb.int()) { k, v ->
      val map = TMap.new<Int, Int>()
      atomically { map.insert(k, v) }
      atomically { map[k] } shouldBe v
    }
  }

  @Test fun insertMultipleValues() = runTest {
    checkAll(Arb.map(Arb.int(), Arb.int())) { pairs ->
      val map = TMap.new<Int, Int>()
      atomically {
        for ((k, v) in pairs) map.insert(k, v)
      }
      atomically {
        for ((k, v) in pairs) map[k] shouldBe v
      }
    }
  }

  @Test fun insertMultipleCollidingValues() = runTest {
    checkAll(Arb.map(Arb.int(), Arb.int())) { pairs ->
      val map = TMap.new<Int, Int>() // hash function that always returns 0
      atomically {
        for ((k, v) in pairs) map.insert(k, v)
      }
      atomically {
        for ((k, v) in pairs) map[k] shouldBe v
      }
    }
  }

  @Test fun insertAndRemove() = runTest {
    checkAll(Arb.int(), Arb.int()) { k, v ->
      val map = TMap.new<Int, Int>()
      atomically { map.insert(k, v) }
      atomically { map[k] } shouldBe v
      atomically { map.remove(k) }
      atomically { map[k] } shouldBe null
    }
  }

  @Test fun update() = runTest {
    checkAll(Arb.int(), Arb.int(), Arb.int()) { k, v, g ->
      val map = TMap.new<Int, Int>()
      atomically { map.insert(k, v) }
      atomically { map[k] } shouldBe v
      atomically { map.update(k) { v + g } }
      atomically { map[k] } shouldBe v + g
    }
  }
}
