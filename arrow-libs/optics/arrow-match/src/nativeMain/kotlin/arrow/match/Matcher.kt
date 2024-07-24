package arrow.match

import kotlin.reflect.KType

public actual fun <S, A> Matcher(
  name: String,
  get: (S) -> A
): Matcher<S, A> = object : Matcher<S, A> {
  override val name: String = name
  override fun get(receiver: S): A = get(receiver)
  override fun invoke(receiver: S): A = get(receiver)

  override val returnType: KType
    get() = TODO("Not yet implemented")
}
