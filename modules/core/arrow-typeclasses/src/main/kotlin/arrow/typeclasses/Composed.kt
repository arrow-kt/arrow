package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.Eval

/**
 * A type to represent λ[α => Kind[F, C, α]]
 *
 * Use unnest to expand it, nest to re-compose it
 */
interface Nested<out F, out G>

typealias NestedType<F, G, A> = Kind<Nested<F, G>, A>

typealias UnnestedType<F, G, A> = Kind<F, Kind<G, A>>

typealias BinestedType<F, G, A, B> = Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>

typealias BiunnestedType<F, G, A, B> = Kind2<Nested<F, G>, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> UnnestedType<F, G, A>.nest(): NestedType<F, G, A> = this as NestedType<F, G, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> NestedType<F, G, A>.unnest(): UnnestedType<F, G, A> = this as UnnestedType<F, G, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> BinestedType<F, G, A, B>.binest(): BiunnestedType<F, G, A, B> = this as BiunnestedType<F, G, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> BiunnestedType<F, G, A, B>.biunnest(): BinestedType<F, G, A, B> = this as BinestedType<F, G, A, B>

interface ComposedFoldable<F, G> :
  Foldable<Nested<F, G>> {

  fun FF(): Foldable<F>

  fun GF(): Foldable<G>

  override fun <A, B> NestedType<F, G, A>.foldLeft(b: B, f: (B, A) -> B): B =
    FF().run { unnest().foldLeft(b) { bb, aa -> GF().run { aa.foldLeft(bb, f) } } }

  fun <A, B> foldLC(fa: UnnestedType<F, G, A>, b: B, f: (B, A) -> B): B =
    fa.nest().foldLeft(b, f)

  override fun <A, B> NestedType<F, G, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    FF().run { unnest().foldRight(lb) { laa, lbb -> GF().run { laa.foldRight(lbb, f) } } }

  fun <A, B> UnnestedType<F, G, A>.foldRC(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    nest().foldRight(lb, f)

  companion object {
    operator fun <F, G> invoke(FF: Foldable<F>, GF: Foldable<G>): ComposedFoldable<F, G> =
      object : ComposedFoldable<F, G> {
        override fun FF(): Foldable<F> = FF

        override fun GF(): Foldable<G> = GF
      }
  }
}

fun <F, G> Foldable<F>.compose(GT: Foldable<G>): ComposedFoldable<F, G> = ComposedFoldable(this, GT)

interface ComposedTraverse<F, G> :
  Traverse<Nested<F, G>>,
  ComposedFoldable<F, G> {

  fun FT(): Traverse<F>

  fun GT(): Traverse<G>

  fun GA(): Applicative<G>

  override fun FF(): Foldable<F> = FT()

  override fun GF(): Foldable<G> = GT()

  override fun <H, A, B> NestedType<F, G, A>.traverse(AP: Applicative<H>, f: (A) -> Kind<H, B>): Kind<H, Kind<Nested<F, G>, B>> = AP.run {
    FT().run { unnest().traverse(AP) { ga -> GT().run { ga.traverse(AP, f) } } }.map { it.nest() }
  }

  fun <H, A, B> UnnestedType<F, G, A>.traverseC(f: (A) -> Kind<H, B>, HA: Applicative<H>): Kind<H, Kind<Nested<F, G>, B>> =
    nest().traverse(HA, f)

  companion object {
    operator fun <F, G> invoke(
      FF: Traverse<F>,
      GF: Traverse<G>,
      GA: Applicative<G>): ComposedTraverse<F, G> =
      object : ComposedTraverse<F, G> {
        override fun FT(): Traverse<F> = FF

        override fun GT(): Traverse<G> = GF

        override fun GA(): Applicative<G> = GA
      }
  }
}

fun <F, G> Traverse<F>.compose(GT: Traverse<G>, GA: Applicative<G>): Traverse<Nested<F, G>> = ComposedTraverse(this, GT, GA)

interface ComposedSemigroupK<F, G> : SemigroupK<Nested<F, G>> {

  fun F(): SemigroupK<F>

  override fun <A> NestedType<F, G, A>.combineK(y: NestedType<F, G, A>): NestedType<F, G, A> = F().run {
    unnest().combineK(y.unnest()).nest()
  }

  fun <A> UnnestedType<F, G, A>.combineKC(y: UnnestedType<F, G, A>): NestedType<F, G, A> =
    nest().combineK(y.nest())

  companion object {
    operator fun <F, G> invoke(SF: SemigroupK<F>): SemigroupK<Nested<F, G>> =
      object : ComposedSemigroupK<F, G> {
        override fun F(): SemigroupK<F> = SF
      }
  }
}

fun <F, G> SemigroupK<F>.compose(): SemigroupK<Nested<F, G>> = ComposedSemigroupK(this)

interface ComposedMonoidK<F, G> : MonoidK<Nested<F, G>>, ComposedSemigroupK<F, G> {

  override fun F(): MonoidK<F>

  override fun <A> empty(): NestedType<F, G, A> = F().empty<Kind<G, A>>().nest()

  fun <A> emptyC(): UnnestedType<F, G, A> = empty<A>().unnest()

  companion object {
    operator fun <F, G> invoke(MK: MonoidK<F>): MonoidK<Nested<F, G>> =
      object : ComposedMonoidK<F, G> {
        override fun F(): MonoidK<F> = MK
      }
  }
}

fun <F, G> MonoidK<F>.compose(): MonoidK<Nested<F, G>> = ComposedMonoidK(this)

interface ComposedInvariant<F, G> : Invariant<Nested<F, G>> {
  fun F(): Invariant<F>

  fun G(): Invariant<G>

  override fun <A, B> Kind<Nested<F, G>, A>.imap(f: (A) -> B, g: (B) -> A): Kind<Nested<F, G>, B> {
      val fl: (Kind<G, A>) -> Kind<G, B> = { ga -> G().run { ga.imap(f, g) } }
      val fr: (Kind<G, B>) -> Kind<G, A> = { gb -> G().run { gb.imap(g, f) } }
      return F().run { unnest().imap(fl, fr) }.nest()
  }

  companion object {
    operator fun <F, G> invoke(FF: Invariant<F>, GF: Invariant<G>): Invariant<Nested<F, G>> =
        object : ComposedInvariant<F, G> {
            override fun F(): Invariant<F> = FF

            override fun G(): Invariant<G> = GF
        }
  }
}

interface ComposedInvariantCovariant<F, G> : Invariant<Nested<F, G>> {
    fun F(): Invariant<F>

    fun G(): Functor<G>

    override fun <A, B> Kind<Nested<F, G>, A>.imap(f: (A) -> B, g: (B) -> A): Kind<Nested<F, G>, B> {
        val fl: (Kind<G, A>) -> Kind<G, B> = { ga -> G().run { ga.map(f) } }
        val fr: (Kind<G, B>) -> Kind<G, A> = { gb -> G().run { gb.map(g) } }
        return F().run { unnest().imap(fl, fr) }.nest()
    }

    companion object {
        operator fun <F, G> invoke(FF: Invariant<F>, GF: Functor<G>): Invariant<Nested<F, G>> =
            object : ComposedInvariantCovariant<F, G> {
                override fun F(): Invariant<F> = FF

                override fun G(): Functor<G> = GF
            }
    }
}

interface ComposedInvariantContravariant<F, G> : Invariant<Nested<F, G>> {
    fun F(): Invariant<F>

    fun G(): Contravariant<G>

    override fun <A, B> Kind<Nested<F, G>, A>.imap(f: (A) -> B, g: (B) -> A): Kind<Nested<F, G>, B> {
        val fl: (Kind<G, A>) -> Kind<G, B> = { ga -> G().run { ga.contramap(g) } }
        val fr: (Kind<G, B>) -> Kind<G, A> = { gb -> G().run { gb.contramap(f) } }
        return F().run { unnest().imap(fl, fr) }.nest()
    }

    companion object {
        operator fun <F, G> invoke(FF: Invariant<F>, GF: Contravariant<G>): Invariant<Nested<F, G>> =
            object : ComposedInvariantContravariant<F, G> {
                override fun F(): Invariant<F> = FF

                override fun G(): Contravariant<G> = GF
            }
    }
}

fun <F, G> Invariant<F>.compose(GF: Invariant<G>): Invariant<Nested<F, G>> = ComposedInvariant(this, GF)

fun <F, G> Invariant<F>.composeFunctor(GF: Functor<G>): Invariant<Nested<F, G>> =
    ComposedInvariantCovariant(this, GF)

fun <F, G> Invariant<F>.composeContravariant(GF: Contravariant<G>): Invariant<Nested<F, G>> =
    ComposedInvariantContravariant(this, GF)

interface ComposedFunctor<F, G> : Functor<Nested<F, G>> {
  fun F(): Functor<F>

  fun G(): Functor<G>

  override fun <A, B> NestedType<F, G, A>.map(f: (A) -> B): Kind<Nested<F, G>, B> = F().run {
    unnest().map { G().run { it.map(f) } }.nest()
  }

  fun <A, B> UnnestedType<F, G, A>.mapC(f: (A) -> B): Kind<F, Kind<G, B>> =
    nest().map(f).unnest()

  companion object {
    operator fun <F, G> invoke(FF: Functor<F>, GF: Functor<G>): Functor<Nested<F, G>> =
      object : ComposedFunctor<F, G> {
        override fun F(): Functor<F> = FF

        override fun G(): Functor<G> = GF
      }
  }
}

interface ComposedCovariantContravariant<F, G> : Contravariant<Nested<F, G>> {
    fun F(): Functor<F>

    fun G(): Contravariant<G>

    override fun <A, B> Kind<Nested<F, G>, A>.contramap(f: (B) -> A): Kind<Nested<F, G>, B> =
        F().run { unnest().map { G().run { it.contramap(f) } }.nest() }

    companion object {
        operator fun <F, G> invoke(FF: Functor<F>, GF: Contravariant<G>): Contravariant<Nested<F, G>> =
            object : ComposedCovariantContravariant<F, G> {
                override fun F(): Functor<F> = FF

                override fun G(): Contravariant<G> = GF
            }
    }
}

fun <F, G> Functor<F>.compose(GF: Functor<G>): Functor<Nested<F, G>> = ComposedFunctor(this, GF)

fun <F, G> Functor<F>.composeContravariant(GF: Contravariant<G>): Contravariant<Nested<F, G>> =
    ComposedCovariantContravariant(this, GF)

interface ComposedContravariant<F, G> : Functor<Nested<F, G>> {
    fun F(): Contravariant<F>

    fun G(): Contravariant<G>

    override fun <A, B> Kind<Nested<F, G>, A>.map(f: (A) -> B): Kind<Nested<F, G>, B> =
        F().run { unnest().contramap { gb: Kind<G, B> -> G().run { gb.contramap(f) } }.nest() }

    companion object {
      operator fun <F, G> invoke(FF: Contravariant<F>, GF: Contravariant<G>): Functor<Nested<F, G>> =
        object : ComposedContravariant<F, G> {
          override fun F(): Contravariant<F> = FF

          override fun G(): Contravariant<G> = GF
        }
    }
}

interface ComposedContravariantCovariant<F, G> : Contravariant<Nested<F, G>> {
    fun F(): Contravariant<F>

    fun G(): Functor<G>

    override fun <A, B> Kind<Nested<F, G>, A>.contramap(f: (B) -> A): Kind<Nested<F, G>, B> =
        F().run { unnest().contramap { gb: Kind<G, B> -> G().run { gb.map(f) } }.nest() }

    companion object {
        operator fun <F, G> invoke(FF: Contravariant<F>, GF: Functor<G>): Contravariant<Nested<F, G>> =
            object : ComposedContravariantCovariant<F, G> {
                override fun F(): Contravariant<F> = FF

                override fun G(): Functor<G> = GF
            }
    }
}

fun <F, G> Contravariant<F>.compose(GF: Contravariant<G>): Functor<Nested<F, G>> =
    ComposedContravariant(this, GF)

fun <F, G> Contravariant<F>.composeFunctor(GF: Functor<G>): Contravariant<Nested<F, G>> =
    ComposedContravariantCovariant(this, GF)

interface ComposedApplicative<F, G> : Applicative<Nested<F, G>>, ComposedFunctor<F, G> {
  override fun F(): Applicative<F>

  override fun G(): Applicative<G>

  override fun <A, B> NestedType<F, G, A>.map(f: (A) -> B): Kind<Nested<F, G>, B> =
    ap(just(f))

  override fun <A> just(a: A): NestedType<F, G, A> = F().just(G().just(a)).nest()

  override fun <A, B> NestedType<F, G, A>.ap(ff: Kind<Nested<F, G>, (A) -> B>): Kind<Nested<F, G>, B> =
    F().run { unnest().ap(ff.unnest().map { gfa: Kind<G, (A) -> B> -> { ga: Kind<G, A> -> G().run { ga.ap(gfa) } } }) }.nest()

  fun <A, B> UnnestedType<F, G, A>.apC(ff: Kind<F, Kind<G, (A) -> B>>): Kind<F, Kind<G, B>> =
    nest().ap(ff.nest()).unnest()

  companion object {
    operator fun <F, G> invoke(FF: Applicative<F>, GF: Applicative<G>)
      : Applicative<Nested<F, G>> =
      object : ComposedApplicative<F, G> {
        override fun F(): Applicative<F> = FF

        override fun G(): Applicative<G> = GF
      }
  }
}

fun <F, G> Applicative<F>.compose(GA: Applicative<G>): Applicative<Nested<F, G>> = ComposedApplicative(this, GA)

interface ComposedAlternative<F, G> : Alternative<Nested<F, G>>, ComposedApplicative<F, G>, ComposedMonoidK<F, G> {
  override fun F(): Alternative<F>

  companion object {
    operator fun <F, G> invoke(AF: Alternative<F>, AG: Applicative<G>)
      : Alternative<Nested<F, G>> =
      object : ComposedAlternative<F, G> {
        override fun F(): Alternative<F> = AF

        override fun G(): Applicative<G> = AG
      }
  }
}

fun <F, G> Alternative<F>.compose(GA: Applicative<G>): Alternative<Nested<F, G>> = ComposedAlternative(this, GA)

interface ComposedBifoldable<F, G> : Bifoldable<Nested<F, G>> {
  fun F(): Bifoldable<F>

  fun G(): Bifoldable<G>

  override fun <A, B, C> BiunnestedType<F, G, A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C = F().run {
    biunnest().bifoldLeft(c, { cc: C, gab: Kind2<G, A, B> -> G().run { gab.bifoldLeft(cc, f, g) } },
      { cc: C, gab: Kind2<G, A, B> -> G().run { gab.bifoldLeft(cc, f, g) } })
  }

  override fun <A, B, C> BiunnestedType<F, G, A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> = F().run {
    biunnest().bifoldRight(c, { gab: Kind2<G, A, B>, cc: Eval<C> -> G().run { gab.bifoldRight(cc, f, g) } },
      { gab: Kind2<G, A, B>, cc: Eval<C> -> G().run { gab.bifoldRight(cc, f, g) } })
  }

  fun <A, B, C> BinestedType<F, G, A, B>.bifoldLeftC(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    binest().bifoldLeft(c, f, g)

  fun <A, B, C> BinestedType<F, G, A, B>.bifoldRightC(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    binest().bifoldRight(c, f, g)

  companion object {
    operator fun <F, G> invoke(BF: Bifoldable<F>, BG: Bifoldable<G>): ComposedBifoldable<F, G> =
      object : ComposedBifoldable<F, G> {
        override fun F(): Bifoldable<F> = BF

        override fun G(): Bifoldable<G> = BG
      }
  }
}

fun <F, G> Bifoldable<F>.compose(BG: Bifoldable<G>): Bifoldable<Nested<F, G>> = ComposedBifoldable(this, BG)

interface ComposedBifunctor<F, G> : Bifunctor<Nested<F, G>> {
  val F: Bifunctor<F>
  val G: Bifunctor<G>

  override fun <A, B, C, D> BiunnestedType<F, G, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<Nested<F, G>, C, D> {
    val innerBimap = { gab: Kind2<G, A, B> -> G.run { gab.bimap(fl, fr) } }
    return F.run { biunnest().bimap(innerBimap, innerBimap) }.binest()
  }

  fun <A, B, C, D> BinestedType<F, G, A, B>.bimapC(fl: (A) -> C, fr: (B) -> D): Kind2<Nested<F, G>, C, D> =
    binest().bimap(fl, fr)

  companion object {
    operator fun <F, G> invoke(BF: Bifunctor<F>, BG: Bifunctor<G>): ComposedBifunctor<F, G> =
      object : ComposedBifunctor<F, G> {
        override val F: Bifunctor<F> = BF
        override val G: Bifunctor<G> = BG
      }
  }
}

fun <F, G> Bifunctor<F>.compose(BG: Bifunctor<G>): Bifunctor<Nested<F, G>> = ComposedBifunctor(this, BG)
