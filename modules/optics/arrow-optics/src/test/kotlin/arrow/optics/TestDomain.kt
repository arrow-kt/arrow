package arrow.optics

import arrow.core.*
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

sealed class SumType {
  data class A(val string: String) : SumType()
  data class B(val int: Int) : SumType()
}

object AGen : Gen<SumType.A> {
  override fun generate(): SumType.A = SumType.A(Gen.string().generate())
}

object SumGen : Gen<SumType> {
  override fun generate(): SumType = Gen.oneOf(AGen, Gen.create { SumType.B(Gen.int().generate()) }).generate()
}

val sumPrism: Prism<SumType, String> = Prism(
  {
    when (it) {
      is SumType.A -> Right(it.string)
      else -> Left(it)
    }
  },
  SumType::A
)

val stringPrism: Prism<String, List<Char>> = Prism(
  { Right(it.toList()) },
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

internal object TokenGen : Gen<Token> {
  override fun generate() = Token(Gen.string().generate())
}

internal data class User(val token: Token)
internal object UserGen : Gen<User> {
  override fun generate() = User(TokenGen.generate())
}

internal val tokenGetter: Getter<Token, String> = Getter(Token::value)

internal val userLens: Lens<User, Token> = Lens(
  { user: User -> user.token },
  { user: User, token: Token -> user.copy(token = token) }
)

internal val optionalHead: Optional<List<Int>, Int> = Optional(
  { it.firstOrNull()?.right() ?: it.left() },
  { list, int -> listOf(int) + if (list.size > 1) list.drop(1) else emptyList() }
)

internal val defaultHead: Optional<Int, Int> = Optional(
  { it.right() },
  { s, _ -> s }
)
