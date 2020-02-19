package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object HashLaws {

  fun <F> laws(HF: Hash<F>, G: Gen<F>, EQ: Eq<F>): List<Law> =
    EqLaws.laws(EQ, G) + listOf(
      Law("Hash Laws: Equality implies equal hash") { equalHash(HF, EQ, G) },
      Law("Hash Laws: Multiple calls to hash should result in the same hash") { equalHashM(HF, G) }
    )

  private fun <F> equalHash(HF: Hash<F>, EQ: Eq<F>, G: Gen<F>) {
    forAll(G, G) { a, b ->
      if (EQ.run { a.eqv(b) })
        HF.run { a.hash() == b.hash() }
      else
        true
    }
  }

  private fun <F> equalHashM(HF: Hash<F>, G: Gen<F>) {
    forAll(G) { a ->
      HF.run { a.hash() == a.hash() }
    }
  }
}
