package arrow.core

import arrow.Kind
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.const.eq.eq
import arrow.core.extensions.const.functor.functor
import arrow.core.extensions.const.show.show
import arrow.core.extensions.const.traverseFilter.traverseFilter
import arrow.core.extensions.eq
import arrow.core.extensions.monoid
import arrow.test.UnitSpec
import arrow.test.generators.genConst
import arrow.test.generators.genK
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.EqLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseFilterLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen

class ConstTest : UnitSpec() {

  fun <A> EQK(EQA: Eq<A>): EqK<ConstPartialOf<A>> = object : EqK<ConstPartialOf<A>> {
    override fun <B> Kind<ConstPartialOf<A>, B>.eqK(other: Kind<ConstPartialOf<A>, B>, EQ: Eq<B>): Boolean =
      Const.eq<A, B>(EQA).run {
        this@eqK.fix().eqv(other.fix())
      }
  }

  init {
    Int.monoid().run {
      testLaws(
        TraverseFilterLaws.laws(Const.traverseFilter(),
          Const.applicative(this),
          Const.genK(Gen.int()),
          EQK(Int.eq())),
        ApplicativeLaws.laws(Const.applicative(this), Const.functor(), Const.genK(Gen.int()), EQK(Int.eq())),
        EqLaws.laws(Const.eq<Int, Int>(Eq.any()), Gen.genConst<Int, Int>(Gen.int())),
        ShowLaws.laws(Const.show(), Const.eq<Int, Int>(Eq.any()), Gen.genConst<Int, Int>(Gen.int()))
      )
    }
  }
}
