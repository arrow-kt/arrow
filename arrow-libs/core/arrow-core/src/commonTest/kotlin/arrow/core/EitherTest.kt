package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.raise.ensure
import arrow.core.test.either
import arrow.core.test.intSmall
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.nonEmptyList
import arrow.core.test.testLaws
import io.kotest.assertions.AssertionErrorBuilder.Companion.fail
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test

class EitherTest {

  @Test
  fun monoidLaws() = testLaws(
    MonoidLaws(
      "Either",
      0.right(),
      { x, y ->
        x.combine(y, String::plus, Int::plus)
      },
      Arb.either(Arb.string(), Arb.int()),
    ),
  )

  @Test
  fun leftIsLeftIsRight() = runTest {
    checkAll(Arb.int()) { a: Int ->
      val x = Left(a)
      if (x.isLeft()) {
        x.value shouldBe a
      } else {
        fail("Left(a).isLeft() cannot be false")
      }
      x.isRight() shouldBe false
    }
  }

  @Test
  fun rightIsLeftIsRight() = runTest {
    checkAll(Arb.int()) { a: Int ->
      val x = Right(a)
      if (x.isRight()) {
        x.value shouldBe a
      } else {
        fail("Right(a).isRight() cannot be false")
      }
      x.isLeft() shouldBe false
    }
  }

  @Test
  fun tapAppliesEffects() = runTest {
    checkAll(Arb.either(Arb.long(), Arb.int())) { either ->
      var effect = 0
      val res = either.onRight { effect += 1 }
      val expected = when (either) {
        is Left -> 0
        is Right -> 1
      }
      effect shouldBe expected
      res shouldBe either
    }
  }

  @Test
  fun tapLeftAppliesEffects() = runTest {
    checkAll(Arb.either(Arb.long(), Arb.int())) { either ->
      var effect = 0
      val res = either.onLeft { effect += 1 }
      val expected = when (either) {
        is Left -> 1
        is Right -> 0
      }
      effect shouldBe expected
      res shouldBe either
    }
  }

  @Test
  fun foldOk() = runTest {
    checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
      val right: Either<Int, Int> = Right(a)
      val left: Either<Int, Int> = Left(b)

      right.fold({ it + 2 }, { it + 1 }) shouldBe a + 1
      left.fold({ it + 2 }, { it + 1 }) shouldBe b + 2
    }
  }

  @Test
  fun combineTwoRights() = runTest {
    checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
      Right(a + b) shouldBe Right(a).combine(
        Right(b),
        Int::plus,
        String::plus,
      )
    }
  }

  @Test
  fun combineTwoLefts() = runTest {
    checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
      Left(a + b) shouldBe Left(a).combine(
        Left(b),
        String::plus,
        Int::plus,
      )
    }
  }

  @Test
  fun combineRightLeft() = runTest {
    checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
      Left(a) shouldBe Left(a).combine(Right(b), String::plus, String::plus)
      Left(a) shouldBe Right(b).combine(Left(a), String::plus, String::plus)
    }
  }

  @Test
  fun getOrElseOk() = runTest {
    checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
      Right(a).getOrElse { b } shouldBe a
      Left(a).getOrElse { b } shouldBe b
    }
  }

  @Test
  fun getOrNullOk() = runTest {
    checkAll(Arb.int()) { a: Int ->
      Right(a).getOrNull() shouldBe a
    }
  }

  @Test
  fun getOrNoneRight() = runTest {
    checkAll(Arb.int()) { a: Int ->
      Right(a).getOrNone() shouldBe Some(a)
    }
  }

  @Test
  fun getOrNoneLeft() = runTest {
    checkAll(Arb.string()) { a: String ->
      Left(a).getOrNone() shouldBe None
    }
  }

  @Test
  fun swapOk() = runTest {
    checkAll(Arb.int()) { a: Int ->
      Left(a).swap() shouldBe Right(a)
      Right(a).swap() shouldBe Left(a)
    }
  }

  @Test
  fun mapOnlyRight() = runTest {
    checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
      val right: Either<Int, Int> = Right(a)
      val left: Either<Int, Int> = Left(b)

      right.map { it + 1 } shouldBe Right(a + 1)
      left.map { it + 1 } shouldBe left
    }
  }

  @Test
  fun mapLeftOnlyLeft() = runTest {
    checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
      val right: Either<Int, Int> = Right(a)
      val left: Either<Int, Int> = Left(b)

      right.mapLeft { it + 1 } shouldBe right
      left.mapLeft { it + 1 } shouldBe Left(b + 1)
    }
  }

  @Test
  fun flatMapOnlyRight() = runTest {
    checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
      val right: Either<Int, Int> = Right(a)
      val left: Either<Int, Int> = Left(b)

      right.flatMap { Right(it + 1) } shouldBe Right(a + 1)
      left.flatMap { Right(it + 1) } shouldBe left
    }
  }

  @Test
  fun handleErrorWithOk() = runTest {
    checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
      Left(a).recover<Int, String, String> { Right(b).bind() } shouldBe Right(b)
      Right(a).recover { Right(a + 1).bind() } shouldBe Right(a)
      Left(a).recover { Left(b).bind() } shouldBe Left(b)
    }
  }

  @Test
  fun catchRight() = runTest {
    checkAll(Arb.int()) { a ->
      Either.catch { a } shouldBe Right(a)
    }
  }

  @Test
  fun catchLeft() = runTest {
    val exception = Exception("Boom!")
    Either.catch { throw exception } shouldBe Left(exception)
  }

  @Test
  fun zipOrAccumulateCombine9() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
      Arb.either(Arb.string(), Arb.boolean()),
    ) { a, b, c, d, e, f, g, h, i ->
      val expected = listOf(a, b, c, d, e, f, g, h, i).simpleFlattenOrAccumulateCombine().map { it.shouldHave9Elements() }
      Either.zipOrAccumulate(String::plus, a, b, c, d, e, f, g, h, i, ::Tuple9) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulate9() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
      Arb.either(Arb.string(), Arb.boolean()),
    ) { a, b, c, d, e, f, g, h, i ->
      val expected = listOf(a, b, c, d, e, f, g, h, i).simpleFlattenOrAccumulate().map { it.shouldHave9Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, f, g, h, i, ::Tuple9) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateEitherNel9() = runTest {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.int()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.long()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.float()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.double()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.char()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.string()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.boolean()),
    ) { a, b, c, d, e, f, g, h, i ->
      val expected = listOf(a, b, c, d, e, f, g, h, i).simpleFlattenOrAccumulateNel().map { it.shouldHave9Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, f, g, h, i, ::Tuple9) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateCombine2() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
    ) { a, b ->
      val expected = listOf(a, b).simpleFlattenOrAccumulateCombine().map { it.shouldHave2Elements() }
      Either.zipOrAccumulate(String::plus, a, b, ::Pair) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateCombine3() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
    ) { a, b, c ->
      val expected = listOf(a, b, c).simpleFlattenOrAccumulateCombine().map { it.shouldHave3Elements() }
      Either.zipOrAccumulate(String::plus, a, b, c, ::Triple) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateCombine4() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
    ) { a, b, c, d ->
      val expected = listOf(a, b, c, d).simpleFlattenOrAccumulateCombine().map { it.shouldHave4Elements() }
      Either.zipOrAccumulate(String::plus, a, b, c, d, ::Tuple4) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateCombine5() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
    ) { a, b, c, d, e ->
      val expected = listOf(a, b, c, d, e).simpleFlattenOrAccumulateCombine().map { it.shouldHave5Elements() }
      Either.zipOrAccumulate(String::plus, a, b, c, d, e, ::Tuple5) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateCombine6() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
    ) { a, b, c, d, e, f ->
      val expected = listOf(a, b, c, d, e, f).simpleFlattenOrAccumulateCombine().map { it.shouldHave6Elements() }
      Either.zipOrAccumulate(String::plus, a, b, c, d, e, f, ::Tuple6) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateCombine7() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
    ) { a, b, c, d, e, f, g ->
      val expected = listOf(a, b, c, d, e, f, g).simpleFlattenOrAccumulateCombine().map { it.shouldHave7Elements() }
      Either.zipOrAccumulate(String::plus, a, b, c, d, e, f, g, ::Tuple7) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateCombine8() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
    ) { a, b, c, d, e, f, g, h ->
      val expected = listOf(a, b, c, d, e, f, g, h).simpleFlattenOrAccumulateCombine().map { it.shouldHave8Elements() }
      Either.zipOrAccumulate(String::plus, a, b, c, d, e, f, g, h, ::Tuple8) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulate2() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
    ) { a, b ->
      val expected = listOf(a, b).simpleFlattenOrAccumulate().map { it.shouldHave2Elements() }
      Either.zipOrAccumulate(a, b, ::Pair) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulate3() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
    ) { a, b, c ->
      val expected = listOf(a, b, c).simpleFlattenOrAccumulate().map { it.shouldHave3Elements() }
      Either.zipOrAccumulate(a, b, c, ::Triple) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulate4() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
    ) { a, b, c, d ->
      val expected = listOf(a, b, c, d).simpleFlattenOrAccumulate().map { it.shouldHave4Elements() }
      Either.zipOrAccumulate(a, b, c, d, ::Tuple4) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulate5() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
    ) { a, b, c, d, e ->
      val expected = listOf(a, b, c, d, e).simpleFlattenOrAccumulate().map { it.shouldHave5Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, ::Tuple5) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulate6() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
    ) { a, b, c, d, e, f ->
      val expected = listOf(a, b, c, d, e, f).simpleFlattenOrAccumulate().map { it.shouldHave6Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, f, ::Tuple6) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulate7() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
    ) { a, b, c, d, e, f, g ->
      val expected = listOf(a, b, c, d, e, f, g).simpleFlattenOrAccumulate().map { it.shouldHave7Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, f, g, ::Tuple7) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulate8() = runTest {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
    ) { a, b, c, d, e, f, g, h ->
      val expected = listOf(a, b, c, d, e, f, g, h).simpleFlattenOrAccumulate().map { it.shouldHave8Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, f, g, h, ::Tuple8) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateEitherNel2() = runTest {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
    ) { a, b ->
      val expected = listOf(a, b).simpleFlattenOrAccumulateNel().map { it.shouldHave2Elements() }
      Either.zipOrAccumulate(a, b, ::Pair) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateEitherNel3() = runTest {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.int()),
    ) { a, b, c ->
      val expected = listOf(a, b, c).simpleFlattenOrAccumulateNel().map { it.shouldHave3Elements() }
      Either.zipOrAccumulate(a, b, c, ::Triple) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateEitherNel4() = runTest {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.int()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.long()),
    ) { a, b, c, d ->
      val expected = listOf(a, b, c, d).simpleFlattenOrAccumulateNel().map { it.shouldHave4Elements() }
      Either.zipOrAccumulate(a, b, c, d, ::Tuple4) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateEitherNel5() = runTest {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.int()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.long()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.float()),
    ) { a, b, c, d, e ->
      val expected = listOf(a, b, c, d, e).simpleFlattenOrAccumulateNel().map { it.shouldHave5Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, ::Tuple5) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateEitherNel6() = runTest {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.int()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.long()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.float()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.double()),
    ) { a, b, c, d, e, f ->
      val expected = listOf(a, b, c, d, e, f).simpleFlattenOrAccumulateNel().map { it.shouldHave6Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, f, ::Tuple6) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateEitherNel7() = runTest {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.int()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.long()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.float()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.double()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.char()),
    ) { a, b, c, d, e, f, g ->
      val expected = listOf(a, b, c, d, e, f, g).simpleFlattenOrAccumulateNel().map { it.shouldHave7Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, f, g, ::Tuple7) shouldBe expected
    }
  }

  @Test
  fun zipOrAccumulateEitherNel8() = runTest {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.int()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.long()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.float()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.double()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.char()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.string()),
    ) { a, b, c, d, e, f, g, h ->
      val expected = listOf(a, b, c, d, e, f, g, h).simpleFlattenOrAccumulateNel().map { it.shouldHave8Elements() }
      Either.zipOrAccumulate(a, b, c, d, e, f, g, h, ::Tuple8) shouldBe expected
    }
  }

  @Test
  fun leftIsLeftPredicate() = runTest {
    checkAll(Arb.either(Arb.int(), Arb.boolean()), Arb.int()) { e, cmp ->
      fun func(i: Int) = i > cmp
      val expected = e.fold(::func) { false }
      e.isLeft(::func) shouldBe expected
    }
  }

  @Test
  fun rightIsRightPredicate() = runTest {
    checkAll(Arb.either(Arb.boolean(), Arb.int()), Arb.int()) { e, cmp ->
      fun func(i: Int) = i > cmp
      val expected = e.fold({ false }, ::func)
      e.isRight(::func) shouldBe expected
    }
  }

  @Test
  fun leftRightToStringContainsValue() = runTest {
    checkAll(100, Arb.either(Arb.string(), Arb.int())) { e ->
      val expected = e.fold({ it }, { it.toString() })
      e.toString() shouldContain expected
    }
  }

  @Test
  fun toIor() = runTest {
    checkAll(Arb.either(Arb.string(), Arb.int())) { e ->
      val expected = when (e) {
        is Left -> e.value.leftIor()
        is Right -> e.value.rightIor()
      }
      e.toIor() shouldBe expected
    }
  }

  @Test
  fun catchOrThrow() = runTest {
    data class TestThrowable(override val message: String) : Throwable()

    checkAll(20, Arb.either(Arb.string(), Arb.int())) { e ->
      val expected = e.mapLeft { TestThrowable(it) }
      Either.catchOrThrow<TestThrowable, Int> {
        e.fold({ throw TestThrowable(it) }) { it }
      } shouldBe expected
    }
  }

  @Test
  fun catchOrThrowRuntimeException() = runTest {
    checkAll(20, Arb.int(-2..2)) { a ->
      fun func() = 1001 / a
      val res = Either.catchOrThrow<Throwable, Int>(::func)

      when (res) {
        is Left -> {
          res.value::class shouldBe ArithmeticException::class
        }

        is Right -> {
          res.value shouldBe func()
        }
      }
    }
  }

  @Test
  fun catchOrThrowDoesntCatchCancellationException() = runTest {
    checkAll(20, Arb.either(Arb.string(), Arb.int())) { e ->
      try {
        val res = Either.catchOrThrow<CancellationException, Int> {
          e.fold({ throw CancellationException(it) }) { it }
        }
        res.fold(
          { fail("Should not catch CancellationException") },
          { it shouldBe e.getOrNull() ; Unit },
        )
      } catch (e: Throwable) {
        e::class shouldBe CancellationException::class
      }
    }
  }

  @Test
  fun handleErrorWith() = runTest {
    fun func(i: Int) = (i + 1).left()

    checkAll(Arb.either(Arb.int(), Arb.int())) { e ->
      val expected = e.fold(::func, { e })
      e.handleErrorWith(::func) shouldBe expected
    }
  }

  @Test
  fun flatten() = runTest {
    checkAll(Arb.either(Arb.string(), Arb.either(Arb.string(), Arb.int()))) { e ->
      e.flatten().let {
        when (e) {
          is Left -> {
            it shouldBe e
          }
          is Right -> {
            it shouldBe e.value
          }
        }
      }
    }
  }

  @Test
  fun compareTo() = runTest {
    checkAll(Arb.either(Arb.string(), Arb.int()), Arb.either(Arb.string(), Arb.int())) { a, b ->

      val expected = when (a) {
        is Left -> {
          when (b) {
            is Left -> {
              a.compareTo(b)
            }
            is Right -> { // Left is lesser than Right
              -1
            }
          }
        }
        is Right -> {
          when (b) {
            is Left -> { // Right is greater than Left
              1
            }
            is Right -> {
              a.compareTo(b)
            }
          }
        }
      }

      a.compareTo(b) shouldBe expected
    }
  }

  @Test
  fun toEitherNel() = runTest {
    checkAll(Arb.either(Arb.string(), Arb.int())) { e ->
      e.toEitherNel() shouldBe e.mapLeft { it.nel() }
    }
  }

  @Test
  fun validateWorks() = runTest {
    checkAll(Arb.either(Arb.string(), Arb.int())) { e ->
      e.validate { /* nothing */ } shouldBe e
      e.validate { raise("problem") }.shouldBeInstanceOf<Left<String>>()
      e.validate { ensure(it > 0) { "negative" } } shouldBe when (e) {
        is Right -> if (e.value > 0) e else "negative".left()
        is Left -> e
      }
    }
  }
}

private fun List<*>.shouldHave2Elements(): Pair<*, *> {
  shouldHaveSize(2)
  return this[0] to this[1]
}

private fun List<*>.shouldHave3Elements(): Triple<*, *, *> {
  shouldHaveSize(3)
  return Triple(this[0], this[1], this[2])
}

private fun List<*>.shouldHave4Elements(): Tuple4<*, *, *, *> {
  shouldHaveSize(4)
  return Tuple4(this[0], this[1], this[2], this[3])
}

private fun List<*>.shouldHave5Elements(): Tuple5<*, *, *, *, *> {
  shouldHaveSize(5)
  return Tuple5(this[0], this[1], this[2], this[3], this[4])
}

private fun List<*>.shouldHave6Elements(): Tuple6<*, *, *, *, *, *> {
  shouldHaveSize(6)
  return Tuple6(this[0], this[1], this[2], this[3], this[4], this[5])
}

private fun List<*>.shouldHave7Elements(): Tuple7<*, *, *, *, *, *, *> {
  shouldHaveSize(7)
  return Tuple7(this[0], this[1], this[2], this[3], this[4], this[5], this[6])
}

private fun List<*>.shouldHave8Elements(): Tuple8<*, *, *, *, *, *, *, *> {
  shouldHaveSize(8)
  return Tuple8(this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7])
}

private fun List<*>.shouldHave9Elements(): Tuple9<*, *, *, *, *, *, *, *, *> {
  shouldHaveSize(9)
  return Tuple9(this[0], this[1], this[2], this[3], this[4], this[5], this[6], this[7], this[8])
}
