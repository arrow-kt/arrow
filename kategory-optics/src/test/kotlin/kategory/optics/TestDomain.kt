package kategory.optics

import io.kotlintest.properties.Gen
import kategory.left
import kategory.right

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

val sumPrism = Prism<SumType, String>(
        {
            when (it) {
                is SumType.A -> it.string.right()
                else -> it.left()
            }
        },
        SumType::A
)

val stringPrism = Prism<String, List<Char>>(
        { it.toList().right() },
        { it.joinToString(separator = "") }
)

internal val tokenLens: Lens<Token, String> = Lens(
        { token: Token -> token.value },
        { value: String -> { token: Token -> token.copy(value = value) } }
)

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
