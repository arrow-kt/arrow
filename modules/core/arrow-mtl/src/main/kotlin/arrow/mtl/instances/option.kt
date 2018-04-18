package arrow.mtl.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.mtl.typeclasses.MonadFilter
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.Applicative
import arrow.instances.traverse as optionTraverse
import arrow.instances.traverseFilter as optionTraverseFilter

@instance(Option::class)
interface OptionTraverseFilterInstance : TraverseFilter<ForOption> {
  override fun <A> Kind<ForOption, A>.filter(f: (A) -> Boolean): Option<A> =
    fix().filter(f)

  override fun <G, A, B> Kind<ForOption, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): arrow.Kind<G, Option<B>> =
    optionTraverseFilter(AP, f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <G, A, B> Kind<ForOption, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): arrow.Kind<G, Option<B>> =
    optionTraverse(AP, f)

  override fun <A> Kind<ForOption, A>.exists(p: (A) -> Boolean): kotlin.Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForOption, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForOption, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> OptionOf<A>.forAll(p: (A) -> Boolean): kotlin.Boolean =
    fix().forall(p)

  override fun <A> Kind<ForOption, A>.isEmpty(): kotlin.Boolean =
    fix().isEmpty()

  override fun <A> Kind<ForOption, A>.nonEmpty(): kotlin.Boolean =
    fix().nonEmpty()
}

@instance(Option::class)
interface OptionMonadFilterInstance : MonadFilter<ForOption> {
  override fun <A> empty(): Option<A> =
    Option.empty()

  override fun <A, B> Kind<ForOption, A>.ap(ff: Kind<ForOption, (A) -> B>): Option<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForOption, A>.flatMap(f: (A) -> Kind<ForOption, B>): Option<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, OptionOf<Either<A, B>>>): Option<B> =
    Option.tailRecM(a, f)

  override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
    fix().map(f)

  override fun <A> just(a: A): Option<A> =
    Option.just(a)

  override fun <A> Kind<ForOption, A>.filter(f: (A) -> Boolean): Option<A> =
    fix().filter(f)
}
