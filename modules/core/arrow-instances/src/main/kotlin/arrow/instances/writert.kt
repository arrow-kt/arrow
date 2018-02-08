package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.toT
import arrow.data.WriterT
import arrow.data.WriterTOf
import arrow.data.WriterTPartialOf
import arrow.instance
import arrow.typeclasses.*

@instance(WriterT::class)
interface WriterTFunctorInstance<F, W> : Functor<WriterTPartialOf<F, W>> {
    fun FF(): Functor<F>

    override fun <A, B> map(fa: WriterTOf<F, W, A>, f: (A) -> B): WriterT<F, W, B> = fa.extract().map({ f(it) }, FF())
}

@instance(WriterT::class)
interface WriterTApplicativeInstance<F, W> : Applicative<WriterTPartialOf<F, W>>, WriterTFunctorInstance<F, W> {

    override fun FF(): Monad<F>

    fun MM(): Monoid<W>

    override fun <A> pure(a: A): WriterTOf<F, W, A> =
            WriterT(FF().pure(MM().empty() toT a))

    override fun <A, B> ap(fa: WriterTOf<F, W, A>, ff: Kind<WriterTPartialOf<F, W>, (A) -> B>): WriterT<F, W, B> =
            fa.extract().ap(ff, MM(), FF())

    override fun <A, B> map(fa: WriterTOf<F, W, A>, f: (A) -> B): WriterT<F, W, B> =
            fa.extract().map({ f(it) }, FF())
}

@instance(WriterT::class)
interface WriterTMonadInstance<F, W> : WriterTApplicativeInstance<F, W>, Monad<WriterTPartialOf<F, W>> {

    override fun <A, B> map(fa: WriterTOf<F, W, A>, f: (A) -> B): WriterT<F, W, B> =
            fa.extract().map({ f(it) }, FF())

    override fun <A, B> flatMap(fa: WriterTOf<F, W, A>, f: (A) -> Kind<WriterTPartialOf<F, W>, B>): WriterT<F, W, B> =
            fa.extract().flatMap({ f(it).extract() }, MM(), FF())

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<WriterTPartialOf<F, W>, Either<A, B>>): WriterT<F, W, B> =
            WriterT.tailRecM(a, f, FF())

    override fun <A, B> ap(fa: WriterTOf<F, W, A>, ff: Kind<WriterTPartialOf<F, W>, (A) -> B>): WriterT<F, W, B> =
            fa.extract().ap(ff, MM(), FF())
}

@instance(WriterT::class)
interface WriterTSemigroupKInstance<F, W> : SemigroupK<WriterTPartialOf<F, W>> {

    fun SS(): SemigroupK<F>

    override fun <A> combineK(x: WriterTOf<F, W, A>, y: WriterTOf<F, W, A>): WriterT<F, W, A> =
            x.extract().combineK(y, SS())
}

@instance(WriterT::class)
interface WriterTMonoidKInstance<F, W> : MonoidK<WriterTPartialOf<F, W>>, WriterTSemigroupKInstance<F, W> {

    override fun SS(): MonoidK<F>

    override fun <A> empty(): WriterT<F, W, A> = WriterT(SS().empty())
}