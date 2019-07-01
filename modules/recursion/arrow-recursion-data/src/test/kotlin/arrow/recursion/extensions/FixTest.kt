package arrow.recursion.extensions

import arrow.recursion.data.Fix
import arrow.recursion.extensions.fix.birecursive.birecursive
import arrow.test.UnitSpec
import arrow.test.laws.BirecursiveLaws

class FixTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Fix.birecursive()))
  }
}
