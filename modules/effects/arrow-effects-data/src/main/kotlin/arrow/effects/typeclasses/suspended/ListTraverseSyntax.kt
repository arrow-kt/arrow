package arrow.effects.typeclasses.suspended

interface ListTraverseSyntax<F> : MonadSyntax<F> {

  suspend fun <A, B> List<suspend () -> A>.traverseEffects(f: suspend (A) -> B): List<B> =
    map { fa: suspend () -> A -> f(fa()) }

  suspend fun <A> List<suspend () -> A>.sequenceEffects(): List<A> =
    traverseEffects(::effectIdentity)

  suspend fun <A, B> List<suspend () -> A>.flatTraverseEffects(f: suspend (A) -> List<B>): List<B> =
    flatMap { f(it()) }

  suspend fun <A> List<List<suspend () -> A>>.flatSequenceEffects(): List<A> =
    flatten().sequenceEffects()
}