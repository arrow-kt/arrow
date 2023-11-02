package arrow.optics.test.laws

import arrow.core.identity
import arrow.optics.Lens
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.constant
import io.kotest.property.checkAll

data class LensLaws<A, B>(
  val lensGen: Arb<Lens<A, B>>,
  val aGen: Arb<A>,
  val bGen: Arb<B>,
  val funcGen: Arb<(B) -> B>,
  val eqa: (A, A) -> Boolean = { a, b -> a == b },
  val eqb: (B, B) -> Boolean = { a, b -> a == b }
): LawSet {

  constructor(
    lens: Lens<A, B>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    eqa: (A, A) -> Boolean = { a, b -> a == b },
    eqb: (B, B) -> Boolean = { a, b -> a == b }
  ): this(Arb.constant(lens), aGen, bGen, funcGen, eqa, eqb)

  override val laws: List<Law> =
    listOf(
      Law("Lens law: get set") { lensGetSet(lensGen, aGen, eqa) },
      Law("Lens law: set get") { lensSetGet(lensGen, aGen, bGen, eqb) },
      Law("Lens law: is set idempotent") { lensSetIdempotent(lensGen, aGen, bGen, eqa) },
      Law("Lens law: modify identity") { lensModifyIdentity(lensGen, aGen, eqa) },
      Law("Lens law: compose modify") { lensComposeModify(lensGen, aGen, funcGen, eqa) },
      Law("Lens law: consistent set modify") { lensConsistentSetModify(lensGen, aGen, bGen, eqa) }
    )

  private suspend fun <A, B> lensGetSet(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, lensGen, aGen) { lens, a ->
      lens.run {
        set(a, get(a)).equalUnderTheLaw(a, eq)
      }
    }

  private suspend fun <A, B> lensSetGet(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, bGen: Arb<B>, eq: (B, B) -> Boolean): PropertyContext =
    checkAll(100, lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        get(set(a, b)).equalUnderTheLaw(b, eq)
      }
    }

  private suspend fun <A, B> lensSetIdempotent(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        set(set(a, b), b).equalUnderTheLaw(set(a, b), eq)
      }
    }

  private suspend fun <A, B> lensModifyIdentity(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, lensGen, aGen) { lens, a ->
      lens.run {
        modify(a, ::identity).equalUnderTheLaw(a, eq)
      }
    }

  private suspend fun <A, B> lensComposeModify(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, funcGen: Arb<(B) -> B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, lensGen, aGen, funcGen, funcGen) { lens, a, f, g ->
      lens.run {
        modify(modify(a, f), g).equalUnderTheLaw(modify(a) { g(f(it)) }, eq)
      }
    }

  private suspend fun <A, B> lensConsistentSetModify(lensGen: Arb<Lens<A, B>>, aGen: Arb<A>, bGen: Arb<B>, eq: (A, A) -> Boolean): PropertyContext =
    checkAll(100, lensGen, aGen, bGen) { lens, a, b ->
      lens.run {
        set(a, b).equalUnderTheLaw(modify(a) { b }, eq)
      }
    }
}
