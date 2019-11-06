package arrow.recursion

import arrow.core.Eval
import arrow.core.ForOption
import arrow.core.None
import arrow.core.Option
import arrow.core.extensions.eval.monad.monad
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.traverse.traverse
import arrow.core.fix
import arrow.core.none
import arrow.core.some
import arrow.core.value
import arrow.recursion.data.Fix
import arrow.recursion.extensions.fix.birecursive.birecursive
import arrow.test.UnitSpec
import arrow.test.laws.BirecursiveLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class FixBirecursive : UnitSpec() {
  init {
    testLaws(
      BirecursiveLaws.laws(
        Option.traverse(),
        Fix.birecursive(Option.functor()),
        Gen.list(Gen.int()).map { it.toFix() },
        Gen.constant((0..5000).toList()).map { it.toFix() },
        Eq.any(),
        {
          it.fix().fold({ 0 }, { it + 1 })
        },
        {
          Eval.now(it.fix().fold({ 0 }, { it + 1 }))
        },
        {
          when (it) {
            0 -> none()
            else -> (it - 1).some()
          }
        },
        {
          Eval.later {
            when (it) {
              0 -> none()
              else -> (it - 1).some()
            }
          }
        }
      )
    )
  }
}

fun <A> List<A>.toFix(): Fix<ForOption> = Fix.birecursive(Option.functor()).run {
  this@toFix.anaM(Option.traverse(), Eval.monad()) {
    Eval.later {
      if (it.isEmpty()) None
      else it.drop(1).some()
    }
  }.value()
}
