package arrow.recursion

import arrow.core.Eval
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.extensions.eq
import arrow.core.extensions.nonemptylist.eq.eq
import arrow.core.none
import arrow.core.some
import arrow.recursion.extensions.nonemptylist.birecursive.birecursive
import arrow.recursion.extensions.nonemptylistf.traverse.traverse
import arrow.recursion.pattern.NonEmptyListF
import arrow.recursion.pattern.fix
import arrow.test.UnitSpec
import arrow.test.laws.BirecursiveLaws
import io.kotlintest.properties.Gen

class NonEmptyListBirecursiveTest : UnitSpec() {
  init {
    testLaws(
      BirecursiveLaws.laws(
        NonEmptyListF.traverse(),
        NonEmptyList.birecursive(),
        Gen.list(Gen.int()).filter { it.isNotEmpty() }.map { Nel.fromListUnsafe(it) },
        Gen.constant(Nel.fromListUnsafe((0..5000).toList())),
        Nel.eq(Int.eq()),
        {
          it.fix().tail.fold({ 0 }, { it + 1 })
        },
        {
          Eval.now(it.fix().tail.fold({ 0 }, { it + 1 }))
        },
        {
          NonEmptyListF(it, when (it) {
            0 -> none()
            else -> (it - 1).some()
          })
        },
        {
          Eval.now(NonEmptyListF(it, when (it) {
            0 -> none()
            else -> (it - 1).some()
          }))
        }
      )
    )
  }
}
