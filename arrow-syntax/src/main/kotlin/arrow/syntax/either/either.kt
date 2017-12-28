package arrow.syntax.either

import arrow.*

fun <A> A.left(): Either<A, Nothing> = Either.Left(this)

fun <A> A.right(): Either<Nothing, A> = Either.Right(this)

fun <G, A, B, C> Either<A, B>.traverse(f: (B) -> HK<G, C>, GA: Applicative<G>): HK<G, Either<A, C>> =
        this.ev().fold({ GA.pure(Either.Left(it)) }, { GA.map(f(it), { Either.Right(it) }) })