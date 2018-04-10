package arrow.optics

import arrow.core.Left
import arrow.core.Right
import arrow.core.identity
import arrow.test.generators.generate
import arrow.test.generators.oneOf
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.map

sealed class SumType {
  data class A(val string: String) : SumType()
  data class B(val int: Int) : SumType()
}

object AGen : Gen<SumType.A> {
  override fun always(): Iterable<SumType.A> = emptyList()

  override fun random(): Sequence<SumType.A> = Gen.string().random().map { SumType.A(it) }

}

object SumGen : Gen<SumType> {
  override fun always(): Iterable<SumType> = listOf()

  override fun random(): Sequence<SumType> = oneOf(AGen, Gen.create { SumType.B(Gen.int().generate()) }).random()
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
  { value: String -> { token: Token -> token.copy(value = value) } }
)

internal val tokenIso: Iso<Token, String> = Iso(
  { token: Token -> token.value },
  ::Token
)

internal val tokenSetter: Setter<Token, String> = Setter { s ->
  { token -> token.copy(value = s(token.value)) }
}

internal val userIso: Iso<User, Token> = Iso(
  { user: User -> user.token },
  ::User
)

internal val userSetter: Setter<User, Token> = Setter { s ->
  { user -> user.copy(token = s(user.token)) }
}

internal data class Token(val value: String) {
  companion object {
    fun eq() = object : Eq<Token> {
      override fun Token.eqv(b: Token): Boolean = this == b
    }
  }
}

internal object TokenGen : Gen<Token> {
  override fun always(): Iterable<Token> = emptyList()

  override fun random(): Sequence<Token> = Gen.string().random().map { Token(it) }
}

internal data class User(val token: Token)
internal object UserGen : Gen<User> {
  override fun always(): Iterable<User> = emptyList()

  override fun random(): Sequence<User> = TokenGen.random().map { User(it) }
}

internal val tokenGetter: Getter<Token, String> = Getter(Token::value)

internal val userLens: Lens<User, Token> = Lens(
  { user: User -> user.token },
  { token: Token -> { user: User -> user.copy(token = token) } }
)

internal val optionalHead: Optional<List<Int>, Int> = Optional(
  { it.firstOrNull()?.let(::Right) ?: it.let(::Left) },
  { int -> { list -> listOf(int) + if (list.size > 1) list.drop(1) else emptyList() } }
)

internal val defaultHead: Optional<Int, Int> = Optional(
  { it.let(::Right) },
  { ::identity }
)
