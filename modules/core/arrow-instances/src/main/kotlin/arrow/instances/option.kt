package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*

@instance(Option::class)
interface OptionSemigroupInstance<A> : Semigroup<Option<A>> {

    fun SG(): Semigroup<A>

    override fun Option<A>.combine(b: Option<A>): Option<A> =
            when (this) {
                is Some<A> -> when (b) {
                    is Some<A> -> Some(SG().run { t.combine(b.t) })
                    None -> b
                }
                None -> this
            }
}

@instance(Option::class)
interface OptionMonoidInstance<A> : OptionSemigroupInstance<A>, Monoid<Option<A>> {
    override fun empty(): Option<A> = None
}

@instance(Option::class)
interface OptionApplicativeErrorInstance : OptionApplicativeInstance, ApplicativeError<ForOption, Unit> {
    override fun <A> raiseError(e: Unit): Option<A> =
            None

    override fun <A> Kind<ForOption, A>.handleErrorWith(f: (Unit) -> Kind<ForOption, A>): Option<A> =
            fix().orElse({ f(Unit).fix() })
}

@instance(Option::class)
interface OptionMonadErrorInstance : OptionMonadInstance, MonadError<ForOption, Unit> {
    override fun <A> raiseError(e: Unit): Kind<ForOption, A> =
            None

    override fun <A> Kind<ForOption, A>.handleErrorWith(f: (Unit) -> Kind<ForOption, A>): Option<A> =
            fix().orElse({ f(Unit).fix() })
}

@instance(Option::class)
interface OptionEqInstance<A> : Eq<Option<A>> {

    fun EQ(): Eq<A>

    override fun Option<A>.eqv(b: Option<A>): Boolean = when (this) {
        is Some -> when (b) {
            None -> false
            is Some -> EQ().run { t.eqv(b.t) }
        }
        None -> when (b) {
            None -> true
            is Some -> false
        }
    }

}

@instance(Option::class)
interface OptionShowInstance<A> : Show<Option<A>> {
    override fun Option<A>.show(): String =
            toString()
}

@instance(Option::class)
interface OptionFunctorInstance : Functor<ForOption> {
    override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
            this@map.fix().map(f)
}

@instance(Option::class)
interface OptionApplicativeInstance : Applicative<ForOption> {
    override fun <A, B> Kind<ForOption, A>.ap(ff: Kind<ForOption, (A) -> B>): Option<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
            this@map.fix().map(f)

    override fun <A> pure(a: A): Option<A> =
            Option.pure(a)
}

@instance(Option::class)
interface OptionMonadInstance : Monad<ForOption> {
    override fun <A, B> Kind<ForOption, A>.ap(ff: Kind<ForOption, (A) -> B>): Option<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForOption, A>.flatMap(f: (A) -> Kind<ForOption, B>): Option<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, OptionOf<Either<A, B>>>): Option<B> =
            Option.tailRecM(a, f)

    override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
            this@map.fix().map(f)

    override fun <A> pure(a: A): Option<A> =
            Option.pure(a)
}

@instance(Option::class)
interface OptionFoldableInstance : Foldable<ForOption> {
    override fun <A> Kind<ForOption, A>.exists(p: (A) -> Boolean): kotlin.Boolean =
            fix().exists(p)

    override fun <A, B> foldLeft(fa: OptionOf<A>, b: B, f: Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: OptionOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> OptionOf<A>.forAll(p: (A) -> Boolean): kotlin.Boolean =
            fix().forall(p)

    override fun <A> Kind<ForOption, A>.isEmpty(): kotlin.Boolean =
            fix().isEmpty()

    override fun <A> Kind<ForOption, A>.nonEmpty(): kotlin.Boolean =
            fix().nonEmpty()
}

fun <A, G, B> OptionOf<A>.traverse(f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Option<B>> = GA.run {
    fix().let { option ->
        when (option) {
            is Some -> f(option.t).map({ Some(it) })
            is None -> pure(None)
        }
    }
}

fun <A, G, B> OptionOf<A>.traverseFilter(f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>): Kind<G, Option<B>> =
        this.fix().let { option ->
            when (option) {
                is Some -> f(option.t)
                None -> GA.pure(None)
            }
        }

@instance(Option::class)
interface OptionTraverseInstance : Traverse<ForOption> {
    override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
            fix().map(f)

    override fun <G, A, B> Kind<ForOption, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Option<B>> =
            fix().traverse(f, AP)

    override fun <A> arrow.Kind<arrow.core.ForOption, A>.exists(p: (A) -> kotlin.Boolean): kotlin.Boolean =
            fix().exists(p)

    override fun <A, B> foldLeft(fa: OptionOf<A>, b: B, f: Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: OptionOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> Kind<ForOption, A>.forAll(p: (A) -> Boolean): kotlin.Boolean =
            fix().forall(p)

    override fun <A> Kind<ForOption, A>.isEmpty(): kotlin.Boolean =
            fix().isEmpty()

    override fun <A> Kind<ForOption, A>.nonEmpty(): kotlin.Boolean =
            fix().nonEmpty()
}