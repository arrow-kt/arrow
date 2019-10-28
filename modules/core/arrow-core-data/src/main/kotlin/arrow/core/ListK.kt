package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative

/**
 *
 * ank_macro_hierarchy(arrow.core.ListK)
 *
 * {:.beginner}
 * beginner
 *
 * ListK wraps over the platform `List` type to make it a [type constructor](/docs/patterns/glossary/#type-constructors).
 *
 * It can be created from Kotlin List type with a convenient `k()` function.
 *
 * ```kotlin:ank:playground
 * import arrow.core.k
 *
 * val value =
 * //sampleStart
 *  listOf(1, 2, 3).k()
 * //sampleEnd
 * fun main() {
 *   println(value)
 * }
 * ```
 *
 * For most use cases you will never use `ListK` directly but `List` directly with the extension functions that Arrow projects over it.
 *
 * ListK implements operators from many useful typeclasses.
 *
 * The @extension type class processor expands all type class combinators that `ListK` provides automatically over `List`
 *
 * For instance, it has `combineK` from the [SemigroupK](/docs/arrow/typeclasses/semigroupk/) typeclass.
 *
 * It can be used to cheaply combine two lists:
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.list.semigroupK.combineK
 *
 * //sampleStart
 * val hello = listOf('h', 'e', 'l', 'l', 'o')
 * val commaSpace = listOf(',', ' ')
 * val world = listOf('w', 'o', 'r', 'l', 'd')
 *
 * val combinedList = hello.combineK(commaSpace).combineK(world)
 * //sampleEnd
 * fun main() {
 *  println("combinedList = $combinedList")
 * }
 * ```
 *
 * The functions `traverse` and `sequence` come from [Traverse](/docs/arrow/typeclasses/traverse/).
 *
 * Traversing a list creates a new container [Kind<F, A>](/docs/patterns/glossary/#type-constructors) by combining the result of a function applied to each element:
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.extensions.list.traverse.traverse
 * import arrow.core.extensions.option.applicative.applicative
 *
 * //sampleStart
 * val numbers = listOf(Math.random(), Math.random(), Math.random())
 * val traversedList = numbers.traverse(Option.applicative(), { if (it > 0.5) Some(it) else None })
 * //sampleEnd
 * fun main() {
 *   println("traversedList $traversedList")
 * }
 * ```
 *
 * and complements the convenient function `sequence()` that converts a list of `ListK<Kind<F, A>>` into a `Kind<F, ListK<A>>`:
 *
 * ```kotlin:ank:playground
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.extensions.list.traverse.sequence
 * import arrow.core.extensions.option.applicative.applicative
 *
 * //sampleStart
 * val requests = listOf(Some(Math.random()), Some(Math.random()), Some(Math.random()))
 * val sequenceList = requests.sequence(Option.applicative())
 * //sampleEnd
 * fun main() {
 *   println("sequenceList = $sequenceList")
 * }
 * ```
 *
 * If you want to aggregate the elements of a list into any other value you can use `foldLeft` and `foldRight` from [Foldable](/docs/arrow/typeclasses/foldable).
 *
 * Folding a list into a new value, `String` in this case, starting with an initial value and a combine function:
 *
 * ```kotlin:ank:playground
 * import arrow.core.k
 * import arrow.core.extensions.list.foldable.foldLeft
 * val value =
 * //sampleStart
 *  listOf('a', 'b', 'c', 'd', 'e').k().foldLeft("-> ") { x, y -> x + y }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * Or you can apply a list of transformations using `ap` from [Applicative](/docs/arrow/typeclasses/applicative/).
 *
 * ```kotlin:ank:playground
 * import arrow.core.extensions.list.apply.ap
 *
 * val value =
 * //sampleStart
 *  listOf(1, 2, 3).ap(listOf({ x: Int -> x + 10 }, { x: Int -> x * 2 }))
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ### Supported type classes
 *
 * ```kotlin:ank:replace
 * import arrow.reflect.DataType
 * import arrow.reflect.tcMarkdownList
 * import arrow.core.ListK
 *
 * DataType(ListK::class).tcMarkdownList()
 * ```
 *
 */
@higherkind
data class ListK<out A>(private val list: List<A>) : ListKOf<A>, List<A> by list {

  fun <B> flatMap(f: (A) -> ListKOf<B>): ListK<B> = list.flatMap { f(it).fix().list }.k()

  fun <B> map(f: (A) -> B): ListK<B> = list.map(f).k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = fold(b, f)

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: ListK<A>): Eval<B> = when {
      fa_p.list.isEmpty() -> lb
      else -> f(fa_p.fix().list.first(), Eval.defer { loop(fa_p.list.drop(1).k()) })
    }
    return Eval.defer { loop(this) }
  }

  override fun equals(other: Any?): Boolean =
    when (other) {
      is ListK<*> -> this.list == other.list
      is List<*> -> this.list == other
      else -> false
    }

  fun <B> ap(ff: ListKOf<(A) -> B>): ListK<B> = ff.fix().flatMap { f -> map(f) }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ListK<B>> =
    foldRight(Eval.always { GA.just(emptyList<B>().k()) }) { a, eval ->
      GA.run { f(a).map2Eval(eval) { (listOf(it.a) + it.b).k() } }
    }.value()

  fun <B, Z> map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    flatMap { a ->
      fb.fix().map { b ->
        f(Tuple2(a, b))
      }
    }

  fun <B> filterMap(f: (A) -> Option<B>): ListK<B> =
    flatMap { a -> f(a).fold({ empty<B>() }, { just(it) }) }

  override fun hashCode(): Int = list.hashCode()

  companion object {

    fun <A> just(a: A): ListK<A> = listOf(a).k()

    fun <A> empty(): ListK<A> = emptyList<A>().k()

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(
      buf: ArrayList<B>,
      f: (A) -> Kind<ForListK, Either<A, B>>,
      v: ListK<Either<A, B>>
    ) {
      if (!v.isEmpty()) {
        val head: Either<A, B> = v.first()
        when (head) {
          is Either.Right -> {
            buf += head.b
            go(buf, f, v.drop(1).k())
          }
          is Either.Left -> go(buf, f, (f(head.a).fix() + v.drop(1)).k())
        }
      }
    }

    fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForListK, Either<A, B>>): ListK<B> {
      val buf = ArrayList<B>()
      go(buf, f, f(a).fix())
      return ListK(buf)
    }
  }
}

fun <A> ListKOf<A>.combineK(y: ListKOf<A>): ListK<A> =
  (fix() + y.fix()).k()

fun <A> List<A>.k(): ListK<A> = ListK(this)
