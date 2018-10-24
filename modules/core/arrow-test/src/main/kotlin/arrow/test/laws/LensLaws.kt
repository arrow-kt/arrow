package arrow.test.laws

import arrow.core.*
import arrow.instances.const.applicative.applicative
import arrow.instances.id.functor.functor
import arrow.typeclasses.*
import arrow.optics.Lens
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object LensLaws {

  fun <A, B> laws(lens: Lens<A, B>, aGen: Gen<A>, bGen: Gen<B>, funcGen: Gen<(B) -> B>, EQA: Eq<A>, EQB: Eq<B>, MB: Monoid<B>) =
    listOf(
      Law("Lens law: get set") { lens.lensGetSet(aGen, EQA) },
      Law("Lens law: set get") { lens.lensSetGet(aGen, bGen, EQB) },
      Law("Lens law: is set idempotent") { lens.lensSetIdempotent(aGen, bGen, EQA) },
      Law("Lens law: modify identity") { lens.lensModifyIdentity(aGen, EQA) },
      Law("Lens law: compose modify") { lens.lensComposeModify(aGen, funcGen, EQA) },
      Law("Lens law: consistent set modify") { lens.lensConsistentSetModify(aGen, bGen, EQA) },
      Law("Lens law: consistent modify modify id") { lens.lensConsistentModifyModifyId(aGen, funcGen, EQA) },
      Law("Lens law: consistent get modify id") { lens.lensConsistentGetModifyid(aGen, EQB, MB) }
    )

  fun <A, B> Lens<A, B>.lensGetSet(aGen: Gen<A>, EQA: Eq<A>) =
    forAll(aGen) { a ->
      set(a, get(a)).equalUnderTheLaw(a, EQA)
    }

  fun <A, B> Lens<A, B>.lensSetGet(aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B>) =
    forAll(aGen, bGen) { a, b ->
      get(set(a, b)).equalUnderTheLaw(b, EQB)
    }

  fun <A, B> Lens<A, B>.lensSetIdempotent(aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
    forAll(aGen, bGen) { a, b ->
      set(set(a, b), b).equalUnderTheLaw(set(a, b), EQA)
    }

  fun <A, B> Lens<A, B>.lensModifyIdentity(aGen: Gen<A>, EQA: Eq<A>) =
    forAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, EQA)
    }

  fun <A, B> Lens<A, B>.lensComposeModify(aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>) =
    forAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
    }

  fun <A, B> Lens<A, B>.lensConsistentSetModify(aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
    forAll(aGen, bGen) { a, b ->
      set(a, b).equalUnderTheLaw(modify(a) { b }, EQA)
    }

  fun <A, B> Lens<A, B>.lensConsistentModifyModifyId(aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>) =
    forAll(aGen, funcGen) { a, f ->
      modify(a, f)
        .equalUnderTheLaw(modifyF(Id.functor(), a) { Id.just(f(it)) }.value(), EQA)
    }

  fun <A, B> Lens<A, B>.lensConsistentGetModifyid(aGen: Gen<A>, EQB: Eq<B>, MA: Monoid<B>) =
    forAll(aGen) { a ->
      get(a)
        .equalUnderTheLaw(modifyF(Const.applicative(MA), a, ::Const).value(), EQB)
    }

}