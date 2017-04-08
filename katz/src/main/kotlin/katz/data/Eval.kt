/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

/**
 * Eval is a monad which controls evaluation of a value or a computation that produces a value.
 *
 * Three basic evaluation strategies:
 *
 *  - Now:    evaluated immediately
 *  - Later:  evaluated once when value is needed
 *  - Always: evaluated every time value is needed
 *
 * The Later and Always are both lazy strategies while Now is eager.
 * Later and Always are distinguished from each other only by
 * memoization: once evaluated Later will save the value to be returned
 * immediately if it is needed again. Always will run its computation
 * every time.
 *
 * Eval supports stack-safe lazy computation via the .map and .flatMap
 * methods, which use an internal trampoline to avoid stack overflows.
 * Computation done within .map and .flatMap is always done lazily,
 * even when applied to a Now instance.
 *
 * It is not generally good style to pattern-match on Eval instances.
 * Rather, use .map and .flatMap to chain computation, and use .value
 * to get the result when needed. It is also not good style to create
 * Eval instances whose computation involves calling .value on another
 * Eval instance -- this can defeat the trampolining and lead to stack
 * overflows.
 */
sealed class Eval<A> {

    abstract fun value(): A

    abstract fun memoize(): Eval<A>

    fun <B> map(f: (A) -> B): Eval<B> = flatMap { a -> Now(f(a)) }

    fun <B> flatMap(f: (A) -> Eval<B>): Eval<B> = when (this) {
        is Compute<A, *> -> object : Compute<B, Compute.Start>() {
            override val start: () -> Eval<Start> = (this@Eval as Compute<A, Start>).start
            override val run: (Start) -> Eval<B> = { s: Compute.Start ->
                object : Compute<B, A>() {
                    override val start: () -> Eval<A> = { (this@Eval as Compute<A, Start>).run(s) }
                    override val run = f
                }
            }
        }
        is Eval.Call<A> -> object : Eval.Compute<B, A>() {
            override val start: () -> Eval<A> = this@Eval.thunk
            override val run: (A) -> Eval<B> = f
        }
        else -> object : Eval.Compute<B, A>() {
            override val start: () -> Eval<A> = { this@Eval }
            override val run: (A) -> Eval<B> = f
        }
    }

/*this match {
    case c: Eval.Call[A] =>
    new Eval.Compute[B] {
        type Start = A
        val start = c.thunk
        val run = f
    }
    case _ =>
    new Eval.Compute[B] {
        type Start = A
        val start = () => self
        val run = f
    }
}*/

    /**
     * Construct an eager Eval[A] instance.
     *
     * In some sense it is equivalent to using a val.
     *
     * This type should be used when an A value is already in hand, or
     * when the computation to produce an A value is pure and very fast.
     */
    class Now<A>(val value: A) : Eval<A>() {
        override fun value(): A = value
        override fun memoize(): Eval<A> = this
    }

    /**
     * Construct a lazy Eval[A] instance.
     *
     * This type should be used for most "lazy" values. In some sense it
     * is equivalent to using a lazy val.
     *
     * When caching is not required or desired (e.g. if the value produced
     * may be large) prefer Always. When there is no computation
     * necessary, prefer Now.
     *
     * Once Later has been evaluated, the closure (and any values captured
     * by the closure) will not be retained, and will be available for
     * garbage collection.
     */
    class Later<A>(val f: () -> A) : Eval<A>() {
        override fun value(): A = value
        val value: A by lazy {
            val result = f()
            result
        }

        override fun memoize(): Eval<A> = this
    }

    object LaterFactory {
        fun <A> apply(a: () -> A): Later<A> = Later(a)
    }

    /**
     * Construct a lazy Eval[A] instance.
     *
     * This type can be used for "lazy" values. In some sense it is
     * equivalent to using a Function0 value.
     *
     * This type will evaluate the computation every time the value is
     * required. It should be avoided except when laziness is required and
     * caching must be avoided. Generally, prefer Later.
     */
    class Always<A>(val f: () -> A) : Eval<A>() {
        override fun value(): A = f()
        override fun memoize(): Eval<A> = Later(f)
    }

    object AlwaysFactory {
        fun <A> apply(a: () -> A): Always<A> = Always(a)
    }

    companion object EvalFactory {
        fun <A> now(a: A) = Now(a)
        fun <A> later(f: () -> A) = Later(f)
        fun <A> always(f: () -> A) = Always(f)
        fun <A> defer(f: () -> Eval<A>): Eval<A> = Call(f)
        fun raise(t: Throwable): Eval<Nothing> = defer { throw t }

        val Unit: Eval<Unit> = Now(kotlin.Unit)
        val True: Eval<Boolean> = Now(true)
        val False: Eval<Boolean> = Now(false)
        val Zero: Eval<Int> = Now(0)
        val One: Eval<Int> = Now(1)
    }

    /**
     * Call is a type of Eval[A] that is used to defer computations
     * which produce Eval[A].
     *
     * Users should not instantiate Call instances themselves. Instead,
     * they will be automatically created when needed.
     */
    class Call<A>(val thunk: () -> Eval<A>) : Eval<A>() {
        override fun memoize(): Eval<A> = Later { value() }
        override fun value(): A = CallFactory.loop(this).value()
    }

    object CallFactory {

        /**
         * Collapse the call stack for eager evaluations.
         */
        tailrec fun <A> loop(fa: Eval<A>): Eval<A> = when (fa) {
            is Call<A> -> loop(fa.thunk())
            is Compute<A, *> -> {
                fa as Compute<A, Compute.Start>
                object : Compute<A, Compute.Start>() {
                    override val start: () -> Eval<Compute.Start> = { fa.start() }
                    override val run: (Compute.Start) -> Eval<A> = { s -> loop1(fa.run(s)) }
                }
            }
            else -> fa
        }

        /**
         * Alias for loop that can be called in a non-tail position
         * from an otherwise tailrec-optimized loop.
         */
        fun <A> loop1(fa: Eval<A>): Eval<A> = loop(fa)
    }

    /**
     * Compute is a type of Eval[A] that is used to chain computations
     * involving .map and .flatMap. Along with Eval#flatMap it
     * implements the trampoline that guarantees stack-safety.
     *
     * Users should not instantiate Compute instances
     * themselves. Instead, they will be automatically created when
     * needed.
     *
     * Unlike a traditional trampoline, the internal workings of the
     * trampoline are not exposed. This allows a slightly more efficient
     * implementation of the .value method.
     */
    abstract class Compute<A, StartT> : Eval<A>() {
        open class Start

        abstract val start: () -> Eval<StartT>
        abstract val run: (StartT) -> Eval<A>

        override fun memoize(): Eval<A> = Later { value() }

        override fun value(): A {
            tailrec fun loop(curr: L, fs: List<C>): Any =
                    when (curr) {
                        is Compute<*, *> -> {
                            curr as Compute<A, StartT>
                            val cc = curr.start()
                            when (cc) {
                                is Compute<*, *> -> loop(
                                        cc.start() as L,
                                        listOf(cc.run as C) + listOf(curr.run as C) + fs)
                                else -> loop(curr.run(cc.value()) as L, fs)
                            }
                        }
                        else -> when {
                            fs.size >= 2 -> loop(fs[0](curr.value()), fs)
                            else -> curr.value()!!
                        }
                    }
            return loop(this as L, listOf()) as A
        }
    }
}

typealias L = Eval<Any>
typealias C = (Any) -> Eval<Any>
