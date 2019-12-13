package arrow.test.laws

import arrow.Kind
import arrow.core.compose
import arrow.core.identity
import arrow.test.generators.functionAToB
import arrow.typeclasses.Eq
import arrow.typeclasses.Invariant
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object InvariantLaws {

  fun <F> laws(IF: Invariant<F>, G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): List<Law> =
    listOf(
      Law("Invariant Laws: Invariant Identity") { IF.identity(G, EQ) },
      Law("Invariant Laws: Invariant Composition") { IF.composition(G, EQ) }
    )

  fun <F> Invariant<F>.identity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      val imap: Kind<F, Int> = fa.imap<Int, Int>(::identity, ::identity)
      imap.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Invariant<F>.composition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G,
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, f1, f2, g1, g2 ->
      fa.imap(f1, f2).imap(g1, g2).equalUnderTheLaw(fa.imap(g1 compose f1, f2 compose g2), EQ)
    }
}
