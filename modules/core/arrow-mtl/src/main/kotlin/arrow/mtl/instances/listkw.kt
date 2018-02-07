package arrow.mtl.instances

import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.data.*
import arrow.instance
import arrow.mtl.FunctorFilter
import arrow.mtl.MonadCombine
import arrow.mtl.MonadFilter

@instance(ListKW::class)
interface ListKWMonadCombineInstance : MonadCombine<ForListKW> {
    override fun <A> empty(): ListKW<A> =
            ListKW.empty()

    override fun <A, B> mapFilter(fa: ListKWOf<A>, f: kotlin.Function1<A, Option<B>>): ListKW<B> =
            fa.reify().mapFilter(f)

    override fun <A, B> ap(fa: ListKWOf<A>, ff: ListKWOf<kotlin.Function1<A, B>>): ListKW<B> =
            fa.reify().ap(ff)

    override fun <A, B> flatMap(fa: ListKWOf<A>, f: kotlin.Function1<A, ListKWOf<B>>): ListKW<B> =
            fa.reify().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKWOf<Either<A, B>>>): ListKW<B> =
            ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: ListKWOf<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.reify().map(f)

    override fun <A, B, Z> map2(fa: ListKWOf<A>, fb: ListKWOf<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListKW<Z> =
            fa.reify().map2(fb, f)

    override fun <A> pure(a: A): ListKW<A> =
            ListKW.pure(a)

    override fun <A> combineK(x: ListKWOf<A>, y: ListKWOf<A>): ListKW<A> =
            x.reify().combineK(y)
}

@instance(ListKW::class)
interface ListKWFunctorFilterInstance : FunctorFilter<ForListKW> {
    override fun <A, B> mapFilter(fa: ListKWOf<A>, f: kotlin.Function1<A, Option<B>>): ListKW<B> =
            fa.reify().mapFilter(f)

    override fun <A, B> map(fa: ListKWOf<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.reify().map(f)
}

@instance(ListKW::class)
interface ListKWMonadFilterInstance : MonadFilter<ForListKW> {
    override fun <A> empty(): ListKW<A> =
            ListKW.empty()

    override fun <A, B> mapFilter(fa: ListKWOf<A>, f: kotlin.Function1<A, Option<B>>): ListKW<B> =
            fa.reify().mapFilter(f)

    override fun <A, B> ap(fa: ListKWOf<A>, ff: ListKWOf<kotlin.Function1<A, B>>): ListKW<B> =
            fa.reify().ap(ff)

    override fun <A, B> flatMap(fa: ListKWOf<A>, f: kotlin.Function1<A, ListKWOf<B>>): ListKW<B> =
            fa.reify().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKWOf<Either<A, B>>>): ListKW<B> =
            ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: ListKWOf<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.reify().map(f)

    override fun <A, B, Z> map2(fa: ListKWOf<A>, fb: ListKWOf<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListKW<Z> =
            fa.reify().map2(fb, f)

    override fun <A> pure(a: A): ListKW<A> =
            ListKW.pure(a)
}
