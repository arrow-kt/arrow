package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Prism
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.checkAll

public object PrismLaws {

  public fun <A, B> laws(
    prism: Prism<A, B>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    eqa: (A, A) -> Boolean = { a, b -> a == b },
    eqb: (B?, B?) -> Boolean = { a, b -> a == b }
  ): List<Law> = listOf(
    Law("Prism law: partial round trip one way") { prism.partialRoundTripOneWay(aGen, eqa) },
    Law("Prism law: round trip other way") { prism.roundTripOtherWay(bGen, eqb) },
    Law("Prism law: modify identity") { prism.modifyIdentity(aGen, eqa) },
    Law("Prism law: compose modify") { prism.composeModify(aGen, funcGen, eqa) },
    Law("Prism law: consistent set modify") { prism.consistentSetModify(aGen, bGen, eqa) }
  )

  public suspend fun <A, B> Prism<A, B>.partialRoundTripOneWay(aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(aGen) { a ->
      getOrModify(a).fold(::identity, ::reverseGet)
        .equalUnderTheLaw(a, eq)
    }

  public suspend fun <A, B> Prism<A, B>.roundTripOtherWay(bGen: Arb<B>, eq: (B?, B?) -> Boolean): PropertyContext =
    checkAll(bGen) { b ->
      getOrNull(reverseGet(b))
        .equalUnderTheLaw(b, eq)
    }

  public suspend fun <A, B> Prism<A, B>.modifyIdentity(aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, eq)
    }

  public suspend fun <A, B> Prism<A, B>.composeModify(aGen: Arb<A>, funcGen: Arb<(B) -> B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), eq)
    }

  public suspend fun <A, B> Prism<A, B>.consistentSetModify(aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(aGen, bGen) { a, b ->
      set(a, b).equalUnderTheLaw(modify(a) { b }, eq)
    }
}
