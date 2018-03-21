package arrow.free.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(Const::class)
interface ConstFunctorInstance<A> : Functor<ConstPartialOf<A>> {
    override fun <T, U> map(fa: ConstOf<A, T>, f: (T) -> U): Const<A, U> = fa.fix().retag()
}

@instance(Const::class)
interface ConstApplicativeInstance<A> : Applicative<ConstPartialOf<A>> {

    fun MA(): Monoid<A>

    override fun <T, U> map(fa: ConstOf<A, T>, f: (T) -> U): Const<A, U> = fa.fix().retag()

    override fun <T> pure(a: T): Const<A, T> = object : ConstMonoidInstance<A, T> {
        override fun SA(): Monoid<A> = MA()
    }.empty().fix()

    override fun <T, U> ap(fa: ConstOf<A, T>, ff: ConstOf<A, (T) -> U>): Const<A, U> = fa.ap(ff, MA())
}

@instance(Const::class)
interface ConstFoldableInstance<A> : Foldable<ConstPartialOf<A>> {

    override fun <T, U> foldLeft(fa: ConstOf<A, T>, b: U, f: (U, T) -> U): U = b

    override fun <T, U> foldRight(fa: ConstOf<A, T>, lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb

}

@instance(Const::class)
interface ConstTraverseInstance<X> : ConstFoldableInstance<X>, Traverse<ConstPartialOf<X>> {

    override fun <T, U> map(fa: ConstOf<X, T>, f: (T) -> U): Const<X, U> = fa.fix().retag()

    override fun <G, A, B> traverse(fa: ConstOf<X, A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, ConstOf<X, B>> =
            fa.fix().traverse(f, GA)
}

@instance(Const::class)
interface ConstSemigroupInstance<A, T> : Semigroup<ConstOf<A, T>> {

    fun SA(): Semigroup<A>

    override fun combine(a: ConstOf<A, T>, b: ConstOf<A, T>): Const<A, T> = a.combine(b, SA())

}

@instance(Const::class)
interface ConstMonoidInstance<A, T> : ConstSemigroupInstance<A, T>, Monoid<ConstOf<A, T>> {

    override fun SA(): Monoid<A>

    override fun empty(): Const<A, T> = Const(SA().empty())

}

@instance(Const::class)
interface ConstEqInstance<A, T> : Eq<Const<A, T>> {

    fun EQ(): Eq<A>

    override fun Const<A, T>.eqv(b: Const<A, T>): Boolean =
            EQ().run { value.eqv(b.value) }
}

@instance(Const::class)
interface ConstShowInstance<A, T> : Show<Const<A, T>> {
    override fun show(a: Const<A, T>): String =
            a.toString()
}