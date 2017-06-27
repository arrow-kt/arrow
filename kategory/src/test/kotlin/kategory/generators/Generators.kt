package kategory

import io.kotlintest.properties.Gen

inline fun <reified F, A> genApplicative(valueGen: Gen<A>, AP: Applicative<F> = applicative<F>()): Gen<HK<F, A>> = object : Gen<HK<F, A>> {
    override fun generate(): HK<F, A> = AP.pure(valueGen.generate())
}

fun <A, B> genFunctionAToB(genB: Gen<B>): Gen<(A) -> B> = object : Gen<(A) -> B> {
    override fun generate(): (A) -> B {
        val v = genB.generate()
        return { a -> v }
    }
}

fun genThrowable(): Gen<Throwable> = object : Gen<Throwable> {
    override fun generate(): Throwable =
            Gen.oneOf(listOf(RuntimeException(), NoSuchElementException(), IllegalArgumentException())).generate()
}