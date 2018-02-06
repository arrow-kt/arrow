package arrow.mtl.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.mtl.FunctorFilter
import arrow.mtl.MonadCombine
import arrow.mtl.MonadFilter

@instance(ListKW::class)
interface ListKWMonadCombineInstance : MonadCombine<ForListKW> {
    override fun <A> empty(): ListKW<A> =
            ListKW.empty()

    override fun <A, B> mapFilter(fa: ListKWKind<A>, f: kotlin.Function1<A, Option<B>>): ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> ap(fa: ListKWKind<A>, ff: ListKWKind<kotlin.Function1<A, B>>): ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ListKWKind<A>, f: kotlin.Function1<A, ListKWKind<B>>): ListKW<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKWKind<Either<A, B>>>): ListKW<B> =
            ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: ListKWKind<A>, fb: ListKWKind<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): ListKW<A> =
            ListKW.pure(a)

    override fun <A> combineK(x: ListKWKind<A>, y: ListKWKind<A>): ListKW<A> =
            x.ev().combineK(y)
}

@instance(ListKW::class)
interface ListKWFunctorFilterInstance : FunctorFilter<ForListKW> {
    override fun <A, B> mapFilter(fa: ListKWKind<A>, f: kotlin.Function1<A, Option<B>>): ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> map(fa: ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)
}

@instance(ListKW::class)
interface ListKWMonadFilterInstance : MonadFilter<ForListKW> {
    override fun <A> empty(): ListKW<A> =
            ListKW.empty()

    override fun <A, B> mapFilter(fa: ListKWKind<A>, f: kotlin.Function1<A, Option<B>>): ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> ap(fa: ListKWKind<A>, ff: ListKWKind<kotlin.Function1<A, B>>): ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ListKWKind<A>, f: kotlin.Function1<A, ListKWKind<B>>): ListKW<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKWKind<Either<A, B>>>): ListKW<B> =
            ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: ListKWKind<A>, fb: ListKWKind<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): ListKW<A> =
            ListKW.pure(a)
}
