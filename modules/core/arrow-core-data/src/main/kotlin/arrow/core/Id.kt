package arrow.core

import arrow.core.Id.Companion.just
import arrow.higherkind

fun <A> IdOf<A>.value(): A = this.fix().extract()

/**
 *
 * The identity monad can be seen as the ambient monad that encodes the effect of having no effect.
 * It is ambient in the sense that plain pure values are values of `Id`.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Id
 *
 * fun getId() =
 * //sampleStart
 *  Id("hello")
 * //sampleEnd
 *
 * fun main() {
 *  println(getId())
 * }
 * ```
 *
 * Using this type declaration, we can treat our Id type constructor as a `Monad` and as a `Comonad`.
 * The `just` method, which has type `A -> Id<A>` just becomes the identity function. The `map` method
 * from `Functor` just becomes function application.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Id
 *
 * //sampleStart
 * fun idPlusThree(value: Int) =
 *  Id.just(value)
 *    .map { it + 3 }
 * //sampleEnd
 *
 * fun main() {
 *  val value = 3
 *  println("idPlusThree($value) = ${idPlusThree(value)}")
 * }
 * ```
 */

@higherkind
data class Id<out A>(private val value: A) : IdOf<A> {

  fun <B> map(f: (A) -> B): Id<B> = Id(f(extract()))

  fun <B> flatMap(f: (A) -> IdOf<B>): Id<B> = f(extract()).fix()

  fun <B> foldLeft(initial: B, operation: (B, A) -> B): B = operation(initial, value)

  fun <B> foldRight(initial: Eval<B>, operation: (A, Eval<B>) -> Eval<B>): Eval<B> = operation(value, initial)

  fun <B> coflatMap(f: (IdOf<A>) -> B): Id<B> = this.fix().map { f(this) }

  fun extract(): A = value

  fun <B> ap(ff: IdOf<(A) -> B>): Id<B> = ff.fix().flatMap { f -> map(f) }.fix()

  companion object {

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> IdOf<Either<A, B>>): Id<B> {
      val x: Either<A, B> = f(a).value()
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

  override fun hashCode(): Int = value.hashCode()
}

fun <A, B> Id<Either<A, B>>.select(f: IdOf<(A) -> B>): Id<B> =
  flatMap { it.fold({ l -> just(l).ap(f) }, { r -> just(r) }) }
