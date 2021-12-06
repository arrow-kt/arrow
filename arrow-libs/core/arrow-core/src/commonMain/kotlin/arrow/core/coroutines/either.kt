@file:JvmMultifileClass
@file:JvmName("ControlKt")
package arrow.core.coroutines

import arrow.core.Either
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public object either {
  public fun <E, A> eager(f: suspend EagerControlEffect<E>.() -> A): Either<E, A> =
    eagerControl(f).toEither()

  public suspend operator fun <E, A> invoke(f: suspend ControlEffect<E>.() -> A): Either<E, A> =
    control(f).toEither()
}

