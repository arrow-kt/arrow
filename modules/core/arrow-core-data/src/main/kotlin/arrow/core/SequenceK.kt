package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative

@higherkind
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
      GA.run { Eval.later { f(a).lazyAp { eval.value().map { xs -> { b: B -> (listOf(b) + xs).k() } } } } }
    }.value().let { GA.run { it.map { it.asSequence().k() } } }

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
        if (v.toList().isNotEmpty()) {
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
