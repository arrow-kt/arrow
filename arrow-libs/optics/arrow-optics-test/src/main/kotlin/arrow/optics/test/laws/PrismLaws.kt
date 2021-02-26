package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Prism
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object PrismLaws {

  fun <A, B> laws(prism: Prism<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): List<Law> = listOf(
    Law("Prism law: partial round trip one way") { prism.partialRoundTripOneWay(aGen, EQA) },
    Law("Prism law: round trip other way") { prism.roundTripOtherWay(bGen) },
    Law("Prism law: modify identity") { prism.modifyIdentity(aGen, EQA) },
    Law("Prism law: compose modify") { prism.composeModify(aGen, funcGen, EQA) },
    Law("Prism law: consistent set modify") { prism.consistentSetModify(aGen, bGen, EQA) }
  )

  fun <A, B> Prism<A, B>.partialRoundTripOneWay(aGen: Gen<A>, EQA: Eq<A>): Unit =
    forAll(aGen) { a ->
      getOrModify(a).fold(::identity, ::reverseGet)
        .equalUnderTheLaw(a, EQA)
    }

  fun <A, B> Prism<A, B>.roundTripOtherWay(bGen: Gen<B>): Unit =
    forAll(bGen) { b ->
      getOrNull(reverseGet(b))
        .equalUnderTheLaw(b, Eq.any())
    }

  fun <A, B> Prism<A, B>.modifyIdentity(aGen: Gen<A>, EQA: Eq<A>): Unit =
    forAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, EQA)
    }

  fun <A, B> Prism<A, B>.composeModify(aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
    forAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
    }

  fun <A, B> Prism<A, B>.consistentSetModify(aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
    forAll(aGen, bGen) { a, b ->
      set(a, b).equalUnderTheLaw(modify(a) { b }, EQA)
    }
}
