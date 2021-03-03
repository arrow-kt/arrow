package arrow.core

const val SequenceKDeprecation =
  "SequenceK is deprecated along side Higher Kinded Types in Arrow. Prefer to simply use kotlin.sequences.Sequence instead." +
    "Arrow provides extension functions on kotlin.sequences.Sequence to cover all the behavior defined for SequenceK"

@Deprecated(SequenceKDeprecation)
data class SequenceK<out A>(val sequence: Sequence<A>) : Sequence<A> by sequence {

  fun <B> flatMap(f: (A) -> SequenceK<B>): SequenceK<B> = sequence.flatMap { f(it).sequence }.k()

  fun <B> ap(ff: SequenceK<(A) -> B>): SequenceK<B> = flatMap { a -> ff.map { f -> f(a) } }

  fun <B> map(f: (A) -> B): SequenceK<B> = sequence.map(f).k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = fold(b, f)

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun Iterator<A>.loop(): Eval<B> =
      if (hasNext()) f(next(), Eval.defer { loop() }) else lb
    return Eval.defer { this.iterator().loop() }
  }

  fun <B, Z> map2(fb: SequenceK<B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    flatMap { a ->
      fb.map { b ->
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

  fun toList(): List<A> = this.sequence.toList()

  override fun toString(): String =
    sequence.toString()

  companion object {

    @Deprecated(SequenceKDeprecation, ReplaceWith("emptySequence<A>()"))
    fun <A> empty(): SequenceK<A> = emptySequence<A>().k()

    @Deprecated(SequenceKDeprecation, ReplaceWith("sequenceOf<A>(a)"))
    fun <A> just(a: A): SequenceK<A> = sequenceOf(a).k()

    fun <B, C, D> mapN(
      b: Sequence<B>,
      c: Sequence<C>,
      map: (B, C) -> D
    ): Sequence<D> =
      mapN(b, c, sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit)) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

    fun <B, C, D, E> mapN(
      b: Sequence<B>,
      c: Sequence<C>,
      d: Sequence<D>,
      map: (B, C, D) -> E
    ): Sequence<E> =
      mapN(b, c, d, sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit)) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

    fun <B, C, D, E, F> mapN(
      b: Sequence<B>,
      c: Sequence<C>,
      d: Sequence<D>,
      e: Sequence<E>,
      map: (B, C, D, E) -> F
    ): Sequence<F> =
      mapN(b, c, d, e, sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit)) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

    fun <B, C, D, E, F, G> mapN(
      b: Sequence<B>,
      c: Sequence<C>,
      d: Sequence<D>,
      e: Sequence<E>,
      f: Sequence<F>,
      map: (B, C, D, E, F) -> G
    ): Sequence<G> =
      mapN(b, c, d, e, f, sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit)) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

    fun <B, C, D, E, F, G, H> mapN(
      b: Sequence<B>,
      c: Sequence<C>,
      d: Sequence<D>,
      e: Sequence<E>,
      f: Sequence<F>,
      g: Sequence<G>,
      map: (B, C, D, E, F, G) -> H
    ): Sequence<H> =
      mapN(b, c, d, e, f, g, sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit)) { b, c, d, e, f, g, _, _, _, _ -> map(b, c, d, e, f, g) }

    fun <B, C, D, E, F, G, H, I> mapN(
      b: Sequence<B>,
      c: Sequence<C>,
      d: Sequence<D>,
      e: Sequence<E>,
      f: Sequence<F>,
      g: Sequence<G>,
      h: Sequence<H>,
      map: (B, C, D, E, F, G, H) -> I
    ): Sequence<I> =
      mapN(b, c, d, e, f, g, h, sequenceOf(Unit), sequenceOf(Unit), sequenceOf(Unit)) { b, c, d, e, f, g, h, _, _, _ -> map(b, c, d, e, f, g, h) }

    fun <B, C, D, E, F, G, H, I, J> mapN(
      b: Sequence<B>,
      c: Sequence<C>,
      d: Sequence<D>,
      e: Sequence<E>,
      f: Sequence<F>,
      g: Sequence<G>,
      h: Sequence<H>,
      i: Sequence<I>,
      map: (B, C, D, E, F, G, H, I) -> J
    ): Sequence<J> =
      mapN(b, c, d, e, f, g, h, i, sequenceOf(Unit), sequenceOf(Unit)) { b, c, d, e, f, g, h, i, _, _ -> map(b, c, d, e, f, g, h, i) }

    fun <B, C, D, E, F, G, H, I, J, K> mapN(
      b: Sequence<B>,
      c: Sequence<C>,
      d: Sequence<D>,
      e: Sequence<E>,
      f: Sequence<F>,
      g: Sequence<G>,
      h: Sequence<H>,
      i: Sequence<I>,
      j: Sequence<J>,
      map: (B, C, D, E, F, G, H, I, J) -> K
    ): Sequence<K> =
      mapN(b, c, d, e, f, g, h, i, j, sequenceOf(Unit)) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }

    fun <B, C, D, E, F, G, H, I, J, K, L> mapN(
      b: Sequence<B>,
      c: Sequence<C>,
      d: Sequence<D>,
      e: Sequence<E>,
      f: Sequence<F>,
      g: Sequence<G>,
      h: Sequence<H>,
      i: Sequence<I>,
      j: Sequence<J>,
      k: Sequence<K>,
      map: (B, C, D, E, F, G, H, I, J, K) -> L
    ): Sequence<L> =
      b.flatMap { bb ->
        c.flatMap { cc ->
          d.flatMap { dd ->
            e.flatMap { ee ->
              f.flatMap { ff ->
                g.flatMap { gg ->
                  h.flatMap { hh ->
                    i.flatMap { ii ->
                      j.flatMap { jj ->
                        k.map { kk ->
                          map(bb, cc, dd, ee, ff, gg, hh, ii, jj, kk)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

    @Deprecated("tailRecM for Sequence is a terminal operator that breaks the Sequence semantics and will be no longer be supported")
    fun <A, B> tailRecM(a: A, f: (A) -> SequenceK<Either<A, B>>): SequenceK<B> {
      tailrec fun <A, B> go(
        buf: MutableList<B>,
        f: (A) -> SequenceK<Either<A, B>>,
        v: SequenceK<Either<A, B>>
      ) {
        if (v.toList().isNotEmpty()) {
          val head: Either<A, B> = v.first()
          when (head) {
            is Either.Right -> {
              buf += head.value
              go(buf, f, v.drop(1).k())
            }
            is Either.Left -> {
              if (v.count() == 1) {
                go(buf, f, (f(head.value)).k())
              } else {
                go(buf, f, (f(head.value) + v.drop(1)).k())
              }
            }
          }
        }
      }

      val buf = mutableListOf<B>()
      go(buf, f, f(a))
      return SequenceK(buf.asSequence())
    }
  }
}

@Deprecated(SequenceKDeprecation, ReplaceWith("this + y"))
fun <A> SequenceK<A>.combineK(y: SequenceK<A>): SequenceK<A> = (sequence + y.sequence).k()

@Deprecated(SequenceKDeprecation, ReplaceWith("this"))
fun <A> Sequence<A>.k(): SequenceK<A> = SequenceK(this)
