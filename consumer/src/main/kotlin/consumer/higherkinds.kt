package consumer

/** HigherKinds **/
sealed class Option<out A> {
  object None : Option<Nothing>()
  data class Some<out A>(val value: A) : Option<A>()

  fun <B> map(f: (A) -> B): Option<B> =
    when (this) {
      None -> None
      is Some -> Some(f(value))
    }

  fun <B> flatMap(f: (A) -> Option<B>): Option<B> =
    when (this) {
      None -> None
      is Some -> f(value)
    }

  companion object {
    fun <A> fx(f: () -> A): Option<A> =
      Some(f())
  }
}

sealed class Either<out A, out B> {
  class Left<out A> : Either<A, Nothing>()
  class Right<out B>(val value: B) : Either<Nothing, B>()
}

class Kleisli<out F, out D, out A>

object HigherKinds {
  fun implicitArity1Casts() {
    val opOfInt: OptionOf<Int> = Option.None
    /**
     * `x` is implicitly casted since the KindAwareTypeChecker
     * establishes an iso between OptionOf<Int> <-> Option<Int>
     */
    val optIn: Option<Int> = opOfInt
    println("HigherKinds.implicitArity1Casts: $optIn")
  }

  fun implicitArity2Casts() {
    val eitherOfStringOrInt: EitherOf<String, Int> = Either.Right(1)
    /**
     * `x` is implicitly casted since the KindAwareTypeChecker
     * establishes an iso between OptionOf<Int> <-> Option<Int>
     */
    val eitherStringOrInt: Either<String, Int> = eitherOfStringOrInt
    println("HigherKinds.implicitArity2Casts: $eitherStringOrInt")
  }
}