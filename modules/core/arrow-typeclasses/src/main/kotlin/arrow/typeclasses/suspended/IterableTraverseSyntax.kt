package arrow.typeclasses.suspended

interface IterableTraverseSyntax<F> : MonadSyntax<F> {

  suspend fun <A, B> Iterable<suspend () -> A>.traverse(f: suspend (A) -> B): List<B> =
    map { fa: suspend () -> A -> f(fa()) }

  suspend fun <A> Iterable<suspend () -> A>.sequence(): List<A> =
    traverse(::effectIdentity)

  suspend fun <A, B> Iterable<suspend () -> A>.flatTraverse(f: suspend (A) -> List<B>): List<B> =
    flatMap { f(it()) }

  suspend fun <A> Iterable<Iterable<suspend () -> A>>.flatSequence(): List<A> =
    flatten().sequence()
}