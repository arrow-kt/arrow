package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object HashLaws {

  fun <F> laws(HF: Hash<F>, EQ: Eq<F>, cf: (Int) -> F): List<Law> =
    EqLaws.laws(EQ, cf) + listOf(
      Law("Hash Laws: Equality implies equal hash") { equalHash(HF, EQ, cf) },
      Law("Hash Laws: Multiple calls to hash should result in the same hash") { equalHashM(HF, cf) }
    )

  fun <F> equalHash(HF: Hash<F>, EQ: Eq<F>, cf: (Int) -> F) {
    forAll(Gen.int()) { f ->
      val a = cf(f)
      val b = cf(f)
      EQ.run { a.eqv(b) } && HF.run { a.hash() == b.hash() }
    }
  }

  fun <F> equalHashM(HF: Hash<F>, cf: (Int) -> F) {
    forAll(Gen.int()) { f ->
      val a = cf(f)
      HF.run { a.hash() == a.hash() }
    }
  }
}
