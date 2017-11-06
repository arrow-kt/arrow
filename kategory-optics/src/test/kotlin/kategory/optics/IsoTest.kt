package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Either
import kategory.Eq
import kategory.IsoLaws
import kategory.LensLaws
import kategory.NonEmptyList
import kategory.Option
import kategory.PrismLaws
import kategory.StringMonoidInstance
import kategory.UnitSpec
import kategory.applicative
import kategory.functor
import kategory.genFunctionAToB
import kategory.some
import kategory.toT
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IsoTest : UnitSpec() {

    init {

        val aIso: Iso<SumType.A, String> = Iso(
                get = { a: SumType.A -> a.string },
                reverseGet = SumType::A
        )

        testLaws(
            LensLaws.laws(
                lens = tokenIso.asLens(),
                aGen = TokenGen,
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                FA = NonEmptyList.applicative()),

            PrismLaws.laws(
                prism = aIso.asPrism(),
                aGen = AGen,
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            IsoLaws.laws(
                iso = tokenIso,
                aGen = TokenGen,
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                bMonoid = StringMonoidInstance)
        )

        "Lifting a function should yield the same result as not yielding" {
            forAll(TokenGen, Gen.string(), { token, value ->
                tokenIso.modify(token) { value } == tokenIso.lift { value }(token)
            })
        }

        "Lifting a function as a functor should yield the same result as not yielding" {
            forAll(TokenGen, Gen.string(), { token, value ->
                tokenIso.modifyF(Option.functor(), token) { value.some() } == tokenIso.liftF { value.some() }(token)
            })
        }

        "Creating a first pair with a type should result in the target to value" {
            val first = tokenIso.first<Int>()
            forAll(TokenGen, Gen.int(), { token: Token, int: Int ->
                first.get(token toT int) == token.value toT int
            })
        }

        "Creating a second pair with a type should result in the value to target" {
            val second = tokenIso.second<Int>()
            forAll(Gen.int(), TokenGen, { int: Int, token: Token ->
                second.get(int toT token) == int toT token.value
            })
        }

        "Creating a left with a type should result in a sum target to value" {
            val left = tokenIso.left<Int>()
            forAll(TokenGen, Gen.int(), { token: Token, int: Int ->
                left.get(Either.Left(token)) == Either.Left(token.value) &&
                        left.get(Either.Right(int)) == Either.Right(int)
            })
        }

        "Creating a right with a type should result in a sum value to target" {
            val left = tokenIso.right<Int>()
            forAll(TokenGen, Gen.int(), { token: Token, int: Int ->
                left.get(Either.Left(int)) == Either.Left(int) &&
                        left.get(Either.Right(token)) == Either.Right(token.value)
            })
        }

        "Finding a target using a predicate within a Iso should be wrapped in the correct option result" {
            forAll({ predicate: Boolean ->
                tokenIso.find(Token("any value")) { predicate }.fold({ false }, { true }) == predicate
            })
        }

        "Checking existence predicate over the target should result in same result as predicate" {
            forAll({ predicate: Boolean ->
                tokenIso.exist(Token("any value")) { predicate } == predicate
            })
        }

        "Pairing two disjoint isos together" {
            val joinedIso = tokenIso split userIso

            forAll({ tokenValue: String ->
                val token = Token(tokenValue)
                val user = User(token)
                joinedIso.get(token toT user) == tokenValue toT token
            })
        }

        "Composing isos should result in an iso of the first iso's value with the second iso's target" {
            val composedIso = userIso compose tokenIso

            forAll({ tokenValue: String ->
                val token = Token(tokenValue)
                val user = User(token)
                composedIso.get(user) == tokenValue
            })
        }
    }

}
