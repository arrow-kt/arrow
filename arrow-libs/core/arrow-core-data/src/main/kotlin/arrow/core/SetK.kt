package arrow.core

import arrow.typeclasses.Show

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
class ForSetK private constructor() { companion object }

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
typealias SetKOf<A> = arrow.Kind<ForSetK, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
inline fun <A> SetKOf<A>.fix(): SetK<A> =
  this as SetK<A>

data class SetK<out A>(private val set: Set<A>) : SetKOf<A>, Set<A> by set {

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = fold(b, f)

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: SetK<A>): Eval<B> = when {
      fa_p.isEmpty() -> lb
      else -> f(fa_p.first(), Eval.defer { loop(fa_p.set.asSequence().drop(1).toSet().k()) })
    }
    return Eval.defer { loop(this) }
  }

  override fun equals(other: Any?): Boolean =
    when (other) {
      is SetK<*> -> this.set == other.set
      is Set<*> -> this.set == other
      else -> false
    }

  fun show(SA: Show<A>): String = "Set(${toList().k().show(SA)})"

  override fun toString(): String = show(Show.any())

  companion object {

    fun <A> just(a: A): SetK<A> = setOf(a).k()

    fun empty(): SetK<Nothing> = empty

    private val empty = emptySet<Nothing>().k()
  }
}

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <A> SetKOf<A>.combineK(y: SetKOf<A>): SetK<A> = (fix() + y.fix()).k()

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <A> Set<A>.k(): SetK<A> = SetK(this)
