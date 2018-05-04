package arrow.recursion

import arrow.recursion.data.Nu
import arrow.recursion.data.birecursive
import arrow.test.UnitSpec
import arrow.recursion.laws.BirecursiveLaws

class NuTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Nu.birecursive()))
  }
}
