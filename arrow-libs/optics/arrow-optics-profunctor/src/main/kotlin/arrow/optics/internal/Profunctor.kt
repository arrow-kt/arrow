package arrow.optics.internal

import arrow.core.Either
import arrow.typeclasses.Monoid

interface Kind<out F, out A>
typealias Pro<P, A, B> = Kind<Kind<P, A>, B>

// We are not using default impl anywhere because unlike haskell composition won't
//  ever fuse and produce equally performant implementations, also its not that much
//  of an inconvenience since we don't have too many types implementing this

interface Profunctor<P> {
  fun <A, B, C, D> Pro<P, B, C>.dimap(f: (A) -> B, g: (C) -> D): Pro<P, A, D>
  fun <A, B, C> Pro<P, B, C>.lMap(f: (A) -> B): Pro<P, A, C>
  fun <B, C, D> Pro<P, B, C>.rMap(g: (C) -> D): Pro<P, B, D>
}

interface Strong<P> : Profunctor<P> {
  fun <A, B, C> Pro<P, A, B>.first(): Pro<P, Pair<A, C>, Pair<B, C>>
  fun <A, B, C> Pro<P, A, B>.second(): Pro<P, Pair<C, A>, Pair<C, B>>
}

interface Choice<P> : Profunctor<P> {
  fun <A, B, C> Pro<P, A, B>.left(): Pro<P, Either<A, C>, Either<B, C>>
  fun <A, B, C> Pro<P, A, B>.right(): Pro<P, Either<C, A>, Either<C, B>>
}

interface Traversing<P> : Strong<P>, Choice<P> {
  fun <S, T, A, B> Pro<P, A, B>.wander(f: WanderF<S, T, A, B>): Pro<P, S, T>
}

// Traverse in disguise
interface WanderF<S, T, A, B> {
  operator fun <F> invoke(AF: Applicative<F>, source: S, f: (A) -> Kind<F, B>): Kind<F, T>
}

// RETURN OF THE TYPECLASSES PART 2!
interface Applicative<F> {
  fun <A> pure(a: A): Kind<F, A>
  fun <A, B> ap(ff: Kind<F, (A) -> B>, fa: Kind<F, A>): Kind<F, B>

  fun <A, B> map(fa: Kind<F, A>, f: (A) -> B): Kind<F, B> = ap(pure(f), fa)
}

// Implementations
internal class ForFunction1

internal inline fun <A, B> Kind<Kind<ForFunction1, A>, B>.fix(): Function1<A, B> = this as Function1<A, B>
internal class Function1<A, B>(internal val f: (A) -> B) : Kind<Kind<ForFunction1, A>, B> {
  companion object {
    fun traversing() = object : Function1Traversing {}
  }
}

internal interface Function1Profunctor : Profunctor<ForFunction1> {
  override fun <A, B, C, D> Pro<ForFunction1, B, C>.dimap(f: (A) -> B, g: (C) -> D): Pro<ForFunction1, A, D> =
    Function1 { a: A -> g(this.fix().f(f(a))) }

  override fun <A, B, C> Pro<ForFunction1, B, C>.lMap(f: (A) -> B): Pro<ForFunction1, A, C> =
    Function1 { a: A -> this.fix().f(f(a)) }

  override fun <B, C, D> Pro<ForFunction1, B, C>.rMap(g: (C) -> D): Pro<ForFunction1, B, D> =
    Function1 { b: B -> g(this.fix().f(b)) }
}

internal interface Function1Strong : Strong<ForFunction1>, Function1Profunctor {
  override fun <A, B, C> Pro<ForFunction1, A, B>.first(): Pro<ForFunction1, Pair<A, C>, Pair<B, C>> =
    Function1 { (a, c) -> this.fix().f(a) to c }

  override fun <A, B, C> Pro<ForFunction1, A, B>.second(): Pro<ForFunction1, Pair<C, A>, Pair<C, B>> =
    Function1 { (c, a) -> c to this.fix().f(a) }
}

internal interface Function1Choice : Choice<ForFunction1>, Function1Profunctor {
  override fun <A, B, C> Pro<ForFunction1, A, B>.left(): Pro<ForFunction1, Either<A, C>, Either<B, C>> =
    Function1 { e -> e.fold({ Either.Left(this.fix().f(it)) }, { Either.Right(it) }) }

  override fun <A, B, C> Pro<ForFunction1, A, B>.right(): Pro<ForFunction1, Either<C, A>, Either<C, B>> =
    Function1 { e -> e.fold({ Either.Left(it) }, { Either.Right(this.fix().f(it)) }) }
}

internal interface Function1Traversing : Traversing<ForFunction1>, Function1Choice, Function1Strong {
  override fun <S, T, A, B> Pro<ForFunction1, A, B>.wander(f: WanderF<S, T, A, B>): Pro<ForFunction1, S, T> =
    Function1 { s ->
      f.invoke(object : IdApplicative {}, s) { a -> Id(this.fix().f(a)) }
        .fix().v
    }
}

internal class ForStar

internal inline fun <F, A, B> Kind<Kind<Kind<ForStar, F>, A>, B>.fix(): Star<F, A, B> = this as Star<F, A, B>
internal class Star<F, A, B>(internal val f: (A) -> Kind<F, B>) : Kind<Kind<Kind<ForStar, F>, A>, B> {
  companion object {
    fun <F> traversing(AF: Applicative<F>) = object : StarTraversing<F> {
      override fun AF(): Applicative<F> = AF
    }
  }
}

internal interface StarProfunctor<F> : Profunctor<Kind<ForStar, F>> {
  fun AF(): Applicative<F>
  override fun <A, B, C, D> Pro<Kind<ForStar, F>, B, C>.dimap(f: (A) -> B, g: (C) -> D): Pro<Kind<ForStar, F>, A, D> =
    Star { a -> AF().map(this.fix().f(f(a)), g) }

  override fun <A, B, C> Pro<Kind<ForStar, F>, B, C>.lMap(f: (A) -> B): Pro<Kind<ForStar, F>, A, C> =
    Star { a -> this.fix().f(f(a)) }

  override fun <B, C, D> Pro<Kind<ForStar, F>, B, C>.rMap(g: (C) -> D): Pro<Kind<ForStar, F>, B, D> =
    Star { b -> AF().map(this.fix().f(b), g) }
}

internal interface StarStrong<F> : Strong<Kind<ForStar, F>>, StarProfunctor<F> {
  override fun <A, B, C> Pro<Kind<ForStar, F>, A, B>.first(): Pro<Kind<ForStar, F>, Pair<A, C>, Pair<B, C>> =
    Star { (a, c) -> AF().map(this.fix().f(a)) { b -> b to c } }

  override fun <A, B, C> Pro<Kind<ForStar, F>, A, B>.second(): Pro<Kind<ForStar, F>, Pair<C, A>, Pair<C, B>> =
    Star { (c, a) -> AF().map(this.fix().f(a)) { b -> c to b } }
}

internal interface StarChoice<F> : Choice<Kind<ForStar, F>>, StarProfunctor<F> {
  override fun <A, B, C> Pro<Kind<ForStar, F>, A, B>.left(): Pro<Kind<ForStar, F>, Either<A, C>, Either<B, C>> =
    Star { e ->
      e.fold({ a ->
        AF().map(this.fix().f(a)) { b -> Either.Left(b) }
      }, { c ->
        AF().pure(Either.Right(c))
      })
    }

  override fun <A, B, C> Pro<Kind<ForStar, F>, A, B>.right(): Pro<Kind<ForStar, F>, Either<C, A>, Either<C, B>> =
    Star { e ->
      e.fold({ c ->
        AF().pure(Either.Left(c))
      }, { a ->
        AF().map(this.fix().f(a)) { b -> Either.Right(b) }
      })
    }
}

internal interface StarTraversing<F> : Traversing<Kind<ForStar, F>>, StarChoice<F>, StarStrong<F> {
  override fun <S, T, A, B> Pro<Kind<ForStar, F>, A, B>.wander(f: WanderF<S, T, A, B>): Pro<Kind<ForStar, F>, S, T> =
    Star { s -> f.invoke(AF(), s, this.fix().f) }
}

internal class ForForget

internal inline fun <R, A, B> Kind<Kind<Kind<ForForget, R>, A>, B>.fix(): Forget<R, A, B> = this as Forget<R, A, B>
internal class Forget<R, A, B>(internal val f: (A) -> R) : Kind<Kind<Kind<ForForget, R>, A>, B> {
  companion object {
    fun <R> strong(): ForgetStrong<R> = object : ForgetStrong<R> {}
    fun <R> traversing(MR: Monoid<R>) = object : ForgetTraversing<R> {
      override fun MR(): Monoid<R> = MR
    }
  }
}

internal interface ForgetProfunctor<R> : Profunctor<Kind<ForForget, R>> {
  override fun <A, B, C, D> Pro<Kind<ForForget, R>, B, C>.dimap(
    f: (A) -> B,
    g: (C) -> D
  ): Pro<Kind<ForForget, R>, A, D> =
    Forget { a -> this.fix().f(f(a)) }

  override fun <A, B, C> Pro<Kind<ForForget, R>, B, C>.lMap(f: (A) -> B): Pro<Kind<ForForget, R>, A, C> =
    Forget { a -> this.fix().f(f(a)) }

  override fun <B, C, D> Pro<Kind<ForForget, R>, B, C>.rMap(g: (C) -> D): Pro<Kind<ForForget, R>, B, D> =
    this as Forget<R, B, D> // Safe because C and D are phantom types
}

internal interface ForgetStrong<R> : Strong<Kind<ForForget, R>>, ForgetProfunctor<R> {
  override fun <A, B, C> Pro<Kind<ForForget, R>, A, B>.first(): Pro<Kind<ForForget, R>, Pair<A, C>, Pair<B, C>> =
    Forget { (a, _) -> this.fix().f(a) }

  override fun <A, B, C> Pro<Kind<ForForget, R>, A, B>.second(): Pro<Kind<ForForget, R>, Pair<C, A>, Pair<C, B>> =
    Forget { (_, a) -> this.fix().f(a) }
}

internal interface ForgetChoice<R> : Choice<Kind<ForForget, R>>, ForgetProfunctor<R> {
  fun MR(): Monoid<R>

  override fun <A, B, C> Pro<Kind<ForForget, R>, A, B>.left(): Pro<Kind<ForForget, R>, Either<A, C>, Either<B, C>> =
    Forget { e -> e.fold({ a -> this.fix().f(a) }, { MR().empty() }) }

  override fun <A, B, C> Pro<Kind<ForForget, R>, A, B>.right(): Pro<Kind<ForForget, R>, Either<C, A>, Either<C, B>> =
    Forget { e -> e.fold({ MR().empty() }, { a -> this.fix().f(a) }) }
}

internal interface ForgetTraversing<R> : Traversing<Kind<ForForget, R>>, ForgetStrong<R>, ForgetChoice<R> {
  override fun <S, T, A, B> Pro<Kind<ForForget, R>, A, B>.wander(f: WanderF<S, T, A, B>): Pro<Kind<ForForget, R>, S, T> =
    Forget { s ->
      f.invoke(
        object : ConstApplicative<R> {
          override fun MR(): Monoid<R> = this@ForgetTraversing.MR()
        }, s, { a -> Const(this.fix().f(a)) }
      ).fix().v
    }
}

internal class ForId

internal fun <A> Kind<ForId, A>.fix(): Id<A> = this as Id<A>
internal class Id<A>(val v: A) : Kind<ForId, A>

internal interface IdApplicative : Applicative<ForId> {
  override fun <A> pure(a: A): Kind<ForId, A> = Id(a)
  override fun <A, B> ap(ff: Kind<ForId, (A) -> B>, fa: Kind<ForId, A>): Kind<ForId, B> =
    Id(ff.fix().v(fa.fix().v))

  override fun <A, B> map(fa: Kind<ForId, A>, f: (A) -> B): Kind<ForId, B> =
    Id(f(fa.fix().v))
}

internal class ForConst

internal fun <A, B> Kind<Kind<ForConst, A>, B>.fix(): Const<A, B> = this as Const<A, B>
internal class Const<A, B>(internal val v: A) : Kind<Kind<ForConst, A>, B>

internal interface ConstApplicative<R> : Applicative<Kind<ForConst, R>> {
  fun MR(): Monoid<R>
  override fun <A> pure(a: A): Kind<Kind<ForConst, R>, A> =
    Const(MR().empty())

  override fun <A, B> ap(
    ff: Kind<Kind<ForConst, R>, (A) -> B>,
    fa: Kind<Kind<ForConst, R>, A>
  ): Kind<Kind<ForConst, R>, B> =
    Const(MR().run { ff.fix().v.combine(fa.fix().v) })

  override fun <A, B> map(fa: Kind<Kind<ForConst, R>, A>, f: (A) -> B): Kind<Kind<ForConst, R>, B> =
    fa as Const<R, B>
}
