package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.Id
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.test.concurrency.SideEffect
import arrow.test.generators.*
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FoldableLaws {

  fun <F> laws(FF: Foldable<F>, cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>): List<Law> =
    listOf(
      Law("Foldable Laws: Left fold consistent with foldMap") { FF.leftFoldConsistentWithFoldMap(cf, EQ) },
      Law("Foldable Laws: Right fold consistent with foldMap") { FF.rightFoldConsistentWithFoldMap(cf, EQ) },
      Law("Foldable Laws: Exists is consistent with find") { FF.existsConsistentWithFind(cf) },
      Law("Foldable Laws: Exists is lazy") { FF.existsIsLazy(cf, EQ) },
      Law("Foldable Laws: ForAll is lazy") { FF.forAllIsLazy(cf, EQ) },
      Law("Foldable Laws: ForAll consistent with exists") { FF.forallConsistentWithExists(cf) },
      Law("Foldable Laws: ForAll returns true if isEmpty") { FF.forallReturnsTrueIfEmpty(cf) },
      Law("Foldable Laws: FoldM for Id is equivalent to fold left") { FF.foldMIdIsFoldL(cf, EQ) }
    )

  fun <F> Foldable<F>.leftFoldConsistentWithFoldMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.intSmall().map(cf)) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.foldMap(this, f).equalUnderTheLaw(fa.foldLeft(empty()) { acc, a -> acc.combine(f(a)) }, EQ)
      }
    }

  fun <F> Foldable<F>.rightFoldConsistentWithFoldMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.intSmall().map(cf)) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.foldMap(this, f).equalUnderTheLaw(fa.foldRight(Eval.later { empty() }) { a, lb: Eval<Int> -> lb.map { f(a).combine(it) } }.value(), EQ)
      }
    }

  fun <F> Foldable<F>.existsConsistentWithFind(cf: (Int) -> Kind<F, Int>) =
    forAll(Gen.intPredicate(), Gen.int().map(cf)) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      fa.exists(f).equalUnderTheLaw(fa.find(f).fold({ false }, { true }), Eq.any())
    }

  fun <F> Foldable<F>.existsIsLazy(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
    forAll(Gen.int().map(cf)) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      fa.exists { _ ->
        sideEffect.increment()
        true
      }
      val expected = if (fa.isEmpty()) 0 else 1
      sideEffect.counter.equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.forAllIsLazy(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
    forAll(Gen.int().map(cf)) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      fa.forAll { _ ->
        sideEffect.increment()
        true
      }
      val expected = if (fa.isEmpty()) 0 else 1
      sideEffect.counter.equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.forallConsistentWithExists(cf: (Int) -> Kind<F, Int>) =
    forAll(Gen.intPredicate(), Gen.int().map(cf)) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      if (fa.forAll(f)) {
        // if f is true for all elements, then there cannot be an element for which
        // it does not hold.
        val negationExists = fa.exists { a -> !(f(a)) }
        // if f is true for all elements, then either there must be no elements
        // or there must exist an element for which it is true.
        !negationExists && (fa.isEmpty() || fa.exists(f))
      } else true
    }

  fun <F> Foldable<F>.forallReturnsTrueIfEmpty(cf: (Int) -> Kind<F, Int>) =
    forAll(Gen.intPredicate(), Gen.int().map(cf)) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      !fa.isEmpty() || fa.forAll(f)
    }

  fun <F> Foldable<F>.foldMIdIsFoldL(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.intSmall().map(cf)) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        val foldL: Int = fa.foldLeft(empty()) { acc, a -> acc.combine(f(a)) }
        val foldM: Int = fa.foldM(Id.monad(), empty()) { acc, a -> Id(acc.combine(f(a))) }.extract()
        foldM.equalUnderTheLaw(foldL, EQ)
      }
    }
}
