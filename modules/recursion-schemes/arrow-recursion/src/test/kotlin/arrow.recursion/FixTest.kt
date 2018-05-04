package arrow.recursion

import arrow.recursion.data.Fix
import arrow.recursion.data.birecursive
import arrow.test.UnitSpec
import arrow.recursion.laws.BirecursiveLaws

class FixTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Fix.birecursive()))
  }
}
