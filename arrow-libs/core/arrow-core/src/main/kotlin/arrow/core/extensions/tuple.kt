@file:Suppress("UnusedImports")

package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Eval
import arrow.core.ForTuple2
import arrow.core.Ordering
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple2Of
import arrow.core.Tuple2PartialOf
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.list.foldable.fold
import arrow.core.extensions.ordering.monoid.monoid
import arrow.core.fix
import arrow.core.identity
import arrow.core.toT
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.core.extensions.traverse as tuple2Traverse

// TODO this should be user driven allowing consumers to generate the tuple arities on demand to avoid cluttering arrow dependents with unused code
// TODO @arities(fromTupleN = 2, toTupleN = 22 | fromHListN = 1, toHListN = 22)

interface Tuple2Functor<F> : Functor<Tuple2PartialOf<F>> {
  override fun <A, B> Tuple2Of<F, A>.map(f: (A) -> B) =
    fix().map(f)
}

interface Tuple2Apply<F> : Apply<Tuple2PartialOf<F>>, Tuple2Functor<F> {

  override fun <A, B> Tuple2Of<F, A>.map(f: (A) -> B) =
    fix().map(f)

  override fun <A, B> Tuple2Of<F, A>.ap(ff: Tuple2Of<F, (A) -> B>) =
    fix().ap(ff.fix())
}

interface Tuple2Applicative<F> : Applicative<Tuple2PartialOf<F>>, Tuple2Functor<F> {
  fun MF(): Monoid<F>

  override fun <A, B> Tuple2Of<F, A>.map(f: (A) -> B) =
    fix().map(f)

  override fun <A, B> Tuple2Of<F, A>.ap(ff: Tuple2Of<F, (A) -> B>) =
    fix().ap(ff.fix())

  override fun <A> just(a: A) =
    MF().empty() toT a
}

interface Tuple2Monad<F> : Monad<Tuple2PartialOf<F>>, Tuple2Applicative<F> {

  override fun MF(): Monoid<F>

  override fun <A, B> Tuple2Of<F, A>.map(f: (A) -> B) =
    fix().map(f)

  override fun <A, B> Tuple2Of<F, A>.ap(ff: Tuple2Of<F, (A) -> B>) =
    fix().ap(ff)

  override fun <A, B> Tuple2Of<F, A>.flatMap(f: (A) -> Tuple2Of<F, B>) =
    fix().flatMap { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Tuple2Of<F, Either<A, B>>): Tuple2<F, B> {
    val b = f(a).fix().b
    return when (b) {
      is Left -> tailRecM(b.a, f)
      is Right -> just(b.b)
    }
  }
}

interface Tuple2Bifunctor : Bifunctor<ForTuple2> {
  override fun <A, B, C, D> Tuple2Of<A, B>.bimap(
    fl: (A) -> C,
    fr: (B) -> D
  ) = fix().bimap(fl, fr)
}

interface Tuple2Comonad<F> : Comonad<Tuple2PartialOf<F>>, Tuple2Functor<F> {
  override fun <A, B> Tuple2Of<F, A>.coflatMap(f: (Tuple2Of<F, A>) -> B) =
    fix().coflatMap(f)

  override fun <A> Tuple2Of<F, A>.extract() =
    fix().extract()
}

interface Tuple2Foldable<F> : Foldable<Tuple2PartialOf<F>> {
  override fun <A, B> Tuple2Of<F, A>.foldLeft(b: B, f: (B, A) -> B) =
    fix().foldL(b, f)

  override fun <A, B> Tuple2Of<F, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>) =
    fix().foldR(lb, f)
}

interface Tuple2Bifoldable : Bifoldable<ForTuple2> {
  override fun <A, B, C> Tuple2Of<A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C = fix().let { g(f(c, it.a), it.b) }

  override fun <A, B, C> Tuple2Of<A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().let { f(it.a, g(it.b, c)) }
}

fun <F, G, A, B> Tuple2Of<F, A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Tuple2<F, B>> = GA.run {
  fix().let { f(it.b).map(it.a::toT) }
}

fun <F, G, A> Tuple2Of<F, Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Tuple2<F, A>> =
  fix().tuple2Traverse(GA, ::identity)

interface Tuple2Traverse<F> : Traverse<Tuple2PartialOf<F>>, Tuple2Foldable<F> {

  override fun <G, A, B> Tuple2Of<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Tuple2<F, B>> =
    tuple2Traverse(AP, f)
}

interface Tuple2Bitraverse : Bitraverse<ForTuple2>, Tuple2Bifoldable {
  override fun <G, A, B, C, D> Tuple2Of<A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>): Kind<G, Tuple2Of<C, D>> =
    AP.run {
      fix().let { tupledN(f(it.a), g(it.b)) }
    }
}

interface Tuple2Semigroup<A, B> : Semigroup<Tuple2<A, B>> {

  fun SA(): Semigroup<A>

  fun SB(): Semigroup<B>

  override fun Tuple2<A, B>.combine(b: Tuple2<A, B>): Tuple2<A, B> {
    val (xa, xb) = this
    val (ya, yb) = b
    return Tuple2(SA().run { xa.combine(ya) }, SB().run { xb.combine(yb) })
  }
}

interface Tuple2Monoid<A, B> : Monoid<Tuple2<A, B>>, Tuple2Semigroup<A, B> {

  fun MA(): Monoid<A>

  fun MB(): Monoid<B>

  override fun SA(): Semigroup<A> = MA()

  override fun SB(): Semigroup<B> = MB()

  override fun empty(): Tuple2<A, B> = Tuple2(MA().empty(), MB().empty())
}

interface Tuple2Eq<A, B> : Eq<Tuple2<A, B>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  override fun Tuple2<A, B>.eqv(b: Tuple2<A, B>): Boolean =
    EQA().run { a.eqv(b.a) && EQB().run { this@eqv.b.eqv(b.b) } }
}

interface Tuple2Show<A, B> : Show<Tuple2<A, B>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  override fun Tuple2<A, B>.show(): String =
    show(SA(), SB())
}

interface Tuple2Hash<A, B> : Hash<Tuple2<A, B>> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>

  override fun Tuple2<A, B>.hashWithSalt(salt: Int): Int =
    HA().run { HB().run { a.hashWithSalt(b.hashWithSalt(salt)) } }
}

@Deprecated(OrderDeprecation)
interface Tuple2Order<A, B> : Order<Tuple2<A, B>> {
  fun OA(): Order<A>
  fun OB(): Order<B>

  override fun Tuple2<A, B>.compare(other: Tuple2<A, B>): Ordering = listOf(
    OA().run { a.compare(other.a) },
    OB().run { b.compare(other.b) }
  ).fold(Ordering.monoid())
}

interface Tuple3Eq<A, B, C> : Eq<Tuple3<A, B, C>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  override fun Tuple3<A, B, C>.eqv(b: Tuple3<A, B, C>): Boolean =
    EQA().run { a.eqv(b.a) } &&
      EQB().run { this@eqv.b.eqv(b.b) } &&
      EQC().run { c.eqv(b.c) }
}

interface Tuple3Show<A, B, C> : Show<Tuple3<A, B, C>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  fun SC(): Show<C>
  override fun Tuple3<A, B, C>.show(): String =
    show(SA(), SB(), SC())
}

interface Tuple3Hash<A, B, C> : Hash<Tuple3<A, B, C>> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>

  override fun Tuple3<A, B, C>.hashWithSalt(salt: Int): Int =
    HA().run { HB().run { HC().run { a.hashWithSalt(b.hashWithSalt(c.hashWithSalt(salt))) } } }
}

@Deprecated(OrderDeprecation)
interface Tuple3Order<A, B, C> : Order<Tuple3<A, B, C>> {
  fun OA(): Order<A>
  fun OB(): Order<B>
  fun OC(): Order<C>

  override fun Tuple3<A, B, C>.compare(other: Tuple3<A, B, C>): Ordering = listOf(
    OA().run { a.compare(other.a) },
    OB().run { b.compare(other.b) },
    OC().run { c.compare(other.c) }
  ).fold(Ordering.monoid())
}

interface Tuple4Eq<A, B, C, D> : Eq<Tuple4<A, B, C, D>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  override fun Tuple4<A, B, C, D>.eqv(b: Tuple4<A, B, C, D>): Boolean =
    EQA().run { a.eqv(b.a) } &&
      EQB().run { this@eqv.b.eqv(b.b) } &&
      EQC().run { c.eqv(b.c) } &&
      EQD().run { d.eqv(b.d) }
}

interface Tuple4Hash<A, B, C, D> : Hash<Tuple4<A, B, C, D>> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>

  override fun Tuple4<A, B, C, D>.hashWithSalt(salt: Int): Int =
    HA().run {
      HB().run {
        HC().run {
          HD().run {
            a.hashWithSalt(b.hashWithSalt(c.hashWithSalt(d.hashWithSalt(salt))))
          }
        }
      }
    }
}

@Deprecated(OrderDeprecation)
interface Tuple4Order<A, B, C, D> : Order<Tuple4<A, B, C, D>> {
  fun OA(): Order<A>
  fun OB(): Order<B>
  fun OC(): Order<C>
  fun OD(): Order<D>

  override fun Tuple4<A, B, C, D>.compare(other: Tuple4<A, B, C, D>): Ordering = listOf(
    OA().run { a.compare(other.a) },
    OB().run { b.compare(other.b) },
    OC().run { c.compare(other.c) },
    OD().run { d.compare(other.d) }
  ).fold(Ordering.monoid())
}

interface Tuple4Show<A, B, C, D> : Show<Tuple4<A, B, C, D>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  fun SC(): Show<C>
  fun SD(): Show<D>
  override fun Tuple4<A, B, C, D>.show(): String =
    show(SA(), SB(), SC(), SD())
}

interface Tuple5Eq<A, B, C, D, E> : Eq<Tuple5<A, B, C, D, E>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  fun EQE(): Eq<E>

  override fun Tuple5<A, B, C, D, E>.eqv(b: Tuple5<A, B, C, D, E>): Boolean =
    EQA().run { a.eqv(b.a) } &&
      EQB().run { this@eqv.b.eqv(b.b) } &&
      EQC().run { c.eqv(b.c) } &&
      EQD().run { d.eqv(b.d) } &&
      EQE().run { e.eqv(b.e) }
}

interface Tuple5Hash<A, B, C, D, E> : Hash<Tuple5<A, B, C, D, E>> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>

  override fun Tuple5<A, B, C, D, E>.hashWithSalt(salt: Int): Int =
    HA().run {
      HB().run {
        HC().run {
          HD().run {
            HE().run {
              a.hashWithSalt(b.hashWithSalt(c.hashWithSalt(d.hashWithSalt(e.hashWithSalt(salt)))))
            }
          }
        }
      }
    }
}

@Deprecated(OrderDeprecation)
interface Tuple5Order<A, B, C, D, E> : Order<Tuple5<A, B, C, D, E>> {
  fun OA(): Order<A>
  fun OB(): Order<B>
  fun OC(): Order<C>
  fun OD(): Order<D>
  fun OE(): Order<E>

  override fun Tuple5<A, B, C, D, E>.compare(other: Tuple5<A, B, C, D, E>): Ordering = listOf(
    OA().run { a.compare(other.a) },
    OB().run { b.compare(other.b) },
    OC().run { c.compare(other.c) },
    OD().run { d.compare(other.d) },
    OE().run { e.compare(other.e) }
  ).fold(Ordering.monoid())
}

interface Tuple5Show<A, B, C, D, E> : Show<Tuple5<A, B, C, D, E>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  fun SC(): Show<C>
  fun SD(): Show<D>
  fun SE(): Show<E>
  override fun Tuple5<A, B, C, D, E>.show(): String =
    show(SA(), SB(), SC(), SD(), SE())
}

interface Tuple6Eq<A, B, C, D, E, F> : Eq<Tuple6<A, B, C, D, E, F>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  fun EQE(): Eq<E>

  fun EQF(): Eq<F>

  override fun Tuple6<A, B, C, D, E, F>.eqv(b: Tuple6<A, B, C, D, E, F>): Boolean =
    EQA().run { a.eqv(b.a) } &&
      EQB().run { this@eqv.b.eqv(b.b) } &&
      EQC().run { c.eqv(b.c) } &&
      EQD().run { d.eqv(b.d) } &&
      EQE().run { e.eqv(b.e) } &&
      EQF().run { f.eqv(b.f) }
}

interface Tuple6Hash<A, B, C, D, E, F> : Hash<Tuple6<A, B, C, D, E, F>> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>
  fun HF(): Hash<F>

  override fun Tuple6<A, B, C, D, E, F>.hashWithSalt(salt: Int): Int =
    HA().run {
      HB().run {
        HC().run {
          HD().run {
            HE().run {
              HF().run {
                a.hashWithSalt(b.hashWithSalt(c.hashWithSalt(d.hashWithSalt(e.hashWithSalt(f.hashWithSalt(salt))))))
              }
            }
          }
        }
      }
    }
}

@Deprecated(OrderDeprecation)
interface Tuple6Order<A, B, C, D, E, F> : Order<Tuple6<A, B, C, D, E, F>> {
  fun OA(): Order<A>
  fun OB(): Order<B>
  fun OC(): Order<C>
  fun OD(): Order<D>
  fun OE(): Order<E>
  fun OF(): Order<F>

  override fun Tuple6<A, B, C, D, E, F>.compare(other: Tuple6<A, B, C, D, E, F>): Ordering = listOf(
    OA().run { a.compare(other.a) },
    OB().run { b.compare(other.b) },
    OC().run { c.compare(other.c) },
    OD().run { d.compare(other.d) },
    OE().run { e.compare(other.e) },
    OF().run { f.compare(other.f) }
  ).fold(Ordering.monoid())
}

interface Tuple6Show<A, B, C, D, E, F> : Show<Tuple6<A, B, C, D, E, F>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  fun SC(): Show<C>
  fun SD(): Show<D>
  fun SE(): Show<E>
  fun SF(): Show<F>
  override fun Tuple6<A, B, C, D, E, F>.show(): String =
    show(SA(), SB(), SC(), SD(), SE(), SF())
}

interface Tuple7Eq<A, B, C, D, E, F, G> : Eq<Tuple7<A, B, C, D, E, F, G>> {

  fun EQA(): Eq<A>

  fun EQB(): Eq<B>

  fun EQC(): Eq<C>

  fun EQD(): Eq<D>

  fun EQE(): Eq<E>

  fun EQF(): Eq<F>

  fun EQG(): Eq<G>

  override fun Tuple7<A, B, C, D, E, F, G>.eqv(b: Tuple7<A, B, C, D, E, F, G>): Boolean =
    EQA().run { a.eqv(b.a) } &&
      EQB().run { this@eqv.b.eqv(b.b) } &&
      EQC().run { c.eqv(b.c) } &&
      EQD().run { d.eqv(b.d) } &&
      EQE().run { e.eqv(b.e) } &&
      EQF().run { f.eqv(b.f) } &&
      EQG().run { g.eqv(b.g) }
}

interface Tuple7Hash<A, B, C, D, E, F, G> : Hash<Tuple7<A, B, C, D, E, F, G>> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>
  fun HF(): Hash<F>
  fun HG(): Hash<G>

  override fun Tuple7<A, B, C, D, E, F, G>.hashWithSalt(salt: Int): Int =
    HA().run {
      HB().run {
        HC().run {
          HD().run {
            HE().run {
              HF().run {
                HG().run {
                  a.hashWithSalt(
                    b.hashWithSalt(
                      c.hashWithSalt(
                        d.hashWithSalt(
                          e.hashWithSalt(
                            f.hashWithSalt(
                              g.hashWithSalt(salt)
                            )
                          )
                        )
                      )
                    )
                  )
                }
              }
            }
          }
        }
      }
    }
}

@Deprecated(OrderDeprecation)
interface Tuple7Order<A, B, C, D, E, F, G> : Order<Tuple7<A, B, C, D, E, F, G>> {
  fun OA(): Order<A>
  fun OB(): Order<B>
  fun OC(): Order<C>
  fun OD(): Order<D>
  fun OE(): Order<E>
  fun OF(): Order<F>
  fun OG(): Order<G>

  override fun Tuple7<A, B, C, D, E, F, G>.compare(other: Tuple7<A, B, C, D, E, F, G>): Ordering = listOf(
    OA().run { a.compare(other.a) },
    OB().run { b.compare(other.b) },
    OC().run { c.compare(other.c) },
    OD().run { d.compare(other.d) },
    OE().run { e.compare(other.e) },
    OF().run { f.compare(other.f) },
    OG().run { g.compare(other.g) }
  ).fold(Ordering.monoid())
}

interface Tuple7Show<A, B, C, D, E, F, G> : Show<Tuple7<A, B, C, D, E, F, G>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  fun SC(): Show<C>
  fun SD(): Show<D>
  fun SE(): Show<E>
  fun SF(): Show<F>
  fun SG(): Show<G>
  override fun Tuple7<A, B, C, D, E, F, G>.show(): String =
    show(SA(), SB(), SC(), SD(), SE(), SF(), SG())
}

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
    EQA().run { a.eqv(b.a) } &&
      EQB().run { this@eqv.b.eqv(b.b) } &&
      EQC().run { c.eqv(b.c) } &&
      EQD().run { d.eqv(b.d) } &&
      EQE().run { e.eqv(b.e) } &&
      EQF().run { f.eqv(b.f) } &&
      EQG().run { g.eqv(b.g) } &&
      EQH().run { h.eqv(b.h) }
}

interface Tuple8Hash<A, B, C, D, E, F, G, H> : Hash<Tuple8<A, B, C, D, E, F, G, H>> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>
  fun HF(): Hash<F>
  fun HG(): Hash<G>
  fun HH(): Hash<H>

  override fun Tuple8<A, B, C, D, E, F, G, H>.hashWithSalt(salt: Int): Int =
    HA().run {
      HB().run {
        HC().run {
          HD().run {
            HE().run {
              HF().run {
                HG().run {
                  HH().run {
                    a.hashWithSalt(
                      b.hashWithSalt(
                        c.hashWithSalt(
                          d.hashWithSalt(
                            e.hashWithSalt(
                              f.hashWithSalt(
                                g.hashWithSalt(
                                  h.hashWithSalt(salt)
                                )))))))
                  }
                }
              }
            }
          }
        }
      }
    }
}

@Deprecated(OrderDeprecation)
interface Tuple8Order<A, B, C, D, E, F, G, H> : Order<Tuple8<A, B, C, D, E, F, G, H>> {
  fun OA(): Order<A>
  fun OB(): Order<B>
  fun OC(): Order<C>
  fun OD(): Order<D>
  fun OE(): Order<E>
  fun OF(): Order<F>
  fun OG(): Order<G>
  fun OH(): Order<H>

  override fun Tuple8<A, B, C, D, E, F, G, H>.compare(other: Tuple8<A, B, C, D, E, F, G, H>): Ordering = listOf(
    OA().run { a.compare(other.a) },
    OB().run { b.compare(other.b) },
    OC().run { c.compare(other.c) },
    OD().run { d.compare(other.d) },
    OE().run { e.compare(other.e) },
    OF().run { f.compare(other.f) },
    OG().run { g.compare(other.g) },
    OH().run { h.compare(other.h) }
  ).fold(Ordering.monoid())
}

interface Tuple8Show<A, B, C, D, E, F, G, H> : Show<Tuple8<A, B, C, D, E, F, G, H>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  fun SC(): Show<C>
  fun SD(): Show<D>
  fun SE(): Show<E>
  fun SF(): Show<F>
  fun SG(): Show<G>
  fun SH(): Show<H>
  override fun Tuple8<A, B, C, D, E, F, G, H>.show(): String =
    show(SA(), SB(), SC(), SD(), SE(), SF(), SG(), SH())
}

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
    EQA().run { a.eqv(b.a) } &&
      EQB().run { this@eqv.b.eqv(b.b) } &&
      EQC().run { c.eqv(b.c) } &&
      EQD().run { d.eqv(b.d) } &&
      EQE().run { e.eqv(b.e) } &&
      EQF().run { f.eqv(b.f) } &&
      EQG().run { g.eqv(b.g) } &&
      EQH().run { h.eqv(b.h) } &&
      EQI().run { i.eqv(b.i) }
}

interface Tuple9Hash<A, B, C, D, E, F, G, H, I> : Hash<Tuple9<A, B, C, D, E, F, G, H, I>> {
  fun HA(): Hash<A>
  fun HB(): Hash<B>
  fun HC(): Hash<C>
  fun HD(): Hash<D>
  fun HE(): Hash<E>
  fun HF(): Hash<F>
  fun HG(): Hash<G>
  fun HH(): Hash<H>
  fun HI(): Hash<I>

  override fun Tuple9<A, B, C, D, E, F, G, H, I>.hashWithSalt(salt: Int): Int =
    HA().run {
      HB().run {
        HC().run {
          HD().run {
            HE().run {
              HF().run {
                HG().run {
                  HH().run {
                    HI().run {
                      a.hashWithSalt(
                        b.hashWithSalt(
                          c.hashWithSalt(
                            d.hashWithSalt(
                              e.hashWithSalt(
                                f.hashWithSalt(
                                  g.hashWithSalt(
                                    h.hashWithSalt(
                                      i.hashWithSalt(salt)
                                    ))))))))
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

@Deprecated(OrderDeprecation)
interface Tuple9Order<A, B, C, D, E, F, G, H, I> : Order<Tuple9<A, B, C, D, E, F, G, H, I>> {
  fun OA(): Order<A>
  fun OB(): Order<B>
  fun OC(): Order<C>
  fun OD(): Order<D>
  fun OE(): Order<E>
  fun OF(): Order<F>
  fun OG(): Order<G>
  fun OH(): Order<H>
  fun OI(): Order<I>

  override fun Tuple9<A, B, C, D, E, F, G, H, I>.compare(other: Tuple9<A, B, C, D, E, F, G, H, I>): Ordering = listOf(
    OA().run { a.compare(other.a) },
    OB().run { b.compare(other.b) },
    OC().run { c.compare(other.c) },
    OD().run { d.compare(other.d) },
    OE().run { e.compare(other.e) },
    OF().run { f.compare(other.f) },
    OG().run { g.compare(other.g) },
    OH().run { h.compare(other.h) },
    OI().run { i.compare(other.i) }
  ).fold(Ordering.monoid())
}

interface Tuple9Show<A, B, C, D, E, F, G, H, I> : Show<Tuple9<A, B, C, D, E, F, G, H, I>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  fun SC(): Show<C>
  fun SD(): Show<D>
  fun SE(): Show<E>
  fun SF(): Show<F>
  fun SG(): Show<G>
  fun SH(): Show<H>
  fun SI(): Show<I>
  override fun Tuple9<A, B, C, D, E, F, G, H, I>.show(): String =
    show(SA(), SB(), SC(), SD(), SE(), SF(), SG(), SH(), SI())
}

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
    EQA().run { a.eqv(b.a) } &&
      EQB().run { this@eqv.b.eqv(b.b) } &&
      EQC().run { c.eqv(b.c) } &&
      EQD().run { d.eqv(b.d) } &&
      EQE().run { e.eqv(b.e) } &&
      EQF().run { f.eqv(b.f) } &&
      EQG().run { g.eqv(b.g) } &&
      EQH().run { h.eqv(b.h) } &&
      EQI().run { i.eqv(b.i) } &&
      EQJ().run { j.eqv(b.j) }
}

interface Tuple10Hash<A, B, C, D, E, F, G, H, I, J> : Hash<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
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

  override fun Tuple10<A, B, C, D, E, F, G, H, I, J>.hashWithSalt(salt: Int): Int =
    HA().run {
      HB().run {
        HC().run {
          HD().run {
            HE().run {
              HF().run {
                HG().run {
                  HH().run {
                    HI().run {
                      HJ().run {
                        a.hashWithSalt(
                          b.hashWithSalt(
                            c.hashWithSalt(
                              d.hashWithSalt(
                                e.hashWithSalt(
                                  f.hashWithSalt(
                                    g.hashWithSalt(
                                      h.hashWithSalt(
                                        i.hashWithSalt(
                                          j.hashWithSalt(salt)
                                        )))))))))
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
}

@Deprecated(OrderDeprecation)
interface Tuple10Order<A, B, C, D, E, F, G, H, I, J> : Order<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
  fun OA(): Order<A>
  fun OB(): Order<B>
  fun OC(): Order<C>
  fun OD(): Order<D>
  fun OE(): Order<E>
  fun OF(): Order<F>
  fun OG(): Order<G>
  fun OH(): Order<H>
  fun OI(): Order<I>
  fun OJ(): Order<J>

  override fun Tuple10<A, B, C, D, E, F, G, H, I, J>.compare(other: Tuple10<A, B, C, D, E, F, G, H, I, J>): Ordering = listOf(
    OA().run { a.compare(other.a) },
    OB().run { b.compare(other.b) },
    OC().run { c.compare(other.c) },
    OD().run { d.compare(other.d) },
    OE().run { e.compare(other.e) },
    OF().run { f.compare(other.f) },
    OG().run { g.compare(other.g) },
    OH().run { h.compare(other.h) },
    OI().run { i.compare(other.i) },
    OJ().run { j.compare(other.j) }
  ).fold(Ordering.monoid())
}

interface Tuple10Show<A, B, C, D, E, F, G, H, I, J> : Show<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  fun SC(): Show<C>
  fun SD(): Show<D>
  fun SE(): Show<E>
  fun SF(): Show<F>
  fun SG(): Show<G>
  fun SH(): Show<H>
  fun SI(): Show<I>
  fun SJ(): Show<J>
  override fun Tuple10<A, B, C, D, E, F, G, H, I, J>.show(): String =
    show(SA(), SB(), SC(), SD(), SE(), SF(), SG(), SH(), SI(), SJ())
}
