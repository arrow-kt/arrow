package kats

/**
 * Basic implementation of the Reader monad. Provides an "implicit" context (configuration) for
 * function execution. Intended to provide Dependency Injection.
 */
class Reader<C : Any, out A : Any>(val run: (C) -> A) {

    inline fun <B : Any> map(crossinline fa: (A) -> B): Reader<C, B> = Reader { c -> fa(run(c)) }

    inline fun <B : Any> flatMap(crossinline fa: (A) -> Reader<C, B>): Reader<C, B> = Reader { c ->
        fa(run(c)).run(c)
    }

    /**
     * local combinator allows switching the environment to unify two different dependency types, so
     * you can compose readers with different type dependencies.
     *
     * D: type represents a bigger context than C.
     * @param fd: function to convert from the bigger context D to a context of type C.
     */
    inline fun <D : Any> local(crossinline fd: (D) -> C): Reader<D, A> = Reader { d ->
        run(fd(d))
    }

    companion object Factory {

        /**
         * Lifts an A value to Reader wrapping it in a supplier function with a Nothing argument.
         */
        fun <C: Any, A : Any> pure(a: A): Reader<C, A> = Reader { _ -> a }

        /**
         * Lifts read function to Reader.
         */
        inline operator fun <C : Any, A : Any> invoke(noinline rd: (C) -> A) = Reader(rd)
    }
}
