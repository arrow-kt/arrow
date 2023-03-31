package arrow.core

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.test.any
import arrow.core.test.either
import arrow.core.test.intSmall
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.nonEmptyList
import arrow.core.test.suspendFunThatReturnsAnyLeft
import arrow.core.test.suspendFunThatReturnsAnyRight
import arrow.core.test.suspendFunThatReturnsEitherAnyOrAnyOrThrows
import arrow.core.test.suspendFunThatThrows
import arrow.core.test.testLaws
import arrow.typeclasses.Monoid
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.nonPositiveInt
import io.kotest.property.arbitrary.short
import io.kotest.property.checkAll

class EitherTest : StringSpec({
  
  val ARB = Arb.either(Arb.string(), Arb.int())

    testLaws(
      MonoidLaws(0.right(), { x, y -> x.combine(y, String::plus, Int::plus) }, ARB)
    )
    
    "isLeft should return true if Left and false if Right" {
      checkAll(Arb.int()) { a: Int ->
        val x = Left(a)
        if (x.isLeft()) x.value shouldBe a
        else fail("Left(a).isLeft() cannot be false")
        x.isRight() shouldBe false
      }
    }
    
    "isRight should return false if Left and true if Right" {
      checkAll(Arb.int()) { a: Int ->
        val x = Right(a)
        if (x.isRight()) x.value shouldBe a
        else fail("Right(a).isRight() cannot be false")
        x.isLeft() shouldBe false
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
    
    "traverse should return list of Right when Right and singleton list when Left" {
      checkAll(
        Arb.int(),
        Arb.int(),
        Arb.int()
      ) { a: Int, b: Int, c: Int ->
        Right(a).traverse { emptyList<Int>() } shouldBe emptyList<Int>()
        Right(a).traverse { listOf(b, c) } shouldBe listOf(Right(b), Right(c))
        Left(a).traverse { listOf(b, c) } shouldBe listOf(Left(a))
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

    "combine should combine 2 eithers" {
      checkAll(Arb.either(Arb.string(), Arb.int()), Arb.either(Arb.string(), Arb.int())) { e1, e2 ->
        val obtained = e1.combine(e2, { l1, l2 -> l1 + l2 }, { r1, r2 -> r1 + r2 })
        val expected = when(e1){
          is Left -> when(e2) {
            is Left -> Left(e1.value + e2.value)
            is Right -> e1
          }
          is Right -> when(e2) {
            is Left -> e2
            is Right -> Right(e1.value + e2.value)
          }
        }
        obtained shouldBe expected
      }
    }


  "combineAll replacement should work " {
    checkAll(Arb.list(Arb.either(Arb.string(), Arb.int()))) { list ->
      val obtained = list.fold<Either<String, Int>, Either<String, Int>>(0.right()) { x, y ->
        Either.zipOrAccumulate<String, Int, Int, Int>(
          { a1, a2 -> a1 + a2 },
          x,
          y,
          { b1, b2 -> b1 + b2 })
      }
      val expected = list.combineAll(Monoid.string(), Monoid.int())
      obtained shouldBe expected
    }
  }



  "traverse should return list if either is right" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")
      
      right.traverse { listOf(it, 2, 3) } shouldBe listOf(Right(1), Right(2), Right(3))
      left.traverse { listOf(it, 2, 3) } shouldBe listOf(left)
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
      left.traverseNullable { it } shouldBe left
    }
    
    "sequence for Nullable should be consistent with traverseNullable" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.map { it }.sequence() shouldBe either.traverseNullable { it }
        // wrong! if you map a `Left`, you should get a `Left` back, not null
        // either.map { null }.sequence() shouldBe null
      }
    }
    
    "traverse for Option should return option if either is right" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")
      
      right.traverse { Some(it) } shouldBe Some(Right(1))
      right.traverse { None } shouldBe None
      left.traverse { Some(it) } shouldBe Some(left)
      left.traverse { None } shouldBe Some(left)
    }
    
    "sequence for Option should be consistent with traverseOption" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.map { Some(it) }.sequence() shouldBe either.traverse { Some(it) }
      }
    }
    
    "traverse for Validated should return validated of either" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")
      
      right.traverse { it.valid() } shouldBe Valid(Right(1))
      left.traverse { it.valid() } shouldBe Valid(Left("foo"))
    }
    
    "sequence for Validated should be consistent with traverseValidated" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.map { it.valid() }.sequence() shouldBe either.traverse { it.valid() }
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
    
    "bitraverseValidated should return validated of either" {
      val right: Either<String, Int> = Right(1)
      val left: Either<String, Int> = Left("foo")
      
      right.bitraverseValidated({ it.invalid() }, { it.valid() }) shouldBe Valid(Right(1))
      left.bitraverseValidated({ it.invalid() }, { it.valid() }) shouldBe Invalid("foo")
    }
    
    "bisequenceValidated should be consistent with bitraverseValidated" {
      checkAll(Arb.either(Arb.string(), Arb.int())) { either ->
        either.bimap({ it.invalid() }, { it.valid() }).bisequenceValidated() shouldBe
          either.bitraverseValidated({ it.invalid() }, { it.valid() })
      }
    }

  "zipOrAccumulate results in all Right transformed, or all Left combined according to combine" {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
      Arb.either(Arb.string(), Arb.boolean())
    ) { a, b, c, d, e, f, g, h, i ->
      val res = Either.zipOrAccumulate({ e1, e2 -> "$e1$e2" }, a, b, c, d, e, f, g, h, i, ::Tuple9)
      val all = listOf(a, b, c, d, e, f, g, h, i)

      val expected = if (all.any { it.isLeft() }) {
        all.filterIsInstance<Left<String>>().fold("") { acc, t -> "$acc${t.value}" }.left()
      } else {
        all.filterIsInstance<Right<Any?>>().map { it.value }.let {
          Tuple9(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8]).right()
        }
      }

      res shouldBe expected
    }
  }

  "zipOrAccumulate without Semigroup results in all Right transformed, or all Left in a NonEmptyList" {
    checkAll(
      Arb.either(Arb.string(), Arb.short()),
      Arb.either(Arb.string(), Arb.byte()),
      Arb.either(Arb.string(), Arb.int()),
      Arb.either(Arb.string(), Arb.long()),
      Arb.either(Arb.string(), Arb.float()),
      Arb.either(Arb.string(), Arb.double()),
      Arb.either(Arb.string(), Arb.char()),
      Arb.either(Arb.string(), Arb.string()),
      Arb.either(Arb.string(), Arb.boolean())
    ) { a, b, c, d, e, f, g, h, i ->
      val res = Either.zipOrAccumulate(a, b, c, d, e, f, g, h, i, ::Tuple9)
      val all = listOf(a, b, c, d, e, f, g, h, i)

      val expected = if (all.any { it.isLeft() }) {
        all.filterIsInstance<Left<String>>().map { it.value }.toNonEmptyListOrNull()!!.left()
      } else {
        all.filterIsInstance<Right<Any?>>().map { it.value }.let {
          Tuple9(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8]).right()
        }
      }

      res shouldBe expected
    }
  }

  "zipOrAccumulate EitherNel results in all Right transformed, or all Left in a NonEmptyList" {
    checkAll(
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.short()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.byte()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.int()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.long()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.float()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.double()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.char()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.string()),
      Arb.either(Arb.nonEmptyList(Arb.int()), Arb.boolean())
    ) { a, b, c, d, e, f, g, h, i ->
      val res = Either.zipOrAccumulate(a, b, c, d, e, f, g, h, i, ::Tuple9)
      val all = listOf(a, b, c, d, e, f, g, h, i)

      val expected = if (all.any { it.isLeft() }) {
        all.filterIsInstance<Left<NonEmptyList<String>>>()
          .flatMap { it.value }
          .toNonEmptyListOrNull()!!.left()
      } else {
        all.filterIsInstance<Right<Any?>>().map { it.value }.let {
          Tuple9(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8]).right()
        }
      }

      res shouldBe expected
    }
  }
})

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
suspend fun handleWithPureFunction(a: Any, b: Any): Either<Throwable, Any> =
  b.right()

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
suspend fun handleWithPureFunction(throwable: Throwable): Either<Throwable, Unit> =
  Unit.right()

@Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
private suspend fun <A> throwException(
  a: A,
): Either<Throwable, Any> =
  throw RuntimeException("An Exception is thrown while handling the result of the supplied function.")
