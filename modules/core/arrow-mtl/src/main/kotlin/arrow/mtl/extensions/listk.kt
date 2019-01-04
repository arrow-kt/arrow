package arrow.mtl.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.data.*
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter
import arrow.mtl.typeclasses.MonadCombine
import arrow.mtl.typeclasses.MonadFilter
import arrow.data.combineK as listCombineK

@extension
interface ListKMonadCombine : MonadCombine<ForListK> {
  override fun <A> empty(): ListK<A> =
    ListK.empty()

  override fun <A, B> Kind<ForListK, A>.mapFilter(f: (A) -> Option<B>): ListK<B> =
    fix().mapFilter(f)

  override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForListK, A>.flatMap(f: (A) -> Kind<ForListK, B>): ListK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
    ListK.tailRecM(a, f)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)

  override fun <A> Kind<ForListK, A>.combineK(y: Kind<ForListK, A>): ListK<A> =
    fix().listCombineK(y)
}

@extension
interface ListKFunctorFilter : FunctorFilter<ForListK> {
  override fun <A, B> Kind<ForListK, A>.mapFilter(f: (A) -> Option<B>): ListK<B> =
    fix().mapFilter(f)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)
}

@extension
interface ListKMonadFilter : MonadFilter<ForListK> {
  override fun <A> empty(): ListK<A> =
    ListK.empty()

  override fun <A, B> Kind<ForListK, A>.mapFilter(f: (A) -> Option<B>): ListK<B> =
    fix().mapFilter(f)

  override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForListK, A>.flatMap(f: (A) -> Kind<ForListK, B>): ListK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
    ListK.tailRecM(a, f)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

