package arrow.core.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object HashLaws {

  fun <F> laws(HF: Hash<F>, G: Gen<F>, EQ: Eq<F>): List<Law> =
    listOf(
      Law("Hash Laws: Equality implies equal hash") { equalHash(HF, EQ, G) },
      Law("Hash Laws: Multiple calls to hash should result in the same hash") { equalHashM(HF, G) },
        Law("Hash Laws: Multiple calls to hashWithSalt with the same salt should result in the same hash") { equalHashWithSaltM(HF, G) }
    )

  private fun <F> equalHash(HF: Hash<F>, EQ: Eq<F>, G: Gen<F>) {
    forAll(G, G, Gen.int()) { a, b, salt ->
      if (EQ.run { a.eqv(b) })
        HF.run { a.hash() == b.hash() } && HF.run { a.hashWithSalt(salt) == b.hashWithSalt(salt) }
      else
        true
    }
  }

  private fun <F> equalHashM(HF: Hash<F>, G: Gen<F>) {
    forAll(G) { a ->
      HF.run { a.hash() == a.hash() }
    }
  }

  private fun <F> equalHashWithSaltM(HF: Hash<F>, G: Gen<F>) {
    forAll(G, Gen.int()) { a, salt ->
      HF.run { a.hashWithSalt(salt) == a.hashWithSalt(salt) }
    }
  }
}
