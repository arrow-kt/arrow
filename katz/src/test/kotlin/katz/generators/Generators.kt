package katz

import io.kotlintest.properties.Gen
import katz.HK
import katz.Option

fun <A> genOptionHK(valueGen: Gen<A>): Gen<HK<Option.F, A>> = object : Gen<HK<Option.F, A>> {
    override fun generate(): HK<Option.F, A> = Option(valueGen.generate())
}

fun <A, B> genFunctionAToB(genB: Gen<B>): Gen<(A) -> B> = object : Gen<(A) -> B> {
    override fun generate(): (A) -> B  {
        val v = genB.generate()
        return { a -> v }
    }
}