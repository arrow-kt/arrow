package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.Id
import arrow.core.extensions.eq
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.test.concurrency.SideEffect
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.test.generators.intPredicate
import arrow.test.generators.intSmall
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FoldableLaws {

  fun <F> laws(FF: Foldable<F>, GENK: GenK<F>): List<Law> {
    val GEN = GENK.genK(Gen.intSmall())
    val EQ = Int.eq()

    return listOf(
        Law("Foldable Laws: Left fold consistent with foldMap") { FF.leftFoldConsistentWithFoldMap(GEN, EQ) },
        Law("Foldable Laws: Right fold consistent with foldMap") { FF.rightFoldConsistentWithFoldMap(GEN, EQ) },
        Law("Foldable Laws: Exists is consistent with find") { FF.existsConsistentWithFind(GEN) },
        Law("Foldable Laws: Exists is lazy") { FF.existsIsLazy(GEN, EQ) },
        Law("Foldable Laws: ForAll is lazy") { FF.forAllIsLazy(GEN, EQ) },
        Law("Foldable Laws: ForAll consistent with exists") { FF.forallConsistentWithExists(GEN) },
        Law("Foldable Laws: ForAll returns true if isEmpty") { FF.forallReturnsTrueIfEmpty(GEN) },
        Law("Foldable Laws: FirstOption returns None if isEmpty") { FF.firstOptionReturnsNoneIfEmpty(GEN) },
        Law("Foldable Laws: FirstOption returns None if predicate fails") { FF.firstOptionReturnsNoneIfPredicateFails(GEN) },
        Law("Foldable Laws: FoldM for Id is equivalent to fold left") { FF.foldMIdIsFoldL(GEN, EQ) }
      )
  }

  fun <F> Foldable<F>.leftFoldConsistentWithFoldMap(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.foldMap(this, f).equalUnderTheLaw(fa.foldLeft(empty()) { acc, a -> acc.combine(f(a)) }, EQ)
      }
    }

  fun <F> Foldable<F>.rightFoldConsistentWithFoldMap(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.foldMap(this, f).equalUnderTheLaw(fa.foldRight(Eval.later { empty() }) { a, lb: Eval<Int> -> lb.map { f(a).combine(it) } }.value(), EQ)
      }
    }

  fun <F> Foldable<F>.existsConsistentWithFind(G: Gen<Kind<F, Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      fa.exists(f).equalUnderTheLaw(fa.find(f).fold({ false }, { true }), Eq.any())
    }

  fun <F> Foldable<F>.existsIsLazy(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      fa.exists { _ ->
        sideEffect.increment()
        true
      }
      val expected = if (fa.isEmpty()) 0 else 1
      sideEffect.counter.equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.forAllIsLazy(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      fa.forAll { _ ->
        sideEffect.increment()
        true
      }
      val expected = if (fa.isEmpty()) 0 else fa.size(Long.monoid())
      sideEffect.counter.equalUnderTheLaw(expected.toInt(), EQ)
    }

  fun <F> Foldable<F>.forallConsistentWithExists(G: Gen<Kind<F, Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      if (fa.forAll(f)) {
        // if f is true for all elements, then there cannot be an element for which
        // it does not hold.
        val negationExists = fa.exists { a -> !(f(a)) }
        // if f is true for all elements, then either there must be no elements
        // or there must exist an element for which it is true.
        !negationExists && (fa.isEmpty() || fa.exists(f))
      } else true
    }

  fun <F> Foldable<F>.forallReturnsTrueIfEmpty(G: Gen<Kind<F, Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      !fa.isEmpty() || fa.forAll(f)
    }

  fun <F> Foldable<F>.firstOptionReturnsNoneIfEmpty(G: Gen<Kind<F, Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      if (fa.isEmpty()) fa.firstOption().isEmpty()
      else fa.firstOption().isDefined()
    }

  fun <F> Foldable<F>.firstOptionReturnsNoneIfPredicateFails(G: Gen<Kind<F, Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.firstOption { false }.isEmpty()
    }

  fun <F> Foldable<F>.foldMIdIsFoldL(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        val foldL: Int = fa.foldLeft(empty()) { acc, a -> acc.combine(f(a)) }
        val foldM: Int = fa.foldM(Id.monad(), empty()) { acc, a -> Id(acc.combine(f(a))) }.extract()
        foldM.equalUnderTheLaw(foldL, EQ)
      }
    }
}
