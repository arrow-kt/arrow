package arrow.optics.test.laws

import arrow.core.identity
import arrow.optics.Iso
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.checkAll

data class IsoLaws<A, B>(
  val iso: Iso<A, B>,
  val aGen: Arb<A>,
  val bGen: Arb<B>,
  val funcGen: Arb<(B) -> B>,
  val eqa: (A, A) -> Boolean = { a, b -> a == b },
  val eqb: (B, B) -> Boolean = { a, b -> a == b }
): LawSet {

  override val laws: List<Law> =
    listOf(
      Law("Iso Law: round trip one way") { iso.roundTripOneWay(aGen, eqa) },
      Law("Iso Law: round trip other way") { iso.roundTripOtherWay(bGen, eqb) },
      Law("Iso Law: modify identity is identity") { iso.modifyIdentity(aGen, eqa) },
      Law("Iso Law: compose modify") { iso.composeModify(aGen, funcGen, eqa) },
      Law("Iso Law: consitent set with modify") { iso.consistentSetModify(aGen, bGen, eqa) }
    )

  private suspend fun <A, B> Iso<A, B>.roundTripOneWay(aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen) { a ->
      reverseGet(get(a)).equalUnderTheLaw(a, eq)
    }

  private suspend fun <A, B> Iso<A, B>.roundTripOtherWay(bGen: Arb<B>, eq: (B, B) -> Boolean): PropertyContext =
    checkAll(100, bGen) { b ->
      get(reverseGet(b)).equalUnderTheLaw(b, eq)
    }

  private suspend fun <A, B> Iso<A, B>.modifyIdentity(aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, eq)
    }

  private suspend fun <A, B> Iso<A, B>.composeModify(aGen: Arb<A>, funcGen: Arb<(B) -> B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a) { g(f(it)) }, eq)
    }

  private suspend fun <A, B> Iso<A, B>.consistentSetModify(aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen, bGen) { a, b ->
      set(b).equalUnderTheLaw(modify(a) { b }, eq)
    }
}
