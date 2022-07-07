package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.core.test.concurrency.deprecateArrowTestModules
import arrow.optics.Setter
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.checkAll

@Deprecated(deprecateArrowTestModules)
public object SetterLaws {

  @Deprecated(deprecateArrowTestModules)
  public fun <A, B> laws(
    setter: Setter<A, B>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    eq: (A, A) -> Boolean = { a, b -> a == b }
  ): List<Law> = listOf(
    Law("Setter law: set is idempotent") { setter.setIdempotent(aGen, bGen, eq) },
    Law("Setter law: modify identity") { setter.modifyIdentity(aGen, eq) },
    Law("Setter law: compose modify") { setter.composeModify(aGen, eq, funcGen) },
    Law("Setter law: consistent set modify") { setter.consistentSetModify(aGen, bGen, eq) }
  )

  @Deprecated(deprecateArrowTestModules)
  public suspend fun <A, B> Setter<A, B>.setIdempotent(aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen, bGen) { a, b ->
      set(set(a, b), b).equalUnderTheLaw(set(a, b), eq)
    }

  @Deprecated(deprecateArrowTestModules)
  public suspend fun <A, B> Setter<A, B>.modifyIdentity(aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, eq)
    }

  @Deprecated(deprecateArrowTestModules)
  public suspend fun <A, B> Setter<A, B>.composeModify(aGen: Arb<A>, eq: (A, A) -> Boolean, funcGen: Arb<(B) -> B>): PropertyContext =
    checkAll(100, aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), eq)
    }

  @Deprecated(deprecateArrowTestModules)
  public suspend fun <A, B> Setter<A, B>.consistentSetModify(aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen, bGen) { a, b ->
      modify(a) { b }.equalUnderTheLaw(set(a, b), eq)
    }
}
