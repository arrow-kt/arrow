package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.instances.extensions
import arrow.instances.monoid
import arrow.instances.semigroup
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function0Test : UnitSpec() {
  val EQ: Eq<Kind<ForFunction0, Int>> = Eq { a, b ->
    a() == b()
  }

  init {
    ForFunction0 extensions {
      testLaws(
        SemigroupLaws.laws(Function0.semigroup(Int.semigroup()), Function0 { 1 }, Function0 { 2 }, Function0 { 3 }, EQ),
        MonoidLaws.laws(Function0.monoid(Int.monoid()), Function0 { 1 }, EQ),
        MonadLaws.laws(this, EQ),
        ComonadLaws.laws(this, { { it }.k() }, EQ)
      )
    }

    "Semigroup of Function0<0> is a Function0<O> of the Semigroup of O" {
      forAll { a: String, b: String ->
        val f1 = String.semigroup().run { Function0 { a.combine(b) } }
        val f2 = Function0.semigroup(String.monoid()).run { (Function0 { a }).combine(Function0 { b }) }
        f1() == f2()
      }
    }
  }
}
