package kategory

import io.kotlintest.properties.Gen

fun <F> genEqAnyLogged() = object : Eq<F> {
    val any = Eq.any()

    override fun eqv(a: F, b: F): Boolean {
        val result = any.eqv(a, b)
        if (!result) {
            println("$a <---> $b")
        }
        return result
    }
}

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

fun <A> genFunctionAAToA(genA: Gen<A>): Gen<(A, A) -> A> =
        object : Gen<(A, A) -> A> {
            override fun generate(): (A, A) -> A {
                val v = genA.generate()
                return { _, _ -> v }
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

inline fun <F, A, B> genConstructor2(valueGen: Gen<A>, crossinline ff: (A) -> HK<F, (A) -> B>): Gen<HK<F, (A) -> B>> =
        object : Gen<HK<F, (A) -> B>> {
            override fun generate(): HK<F, (A) -> B> =
                    ff(valueGen.generate())
        }

fun genIntSmall(): Gen<Int> =
        Gen.oneOf(Gen.negativeIntegers(), Gen.choose(0, Int.MAX_VALUE / 10000))

fun <A, B> genTuple(genA: Gen<A>, genB: Gen<B>): Gen<Tuple2<A, B>> =
        object : Gen<Tuple2<A, B>> {
            override fun generate(): Tuple2<A, B> = Tuple2(genA.generate(), genB.generate())
        }

fun genIntPredicate(): Gen<(Int) -> Boolean> =
        Gen.int().let { gen ->
            /* If you ever see two zeros in a row please contact the maintainers for a pat in the back */
            val num = gen.generate().let { if (it == 0) gen.generate() else it }
            val absNum = Math.abs(num)
            Gen.oneOf(listOf<(Int) -> Boolean>(
                    { it > num },
                    { it <= num },
                    { it % absNum == 0 },
                    { it % absNum == absNum - 1 })
            )
        }

fun <B> genOption(genB: Gen<B>): Gen<Option<B>> =
        object : Gen<Option<B>> {
            val random = genIntSmall()
            override fun generate(): Option<B> =
                    if (random.generate() % 20 == 0) Option.None else Option.pure(genB.generate())
        }

inline fun <reified E, reified A> genEither(genE: Gen<E>, genA: Gen<A>): Gen<Either<E, A>> =
        object : Gen<Either<E, A>> {
            override fun generate(): Either<E, A> =
                    Gen.oneOf(genE, genA).generate().let {
                        when (it) {
                            is E -> Either.Left(it)
                            is A -> Either.Right(it)
                            else -> throw IllegalStateException("genEither incorrect value $it")
                        }
                    }
        }
