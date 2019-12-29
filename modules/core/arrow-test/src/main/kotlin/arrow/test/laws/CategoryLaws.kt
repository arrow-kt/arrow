package arrow.test.laws

import arrow.Kind2
import arrow.core.extensions.eq
import arrow.test.generators.GenK2
import arrow.typeclasses.Category
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK2
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object CategoryLaws {

  fun <F> laws(C: Category<F>, GENK: GenK2<F>, EQK: EqK2<F>): List<Law> {
    val G = GENK.genK(Gen.int(), Gen.int())
    val EQ = EQK.liftEq(Int.eq(), Int.eq())

    return categoryLaws<F>(C, G, EQ)
  }

  fun <F> laws(C: Category<F>, f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): List<Law> {

    val G = Gen.int().map(f)

    return categoryLaws<F>(C, G, EQ)
  }

  private fun <F> categoryLaws(C: Category<F>, G: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): List<Law> =
    listOf(
      Law("Category Laws: right identity") { C.rightIdentity(G, EQ) },
      Law("Category Laws: left identity") { C.leftIdentity(G, EQ) },
      Law("Category Laws: associativity") { C.associativity(G, EQ) }
    )

  fun <F> Category<F>.rightIdentity(G: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(G) { fa: Kind2<F, Int, Int> ->
      fa.compose(id()).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Category<F>.leftIdentity(G: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(G) { fa: Kind2<F, Int, Int> ->
      id<Int>().compose(fa).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Category<F>.associativity(G: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(G, G, G) { a, b, c ->
      a.compose(b).compose(c).equalUnderTheLaw(a.compose(b.compose(c)), EQ)
    }
}
