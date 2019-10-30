package arrow.recursion

import arrow.core.Eval
import arrow.core.ForOption
import arrow.core.FunctionK
import arrow.core.Option
import arrow.core.extensions.eval.monad.monad
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.monad.monad
import arrow.core.extensions.option.traverse.traverse
import arrow.core.fix
import arrow.core.some
import arrow.core.value
import arrow.free.Free
import arrow.free.extensions.free.eq.eq
import arrow.recursion.extensions.free.birecursive.birecursive
import arrow.recursion.extensions.freef.traverse.traverse
import arrow.recursion.pattern.FreeF
import arrow.recursion.pattern.fix
import arrow.test.UnitSpec
import arrow.test.generators.intSmall
import arrow.test.laws.BirecursiveLaws
import io.kotlintest.properties.Gen

class FreeBirecursive : UnitSpec() {
  init {
    testLaws(
      BirecursiveLaws.laws(
        FreeF.traverse<ForOption, Int>(Option.traverse()),
        Free.birecursive<ForOption, Int>(Option.functor()),
        Gen.intSmall().filter { it in 0..100 }.map { it.unfoldFree() },
        Gen.constant(5000).map { it.unfoldFree() },
        Free.eq(Option.monad(), FunctionK.id()),
        {
          it.fix().let { f ->
            when (f) {
              is FreeF.Pure -> f.e
              is FreeF.Impure -> f.fa.fix().fold({ 1 }, { it + 1 })
            }
          }
        },
        {
          Eval.later {
            it.fix().let { f ->
              when (f) {
                is FreeF.Pure -> f.e
                is FreeF.Impure -> f.fa.fix().fold({ 1 }, { it + 1 })
              }
            }
          }
        },
        {
          if (it > 0) FreeF.Impure((it - 1).some())
          else FreeF.Pure(1)
        },
        {
          Eval.now(
            if (it > 0) FreeF.Impure((it - 1).some())
            else FreeF.Pure(1)
          )
        }
      )
    )
  }
}

fun Int.unfoldFree(): Free<ForOption, Int> = Free.birecursive<ForOption, Int>(Option.functor()).run {
  this@unfoldFree.anaM(FreeF.traverse(Option.traverse()), Eval.monad()) {
    Eval.now(
      if (it == 0) FreeF.Pure(1)
      else FreeF.Impure((it - 1).some())
    )
  }.value()
}
