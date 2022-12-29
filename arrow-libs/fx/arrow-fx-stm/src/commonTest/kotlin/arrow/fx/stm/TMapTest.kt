package arrow.fx.stm

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll

class TMapTest : StringSpec({
    "insert values" {
      checkAll(Arb.int(), Arb.int()) { k, v ->
        val map = TMap.new<Int, Int>()
        atomically { map.insert(k, v) }
        atomically { map.lookup(k) } shouldBe v
      }
    }
    "insert multiple values" {
      checkAll(Arb.map(Arb.int(), Arb.int())) { pairs ->
        val map = TMap.new<Int, Int>()
        atomically {
          for ((k, v) in pairs) map.insert(k, v)
        }
        atomically {
          for ((k, v) in pairs) map.lookup(k) shouldBe v
        }
      }
    }
    "insert multiple colliding values" {
      checkAll(Arb.map(Arb.int(), Arb.int())) { pairs ->
        val map = TMap.new<Int, Int> { 0 } // hash function that always returns 0
        atomically {
          for ((k, v) in pairs) map.insert(k, v)
        }
        atomically {
          for ((k, v) in pairs) map.lookup(k) shouldBe v
        }
      }
    }
    "insert and remove" {
      checkAll(Arb.int(), Arb.int()) { k, v ->
        val map = TMap.new<Int, Int>()
        atomically { map.insert(k, v) }
        atomically { map.lookup(k) } shouldBe v
        atomically { map.remove(k) }
        atomically { map.lookup(k) } shouldBe null
      }
    }
    "update" {
      checkAll(Arb.int(), Arb.int(), Arb.int()) { k, v, g ->
        val map = TMap.new<Int, Int>()
        atomically { map.insert(k, v) }
        atomically { map.lookup(k) } shouldBe v
        atomically { map.update(k) { v + g } }
        atomically { map.lookup(k) } shouldBe v + g
      }
    }
  }
)
