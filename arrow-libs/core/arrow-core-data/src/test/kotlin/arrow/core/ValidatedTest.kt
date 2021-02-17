package arrow.core

import arrow.core.computations.RestrictedValidatedEffect
import arrow.core.computations.ValidatedEffect
import arrow.core.computations.validated
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.monoid
import arrow.core.extensions.order
import arrow.core.extensions.semigroup
import arrow.core.extensions.show
import arrow.core.extensions.validated.applicative.applicative
import arrow.core.extensions.validated.bifunctor.bifunctor
import arrow.core.extensions.validated.bitraverse.bitraverse
import arrow.core.extensions.validated.eq.eq
import arrow.core.extensions.validated.eqK.eqK
import arrow.core.extensions.validated.eqK2.eqK2
import arrow.core.extensions.validated.functor.functor
import arrow.core.extensions.validated.hash.hash
import arrow.core.extensions.validated.order.order
import arrow.core.extensions.validated.selective.selective
import arrow.core.extensions.validated.semigroupK.semigroupK
import arrow.core.extensions.validated.show.show
import arrow.core.extensions.validated.traverse.traverse
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.genK2
import arrow.core.test.generators.validated
import arrow.core.test.laws.BifunctorLaws
import arrow.core.test.laws.BitraverseLaws
import arrow.core.test.laws.EqK2Laws
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.FxLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.OrderLaws
import arrow.core.test.laws.SelectiveLaws
import arrow.core.test.laws.SemigroupKLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Semigroup
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe

@Suppress("RedundantSuspendModifier")
class ValidatedTest : UnitSpec() {

  init {

    val EQ = Validated.eq(String.eq(), Int.eq())

    val VAL_AP = Validated.applicative(String.monoid())

    val VAL_SGK = Validated.semigroupK(String.semigroup())

    val validatedGen = Gen.validated(Gen.string(), Gen.int())

    testLaws(
      EqK2Laws.laws(Validated.eqK2(), Validated.genK2()),
      BifunctorLaws.laws(Validated.bifunctor(), Validated.genK2(), Validated.eqK2()),
      EqLaws.laws(EQ, Gen.validated(Gen.string(), Gen.int())),
      ShowLaws.laws(Validated.show(String.show(), Int.show()), EQ, Gen.validated(Gen.string(), Gen.int())),
      HashLaws.laws(Validated.hash(String.hash(), Int.hash()), Gen.validated(Gen.string(), Gen.int()), EQ),
      OrderLaws.laws(Validated.order(String.order(), Int.order()), Gen.validated(Gen.string(), Gen.int())),
      SelectiveLaws.laws(Validated.selective(String.semigroup()), Validated.functor(), Validated.genK(Gen.string()), Validated.eqK(String.eq())),
      TraverseLaws.laws(Validated.traverse(), Validated.applicative(String.semigroup()), Validated.genK(Gen.string()), Validated.eqK(String.eq())),
      SemigroupKLaws.laws(
        Validated.semigroupK(String.semigroup()),
        Validated.genK(Gen.string()),
        Validated.eqK(String.eq())
      ),
      BitraverseLaws.laws(
        Validated.bitraverse(),
        Validated.genK2(),
        Validated.eqK2()
      ),
      FxLaws.suspended<ValidatedEffect<String, *>, Validated<String, Int>, Int>(Gen.int().map(::Valid), Gen.int().map(::Valid), Eq.any(), validated::invoke) {
        it.bind()
      },
      FxLaws.eager<RestrictedValidatedEffect<String, *>, Validated<String, Int>, Int>(Gen.int().map(::Valid), Gen.int().map(::Valid), Eq.any(), validated::eager) {
        it.bind()
      }
    )

    "fold should call function on Invalid" {
      val exception = Exception("My Exception")
      val result: Validated<Throwable, String> = Invalid(exception)
      result.fold(
        { e -> e.message + " Checked" },
        { fail("Some should not be called") }
      ) shouldBe "My Exception Checked"
    }

    "fold should call function on Valid" {
      val value = "Some value"
      val result: Validated<Throwable, String> = Valid(value)
      result.fold(
        { fail("None should not be called") },
        { a -> "$a processed" }
      ) shouldBe "$value processed"
    }

    "leftMap should modify error" {
      Valid(10).mapLeft { fail("None should not be called") } shouldBe Valid(10)
      Invalid(13).mapLeft { i -> "$i is Coming soon!" } shouldBe Invalid("13 is Coming soon!")
    }

    "exist should return false if is Invalid" {
      Invalid(13).exist { fail("None should not be called") } shouldBe false
    }

    "exist should return the value of predicate if is Valid" {
      Valid(13).exist { v -> v > 10 } shouldBe true
      Valid(13).exist { v -> v < 10 } shouldBe false
    }

    "swap should return Valid(e) if is Invalid and Invalid(v) otherwise" {
      Valid(13).swap() shouldBe Invalid(13)
      Invalid(13).swap() shouldBe Valid(13)
    }

    "getOrElse should return value if is Valid or default otherwise" {
      Valid(13).getOrElse { fail("None should not be called") } shouldBe 13
      Invalid(13).getOrElse { "defaultValue" } shouldBe "defaultValue"
    }

    "orNull should return value if is Valid or null otherwise" {
      Valid(13).orNull() shouldBe 13
      val invalid: Validated<Int, Int> = Invalid(13)
      invalid.orNull() shouldBe null
    }

    "valueOr should return value if is Valid or the the result of f otherwise" {
      Valid(13).valueOr { fail("None should not be called") } shouldBe 13
      Invalid(13).valueOr { e -> "$e is the defaultValue" } shouldBe "13 is the defaultValue"
    }

    "orElse should return Valid(value) if is Valid or the result of default otherwise" {
      Valid(13).orElse { fail("None should not be called") } shouldBe Valid(13)
      Invalid(13).orElse { Valid("defaultValue") } shouldBe Valid("defaultValue")
      Invalid(13).orElse { Invalid("defaultValue") } shouldBe Invalid("defaultValue")
    }

    "foldLeft should return b when is Invalid" {
      Invalid(13).foldLeft("Coming soon!") { _, _ -> fail("None should not be called") } shouldBe "Coming soon!"
    }

    "foldLeft should return f processed when is Valid" {
      Valid(10).foldLeft("Tennant") { b, a -> "$a is $b" } shouldBe "10 is Tennant"
    }

    "toEither should return Either.Right(value) if is Valid or Either.Left(error) otherwise" {
      Valid(10).toEither() shouldBe Right(10)
      Invalid(13).toEither() shouldBe Left(13)
    }

    "toIor should return Ior.Right(value) if is Valid or Ior.Left(error) otherwise" {
      Valid(10).toIor() shouldBe Ior.Right(10)
      Invalid(13).toIor() shouldBe Ior.Left(13)
    }

    "toOption should return Some(value) if is Valid or None otherwise" {
      Valid(10).toOption() shouldBe Some(10)
      Invalid(13).toOption() shouldBe None
    }

    "toList should return listOf(value) if is Valid or empty list otherwise" {
      Valid(10).toList() shouldBe listOf(10)
      Invalid(13).toList() shouldBe listOf<Int>()
    }

    "toValidatedNel should return Valid(value) if is Valid or Invalid<NonEmptyList<E>, A>(error) otherwise" {
      Valid(10).toValidatedNel() shouldBe Valid(10)
      Invalid(13).toValidatedNel() shouldBe Invalid(NonEmptyList(13, listOf()))
    }

    val plusIntSemigroup: Semigroup<Int> = Int.semigroup()

    "findValid should return the first Valid value or combine or Invalid values otherwise" {
      Valid(10).findValid(plusIntSemigroup) { fail("None should not be called") } shouldBe Valid(10)
      Invalid(10).findValid(plusIntSemigroup) { Valid(5) } shouldBe Valid(5)
      Invalid(10).findValid(plusIntSemigroup) { Invalid(5) } shouldBe Invalid(15)
    }

    "ap should return Valid(f(a)) if both are Valid" {
      Valid(10).ap<Int, Int, Int>(plusIntSemigroup, Valid({ a -> a + 5 })) shouldBe Valid(15)
    }

    "ap should return first Invalid found if is unique or combine both otherwise" {
      Invalid(10).ap<Int, Int, Int>(plusIntSemigroup, Valid({ a -> a + 5 })) shouldBe Invalid(10)
      Valid(10).ap<Int, Int, Int>(plusIntSemigroup, Invalid(5)) shouldBe Invalid(5)
      Invalid(10).ap<Int, Int, Int>(plusIntSemigroup, Invalid(5)) shouldBe Invalid(15)
    }

    data class MyException(val msg: String) : Exception()

    "fromEither should return Valid if is Either.Right or Failure otherwise" {
      Validated.fromEither(Right(10)) shouldBe Valid(10)
      Validated.fromEither(Left(10)) shouldBe Invalid(10)
    }

    "fromOption should return Valid if is Some or Invalid otherwise" {
      Validated.fromOption<Int, Int>(Some(10)) { fail("should not be called") } shouldBe Valid(10)
      Validated.fromOption<Int, Int>(None) { 5 } shouldBe Invalid(5)
    }

    "fromNullable should return Valid if is not-null or Invalid otherwise" {
      Validated.fromNullable<Int, Int>(10) { fail("should not be called") } shouldBe Valid(10)
      Validated.fromNullable<Int, Int>(null) { 5 } shouldBe Invalid(5)
    }

    "invalidNel<E> should return a Invalid<NonEmptyList<E>>" {
      Validated.invalidNel<Int, Int>(10) shouldBe Invalid(NonEmptyList(10, listOf()))
    }

    "withEither should return Valid(result) if f return Right" {
      Valid(10).withEither { it.map { it + 5 } } shouldBe Valid(15)
      Invalid(10).withEither { Right(5) } shouldBe Valid(5)
    }

    "withEither should return Invalid(result) if f return Left" {
      Valid(10).withEither { Left(5) } shouldBe Invalid(5)
      Invalid(10).withEither(::identity) shouldBe Invalid(10)
    }

    "catch should return Valid(result) when f does not throw" {
      suspend fun loadFromNetwork(): Int = 1
      Validated.catch { loadFromNetwork() } shouldBe Valid(1)
    }

    "catch should return Invalid(result) when f throws" {
      val exception = MyException("Boom!")
      suspend fun loadFromNetwork(): Int = throw exception
      Validated.catch { loadFromNetwork() } shouldBe Invalid(exception)
    }

    "catchNel should return Valid(result) when f does not throw" {
      suspend fun loadFromNetwork(): Int = 1
      Validated.catchNel { loadFromNetwork() } shouldBe Valid(1)
    }

    "catchNel should return Invalid(Nel(result)) when f throws" {
      val exception = MyException("Boom!")
      suspend fun loadFromNetwork(): Int = throw exception
      Validated.catchNel { loadFromNetwork() } shouldBe Invalid(NonEmptyList(exception))
    }

    with(VAL_AP) {

      "Cartesian builder should build products over homogeneous Validated" {
        mapN(
          Valid("11th"),
          Valid("Doctor"),
          Valid("Who")
        ) { (a, b, c) -> "$a $b $c" } shouldBe Valid("11th Doctor Who")
      }

      "Cartesian builder should build products over heterogeneous Validated" {
        mapN(
          Valid(13),
          Valid("Doctor"),
          Valid(false)
        ) { (a, b, c) -> "${a}th $b is $c" } shouldBe Valid("13th Doctor is false")
      }

      "Cartesian builder should build products over Invalid Validated" {
        mapN(
          Invalid("fail1"),
          Invalid("fail2"),
          Valid("Who")
        ) { "success!" } shouldBe Invalid("fail1fail2")
      }
    }

    with(VAL_SGK) {
      "CombineK should combine Valid Validated" {
        val valid = Valid("Who")

        valid.combineK(valid) shouldBe (Valid("Who"))
      }

      "CombineK should combine Valid and Invalid Validated" {
        val valid = Valid("Who")
        val invalid = Invalid("Nope")

        valid.combineK(invalid) shouldBe (Valid("Who"))
      }

      "CombineK should combine Invalid Validated" {
        val invalid = Invalid("Nope")

        invalid.combineK(invalid) shouldBe (Invalid("NopeNope"))
      }
    }

    "Combine should combine Valid Validated" {
      val valid: Validated<String, String> = Valid("Who")

      valid.combine(String.monoid(), String.monoid(), valid) shouldBe (Valid("WhoWho"))
    }

    "Combine should combine Valid and Invalid Validated" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.combine(String.monoid(), String.monoid(), invalid) shouldBe (Invalid("Nope"))
    }

    "Combine should combine Invalid Validated" {
      val invalid: Validated<String, String> = Invalid("Nope")

      invalid.combine(String.monoid(), String.monoid(), invalid) shouldBe (Invalid("NopeNope"))
    }
  }
}
