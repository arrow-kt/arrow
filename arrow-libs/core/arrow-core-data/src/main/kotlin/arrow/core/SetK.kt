package arrow.core

import arrow.KindDeprecation
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

const val SetKDeprecation =
  "SetK is deprecated along side Higher Kinded Types in Arrow. Prefer to simply use kotlin.collections.Set instead." +
    "Arrow provides extension functions on Iterable and kotlin.collections.Set to cover all the behavior defined for SetK"

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
) class ForSetK private constructor() { companion object }

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
) typealias SetKOf<A> = arrow.Kind<ForSetK, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <A> SetKOf<A>.fix(): SetK<A> =
  this as SetK<A>

@Deprecated(SetKDeprecation)
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

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>): String = "Set(${toList().k().show(SA)})"

  override fun toString(): String =
    set.toString()

  companion object {

    @Deprecated(SetKDeprecation, ReplaceWith("setOf(a)"))
    fun <A> just(a: A): SetK<A> = setOf(a).k()

    @Deprecated(SetKDeprecation, ReplaceWith("emptySet<Nothing>()"))
    fun empty(): SetK<Nothing> = empty

    private val empty = emptySet<Nothing>().k()
  }
}

@Deprecated(SetKDeprecation, ReplaceWith("this + y"))
fun <A> SetKOf<A>.combineK(y: SetKOf<A>): SetK<A> = (fix() + y.fix()).k()

@Deprecated(SetKDeprecation, ReplaceWith("this"))
fun <A> Set<A>.k(): SetK<A> = SetK(this)
