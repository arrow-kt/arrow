package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Traversal
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object TraversalLaws {

  fun <A, B : Any> laws(
    traversal: Traversal<A, B>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    eq: (A, A) -> Boolean = { a, b -> a == b }
  ) = listOf(
    Law("Traversal law: set is idempotent") { traversal.setIdempotent(aGen, bGen, eq) },
    Law("Traversal law: modify identity") { traversal.modifyIdentity(aGen, eq) },
    Law("Traversal law: compose modify") { traversal.composeModify(aGen, funcGen, eq) }
  )

  fun <A, B> Traversal<A, B>.setIdempotent(aGen: Gen<A>, bGen: Gen<B>, eq: (A, A) -> Boolean): Unit =
    forAll(aGen, bGen) { a, b ->
      set(set(a, b), b)
        .equalUnderTheLaw(set(a, b), eq)
    }

  fun <A, B> Traversal<A, B>.modifyIdentity(aGen: Gen<A>, eq: (A, A) -> Boolean): Unit =
    forAll(aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, eq)
    }

  fun <A, B> Traversal<A, B>.composeModify(aGen: Gen<A>, funcGen: Gen<(B) -> B>, eq: (A, A) -> Boolean): Unit =
    forAll(aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g)
        .equalUnderTheLaw(modify(a, g compose f), eq)
    }
}
