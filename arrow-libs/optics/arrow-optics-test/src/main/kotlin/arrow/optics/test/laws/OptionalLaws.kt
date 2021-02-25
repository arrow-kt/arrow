package arrow.optics.test.laws

import arrow.core.Option
import arrow.core.compose
import arrow.core.identity
import arrow.optics.Optional
import arrow.core.test.laws.Law
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object OptionalLaws {

  fun <A, B> laws(
    optionalGen: Gen<Optional<A, B>>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    EQA: Eq<A>,
    EQOptionB: Eq<Option<B>>
  ): List<Law> = listOf(
    Law("Optional Law: set what you get") { getOptionSet(optionalGen, aGen, EQA) },
    Law("Optional Law: set what you get") { setGetOption(optionalGen, aGen, bGen, EQOptionB) },
    Law("Optional Law: set is idempotent") { setIdempotent(optionalGen, aGen, bGen, EQA) },
    Law("Optional Law: modify identity = identity") { modifyIdentity(optionalGen, aGen, EQA) },
    Law("Optional Law: compose modify") { composeModify(optionalGen, aGen, funcGen, EQA) },
    Law("Optional Law: consistent set with modify") { consistentSetModify(optionalGen, aGen, bGen, EQA) }
  )

  /**
   * Warning: Use only when a `Gen.constant()` applies
   */
  fun <A, B> laws(
    optional: Optional<A, B>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    funcGen: Gen<(B) -> B>,
    EQA: Eq<A>,
    EQOptionB: Eq<Option<B>>
  ): List<Law> = laws(Gen.constant(optional), aGen, bGen, funcGen, EQA, EQOptionB)

  fun <A, B> getOptionSet(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, EQA: Eq<A>): Unit =
    forAll(optionalGen, aGen) { optional, a ->
      optional.run {
        getOrModify(a).fold(::identity) { set(a, it) }
          .equalUnderTheLaw(a, EQA)
      }
    }

  fun <A, B> setGetOption(
    optionalGen: Gen<Optional<A, B>>,
    aGen: Gen<A>,
    bGen: Gen<B>,
    EQOptionB: Eq<Option<B>>
  ): Unit =
    forAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        getOption(set(a, b))
          .equalUnderTheLaw(getOption(a).map { b }, EQOptionB)
      }
    }

  fun <A, B> setIdempotent(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
    forAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        set(set(a, b), b)
          .equalUnderTheLaw(set(a, b), EQA)
      }
    }

  fun <A, B> modifyIdentity(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, EQA: Eq<A>): Unit =
    forAll(optionalGen, aGen) { optional, a ->
      optional.run {
        modify(a, ::identity)
          .equalUnderTheLaw(a, EQA)
      }
    }

  fun <A, B> composeModify(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, funcGen: Gen<(B) -> B>, EQA: Eq<A>): Unit =
    forAll(optionalGen, aGen, funcGen, funcGen) { optional, a, f, g ->
      optional.run {
        modify(modify(a, f), g)
          .equalUnderTheLaw(modify(a, g compose f), EQA)
      }
    }

  fun <A, B> consistentSetModify(optionalGen: Gen<Optional<A, B>>, aGen: Gen<A>, bGen: Gen<B>, EQA: Eq<A>): Unit =
    forAll(optionalGen, aGen, bGen) { optional, a, b ->
      optional.run {
        set(a, b)
          .equalUnderTheLaw(modify(a) { b }, EQA)
      }
    }
}
