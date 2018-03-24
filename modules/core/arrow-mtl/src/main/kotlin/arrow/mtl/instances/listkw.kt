package arrow.mtl.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.data.*
import arrow.instance
import arrow.mtl.typeclasses.FunctorFilter
import arrow.mtl.typeclasses.MonadCombine
import arrow.mtl.typeclasses.MonadFilter

@instance(ListK::class)
interface ListKMonadCombineInstance : MonadCombine<ForListK> {
    override fun <A> empty(): ListK<A> =
            ListK.empty()

    override fun <A, B> Kind<ForListK, A>.mapFilter(f: (A) -> Option<B>): ListK<B> =
            this@mapFilter.fix().mapFilter(f)

    override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForListK, A>.flatMap(f: (A) -> Kind<ForListK, B>): ListK<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
            ListK.tailRecM(a, f)

    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)

    override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
            fix().map2(fb, f)

    override fun <A> pure(a: A): ListK<A> =
            ListK.pure(a)

    override fun <A> combineK(x: ListKOf<A>, y: ListKOf<A>): ListK<A> =
            x.fix().combineK(y)
}

@instance(ListK::class)
interface ListKFunctorFilterInstance : FunctorFilter<ForListK> {
    override fun <A, B> arrow.Kind<arrow.data.ForListK, A>.mapFilter(f: (A) -> arrow.core.Option<B>): ListK<B> =
            this@mapFilter.fix().mapFilter(f)

    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)
}

@instance(ListK::class)
interface ListKMonadFilterInstance : MonadFilter<ForListK> {
    override fun <A> empty(): ListK<A> =
            ListK.empty()

    override fun <A, B> arrow.Kind<arrow.data.ForListK, A>.mapFilter(f: (A) -> arrow.core.Option<B>): ListK<B> =
            this@mapFilter.fix().mapFilter(f)

    override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForListK, A>.flatMap(f: (A) -> Kind<ForListK, B>): ListK<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
            ListK.tailRecM(a, f)

    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)

    override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
            fix().map2(fb, f)

    override fun <A> pure(a: A): ListK<A> =
            ListK.pure(a)
}
