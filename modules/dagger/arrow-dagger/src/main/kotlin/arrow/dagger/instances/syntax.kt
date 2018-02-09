package arrow.dagger.instances

import arrow.typeclasses.*
import javax.inject.Inject

class DaggerEqSyntaxInstance<F> @Inject constructor(val eq: Eq<F>) : EqSyntax<F> {
    override fun eq(): Eq<F> = eq
}

class DaggerOrderSyntaxInstance<F> @Inject constructor(val order: Order<F>) : OrderSyntax<F> {
    override fun order(): Order<F> = order
}

class DaggerFunctorSyntaxInstance<F> @Inject constructor(val functor: Functor<F>) : FunctorSyntax<F> {
    override fun functor(): Functor<F> = functor
}

class DaggerApplicativeSyntaxInstance<F> @Inject constructor(val applicative: Applicative<F>) : ApplicativeSyntax<F> {
    override fun applicative(): Applicative<F> = applicative
}

class DaggerMonadSyntaxInstance<F> @Inject constructor(val monad: Monad<F>) : MonadSyntax<F> {
    override fun monad(): Monad<F> = monad
}

class DaggerFoldableSyntaxInstance<F> @Inject constructor(val foldable: Foldable<F>) : FoldableSyntax<F> {
    override fun foldable(): Foldable<F> = foldable
}

class DaggerTraverseSyntaxInstance<F> @Inject constructor(val traverse: Traverse<F>) : TraverseSyntax<F> {
    override fun traverse(): Traverse<F> = traverse
}

class DaggerAlternativeSyntaxInstance<F> @Inject constructor(val alternative: Alternative<F>) : AlternativeSyntax<F> {
    override fun alternative(): Alternative<F> = alternative
}

class DaggerApplicativeErrorSyntaxInstance<F, E> @Inject constructor(val applicativeError: ApplicativeError<F, E>) : ApplicativeErrorSyntax<F, E> {
    override fun applicativeError(): ApplicativeError<F, E> = applicativeError
}

class DaggerBimonadSyntaxInstance<F> @Inject constructor(val bimonad: Bimonad<F>) : BimonadSyntax<F> {
    override fun bimonad(): Bimonad<F> = bimonad
}

class DaggerComonadSyntaxInstance<F> @Inject constructor(val comonad: Comonad<F>) : ComonadSyntax<F> {
    override fun comonad(): Comonad<F> = comonad
}

class DaggerInjectSyntaxInstance<F, G> @Inject constructor(val inject: arrow.typeclasses.Inject<F, G>) : InjectSyntax<F, G> {
    override fun inject(): arrow.typeclasses.Inject<F, G> = inject
}

class DaggerMonaderrorSyntaxInstance<F, E> @Inject constructor(val monadError: MonadError<F, E>) : MonadErrorSyntax<F, E> {
    override fun monadError(): MonadError<F, E> = monadError
}

class DaggerSemigroupSyntaxInstance<A> @Inject constructor(val semigroup: Semigroup<A>) : SemigroupSyntax<A> {
    override fun semigroup(): Semigroup<A> = semigroup
}

class DaggerSemigroupKSyntaxInstance<F> @Inject constructor(val semigroupK: SemigroupK<F>) : SemigroupKSyntax<F> {
    override fun semigroupK(): SemigroupK<F> = semigroupK
}

class DaggerMonoidKSyntaxInstance<F> @Inject constructor(val monoidK: MonoidK<F>) : MonoidKSyntax<F> {
    override fun monoidK(): MonoidK<F> = monoidK
}

class DaggerReducibleSyntaxInstance<F> @Inject constructor(val reducible: Reducible<F>) : ReducibleSyntax<F> {
    override fun reducible(): Reducible<F> = reducible
}