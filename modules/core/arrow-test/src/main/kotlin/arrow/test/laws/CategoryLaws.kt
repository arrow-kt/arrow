package arrow.test.laws

import arrow.Kind2
import arrow.test.generators.genDoubleConstructor
import arrow.typeclasses.Category
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object CategoryLaws {

  fun <F> laws(C: Category<F>, f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): List<Law> =
    listOf(
      Law("Category Laws: right identity") { C.rightIdentity(f, EQ) },
      Law("Category Laws: left identity") { C.leftIdentity(f, EQ) },
      Law("Category Laws: associativity") { C.associativity(f, EQ) }
    )

  fun <F> Category<F>.rightIdentity(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(genDoubleConstructor(Gen.int(), f)) { fa: Kind2<F, Int, Int> ->
      fa.compose(id()).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Category<F>.leftIdentity(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(genDoubleConstructor(Gen.int(), f)) { fa: Kind2<F, Int, Int> ->
      id<Int>().compose(fa).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Category<F>.associativity(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(genDoubleConstructor(Gen.int(), f), genDoubleConstructor(Gen.int(), f), genDoubleConstructor(Gen.int(), f)) { a, b, c ->
      a.compose(b).compose(c).equalUnderTheLaw(a.compose(b.compose(c)), EQ)
    }
}