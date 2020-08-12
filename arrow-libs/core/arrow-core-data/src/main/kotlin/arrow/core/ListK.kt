package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Show

/**
 *
 * ank_macro_hierarchy(arrow.core.ListK)
 *
 *
 * ListK wraps over the platform `List` type to make it a [type constructor]({{'/patterns/glossary/#type-constructors' | relative_url }}).
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
 * For instance, it has `combineK` from the [SemigroupK]({{'/arrow/typeclasses/semigroupk/' | relative_url }}) typeclass.
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
 * The functions `traverse` and `sequence` come from [Traverse]({{'/apidocs/arrow-core-data/arrow.typeclasses/-traverse/' | relative_url }}).
 *
 * Traversing a list creates a new container [Kind<F, A>]({{'/patterns/glossary/#type-constructors' | relative_url }}) by combining the result of a function applied to each element:
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
 * If you want to aggregate the elements of a list into any other value you can use `foldLeft` and `foldRight` from [Foldable]({{'/arrow/typeclasses/foldable' | relative_url }}).
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
 * Or you can apply a list of transformations using `ap` from [Applicative]({{'/arrow/typeclasses/applicative/' | relative_url }}).
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

  fun <B> ap(ff: ListKOf<(A) -> B>): ListK<B> = flatMap { a -> ff.fix().map { f -> f(a) } }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ListK<B>> =
    foldRight(Eval.now(GA.just(emptyList<B>().k()))) { a, eval ->
      GA.run { f(a).apEval(eval.map { it.map { xs -> { a: B -> (listOf(a) + xs).k() } } }) }
    }.value()

  fun <B, Z> map2(fb: ListKOf<B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    flatMap { a ->
      fb.fix().map { b ->
        f(Tuple2(a, b))
      }
    }

  @Deprecated("Deprecated, use mapNotNull(f: (A) -> B?) instead", ReplaceWith("mapNotNull(f: (A) -> B?)"))
  fun <B> filterMap(f: (A) -> Option<B>): ListK<B> =
    flatMap { a -> f(a).fold({ empty<B>() }, { just(it) }) }

  /**
   * Returns a [ListK] containing the transformed values from the original
   * [ListK] filtering out any null value.
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.*
   *
   * //sampleStart
   * val evenStrings = listOf(1, 2).k().mapNotNull {
   *   when (it % 2 == 0) {
   *     true -> it.toString()
   *     else -> null
   *   }
   * }
   * //sampleEnd
   *
   * fun main() {
   *   println("evenStrings = $evenStrings")
   * }
   * ```
   */
  fun <B> mapNotNull(f: (A) -> B?): ListK<B> =
    flatMap { a -> f(a)?.let { just(it) } ?: empty<B>() }

  override fun hashCode(): Int = list.hashCode()

  /**
   * Align two Lists as in zip, but filling in blanks with None.
   */
  @Deprecated("Deprecated, use `padZipWithNull` instead", ReplaceWith("padZipWithNull(other: ListK<B>)"))
  fun <B> padZip(
    other: ListK<B>
  ): ListK<Tuple2<Option<A>, Option<B>>> =
    alignWith(this, other) { ior ->
      ior.fold(
        { it.some() toT Option.empty<B>() },
        { Option.empty<A>() toT it.some() },
        { a, b -> a.some() toT b.some() })
    }

  /**
   * Returns a [ListK<Tuple2<A?, B?>>] containing the zipped values of the two listKs
   * with null for padding.
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.*
   *
   * //sampleStart
   * val padRight = listOf(1, 2).k().padZip(listOf("a").k())        // Result: ListK(Tuple2(1, "a"), Tuple2(2, null))
   * val padLeft = listOf(1).k().padZip(listOf("a", "b").k())       // Result: ListK(Tuple2(1, "a"), Tuple2(null, "b"))
   * val noPadding = listOf(1, 2).k().padZip(listOf("a", "b").k())  // Result: ListK(Tuple2(1, "a"), Tuple2(2, "b"))
   * //sampleEnd
   *
   * fun main() {
   *   println("padRight = $padRight")
   *   println("padLeft = $padLeft")
   *   println("noPadding = $noPadding")
   * }
   * ```
   */
  fun <B> padZipWithNull(
    other: ListK<B>
  ): ListK<Tuple2<A?, B?>> =
    alignWith(this, other) { ior ->
      ior.fold(
        { it toT null },
        { null toT it },
        { a, b -> a toT b })
    }

  /**
   * Align two Lists as in zipWith, but filling in blanks with None.
   */
  @Deprecated("Deprecated, use `padZip(other: ListK<B>, fa: (A?, B?) -> C)` instead", ReplaceWith("padZip(other: ListK<B>, fa: (A?, B?) -> C)"))
  fun <B, C> padZipWith(
    other: ListK<B>,
    fa: (Option<A>, Option<B>) -> C
  ): ListK<C> =
    padZip(other).map { fa(it.a, it.b) }

  /**
   * Returns a [ListK<C>] containing the result of applying some transformation `(A?, B?) -> C`
   * on a zip.
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.*
   *
   * //sampleStart
   * val padZipRight = listOf(1, 2).k().padZip(listOf("a").k()) { l, r -> l toT r }.k()     // Result: ListK(Tuple2(1, "a"), Tuple2(2, null))
   * val padZipLeft = listOf(1).k().padZip(listOf("a", "b").k()) { l, r -> l toT r }.k()    // Result: ListK(Tuple2(1, "a"), Tuple2(null, "b"))
   * val noPadding = listOf(1, 2).k().padZip(listOf("a", "b").k()) { l, r -> l toT r }.k()  // Result: ListK(Tuple2(1, "a"), Tuple2(2, "b"))
   * //sampleEnd
   *
   * fun main() {
   *   println("padZipRight = $padZipRight")
   *   println("padZipLeft = $padZipLeft")
   *   println("noPadding = $noPadding")
   * }
   * ```
   */
  fun <B, C> padZip(
    other: ListK<B>,
    fa: (A?, B?) -> C
  ): ListK<C> =
    padZipWithNull(other).map { fa(it.a, it.b) }

  /**
   * Left-padded zipWith.
   */
  @Deprecated("Deprecated, use `leftPadZip(other: ListK<B>, fab: (A?, B) -> C)` instead", ReplaceWith("leftPadZip(other: ListK<B>, fab: (A?, B) -> C)"))
  fun <B, C> lpadZipWith(
    other: ListK<B>,
    fab: (Option<A>, B) -> C
  ): ListK<C> =
    padZipWith(other) { a: Option<A>, b -> b.map { fab(a, it) } }.filterMap(::identity)

  /**
   * Returns a [ListK<C>] containing the result of applying some transformation `(A?, B) -> C`
   * on a zip, excluding all cases where the right value is null.
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.*
   *
   * //sampleStart
   * val left = listOf(1, 2).k().leftPadZip(listOf("a").k()) { l, r -> l toT r }.k()      // Result: ListK(Tuple2(1, "a"))
   * val right = listOf(1).k().leftPadZip(listOf("a", "b").k()) { l, r -> l toT r }.k()   // Result: ListK(Tuple2(1, "a"), Tuple2(null, "b"))
   * val both = listOf(1, 2).k().leftPadZip(listOf("a", "b").k()) { l, r -> l toT r }.k() // Result: ListK(Tuple2(1, "a"), Tuple2(2, "b"))
   * //sampleEnd
   *
   * fun main() {
   *   println("left = $left")
   *   println("right = $right")
   *   println("both = $both")
   * }
   * ```
   */
  fun <B, C> leftPadZip(
    other: ListK<B>,
    fab: (A?, B) -> C
  ): ListK<C> =
    padZip(other) { a: A?, b: B? -> b?.let { fab(a, it) } }.mapNotNull(::identity)

  /**
   * Left-padded zip.
   */
  fun <B> lpadZip(
    other: ListK<B>
  ): ListK<Tuple2<Option<A>, B>> =
    this.lpadZipWith(other) { a, b -> a toT b }

  /**
   * Right-padded zipWith.
   */
  fun <B, C> rpadZipWith(
    other: ListK<B>,
    fa: (A, Option<B>) -> C
  ): ListK<C> =
    other.lpadZipWith(this) { a, b -> fa(b, a) }

  /**
   * Right-padded zip.
   */
  fun <B> rpadZip(
    other: ListK<B>
  ): ListK<Tuple2<A, Option<B>>> =
    this.rpadZipWith(other) { a, b ->
      a toT b
    }

  fun show(SA: Show<A>): String = "[" +
    list.joinToString(", ") { SA.run { it.show() } } + "]"

  override fun toString(): String = show(Show.any())

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

    fun <A, B, C> alignWith(a: ListK<A>, b: ListK<B>, fa: (Ior<A, B>) -> C): ListK<C> = align(a, b).map(fa)

    fun <A, B> align(a: ListK<A>, b: ListK<B>): ListK<Ior<A, B>> = alignRec(a, b).k()

    private fun <X, Y> alignRec(ls: List<X>, rs: List<Y>): List<Ior<X, Y>> = when {
      ls.isEmpty() -> rs.map { it.rightIor() }
      rs.isEmpty() -> ls.map { it.leftIor() }
      else -> listOf(Ior.Both(ls.first(), rs.first())) + alignRec(ls.drop(1), rs.drop(1))
    }
  }
}

fun <A> ListKOf<A>.combineK(y: ListKOf<A>): ListK<A> =
  (fix() + y.fix()).k()

fun <A> List<A>.k(): ListK<A> = ListK(this)

fun <A> listKOf(vararg elements: A): ListK<A> =
  listOf(*elements).k()
