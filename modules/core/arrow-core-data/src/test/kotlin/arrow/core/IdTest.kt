package arrow.core

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.comonad.comonad
import arrow.core.extensions.id.eq.eq
import arrow.core.extensions.id.hash.hash
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.id.monoid.monoid
import arrow.core.extensions.id.semigroup.semigroup
import arrow.core.extensions.id.show.show
import arrow.core.extensions.id.traverse.traverse
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class IdTest : UnitSpec() {
  init {
    testLaws(
      SemigroupLaws.laws(Id.semigroup(Int.semigroup()), Id(1), Id(2), Id(3), Id.eq(Int.eq())),
      MonoidLaws.laws(Id.monoid(Int.monoid()), Gen.constant(Id(1)), Id.eq(Int.eq())),
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
