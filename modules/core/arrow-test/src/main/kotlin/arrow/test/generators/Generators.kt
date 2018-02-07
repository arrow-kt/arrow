package arrow.test.generators

import arrow.typeclasses.Applicative
import arrow.HK
import arrow.typeclasses.applicative
import arrow.core.*
import arrow.data.*
import io.kotlintest.properties.Gen
import java.util.concurrent.TimeUnit

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

fun <A, B, C> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>): Gen<Tuple3<A, B, C>> =
        object : Gen<Tuple3<A, B, C>> {
            override fun generate(): Tuple3<A, B, C> = Tuple3(genA.generate(), genB.generate(), genC.generate())
        }

fun <A, B, C, D> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>): Gen<Tuple4<A, B, C, D>> =
        object : Gen<Tuple4<A, B, C, D>> {
            override fun generate(): Tuple4<A, B, C, D> = Tuple4(genA.generate(), genB.generate(), genC.generate(), genD.generate())
        }

fun <A, B, C, D, E> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>): Gen<Tuple5<A, B, C, D, E>> =
        object : Gen<Tuple5<A, B, C, D, E>> {
            override fun generate(): Tuple5<A, B, C, D, E> = Tuple5(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate())
        }

fun <A, B, C, D, E, F> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>): Gen<Tuple6<A, B, C, D, E, F>> =
        object : Gen<Tuple6<A, B, C, D, E, F>> {
            override fun generate(): Tuple6<A, B, C, D, E, F> = Tuple6(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate())
        }

fun <A, B, C, D, E, F, G> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>): Gen<Tuple7<A, B, C, D, E, F, G>> =
        object : Gen<Tuple7<A, B, C, D, E, F, G>> {
            override fun generate(): Tuple7<A, B, C, D, E, F, G> = Tuple7(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate(), genG.generate())
        }

fun <A, B, C, D, E, F, G, H> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>): Gen<Tuple8<A, B, C, D, E, F, G, H>> =
        object : Gen<Tuple8<A, B, C, D, E, F, G, H>> {
            override fun generate(): Tuple8<A, B, C, D, E, F, G, H> = Tuple8(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate(), genG.generate(), genH.generate())
        }

fun <A, B, C, D, E, F, G, H, I> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>, genI: Gen<I>): Gen<Tuple9<A, B, C, D, E, F, G, H, I>> =
        object : Gen<Tuple9<A, B, C, D, E, F, G, H, I>> {
            override fun generate(): Tuple9<A, B, C, D, E, F, G, H, I> = Tuple9(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate(), genG.generate(), genH.generate(), genI.generate())
        }

fun <A, B, C, D, E, F, G, H, I, J> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>, genI: Gen<I>, genJ: Gen<J>): Gen<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
        object : Gen<Tuple10<A, B, C, D, E, F, G, H, I, J>> {
            override fun generate(): Tuple10<A, B, C, D, E, F, G, H, I, J> = Tuple10(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate(), genG.generate(), genH.generate(), genI.generate(), genJ.generate())
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
                    if (random.generate() % 20 == 0) None else Option.pure(genB.generate())
        }

inline fun <reified E, reified A> genEither(genE: Gen<E>, genA: Gen<A>): Gen<Either<E, A>> =
        object : Gen<Either<E, A>> {
            override fun generate(): Either<E, A> =
                    Gen.oneOf(genE, genA).generate().let {
                        when (it) {
                            is E -> Left(it)
                            is A -> Right(it)
                            else -> throw IllegalStateException("genEither incorrect value $it")
                        }
                    }
        }

inline fun <reified E, reified A> genValidated(genE: Gen<E>, genA: Gen<A>): Gen<Validated<E, A>> =
        Gen.create { Validated.fromEither(genEither(genE, genA).generate()) }

inline fun <reified A> genTry(genA: Gen<A>, genThrowable: Gen<Throwable> = genThrowable()): Gen<Try<A>> = Gen.create {
    genEither(genThrowable, genA).generate().fold(
            { throwable -> Failure<A>(throwable) },
            { a -> Success(a) }
    )
}

fun <A> genNullable(genA: Gen<A>): Gen<A?> =
        Gen.oneOf(genA, Gen.create { null })

fun <A> genNonEmptyList(genA: Gen<A>): Gen<NonEmptyList<A>> =
        Gen.create { NonEmptyList(genA.generate(), Gen.list(genA).generate()) }

fun <K, V> genMap(genK: Gen<K>, genV: Gen<V>): Gen<Map<K, V>> =
        Gen.create { Gen.list(genK).generate().map { it to genV.generate() }.toMap() }

fun <K, V> genMapKW(genK: Gen<K>, genV: Gen<V>): Gen<MapKW<K, V>> =
        Gen.create { Gen.list(genK).generate().map { it to genV.generate() }.toMap().k() }

fun genTimeUnit(): Gen<TimeUnit> = object : Gen<TimeUnit> {
    val units = TimeUnit.values()
    val random = Gen.choose(0, units.size - 1)
    override fun generate(): TimeUnit = units[random.generate()]
}

fun <A> genListKW(genA: Gen<A>): Gen<ListKW<A>> =
        Gen.create { Gen.list(genA).generate().k() }

fun <A> genSequenceKW(genA: Gen<A>): Gen<SequenceKW<A>> =
        Gen.create { Gen.list(genA).generate().asSequence().k() }

fun genChars(): Gen<Char> =
        Gen.oneOf(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())

fun <A> genSetKW(genA: Gen<A>): Gen<SetKW<A>> {
    val genSetA = Gen.set(genA)
    return Gen.create { genSetA.generate().k() }
}
