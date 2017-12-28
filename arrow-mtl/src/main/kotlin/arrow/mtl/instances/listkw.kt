package arrow.mtl.instances

import arrow.*

@instance(ListKW::class)
interface ListKWMonadCombineInstance : arrow.MonadCombine<ListKWHK> {
    override fun <A> empty(): arrow.ListKW<A> =
            arrow.ListKW.empty()

    override fun <A, B> mapFilter(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.Option<B>>): arrow.ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> ap(fa: arrow.ListKWKind<A>, ff: arrow.ListKWKind<kotlin.Function1<A, B>>): arrow.ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.ListKWKind<B>>): arrow.ListKW<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.ListKWKind<arrow.Either<A, B>>>): arrow.ListKW<B> =
            arrow.ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): arrow.ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: arrow.ListKWKind<A>, fb: arrow.ListKWKind<B>, f: kotlin.Function1<arrow.Tuple2<A, B>, Z>): arrow.ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): arrow.ListKW<A> =
            arrow.ListKW.pure(a)

    override fun <A> combineK(x: arrow.ListKWKind<A>, y: arrow.ListKWKind<A>): arrow.ListKW<A> =
            x.ev().combineK(y)
}

@instance(ListKW::class)
interface ListKWFunctorFilterInstance : arrow.FunctorFilter<ListKWHK> {
    override fun <A, B> mapFilter(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.Option<B>>): arrow.ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): arrow.ListKW<B> =
            fa.ev().map(f)
}

@instance(ListKW::class)
interface ListKWMonadFilterInstance : arrow.MonadFilter<ListKWHK> {
    override fun <A> empty(): arrow.ListKW<A> =
            arrow.ListKW.empty()

    override fun <A, B> mapFilter(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.Option<B>>): arrow.ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> ap(fa: arrow.ListKWKind<A>, ff: arrow.ListKWKind<kotlin.Function1<A, B>>): arrow.ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.ListKWKind<B>>): arrow.ListKW<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.ListKWKind<arrow.Either<A, B>>>): arrow.ListKW<B> =
            arrow.ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): arrow.ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: arrow.ListKWKind<A>, fb: arrow.ListKWKind<B>, f: kotlin.Function1<arrow.Tuple2<A, B>, Z>): arrow.ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): arrow.ListKW<A> =
            arrow.ListKW.pure(a)
}
