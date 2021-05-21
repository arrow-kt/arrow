package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Iso
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotest.property.Arb
import io.kotest.property.checkAll

object IsoLaws {

  fun <A, B> laws(
    iso: Iso<A, B>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    eqa: (A, A) -> Boolean = { a, b -> a == b },
    eqb: (B, B) -> Boolean = { a, b -> a == b }
  ): List<Law> =
    listOf(
      Law("Iso Law: round trip one way") { iso.roundTripOneWay(aGen, eqa) },
      Law("Iso Law: round trip other way") { iso.roundTripOtherWay(bGen, eqb) },
      Law("Iso Law: modify identity is identity") { iso.modifyIdentity(aGen, eqa) },
      Law("Iso Law: compose modify") { iso.composeModify(aGen, funcGen, eqa) },
      Law("Iso Law: consitent set with modify") { iso.consistentSetModify(aGen, bGen, eqa) }
    )

  fun <A, B> Iso<A, B>.roundTripOneWay(aGen: Arb<A>, eq: (A, A) -> Boolean): Unit =
    checkAll(aGen) { a ->
      reverseGet(get(a)).equalUnderTheLaw(a, eq)
    }

  fun <A, B> Iso<A, B>.roundTripOtherWay(bGen: Arb<B>, eq: (B, B) -> Boolean): Unit =
    checkAll(bGen) { b ->
      get(reverseGet(b)).equalUnderTheLaw(b, eq)
    }

  fun <A, B> Iso<A, B>.modifyIdentity(aGen: Arb<A>, eq: (A, A) -> Boolean): Unit =
    checkAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, eq)
    }

  fun <A, B> Iso<A, B>.composeModify(aGen: Arb<A>, funcGen: Arb<(B) -> B>, eq: (A, A) -> Boolean): Unit =
    checkAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), eq)
    }

  fun <A, B> Iso<A, B>.consistentSetModify(aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): Unit =
    checkAll(aGen, bGen) { a, b ->
      set(b).equalUnderTheLaw(modify(a) { b }, eq)
    }
}
