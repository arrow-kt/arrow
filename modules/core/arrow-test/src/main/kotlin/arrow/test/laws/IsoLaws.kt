package arrow.test.laws

import arrow.core.Const
import arrow.core.Id
import arrow.core.compose
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.id.functor.functor
import arrow.core.identity
import arrow.core.value
import arrow.optics.Iso
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object IsoLaws {

  fun <A, B> laws(iso: Iso<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, bMonoid: Monoid<B>): List<Law> =
    listOf(
      Law("Iso Law: round trip one way") { iso.roundTripOneWay(aGen, EQA) },
      Law("Iso Law: round trip other way") { iso.roundTripOtherWay(bGen, EQB) },
      Law("Iso Law: modify identity is identity") { iso.modifyIdentity(aGen, EQA) },
      Law("Iso Law: compose modify") { iso.composeModify(aGen, funcGen, EQA) },
      Law("Iso Law: consitent set with modify") { iso.consistentSetModify(aGen, bGen, EQA) },
      Law("Iso Law: consistent modify with modify identity") { iso.consistentModifyModifyId(aGen, funcGen, EQA) },
      Law("Iso Law: consitent get with modify identity") { iso.consitentGetModifyId(aGen, EQB, bMonoid) }
    )

  fun <A, B> Iso<A, B>.roundTripOneWay(aGen: Gen<A>, EQA: Eq<A>): Unit =
    forAll(aGen) { a ->
      reverseGet(get(a)).equalUnderTheLaw(a, EQA)
    }

  fun <A, B> Iso<A, B>.roundTripOtherWay(bGen: Gen<B>, EQB: Eq<B>): Unit =
    forAll(bGen) { b ->
      get(reverseGet(b)).equalUnderTheLaw(b, EQB)
    }

  fun <A, B> Iso<A, B>.modifyIdentity(aGen: Gen<A>, EQA: Eq<A>): Unit =
    forAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, EQA)
    }

  fun <A, B> Iso<A, B>.composeModify(aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
    forAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
    }

  fun <A, B> Iso<A, B>.consistentSetModify(aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
    forAll(aGen, bGen) { a, b ->
      set(b).equalUnderTheLaw(modify(a) { b }, EQA)
    }

  fun <A, B> Iso<A, B>.consistentModifyModifyId(aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
    forAll(aGen, funcGen) { a, f ->
      modify(a, f).equalUnderTheLaw(modifyF(Id.functor(), a) { Id.just(f(it)) }.value(), EQA)
    }

  fun <A, B> Iso<A, B>.consitentGetModifyId(aGen: Gen<A>, EQB: Eq<B>, bMonoid: Monoid<B>): Unit =
    forAll(aGen) { a ->
      get(a).equalUnderTheLaw(modifyF(Const.applicative(bMonoid), a, ::Const).value(), EQB)
    }
}
