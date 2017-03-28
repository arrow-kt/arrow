package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith


@RunWith(KTestJUnitRunner::class)
class IorTest : UnitSpec() {
    init {
        "flatMap() should modify entity" {
            forAll { a: Int, b: String ->
                {
                    val intMonoidInstance: Semigroup<Int> = object : Semigroup<Int> {
                        override fun combine(a: Int, b: Int): Int = a
                    }
                    Ior.Right(b).flatMap(intMonoidInstance) { Ior.Left(a) } == Ior.Left(a) &&
                            Ior.Right(a).flatMap(intMonoidInstance) { Ior.Right(b) } == Ior.Right(b) &&
                            Ior.Left(a).flatMap(intMonoidInstance) { Ior.Right(b) } == Ior.Left(a) &&
                            Ior.Both(a, b).flatMap(intMonoidInstance) { Ior.Left(a) } == Ior.Left(intMonoidInstance.combine(a, a)) &&
                            Ior.Both(a, b).flatMap(intMonoidInstance) { Ior.Right(b) } == Ior.Right(b) &&
                            Ior.Both(a, b).flatMap(intMonoidInstance) { Ior.Both(a, b) } == Ior.Both(intMonoidInstance.combine(a, a), b)
                }()
            }
        }

        "flatMap() should combine with upper bound" {
            forAll { a: String, b: Int ->
                {
                    val numberMonoidInstance: Semigroup<Number> = object : Semigroup<Number> {
                        override fun combine(a: Number, b: Number): Number = a
                    }
                    val iorRightString: Ior<Int, String> = Ior.Right(a)
                    val iorLeftNumberAsUpperBoundOfInt: Ior<Number, String> = Ior.Left(b)
                    val iorResult: Ior<Number, String> = iorRightString.flatMap(numberMonoidInstance) { iorLeftNumberAsUpperBoundOfInt }
                    iorResult == iorLeftNumberAsUpperBoundOfInt
                }()
            }
        }

        "map() should modify only right value" {
            forAll { a: Int, b: String ->
                {
                    Ior.Right(b).map { a * 2 } == Ior.Right(a * 2) &&
                            Ior.Left(a).map { b } == Ior.Left(a) &&
                            Ior.Both(a, b).map { "power of $it" } == Ior.Both(a, "power of $b")
                }()
            }
        }

        "bimap() should allow modify both value" {
            forAll { a: Int, b: String ->
                {
                    Ior.Right(b).bimap({ "5" }, { a * 2 }) == Ior.Right(a * 2) &&
                            Ior.Left(a).bimap({ a * 3 }, { "5" }) == Ior.Left(a * 3) &&
                            Ior.Both(a, b).bimap({ 2 }, { "power of $it" }) == Ior.Both(2, "power of $b")
                }()
            }
        }

        "mapLeft() should modify only left value" {
            forAll { a: Int, b: String ->
                {
                    Ior.Right(b).mapLeft { a * 2 } == Ior.Right(b) &&
                            Ior.Left(a).mapLeft { b } == Ior.Left(b) &&
                            Ior.Both(a, b).mapLeft { "power of $it" } == Ior.Both("power of $a", b)
                }()
            }
        }

        "swap() should interchange value" {
            forAll { a: Int, b: String ->
                {
                    Ior.Both(a, b).swap() == Ior.Both(b, a)
                }()
            }
        }

        "swap() should interchange entity" {
            forAll { a: Int ->
                {
                    Ior.Left(a).swap() == Ior.Right(a) &&
                            Ior.Right(a).swap() == Ior.Left(a)
                }()
            }
        }

        "unwrap() should return the isomorphic either" {
            forAll { a: Int, b: String ->
                {
                    Ior.Left(a).unwrap() == Either.Left(Either.Left(a)) &&
                            Ior.Right(b).unwrap() == Either.Left(Either.Right(b)) &&
                            Ior.Both(a, b).unwrap() == Either.Right(Pair(a, b))
                }()
            }
        }

        "pad() should return the correct Pair of Options" {
            forAll { a: Int, b: String ->
                {
                    Ior.Left(a).pad() == Pair(Option.Some(a), Option.None) &&
                            Ior.Right(b).pad() == Pair(Option.None, Option.Some(b)) &&
                            Ior.Both(a, b).pad() == Pair(Option.Some(a), Option.Some(b))
                }()
            }
        }

        "toEither() should convert values into a valid Either" {
            forAll { a: Int, b: String ->
                {
                    Ior.Left(a).toEither() == Either.Left(a) &&
                            Ior.Right(b).toEither() == Either.Right(b) &&
                            Ior.Both(a, b).toEither() == Either.Right(b)
                }()
            }
        }

        "toOption() should convert values into a valid Option" {
            forAll { a: Int, b: String ->
                {
                    Ior.Left(a).toOption() == Option.None &&
                            Ior.Right(b).toOption() == Option.Some(b) &&
                            Ior.Both(a, b).toOption() == Option.Some(b)
                }()
            }
        }

        "fromOption() should build a correct Option<Ior>" {
            forAll { a: Int, b: String ->
                {
                    Ior.fromOptions(Option.Some(a), Option.None) == Option.Some(Ior.Left(a)) &&
                            Ior.fromOptions(Option.Some(a), Option.Some(b)) == Option.Some(Ior.Both(a, b)) &&
                            Ior.fromOptions(Option.None, Option.Some(b)) == Option.Some(Ior.Right(b)) &&
                            Ior.fromOptions(Option.None, Option.None) == Option.None
                }()
            }
        }


        "getOrElse() should return value" {
            forAll { a: Int, b: Int ->
                Ior.Right(a).getOrElse { b } == a &&
                        Ior.Left(a).getOrElse { b } == b &&
                        Ior.Both(a, b).getOrElse { a * 2 } == b
            }

        }

        val intMonoidInstance: Semigroup<Int> = object : Semigroup<Int> {
            override fun combine(a: Int, b: Int): Int = a + b
        }
        val intIorMonad = IorMonad(intMonoidInstance)

        "Ior.monad.flatMap should combine left values" {
            val ior1 = Ior.Both(3, "Hello, world!")
            val iorResult = intIorMonad.flatMap(ior1, { Ior.Left(7) })
            iorResult shouldBe Ior.Left(10)
        }

        "Ior.monad.flatMap should be consistent with Ior#flatMap" {
            forAll { a: Int ->
                val x = { b: Int -> Ior.Right(b * a) }
                val ior = Ior.Right(a)
                ior.flatMap(intIorMonad.semigroup, x) == intIorMonad.flatMap(ior, x)
            }
        }

        "Ior.monad.binding should for comprehend over right Ior" {
            val result = intIorMonad.binding {
                val x = !Ior.Right(1)
                val y = Ior.Right(1).bind()
                val z = bind { Ior.Right(1) }
                yields(x + y + z)
            }
            result shouldBe Ior.Right(3)
        }

    }
}