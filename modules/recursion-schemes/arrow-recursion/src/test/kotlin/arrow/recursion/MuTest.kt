package arrow.recursion

import arrow.recursion.data.Mu
import arrow.recursion.data.birecursive
import arrow.test.UnitSpec
import arrow.test.laws.BirecursiveLaws

class MuTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Mu.birecursive()))
  }
}
