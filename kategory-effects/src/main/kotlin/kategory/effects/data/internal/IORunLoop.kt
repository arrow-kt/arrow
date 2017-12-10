package kategory.effects.data.internal

import kategory.Either
import kategory.effects.IO

object IORunLoop {
    fun <A> start(source: IO<A>, cb: (Either<Throwable, A>) -> Unit): Unit = TODO()

    fun <A> step(source: IO<A>): IO<A> = TODO()
}