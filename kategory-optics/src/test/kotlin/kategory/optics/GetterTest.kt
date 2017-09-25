package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Tuple2
import kategory.UnitSpec
import kategory.left
import kategory.right
import kategory.toT
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class GetterTest : UnitSpec() {

    init {

        val userGetter = userIso.asGetter()
        val tokenGetter = tokenIso.asGetter()
        val length = Getter<String, Int> { it.length }
        val upper = Getter<String, String> { it.toUpperCase() }

        "Getting the target should always yield the exact result" {
            forAll({ value: String ->
                tokenGetter.get(Token(value)) == value
            })
        }

        "Finding a target using a predicate within a Getter should be wrapped in the correct option result" {
            forAll({ value: String, predicate: Boolean ->
                tokenGetter.find(Token(value)) { predicate }.fold({ false }, { true }) == predicate
            })
        }

        "Checking existence of a target should always result in the same result as predicate" {
            forAll({ value: String, predicate: Boolean ->
                tokenGetter.exist(Token(value)) { predicate } == predicate
            })
        }

        "Zipping two lenses should yield a tuple of the targets" {
            forAll({ value: String ->
                length.zip(upper).get(value) == value.length toT value.toUpperCase()
            })
        }

        "Joining two getters together with same target should yield same result" {
            val userTokenStringGetter = userGetter compose tokenGetter
            val joinedGetter = tokenGetter.choice(userTokenStringGetter)

            forAll({ tokenValue: String ->
                val token = Token(tokenValue)
                val user = User(token)
                joinedGetter.get(token.left()) == joinedGetter.get(user.right())
            })
        }

        "Pairing two disjoint getters should yield a pair of their results" {
            val splitGetter: Getter<Tuple2<Token, User>, Tuple2<String, Token>> = tokenGetter.split(userGetter)
            forAll(TokenGen, UserGen, { token: Token, user: User ->
                splitGetter.get(token toT user) == token.value toT user.token
            })
        }

        "Creating a first pair with a type should result in the target to value" {
            val first = tokenGetter.first<Int>()
            forAll(TokenGen, Gen.int(), { token: Token, int: Int ->
                first.get(token toT int) == token.value toT int
            })
        }

        "Creating a second pair with a type should result in the value target" {
            val first = tokenGetter.second<Int>()
            forAll(Gen.int(), TokenGen, { int: Int, token: Token ->
                first.get(int toT token) == int toT token.value
            })
        }

    }

}