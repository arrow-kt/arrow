import arrow.core.Ior
import arrow.core.identity
import arrow.typeclasses.Semigroup

suspend fun <E, A> ior(semigroup: Semigroup<E>, f: suspend IorEffect<E>.() -> A): Ior<E, A> =
  cont<E, Ior<E, A>> {
    val effect = IorEffect(semigroup, this)
    val res = f(effect)
    if (effect.leftState === EmptyValue) Ior.Right(res)
    else Ior.Both(EmptyValue.unbox(effect.leftState), res)
  }.fold({ Ior.Left(it) }, ::identity)

class IorEffect<E>(
  semigroup: Semigroup<E>,
  private val cont: ContEffect<E>
) : ContEffect<E>, Semigroup<E> by semigroup {

  internal var leftState: Any? = EmptyValue

  private fun combine(other: E): E =
    if (leftState === EmptyValue) {
      leftState = other
      other
    } else EmptyValue.unbox<E>(leftState).combine(other)

  suspend fun <B> Ior<E, B>.bind(): B =
    when (this) {
      is Ior.Left -> shift(value)
      is Ior.Right -> value
      is Ior.Both -> {
        leftState = combine(leftValue)
        rightValue
      }
    }

  override suspend fun <B> shift(r: E): B =
    cont.shift(combine(r))
}
