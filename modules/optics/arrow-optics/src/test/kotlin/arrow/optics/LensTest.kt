package arrow.optics

import arrow.core.Left
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.extensions.monoid
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.functor.functor
import arrow.core.toT
import arrow.core.ListK
import arrow.mtl.State
import arrow.core.k
import arrow.mtl.map
import arrow.mtl.run
import arrow.mtl.runId
import arrow.optics.mtl.ask
import arrow.optics.mtl.asks
import arrow.optics.mtl.assign
import arrow.optics.mtl.assignOld
import arrow.optics.mtl.assign_
import arrow.optics.mtl.extract
import arrow.optics.mtl.extractMap
import arrow.optics.mtl.toReader
import arrow.optics.mtl.toState
import arrow.optics.mtl.update
import arrow.optics.mtl.updateOld
import arrow.optics.mtl.update_
import arrow.test.UnitSpec
import arrow.test.generators.functionAToB
import arrow.test.laws.LensLaws
import arrow.test.laws.OptionalLaws
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class LensTest : UnitSpec() {

  init {
    testLaws(
      LensLaws.laws(
        lens = tokenLens,
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = String.monoid()
      ),

      TraversalLaws.laws(
        traversal = tokenLens.asTraversal(),
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      OptionalLaws.laws(
        optional = tokenLens.asOptional(),
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      ),

      SetterLaws.laws(
        setter = tokenLens.asSetter(),
        aGen = genToken,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Token.eq()
      )
    )

    testLaws(
      LensLaws.laws(
        lens = Lens.id(),
        aGen = Gen.int(),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQB = Eq.any(),
        MB = Int.monoid()
      )
    )

    "asFold should behave as valid Fold: size" {
      forAll(genToken) { token ->
        tokenLens.asFold().size(token) == 1
      }
    }

    "asFold should behave as valid Fold: nonEmpty" {
      forAll(genToken) { token ->
        tokenLens.asFold().nonEmpty(token)
      }
    }

    "asFold should behave as valid Fold: isEmpty" {
      forAll(genToken) { token ->
        !tokenLens.asFold().isEmpty(token)
      }
    }

    "asFold should behave as valid Fold: getAll" {
      forAll(genToken) { token ->
        tokenLens.asFold().getAll(token) == listOf(token.value).k()
      }
    }

    "asFold should behave as valid Fold: combineAll" {
      forAll(genToken) { token ->
        tokenLens.asFold().combineAll(String.monoid(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: fold" {
      forAll(genToken) { token ->
        tokenLens.asFold().fold(String.monoid(), token) == token.value
      }
    }

    "asFold should behave as valid Fold: headOption" {
      forAll(genToken) { token ->
        tokenLens.asFold().headOption(token) == Some(token.value)
      }
    }

    "asFold should behave as valid Fold: lastOption" {
      forAll(genToken) { token ->
        tokenLens.asFold().lastOption(token) == Some(token.value)
      }
    }

    "asGetter should behave as valid Getter: get" {
      forAll(genToken) { token ->
        tokenLens.asGetter().get(token) == tokenGetter.get(token)
      }
    }

    "asGetter should behave as valid Getter: find" {
      forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.asGetter().find(token, p) == tokenGetter.find(token, p)
      }
    }

    "asGetter should behave as valid Getter: exist" {
      forAll(genToken, Gen.functionAToB<String, Boolean>(Gen.bool())) { token, p ->
        tokenLens.asGetter().exist(token, p) == tokenGetter.exist(token, p)
      }
    }

    "Lifting a function should yield the same result as not yielding" {
      forAll(genToken, Gen.string()) { token, value ->
        tokenLens.set(token, value) == tokenLens.lift { value }(token)
      }
    }

    "Lifting a function as a functor should yield the same result as not yielding" {
      forAll(genToken, Gen.string()) { token, value ->
        tokenLens.modifyF(Option.functor(), token) { Some(value) } == tokenLens.liftF(Option.functor()) { Some(value) }(
          token
        )
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
      forAll(genToken, genUser) { token: Token, user: User ->
        spiltLens.get(token toT user) == token.value toT user.token
      }
    }

    "Creating a first pair with a type should result in the target to value" {
      val first = tokenLens.first<Int>()
      forAll(genToken, Gen.int()) { token: Token, int: Int ->
        first.get(token toT int) == token.value toT int
      }
    }

    "Creating a second pair with a type should result in the value target" {
      val second = tokenLens.second<Int>()
      forAll(Gen.int(), genToken) { int: Int, token: Token ->
        second.get(int toT token) == int toT token.value
      }
    }

    "Asking for the focus in a Reader" {
      forAll(genToken) { token: Token ->
        tokenLens.ask().runId(token) == token.value
      }
    }

    "toReader is an alias for ask" {
      forAll(genToken) { token: Token ->
        tokenLens.ask().runId(token) == tokenLens.toReader().runId(token)
      }
    }

    "Asks with f is the same as applying f to the focus of the lens" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { token, f ->
        tokenLens.asks(f).runId(token) == f(token.value)
      }
    }

    "Extract should extract the focus from the state" {
      forAll(genToken) { generatedToken ->
        tokenLens.extract().run(generatedToken) ==
          State { token: Token ->
            token toT tokenLens.get(token)
          }.run(generatedToken)
      }
    }

    "toState should be an alias to extract" {
      forAll(genToken) { token ->
        tokenLens.toState().run(token) == tokenLens.extract().run(token)
      }
    }

    "Extracts with f should be same as extract and map" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenLens.extractMap(f).run(generatedToken) == tokenLens.extract().map(f).run(generatedToken)
      }
    }

    "update f should be same modify f within State and returning new state" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenLens.update(f).run(generatedToken) ==
          State { token: Token ->
            tokenLens.modify(token, f)
              .let { it toT it.value }
          }.run(generatedToken)
      }
    }

    "updateOld f should be same as modify f within State and returning old state" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenLens.updateOld(f).run(generatedToken) ==
          State { token: Token ->
            tokenLens.modify(token, f) toT tokenLens.get(token)
          }.run(generatedToken)
      }
    }

    "update_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Gen.functionAToB<String, String>(Gen.string())) { generatedToken, f ->
        tokenLens.update_(f).run(generatedToken) ==
          State { token: Token ->
            tokenLens.modify(token, f) toT Unit
          }.run(generatedToken)
      }
    }

    "assign a should be same set a within State and returning new value" {
      forAll(genToken, Gen.string()) { generatedToken, string ->
        tokenLens.assign(string).run(generatedToken) ==
          State { token: Token ->
            tokenLens.set(token, string)
              .let { it toT it.value }
          }.run(generatedToken)
      }
    }

    "assignOld f should be same as modify f within State and returning old state" {
      forAll(genToken, Gen.string()) { generatedToken, string ->
        tokenLens.assignOld(string).run(generatedToken) ==
          State { token: Token ->
            tokenLens.set(token, string) toT tokenLens.get(token)
          }.run(generatedToken)
      }
    }

    "assign_ f should be as modify f within State and returning Unit" {
      forAll(genToken, Gen.string()) { generatedToken, string ->
        tokenLens.assign_(string).run(generatedToken) ==
          State { token: Token ->
            tokenLens.set(token, string) toT Unit
          }.run(generatedToken)
      }
    }
  }
}
