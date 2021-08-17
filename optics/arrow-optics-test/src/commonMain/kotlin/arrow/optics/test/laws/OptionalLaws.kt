package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Optional
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.constant
import io.kotest.property.checkAll

public object OptionalLaws {

  public fun <A, B> laws(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    eqa: (A, A) -> Boolean = { a, b -> a == b },
    eqb: (B?, B?) -> Boolean = { a, b -> a == b }
  ): List<Law> = listOf(
    Law("Optional Law: set what you get") { getOptionSet(optionalGen, aGen, eqa) },
    Law("Optional Law: set what you get") { setGetOption(optionalGen, aGen, bGen, eqb) },
    Law("Optional Law: set is idempotent") { setIdempotent(optionalGen, aGen, bGen, eqa) },
    Law("Optional Law: modify identity = identity") { modifyIdentity(optionalGen, aGen, eqa) },
    Law("Optional Law: compose modify") { composeModify(optionalGen, aGen, funcGen, eqa) },
    Law("Optional Law: consistent set with modify") { consistentSetModify(optionalGen, aGen, bGen, eqa) }
  )

  /**
   * Warning: Use only when a `Gen.constant()` applies
   */
  public fun <A, B> laws(
    optional: Optional<A, B>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    funcGen: Arb<(B) -> B>,
    eqa: (A, A) -> Boolean = { a, b -> a == b },
    eqb: (B?, B?) -> Boolean = { a, b -> a == b }
  ): List<Law> = laws(Arb.constant(optional), aGen, bGen, funcGen, eqa, eqb)

  public suspend fun <A, B> getOptionSet(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    eq: (A, A) -> Boolean
  ): PropertyContext =
    checkAll(optionalGen, aGen) { optional, a ->
      optional.run {
        getOrModify(a).fold(::identity) { set(a, it) }
          .equalUnderTheLaw(a, eq)
      }
    }

  public suspend fun <A, B> setGetOption(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    eq: (B?, B?) -> Boolean
  ): PropertyContext =
    checkAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        getOrNull(set(a, b))
          .equalUnderTheLaw(getOrNull(a)?.let { b }) { a, b -> eq(a, b) }
      }
    }

  public suspend fun <A, B> setIdempotent(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    eq: (A, A) -> Boolean
  ): PropertyContext =
    checkAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        set(set(a, b), b)
          .equalUnderTheLaw(set(a, b), eq)
      }
    }

  public suspend fun <A, B> modifyIdentity(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    eq: (A, A) -> Boolean
  ): PropertyContext =
    checkAll(optionalGen, aGen) { optional, a ->
      optional.run {
        modify(a, ::identity)
          .equalUnderTheLaw(a, eq)
      }
    }

  public suspend fun <A, B> composeModify(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    funcGen: Arb<(B) -> B>,
    eq: (A, A) -> Boolean
  ): PropertyContext =
    checkAll(optionalGen, aGen, funcGen, funcGen) { optional, a, f, g ->
      optional.run {
        modify(modify(a, f), g)
          .equalUnderTheLaw(modify(a, g compose f), eq)
      }
    }

  public suspend fun <A, B> consistentSetModify(
    optionalGen: Arb<Optional<A, B>>,
    aGen: Arb<A>,
    bGen: Arb<B>,
    eq: (A, A) -> Boolean
  ): PropertyContext =
    checkAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        set(a, b)
          .equalUnderTheLaw(modify(a) { b }, eq)
      }
    }
}
