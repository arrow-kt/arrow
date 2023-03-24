package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Setter
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.checkAll

data class SetterLaws<A, B>(
  val setter: Setter<A, B>,
  val aGen: Arb<A>,
  val bGen: Arb<B>,
  val funcGen: Arb<(B) -> B>,
  val eq: (A, A) -> Boolean = { a, b -> a == b }
): LawSet {

  override val laws: List<Law> = listOf(
    Law("Setter law: set is idempotent") { setter.setIdempotent(aGen, bGen, eq) },
    Law("Setter law: modify identity") { setter.modifyIdentity(aGen, eq) },
    Law("Setter law: compose modify") { setter.composeModify(aGen, eq, funcGen) },
    Law("Setter law: consistent set modify") { setter.consistentSetModify(aGen, bGen, eq) }
  )

  private suspend fun <A, B> Setter<A, B>.setIdempotent(aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen, bGen) { a, b ->
      set(set(a, b), b).equalUnderTheLaw(set(a, b), eq)
    }

  private suspend fun <A, B> Setter<A, B>.modifyIdentity(aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen) { a ->
      modify(a, ::identity).equalUnderTheLaw(a, eq)
    }

  private suspend fun <A, B> Setter<A, B>.composeModify(aGen: Arb<A>, eq: (A, A) -> Boolean, funcGen: Arb<(B) -> B>): PropertyContext =
    checkAll(100, aGen, funcGen, funcGen) { a, f, g ->
      modify(modify(a, f), g).equalUnderTheLaw(modify(a, g compose f), eq)
    }

  private suspend fun <A, B> Setter<A, B>.consistentSetModify(aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, aGen, bGen) { a, b ->
      modify(a) { b }.equalUnderTheLaw(set(a, b), eq)
    }
}
