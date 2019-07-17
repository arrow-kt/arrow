package consumer

sealed class Option<out A> {
  object None : Option<Nothing>()
  data class Some<out A>(val value: A) : Option<A>()
}

sealed class Either<out A, out B> {
  class Left<out A> : Either<A, Nothing>()
  class Right<out B>(val value: B) : Either<Nothing, B>()
}

class Kleisli<out F, out D, out A>