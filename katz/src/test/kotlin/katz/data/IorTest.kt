package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith


@RunWith(KTestJUnitRunner::class)
class IorTest : UnitSpec() {
    init {
        "flatMap() should modify entity" {
            forAll { a: Int, b: String ->
                {
                    val implicit: Semigroup<Number> = object : Semigroup<Number> {
                        override fun combine(a: Number, b: Number): Number = a
                    }
                    Ior.Right(b).flatMap(implicit) { Ior.Left(a) } == Ior.Left(a) &&
                            Ior.Right(a).flatMap(implicit) { Ior.Right(b) } == Ior.Right(b) &&
                            Ior.Left(a).flatMap(implicit) { Ior.Right(b) } == Ior.Left(a) &&
                            Ior.Both(a, b).flatMap(implicit) { Ior.Left(a) } == Ior.Left(implicit.combine(a, a)) &&
                            Ior.Both(a, b).flatMap(implicit) { Ior.Right(b) } == Ior.Right(b) &&
                            Ior.Both(a, b).flatMap(implicit) { Ior.Both(a, b) } == Ior.Both(implicit.combine(a, a), b)
                }()
            }
        }

        "flatMap() should combine with upper bound" {
            forAll { a: String, b: Int ->
                {
                    val implicit: Semigroup<Number> = object : Semigroup<Number> {
                        override fun combine(a: Number, b: Number): Number = a
                    }
                    val iorRightString: Ior<Int, String> = Ior.Right(a)
                    val iorLeftNumberAsUpperBoundOfInt: Ior<Number, String> = Ior.Left(b)
                    val iorResult: Ior<Number, String> = iorRightString.flatMap(implicit) { iorLeftNumberAsUpperBoundOfInt }
                    iorResult == Ior.Left(b)
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

    }
}