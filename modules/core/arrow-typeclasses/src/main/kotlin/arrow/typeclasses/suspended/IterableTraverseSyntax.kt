package arrow.typeclasses.suspended

import arrow.Kind

interface IterableTraverseSyntax<F> : MonadSyntax<F> {

  fun <A, B> Iterable<suspend () -> A>.traverse(f: suspend (A) -> B): Kind<F, List<B>> =
    effect { map { fa: suspend () -> A -> f(fa()) } }

  fun <A> Iterable<suspend () -> A>.sequence(): Kind<F, List<A>> =
    traverse(::effectIdentity)

  fun <A, B> Iterable<suspend () -> A>.flatTraverse(f: suspend (A) -> List<B>): Kind<F, List<B>> =
    effect { flatMap { f(it()) } }

  fun <A> Iterable<Iterable<suspend () -> A>>.flatSequence(): Kind<F, List<A>> =
    flatten().sequence()

}