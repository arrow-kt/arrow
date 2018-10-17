package arrow.recursion

import arrow.recursion.data.Fix
import arrow.recursion.instances.fix.birecursive.birecursive
import arrow.test.UnitSpec
import arrow.test.laws.BirecursiveLaws

class FixTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Fix.birecursive()))
  }
}
