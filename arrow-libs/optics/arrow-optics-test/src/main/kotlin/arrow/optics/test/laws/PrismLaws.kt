package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Prism
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object PrismLaws {

  fun <A, B> laws(
    prism: Prism<A, B>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    eqa: (A, A) -> Boolean = { a, b -> a == b },
    eqb: (B?, B?) -> Boolean = { a, b -> a == b }
  ): List<Law> = listOf(
    Law("Prism law: partial round trip one way") { prism.partialRoundTripOneWay(aGen, eqa) },
    Law("Prism law: round trip other way") { prism.roundTripOtherWay(bGen, eqb) },
    Law("Prism law: modify identity") { prism.modifyIdentity(aGen, eqa) },
    Law("Prism law: compose modify") { prism.composeModify(aGen, funcGen, eqa) },
    Law("Prism law: consistent set modify") { prism.consistentSetModify(aGen, bGen, eqa) }
  )

  fun <A, B> Prism<A, B>.partialRoundTripOneWay(aGen: Gen<A>, eq: (A, A) -> Boolean): Unit =
    forAll(aGen) { a ->
      getOrModify(a).fold(::identity, ::reverseGet)
        .equalUnderTheLaw(a, eq)
    }

  fun <A, B> Prism<A, B>.roundTripOtherWay(bGen: Gen<B>, eq: (B?, B?) -> Boolean): Unit =
    forAll(bGen) { b ->
      getOrNull(reverseGet(b))
        .equalUnderTheLaw(b, eq)
    }

  fun <A, B> Prism<A, B>.modifyIdentity(aGen: Gen<A>, eq: (A, A) -> Boolean): Unit =
    forAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, eq)
    }

  fun <A, B> Prism<A, B>.composeModify(aGen: Gen<A>, funcGen: Gen<(B) -> B>, eq: (A, A) -> Boolean): Unit =
    forAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), eq)
    }

  fun <A, B> Prism<A, B>.consistentSetModify(aGen: Gen<A>, bGen: Gen<B>, eq: (A, A) -> Boolean): Unit =
    forAll(aGen, bGen) { a, b ->
      set(a, b).equalUnderTheLaw(modify(a) { b }, eq)
    }
}
