package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.typeclasses.Applicative
import kotlin.coroutines.CoroutineContext

@Suppress("FunctionName", "DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "ObjectPropertyName")
internal fun <F> Concurrent<F>.ParApplicative(ctx: CoroutineContext? = null): Applicative<F> = object : Concurrent<F> {

  val _ctx = ctx ?: dispatchers().default()

  override fun <A, B, Z> Kind<F, A>.map2(fb: Kind<F, B>, f: (Tuple2<A, B>) -> Z): Kind<F, Z> =
    parMapN(_ctx, this@map2, fb, f)

  override fun <A, B, Z> Kind<F, A>.map2Eval(fb: Eval<Kind<F, B>>, f: (Tuple2<A, B>) -> Z): Eval<Kind<F, Z>> =
    fb.map { fc -> parMapN(_ctx, this@map2Eval, fc, f) }

  override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> =
    parMapN(_ctx, ff, this@ap) { (f, a) -> f(a) }

  override fun <A, B> Kind<F, A>.apEval(ff: Eval<Kind<F, (A) -> B>>): Eval<Kind<F, B>> =
    Eval.now(parMapN(_ctx, defer { ff.value() }, this@apEval) { (f, a) -> f(a) })

  override fun <A, B> Kind<F, A>.product(fb: Kind<F, B>): Kind<F, Tuple2<A, B>> =
    this@ParApplicative.parTupledN(_ctx, this@product, fb)

  override fun dispatchers(): Dispatchers<F> = this@ParApplicative.dispatchers()

  override fun <A> Kind<F, A>.fork(ctx: CoroutineContext): Kind<F, Fiber<F, A>> =
    this@ParApplicative.run { this@fork.fork(ctx) }

  override fun <A> Kind<F, A>.fork(): Kind<F, Fiber<F, A>> =
    this@ParApplicative.run { this@fork.fork() }

  override fun <A, B> CoroutineContext.racePair(fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, RacePair<F, A, B>> =
    this@ParApplicative.run { this@racePair.racePair(fa, fb) }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>): Kind<F, RaceTriple<F, A, B, C>> =
    this@ParApplicative.run { this@raceTriple.raceTriple(fa, fb, fc) }

  override fun <A> asyncF(k: ProcF<F, A>): Kind<F, A> = this@ParApplicative.asyncF(k)

  override fun <A> Kind<F, A>.continueOn(ctx: CoroutineContext): Kind<F, A> =
    this@ParApplicative.run { this@continueOn.continueOn(ctx) }

  override fun <A> defer(fa: () -> Kind<F, A>): Kind<F, A> = this@ParApplicative.defer(fa)

  override fun <A> just(a: A): Kind<F, A> = this@ParApplicative.just(a)

  override fun <A> raiseError(e: Throwable): Kind<F, A> = this@ParApplicative.raiseError(e)

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<F, Either<A, B>>): Kind<F, B> = this@ParApplicative.tailRecM(a, f)

  override fun <A, B> Kind<F, A>.flatMap(f: (A) -> Kind<F, B>): Kind<F, B> =
    this@ParApplicative.run { this@flatMap.flatMap(f) }

  override fun <A> Kind<F, A>.handleErrorWith(f: (Throwable) -> Kind<F, A>): Kind<F, A> =
    this@ParApplicative.run { this@handleErrorWith.handleErrorWith(f) }

  override fun <A, B> Kind<F, A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B> =
    this@ParApplicative.run { this@bracketCase.bracketCase(release, use) }
}
