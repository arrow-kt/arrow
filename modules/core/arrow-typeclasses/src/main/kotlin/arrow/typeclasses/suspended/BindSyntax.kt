package arrow.typeclasses.suspended

import arrow.Kind

interface BindSyntax<F> {

  suspend fun <A> Kind<F, A>.bind(): A

  suspend operator fun <A> Kind<F, A>.component1(): A =
    bind()

  suspend operator fun <A> Kind<F, A>.not(): A =
    bind()

}