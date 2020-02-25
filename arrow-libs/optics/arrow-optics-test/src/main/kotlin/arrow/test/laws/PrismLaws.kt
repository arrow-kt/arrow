package arrow.test.laws

import arrow.core.Const
import arrow.core.Id
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.compose
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.id.applicative.applicative
import arrow.core.identity
import arrow.core.orElse
import arrow.core.value
import arrow.optics.Prism
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object PrismLaws {

  fun <A, B> laws(prism: Prism<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQOptionB: Eq<Option<B>>): List<Law> = listOf(
    Law("Prism law: partial round trip one way") { prism.partialRoundTripOneWay(aGen, EQA) },
    Law("Prism law: round trip other way") { prism.roundTripOtherWay(bGen, EQOptionB) },
    Law("Prism law: modify identity") { prism.modifyIdentity(aGen, EQA) },
    Law("Prism law: compose modify") { prism.composeModify(aGen, funcGen, EQA) },
    Law("Prism law: consistent set modify") { prism.consistentSetModify(aGen, bGen, EQA) },
    Law("Prism law: consistent modify with modifyF Id") { prism.consistentModifyModifyFId(aGen, funcGen, EQA) },
    Law("Prism law: consistent get option modify id") { prism.consistentGetOptionModifyId(aGen, EQOptionB) }
  )

  fun <A, B> Prism<A, B>.partialRoundTripOneWay(aGen: Gen<A>, EQA: Eq<A>): Unit =
    forAll(aGen) { a ->
      getOrModify(a).fold(::identity, ::reverseGet)
        .equalUnderTheLaw(a, EQA)
    }

  fun <A, B> Prism<A, B>.roundTripOtherWay(bGen: Gen<B>, EQOptionB: Eq<Option<B>>): Unit =
    forAll(bGen) { b ->
      getOption(reverseGet(b))
        .equalUnderTheLaw(Some(b), EQOptionB)
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

  fun <A, B> Prism<A, B>.consistentModifyModifyFId(aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
    forAll(aGen, funcGen) { a, f ->
      modifyF(Id.applicative(), a) { Id.just(f(it)) }.value().equalUnderTheLaw(modify(a, f), EQA)
    }

  fun <A, B> Prism<A, B>.consistentGetOptionModifyId(aGen: Gen<A>, EQOptionB: Eq<Option<B>>): Unit =
    forAll(aGen) { a ->
      modifyF(Const.applicative(object : Monoid<Option<B>> {
        override fun Option<B>.combine(b: Option<B>): Option<B> = orElse { b }

        override fun empty(): Option<B> = None
      }), a) { Const(Some(it)) }.value().equalUnderTheLaw(getOption(a), EQOptionB)
    }
}
