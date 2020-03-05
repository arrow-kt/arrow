package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.typeclasses.Applicative
import kotlin.coroutines.CoroutineContext

@Suppress("FunctionName", "DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE", "ObjectPropertyName")
internal fun <F> Concurrent<F>.ParApplicative(ctx: CoroutineContext? = null): Applicative<F> = object : Concurrent<F> by this {

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
    parTupledN(_ctx, this@product, fb)
}
