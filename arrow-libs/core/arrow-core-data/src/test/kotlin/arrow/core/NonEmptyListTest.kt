package arrow.core

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.nonemptylist.applicative.applicative
import arrow.core.extensions.nonemptylist.bimonad.bimonad
import arrow.core.extensions.nonemptylist.comonad.comonad
import arrow.core.extensions.nonemptylist.eq.eq
import arrow.core.extensions.nonemptylist.eqK.eqK
import arrow.core.extensions.nonemptylist.foldable.foldable
import arrow.core.extensions.nonemptylist.functor.functor
import arrow.core.extensions.nonemptylist.hash.hash
import arrow.core.extensions.nonemptylist.monad.monad
import arrow.core.extensions.nonemptylist.order.order
import arrow.core.extensions.nonemptylist.semialign.semialign
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.nonemptylist.semigroupK.semigroupK
import arrow.core.extensions.nonemptylist.show.show
import arrow.core.extensions.nonemptylist.traverse.traverse
import arrow.core.extensions.nonemptylist.unzip.unzip
import arrow.core.extensions.order
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.laws.BimonadLaws
import arrow.core.test.laws.EqKLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.OrderLaws
import arrow.core.test.laws.SemigroupKLaws
import arrow.core.test.laws.SemigroupLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.core.test.laws.UnzipLaws
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

class NonEmptyListTest : UnitSpec() {
  init {

    val EQ1 = NonEmptyList.eq(Int.eq())

    testLaws(
      ShowLaws.laws(NonEmptyList.show(Int.show()), EQ1, Gen.nonEmptyList(Gen.int())),
      SemigroupKLaws.laws(
        NonEmptyList.semigroupK(),
        NonEmptyList.genK(),
        NonEmptyList.eqK()
      ),
      BimonadLaws.laws(
        NonEmptyList.bimonad(),
        NonEmptyList.monad(),
        NonEmptyList.comonad(),
        NonEmptyList.functor(),
        NonEmptyList.applicative(),
        NonEmptyList.monad(),
        NonEmptyList.genK(),
        NonEmptyList.eqK()
      ),
      TraverseLaws.laws(NonEmptyList.traverse(), NonEmptyList.applicative(), NonEmptyList.genK(), NonEmptyList.eqK()),
      SemigroupLaws.laws(NonEmptyList.semigroup(), Gen.nonEmptyList(Gen.int()), EQ1),
      HashLaws.laws(NonEmptyList.hash(Int.hash()), Gen.nonEmptyList(Gen.int()), EQ1),
      OrderLaws.laws(NonEmptyList.order(Int.order()), Gen.nonEmptyList(Gen.int())),
      EqKLaws.laws(
        NonEmptyList.eqK(),
        NonEmptyList.genK()
      ),
      UnzipLaws.laws(
        NonEmptyList.unzip(),
        NonEmptyList.genK(),
        NonEmptyList.eqK(),
        NonEmptyList.foldable()
      )
    )

    "can align lists with different lengths" {
      forAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        NonEmptyList.semialign().run {
          align(a, b).fix().size == max(a.size, b.size)
        }
      }

      forAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        NonEmptyList.semialign().run {
          align(a, b).fix().all.take(min(a.size, b.size)).all {
            it.isBoth
          }
        }
      }
    }

    "zip2" {
      forAll(Gen.nonEmptyList(Gen.int()), Gen.nonEmptyList(Gen.int())) { a, b ->
        val result = a.zip(b)
        val expected = a.all.zip(b.all).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip3" {
      forAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c ->
        val result = a.zip(b, c, ::Triple)
        val expected = a.all.zip(b.all, c.all, ::Triple).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip4" {
      forAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d ->
        val result = a.zip(b, c, d, ::Tuple4)
        val expected = a.all.zip(b.all, c.all, d.all, ::Tuple4).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip5" {
      forAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e ->
        val result = a.zip(b, c, d, e, ::Tuple5)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, ::Tuple5).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip6" {
      forAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f ->
        val result = a.zip(b, c, d, e, f, ::Tuple6)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, ::Tuple6).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip7" {
      forAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f, g ->
        val result = a.zip(b, c, d, e, f, g, ::Tuple7)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, ::Tuple7).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip8" {
      forAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f, g, h ->
        val result = a.zip(b, c, d, e, f, g, h, ::Tuple8)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, ::Tuple8).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip9" {
      forAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f, g, h, i ->
        val result = a.zip(b, c, d, e, f, g, h, i, ::Tuple9)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, i.all, ::Tuple9).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }

    "zip10" {
      forAll(
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int()),
        Gen.nonEmptyList(Gen.int())
      ) { a, b, c, d, e, f, g, h, i, j ->
        val result = a.zip(b, c, d, e, f, g, h, i, j, ::Tuple10)
        val expected = a.all.zip(b.all, c.all, d.all, e.all, f.all, g.all, h.all, i.all, j.all, ::Tuple10).let(NonEmptyList.Companion::fromListUnsafe)
        result == expected
      }
    }
  }
}
