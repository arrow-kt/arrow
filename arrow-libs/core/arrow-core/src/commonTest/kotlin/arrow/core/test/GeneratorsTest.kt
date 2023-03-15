package arrow.core.test

import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.assume
import io.kotest.property.checkAll

class GeneratorsTest : FreeSpec( {

  "functionAToB" - {
    "should return same result when invoked multiple times" {
      checkAll(Arb.string(), Arb.functionAToB<String, Int>(Arb.int())) { a, fn ->
        fn(a) shouldBe fn(a)
      }
    }

    "should return some different values" {
      Arb.functionAToB<String, Int>(Arb.int()).take(100)
        .forAtLeastOne {  fn ->
          checkAll(100, Arb.string(), Arb.string()) {
            a,b ->
              assume(a != b)
              fn(a) shouldNotBe fn(b)
          }
        }
    }
  }

  "functionABCToD" - {
    "should return same result when invoked multiple times" {
      checkAll(Arb.string(), Arb.string(), Arb.string(), Arb.functionABCToD<String, String, String, Int>(Arb.int())) { a, b, c, fn ->
        fn(a,b,c) shouldBe fn(a,b,c)
      }
    }

    "should return some different values" {
      Arb.functionABCToD<String, String, String, Int>(Arb.int()).take(100)
        .forAtLeastOne {  fn ->
          checkAll(100, Arb.string(), Arb.string(), Arb.string()) {
              a,b,c ->
            assume(a != c)
            fn(a,b,c) shouldNotBe fn(c,b,a)
          }
        }
    }
  }

})
