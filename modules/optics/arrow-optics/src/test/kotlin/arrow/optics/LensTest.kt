package arrow.optics

import arrow.core.*
import arrow.data.*
import arrow.instances.monoid
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.laws.LensLaws
import arrow.test.laws.OptionalLaws
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
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
        MB = String.monoid()
      ),

      TraversalLaws.laws(
        traversal = tokenLens.asTraversal(),
        aGen = TokenGen,
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      OptionalLaws.laws(
        optional = tokenLens.asOptional(),
        aGen = TokenGen,
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      ),

      SetterLaws.laws(
        setter = tokenLens.asSetter(),
        aGen = TokenGen,
        bGen = Gen.string(),
        funcGen = genFunctionAToB(Gen.string()),
        EQA = Token.eq()
      )
    )

    testLaws(LensLaws.laws(
      lens = Lens.id(),
      aGen = Gen.int(),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any(),
      EQB = Eq.any(),
      MB = Int.monoid()
    ))

    "asFold should behave as valid Fold: size" {
      forAll(TokenGen) { token ->
        tokenLens.asFold().size(token) == 1
      }
    }

    "asFold should behave as valid Fold: nonEmpty" {
      forAll(TokenGen) { token ->
        tokenLens.asFold().nonEmpty(token)
      }
    }

    "asFold should behave as valid Fold: isEmpty" {
      forAll(TokenGen) { token ->
        !tokenLens.asFold().isEmpty(token)
      }
    }

    "asFold should behave as valid Fold: getAll" {
      forAll(TokenGen) { token ->
        tokenLens.asFold().getAll(token) == listOf(token.value).k()
      }
    }

    "asFold should behave as valid Fold: combineAll" {
      forAll(TokenGen) { token ->
        tokenLens.asFold().combineAll(String.monoid(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: fold" {
      forAll(TokenGen) { token ->
        tokenLens.asFold().fold(String.monoid(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: headOption" {
      forAll(TokenGen) { token ->
        tokenLens.asFold().headOption(token) == Some(token.value)
      }
    }

    "asFold should behave as valid Fold: lastOption" {
      forAll(TokenGen) { token ->
        tokenLens.asFold().lastOption(token) == Some(token.value)
      }
    }

    "asGetter should behave as valid Getter: get" {
      forAll(TokenGen) { token ->
        tokenLens.asGetter().get(token) == tokenGetter.get(token)
      }
    }

    "asGetter should behave as valid Getter: find" {
      forAll(TokenGen, genFunctionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.asGetter().find(token, p) == tokenGetter.find(token, p)
      }
    }

    "asGetter should behave as valid Getter: exist" {
      forAll(TokenGen, genFunctionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.asGetter().exist(token, p) == tokenGetter.exist(token, p)
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      forAll(TokenGen, Gen.string()) { token, value ->
        tokenLens.set(token, value) == tokenLens.lift { value }(token)
      }
    }

    "Lifting a function as a functor should yield the same result as not yielding" {
      forAll(TokenGen, Gen.string()) { token, value ->
        tokenLens.modifyF(Option.functor(), token) { Some(value) } == tokenLens.liftF(Option.functor()) { Some(value) }(token)
      }
    }

    "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
      forAll { predicate: Boolean ->
        tokenLens.find(Token("any value")) { predicate }.fold({ false }, { true }) == predicate
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll { predicate: Boolean ->
        tokenLens.exist(Token("any value")) { predicate } == predicate
      }
    }

    "Joining two lenses together with same target should yield same result" {
      val userTokenStringLens = userLens compose tokenLens
      val joinedLens = tokenLens choice userTokenStringLens

      forAll { tokenValue: String ->
        val token = Token(tokenValue)
        val user = User(token)
        joinedLens.get(Left(token)) == joinedLens.get(Right(user))
      }
    }

    "Pairing two disjoint lenses should yield a pair of their results" {
      val spiltLens: Lens<Tuple2<Token, User>, Tuple2<String, Token>> = tokenLens split userLens
      forAll(TokenGen, UserGen) { token: Token, user: User ->
        spiltLens.get(token toT user) == token.value toT user.token
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenLens.first<Int>()
      forAll(TokenGen, Gen.int()) { token: Token, int: Int ->
        first.get(token toT int) == token.value toT int
      }
    }

    "Creating a second pair with a type should result in the value target" {
      val second = tokenLens.second<Int>()
      forAll(Gen.int(), TokenGen) { int: Int, token: Token ->
        second.get(int toT token) == int toT token.value
      }
    }

    "Asking for the focus in a Reader" {
      forAll(TokenGen) { token: Token ->
        tokenLens.ask().runId(token) == token.value
      }
    }

    "toReader is an alias for ask" {
      forAll(TokenGen) { token: Token ->
        tokenLens.ask().runId(token) == tokenLens.toReader().runId(token)
      }
    }

    "Asks with f is the same as applying f to the focus of the lens" {
      forAll(TokenGen, genFunctionAToB<String, String>(Gen.string())) { token, f ->
        tokenLens.asks(f).runId(token) == f(token.value)
      }
    }

  }

}

fun main(args: Array<String>) {

  val reader: Reader<NonEmptyList<String>, String> = NonEmptyList.head<String>().asGetter().ask()

  reader
    .map(String::toUpperCase)
    .runId(NonEmptyList("Hello", "World", "Viewed", "With", "Optics"))

  NonEmptyList.head<String>().asGetter().asks(String::toUpperCase)
    .runId(NonEmptyList("Hello", "World", "Viewed", "With", "Optics"))
}