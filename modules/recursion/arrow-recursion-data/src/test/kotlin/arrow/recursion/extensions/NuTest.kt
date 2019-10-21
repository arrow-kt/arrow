package arrow.recursion.extensions

import arrow.recursion.data.Nu
import arrow.recursion.extensions.nu.birecursive.birecursive
import arrow.test.UnitSpec
import arrow.test.laws.BirecursiveLaws

class NuTest : UnitSpec() {
  init {
    testLaws(BirecursiveLaws.laws(Nu.birecursive()))
  }
}
