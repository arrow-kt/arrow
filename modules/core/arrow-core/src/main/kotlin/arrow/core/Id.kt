package arrow.core

import arrow.higherkind

fun <A> IdOf<A>.value(): A = this.fix().value

@higherkind
data class Id<out A>(val value: A) : IdOf<A> {

  inline fun <B> map(f: (A) -> B): Id<B> = Id(f(value))

  inline fun <B> flatMap(f: (A) -> IdOf<B>): Id<B> = f(value).fix()

  fun <B> foldLeft(initial: B, operation: (B, A) -> B): B = operation(initial, this.fix().value)

  fun <B> foldRight(initial: Eval<B>, operation: (A, Eval<B>) -> Eval<B>): Eval<B> = operation(this.fix().value, initial)

  fun <B> coflatMap(f: (IdOf<A>) -> B): Id<B> = this.fix().map { f(this) }

  fun extract(): A = this.fix().value

  fun <B> ap(ff: IdOf<(A) -> B>): Id<B> = ff.fix().flatMap { f -> map(f) }.fix()

  companion object {

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> IdOf<Either<A, B>>): Id<B> {
      val x: Either<A, B> = f(a).fix().value
      return when (x) {
        is Either.Left -> tailRecM(x.a, f)
        is Either.Right -> Id(x.b)
      }
    }

    fun <A> just(a: A): Id<A> = Id(a)
  }

  override fun equals(other: Any?): Boolean =
    when (other) {
      is Id<*> -> other.value == value
      else -> other == value
    }
}
