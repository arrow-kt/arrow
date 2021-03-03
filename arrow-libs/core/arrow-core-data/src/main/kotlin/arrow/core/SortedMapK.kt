package arrow.core

const val SortedMapKDeprecation =
  "SortedMapK is deprecated along side Higher Kinded Types in Arrow. Prefer to simply use java.util.SortedMap instead." +
    "Arrow provides extension functions on java.util.SortedMap and kotlin.collections.Map to cover all the behavior defined for SortedMapK"

@Deprecated(SortedMapKDeprecation)
data class SortedMapK<A : Comparable<A>, B>(private val map: SortedMap<A, B>) : SortedMap<A, B> by map {

  fun <C> map(f: (B) -> C): SortedMapK<A, C> =
    this.map.map { it.key to f(it.value) }.toMap().toSortedMap().k()

  fun <C> foldRight(c: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    this.map.values.iterator().iterateRight(c, f)

  fun <C> foldLeft(c: C, f: (C, B) -> C): C = this.map.values.fold(c, f)

  override fun equals(other: Any?): Boolean =
    when (other) {
      is SortedMapK<*, *> -> this.map == other.map
      is SortedMap<*, *> -> this.map == other
      else -> false
    }

  override fun hashCode(): Int = map.hashCode()

  override fun toString(): String =
    map.toString()

  companion object
}

@Deprecated(SortedMapKDeprecation, ReplaceWith("this"))
fun <A : Comparable<A>, B> SortedMap<A, B>.k(): SortedMapK<A, B> = SortedMapK(this)

@Deprecated(SortedMapKDeprecation, ReplaceWith("this.map { (k, v) -> Pair(k, v) }.toMap().toSortedMap()"))
fun <A : Comparable<A>, B> List<Map.Entry<A, B>>.k(): SortedMapK<A, B> =
  this.map { it.key to it.value }.toMap().toSortedMap().k()

@Deprecated(SortedMapKDeprecation, ReplaceWith("Option.fromNullable(this[k])", "arrow.core.Option"))
fun <A, B> SortedMap<A, B>.getOption(k: A): Option<B> = Option.fromNullable(this[k])

@Deprecated(SortedMapKDeprecation, ReplaceWith("this.apply { put(k, value) }"))
fun <A : Comparable<A>, B> SortedMapK<A, B>.updated(k: A, value: B): SortedMapK<A, B> =
  this.apply { put(k, value) }

fun <A, B, C> SortedMap<A, B>.foldLeft(b: SortedMap<A, C>, f: (SortedMap<A, C>, Map.Entry<A, B>) -> SortedMap<A, C>): SortedMap<A, C> {
  var result = b
  this.forEach { result = f(result, it) }
  return result
}

fun <A : Comparable<A>, B, C> SortedMap<A, B>.foldRight(b: SortedMap<A, C>, f: (Map.Entry<A, B>, SortedMap<A, C>) -> SortedMap<A, C>): SortedMap<A, C> =
  this.entries.reversed().k().foldLeft(b) { x: SortedMap<A, C>, y: Map.Entry<A, B> -> f(y, x) }
