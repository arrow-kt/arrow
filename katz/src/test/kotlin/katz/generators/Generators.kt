package katz

import io.kotlintest.properties.Gen

inline fun <reified F, reified A> genMonad(valueGen: Gen<A> = Gen.default(), M : Monad<F> = monad<F>()): Gen<HK<F, A>> = object : Gen<HK<F, A>> {
    override fun generate(): HK<F, A> = M.pure(valueGen.generate())
}

fun <A, B> genFunctionAToB(genB: Gen<B>): Gen<(A) -> B> = object : Gen<(A) -> B> {
    override fun generate(): (A) -> B  {
        val v = genB.generate()
        return { a -> v }
    }
}