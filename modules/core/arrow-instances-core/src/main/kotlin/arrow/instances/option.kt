package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*
import arrow.instances.traverse as optionTraverse

@instance(Option::class)
interface OptionSemigroupInstance<A> : Semigroup<Option<A>> {

  fun SG(): Semigroup<A>

  override fun Option<A>.combine(b: Option<A>): Option<A> =
    when (this) {
      is Some<A> -> when (b) {
        is Some<A> -> Some(SG().run { t.combine(b.t) })
        None -> this
      }
      None -> b
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
    fix().orElse { f(Unit).fix() }
}

@instance(Option::class)
interface OptionMonadErrorInstance : OptionMonadInstance, MonadError<ForOption, Unit> {
  override fun <A> raiseError(e: Unit): Kind<ForOption, A> =
    None

  override fun <A> Kind<ForOption, A>.handleErrorWith(f: (Unit) -> Kind<ForOption, A>): Option<A> =
    fix().orElse { f(Unit).fix() }
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
    fix().map(f)
}

@instance(Option::class)
interface OptionApplicativeInstance : Applicative<ForOption> {
  override fun <A, B> Kind<ForOption, A>.apPipe(ff: Kind<ForOption, (A) -> B>): Option<B> =
    fix().apPipe(ff)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)
}

@instance(Option::class)
interface OptionMonadInstance : Monad<ForOption> {
  override fun <A, B> Kind<ForOption, A>.apPipe(ff: Kind<ForOption, (A) -> B>): Option<B> =
    fix().apPipe(ff)

  override fun <A, B> Kind<ForOption, A>.flatMap(f: (A) -> Kind<ForOption, B>): Option<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> OptionOf<Either<A, B>>): Option<B> =
    Option.tailRecM(a, f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)
}

@instance(Option::class)
interface OptionFoldableInstance : Foldable<ForOption> {
  override fun <A> Kind<ForOption, A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForOption, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForOption, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> OptionOf<A>.forAll(p: (A) -> Boolean): Boolean =
    fix().forall(p)

  override fun <A> Kind<ForOption, A>.isEmpty(): Boolean =
    fix().isEmpty()

  override fun <A> Kind<ForOption, A>.nonEmpty(): Boolean =
    fix().nonEmpty()
}

fun <A, G, B> OptionOf<A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Option<B>> = GA.run {
  fix().fold({ just(None) }, { f(it).map { Some(it) } })
}

fun <A, G> OptionOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Option<A>> =
  optionTraverse(GA, ::identity)

fun <A, G, B> OptionOf<A>.traverseFilter(GA: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, Option<B>> = GA.run {
  fix().fold({ just(None) }, f)
}

@instance(Option::class)
interface OptionTraverseInstance : Traverse<ForOption> {
  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <G, A, B> Kind<ForOption, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Option<B>> =
    optionTraverse(AP, f)

  override fun <A> Kind<ForOption, A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForOption, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForOption, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForOption, A>.forAll(p: (A) -> Boolean): Boolean =
    fix().forall(p)

  override fun <A> Kind<ForOption, A>.isEmpty(): Boolean =
    fix().isEmpty()

  override fun <A> Kind<ForOption, A>.nonEmpty(): Boolean =
    fix().nonEmpty()
}

object OptionContext : OptionMonadErrorInstance, OptionTraverseInstance {
  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)
}

infix fun <A> ForOption.Companion.extensions(f: OptionContext.() -> A): A =
  f(OptionContext)
