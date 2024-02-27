package arrow.optics.test.laws

import arrow.core.identity
import arrow.optics.Prism
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.checkAll

data class PrismLaws<A, B>(
  val prism: Prism<A, B>,
  val aGen: Arb<A>,
  val bGen: Arb<B>,
  val funcGen: Arb<(B) -> B>,
  val eqa: (A, A) -> Boolean = { a, b -> a == b },
  val eqb: (B?, B?) -> Boolean = { a, b -> a == b }
): LawSet {

  override val laws: List<Law> = listOf(
    Law("Prism law: partial round trip one way") { prism.partialRoundTripOneWay(aGen, eqa) },
    Law("Prism law: round trip other way") { prism.roundTripOtherWay(bGen, eqb) },
    Law("Prism law: modify identity") { prism.modifyIdentity(aGen, eqa) },
    Law("Prism law: compose modify") { prism.composeModify(aGen, funcGen, eqa) },
    Law("Prism law: consistent set modify") { prism.consistentSetModify(aGen, bGen, eqa) }
  )

  private suspend fun <A, B> Prism<A, B>.partialRoundTripOneWay(aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen) { a ->
      getOrModify(a).fold(::identity, ::reverseGet)
        .equalUnderTheLaw(a, eq)
    }

  private suspend fun <A, B> Prism<A, B>.roundTripOtherWay(bGen: Arb<B>, eq: (B?, B?) -> Boolean): PropertyContext =
    checkAll(100, bGen) { b ->
      getOrNull(reverseGet(b))
        .equalUnderTheLaw(b, eq)
    }

  private suspend fun <A, B> Prism<A, B>.modifyIdentity(aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, eq)
    }

  private suspend fun <A, B> Prism<A, B>.composeModify(aGen: Arb<A>, funcGen: Arb<(B) -> B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a) { g(f(it)) }, eq)
    }

  private suspend fun <A, B> Prism<A, B>.consistentSetModify(aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen, bGen) { a, b ->
      set(a, b).equalUnderTheLaw(modify(a) { b }, eq)
    }
}
