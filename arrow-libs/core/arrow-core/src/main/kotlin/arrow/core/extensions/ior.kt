package arrow.core.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForIor
import arrow.core.GT
import arrow.core.Ior
import arrow.core.IorOf
import arrow.core.IorPartialOf
import arrow.core.LT
import arrow.core.Ordering
import arrow.core.ap
import arrow.core.extensions.ior.eq.eq
import arrow.core.extensions.ior.monad.monad
import arrow.core.fix
import arrow.core.flatMap
import arrow.core.leftIor
import arrow.core.rightIor
import arrow.typeclasses.Align
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Bicrosswalk
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Crosswalk
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.hashWithSalt

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Semigroup.ior()", "arrow.core.ior", "arrow.typeclasses.Semigroup"),
  DeprecationLevel.WARNING
)
interface IorSemigroup<L, R> : Semigroup<Ior<L, R>> {

  fun SGL(): Semigroup<L>
  fun SGR(): Semigroup<R>

  override fun Ior<L, R>.combine(b: Ior<L, R>): Ior<L, R> =
    with(SGL()) {
      with(SGR()) {
        when (val a = this@combine) {
          is Ior.Left -> when (b) {
            is Ior.Left -> Ior.Left(a.value + b.value)
            is Ior.Right -> Ior.Both(a.value, b.value)
            is Ior.Both -> Ior.Both(a.value + b.leftValue, b.rightValue)
          }
          is Ior.Right -> when (b) {
            is Ior.Left -> Ior.Both(b.value, a.value)
            is Ior.Right -> Ior.Right(a.value + b.value)
            is Ior.Both -> Ior.Both(b.leftValue, a.value + b.rightValue)
          }
          is Ior.Both -> when (b) {
            is Ior.Left -> Ior.Both(a.leftValue + b.value, a.rightValue)
            is Ior.Right -> Ior.Both(a.leftValue, a.rightValue + b.value)
            is Ior.Both -> Ior.Both(a.leftValue + b.leftValue, a.rightValue + b.rightValue)
          }
        }
      }
    }
}

@Deprecated(
  message = "Functor typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorFunctor<L> : Functor<IorPartialOf<L>> {
  override fun <A, B> Kind<IorPartialOf<L>, A>.map(f: (A) -> B): Ior<L, B> = fix().map(f)
}

@Deprecated(
  message = "Bifunctor typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorBifunctor : Bifunctor<ForIor> {
  override fun <A, B, C, D> Kind2<ForIor, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<ForIor, C, D> =
    fix().bimap(fl, fr)
}

@Deprecated(
  message = "Apply typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorApply<L> : Apply<IorPartialOf<L>>, IorFunctor<L> {

  fun SL(): Semigroup<L>

  override fun <A, B> Kind<IorPartialOf<L>, A>.map(f: (A) -> B): Ior<L, B> = fix().map(f)

  override fun <A, B> Kind<IorPartialOf<L>, A>.ap(ff: Kind<IorPartialOf<L>, (A) -> B>): Ior<L, B> =
    fix().ap(SL(), ff)

  override fun <A, B> Kind<IorPartialOf<L>, A>.apEval(ff: Eval<Kind<IorPartialOf<L>, (A) -> B>>): Eval<Kind<IorPartialOf<L>, B>> =
    fix().fold(
      { l ->
        Eval.now(l.leftIor())
      },
      { r ->
        ff.map { it.fix().map { f -> f(r) } }
      },
      { l, r ->
        ff.map {
          it.fix().fold(
            { ll ->
              SL().run { l + ll }.leftIor()
            },
            { f ->
              Ior.Both(l, f(r))
            },
            { ll, f ->
              Ior.Both(SL().run { l + ll }, f(r))
            }
          )
        }
      }
    )
}

@Deprecated(
  message = "Applicative typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorApplicative<L> : Applicative<IorPartialOf<L>>, IorApply<L> {

  override fun SL(): Semigroup<L>

  override fun <A> just(a: A): Ior<L, A> = Ior.Right(a)

  override fun <A, B> Kind<IorPartialOf<L>, A>.map(f: (A) -> B): Ior<L, B> = fix().map(f)

  override fun <A, B> Kind<IorPartialOf<L>, A>.ap(ff: Kind<IorPartialOf<L>, (A) -> B>): Ior<L, B> =
    fix().ap(SL(), ff)
}

@Deprecated(
  message = "Monad typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorMonad<L> : Monad<IorPartialOf<L>>, IorApplicative<L> {

  override fun SL(): Semigroup<L>

  override fun <A, B> Kind<IorPartialOf<L>, A>.map(f: (A) -> B): Ior<L, B> = fix().map(f)

  override fun <A, B> Kind<IorPartialOf<L>, A>.flatMap(f: (A) -> Kind<IorPartialOf<L>, B>): Ior<L, B> =
    fix().flatMap(SL()) { f(it).fix() }

  override fun <A, B> Kind<IorPartialOf<L>, A>.ap(ff: Kind<IorPartialOf<L>, (A) -> B>): Ior<L, B> =
    fix().ap(SL(), ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> IorOf<L, Either<A, B>>): Ior<L, B> =
    Ior.tailRecM(a, f, SL())
}

@Deprecated(
  message = "Foldable typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorFoldable<L> : Foldable<IorPartialOf<L>> {

  override fun <B, C> Kind<IorPartialOf<L>, B>.foldLeft(b: C, f: (C, B) -> C): C = fix().foldLeft(b, f)

  override fun <B, C> Kind<IorPartialOf<L>, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(lb, f)
}

@Deprecated(
  message = "Traverse typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorTraverse<L> : Traverse<IorPartialOf<L>>, IorFoldable<L> {

  override fun <G, B, C> IorOf<L, B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Ior<L, C>> =
    fix().traverse(AP, f)
}

@Deprecated(
  message = "Bifoldable typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorBifoldable : Bifoldable<ForIor> {
  override fun <A, B, C> IorOf<A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fix().bifoldLeft(c, f, g)

  override fun <A, B, C> IorOf<A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().bifoldRight(c, f, g)
}

@Deprecated(
  message = "Bitraverse typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorBitraverse : Bitraverse<ForIor>, IorBifoldable {
  override fun <G, A, B, C, D> IorOf<A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>): Kind<G, IorOf<C, D>> =
    fix().let {
      AP.run {
        it.fold(
          { f(it).map { Ior.Left(it) } },
          { g(it).map { Ior.Right(it) } },
          { a, b -> mapN(f(a), g(b)) { Ior.Both(it.a, it.b) } }
        )
      }
    }
}

@Deprecated(
  message = "Eq typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorEq<L, R> : Eq<Ior<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun Ior<L, R>.eqv(b: Ior<L, R>): Boolean = when (this) {
    is Ior.Left -> when (b) {
      is Ior.Both -> false
      is Ior.Right -> false
      is Ior.Left -> EQL().run { value.eqv(b.value) }
    }
    is Ior.Both -> when (b) {
      is Ior.Left -> false
      is Ior.Both -> EQL().run { leftValue.eqv(b.leftValue) } && EQR().run { rightValue.eqv(b.rightValue) }
      is Ior.Right -> false
    }
    is Ior.Right -> when (b) {
      is Ior.Left -> false
      is Ior.Both -> false
      is Ior.Right -> EQR().run { value.eqv(b.value) }
    }
  }
}

@Deprecated(
  message = "EqK typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorEqK<A> : EqK<IorPartialOf<A>> {
  fun EQA(): Eq<A>

  override fun <B> Kind<IorPartialOf<A>, B>.eqK(other: Kind<IorPartialOf<A>, B>, EQ: Eq<B>): Boolean =
    Ior.eq(EQA(), EQ).run {
      this@eqK.fix().eqv(other.fix())
    }
}

@Deprecated(
  message = "EqK2 typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorEqK2 : EqK2<ForIor> {
  override fun <A, B> Kind2<ForIor, A, B>.eqK(other: Kind2<ForIor, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
    (this.fix() to other.fix()).let {
      Ior.eq(EQA, EQB).run {
        it.first.eqv(it.second)
      }
    }
}

@Deprecated(
  message = "Show typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorShow<L, R> : Show<Ior<L, R>> {
  fun SL(): Show<L>
  fun SR(): Show<R>
  override fun Ior<L, R>.show(): String = show(SL(), SR())
}

@Deprecated(
  message = "Hash typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorHash<L, R> : Hash<Ior<L, R>> {

  fun HL(): Hash<L>
  fun HR(): Hash<R>

  override fun Ior<L, R>.hashWithSalt(salt: Int): Int = when (this) {
    is Ior.Left -> HL().run { value.hashWithSalt(salt.hashWithSalt(0)) }
    is Ior.Right -> HR().run { value.hashWithSalt(salt.hashWithSalt(1)) }
    is Ior.Both -> HL().run { HR().run { leftValue.hashWithSalt(rightValue.hashWithSalt(salt.hashWithSalt(2))) } }
  }
}

@Deprecated(OrderDeprecation)
interface IorOrder<L, R> : Order<Ior<L, R>> {
  fun OL(): Order<L>
  fun OR(): Order<R>
  override fun Ior<L, R>.compare(b: Ior<L, R>): Ordering = fold(
    { l1 ->
      b.fold({ l2 -> OL().run { l1.compare(l2) } }, { LT }, { _, _ -> LT })
    },
    { r1 ->
      b.fold({ GT }, { r2 -> OR().run { r1.compare(r2) } }, { _, _ -> LT })
    },
    { l1, r1 ->
      b.fold({ GT }, { GT }, { l2, r2 -> OL().run { l1.compare(l2) } + OR().run { r1.compare(r2) } })
    }
  )
}

fun <L, R> Ior.Companion.fx(SL: Semigroup<L>, c: suspend MonadSyntax<IorPartialOf<L>>.() -> R): Ior<L, R> =
  Ior.monad(SL).fx.monad(c).fix()

@Deprecated(
  message = "Crosswalk typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorCrosswalk<L> : Crosswalk<IorPartialOf<L>>, IorFunctor<L>, IorFoldable<L> {
  override fun <F, A, B> crosswalk(ALIGN: Align<F>, a: Kind<IorPartialOf<L>, A>, fa: (A) -> Kind<F, B>): Kind<F, Kind<IorPartialOf<L>, B>> {
    return when (val ior = a.fix()) {
      is Ior.Left -> ALIGN.run { empty<Kind<IorPartialOf<L>, B>>() }
      is Ior.Both -> ALIGN.run { fa(ior.rightValue).map { Ior.Both(ior.leftValue, it) } }
      is Ior.Right -> ALIGN.run { fa(ior.value).map { it.rightIor() } }
    }
  }
}

@Deprecated(
  message = "Bicrosswalk typeclass is deprecated and will be removed in 0.13.0. Use concrete methods on Ior",
  level = DeprecationLevel.WARNING
)
interface IorBicrosswalk : Bicrosswalk<ForIor>, IorBifunctor, IorBifoldable {
  override fun <F, A, B, C, D> bicrosswalk(
    ALIGN: Align<F>,
    tab: Kind2<ForIor, A, B>,
    fa: (A) -> Kind<F, C>,
    fb: (B) -> Kind<F, D>
  ): Kind<F, Kind2<ForIor, C, D>> =
    when (val e = tab.fix()) {
      is Ior.Left -> ALIGN.run {
        fa(e.value).map { it.leftIor() }
      }
      is Ior.Right -> ALIGN.run {
        fb(e.value).map { it.rightIor() }
      }
      is Ior.Both -> ALIGN.run {
        align(fa(e.leftValue), fb(e.rightValue))
      }
    }
}

operator fun <L, R> Ior<L, R>.component1(): L? = leftOrNull()
operator fun <L, R> Ior<L, R>.component2(): R? = orNull()
