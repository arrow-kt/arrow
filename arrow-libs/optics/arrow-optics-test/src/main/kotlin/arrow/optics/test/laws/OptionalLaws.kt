package arrow.optics.test.laws

import arrow.core.compose
import arrow.core.identity
import arrow.optics.Optional
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object OptionalLaws {

  fun <A, B> laws(
    optionalGen: Gen<Optional<A, B>>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
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
  fun <A, B> laws(
    optional: Optional<A, B>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    eqa: (A, A) -> Boolean = { a, b -> a == b },
    eqb: (B?, B?) -> Boolean = { a, b -> a == b }
  ): List<Law> = laws(Gen.constant(optional), aGen, bGen, funcGen, eqa, eqb)

  fun <A, B> getOptionSet(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, eq: (A, A) -> Boolean): Unit =
    forAll(optionalGen, aGen) { optional, a ->
      optional.run {
        getOrModify(a).fold(::identity) { set(a, it) }
          .equalUnderTheLaw(a, eq)
      }
    }

  fun <A, B> setGetOption(
    optionalGen: Gen<Optional<A, B>>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    eq: (B?, B?) -> Boolean
  ): Unit =
    forAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        getOrNull(set(a, b))
          .equalUnderTheLaw(getOrNull(a)?.let { b }) { a, b -> eq(a, b) }
      }
    }

  fun <A, B> setIdempotent(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, bGen: Gen<B>, eq: (A, A) -> Boolean): Unit =
    forAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        set(set(a, b), b)
          .equalUnderTheLaw(set(a, b), eq)
      }
    }

  fun <A, B> modifyIdentity(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, eq: (A, A) -> Boolean): Unit =
    forAll(optionalGen, aGen) { optional, a ->
      optional.run {
        modify(a, ::identity)
          .equalUnderTheLaw(a, eq)
      }
    }

  fun <A, B> composeModify(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, eq: (A, A) -> Boolean): Unit =
    forAll(optionalGen, aGen, funcGen, funcGen) { optional, a, f, g ->
      optional.run {
        modify(modify(a, f), g)
          .equalUnderTheLaw(modify(a, g compose f), eq)
      }
    }

  fun <A, B> consistentSetModify(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, bGen: Gen<B>, eq: (A, A) -> Boolean): Unit =
    forAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        set(a, b)
          .equalUnderTheLaw(modify(a) { b }, eq)
      }
    }
}
