package arrow.recursion

import arrow.recursion.data.Fix
import arrow.recursion.extensions.fix.birecursive.birecursive
import arrow.test.UnitSpec

class FixTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Fix.birecursive()))
  }
}
