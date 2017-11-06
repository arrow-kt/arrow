package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Eq
import kategory.LensLaws
import kategory.Option
import kategory.Try
import kategory.Tuple2
import kategory.UnitSpec
import kategory.applicative
import kategory.functor
import kategory.genFunctionAToB
import kategory.left
import kategory.right
import kategory.some
import kategory.toT
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class LensTest : UnitSpec() {

    init {
        testLaws(
            LensLaws.laws(
                lens = tokenLens,
                aGen = TokenGen,
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                FA = Option.applicative()),
            LensLaws.laws(
                lens = Lens.id(),
                aGen = Gen.int(),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                FA = Try.applicative())
        )

        "Lifting a function should yield the same result as not yielding" {
            forAll(TokenGen, Gen.string(), { token, value ->
                tokenLens.set(token, value) == tokenLens.lift { value }(token)
            })
        }

        "Lifting a function as a functor should yield the same result as not yielding" {
            forAll(TokenGen, Gen.string(), { token, value ->
                tokenLens.modifyF(Option.functor(), token) { value.some() } == tokenLens.liftF { value.some() }(token)
            })
        }

        "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
            forAll({ predicate: Boolean ->
                tokenLens.find(Token("any value")) { predicate }.fold({ false }, { true }) == predicate
            })
        }

        "Checking existence predicate over the target should result in same result as predicate" {
            forAll({ predicate: Boolean ->
                tokenLens.exist(Token("any value")) { predicate } == predicate
            })
        }

        "Joining two lenses together with same target should yield same result" {
            val userTokenStringLens = userLens compose tokenLens
            val joinedLens = tokenLens choice userTokenStringLens

            forAll({ tokenValue: String ->
                val token = Token(tokenValue)
                val user = User(token)
                joinedLens.get(token.left()) == joinedLens.get(user.right())
            })
        }

        "Pairing two disjoint lenses should yield a pair of their results" {
            val spiltLens: Lens<Tuple2<Token, User>, Tuple2<String, Token>> = tokenLens split userLens
            forAll(TokenGen, UserGen, { token: Token, user: User ->
                spiltLens.get(token toT user) == token.value toT user.token
            })
        }

        "Creating a first pair with a type should result in the target to value" {
            val first = tokenLens.first<Int>()
            forAll(TokenGen, Gen.int(), { token: Token, int: Int ->
                first.get(token toT int) == token.value toT int
            })
        }

        "Creating a second pair with a type should result in the value target" {
            val first = tokenLens.second<Int>()
            forAll(Gen.int(), TokenGen, { int: Int, token: Token ->
                first.get(int toT token) == int toT token.value
            })
        }
    }

}
