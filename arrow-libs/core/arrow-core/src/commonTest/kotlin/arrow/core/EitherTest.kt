package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.test.UnitSpec
import arrow.core.test.generators.*
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

class EitherTest : UnitSpec() {

  private val ARB = Arb.either(Arb.string(), Arb.int())

  init {
    testLaws(
      MonoidLaws.laws(Monoid.either(Monoid.string(), Monoid.int()), ARB),
      /*FxLaws.suspended<EitherEffect<String, *>, Either<String, Int>, Int>(
        Arb.int().map(::Right),
        ARB.map { it },
        Either<String, Int>::equals,
        either::invoke
      ) {
        it.bind()
      },
      FxLaws.eager<RestrictedEitherEffect<String, *>, Either<String, Int>, Int>(
        Arb.int().map(::Right),
        ARB.map { it },
        Either<String, Int>::equals,
        either::eager
      ) {
        it.bind()
      }*/
    )

    "isLeft should return true if Left and false if Right" {
      checkAll(Arb.int()) { a: Int ->
        Left(a).isLeft() && !Right(a).isLeft()
      }
    }

    "isRight should return false if Left and true if Right" {
      checkAll(Arb.int()) { a: Int ->
        !Left(a).isRight() && Right(a).isRight()
      }
    }

    "tap applies effects returning the original value" {
      checkAll(Arb.either(Arb.long(), Arb.int())) { either ->
        var effect = 0
        val res = either.tap { effect += 1 }
        val expected = when (either) {
          is Left -> 0
          is Right -> 1
        }
        effect shouldBe expected
        res shouldBe either
      }
    }

    "tapLeft applies effects returning the original value" {
      checkAll(Arb.either(Arb.long(), Arb.int())) { either ->
        var effect = 0
        val res = either.tapLeft { effect += 1 }
        val expected = when (either) {
          is Left -> 1
          is Right -> 0
        }
        effect shouldBe expected
        res shouldBe either
      }
    }

    "fold should apply first op if Left and second op if Right" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)

        right.fold({ it + 2 }, { it + 1 }) shouldBe a + 1
        left.fold({ it + 2 }, { it + 1 }) shouldBe b + 2
      }
    }

    "foldLeft should return initial if Left and apply op if Right" {
      checkAll<Int, Int, Int>(Arb.intSmall(), Arb.intSmall(), Arb.intSmall()) { a, b, c ->
        Right(a).foldLeft(c, Int::plus) shouldBe c + a
        Left(b).foldLeft(c, Int::plus) shouldBe c
      }
    }

    "foldMap should return the empty of the inner type if Left and apply op if Right" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val left: Either<Int, Int> = Left(b)

        Right(a).foldMap(Monoid.int()) { it + 1 } shouldBe a + 1
        left.foldMap(Monoid.int()) { it + 1 } shouldBe Monoid.int().empty()
      }
    }

    "bifoldLeft should apply first op if Left and apply second op if Right" {
      checkAll(Arb.intSmall(), Arb.intSmall(), Arb.intSmall()) { a, b, c ->
        Right(a).bifoldLeft(c, Int::plus, Int::times) shouldBe a * c
        Left(b).bifoldLeft(c, Int::plus, Int::times) shouldBe b + c
      }
    }

    "bifoldMap should apply first op if Left and apply second op if Right" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)

        right.bifoldMap(Monoid.int(), { it + 2 }, { it + 1 }) shouldBe a + 1
        left.bifoldMap(Monoid.int(), { it + 2 }, { it + 1 }) shouldBe b + 2
      }
    }

    "fromNullable should lift value as a Right if it is not null" {
      checkAll(Arb.int()) { a: Int ->
        Either.fromNullable(a) shouldBe Right(a)
      }
    }

    "fromNullable should lift value as a Left(Unit) if it is null" {
      Either.fromNullable(null) shouldBe Left(Unit)
    }

    "empty should return a Right of the empty of the inner type" {
      Right(Monoid.string().empty()) shouldBe Monoid.either(Monoid.string(), Monoid.string()).empty()
    }

    "combine two rights should return a right of the combine of the inners" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        Monoid.string().run { Right(a.combine(b)) } shouldBe Right(a).combine(
          Monoid.string(),
          Monoid.string(),
          Right(b)
        )
      }
    }

    "combine two lefts should return a left of the combine of the inners" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        Monoid.string().run { Left(a.combine(b)) } shouldBe Left(a).combine(Monoid.string(), Monoid.string(), Left(b))
      }
    }

    "combine a right and a left should return left" {
      checkAll(Arb.string(), Arb.string()) { a: String, b: String ->
        Left(a) shouldBe Left(a).combine(Monoid.string(), Monoid.string(), Right(b))
        Left(a) shouldBe Right(b).combine(Monoid.string(), Monoid.string(), Left(a))
      }
    }

    "getOrElse should return value" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        Right(a).getOrElse { b } shouldBe a
        Left(a).getOrElse { b } shouldBe b
      }
    }

    "orNull should return value" {
      checkAll(Arb.int()) { a: Int ->
        Right(a).orNull() shouldBe a
      }
    }

    "orNone should return Some(value)" {
      checkAll(Arb.int()) { a: Int ->
        Right(a).orNone() shouldBe Some(a)
      }
    }

    "orNone should return None when left" {
      checkAll(Arb.string()) { a: String ->
        Left(a).orNone() shouldBe None
      }
    }

    "getOrHandle should return value" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        Right(a).getOrHandle { b } shouldBe a
        Left(a).getOrHandle { it + b } shouldBe a + b
      }
    }

    "filterOrElse should filter values" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val left: Either<Int, Int> = Left(a)

        Right(a).filterOrElse({ it > a - 1 }, { b }) shouldBe Right(a)
        Right(a).filterOrElse({ it > a + 1 }, { b }) shouldBe Left(b)
        left.filterOrElse({ it > a - 1 }, { b }) shouldBe Left(a)
        left.filterOrElse({ it > a + 1 }, { b }) shouldBe Left(a)
      }
    }

    "filterOrOther should filter values" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val left: Either<Int, Int> = Left(a)

        Right(a).filterOrOther({ it > a - 1 }, { b + a }) shouldBe Right(a)
        Right(a).filterOrOther({ it > a + 1 }, { b + a }) shouldBe Left(b + a)
        left.filterOrOther({ it > a - 1 }, { b + a }) shouldBe Left(a)
        left.filterOrOther({ it > a + 1 }, { b + a }) shouldBe Left(a)
      }
    }

    "leftIfNull should return Left if Right value is null of if Either is Left" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        Right(a).leftIfNull { b } shouldBe Right(a)
        Right(null).leftIfNull { b } shouldBe Left(b)
        Left(a).leftIfNull { b } shouldBe Left(a)
      }
    }

    "exists should apply predicate to Right only" {
      checkAll(Arb.intSmall()) { a ->
        val left: Either<Int, Int> = Left(a)

        Right(a).exists { it > a - 1 } shouldBe true
        !Right(a).exists { it > a + 1 } shouldBe true
        !left.exists { it > a - 1 } shouldBe true
        !left.exists { it > a + 1 } shouldBe true
      }
    }

    "rightIfNotNull should return Left if value is null or Right of value when not null" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        null.rightIfNotNull { b } shouldBe Left(b)
        a.rightIfNotNull { b } shouldBe Right(a)
      }
    }

    "rightIfNull should return Left if value is not null or Right of value when null" {
      checkAll(Arb.int(), Arb.int()) { a: Int, b: Int ->
        a.rightIfNull { b } shouldBe Left(b)
        null.rightIfNull { b } shouldBe Right(null)
      }
    }

    "swap should interchange values" {
      checkAll(Arb.int()) { a: Int ->
        Left(a).swap() shouldBe Right(a)
        Right(a).swap() shouldBe Left(a)
      }
    }

    "orNull should convert" {
      checkAll(Arb.int()) { a: Int ->
        val left: Either<Int, Int> = Left(a)

        Right(a).orNull() shouldBe a
        left.orNull() shouldBe null
      }
    }

    "contains should check value" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        require(Right(a).contains(a)) { "Expected ${Right(a)}.contains($a) to be true, but it was false." }
        if (a != b) require(!Right(a).contains(b)) { "Expected ${Right(a)}.contains($b) to be false, but it was true." }
        else require(Right(a).contains(b)) { "Expected ${Right(a)}.contains($b) to be true, but it was false." }
        require(!Left(a).contains(a)) { "Expected ${Left(a)}.contains($a) to be false, but it was true." }
      }
    }

    "map should alter right instance only" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)

        right.map { it + 1 } shouldBe Right(a + 1)
        left.map { it + 1 } shouldBe left
      }
    }

    "mapLeft should alter left instance only" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)

        right.mapLeft { it + 1 } shouldBe right
        left.mapLeft { it + 1 } shouldBe Left(b + 1)
      }
    }

    "bimap should alter left or right instance accordingly" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)

        right.bimap({ it + 2 }, { it + 1 }) shouldBe Right(a + 1)
        left.bimap({ it + 2 }, { it + 1 }) shouldBe Left(b + 2)
      }
    }

    "replicate should return Right(empty list) when n <= 0" {
      checkAll(
        Arb.nonPositiveInt(),
        Arb.int(0..100)
      ) { n: Int, a: Int ->
        val expected: Either<Int, List<Int>> = Right(emptyList())

        Right(a).replicate(n) shouldBe expected
        Left(a).replicate(n) shouldBe expected
      }
    }

    "replicate should return Right(list of repeated value size n) when Right and n is positive" {
      checkAll(
        Arb.int(1..10),
        Arb.int()
      ) { n: Int, a: Int ->
        Right(a).replicate(n) shouldBe Right(List(n) { a })
        Left(a).replicate(n) shouldBe Left(a)
      }
    }

    "traverse should return list of Right when Right and empty list when Left" {
      checkAll(
        Arb.int(),
        Arb.int(),
        Arb.int()
      ) { a: Int, b: Int, c: Int ->
        Right(a).traverse { emptyList<Int>() } shouldBe emptyList<Int>()
        Right(a).traverse { listOf(b, c) } shouldBe listOf(Right(b), Right(c))
        Left(a).traverse { listOf(b, c) } shouldBe emptyList<Int>()
      }
    }

    "flatMap should map right instance only" {
      checkAll(Arb.intSmall(), Arb.intSmall()) { a, b ->
        val right: Either<Int, Int> = Right(a)
        val left: Either<Int, Int> = Left(b)

        right.flatMap { Right(it + 1) } shouldBe Right(a + 1)
        left.flatMap { Right(it + 1) } shouldBe left
      }
    }

    "conditionally should create right instance only if test is true" {
      checkAll(Arb.boolean(), Arb.int(), Arb.string()) { t: Boolean, i: Int, s: String ->
        val expected = if (t) Right(i) else Left(s)
        Either.conditionally(t, { s }, { i }) shouldBe expected
      }
    }

    "handleErrorWith should handle left instance otherwise return Right" {
      checkAll(Arb.int(), Arb.string()) { a: Int, b: String ->
        Left(a).handleErrorWith { Right(b) } shouldBe Right(b)
        Right(a).handleErrorWith { Right(b) } shouldBe Right(a)
        Left(a).handleErrorWith { Left(b) } shouldBe Left(b)
      }
    }

    "catch should return Right(result) when f does not throw" {
      Either.catch { 1 } shouldBe Right(1)
    }

    "catch should return Left(result) when f throws" {
      val exception = Exception("Boom!")
      Either.catch { throw exception } shouldBe Left(exception)
    }

    "catchAndFlatten should return Right(result) when f does not throw" {
      Either.catchAndFlatten { Right(1) } shouldBe Right(1)
    }

    "catchAndFlatten should return Left(result) when f throws" {
      val exception = Exception("Boom!")
      Either.catchAndFlatten<Int> { throw exception } shouldBe Left(exception)
    }

    "resolve should yield a result when deterministic functions are used as handlers" {
      checkAll(
        Arb.suspendFunThatReturnsEitherAnyOrAnyOrThrows(),
        Arb.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->
        val result =
          Either.resolve(
            f = { f() },
            success = { a -> handleWithPureFunction(a, returnObject) },
            error = { e -> handleWithPureFunction(e, returnObject) },
            throwable = { t -> handleWithPureFunction(t, returnObject) },
            unrecoverableState = { handleWithPureFunction(it) }
          )
        result shouldBe returnObject
      }
    }

    "resolve should yield a result when an exception is thrown in the success supplied function" {
      checkAll(
        Arb.suspendFunThatReturnsAnyRight(),
        Arb.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->
        val result =
          Either.resolve(
            f = { f() },
            success = { throwException(it) },
            error = { e -> handleWithPureFunction(e, returnObject) },
            throwable = { t -> handleWithPureFunction(t, returnObject) },
            unrecoverableState = { handleWithPureFunction(it) }
          )
        result shouldBe returnObject
      }
    }

    "resolve should yield a result when an exception is thrown in the error supplied function" {
      checkAll(
        Arb.suspendFunThatReturnsAnyLeft(),
        Arb.any()
      ) { f: suspend () -> Either<Any, Any>, returnObject: Any ->
        val result =
          Either.resolve(
            f = { f() },
            success = { a -> handleWithPureFunction(a, returnObject) },
            error = { throwException(it) },
            throwable = { t -> handleWithPureFunction(t, returnObject) },
            unrecoverableState = { handleWithPureFunction(it) }
          )
        result shouldBe returnObject
      }
    }

    "resolve should throw a Throwable when any exception is thrown in the throwable supplied function" {
      checkAll(
        Arb.suspendFunThatThrows()
      ) { f: suspend () -> Either<Any, Any> ->
        shouldThrow<Throwable> {
          Either.resolve(
            f = { f() },
            success = { throwException(it) },
            error = { throwException(it) },
            throwable = { throwException(it) },
            unrecoverableState = { handleWithPureFunction(it) }
          )
        }
      }
    }

    "traverse should return list if either is right" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")

      right.traverse { listOf(it, 2, 3) } shouldBe listOf(Right(1), Right(2), Right(3))
      left.traverse { listOf(it, 2, 3) } shouldBe emptyList()
    }

    "sequence should be consistent with traverse" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.map { listOf(it) }.sequence() shouldBe either.traverse { listOf(it) }
      }
    }

    "traverseNullable should return non-nullable if either is right" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")

      right.traverseNullable { it } shouldBe Right(1)
      right.traverseNullable { null } shouldBe null
      left.traverseNullable { it } shouldBe null
    }

    "sequence for Nullable should be consistent with traverseNullable" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.map { it }.sequence() shouldBe either.traverseNullable { it }
        either.map { null }.sequence() shouldBe null
      }
    }

    "traverse for Option should return option if either is right" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")

      right.traverse { Some(it) } shouldBe Some(Right(1))
      left.traverse { Some(it) } shouldBe None
    }

    "sequence for Option should be consistent with traverseOption" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.map { Some(it) }.sequence() shouldBe either.traverse { Some(it) }
      }
    }

    "bitraverse should wrap either in a list" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")

      right.bitraverse({ listOf(it, "bar", "baz") }, { listOf(it, 2, 3) }) shouldBe listOf(Right(1), Right(2), Right(3))
      left.bitraverse({ listOf(it, "bar", "baz") }, { listOf(it, 2, 3) }) shouldBe
        listOf(Left("foo"), Left("bar"), Left("baz"))
    }

    "bisequence should be consistent with bitraverse" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.bimap({ listOf(it) }, { listOf(it) }).bisequence() shouldBe either.bitraverse(
          { listOf(it) },
          { listOf(it) })
      }
    }

    "bitraverseNullable should wrap either in a nullable" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")

      right.bitraverseNullable({ it }, { it.toString() }) shouldBe Right("1")
      left.bitraverseNullable({ it }, { it.toString() }) shouldBe Left("foo")

      right.bitraverseNullable({ it }, { null }) shouldBe null
      left.bitraverseNullable({ null }, { it.toString() }) shouldBe null
    }

    "bisequenceNullable should be consistent with bitraverseNullable" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.bimap({ it }, { it }).bisequenceNullable() shouldBe
          either.bitraverseNullable({ it }, { it })
      }
    }

    "bitraverseOption should wrap either in an option" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")

      right.bitraverseOption({ Some(it) }, { Some(it.toString()) }) shouldBe Some(Right("1"))
      left.bitraverseOption({ Some(it) }, { Some(it.toString()) }) shouldBe Some(Left("foo"))
    }

    "bisequenceOption should be consistent with bitraverseOption" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.bimap({ Some(it) }, { Some(it) }).bisequenceOption() shouldBe
          either.bitraverseOption({ Some(it) }, { Some(it) })
      }
    }

    "Cartesian builder should build products over homogeneous Either" {
      Either.Right("11th").zip(
        Semigroup.string(),
        Either.Right("Doctor"),
        Either.Right("Who"),
      ) { a, b, c -> "$a $b $c" } shouldBe Either.Right("11th Doctor Who")
    }

    "Cartesian builder should build products over heterogeneous Either" {
      Either.Right(13).zip(
        Semigroup.string(),
        Either.Right("Doctor"),
        Either.Right(false),
      ) { a, b, c -> "${a}th $b is $c" } shouldBe Either.Right("13th Doctor is false")
    }

    "Cartesian builder should build products over Either.Left" {
      Either.Left("fail1").zip(
        Semigroup.string(),
        Either.Left("fail2"),
        Either.Right("Who")
      ) { _, _, _ -> "success!" } shouldBe Either.Left("fail1fail2")
    }

    "Cartesian builder for nel doesn't need semigroup parameter" {
      "fail1".leftNel()
        .zip("fail2".leftNel()
        ) { _,_ -> "success!"} shouldBe Left(nonEmptyListOf("fail1", "fail2"))
    }

    "zip should return Right(f(a)) if both are Right" {
      Right(10).zip(Semigroup.int(), Either.Right { a: Int -> a + 5 }) { a, ff -> ff(a) } shouldBe Either.Right(15)
    }

    "zip should return first Left found if is unique or combine both otherwise" {
      Left(10).zip(Semigroup.int(), Right { a: Int -> a + 5 }) { a, ff -> ff(a) } shouldBe Left(10)
      Right(10).zip<Int, Int, (Int) -> Int, Int>(
        Semigroup.int(),
        Either.Left(5)
      ) { a, ff -> ff(a) } shouldBe Either.Left(5)
      Left(10).zip<Int, Int, (Int) -> Int, Int>(
        Semigroup.int(),
        Either.Left(5)
      ) { a, ff -> ff(a) } shouldBe Either.Left(15)
    }
  }
}

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
suspend fun handleWithPureFunction(a: Any, b: Any): Either<Throwable, Any> =
  b.right()

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
suspend fun handleWithPureFunction(throwable: Throwable): Either<Throwable, Unit> =
  Unit.right()

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
private suspend fun <A> throwException(
  a: A
): Either<Throwable, Any> =
  throw RuntimeException("An Exception is thrown while handling the result of the supplied function.")
