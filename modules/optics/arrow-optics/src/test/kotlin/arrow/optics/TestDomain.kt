package arrow.optics

import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

sealed class SumType {
  data class A(val string: String) : SumType()
  data class B(val int: Int) : SumType()
}

val genSumTypeA: Gen<SumType.A> = Gen.string().map { SumType.A(it) }

val genSum: Gen<SumType> =
  Gen.oneOf<SumType>(Gen.string().map { SumType.A(it) }, Gen.int().map { SumType.B(it) })

val sumPrism: Prism<SumType, String> = Prism<SumType, String>(
  { Option.fromNullable((it as? SumType.A)?.string) },
  SumType::A
)

val stringPrism: Prism<String, List<Char>> = Prism(
  { Some(it.toList()) },
  { it.joinToString(separator = "") }
)

internal val tokenLens: Lens<Token, String> = Lens(
  { token: Token -> token.value },
  { token: Token, value: String -> token.copy(value = value) }
)

internal val tokenIso: Iso<Token, String> = Iso(
  { token: Token -> token.value },
  ::Token
)

internal val tokenSetter: Setter<Token, String> = Setter { token, s ->
  token.copy(value = s(token.value))
}

internal val userIso: Iso<User, Token> = Iso(
  { user: User -> user.token },
  ::User
)

internal val userSetter: Setter<User, Token> = Setter { user, s ->
  user.copy(token = s(user.token))
}

internal data class Token(val value: String) {
  companion object {
    fun eq() = object : Eq<Token> {
      override fun Token.eqv(b: Token): Boolean = this == b
    }
  }
}

internal val genToken: Gen<Token> = Gen.string().map { Token(it) }

internal data class User(val token: Token)

internal val genUser: Gen<User> = genToken.map { User(it) }

internal data class IncompleteUser(val token: Token?)

internal val genIncompleteUser: Gen<IncompleteUser> = Gen.constant(IncompleteUser(null))

internal val tokenGetter: Getter<Token, String> = Getter(Token::value)

internal val userLens: Lens<User, Token> = Lens(
  { user: User -> user.token },
  { user: User, token: Token -> user.copy(token = token) }
)

internal val incompleteUserTokenOptional: Optional<IncompleteUser, Token> = Optional(
  getOrModify = { user -> user.token?.right() ?: user.left() },
  set = { user, token -> user.copy(token = token) }
)

internal val defaultHead: Optional<Int, Int> = Optional(
  { Some(it) },
  { s, _ -> s }
)
