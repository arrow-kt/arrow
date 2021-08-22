//package arrow.optics
//
//import arrow.core.Either.Left
//import arrow.core.Either.Right
//import arrow.core.test.UnitSpec
//import arrow.core.test.generators.functionAToB
//import arrow.optics.test.laws.LensLaws
//import arrow.optics.test.laws.OptionalLaws
//import arrow.optics.test.laws.SetterLaws
//import arrow.optics.test.laws.TraversalLaws
//import arrow.typeclasses.Monoid
//import io.kotest.matchers.shouldBe
//import io.kotest.property.Arb
//import io.kotest.property.arbitrary.bool
//import io.kotest.property.arbitrary.int
//import io.kotest.property.arbitrary.string
//import io.kotest.property.checkAll
//
//class LensTest : UnitSpec() {
//
//  init {
//    testLaws(
//      "TokenLens - ",
//      LensLaws.laws(
//        lens = tokenLens,
//        aGen = genToken,
//        bGen = Arb.string(),
//        funcGen = Arb.functionAToB(Arb.string())
//      ),
//
//      TraversalLaws.laws(
//        traversal = tokenLens,
//        aGen = genToken,
//        bGen = Arb.string(),
//        funcGen = Arb.functionAToB(Arb.string()),
//      ),
//
//      OptionalLaws.laws(
//        optional = tokenLens,
//        aGen = genToken,
//        bGen = Arb.string(),
//        funcGen = Arb.functionAToB(Arb.string()),
//      ),
//
//      SetterLaws.laws(
//        setter = tokenLens,
//        aGen = genToken,
//        bGen = Arb.string(),
//        funcGen = Arb.functionAToB(Arb.string()),
//      )
//    )
//
//    testLaws(
//      "Identity Lens - ",
//      LensLaws.laws(
//        lens = Lens.id(),
//        aGen = Arb.int(),
//        bGen = Arb.int(),
//        funcGen = Arb.functionAToB(Arb.int()),
//      )
//    )
//
//    "asFold should behave as valid Fold: size" {
//      checkAll(genToken) { token ->
//        tokenLens.size(token) shouldBe 1
//      }
//    }
//
//    "asFold should behave as valid Fold: nonEmpty" {
//      checkAll(genToken) { token ->
//        tokenLens.isNotEmpty(token) shouldBe true
//      }
//    }
//
//    "asFold should behave as valid Fold: isEmpty" {
//      checkAll(genToken) { token ->
//        !tokenLens.isEmpty(token) shouldBe true
//      }
//    }
//
//    "asFold should behave as valid Fold: getAll" {
//      checkAll(genToken) { token ->
//        tokenLens.getAll(token) shouldBe listOf(token.value)
//      }
//    }
//
//    "asFold should behave as valid Fold: combineAll" {
//      checkAll(genToken) { token ->
//        tokenLens.combineAll(Monoid.string(), token) shouldBe token.value
//      }
//    }
//
//    "asFold should behave as valid Fold: fold" {
//      checkAll(genToken) { token ->
//        tokenLens.fold(Monoid.string(), token) shouldBe token.value
//      }
//    }
//
//    "asFold should behave as valid Fold: headOption" {
//      checkAll(genToken) { token ->
//        tokenLens.firstOrNull(token) shouldBe token.value
//      }
//    }
//
//    "asFold should behave as valid Fold: lastOption" {
//      checkAll(genToken) { token ->
//        tokenLens.lastOrNull(token) shouldBe token.value
//      }
//    }
//
//    "asGetter should behave as valid Getter: get" {
//      checkAll(genToken) { token ->
//        tokenLens.get(token) shouldBe tokenGetter.get(token)
//      }
//    }
//
//    "asGetter should behave as valid Getter: find" {
//      checkAll(genToken, Arb.functionAToB<String, Boolean>(Arb.bool())) { token, p ->
//        tokenLens.findOrNull(token, p) shouldBe tokenGetter.findOrNull(token, p)
//      }
//    }
//
//    "asGetter should behave as valid Getter: exist" {
//      checkAll(genToken, Arb.functionAToB<String, Boolean>(Arb.bool())) { token, p ->
//        tokenLens.any(token, p) shouldBe tokenGetter.any(token, p)
//      }
//    }
//
//    "Lifting a function should yield the same result as not yielding" {
//      checkAll(genToken, Arb.string()) { token, value ->
//        tokenLens.set(token, value) shouldBe tokenLens.lift { value }(token)
//      }
//    }
//
//    "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
//      checkAll { predicate: Boolean ->
//        tokenLens.findOrNull(Token("any value")) { predicate }?.let { true } ?: false shouldBe predicate
//      }
//    }
//
//    "Checking existence predicate over the target should result in same result as predicate" {
//      checkAll { predicate: Boolean ->
//        tokenLens.any(Token("any value")) { predicate } shouldBe predicate
//      }
//    }
//
//    "Joining two lenses together with same target should yield same result" {
//      val userTokenStringLens = userLens compose tokenLens
//      val joinedLens = tokenLens choice userTokenStringLens
//
//      checkAll { tokenValue: String ->
//        val token = Token(tokenValue)
//        val user = User(token)
//        joinedLens.get(Left(token)) shouldBe joinedLens.get(Right(user))
//      }
//    }
//
//    "Pairing two disjoint lenses should yield a pair of their results" {
//      val spiltLens: Lens<Pair<Token, User>, Pair<String, Token>> = tokenLens split userLens
//      checkAll(genToken, genUser) { token: Token, user: User ->
//        spiltLens.get(token to user) shouldBe (token.value to user.token)
//      }
//    }
//
//    "Creating a first pair with a type should result in the target to value" {
//      val first = tokenLens.first<Int>()
//      checkAll(genToken, Arb.int()) { token: Token, int: Int ->
//        first.get(token to int) shouldBe (token.value to int)
//      }
//    }
//
//    "Creating a second pair with a type should result in the value target" {
//      val second = tokenLens.second<Int>()
//      checkAll(Arb.int(), genToken) { int: Int, token: Token ->
//        second.get(int to token) shouldBe (int to token.value)
//      }
//    }
//  }
//}
