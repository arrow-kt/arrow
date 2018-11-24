package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.extension
import arrow.typeclasses.*
import arrow.instances.traverse as optionTraverse

@extension
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

@extension
interface OptionMonoidInstance<A> : Monoid<Option<A>>, OptionSemigroupInstance<A> {
  override fun SG(): Semigroup<A>
  override fun empty(): Option<A> = None
}

@extension
interface OptionApplicativeErrorInstance : ApplicativeError<ForOption, Unit>, OptionApplicativeInstance {
  override fun <A> raiseError(e: Unit): Option<A> =
    None

  override fun <A> Kind<ForOption, A>.handleErrorWith(f: (Unit) -> Kind<ForOption, A>): Option<A> =
    fix().orElse { f(Unit).fix() }
}

@extension
interface OptionMonadErrorInstance : MonadError<ForOption, Unit>, OptionMonadInstance {
  override fun <A> raiseError(e: Unit): Kind<ForOption, A> =
    None

  override fun <A> Kind<ForOption, A>.handleErrorWith(f: (Unit) -> Kind<ForOption, A>): Option<A> =
    fix().orElse { f(Unit).fix() }
}

@extension
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

@extension
interface OptionShowInstance<A> : Show<Option<A>> {
  override fun Option<A>.show(): String =
    toString()
}

@extension
interface OptionFunctorInstance : Functor<ForOption> {
  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)
}

@extension
interface OptionApplicativeInstance : Applicative<ForOption> {
  override fun <A, B> Kind<ForOption, A>.ap(ff: Kind<ForOption, (A) -> B>): Option<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)
}

@extension
interface OptionMonadInstance : Monad<ForOption> {
  override fun <A, B> Kind<ForOption, A>.ap(ff: Kind<ForOption, (A) -> B>): Option<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForOption, A>.flatMap(f: (A) -> Kind<ForOption, B>): Option<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> OptionOf<Either<A, B>>): Option<B> =
    Option.tailRecM(a, f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)
}

@extension
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

@extension
interface OptionSemigroupKInstance : SemigroupK<ForOption> {
  override fun <A> Kind<ForOption, A>.combineK(y: Kind<ForOption, A>): Option<A> =
    orElse { y.fix() }
}

@extension
interface OptionMonoidKInstance : MonoidK<ForOption> {
  override fun <A> empty(): Option<A> =
    Option.empty()

  override fun <A> Kind<ForOption, A>.combineK(y: Kind<ForOption, A>): Option<A> =
    orElse { y.fix() }
}

fun <A, G, B> OptionOf<A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Option<B>> = GA.run {
  fix().fold({ just(None) }, { f(it).map { Some(it) } })
}

fun <A, G> OptionOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Option<A>> =
  optionTraverse(GA, ::identity)

fun <A, G, B> OptionOf<A>.traverseFilter(GA: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, Option<B>> = GA.run {
  fix().fold({ just(None) }, f)
}

@extension
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

@extension
interface OptionHashInstance<A> : Hash<Option<A>>, OptionEqInstance<A> {

  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun Option<A>.hash(): Int = fold({
    None.hashCode()
  }, {
    HA().run { it.hash() }
  })
}

object OptionContext : OptionMonadErrorInstance, OptionTraverseInstance {
  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)
}

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForOption.Companion.extensions(f: OptionContext.() -> A): A =
  f(OptionContext)