package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.Option
import arrow.core.extensions.monoid
import arrow.test.generators.*
import arrow.typeclasses.Eq
import arrow.typeclasses.Reducible
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ReducibleLaws {

  fun <F> laws(RF: Reducible<F>, cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>, EQOptionInt: Eq<Option<Int>>, EQLong: Eq<Long>): List<Law> =
    FoldableLaws.laws(RF, cf, EQ) + listOf(
      Law("Reducible Laws: reduceLeftTo consistent with reduceMap") { RF.reduceLeftToConsistentWithReduceMap(cf, EQ) },
      Law("Reducible Laws: reduceRightTo consistent with reduceMap") { RF.reduceRightToConsistentWithReduceMap(cf, EQ) },
      Law("Reducible Laws: reduceRightTo consistent with reduceRightToOption") { RF.reduceRightToConsistentWithReduceRightToOption(cf, EQOptionInt) },
      Law("Reducible Laws: reduceRight consistent with reduceRightOption") { RF.reduceRightConsistentWithReduceRightOption(cf, EQOptionInt) },
      Law("Reducible Laws: reduce reduce left consistent") { RF.reduceReduceLeftConsistent(cf, EQ) },
      Law("Reducible Laws: size consistent") { RF.sizeConsistent(cf, EQLong) }
    )

  fun <F> Reducible<F>.reduceLeftToConsistentWithReduceMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.intSmall().map(cf)) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.reduceMap(this, f).equalUnderTheLaw(fa.reduceLeftTo(f) { b, a -> b.combine(f(a)) }, EQ)
      }
    }

  fun <F> Reducible<F>.reduceRightToConsistentWithReduceMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.intSmall().map(cf)) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.reduceMap(this, f).equalUnderTheLaw(fa.reduceRightTo(f) { a, eb -> eb.map { f(a).combine(it) } }.value(), EQ)
      }
    }

  fun <F> Reducible<F>.reduceRightToConsistentWithReduceRightToOption(cf: (Int) -> Kind<F, Int>, EQ: Eq<Option<Int>>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), Gen.intSmall().map(cf)) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.reduceRightToOption(f) { a, eb -> eb.map { f(a).combine(it) } }.value()
          .equalUnderTheLaw(fa.reduceRightTo(f) { a, eb -> eb.map { f(a).combine(it) } }.map { Option(it) }.value(), EQ)
      }
    }

  fun <F> Reducible<F>.reduceRightConsistentWithReduceRightOption(cf: (Int) -> Kind<F, Int>, EQ: Eq<Option<Int>>) =
    forAll(Gen.functionAAToA(Gen.intSmall()), Gen.intSmall().map(cf)) { f: (Int, Int) -> Int, fa: Kind<F, Int> ->
      fa.reduceRight { a1, e2 -> Eval.Now(f(a1, e2.value())) }.map { Option(it) }.value()
        .equalUnderTheLaw(fa.reduceRightOption { a1, e2 -> Eval.Now(f(a1, e2.value())) }.value(), EQ)
    }

  fun <F> Reducible<F>.reduceReduceLeftConsistent(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
    forAll(Gen.intSmall().map(cf)) { fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.reduce(this).equalUnderTheLaw(fa.reduceLeft { a1, a2 -> a1.combine(a2) }, EQ)
      }
    }

  fun <F> Reducible<F>.sizeConsistent(cf: (Int) -> Kind<F, Int>, EQ: Eq<Long>) =
    forAll(Gen.intSmall().map(cf)) { fa: Kind<F, Int> ->
      with(Long.monoid()) {
        fa.size(this).equalUnderTheLaw(fa.reduceMap(this) { 1L }, EQ)
      }
    }
}
