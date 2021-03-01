package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Iso
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object IsoLaws {

  fun <A, B> laws(
    iso: Iso<A, B>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    EQA: (A, A) -> Boolean = { a, b -> a?.equals(b) == true },
    EQB: (B, B) -> Boolean = { a, b -> a?.equals(b) == true }
  ): List<Law> =
    listOf(
      Law("Iso Law: round trip one way") { iso.roundTripOneWay(aGen, EQA) },
      Law("Iso Law: round trip other way") { iso.roundTripOtherWay(bGen, EQB) },
      Law("Iso Law: modify identity is identity") { iso.modifyIdentity(aGen, EQA) },
      Law("Iso Law: compose modify") { iso.composeModify(aGen, funcGen, EQA) },
      Law("Iso Law: consitent set with modify") { iso.consistentSetModify(aGen, bGen, EQA) }
    )

  fun <A, B> Iso<A, B>.roundTripOneWay(aGen: Gen<A>, EQA: (A, A) -> Boolean): Unit =
    forAll(aGen) { a ->
      reverseGet(get(a)).equalUnderTheLaw(a, EQA)
    }

  fun <A, B> Iso<A, B>.roundTripOtherWay(bGen: Gen<B>, EQB: (B, B) -> Boolean): Unit =
    forAll(bGen) { b ->
      get(reverseGet(b)).equalUnderTheLaw(b, EQB)
    }

  fun <A, B> Iso<A, B>.modifyIdentity(aGen: Gen<A>, EQA: (A, A) -> Boolean): Unit =
    forAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, EQA)
    }

  fun <A, B> Iso<A, B>.composeModify(aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: (A, A) -> Boolean): Unit =
    forAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
    }

  fun <A, B> Iso<A, B>.consistentSetModify(aGen: Gen<A>, bGen: Gen<B>, EQA: (A, A) -> Boolean): Unit =
    forAll(aGen, bGen) { a, b ->
      set(b).equalUnderTheLaw(modify(a) { b }, EQA)
    }
}
