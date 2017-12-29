package arrow.mtl.instances

import arrow.*
import arrow.core.Either
import arrow.core.Option
import arrow.core.Tuple2
import arrow.data.ListKW
import arrow.data.combineK

@instance(ListKW::class)
interface ListKWMonadCombineInstance : arrow.MonadCombine<ListKWHK> {
    override fun <A> empty(): ListKW<A> =
            ListKW.empty()

    override fun <A, B> mapFilter(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, Option<B>>): ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> ap(fa: arrow.ListKWKind<A>, ff: arrow.ListKWKind<kotlin.Function1<A, B>>): ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.ListKWKind<B>>): ListKW<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.ListKWKind<Either<A, B>>>): ListKW<B> =
            ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: arrow.ListKWKind<A>, fb: arrow.ListKWKind<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): ListKW<A> =
            ListKW.pure(a)

    override fun <A> combineK(x: arrow.ListKWKind<A>, y: arrow.ListKWKind<A>): ListKW<A> =
            x.ev().combineK(y)
}

@instance(ListKW::class)
interface ListKWFunctorFilterInstance : arrow.FunctorFilter<ListKWHK> {
    override fun <A, B> mapFilter(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, Option<B>>): ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)
}

@instance(ListKW::class)
interface ListKWMonadFilterInstance : arrow.MonadFilter<ListKWHK> {
    override fun <A> empty(): ListKW<A> =
            ListKW.empty()

    override fun <A, B> mapFilter(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, Option<B>>): ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> ap(fa: arrow.ListKWKind<A>, ff: arrow.ListKWKind<kotlin.Function1<A, B>>): ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.ListKWKind<B>>): ListKW<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.ListKWKind<Either<A, B>>>): ListKW<B> =
            ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: arrow.ListKWKind<A>, fb: arrow.ListKWKind<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): ListKW<A> =
            ListKW.pure(a)
}
