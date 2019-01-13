package arrow.test.laws

import arrow.core.*
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.id.functor.functor
import arrow.typeclasses.*
import arrow.optics.Lens
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object LensLaws {

  fun <A, B> laws(
    lensGen: Gen<Lens<A, B>>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    EQA: Eq<A>,
    EQB: Eq<B>,
    MB: Monoid<B>
  ) =
    listOf(
      Law("Lens law: get set") { lensGetSet(lensGen, aGen, EQA) },
      Law("Lens law: set get") { lensSetGet(lensGen, aGen, bGen, EQB) },
      Law("Lens law: is set idempotent") { lensSetIdempotent(lensGen, aGen, bGen, EQA) },
      Law("Lens law: modify identity") { lensModifyIdentity(lensGen, aGen, EQA) },
      Law("Lens law: compose modify") { lensComposeModify(lensGen, aGen, funcGen, EQA) },
      Law("Lens law: consistent set modify") { lensConsistentSetModify(lensGen, aGen, bGen, EQA) },
      Law("Lens law: consistent modify modify id") { lensConsistentModifyModifyId(lensGen, aGen, funcGen, EQA) },
      Law("Lens law: consistent get modify id") { lensConsistentGetModifyid(lensGen, aGen, EQB, MB) }
    )

  fun <A, B> lensGetSet(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, EQA: Eq<A>) =
    forAll(lensGen, aGen) { lens, a ->
      lens.run {
        set(a, get(a)).equalUnderTheLaw(a, EQA)
      }
    }

  fun <A, B> lensSetGet(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, bGen: Gen<B>, EQB: Eq<B>) =
    forAll(lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        get(set(a, b)).equalUnderTheLaw(b, EQB)
      }
    }

  fun <A, B> lensSetIdempotent(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
    forAll(lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        set(set(a, b), b).equalUnderTheLaw(set(a, b), EQA)
      }
    }

  fun <A, B> lensModifyIdentity(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, EQA: Eq<A>) =
    forAll(lensGen, aGen) { lens, a ->
      lens.run {
        modify(a, ::identity).equalUnderTheLaw(a, EQA)
      }
    }

  fun <A, B> lensComposeModify(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>) =
    forAll(lensGen, aGen, funcGen, funcGen) { lens, a, f, g ->
      lens.run {
        modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
      }
    }

  fun <A, B> lensConsistentSetModify(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>) =
    forAll(lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        set(a, b).equalUnderTheLaw(modify(a) { b }, EQA)
      }
    }

  fun <A, B> lensConsistentModifyModifyId(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>) =
    forAll(lensGen, aGen, funcGen) { lens, a, f ->
      lens.run {
        modify(a, f)
          .equalUnderTheLaw(modifyF(Id.functor(), a) { Id.just(f(it)) }.value(), EQA)
      }
    }

  fun <A, B> lensConsistentGetModifyid(lensGen: Gen<Lens<A, B>>, aGen: Gen<A>, EQB: Eq<B>, MA: Monoid<B>) =
    forAll(lensGen, aGen) { lens, a ->
      lens.run {
        get(a)
          .equalUnderTheLaw(modifyF(Const.applicative(MA), a, ::Const).value(), EQB)
      }
    }

}