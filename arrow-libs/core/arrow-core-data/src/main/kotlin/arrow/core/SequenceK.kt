package arrow.core

import arrow.Kind
import arrow.KindDeprecation
import arrow.typeclasses.Applicative
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

const val SequenceKDeprecation =
  "SequenceK is deprecated along side Higher Kinded Types in Arrow. Prefer to simply use kotlin.sequences.Sequence instead." +
    "Arrow provides extension functions on kotlin.sequences.Sequence to cover all the behavior defined for SequenceK"

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
class ForSequenceK private constructor() {
  companion object
}

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias SequenceKOf<A> = arrow.Kind<ForSequenceK, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <A> SequenceKOf<A>.fix(): SequenceK<A> =
  this as SequenceK<A>

@Deprecated(SequenceKDeprecation)
data class SequenceK<out A>(val sequence: Sequence<A>) : SequenceKOf<A>, Sequence<A> by sequence {

  fun <B> flatMap(f: (A) -> SequenceKOf<B>): SequenceK<B> = sequence.flatMap { f(it).fix().sequence }.k()

  fun <B> ap(ff: SequenceKOf<(A) -> B>): SequenceK<B> = flatMap { a -> ff.fix().map { f -> f(a) } }

  fun <B> map(f: (A) -> B): SequenceK<B> = sequence.map(f).k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = fold(b, f)

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun Iterator<A>.loop(): Eval<B> =
      if (hasNext()) f(next(), Eval.defer { loop() }) else lb
    return Eval.defer { this.iterator().loop() }
  }

  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, SequenceK<B>> =
    foldRight(Eval.now(GA.just(emptyList<B>().k()))) { a, eval ->
      GA.run { f(a).apEval(eval.map { it.map { xs -> { b: B -> (listOf(b) + xs).k() } } }) }
    }.value().let { GA.run { it.map { it.asSequence().k() } } }

  fun <B, Z> map2(fb: SequenceKOf<B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    flatMap { a ->
      fb.fix().map { b ->
        f(Tuple2(a, b))
      }
    }

  @Deprecated("Deprecated, use mapNotNull(f: (A) -> B?) instead", ReplaceWith("mapNotNull(f: (A) -> B?)"))
  fun <B> filterMap(f: (A) -> Option<B>): SequenceK<B> =
    map(f).filter { it.isDefined() }.map { it.orNull()!! }.k()

  /**
   * Returns a [SequenceK] containing the transformed values from the original
   * [SequenceK] filtering out any null value.
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.*
   *
   * //sampleStart
   * val evenStrings = listOf(1, 2).asSequence().k().mapNotNull {
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
  fun <B> mapNotNull(f: (A) -> B?): SequenceK<B> =
    flatMap { a ->
      when (val b = f(a)) {
        null -> empty<B>()
        else -> just(b)
      }
    }

  fun toList(): List<A> = this.fix().sequence.toList()

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>): String =
    "Sequence(${toList().k().show(SA)})"

  override fun toString(): String =
    sequence.toString()

  companion object {

    @Deprecated(SequenceKDeprecation, ReplaceWith("emptySequence<A>()"))
    fun <A> empty(): SequenceK<A> = emptySequence<A>().k()

    @Deprecated(SequenceKDeprecation, ReplaceWith("sequenceOf<A>(a)"))
    fun <A> just(a: A): SequenceK<A> = sequenceOf(a).k()

    @PublishedApi
    internal val unit: Sequence<Unit> =
      sequenceOf(Unit)

    @Deprecated("tailRecM for Sequence is a terminal operator that breaks the Sequence semantics and will be no longer be supported")
    fun <A, B> tailRecM(a: A, f: (A) -> SequenceKOf<Either<A, B>>): SequenceK<B> {
      tailrec fun <A, B> go(
        buf: MutableList<B>,
        f: (A) -> SequenceKOf<Either<A, B>>,
        v: SequenceK<Either<A, B>>
      ) {
        if (v.toList().isNotEmpty()) {
          val head: Either<A, B> = v.first()
          when (head) {
            is Either.Right -> {
              buf += head.b
              go(buf, f, v.drop(1).k())
            }
            is Either.Left -> {
              if (v.count() == 1) {
                go(buf, f, (f(head.a).fix()).k())
              } else {
                go(buf, f, (f(head.a).fix() + v.drop(1)).k())
              }
            }
          }
        }
      }

      val buf = mutableListOf<B>()
      go(buf, f, f(a).fix())
      return SequenceK(buf.asSequence())
    }
  }
}

@Deprecated(SequenceKDeprecation, ReplaceWith("this + y"))
fun <A> SequenceKOf<A>.combineK(y: SequenceKOf<A>): SequenceK<A> = (fix().sequence + y.fix().sequence).k()

@Deprecated("Applicative is deprecated use sequenceEither or sequenceValidated on Sequence instead.")
fun <A, G> SequenceKOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, SequenceK<A>> =
  fix().traverse(GA, ::identity)

@Deprecated(SequenceKDeprecation, ReplaceWith("this"))
fun <A> Sequence<A>.k(): SequenceK<A> = SequenceK(this)
