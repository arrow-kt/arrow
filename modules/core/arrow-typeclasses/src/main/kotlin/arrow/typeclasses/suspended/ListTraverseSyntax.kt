package arrow.typeclasses.suspended

interface ListTraverseSyntax<F> : MonadSyntax<F> {

  suspend fun <A, B> List<suspend () -> A>.traverse(f: suspend (A) -> B): List<B> =
    map { fa: suspend () -> A -> f(fa()) }

  suspend fun <A> List<suspend () -> A>.sequence(): List<A> =
    traverse(::effectIdentity)

  suspend fun <A, B> List<suspend () -> A>.flatTraverse(f: suspend (A) -> List<B>): List<B> =
    flatMap { f(it()) }

  suspend fun <A> List<List<suspend () -> A>>.flatSequence(): List<A> =
    flatten().sequence()
}