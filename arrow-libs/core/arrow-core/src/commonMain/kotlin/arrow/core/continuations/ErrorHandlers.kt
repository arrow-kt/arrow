@file:JvmMultifileClass
@file:JvmName("Effect")

package arrow.core.continuations

import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@OptIn(ExperimentalTypeInference::class)
public infix fun <E, E2, A> Effect<E, A>.catch(@BuilderInference resolve: suspend Shift<E2>.(E) -> A): Effect<E2, A> =
  effect {
    catch({ bind() }) { e -> resolve(e) }
  }

@OptIn(ExperimentalTypeInference::class)
public infix fun <E, A> Effect<E, A>.attempt(@BuilderInference recover: suspend Shift<E>.(Throwable) -> A): Effect<E, A> =
  effect {
    attempt({ bind() }) { t -> recover(t) }
  }

@OptIn(ExperimentalTypeInference::class)
@JvmName("attemptOrThrow")
public inline infix fun <reified T : Throwable, E, A> Effect<E, A>.attempt(
  @BuilderInference crossinline recover: suspend Shift<E>.(T) -> A,
): Effect<E, A> = effect {
  attempt({ bind() }) { t: T -> recover(t) }
}

@OptIn(ExperimentalTypeInference::class)
public infix fun <E, E2, A> EagerEffect<E, A>.catch(@BuilderInference resolve: Shift<E2>.(E) -> A): EagerEffect<E2, A> =
  eagerEffect {
    catch({ bind() }) { e -> resolve(e) }
  }

@OptIn(ExperimentalTypeInference::class)
public infix fun <E, A> EagerEffect<E, A>.attempt(@BuilderInference recover: Shift<E>.(Throwable) -> A): EagerEffect<E, A> =
  eagerEffect {
    attempt({ bind() }) { t -> recover(t) }
  }

@OptIn(ExperimentalTypeInference::class)
@JvmName("attemptOrThrow")
public inline infix fun <reified T : Throwable, E, A> EagerEffect<E, A>.attempt(
  @BuilderInference crossinline recover: Shift<E>.(T) -> A,
): EagerEffect<E, A> = eagerEffect {
  attempt({ bind() }) { t: T -> recover(t) }
}
