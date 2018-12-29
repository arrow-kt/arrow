package arrow.core

import arrow.instances.eq
import arrow.instances.hash
import arrow.instances.id.applicative.applicative
import arrow.instances.id.comonad.comonad
import arrow.instances.id.eq.eq
import arrow.instances.id.hash.hash
import arrow.instances.id.monad.monad
import arrow.instances.id.monoid.monoid
import arrow.instances.id.show.show
import arrow.instances.id.traverse.traverse
import arrow.instances.id.semigroup.semigroup
import arrow.instances.id.monoid.monoid
import arrow.instances.monoid
import arrow.instances.semigroup
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IdTest : UnitSpec() {
  init {
    testLaws(
      SemigroupLaws.laws(Id.semigroup(Int.semigroup()), Id(1), Id(2), Id(3), Id.eq(Int.eq())),
      MonoidLaws.laws(Id.monoid(Int.monoid()), Id(1), Id.eq(Int.eq())),
      ShowLaws.laws(Id.show(), Eq.any()) { Id(it) },
      MonadLaws.laws(Id.monad(), Eq.any()),
      TraverseLaws.laws(Id.traverse(), Id.applicative(), ::Id, Eq.any()),
      ComonadLaws.laws(Id.comonad(), ::Id, Eq.any()),
      HashLaws.laws(Id.hash(Int.hash()), Id.eq(Int.eq())) { Id(it) }
    )

    "Semigroup of Id<A> is Id<Semigroup<A>>"() {
      forAll { a: Int ->
        val left = Id.semigroup(Int.semigroup()).run {
          Id(a).combine(Id(a))
        }

        val right = Id(Int.monoid().run { a.combine(a) })

        Id.eq(Int.eq()).run { left.eqv(right) }
      }
    }

    "Id<A>.empty() is Id{A.empty()}" {
      forAll { a: Int, b: Int ->
        val left = Id.monoid(Int.monoid()).run { empty() }
        val right = Id(Int.monoid().run { empty() })
        Id.eq(Int.eq()).run { left.eqv(right) }
      }
    }
  }
}
