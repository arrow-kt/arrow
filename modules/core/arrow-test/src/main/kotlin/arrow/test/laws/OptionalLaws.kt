package arrow.test.laws

import arrow.core.Const
import arrow.core.Id
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.compose
import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.id.applicative.applicative
import arrow.core.identity
import arrow.core.value
import arrow.optics.Optional
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
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
    Law("Optional Law: consistent set with modify") { consistentSetModify(optionalGen, aGen, bGen, EQA) },
    Law("Optional Law: consistent modify with modify identity") {
      consistentModifyModifyId(
        optionalGen,
        aGen,
        funcGen,
        EQA
      )
    },
    Law("Optional Law: consistent getOption with modify identity") {
      consistentGetOptionModifyId(
        optionalGen,
        aGen,
        EQOptionB
      )
    }
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

  fun <A, B> consistentModifyModifyId(
    optionalGen: Gen<Optional<A, B>>,
    aGen: Gen<A>,
    funcGen: Gen<(B) -> B>,
    EQA: Eq<A>
  ): Unit =
    forAll(optionalGen, aGen, funcGen) { optional, a, f ->
      optional.run {
        modify(a, f)
          .equalUnderTheLaw(modifyF(Id.applicative(), a) { Id.just(f(it)) }.value(), EQA)
      }
    }

  fun <A, B> consistentGetOptionModifyId(
    optionalGen: Gen<Optional<A, B>>,
    aGen: Gen<A>,
    EQOptionB: Eq<Option<B>>
  ) {
    val firstMonoid = object : Monoid<FirstOption<B>> {
      override fun empty(): FirstOption<B> = FirstOption(None)
      override fun FirstOption<B>.combine(b: FirstOption<B>): FirstOption<B> =
        if (option.fold({ false }, { true })) this else b
    }

    forAll(optionalGen, aGen) { optional, a ->
      optional.run {
        modifyF(Const.applicative(firstMonoid), a) { b ->
          Const(FirstOption(Some(b)))
        }.value().option.equalUnderTheLaw(getOption(a), EQOptionB)
      }
    }
  }

  @PublishedApi
  internal data class FirstOption<A>(val option: Option<A>)
}
