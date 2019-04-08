package arrow.recursion

import arrow.core.Eval
import arrow.data.ListK
import arrow.data.k
import arrow.recursion.extensions.listf.traverse.traverse
import arrow.recursion.extensions.listk.birecursive.birecursive
import arrow.recursion.pattern.ListF
import arrow.recursion.pattern.fix
import arrow.test.UnitSpec
import arrow.test.generators.listK
import arrow.test.laws.BirecursiveLaws
import io.kotlintest.properties.Gen
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class ListKBirecursive : UnitSpec() {
  init {
    testLaws(
      BirecursiveLaws.laws(
        ListF.traverse(),
        ListK.birecursive(),
        Gen.listK(Gen.int()),
        Gen.constant((0..5000).toList().k()),
        {
          when (val fa = it.fix()) {
            is ListF.NilF -> 0
            is ListF.ConsF -> fa.a + fa.tail
          }
        },
        {
          when (val fa = it.fix()) {
            is ListF.NilF -> Eval.now(0)
            is ListF.ConsF -> Eval.now(fa.a + fa.tail)
          }
        },
        {
          when (it) {
            0 -> ListF.NilF()
            else -> ListF.ConsF(it, it - 1)
          }
        },
        {
          Eval.later {
            when (it) {
              0 -> ListF.NilF<Int, Int>()
              else -> ListF.ConsF<Int, Int>(it, it - 1)
            }
          }
        }
      )
    )
  }
}