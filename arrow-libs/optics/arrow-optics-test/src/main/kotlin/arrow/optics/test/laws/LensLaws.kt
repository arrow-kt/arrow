package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Lens
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object LensLaws {

  fun <A, B> laws(
    lensGen: Gen<Lens<A, B>>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    EQA: (A, A) -> Boolean = { a, b -> a?.equals(b) == true },
    EQB: (B, B) -> Boolean = { a, b -> a?.equals(b) == true }
  ): List<Law> =
    listOf(
      Law("Lens law: get set") { lensGetSet(lensGen, aGen, EQA) },
      Law("Lens law: set get") { lensSetGet(lensGen, aGen, bGen, EQB) },
      Law("Lens law: is set idempotent") { lensSetIdempotent(lensGen, aGen, bGen, EQA) },
      Law("Lens law: modify identity") { lensModifyIdentity(lensGen, aGen, EQA) },
      Law("Lens law: compose modify") { lensComposeModify(lensGen, aGen, funcGen, EQA) },
      Law("Lens law: consistent set modify") { lensConsistentSetModify(lensGen, aGen, bGen, EQA) }
    )

  /**
   * Warning: Use only when a `Gen.constant()` applies
   */
  fun <A, B> laws(
    lens: Lens<A, B>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    EQA: (A, A) -> Boolean = { a, b -> a?.equals(b) == true },
    EQB: (B, B) -> Boolean = { a, b -> a?.equals(b) == true }
  ): List<Law> = laws(Gen.constant(lens), aGen, bGen, funcGen, EQA, EQB)

  fun <A, B> lensGetSet(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, EQA: (A, A) -> Boolean) =
    forAll(lensGen, aGen) { lens, a ->
      lens.run {
        set(a, get(a)).equalUnderTheLaw(a, EQA)
      }
    }

  fun <A, B> lensSetGet(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, bGen: Gen<B>, EQB: (B, B) -> Boolean) =
    forAll(lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        get(set(a, b)).equalUnderTheLaw(b, EQB)
      }
    }

  fun <A, B> lensSetIdempotent(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, bGen: Gen<B>, EQA: (A, A) -> Boolean) =
    forAll(lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        set(set(a, b), b).equalUnderTheLaw(set(a, b), EQA)
      }
    }

  fun <A, B> lensModifyIdentity(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, EQA: (A, A) -> Boolean) =
    forAll(lensGen, aGen) { lens, a ->
      lens.run {
        modify(a, ::identity).equalUnderTheLaw(a, EQA)
      }
    }

  fun <A, B> lensComposeModify(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: (A, A) -> Boolean) =
    forAll(lensGen, aGen, funcGen, funcGen) { lens, a, f, g ->
      lens.run {
        modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
      }
    }

  fun <A, B> lensConsistentSetModify(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, bGen: Gen<B>, EQA: (A, A) -> Boolean) =
    forAll(lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        set(a, b).equalUnderTheLaw(modify(a) { b }, EQA)
      }
    }
}
