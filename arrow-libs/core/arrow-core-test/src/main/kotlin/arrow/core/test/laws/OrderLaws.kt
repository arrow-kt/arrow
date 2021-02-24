package arrow.core.test.laws

import arrow.core.EQ
import arrow.core.GT
import arrow.core.LT
import arrow.core.Ordering
import arrow.typeclasses.Order
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object OrderLaws {
  fun <A> laws(OA: Order<A>, gen: Gen<A>): List<Law> =
    EqLaws.laws(OA, gen) + listOf(
      Law("OrderLaws: x.gte(y) = y.lte(x)") { OA.gteEqualsLteReversed(gen) },
      Law("OrderLaws: x.lt(y) = x.lte(y) && x != y") { OA.ltEqualsLteAndIneq(gen) },
      Law("OrderLaws: x.gt(y) = y.lt(x)") { OA.gtEqualsLtReversed(gen) },
      Law("OrderLaws: x.lt(y) = x.compare(y) == LT") { OA.ltEqualsCompareLT(gen) },
      Law("OrderLaws: x.gt(y) = x.compare(y) == GT") { OA.gtEqualsCompareGT(gen) },
      Law("OrderLaws: x == y = x.compare(y) == EQ") { OA.eqvEqualsCompareEQ(gen) },
      Law("OrderLaws: x.min(y) = if (x.lte(y)) x else y") { OA.minConsistent(gen) },
      Law("OrderLaws: x.max(y) = if (x.gte(y)) x else y") { OA.maxConsistent(gen) },
      Law("OrderLaws: Ordering.fromInt(x.compareTo(y)) = x.compare(y)") { OA.compareToConsistent(gen) }
    )
}

private fun <F> Order<F>.compareToConsistent(gen: Gen<F>) =
  forAll(gen, gen) { x, y ->
    Ordering.fromInt(x.compareTo(y)) === x.compare(y)
  }

private fun <F> Order<F>.maxConsistent(gen: Gen<F>) =
  forAll(gen, gen) { x, y ->
    x.max(y) == (if (x.gt(y)) x else y)
  }

private fun <F> Order<F>.minConsistent(gen: Gen<F>) =
  forAll(gen, gen) { x, y ->
    x.min(y) == (if (x.lt(y)) x else y)
  }

private fun <F> Order<F>.eqvEqualsCompareEQ(gen: Gen<F>) =
  forAll(gen, gen) { x, y ->
    x.eqv(y) == (x.compare(y) == EQ)
  }

private fun <F> Order<F>.gtEqualsCompareGT(gen: Gen<F>) =
  forAll(gen, gen) { x, y ->
    x.gt(y) == (x.compare(y) == GT)
  }

private fun <F> Order<F>.ltEqualsCompareLT(gen: Gen<F>) =
  forAll(gen, gen) { x, y ->
    x.lt(y) == (x.compare(y) == LT)
  }

private fun <F> Order<F>.gtEqualsLtReversed(gen: Gen<F>) =
  forAll(gen, gen) { x, y ->
    x.gt(y) == y.lt(x)
  }

private fun <F> Order<F>.ltEqualsLteAndIneq(gen: Gen<F>) =
  forAll(gen, gen) { x, y ->
    x.lt(y) == (x.lte(y) && x.neqv(y))
  }

private fun <F> Order<F>.gteEqualsLteReversed(gen: Gen<F>) =
  forAll(gen, gen) { x, y ->
    x.gte(y) == y.lte(x)
  }
