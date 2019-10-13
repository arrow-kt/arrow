package arrow.core

import arrow.core.Id.Companion.just
import arrow.higherkind

fun <A> IdOf<A>.value(): A = fix().extract()

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

// metadebug

@higherkind
data class Id<out A>(private val value: A) {

  fun <B> map(f: (A) -> B): Id<B> = Id(f(extract()))

  fun <B> flatMap(f: (A) -> Id<B>): Id<B> = f(extract())

  fun <B> foldLeft(initial: B, operation: (B, A) -> B): B = operation(initial, value)

  fun <B> foldRight(initial: Eval<B>, operation: (A, Eval<B>) -> Eval<B>): Eval<B> = operation(value, initial)

  fun <B> coflatMap(f: (Id<A>) -> B): Id<B> = map { f(this) }

  fun extract(): A = value

  fun <B> ap(ff: Id<(A) -> B>): Id<B> = ff.flatMap { f -> map(f) }

  companion object {

    tailrec fun <A, B> tailRecM(a: A, f: (A) -> IdOf<Either<A, B>>): Id<B> {
      return when (val x: Either<A, B> = f(a).value()) {
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

fun <A, B> Id<Either<A, B>>.select(f: Id<(A) -> B>): Id<B> =
  flatMap { it.fold({ l -> just(l).ap(f) }, { r -> just(r) }) }
