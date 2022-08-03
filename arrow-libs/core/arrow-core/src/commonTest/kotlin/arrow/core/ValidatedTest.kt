package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.core.validated

@Suppress("RedundantSuspendModifier")
class ValidatedTest : StringSpec() {

  init {

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

    "orNone should return value if is Valid or None otherwise" {
      Valid(13).orNone() shouldBe Some(13)
      val invalid: Validated<Int, Int> = Invalid(13)
      invalid.orNone() shouldBe None
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

    "findValid should return the first Valid value or combine or Invalid values otherwise" {
      Valid(10).findValid(Semigroup.int()) { fail("None should not be called") } shouldBe Valid(10)
      Invalid(10).findValid(Semigroup.int()) { Valid(5) } shouldBe Valid(5)
      Invalid(10).findValid(Semigroup.int()) { Invalid(5) } shouldBe Invalid(15)
    }

    val nullableLongSemigroup = object : Monoid<Long?> {
      override fun empty(): Long? = 0
      override fun Long?.combine(b: Long?): Long? =
        Nullable.zip(this@combine, b) { a, bb -> a + bb }
    }

    "zip identity" {
      checkAll(Arb.validated(Arb.long().orNull(), Arb.int().orNull())) { validated ->
        val res = validated.zip(nullableLongSemigroup, Valid(Unit)) { a, _ -> a }
        res shouldBe validated
      }
    }

    "tap applies effects returning the original value" {
      checkAll(Arb.validated(Arb.long(), Arb.int())) { validated ->
        var effect = 0
        val res = validated.tap { effect += 1 }
        val expected = when (validated) {
          is Validated.Valid -> 1
          is Validated.Invalid -> 0
        }
        effect shouldBe expected
        res shouldBe validated
      }
    }

    "tapInvalid applies effects returning the original value" {
      checkAll(Arb.validated(Arb.long(), Arb.int())) { validated ->
        var effect = 0
        val res = validated.tapInvalid { effect += 1 }
        val expected = when (validated) {
          is Validated.Valid -> 0
          is Validated.Invalid -> 1
        }
        effect shouldBe expected
        res shouldBe validated
      }
    }

    "zip is derived from flatMap" {
      checkAll(
        Arb.validated(Arb.long().orNull(), Arb.int().orNull()),
        Arb.validated(Arb.long().orNull(), Arb.int().orNull()),
        Arb.validated(Arb.long().orNull(), Arb.int().orNull()),
        Arb.validated(Arb.long().orNull(), Arb.int().orNull()),
        Arb.validated(Arb.long().orNull(), Arb.int().orNull()),
        Arb.validated(Arb.long().orNull(), Arb.int().orNull()),
        Arb.validated(Arb.long().orNull(), Arb.int().orNull()),
        Arb.validated(Arb.long().orNull(), Arb.int().orNull()),
        Arb.validated(Arb.long().orNull(), Arb.int().orNull()),
        Arb.validated(Arb.long().orNull(), Arb.int().orNull())
      ) { a, b, c, d, e, f, g, h, i, j ->
        val res = a.zip(
          nullableLongSemigroup,
          b, c, d, e, f, g, h, i, j
        ) { a, b, c, d, e, f, g, h, i, j ->
          Nullable.zip(
            a,
            b,
            c,
            d,
            e,
            f,
            g,
            h,
            i,
            j
          ) { a, b, c, d, e, f, g, h, i, j -> a + b + c + d + e + f + g + h + i + j }
        }

        val all = listOf(a, b, c, d, e, f, g, h, i, j)
        val isValid = all.all { it.isValid }
        val expected: Validated<Long?, Int?> =
          if (isValid) Valid(all.fold<Validated<Long?, Int?>, Int?>(0) { acc, validated ->
            Nullable.zip(
              acc,
              validated.orNull()
            ) { a, b -> a + b }
          })
          else Invalid(
            all.filterIsInstance<Invalid<Long?>>().map { it.value }.fold(nullableLongSemigroup)
          )

        res shouldBe expected
      }
    }

    "zip should return Valid(f(a)) if both are Valid" {
      Valid(10).zip(Semigroup.int(), Valid { a: Int -> a + 5 }) { a, ff -> ff(a) } shouldBe Valid(15)
    }

    "zip should return first Invalid found if is unique or combine both otherwise" {
      Invalid(10).zip(Semigroup.int(), Valid { a: Int -> a + 5 }) { a, ff -> ff(a) } shouldBe Invalid(10)
      Valid(10).zip<Int, Int, (Int) -> Int, Int>(Semigroup.int(), Invalid(5)) { a, ff -> ff(a) } shouldBe Invalid(5)
      Invalid(10).zip<Int, Int, (Int) -> Int, Int>(Semigroup.int(), Invalid(5)) { a, ff -> ff(a) } shouldBe Invalid(15)
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
      Validated.catchNel { loadFromNetwork() } shouldBe Invalid(nonEmptyListOf(exception))
    }

    "Cartesian builder should build products over homogeneous Validated" {
      Valid("11th").zip(
        Semigroup.string(),
        Valid("Doctor"),
        Valid("Who")
      ) { a, b, c -> "$a $b $c" } shouldBe Valid("11th Doctor Who")
    }

    "Cartesian builder should build products over heterogeneous Validated" {
      Valid(13).zip(
        Semigroup.string(),
        Valid("Doctor"),
        Valid(false)
      ) { a, b, c -> "${a}th $b is $c" } shouldBe Valid("13th Doctor is false")
    }

    "Cartesian builder should build products over Invalid Validated" {
      Invalid("fail1").zip(
        Semigroup.string(),
        Invalid("fail2"),
        Valid("Who")
      ) { _, _, _ -> "success!" } shouldBe Invalid("fail1fail2")
    }

    "Cartesian builder for nel doesn't need semigroup parameter" {
      "fail1".invalidNel().zip(
        "fail2".invalidNel()
      ) { _, _ -> "success!" } shouldBe Invalid(nonEmptyListOf("fail1", "fail2"))
    }

    "CombineK should combine Valid Validated" {
      val valid = Valid("Who")

      valid.combineK(Semigroup.string(), valid) shouldBe (Valid("Who"))
    }

    "CombineK should combine Valid and Invalid Validated" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.combineK(Semigroup.string(), invalid) shouldBe (Valid("Who"))
    }

    "CombineK should combine Invalid Validated" {
      val invalid = Invalid("Nope")

      invalid.combineK(Semigroup.string(), invalid) shouldBe (Invalid("NopeNope"))
    }

    "Combine should combine Valid Validated" {
      val valid: Validated<String, String> = Valid("Who")

      valid.combine(Monoid.string(), Monoid.string(), valid) shouldBe (Valid("WhoWho"))
    }

    "Combine should combine Valid and Invalid Validated" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.combine(Monoid.string(), Monoid.string(), invalid) shouldBe (Invalid("Nope"))
    }

    "Combine should combine Invalid Validated" {
      val invalid: Validated<String, String> = Invalid("Nope")

      invalid.combine(Monoid.string(), Monoid.string(), invalid) shouldBe (Invalid("NopeNope"))
    }

    "traverse should yield list when validated is valid" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.traverse { listOf(it) } shouldBe listOf(Valid("Who"))
      invalid.traverse { listOf(it) } shouldBe emptyList()
    }

    "sequence should yield consistent result with traverse" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        val valid = Valid(a)
        val invalid = Invalid(b)

        valid.traverse { listOf(it) } shouldBe valid.map { listOf(it) }.sequence()
        invalid.traverse { listOf(it) } shouldBe invalid.map { listOf(it) }.sequence()
      }
    }

    "traverse for Option should yield option when validated is valid" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.traverse { Some(it) } shouldBe Some(Valid("Who"))
      invalid.traverse { Some(it) } shouldBe None
    }

    "sequence for Option should yield consistent result with traverseOption" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        val valid = Valid(a)
        val invalid = Invalid(b)

        valid.traverse { Some(it) } shouldBe valid.map { Some(it) }.sequence()
        invalid.traverse { Some(it) } shouldBe invalid.map { Some(it) }.sequence()
      }
    }

    "traverseNullable should yield non-null object when validated is valid" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.traverseNullable<String?> { it } shouldBe Valid("Who")
      valid.traverseNullable<String?> { null }.shouldBeNull()
      invalid.traverseNullable<String?> { it }.shouldBeNull()
    }

    "sequence for Nullable should yield consistent result with traverseNullable" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        val valid = Valid(a)
        val invalid = Invalid(b)

        valid.traverseNullable<String?> { it } shouldBe valid.map<String?> { it }.sequence()
        valid.traverseNullable<String?> { null } shouldBe valid.map<String?> { null }.sequence()
        invalid.traverseNullable<String?> { it } shouldBe invalid.map<String?> { it }.sequence()
      }
    }

    "traverse for Either should wrap validated in either" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.traverse { it.right() } shouldBe Valid("Who").right()
      invalid.traverse { it.right() } shouldBe Invalid("Nope").right()
    }

    "sequence for Either should yield consistent result with traverseEither" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        val valid = Valid(a)
        val invalid = Invalid(b)

        valid.traverse { Right(it) } shouldBe valid.map { Right(it) }.sequence()
        invalid.traverse { Right(it) } shouldBe invalid.map { Right(it) }.sequence()
      }
    }

    "bitraverse should wrap valid or invalid in a list" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.bitraverse({ listOf(it) }, { listOf(it) }) shouldBe listOf(Valid("Who"))
      invalid.bitraverse({ listOf(it) }, { listOf(it) }) shouldBe listOf(Invalid("Nope"))
    }

    "bisequence should yield consistent result with bitraverse" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        val valid: Validated<String, String> = Valid(a)
        val invalid: Validated<String, String> = Invalid(b)

        valid.bimap({ listOf(it) }, { listOf(it) }).bisequence() shouldBe valid.bitraverse(
          { listOf(it) },
          { listOf(it) })
        invalid.bimap({ listOf(it) }, { listOf(it) }).bisequence() shouldBe invalid.bitraverse(
          { listOf(it) },
          { listOf(it) })
      }
    }

    "bitraverseOption should wrap valid or invalid in an option" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.bitraverseOption({ Some(it) }, { Some(it) }) shouldBe Some(Valid("Who"))
      invalid.bitraverseOption({ Some(it) }, { Some(it) }) shouldBe Some(Invalid("Nope"))
    }

    "bisequenceOption should yield consistent result with bitraverseOption" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        val valid: Validated<String, String> = Valid(a)
        val invalid: Validated<String, String> = Invalid(b)

        valid.bimap({ Some(it) }, { Some(it) }).bisequenceOption() shouldBe
          valid.bitraverseOption({ Some(it) }, { Some(it) })
        invalid.bimap({ Some(it) }, { Some(it) }).bisequenceOption() shouldBe
          invalid.bitraverseOption({ Some(it) }, { Some(it) })
      }
    }

    "bisequenceNullable should yield consistent result with bitraverseNullable" {
      checkAll(Arb.string().orNull(), Arb.string().orNull()) { a: String?, b: String? ->
        val valid: Validated<String?, String?> = Valid(a)
        val invalid: Validated<String?, String?> = Invalid(b)

        valid.bimap({ it }, { it }).bisequenceNullable() shouldBe
          valid.bitraverseNullable({ it }, { it })
        invalid.bimap({ it }, { it }).bisequenceNullable() shouldBe
          invalid.bitraverseNullable({ it }, { it })
      }
    }

    "bitraverseNullable should wrap valid or invalid in a nullable" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.bitraverseNullable({ it }, { it }) shouldBe Valid("Who")
      invalid.bitraverseNullable({ it }, { it }) shouldBe Invalid("Nope")
    }

    "bitraverseEither should wrap valid or invalid in an either" {
      val valid = Valid("Who")
      val invalid = Invalid("Nope")

      valid.bitraverseEither({ it.left() }, { it.right() }) shouldBe Valid("Who").right()
      invalid.bitraverseEither({ it.left() }, { it.right() }) shouldBe "Nope".left()
    }

    "bisequenceEither should yield consistent result with bitraverseEither" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        val valid: Validated<String, String> = Valid(a)
        val invalid: Validated<String, String> = Invalid(b)

        valid.bimap({ it.left() }, { it.right() }).bisequenceEither() shouldBe
          valid.bitraverseEither({ it.left() }, { it.right() })
        invalid.bimap({ it.left() }, { it.right() }).bisequenceEither() shouldBe
          invalid.bitraverseEither({ it.left() }, { it.right() })
      }
    }

    "andThen should return Valid(result) if f return Valid" {
      checkAll(Arb.int(), Arb.int()) { x, y ->
        Valid(x).andThen { Valid(it + y) } shouldBe Valid(x + y)
      }
    }

    "andThen should only run f on valid instances " {
      checkAll(Arb.int(), Arb.int()) { x, y ->
        Invalid(x).andThen { Valid(y) } shouldBe Invalid(x)
      }
    }

    "andThen should return Invalid(result) if f return Invalid " {
      checkAll(Arb.int(), Arb.int()) { x, y ->
        Valid(x).andThen { Invalid(it + y) } shouldBe Invalid(x + y)
      }
    }
  }
}
