package arrow.effects.typeclasses.suspended

import arrow.core.Either

typealias SProc<A> = suspend ((Either<Throwable, A>) -> Unit) -> Unit