package arrow.optics

import arrow.core.Option
import arrow.core.Some
import arrow.core.left
import arrow.core.right
import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

sealed class SumType {
  data class A(val string: String) : SumType()
  data class B(val int: Int) : SumType()
}

fun Arb.Companion.sumTypeA(): Arb<SumType.A> =
  Arb.string().map { SumType.A(it) }

fun Arb.Companion.sumType(): Arb<SumType> =
  Arb.choice(Arb.string().map { SumType.A(it) }, Arb.int().map { SumType.B(it) })

fun PPrism.Companion.sumType(): Prism<SumType, String> = Prism(
  { Option.fromNullable((it as? SumType.A)?.string) },
  SumType::A
)

fun PPrism.Companion.string(): Prism<String, List<Char>> = Prism(
  { Some(it.toList()) },
  { it.joinToString(separator = "") }
)

internal fun PLens.Companion.token(): Lens<Token, String> = PLens(
  { token: Token -> token.value },
  { token: Token, value: String -> token.copy(value = value) }
)

internal fun PIso.Companion.token(): Iso<Token, String> = Iso(
  { token: Token -> token.value },
  ::Token
)

internal fun PSetter.Companion.token(): Setter<Token, String> = Setter { token, s ->
  token.copy(value = s(token.value))
}

internal fun PIso.Companion.user(): Iso<User, Token> = Iso(
  { user: User -> user.token },
  ::User
)

internal fun PSetter.Companion.user(): Setter<User, Token> = Setter { user, s ->
  user.copy(token = s(user.token))
}

internal data class Token(val value: String) {
  companion object
}

internal fun Arb.Companion.token(): Arb<Token> =
  Arb.string().map { Token(it) }

internal data class User(val token: Token)

internal fun Arb.Companion.user(): Arb<User> =
  Arb.token().map { User(it) }

internal data class IncompleteUser(val token: Token?)

internal fun Arb.Companion.incompleteUser(): Arb<IncompleteUser> = Arb.constant(IncompleteUser(null))

internal fun PGetter.Companion.token(): Getter<Token, String> =
  Getter { it.value }

internal fun PLens.Companion.user(): Lens<User, Token> = Lens(
  { user: User -> user.token },
  { user: User, token: Token -> user.copy(token = token) }
)

internal fun POptional.Companion.incompleteUserToken(): Optional<IncompleteUser, Token> = Optional(
  getOrModify = { user -> user.token?.right() ?: user.left() },
  set = { user, token -> user.copy(token = token) }
)

internal fun POptional.Companion.defaultHead(): Optional<Int, Int> = Optional(
  { Some(it) },
  { s, _ -> s }
)
