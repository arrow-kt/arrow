package arrow.recursion

import arrow.recursion.data.Mu
import arrow.recursion.instances.mu.birecursive.birecursive
import arrow.test.UnitSpec
import arrow.test.laws.BirecursiveLaws

class MuTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Mu.birecursive()))
  }
}
