package arrow.recursion

import arrow.core.Eval
import arrow.core.Option
import arrow.core.extensions.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.option.foldable.fold
import arrow.core.extensions.option.foldable.foldRight
import arrow.core.extensions.option.traverse.traverse
import arrow.recursion.extensions.birecursive
import arrow.test.UnitSpec
import arrow.test.generators.intSmall
import arrow.test.laws.BirecursiveLaws
import io.kotlintest.properties.Gen

class IntBirecursive : UnitSpec() {
  init {

    testLaws(
      BirecursiveLaws.laws(
        Option.traverse(),
        Int.birecursive(),
        Gen.intSmall().filter { it in 0..100 },
        Gen.constant(5000),
        Int.eq(),
        {
          it.fold(Int.monoid())
        },
        {
          it.foldRight(Eval.now(0)) { v, acc -> acc.map { it + v } }
        },
        Int.birecursive().project(),
        {
          Eval.later {
            Int.birecursive().run {
              it.projectT()
            }
          }
        }
      )
    )
  }
}

class LongBirecursive : UnitSpec() {
  init {
    testLaws(
      BirecursiveLaws.laws(
        Option.traverse(),
        Long.birecursive(),
        Gen.intSmall().filter { it in 0..100 }.map { it.toLong() },
        Gen.constant(5000L),
        Long.eq(),
        {
          it.fold(Int.monoid())
        },
        {
          it.foldRight(Eval.now(0)) { v, acc -> acc.map { it + v } }
        },
        Int.birecursive().project(),
        {
          Eval.later {
            Int.birecursive().run {
              it.projectT()
            }
          }
        }
      )
    )
  }
}
