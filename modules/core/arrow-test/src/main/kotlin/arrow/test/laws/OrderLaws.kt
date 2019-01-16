package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Order
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import java.util.*

object OrderLaws {

  fun <F> laws(O: Order<F>, fGen: Gen<F>, funcGen: Gen<(F) -> F>): List<Law> {
    val random = Random()
    return EqLaws.laws(O) {
      val fSeq = fGen.random()
      fSeq.elementAt(random.nextInt(fSeq.count()))
    } + listOf(
      Law("Order law: reflexivity equality") { O.reflexitivityEq(fGen) },
      Law("Order law: symmetry equality") { O.symmetryEq(fGen) },
      Law("Order law: antisymmetry equality") { O.antisymmetryEq(fGen, funcGen) },
      Law("Order law: transitivity equality") { O.transitivityEq(fGen) },
      Law("Order law: reflexivity partial order") { O.reflexivityPartialOrder(fGen) },
      Law("Order law: antisymmetry partial order") { O.antisymmetryPartialOrder(fGen) },
      Law("Order law: transitivity partial order") { O.transitivityPartialOrder(fGen) },
      Law("Order law: greater than or equal partial order") { O.greaterThanOrEqualPartialOrder(fGen) },
      Law("Order law: lesser than partial order") { O.lesserThanPartialOrder(fGen) },
      Law("Order law: greater than partial order") { O.greaterThanPartialOrder(fGen) },
      Law("Order law: totality order") { O.totalityOrder(fGen) },
      Law("Order law: compare order") { O.compareOrder(fGen) },
      Law("Order law: min order") { O.minOrder(fGen) },
      Law("Order law: max order") { O.maxOrder(fGen) },
      Law("Order law: operator compareTo delegates to compare order") { O.operatorCompareToOrder(fGen) }
    )
  }

  fun <F> Order<F>.reflexitivityEq(fGen: Gen<F>) = run {
    val eq = this
    forAll(fGen) { x ->
      x.equalUnderTheLaw(x, eq)
    }
  }

  fun <F> Order<F>.symmetryEq(fGen: Gen<F>) =
    forAll(fGen, fGen) { x, y ->
      x.eqv(y).equalUnderTheLaw(y.eqv(x), Eq.any())
    }

  fun <F> Order<F>.antisymmetryEq(fGen: Gen<F>, funcGen: Gen<(F) -> F>) =
    forAll(fGen, fGen, funcGen) { x, y, f ->
      !x.eqv(y) || f(x).eqv(f(y))
    }

  fun <F> Order<F>.transitivityEq(fGen: Gen<F>) =
    forAll(fGen, fGen, fGen) { x, y, z ->
      !(x.eqv(y) && y.eqv(z)) || x.eqv(z)
    }

  fun <F> Order<F>.reflexivityPartialOrder(fGen: Gen<F>) =
    forAll(fGen) { x ->
      x.lte(x)
    }

  fun <F> Order<F>.antisymmetryPartialOrder(fGen: Gen<F>) =
    forAll(fGen, fGen) { x, y ->
      !(x.lte(y) && y.lte(x)) || x.eqv(y)
    }

  fun <F> Order<F>.transitivityPartialOrder(fGen: Gen<F>) =
    forAll(fGen, fGen, fGen) { x, y, z ->
      !(x.lte(y) && y.lte(x)) || x.lte(z)
    }

  fun <F> Order<F>.greaterThanOrEqualPartialOrder(fGen: Gen<F>) =
    forAll(fGen, fGen) { x, y ->
      x.lte(y) == y.gte(x)
    }

  fun <F> Order<F>.lesserThanPartialOrder(fGen: Gen<F>) =
    forAll(fGen, fGen) { x, y ->
      x.lt(y) == (x.lte(y) && x.neqv(y))
    }

  fun <F> Order<F>.greaterThanPartialOrder(fGen: Gen<F>) =
    forAll(fGen, fGen) { x, y ->
      x.lt(y) == y.gt(x)
    }

  fun <F> Order<F>.totalityOrder(fGen: Gen<F>) =
    forAll(fGen, fGen) { x, y ->
      x.lte(y) || y.lte(x)
    }

  fun <F> Order<F>.compareOrder(fGen: Gen<F>) =
    forAll(fGen, fGen) { x, y ->
      val c = x.compare(y)
      ((c < 0) == x.lt(y)) && ((c == 0) == x.eqv(y)) && ((c > 0) == x.gt(y))
    }

  fun <F> Order<F>.minOrder(fGen: Gen<F>) =
    forAll(fGen, fGen) { x, y ->
      val c = x.compare(y)
      val m = x.min(y)
      if (c < 0) m == x
      else if (c == 0) (m == x) && (m == y)
      else m == y
    }

  fun <F> Order<F>.maxOrder(fGen: Gen<F>) =
    forAll(fGen, fGen) { x, y ->
      val c = x.compare(y)
      val m = x.max(y)
      if (c < 0) m == y
      else if (c == 0) (m == x) && (m == y)
      else m == x
    }

  fun <F> Order<F>.operatorCompareToOrder(fGen: Gen<F>): Unit =
    forAll(fGen, fGen) { x, y ->
      x.compare(y) == x.compareTo(y)
    }
}
