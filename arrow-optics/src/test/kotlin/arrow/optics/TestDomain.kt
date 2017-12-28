package arrow.optics

import io.kotlintest.properties.Gen
import identity
import arrow.syntax.either.left
import arrow.syntax.either.right

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
                is SumType.A -> it.string.right()
                else -> it.left()
            }
        },
        SumType::A
)

val stringPrism: Prism<String, List<Char>> = Prism(
        { it.toList().right() },
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

internal data class Token(val value: String)
internal object TokenGen : Gen<Token> {
    override fun generate() = Token(Gen.string().generate())
}

internal data class User(val token: Token)
internal object UserGen : Gen<User> {
    override fun generate() = User(TokenGen.generate())
}

internal val userLens: Lens<User, Token> = Lens(
        { user: User -> user.token },
        { token: Token -> { user: User -> user.copy(token = token) } }
)

internal val optionalHead: Optional<List<Int>, Int> = Optional(
        { it.firstOrNull()?.right() ?: it.left() },
        { int -> { list -> listOf(int) + if (list.size > 1) list.drop(1) else emptyList() } }
)

internal val defaultHead: Optional<Int, Int> = Optional(
        { it.right() },
        { ::identity }
)