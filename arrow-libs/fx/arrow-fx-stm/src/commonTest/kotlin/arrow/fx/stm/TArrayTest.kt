package arrow.fx.stm

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe

class TArrayTest : StringSpec({
    "creating an array" {
      val t1 = TArray.new(10) { it }
      t1.size() shouldBeExactly 10
      atomically { t1.fold(0) { acc, v -> acc + v } } shouldBeExactly (0..9).sum()
      atomically { (0..9).fold(true) { acc, v -> t1.get(v) == v && acc } } shouldBe true

      val t2 = atomically { newTArray(10) { it } }
      t2.size() shouldBeExactly 10
      atomically { t2.fold(0) { acc, v -> acc + v } } shouldBeExactly (0..9).sum()
      atomically { (0..9).fold(true) { acc, v -> t2.get(v) == v && acc } } shouldBe true
    }
    "get should get the correct value" {
      val t2 = atomically { newTArray(20) { it } }
      atomically { (0..19).fold(true) { acc, v -> t2.get(v) == v && acc } } shouldBe true
    }
    "write should write to the correct value" {
      val t2 = atomically { newTArray(20) { it } }
      atomically { t2.get(5) } shouldBeExactly 5
      atomically { t2[5] = 2 }
      atomically { t2.get(5) } shouldBeExactly 2
    }
    "transform should perform an operation on each element" {
      val t2 = atomically { newTArray(20) { it } }
      atomically { t2.transform { it * 2 } }
      atomically { t2.fold(0) { acc, v -> acc + v } } shouldBeExactly (0..19).sum() * 2
    }
  }
)
