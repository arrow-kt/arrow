package arrow.recursion

import arrow.core.Eval
import arrow.core.ForOption
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.extensions.eval.monad.monad
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.traverse.traverse
import arrow.core.fix
import arrow.core.identity
import arrow.core.none
import arrow.core.some
import arrow.core.value
import arrow.free.Cofree
import arrow.recursion.extensions.cofree.birecursive.birecursive
import arrow.recursion.extensions.cofreef.traverse.traverse
import arrow.recursion.pattern.CofreeF
import arrow.recursion.pattern.fix
import arrow.test.UnitSpec
import arrow.test.laws.BirecursiveLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class CofreeBirecursive : UnitSpec() {
  init {
    testLaws(
      BirecursiveLaws.laws(
        CofreeF.traverse<ForOption, Int>(Option.traverse()),
        Cofree.birecursive<ForOption, Int>(Option.functor()),
        Gen.list(Gen.int()).filter { it.isNotEmpty() }.map { Nel.fromListUnsafe(it) }.map { it.toCofree() },
        Gen.constant((0..5000).toList()).map { Nel.fromListUnsafe(it) }.map { it.toCofree() },
        Eq.any(),
        {
          it.fix().let { co ->
            co.head + co.tail.fix().fold({ 0 }, ::identity)
          }
        },
        {
          Eval.later {
            it.fix().let { co ->
              co.head + co.tail.fix().fold({ 0 }, ::identity)
            }
          }
        },
        {
          when (it) {
            0 -> CofreeF(Option.functor(), 0, none())
            else -> CofreeF(Option.functor(), it, (it - 1).some())
          }
        },
        {
          Eval.later {
            when (it) {
              0 -> CofreeF(Option.functor(), 0, none())
              else -> CofreeF(Option.functor(), it, (it - 1).some())
            }
          }
        }
      )
    )
  }
}

fun <A> NonEmptyList<A>.toCofree(): Cofree<ForOption, A> = Cofree.birecursive<ForOption, A>(Option.functor()).run {
  this@toCofree.anaM(CofreeF.traverse(Option.traverse()), Eval.monad()) {
    Eval.later {
      if (it.size == 1) CofreeF(Option.functor(), it.head, none())
      else CofreeF(Option.functor(), it.head, Nel.fromListUnsafe(it.tail).some())
    }
  }.value()
}
