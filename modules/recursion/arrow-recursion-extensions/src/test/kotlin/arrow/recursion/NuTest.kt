package arrow.recursion

import arrow.recursion.data.Nu
import arrow.recursion.extensions.nu.birecursive.birecursive
import arrow.test.UnitSpec

class NuTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Nu.birecursive()))
  }
}
