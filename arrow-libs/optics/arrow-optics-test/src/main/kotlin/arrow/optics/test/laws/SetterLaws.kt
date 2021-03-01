package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Setter
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SetterLaws {

  fun <A, B> laws(
    setter: Setter<A, B>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    EQA: (A, A) -> Boolean = { a, b -> a?.equals(b) == true }
  ) = listOf(
    Law("Setter law: set is idempotent") { setter.setIdempotent(aGen, bGen, EQA) },
    Law("Setter law: modify identity") { setter.modifyIdentity(aGen, EQA) },
    Law("Setter law: compose modify") { setter.composeModify(aGen, EQA, funcGen) },
    Law("Setter law: consistent set modify") { setter.consistentSetModify(aGen, bGen, EQA) }
  )

  fun <A, B> Setter<A, B>.setIdempotent(aGen: Gen<A>, bGen: Gen<B>, EQA: (A, A) -> Boolean): Unit = forAll(aGen, bGen) { a, b ->
    set(set(a, b), b).equalUnderTheLaw(set(a, b), EQA)
  }

  fun <A, B> Setter<A, B>.modifyIdentity(aGen: Gen<A>, EQA: (A, A) -> Boolean): Unit = forAll(aGen) { a ->
    modify(a, ::identity).equalUnderTheLaw(a, EQA)
  }

  fun <A, B> Setter<A, B>.composeModify(aGen: Gen<A>, EQA: (A, A) -> Boolean, funcGen: Gen<(B) -> B>): Unit = forAll(aGen, funcGen, funcGen) { a, f, g ->
    modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), EQA)
  }

  fun <A, B> Setter<A, B>.consistentSetModify(aGen: Gen<A>, bGen: Gen<B>, EQA: (A, A) -> Boolean): Unit = forAll(aGen, bGen) { a, b ->
    modify(a) { b }.equalUnderTheLaw(set(a, b), EQA)
  }
}
