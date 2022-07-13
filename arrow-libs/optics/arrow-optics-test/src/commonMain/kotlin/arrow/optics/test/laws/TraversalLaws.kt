package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.core.test.concurrency.deprecateArrowTestModules
import arrow.optics.Traversal
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.checkAll
import kotlin.math.max

@Deprecated(deprecateArrowTestModules)
public object TraversalLaws {

  @Deprecated(deprecateArrowTestModules)
  public fun <A, B : Any> laws(
    traversal: Traversal<A, B>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    eq: (A, A) -> Boolean = { a, b -> a == b }
  ): List<Law> = listOf(
    Law("Traversal law: set is idempotent") { traversal.setIdempotent(aGen, bGen, eq) },
    Law("Traversal law: modify identity") { traversal.modifyIdentity(aGen, eq) },
    Law("Traversal law: compose modify") { traversal.composeModify(aGen, funcGen, eq) }
  )

  @Deprecated(deprecateArrowTestModules)
  public suspend fun <A, B> Traversal<A, B>.setIdempotent(aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(max(aGen.minIterations(), bGen.minIterations()), aGen, bGen) { a, b ->
      set(set(a, b), b)
        .equalUnderTheLaw(set(a, b), eq)
    }

  @Deprecated(deprecateArrowTestModules)
  public suspend fun <A, B> Traversal<A, B>.modifyIdentity(aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(aGen.minIterations(), aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, eq)
    }

  @Deprecated(deprecateArrowTestModules)
  public suspend fun <A, B> Traversal<A, B>.composeModify(
    aGen: Arb<A>,
    funcGen: Arb<(B) -> B>,
    eq: (A, A) -> Boolean
  ): PropertyContext =
    checkAll(
      max(max(aGen.minIterations(), funcGen.minIterations()), funcGen.minIterations()),
      aGen,
      funcGen,
      funcGen
    ) { a, f, g ->
      modify(modify(a, f), g)
        .equalUnderTheLaw(modify(a, g compose f), eq)
    }
}
