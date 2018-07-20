package arrow.data

import arrow.core.*
import arrow.instances.extensions
import arrow.instances.monoid
import arrow.instances.semigroup
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith
import io.kotlintest.properties.forAll

@RunWith(KTestJUnitRunner::class)
class IdTest : UnitSpec() {
  init {

    ForId extensions {
      testLaws(
        SemigroupLaws.laws(Id.semigroup(String.semigroup()), Id.just("1"), Id.just("2"), Id.just("3"), Id.eq(Eq.any())),
        MonoidLaws.laws(Id.monoid(String.monoid()), Id.just("1"), Id.eq(Eq.any())),
        EqLaws.laws(Id.eq(Eq.any())) { Id(it) },
        ShowLaws.laws(Id.show(), Eq.any()) { Id(it) },
        MonadLaws.laws(this, Eq.any()),
        TraverseLaws.laws(Id.traverse(), this, ::Id, Eq.any()),
        ComonadLaws.laws(this, ::Id, Eq.any())
      )
    }

    "compose of Id<A> and Id<A> is Id<compose of A and A>" {
      forAll { a: String, b: String ->
        Id.just(a + b) == Id.semigroup(String.semigroup()).run { Id.just(a).combine(Id.just(b)) }
      }
    }

    "empty of Id<A> is Id<empty of A>" {
      Id.just("") == Id.monoid(String.monoid()).run { empty() }
    }
  }
}
