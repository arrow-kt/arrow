package arrow.core

import arrow.test.UnitSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe

class MapsTest : UnitSpec() {

  init {

    "multiple instances with same values of Tuple2 and Pair should give the same map" {
      val mapOfTuple = mapOf(Tuple2("one", 1), Tuple2("two", 2), Tuple2("three", 3))
      val mapOfPair = mapOf("one" to 1, "two" to 2, "three" to 3)
      mapOfPair shouldBe mapOfTuple
    }

    "multiple instances with same values of Tuple2 and Pair in different order should give the same map" {
      val mapOfTuple = mapOf(Tuple2("one", 1), Tuple2("two", 2), Tuple2("three", 3))
      val mapOfPair = mapOf("two" to 2, "three" to 3, "one" to 1)
      mapOfPair shouldBe mapOfTuple
    }

    "different instances of Tuple2 and Pair should give a different maps" {
      val mapOfTuple = mapOf(Tuple2("one", 1), Tuple2("two", 2), Tuple2("three", 3))
      val mapOfPair = mapOf("four" to 4, "two" to 2, "three" to 3)
      mapOfPair shouldNotBe mapOfTuple
    }

    "an instance of Tuple2 should give the same map as the instance of Pair with same value" {
      val mapOfTuple = mapOf(Tuple2("one", 1))
      val mapOfPair = mapOf("one" to 1)
      mapOfPair shouldBe mapOfTuple
    }
  }
}
