package arrow.typeclasses.suspended

import arrow.Kind

interface Prelude {
  suspend fun <A> effectIdentity(a: A): A = a
}

interface BindSyntax<F> : Prelude {
  suspend fun <A> Kind<F, A>.bind(): A
  suspend operator fun <A> Kind<F, A>.component1(): A =
    bind()
}