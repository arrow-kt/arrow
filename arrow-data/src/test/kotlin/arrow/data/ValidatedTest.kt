package arrow

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import arrow.core.Left
import arrow.core.None
import arrow.core.Right
import arrow.core.Some
import arrow.data.*
import arrow.instances.IntMonoid
import arrow.instances.StringMonoidInstance
import arrow.test.laws.EqLaws
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.test.laws.ApplicativeLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.*

@RunWith(KTestJUnitRunner::class)
class ValidatedTest : UnitSpec() {

    init {

        "instances can be resolved implicitly" {
            functor<ValidatedKindPartial<String>>() shouldNotBe null
            applicative<ValidatedKindPartial<String>>() shouldNotBe null
            foldable<ValidatedKindPartial<String>>() shouldNotBe null
            traverse<ValidatedKindPartial<String>>() shouldNotBe null
            applicativeError<ValidatedKindPartial<String>, String>() shouldNotBe null
            eq<Validated<String, Int>>() shouldNotBe null
        }

        testLaws(
            EqLaws.laws { it.valid<String, Int>() },
            ApplicativeLaws.laws(Validated.applicative(StringMonoidInstance), Eq.any()),
            TraverseLaws.laws(Validated.traverse(), Validated.applicative(StringMonoidInstance), ::Valid, Eq.any()),
            SemigroupKLaws.laws(
                Validated.semigroupK(IntMonoid),
                Validated.applicative(IntMonoid),
                Eq.any())
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
                    { a -> a + " processed"}
            ) shouldBe value + " processed"
        }

        "leftMap should modify error" {
            Valid(10).leftMap { fail("None should not be called") } shouldBe Valid(10)
            Invalid(13).leftMap { i -> i.toString() + " is Coming soon!" } shouldBe Invalid("13 is Coming soon!")
        }

        "exist should return false if is Invalid" {
            Invalid(13).exist { fail("None should not be called") } shouldBe false
        }

        "exist should return the value of predicate if is Valid" {
            Valid(13).exist { v -> v > 10 } shouldBe true
            Valid(13).exist { v -> v < 10 } shouldBe false
        }

        "swap should return Valid(e) if is Invalid and Invalid(v) in otherwise" {
            Valid(13).swap() shouldBe Invalid(13)
            Invalid(13).swap() shouldBe Valid(13)
        }

        "getOrElse should return value if is Valid or default in otherwise" {
            Valid(13).getOrElse { fail("None should not be called") } shouldBe 13
            Invalid(13).getOrElse { "defaultValue" } shouldBe "defaultValue"
        }

        "valueOr should return value if is Valid or the the result of f in otherwise" {
            Valid(13).valueOr { fail("None should not be called") } shouldBe 13
            Invalid(13).valueOr { e ->  e.toString() + " is the defaultValue" } shouldBe "13 is the defaultValue"
        }

        "orElse should return Valid(value) if is Valid or the result of default in otherwise" {
            Valid(13).orElse { fail("None should not be called") } shouldBe Valid(13)
            Invalid(13).orElse { Valid("defaultValue") } shouldBe Valid("defaultValue")
            Invalid(13).orElse { Invalid("defaultValue") } shouldBe Invalid("defaultValue")
        }

        "foldLeft should return b when is Invalid" {
            Invalid(13).foldLeft("Coming soon!") { _, _ -> fail("None should not be called") } shouldBe "Coming soon!"
        }

        "foldLeft should return f processed when is Valid" {
            Valid(10).foldLeft("Tennant") { b, a -> a.toString() + " is " + b } shouldBe "10 is Tennant"
        }

        "toEither should return Either.Right(value) if is Valid or Either.Left(error) in otherwise" {
            Valid(10).toEither() shouldBe Right(10)
            Invalid(13).toEither() shouldBe Left(13)
        }

        "toIor should return Ior.Right(value) if is Valid or Ior.Left(error) in otherwise" {
            Valid(10).toIor() shouldBe Ior.Right(10)
            Invalid(13).toIor() shouldBe Ior.Left(13)
        }

        "toOption should return Some(value) if is Valid or None in otherwise" {
            Valid(10).toOption() shouldBe Some(10)
            Invalid(13).toOption() shouldBe None
        }

        "toList should return listOf(value) if is Valid or empty list in otherwise" {
            Valid(10).toList() shouldBe listOf(10)
            Invalid(13).toList() shouldBe listOf<Int>()
        }

        "toValidatedNel should return Valid(value) if is Valid or Invalid<NonEmptyList<E>, A>(error) in otherwise" {
            Valid(10).toValidatedNel() shouldBe Valid(10)
            Invalid(13).toValidatedNel() shouldBe Invalid(NonEmptyList(13, listOf()))
        }

        val plusIntSemigroup: Semigroup<Int> = object : Semigroup<Int> {
            override fun combine(a: Int, b: Int): Int = a + b
        }

        "findValid should return the first Valid value or combine or Invalid values in otherwise" {
            Valid(10).findValid(plusIntSemigroup, { fail("None should not be called") }) shouldBe Valid(10)
            Invalid(10).findValid(plusIntSemigroup, { Valid(5) }) shouldBe Valid(5)
            Invalid(10).findValid(plusIntSemigroup, { Invalid(5) }) shouldBe Invalid(15)
        }

        "ap should return Valid(f(a)) if both are Valid" {
            Valid(10).ap<Int, Int, Int>(Valid({ a -> a + 5 }), plusIntSemigroup) shouldBe Valid(15)
        }

        "ap should return first Invalid found if is unique or combine both in otherwise" {
            Invalid(10).ap<Int, Int, Int>(Valid({ a -> a + 5 }), plusIntSemigroup) shouldBe Invalid(10)
            Valid(10).ap<Int, Int, Int>(Invalid(5), plusIntSemigroup) shouldBe Invalid(5)
            Invalid(10).ap<Int, Int, Int>(Invalid(5), plusIntSemigroup) shouldBe Invalid(15)
        }

        data class MyException(val msg: String) : Exception()

        "fromTry should return Valid if is Success or Failure in otherwise" {
            Validated.fromTry(Success(10)) shouldBe Valid(10)
            Validated.fromTry<Int>(Failure(MyException(""))) shouldBe Invalid(MyException(""))
        }

        "fromEither should return Valid if is Either.Right or Failure in otherwise" {
            Validated.fromEither(Right(10)) shouldBe Valid(10)
            Validated.fromEither(Left(10)) shouldBe Invalid(10)
        }

        "fromOption should return Valid if is Some or Invalid in otherwise" {
            Validated.fromOption<Int, Int>(Some(10)) { fail("None should not be called") } shouldBe Valid(10)
            Validated.fromOption<Int, Int>(None) { 5 } shouldBe Invalid(5)
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
            Invalid(10).withEither { it } shouldBe Invalid(10)
        }

        "Cartesian builder should build products over homogeneous Validated" {
            Validated.applicative<String>().map(
                    Valid("11th"),
                    Valid("Doctor"),
                    Valid("Who"),
                    { (a, b, c) -> "$a $b $c" }) shouldBe Valid("11th Doctor Who")
        }

        "Cartesian builder should build products over heterogeneous Validated" {
            Validated.applicative<String>().map(
                    Valid(13),
                    Valid("Doctor"),
                    Valid(false),
                    { (a, b, c) -> "${a}th $b is $c" }) shouldBe Valid("13th Doctor is false")
        }

        "Cartesian builder should build products over Invalid Validated" {
            Validated.applicative<String>().map(
                    Invalid("fail1"),
                    Invalid("fail2"),
                    Valid("Who"),
                    { "success!" }) shouldBe Invalid("fail1fail2")
        }

        "CombineK should combine Valid Validated" {
            val valid = Valid("Who")

            Validated.semigroupK<String>().combineK(valid, valid) shouldBe(Valid("Who"))
        }

        "CombineK should combine Valid and Invalid Validated" {
            val valid = Valid("Who")
            val invalid = Invalid("Nope")

            Validated.semigroupK<String>().combineK(valid, invalid) shouldBe(Valid("Who"))
        }

        "CombineK should combine Invalid Validated" {
            val invalid = Invalid("Nope")

            Validated.semigroupK<String>().combineK(invalid, invalid) shouldBe(Invalid("NopeNope"))
        }

        "Combine should combine Valid Validated" {
            val valid: Validated<String, String> = Valid("Who")

            valid.combine(valid) shouldBe(Valid("WhoWho"))
        }

        "Combine should combine Valid and Invalid Validated" {
            val valid = Valid("Who")
            val invalid = Invalid("Nope")

            valid.combine(invalid) shouldBe(Invalid("Nope"))
        }

        "Combine should combine Invalid Validated" {
            val invalid: Validated<String, String> = Invalid("Nope")

            invalid.combine(invalid) shouldBe(Invalid("NopeNope"))
        }
    }
}
