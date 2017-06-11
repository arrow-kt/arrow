package katz.effects.internal

/**
 * Abandon all hope
 * Ye who enter here
 */
internal sealed class AndThen<in A, out B> : (A) -> B {
    internal data class Single<in A, out B>(val f: (A) -> B) : AndThen<A, B>()
    internal data class ErrorHandler<in A, out B>(val fa: (A) -> B, val fe: (Throwable) -> B) : AndThen<A, B>()
    internal data class Concat<in A, E, out B>(val left: AndThen<A, E>, val right: AndThen<E, B>) : AndThen<A, B>()

    fun <C> andThen(g: AndThen<B, C>): AndThen<A, C> =
            Concat(this, g)

    fun <C> compose(g: AndThen<C, A>): AndThen<C, B> =
            Concat(g, this)

    override operator fun invoke(a: A): B =
            runLoop(a, null, true)

    /**
     * Raises an error continuation.
     * As Kotlin lacks a way of signaling superclass, the return type of the function has to be hinted by the caller.
     */
    @Suppress("UNCHECKED_CAST")
    fun error(throwable: Throwable, fe: Function1<Throwable, /* B */ *>): B =
            try {
                runLoop(null, throwable, false)
            } catch (throwable: Throwable) {
                fe(throwable) as B
            }

    @Suppress("UNCHECKED_CAST")
    private fun runLoop(_success: A?, _failure: Throwable?, _isSuccess: Boolean): B {
        var self: AndThen<Any?, Any?> = this as AndThen<Any?, Any?>
        var success: Any? = _success
        var failure = _failure
        var isSuccess = _isSuccess
        var continues = true

        fun processSuccess(f: (Any?) -> Any?): Unit =
                try {
                    success = f(success)
                } catch(throwable: Throwable) {
                    failure = throwable
                    isSuccess = false
                }

        fun processError(f: (Throwable) -> Any?): Unit =
                try {
                    /* If we've made it this far then failure isn't null */
                    success = f(failure!!)
                    isSuccess = true
                } catch(throwable: Throwable) {
                    failure = throwable
                }

        fun <A, B, E> AndThen<A, B>.rotateAccum(_right: AndThen<B, E>): AndThen<A, E> {
            var me: AndThen<Any?, Any?> = this as AndThen<Any?, Any?>
            var right: AndThen<Any?, Any?> = _right as AndThen<Any?, Any?>
            var continued = true
            while (continued) {
                when (me) {
                    is Concat<*, *, *> -> {
                        val concat: Concat<Any?, Any?, Any?> = me as Concat<Any?, Any?, Any?>
                        me = concat.left
                        right = concat.right.andThen(right)
                    }
                /* Either Single or ErrorHandler */
                    else -> {
                        me = me.andThen(right)
                        continued = false
                    }
                }
            }
            return me as AndThen<A, E>
        }

        while (continues) {
            when (self) {
                is Single -> {
                    if (isSuccess) {
                        processSuccess(self.f)
                    }
                    continues = false
                }
                is ErrorHandler -> {
                    if (isSuccess) {
                        processSuccess(self.fa)
                    } else {
                        processError(self.fe)
                    }
                    continues = false
                }
                is Concat<*, *, *> -> {
                    val left: AndThen<Any?, Any?> = self.left as AndThen<Any?, Any?>
                    val right: AndThen<Any?, Any?> = self.right as AndThen<Any?, Any?>
                    self = when (left) {
                        is Single -> {
                            if (isSuccess) {
                                processSuccess(left.f)
                            }
                            right
                        }
                        is ErrorHandler -> {
                            if (isSuccess) {
                                processSuccess(left.fa)
                            } else {
                                processError(left.fe)
                            }
                            right
                        }
                        is Concat<*, *, *> ->
                            left.rotateAccum(right)
                    }
                }
            }
        }

        if (isSuccess) {
            return success as B
        } else {
            throw failure!!
        }
    }

    companion object {
        operator fun <A, B> invoke(f: (A) -> B): AndThen<A, B> =
                Single(f)

        operator fun <A, B> invoke(fa: (A) -> B, fb: (Throwable) -> B): AndThen<A, B> =
                ErrorHandler(fa, fb)
    }
}