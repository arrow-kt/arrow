package arrow.effects.typeclasses.suspended

import arrow.core.*
import arrow.core.extensions.either.applicative.tupled as tuppledEither
import arrow.data.extensions.validated.applicative.tupled as tuppledVal

typealias STuple2<A, B> = Tuple2<suspend () -> A, suspend () -> B>
typealias STuple3<A, B, C> = Tuple3<suspend () -> A, suspend () -> B, suspend () -> C>
typealias STuple4<A, B, C, D> = Tuple4<suspend () -> A, suspend () -> B, suspend () -> C, suspend () -> D>
typealias STuple5<A, B, C, D, E> = Tuple5<suspend () -> A, suspend () -> B, suspend () -> C, suspend () -> D, suspend () -> E>
typealias SProc<A> = suspend ((Either<Throwable, A>) -> Unit) -> Unit