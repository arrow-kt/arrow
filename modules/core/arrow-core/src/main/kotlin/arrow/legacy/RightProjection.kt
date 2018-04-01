package arrow.legacy

import arrow.core.*

@Deprecated("arrow.data.Either is right biased. This data type will be removed in future releases")
class RightProjection<out L, out R>(val e: Either<L, R>) {

  fun get(): R = when (e) {
    is Either.Right -> e.b
    is Either.Left -> throw NoSuchElementException("Either.right.value on Left")
  }

  fun forEach(f: (R) -> Unit) {
    when (e) {
      is Either.Right -> f(e.b)
    }
  }

  fun exists(predicate: (R) -> Boolean): Boolean = when (e) {
    is Either.Right -> predicate(e.b)
    is Either.Left -> false
  }

  fun <X> map(f: (R) -> X): Either<L, X> = flatMap { Right(f(it)) }

  fun filter(predicate: (R) -> Boolean): Option<Either<L, R>> = when (e) {
    is Either.Right -> {
      if (predicate(e.b)) {
        Some(e)
      } else {
        None
      }
    }
    is Either.Left -> None
  }

  fun toList(): List<R> = when (e) {
    is Either.Right -> listOf(e.b)
    is Either.Left -> listOf()
  }

  fun toOption(): Option<R> = when (e) {
    is Either.Right -> Some(e.b)
    is Either.Left -> None
  }

}

fun <L, R> RightProjection<L, R>.getOrElse(default: () -> R): R = when (e) {
  is Either.Right -> e.b
  is Either.Left -> default()
}

fun <X, L, R> RightProjection<L, R>.flatMap(f: (R) -> Either<L, X>): Either<L, X> = when (e) {
  is Either.Left -> Left(e.a)
  is Either.Right -> f(e.b)
}

fun <L, R, X, Y> RightProjection<L, R>.map(x: Either<L, X>, f: (R, X) -> Y): Either<L, Y> = flatMap { r -> x.right().map { xx -> f(r, xx) } }
