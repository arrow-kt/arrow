package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative

@higherkind
data class SequenceK<out A>(val sequence: Sequence<A>) : SequenceKOf<A>, Sequence<A> by sequence {

  fun <B> flatMap(f: (A) -> SequenceKOf<B>): SequenceK<B> = sequence.flatMap { f(it).fix().sequence }.k()

  fun <B> ap(ff: SequenceKOf<(A) -> B>): SequenceK<B> = ff.fix().flatMap { f -> map(f) }

  fun <B> map(f: (A) -> B): SequenceK<B> = sequence.map(f).k()

  fun <B> foldLeft(b: B, f: (B, A) -> B): B = fold(b, f)

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
    fun loop(fa_p: SequenceK<A>): Eval<B> = when {
      fa_p.sequence.none() -> lb
      else -> f(fa_p.first(), Eval.defer { loop(fa_p.drop(1).k()) })
    }
    return Eval.defer { loop(this) }
  }

  /**
   * Note: This will always evaluate the entire sequence because it uses applicative internally which
   *  takes only strict arguments. This will fail on infinite sequences. If you need this to work on
   *  infinite sequences your best bet is to define a new traverse instance together with a lazy version of
   *  ap from Applicative for whatever applicative you want to use.
   */
  fun <G, B> traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, SequenceK<B>> =
    foldRight(Eval.always { GA.just(emptySequence<B>().k()) }) { a, eval ->
      GA.run { f(a).map2Eval(eval) { (sequenceOf(it.a) + it.b).k() } }
    }.value()

  fun <B, Z> map2(fb: SequenceKOf<B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
    flatMap { a ->
      fb.fix().map { b ->
        f(Tuple2(a, b))
      }
    }

  fun <B> filterMap(f: (A) -> Option<B>): SequenceK<B> =
    map(f).filter { it.isDefined() }.map { it.orNull()!! }.k()

  fun toList(): List<A> = this.fix().sequence.toList()

  companion object {

    fun <A> just(a: A): SequenceK<A> = sequenceOf(a).k()

    fun <A> empty(): SequenceK<A> = emptySequence<A>().k()

    fun <A, B> tailRecM(a: A, f: (A) -> SequenceKOf<Either<A, B>>): SequenceK<B> {
      tailrec fun <A, B> go(
        buf: MutableList<B>,
        f: (A) -> SequenceKOf<Either<A, B>>,
        v: SequenceK<Either<A, B>>
      ) {
        if (!(v.toList().isEmpty())) {
          val head: Either<A, B> = v.first()
          when (head) {
            is Either.Right -> {
              buf += head.b
              go(buf, f, v.drop(1).k())
            }
            is Either.Left -> {
              if (v.count() == 1)
                go(buf, f, (f(head.a).fix()).k())
              else
                go(buf, f, (f(head.a).fix() + v.drop(1)).k())
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

fun <A> SequenceKOf<A>.combineK(y: SequenceKOf<A>): SequenceK<A> = (fix().sequence + y.fix().sequence).k()

fun <A, G> SequenceKOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, SequenceK<A>> =
  fix().traverse(GA, ::identity)

fun <A> Sequence<A>.k(): SequenceK<A> = SequenceK(this)
