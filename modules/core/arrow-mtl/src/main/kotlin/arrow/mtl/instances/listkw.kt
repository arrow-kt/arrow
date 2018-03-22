package arrow.mtl.instances

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

    override fun <A, B> mapFilter(fa: ListKOf<A>, f: kotlin.Function1<A, Option<B>>): ListK<B> =
            fa.fix().mapFilter(f)

    override fun <A, B> ap(fa: ListKOf<A>, ff: ListKOf<kotlin.Function1<A, B>>): ListK<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: ListKOf<A>, f: kotlin.Function1<A, ListKOf<B>>): ListK<B> =
            fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
            ListK.tailRecM(a, f)

    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)

    override fun <A, B, Z> map2(fa: ListKOf<A>, fb: ListKOf<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListK<Z> =
            fa.fix().map2(fb, f)

    override fun <A> pure(a: A): ListK<A> =
            ListK.pure(a)

    override fun <A> combineK(x: ListKOf<A>, y: ListKOf<A>): ListK<A> =
            x.fix().combineK(y)
}

@instance(ListK::class)
interface ListKFunctorFilterInstance : FunctorFilter<ForListK> {
    override fun <A, B> mapFilter(fa: ListKOf<A>, f: kotlin.Function1<A, Option<B>>): ListK<B> =
            fa.fix().mapFilter(f)

    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)
}

@instance(ListK::class)
interface ListKMonadFilterInstance : MonadFilter<ForListK> {
    override fun <A> empty(): ListK<A> =
            ListK.empty()

    override fun <A, B> mapFilter(fa: ListKOf<A>, f: kotlin.Function1<A, Option<B>>): ListK<B> =
            fa.fix().mapFilter(f)

    override fun <A, B> ap(fa: ListKOf<A>, ff: ListKOf<kotlin.Function1<A, B>>): ListK<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: ListKOf<A>, f: kotlin.Function1<A, ListKOf<B>>): ListK<B> =
            fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
            ListK.tailRecM(a, f)

    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)

    override fun <A, B, Z> map2(fa: ListKOf<A>, fb: ListKOf<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListK<Z> =
            fa.fix().map2(fb, f)

    override fun <A> pure(a: A): ListK<A> =
            ListK.pure(a)
}
