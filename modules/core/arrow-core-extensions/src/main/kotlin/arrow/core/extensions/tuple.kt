@file:Suppress("UnusedImports")

package arrow.core.extensions

import arrow.Kind
import arrow.core.*
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.extension
import arrow.typeclasses.*
import arrow.core.extensions.traverse as tuple2Traverse

//TODO this should be user driven allowing consumers to generate the tuple arities on demand to avoid cluttering arrow dependents with unused code
//TODO @arities(fromTupleN = 2, toTupleN = 22 | fromHListN = 1, toHListN = 22)

@extension
interface Tuple2Functor<F> : Functor<Tuple2PartialOf<F>> {
  override fun <A, B> Tuple2Of<F, A>.map(f: (A) -> B) =
    fix().map(f)
}

@extension
interface Tuple2Applicative<F> : Applicative<Tuple2PartialOf<F>>, Tuple2Functor<F> {
  fun MF(): Monoid<F>

  override fun <A, B> Tuple2Of<F, A>.map(f: (A) -> B) =
    fix().map(f)

  override fun <A, B> Tuple2Of<F, A>.ap(ff: Tuple2Of<F, (A) -> B>) =
    fix().ap(ff.fix())

  override fun <A> just(a: A) =
    MF().empty() toT a
}

@extension
interface Tuple2Monad<F> : Monad<Tuple2PartialOf<F>>, Tuple2Applicative<F> {

  override fun MF(): Monoid<F>

  override fun <A, B> Tuple2Of<F, A>.map(f: (A) -> B) =
    fix().map(f)

  override fun <A, B> Tuple2Of<F, A>.ap(ff: Tuple2Of<F, (A) -> B>) =
    fix().ap(ff)

  override fun <A, B> Tuple2Of<F, A>.flatMap(f: (A) -> Tuple2Of<F, B>) =
    fix().flatMap { f(it).fix() }

  override tailrec fun <A, B> tailRecM(a: A, f: (A) -> Tuple2Of<F, Either<A, B>>): Tuple2<F, B> {
    val b = f(a).fix().b
    return when (b) {
      is Left -> tailRecM(b.a, f)
      is Right -> just(b.b)
    }
  }
}

@extension
interface Tuple2Bifunctor : Bifunctor<ForTuple2> {
  override fun <A, B, C, D> Tuple2Of<A, B>.bimap(
    fl: (A) -> C,
    fr: (B) -> D
  ) = fix().bimap(fl, fr)
}

@extension
interface Tuple2Comonad<F> : Comonad<Tuple2PartialOf<F>>, Tuple2Functor<F> {
  override fun <A, B> Tuple2Of<F, A>.coflatMap(f: (Tuple2Of<F, A>) -> B) =
    fix().coflatMap(f)

  override fun <A> Tuple2Of<F, A>.extract() =
    fix().extract()
}

@extension
interface Tuple2Foldable<F> : Foldable<Tuple2PartialOf<F>> {
  override fun <A, B> Tuple2Of<F, A>.foldLeft(b: B, f: (B, A) -> B) =
    fix().foldL(b, f)

  override fun <A, B> Tuple2Of<F, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
    fix().foldR(lb, f)
}

fun <F, G, A, B> Tuple2Of<F, A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Tuple2<F, B>> = GA.run {
  fix().let { f(it.b).map(it.a::toT) }
}

fun <F, G, A> Tuple2Of<F, Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Tuple2<F, A>> =
  fix().tuple2Traverse(GA, ::identity)

@extension
interface Tuple2Traverse<F> : Traverse<Tuple2PartialOf<F>>, Tuple2Foldable<F> {

  override fun <G, A, B> Tuple2Of<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Tuple2<F, B>> =
    tuple2Traverse(AP, f)
}

@extension
interface Tuple2Semigroup<A, B> : Semigroup<Tuple2<A, B>> {

  fun SA(): Semigroup<A>

  fun SB(): Semigroup<B>

  override fun Tuple2<A, B>.combine(b: Tuple2<A, B>): Tuple2<A, B> {
    val (xa, xb) = this
    val (ya, yb) = b
    return Tuple2(SA().run { xa.combine(ya) }, SB().run { xb.combine(yb) })
  }
}

@extension
interface Tuple2Monoid<A, B> : Monoid<Tuple2<A, B>>, Tuple2Semigroup<A, B> {

  fun MA(): Monoid<A>

  fun MB(): Monoid<B>

  override fun SA(): Semigroup<A> = MA()

  override fun SB(): Semigroup<B> = MB()

  override fun empty(): Tuple2<A, B> = Tuple2(MA().empty(), MB().empty())
}

@extension
interface Tuple2Eq<A, B> : Eq<Tuple2<A, B>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  override fun Tuple2<A, B>.eqv(b: Tuple2<A, B>): Boolean =
    EQA().run { a.eqv(b.a) && EQB().run { this@eqv.b.eqv(b.b) } }
}

@extension
interface Tuple2Show<A, B> : Show<Tuple2<A, B>> {
  override fun Tuple2<A, B>.show(): String =
    toString()
}

@extension
interface Tuple2Hash<A, B> : Hash<Tuple2<A, B>>, Tuple2Eq<A, B> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>

  override fun EQA(): Eq<A> = HA()
  override fun EQB(): Eq<B> = HB()

  override fun Tuple2<A, B>.hash(): Int = listOf(
    HA().run { a.hash() },
    HB().run { b.hash() }
  ).fold(1) { hash, v -> 31 * hash + v }
}

@extension
interface Tuple3Eq<A, B, C> : Eq<Tuple3<A, B, C>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  override fun Tuple3<A, B, C>.eqv(b: Tuple3<A, B, C>): Boolean =
    EQA().run { a.eqv(b.a) }
      && EQB().run { this@eqv.b.eqv(b.b) }
      && EQC().run { c.eqv(b.c) }
}

@extension
interface Tuple3Show<A, B, C> : Show<Tuple3<A, B, C>> {
  override fun Tuple3<A, B, C>.show(): String =
    toString()
}

@extension
interface Tuple3Hash<A, B, C> : Hash<Tuple3<A, B, C>>, Tuple3Eq<A, B, C> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>

  override fun EQA(): Eq<A> = HA()
  override fun EQB(): Eq<B> = HB()
  override fun EQC(): Eq<C> = HC()

  override fun Tuple3<A, B, C>.hash(): Int = listOf(
    HA().run { a.hash() },
    HB().run { b.hash() },
    HC().run { c.hash() }
  ).fold(1) { hash, v -> 31 * hash + v }
}

@extension
interface Tuple4Eq<A, B, C, D> : Eq<Tuple4<A, B, C, D>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  override fun Tuple4<A, B, C, D>.eqv(b: Tuple4<A, B, C, D>): Boolean =
    EQA().run { a.eqv(b.a) }
      && EQB().run { this@eqv.b.eqv(b.b) }
      && EQC().run { c.eqv(b.c) }
      && EQD().run { d.eqv(b.d) }
}

@extension
interface Tuple4Hash<A, B, C, D> : Hash<Tuple4<A, B, C, D>>, Tuple4Eq<A, B, C, D> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>

  override fun EQA(): Eq<A> = HA()
  override fun EQB(): Eq<B> = HB()
  override fun EQC(): Eq<C> = HC()
  override fun EQD(): Eq<D> = HD()

  override fun Tuple4<A, B, C, D>.hash(): Int = listOf(
    HA().run { a.hash() },
    HB().run { b.hash() },
    HC().run { c.hash() },
    HD().run { d.hash() }
  ).fold(1) { hash, v -> 31 * hash + v }
}

@extension
interface Tuple4Show<A, B, C, D> : Show<Tuple4<A, B, C, D>> {
  override fun Tuple4<A, B, C, D>.show(): String =
    toString()
}

@extension
interface Tuple5Eq<A, B, C, D, E> : Eq<Tuple5<A, B, C, D, E>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  fun EQE(): Eq<E>

  override fun Tuple5<A, B, C, D, E>.eqv(b: Tuple5<A, B, C, D, E>): Boolean =
    EQA().run { a.eqv(b.a) }
      && EQB().run { this@eqv.b.eqv(b.b) }
      && EQC().run { c.eqv(b.c) }
      && EQD().run { d.eqv(b.d) }
      && EQE().run { e.eqv(b.e) }

}

@extension
interface Tuple5Hash<A, B, C, D, E> : Hash<Tuple5<A, B, C, D, E>>, Tuple5Eq<A, B, C, D, E> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>

  override fun EQA(): Eq<A> = HA()
  override fun EQB(): Eq<B> = HB()
  override fun EQC(): Eq<C> = HC()
  override fun EQD(): Eq<D> = HD()
  override fun EQE(): Eq<E> = HE()

  override fun Tuple5<A, B, C, D, E>.hash(): Int = listOf(
    HA().run { a.hash() },
    HB().run { b.hash() },
    HC().run { c.hash() },
    HD().run { d.hash() },
    HE().run { e.hash() }
  ).fold(1) { hash, v -> 31 * hash + v }
}

@extension
interface Tuple5Show<A, B, C, D, E> : Show<Tuple5<A, B, C, D, E>> {
  override fun Tuple5<A, B, C, D, E>.show(): String =
    toString()
}

@extension
interface Tuple6Eq<A, B, C, D, E, F> : Eq<Tuple6<A, B, C, D, E, F>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  fun EQE(): Eq<E>

  fun EQF(): Eq<F>

  override fun Tuple6<A, B, C, D, E, F>.eqv(b: Tuple6<A, B, C, D, E, F>): Boolean =
    EQA().run { a.eqv(b.a) }
      && EQB().run { this@eqv.b.eqv(b.b) }
      && EQC().run { c.eqv(b.c) }
      && EQD().run { d.eqv(b.d) }
      && EQE().run { e.eqv(b.e) }
      && EQF().run { f.eqv(b.f) }

}

@extension
interface Tuple6Hash<A, B, C, D, E, F> : Hash<Tuple6<A, B, C, D, E, F>>, Tuple6Eq<A, B, C, D, E, F> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>
  fun HF(): Hash<F>

  override fun EQA(): Eq<A> = HA()
  override fun EQB(): Eq<B> = HB()
  override fun EQC(): Eq<C> = HC()
  override fun EQD(): Eq<D> = HD()
  override fun EQE(): Eq<E> = HE()
  override fun EQF(): Eq<F> = HF()

  override fun Tuple6<A, B, C, D, E, F>.hash(): Int = listOf(
    HA().run { a.hash() },
    HB().run { b.hash() },
    HC().run { c.hash() },
    HD().run { d.hash() },
    HE().run { e.hash() },
    HF().run { f.hash() }
  ).fold(1) { hash, v -> 31 * hash + v }
}

@extension
interface Tuple6Show<A, B, C, D, E, F> : Show<Tuple6<A, B, C, D, E, F>> {
  override fun Tuple6<A, B, C, D, E, F>.show(): String =
    toString()
}

@extension
interface Tuple7Eq<A, B, C, D, E, F, G> : Eq<Tuple7<A, B, C, D, E, F, G>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  fun EQE(): Eq<E>

  fun EQF(): Eq<F>

  fun EQG(): Eq<G>

  override fun Tuple7<A, B, C, D, E, F, G>.eqv(b: Tuple7<A, B, C, D, E, F, G>): Boolean =
    EQA().run { a.eqv(b.a) }
      && EQB().run { this@eqv.b.eqv(b.b) }
      && EQC().run { c.eqv(b.c) }
      && EQD().run { d.eqv(b.d) }
      && EQE().run { e.eqv(b.e) }
      && EQF().run { f.eqv(b.f) }
      && EQG().run { g.eqv(b.g) }

}

@extension
interface Tuple7Hash<A, B, C, D, E, F, G> : Hash<Tuple7<A, B, C, D, E, F, G>>, Tuple7Eq<A, B, C, D, E, F, G> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>
  fun HF(): Hash<F>
  fun HG(): Hash<G>

  override fun EQA(): Eq<A> = HA()
  override fun EQB(): Eq<B> = HB()
  override fun EQC(): Eq<C> = HC()
  override fun EQD(): Eq<D> = HD()
  override fun EQE(): Eq<E> = HE()
  override fun EQF(): Eq<F> = HF()
  override fun EQG(): Eq<G> = HG()

  override fun Tuple7<A, B, C, D, E, F, G>.hash(): Int = listOf(
    HA().run { a.hash() },
    HB().run { b.hash() },
    HC().run { c.hash() },
    HD().run { d.hash() },
    HE().run { e.hash() },
    HF().run { f.hash() },
    HG().run { g.hash() }
  ).fold(1) { hash, v -> 31 * hash + v }
}

@extension
interface Tuple7Show<A, B, C, D, E, F, G> : Show<Tuple7<A, B, C, D, E, F, G>> {
  override fun Tuple7<A, B, C, D, E, F, G>.show(): String =
    toString()
}

@extension
interface Tuple8Eq<A, B, C, D, E, F, G, H> : Eq<Tuple8<A, B, C, D, E, F, G, H>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  fun EQE(): Eq<E>

  fun EQF(): Eq<F>

  fun EQG(): Eq<G>

  fun EQH(): Eq<H>

  override fun Tuple8<A, B, C, D, E, F, G, H>.eqv(b: Tuple8<A, B, C, D, E, F, G, H>): Boolean =
    EQA().run { a.eqv(b.a) }
      && EQB().run { this@eqv.b.eqv(b.b) }
      && EQC().run { c.eqv(b.c) }
      && EQD().run { d.eqv(b.d) }
      && EQE().run { e.eqv(b.e) }
      && EQF().run { f.eqv(b.f) }
      && EQG().run { g.eqv(b.g) }
      && EQH().run { h.eqv(b.h) }

}

@extension
interface Tuple8Hash<A, B, C, D, E, F, G, H> : Hash<Tuple8<A, B, C, D, E, F, G, H>>, Tuple8Eq<A, B, C, D, E, F, G, H> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>
  fun HF(): Hash<F>
  fun HG(): Hash<G>
  fun HH(): Hash<H>

  override fun EQA(): Eq<A> = HA()
  override fun EQB(): Eq<B> = HB()
  override fun EQC(): Eq<C> = HC()
  override fun EQD(): Eq<D> = HD()
  override fun EQE(): Eq<E> = HE()
  override fun EQF(): Eq<F> = HF()
  override fun EQG(): Eq<G> = HG()
  override fun EQH(): Eq<H> = HH()

  override fun Tuple8<A, B, C, D, E, F, G, H>.hash(): Int = listOf(
    HA().run { a.hash() },
    HB().run { b.hash() },
    HC().run { c.hash() },
    HD().run { d.hash() },
    HE().run { e.hash() },
    HF().run { f.hash() },
    HG().run { g.hash() },
    HH().run { h.hash() }
  ).fold(1) { hash, v -> 31 * hash + v }
}

@extension
interface Tuple8Show<A, B, C, D, E, F, G, H> : Show<Tuple8<A, B, C, D, E, F, G, H>> {
  override fun Tuple8<A, B, C, D, E, F, G, H>.show(): String =
    toString()
}

@extension
interface Tuple9Eq<A, B, C, D, E, F, G, H, I> : Eq<Tuple9<A, B, C, D, E, F, G, H, I>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  fun EQE(): Eq<E>

  fun EQF(): Eq<F>

  fun EQG(): Eq<G>

  fun EQH(): Eq<H>

  fun EQI(): Eq<I>

  override fun Tuple9<A, B, C, D, E, F, G, H, I>.eqv(b: Tuple9<A, B, C, D, E, F, G, H, I>): Boolean =
    EQA().run { a.eqv(b.a) }
      && EQB().run { this@eqv.b.eqv(b.b) }
      && EQC().run { c.eqv(b.c) }
      && EQD().run { d.eqv(b.d) }
      && EQE().run { e.eqv(b.e) }
      && EQF().run { f.eqv(b.f) }
      && EQG().run { g.eqv(b.g) }
      && EQH().run { h.eqv(b.h) }
      && EQI().run { i.eqv(b.i) }

}

@extension
interface Tuple9Hash<A, B, C, D, E, F, G, H, I> : Hash<Tuple9<A, B, C, D, E, F, G, H, I>>, Tuple9Eq<A, B, C, D, E, F, G, H, I> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>
  fun HF(): Hash<F>
  fun HG(): Hash<G>
  fun HH(): Hash<H>
  fun HI(): Hash<I>

  override fun EQA(): Eq<A> = HA()
  override fun EQB(): Eq<B> = HB()
  override fun EQC(): Eq<C> = HC()
  override fun EQD(): Eq<D> = HD()
  override fun EQE(): Eq<E> = HE()
  override fun EQF(): Eq<F> = HF()
  override fun EQG(): Eq<G> = HG()
  override fun EQH(): Eq<H> = HH()
  override fun EQI(): Eq<I> = HI()

  override fun Tuple9<A, B, C, D, E, F, G, H, I>.hash(): Int = listOf(
    HA().run { a.hash() },
    HB().run { b.hash() },
    HC().run { c.hash() },
    HD().run { d.hash() },
    HE().run { e.hash() },
    HF().run { f.hash() },
    HG().run { g.hash() },
    HH().run { h.hash() },
    HI().run { i.hash() }
  ).fold(1) { hash, v -> 31 * hash + v }
}

@extension
interface Tuple9Show<A, B, C, D, E, F, G, H, I> : Show<Tuple9<A, B, C, D, E, F, G, H, I>> {
  override fun Tuple9<A, B, C, D, E, F, G, H, I>.show(): String =
    toString()
}

@extension
interface Tuple10Eq<A, B, C, D, E, F, G, H, I, J> : Eq<Tuple10<A, B, C, D, E, F, G, H, I, J>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  fun EQE(): Eq<E>

  fun EQF(): Eq<F>

  fun EQG(): Eq<G>

  fun EQH(): Eq<H>

  fun EQI(): Eq<I>

  fun EQJ(): Eq<J>

  override fun Tuple10<A, B, C, D, E, F, G, H, I, J>.eqv(b: Tuple10<A, B, C, D, E, F, G, H, I, J>): Boolean =
    EQA().run { a.eqv(b.a) }
      && EQB().run { this@eqv.b.eqv(b.b) }
      && EQC().run { c.eqv(b.c) }
      && EQD().run { d.eqv(b.d) }
      && EQE().run { e.eqv(b.e) }
      && EQF().run { f.eqv(b.f) }
      && EQG().run { g.eqv(b.g) }
      && EQH().run { h.eqv(b.h) }
      && EQI().run { i.eqv(b.i) }
      && EQJ().run { j.eqv(b.j) }

}

@extension
interface Tuple10Hash<A, B, C, D, E, F, G, H, I, J> : Hash<Tuple10<A, B, C, D, E, F, G, H, I, J>>, Tuple10Eq<A, B, C, D, E, F, G, H, I, J> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>
  fun HF(): Hash<F>
  fun HG(): Hash<G>
  fun HH(): Hash<H>
  fun HI(): Hash<I>
  fun HJ(): Hash<J>

  override fun EQA(): Eq<A> = HA()
  override fun EQB(): Eq<B> = HB()
  override fun EQC(): Eq<C> = HC()
  override fun EQD(): Eq<D> = HD()
  override fun EQE(): Eq<E> = HE()
  override fun EQF(): Eq<F> = HF()
  override fun EQG(): Eq<G> = HG()
  override fun EQH(): Eq<H> = HH()
  override fun EQI(): Eq<I> = HI()
  override fun EQJ(): Eq<J> = HJ()

  override fun Tuple10<A, B, C, D, E, F, G, H, I, J>.hash(): Int = listOf(
    HA().run { a.hash() },
    HB().run { b.hash() },
    HC().run { c.hash() },
    HD().run { d.hash() },
    HE().run { e.hash() },
    HF().run { f.hash() },
    HG().run { g.hash() },
    HH().run { h.hash() },
    HI().run { i.hash() },
    HJ().run { j.hash() }
  ).fold(1) { hash, v -> 31 * hash + v }
}

@extension
interface Tuple10Show<A, B, C, D, E, F, G, H, I, J> : Show<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
  override fun Tuple10<A, B, C, D, E, F, G, H, I, J>.show(): String =
    toString()
}
