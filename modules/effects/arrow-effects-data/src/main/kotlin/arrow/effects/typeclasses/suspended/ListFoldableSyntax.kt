package arrow.effects.typeclasses.suspended

interface ListFoldableSyntax<F> : MonadSyntax<F> {

  suspend fun <A, B> List<suspend () -> A>.traverse_(f: suspend (A) -> B): Unit =
    forEach { f(it()) }

  suspend fun <A> List<suspend () -> A>.sequence_(): Unit =
    traverse_(::effectIdentity)

}