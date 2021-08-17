/**
 * This file will be removed in the next release when arrow `shadow` is published
 * and available so we can build Arrow with the previous uber jar of Arrow
 */
package arrow.meta

public sealed class Either<out A, out B> {
  public data class Left<out A> (val a: A) : Either<A, Nothing>()
  public data class Right<out B> (val b: B) : Either<Nothing, B>()

  public fun <C> map(f: (B) -> C): Either<A, C> =
    flatMap { Right(f(it)) }

  public inline fun <C> fold(ifLeft: (A) -> C, ifRight: (B) -> C): C = when (this) {
    is Right -> ifRight(b)
    is Left -> ifLeft(a)
  }
}

public inline fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
  when (this) {
    is Either.Right -> f(this.b)
    is Either.Left -> this
  }
