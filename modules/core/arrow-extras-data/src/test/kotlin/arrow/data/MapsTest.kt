package arrow.data

import arrow.core.Tuple2
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.Assert
import org.junit.runner.RunWith
import arrow.test.UnitSpec

@RunWith(KotlinTestRunner::class)
class MapsTest : UnitSpec() {

  init {

    "multiple instances with same values of Tuple2 and Pair should give the same map" {
      val mapOfTuple = mapOf(Tuple2("one", 1), Tuple2("two", 2), Tuple2("three", 3))
      val mapOfPair = mapOf("one" to 1, "two" to 2, "three" to 3)
      Assert.assertEquals(mapOfPair, mapOfTuple)
    }

    "multiple instances with same values of Tuple2 and Pair in different order should give the same map" {
      val mapOfTuple = mapOf(Tuple2("one", 1), Tuple2("two", 2), Tuple2("three", 3))
      val mapOfPair = mapOf("two" to 2, "three" to 3, "one" to 1)
      Assert.assertEquals(mapOfPair, mapOfTuple)
    }

    "different instances of Tuple2 and Pair should give a different maps" {
      val mapOfTuple = mapOf(Tuple2("one", 1), Tuple2("two", 2), Tuple2("three", 3))
      val mapOfPair = mapOf("four" to 4, "two" to 2, "three" to 3)
      Assert.assertNotEquals(mapOfPair, mapOfTuple)
    }

    "an instance of Tuple2 should give the same map as the instance of Pair with same value" {
      val mapOfTuple = mapOf(Tuple2("one", 1))
      val mapOfPair = mapOf("one" to 1)
      Assert.assertEquals(mapOfPair, mapOfTuple)
    }

  }
}
