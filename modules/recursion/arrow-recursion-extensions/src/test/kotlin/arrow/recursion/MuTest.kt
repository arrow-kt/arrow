package arrow.recursion

import arrow.recursion.data.Mu
import arrow.recursion.extensions.mu.birecursive.birecursive
import arrow.test.UnitSpec

class MuTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Mu.birecursive()))
  }
}
