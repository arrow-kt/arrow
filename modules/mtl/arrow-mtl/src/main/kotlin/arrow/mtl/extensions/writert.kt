package arrow.mtl.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.left
import arrow.core.right
import arrow.core.toT
import arrow.extension
import arrow.mtl.ForWriterT
import arrow.mtl.WriterT
import arrow.mtl.WriterTOf
import arrow.mtl.WriterTPartialOf
import arrow.mtl.extensions.writert.monad.monad
import arrow.mtl.fix
import arrow.mtl.typeclasses.MonadTrans
import arrow.mtl.typeclasses.MonadWriter
import arrow.mtl.value
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadFilter
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidK
import arrow.typeclasses.SemigroupK
import arrow.undocumented

@extension
@undocumented
interface WriterTFunctor<W, F> : Functor<WriterTPartialOf<W, F>> {
  fun FF(): Functor<F>

  override fun <A, B> WriterTOf<W, F, A>.map(f: (A) -> B): WriterT<W, F, B> = fix().map(FF()) { f(it) }
}

@extension
@undocumented
interface WriterTApplicative<W, F> : Applicative<WriterTPartialOf<W, F>>, WriterTFunctor<W, F> {

  fun AF(): Applicative<F>

  override fun FF(): Functor<F> = AF()

  fun MM(): Monoid<W>

  override fun <A> just(a: A): WriterTOf<W, F, A> =
    WriterT(AF().just(MM().empty() toT a))

  override fun <A, B> WriterTOf<W, F, A>.ap(ff: WriterTOf<W, F, (A) -> B>): WriterT<W, F, B> =
    fix().ap(AF(), MM(), ff)

  override fun <A, B> WriterTOf<W, F, A>.map(f: (A) -> B): WriterT<W, F, B> =
    fix().map(AF()) { f(it) }

  override fun <A, B> Kind<WriterTPartialOf<W, F>, A>.lazyAp(ff: () -> Kind<WriterTPartialOf<W, F>, (A) -> B>): Kind<WriterTPartialOf<W, F>, B> =
    WriterT(
      AF().run {
        fix().value().lazyAp { ff().fix().value().map { r -> { l: Tuple2<W, A> -> Tuple2(MM().run { l.a + r.a }, r.b(l.b)) } } }
      }
    )
}

@extension
@undocumented
interface WriterTMonad<W, F> : Monad<WriterTPartialOf<W, F>>, WriterTApplicative<W, F> {

  fun MF(): Monad<F>

  override fun AF(): Applicative<F> = MF()

  override fun MM(): Monoid<W>

  override fun <A, B> WriterTOf<W, F, A>.map(f: (A) -> B): WriterT<W, F, B> =
    fix().map(FF()) { f(it) }

  override fun <A, B> WriterTOf<W, F, A>.flatMap(f: (A) -> WriterTOf<W, F, B>): WriterT<W, F, B> =
    fix().flatMap(MF(), MM()) { f(it) }

  override fun <A, B> tailRecM(a: A, f: (A) -> WriterTOf<W, F, Either<A, B>>): WriterT<W, F, B> =
    WriterT.tailRecM(MF(), a, f)

  override fun <A, B> WriterTOf<W, F, A>.ap(ff: WriterTOf<W, F, (A) -> B>): WriterT<W, F, B> =
    fix().ap(MF(), MM(), ff)

  override fun <A, B> Kind<WriterTPartialOf<W, F>, A>.lazyAp(ff: () -> Kind<WriterTPartialOf<W, F>, (A) -> B>): Kind<WriterTPartialOf<W, F>, B> =
    WriterT(
      AF().run {
        fix().value().lazyAp { ff().fix().value().map { r -> { l: Tuple2<W, A> -> Tuple2(MM().run { l.a + r.a }, r.b(l.b)) } } }
      }
    )
}

@extension
@undocumented
interface WriterTApplicativeError<W, F, E> : ApplicativeError<WriterTPartialOf<W, F>, E>, WriterTApplicative<W, F> {

  fun AE(): ApplicativeError<F, E>

  override fun MM(): Monoid<W>

  override fun AF(): Applicative<F> = AE()

  override fun <A> raiseError(e: E): WriterT<W, F, A> =
    WriterT(AE().raiseError(e))

  override fun <A> WriterTOf<W, F, A>.handleErrorWith(f: (E) -> WriterTOf<W, F, A>): WriterT<W, F, A> = AE().run {
    WriterT(value().handleErrorWith { e -> f(e).value() })
  }
}

@extension
@undocumented
interface WriterTMonadError<W, F, E> : MonadError<WriterTPartialOf<W, F>, E>, WriterTApplicativeError<W, F, E>, WriterTMonad<W, F> {

  fun ME(): MonadError<F, E>

  override fun MM(): Monoid<W>

  override fun MF(): Monad<F> = ME()

  override fun AF(): Applicative<F> = ME()

  override fun AE(): ApplicativeError<F, E> = ME()
}

@extension
@undocumented
interface WriterTMonadThrow<W, F> : MonadThrow<WriterTPartialOf<W, F>>, WriterTMonadError<W, F, Throwable> {
  override fun ME(): MonadError<F, Throwable>
  override fun MM(): Monoid<W>
}

@extension
@undocumented
interface WriterTSemigroupK<W, F> : SemigroupK<WriterTPartialOf<W, F>> {

  fun SS(): SemigroupK<F>

  override fun <A> Kind<WriterTPartialOf<W, F>, A>.combineK(y: Kind<WriterTPartialOf<W, F>, A>): WriterT<W, F, A> =
    fix().combineK(SS(), y)
}

@extension
@undocumented
interface WriterTMonoidK<W, F> : MonoidK<WriterTPartialOf<W, F>>, WriterTSemigroupK<W, F> {

  fun MF(): MonoidK<F>

  override fun SS(): SemigroupK<F> = MF()

  override fun <A> empty(): WriterT<W, F, A> = WriterT(MF().empty())
}

@extension
interface WriterTContravariantInstance<W, F> : Contravariant<WriterTPartialOf<W, F>> {
  fun CF(): Contravariant<F>

  override fun <A, B> Kind<WriterTPartialOf<W, F>, A>.contramap(f: (B) -> A): Kind<WriterTPartialOf<W, F>, B> =
    WriterT(
      CF().run {
        value().contramap<Tuple2<W, A>, Tuple2<W, B>> { (w, b) ->
          w toT f(b)
        }
      }
    )
}

@extension
interface WriterTDivideInstance<W, F> : Divide<WriterTPartialOf<W, F>>, WriterTContravariantInstance<W, F> {
  fun DF(): Divide<F>
  override fun CF(): Contravariant<F> = DF()

  override fun <A, B, Z> divide(fa: Kind<WriterTPartialOf<W, F>, A>, fb: Kind<WriterTPartialOf<W, F>, B>, f: (Z) -> Tuple2<A, B>): Kind<WriterTPartialOf<W, F>, Z> =
    WriterT(
      DF().divide(fa.value(), fb.value()) { (w, z) ->
        val (a, b) = f(z)
        (w toT a) toT (w toT b)
      }
    )
}

@extension
interface WriterTDivisibleInstance<W, F> : Divisible<WriterTPartialOf<W, F>>, WriterTDivideInstance<W, F> {
  fun DFF(): Divisible<F>
  override fun DF(): Divide<F> = DFF()

  override fun <A> conquer(): Kind<WriterTPartialOf<W, F>, A> =
    WriterT(
      DFF().conquer()
    )
}

@extension
interface WriterTDecidableInstance<W, F> : Decidable<WriterTPartialOf<W, F>>, WriterTDivisibleInstance<W, F> {
  fun DFFF(): Decidable<F>
  override fun DFF(): Divisible<F> = DFFF()

  override fun <A, B, Z> choose(fa: Kind<WriterTPartialOf<W, F>, A>, fb: Kind<WriterTPartialOf<W, F>, B>, f: (Z) -> Either<A, B>): Kind<WriterTPartialOf<W, F>, Z> =
    WriterT(
      DFFF().choose(fa.value(), fb.value()) { (w, z) ->
        f(z).fold({ a ->
          (w toT a).left()
        }, { b ->
          (w toT b).right()
        })
      }
    )
}

fun <W, F, A> WriterT.Companion.fx(M: Monad<F>, MW: Monoid<W>, c: suspend MonadSyntax<WriterTPartialOf<W, F>>.() -> A): WriterT<W, F, A> =
  WriterT.monad(M, MW).fx.monad(c).fix()

@extension
interface WriterTMonadFilter<W, F> : MonadFilter<WriterTPartialOf<W, F>>, WriterTMonad<W, F> {
  override fun FF(): MonadFilter<F>

  override fun MF(): Monad<F> = FF()

  override fun MM(): Monoid<W>

  override fun <A> empty(): WriterTOf<W, F, A> = WriterT(FF().empty())
}

@extension
interface WriterTMonadWriter<W, F> : MonadWriter<WriterTPartialOf<W, F>, W>, WriterTMonad<W, F> {

  override fun MF(): Monad<F>

  override fun MM(): Monoid<W>

  override fun <A> Kind<WriterTPartialOf<W, F>, A>.listen(): Kind<WriterTPartialOf<W, F>, Tuple2<W, A>> = MF().run {
    WriterT(fix().content(this).flatMap { a -> fix().write(this).map { l -> Tuple2(l, Tuple2(l, a)) } })
  }

  override fun <A> Kind<WriterTPartialOf<W, F>, Tuple2<(W) -> W, A>>.pass(): WriterT<W, F, A> = MF().run {
    WriterT(fix().content(this).flatMap { tuple2FA -> fix().write(this).map { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) } })
  }

  override fun <A> writer(aw: Tuple2<W, A>): WriterT<W, F, A> = WriterT.put2(MF(), aw.b, aw.a)

  override fun tell(w: W): Kind<WriterTPartialOf<W, F>, Unit> = WriterT.tell2(MF(), w)
}

@extension
interface WriterTAlternative<W, F> : Alternative<WriterTPartialOf<W, F>>, WriterTApplicative<W, F>, WriterTMonoidK<W, F> {
  override fun AF(): Applicative<F> = AL()
  override fun MF(): MonoidK<F> = AL()
  override fun MM(): Monoid<W>
  fun AL(): Alternative<F>

  override fun <A> empty(): WriterT<W, F, A> = WriterT(AL().empty())

  override fun <A> Kind<WriterTPartialOf<W, F>, A>.orElse(b: Kind<WriterTPartialOf<W, F>, A>): Kind<WriterTPartialOf<W, F>, A> =
    WriterT(
      AL().run {
        value().orElse(b.value())
      }
    )

  override fun <A> Kind<WriterTPartialOf<W, F>, A>.lazyOrElse(b: () -> Kind<WriterTPartialOf<W, F>, A>): Kind<WriterTPartialOf<W, F>, A> =
    WriterT(
      AL().run {
        value().lazyOrElse { b().value() }
      }
    )

  override fun <A> Kind<WriterTPartialOf<W, F>, A>.combineK(y: Kind<WriterTPartialOf<W, F>, A>): WriterT<W, F, A> =
    orElse(y).fix()
}

@extension
interface WriterTEqK<W, F> : EqK<WriterTPartialOf<W, F>> {
  fun EQKF(): EqK<F>
  fun EQW(): Eq<W>

  override fun <A> Kind<WriterTPartialOf<W, F>, A>.eqK(other: Kind<WriterTPartialOf<W, F>, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      EQKF().liftEq(Tuple2.eq(EQW(), EQ)).run {
        it.first.value().eqv(it.second.value())
      }
    }
}

@extension
interface WriterTMonadTrans<W> : MonadTrans<Kind<ForWriterT, W>> {
  fun MW(): Monoid<W>
  override fun <G, A> Kind<G, A>.liftT(MF: Monad<G>): Kind2<Kind<ForWriterT, W>, G, A> = WriterT(MF.run { map { MW().empty() toT it } })
}
