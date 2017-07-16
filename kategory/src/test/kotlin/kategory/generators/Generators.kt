package kategory

import io.kotlintest.properties.Gen

inline fun <reified F, A> genApplicative(valueGen: Gen<A>, AP: Applicative<F> = applicative<F>()): Gen<HK<F, A>> =
        object : Gen<HK<F, A>> {
            override fun generate(): HK<F, A> =
                    AP.pure(valueGen.generate())
        }

fun <A, B> genFunctionAToB(genB: Gen<B>): Gen<(A) -> B> =
        object : Gen<(A) -> B> {
            override fun generate(): (A) -> B {
                val v = genB.generate()
                return { _ -> v }
            }
        }

fun genThrowable(): Gen<Throwable> = object : Gen<Throwable> {
    override fun generate(): Throwable =
            Gen.oneOf(listOf(RuntimeException(), NoSuchElementException(), IllegalArgumentException())).generate()
}

inline fun <F, A> genConstructor(valueGen: Gen<A>, crossinline cf: (A) -> HK<F, A>): Gen<HK<F, A>> =
        object : Gen<HK<F, A>> {
            override fun generate(): HK<F, A> =
                    cf(valueGen.generate())
        }

fun genIntSmall(): Gen<Int> =
        Gen.oneOf(Gen.negativeIntegers(), Gen.choose(0, Int.MAX_VALUE / 10000))

fun genIntPredicate(): Gen<(Int) -> Boolean> =
        Gen.int().let { gen ->
            /* If you ever see two 0s in a row please contact the maintainers for a pat in the back */
            val num = gen.generate().let { if (it == 0) gen.generate() else it }
            val absNum = Math.abs(num)
            Gen.oneOf(listOf<(Int) -> Boolean>(
                    { it > num },
                    { it <= num },
                    { it % absNum == 0 },
                    { it % absNum == absNum - 1 })
            )
        }
