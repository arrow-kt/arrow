package arrow.test.generators

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.Applicative
import io.kotlintest.properties.Gen
import io.kotlintest.properties.map
import java.util.concurrent.TimeUnit

fun <F, A> genApplicative(valueGen: Gen<A>, AP: Applicative<F>): Gen<Kind<F, A>> = create(
        valueGen.random().map { AP.just(it) }
)

fun <A, B> genFunctionAToB(genB: Gen<B>): Gen<(A) -> B> = create(
        genB.random().map { { _: A -> it} }
)

fun <A> genFunctionAAToA(genA: Gen<A>): Gen<(A, A) -> A> = create(
        genA.random().map { { _: A, _: A -> it} }
)

fun genThrowable(): Gen<Throwable> = object : Gen<Throwable> {

    override fun always() = listOf(RuntimeException(), NoSuchElementException(), IllegalArgumentException())

    override fun random(): Sequence<Throwable> {
        return Gen.from(always()).random()
    }

}

fun <F, A> genConstructor(valueGen: Gen<A>, cf: (A) -> Kind<F, A>): Gen<Kind<F, A>> = create(
        valueGen.random().map(cf)
)

fun <F, A, B> genConstructor2(valueGen: Gen<A>, ff: (A) -> Kind<F, (A) -> B>) = create(
       valueGen.random().map(ff)
)

fun genIntSmall(): Gen<Int> = Gen.choose(Integer.MIN_VALUE, Int.MAX_VALUE / 10000)

fun <A, B> genTuple(genA: Gen<A>, genB: Gen<B>): Gen<Tuple2<A, B>> = Gen.create {
    Tuple2(genA.generate(), genB.generate())
}

fun <A, B, C> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>): Gen<Tuple3<A, B, C>> = Gen.create {
    Tuple3(genA.generate(), genB.generate(), genC.generate())
}

fun <A, B, C, D> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>): Gen<Tuple4<A, B, C, D>> = Gen.create {
    Tuple4(genA.generate(), genB.generate(), genC.generate(), genD.generate())
}

fun <A, B, C, D, E> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>): Gen<Tuple5<A, B, C, D, E>> = Gen.create {
    Tuple5(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate())
}

fun <A, B, C, D, E, F> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>): Gen<Tuple6<A, B, C, D, E, F>> = Gen.create {
    Tuple6(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate())
}

fun <A, B, C, D, E, F, G> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>): Gen<Tuple7<A, B, C, D, E, F, G>> = Gen.create {
    Tuple7(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate(), genG.generate())
}

fun <A, B, C, D, E, F, G, H> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>): Gen<Tuple8<A, B, C, D, E, F, G, H>> = Gen.create {
    Tuple8(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate(), genG.generate(), genH.generate())
}

fun <A, B, C, D, E, F, G, H, I> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>, genI: Gen<I>): Gen<Tuple9<A, B, C, D, E, F, G, H, I>> = Gen.create {
    Tuple9(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate(), genG.generate(), genH.generate(), genI.generate())
}

fun <A, B, C, D, E, F, G, H, I, J> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>, genI: Gen<I>, genJ: Gen<J>): Gen<Tuple10<A, B, C, D, E, F, G, H, I, J>> = Gen.create {
    Tuple10(genA.generate(), genB.generate(), genC.generate(), genD.generate(), genE.generate(), genF.generate(), genG.generate(), genH.generate(), genI.generate(), genJ.generate())
}

fun genIntPredicate(): Gen<(Int) -> Boolean> =
  Gen.int().let { gen ->
    /* If you ever see two zeros in a row please contact the maintainers for a pat in the back */
    val num = gen.generate().let { if (it == 0) gen.generate() else it }
    val absNum = Math.abs(num)
    Gen.from(listOf<(Int) -> Boolean>(
      { it > num },
      { it <= num },
      { it % absNum == 0 },
      { it % absNum == absNum - 1 })
    )
  }

fun <B> genOption(genB: Gen<B>): Gen<Option<B>> {
    val random = genIntSmall()
    return Gen.create {
        if (random.generate() % 20 == 0) None else Option.just(genB.generate())
    }
}

inline fun <reified E: Any, reified A: Any> genEither(genE: Gen<E>, genA: Gen<A>): Gen<Either<E, A>> = Gen.create {
    oneOf(genE, genA).random().map {
        when (it) {
            is E -> Left(it)
            is A -> Right(it)
            else -> throw IllegalStateException("genEither incorrect value $it")
        }
    }.first()
}

inline fun <reified E : Any, reified A : Any> genValidated(genE: Gen<E>, genA: Gen<A>): Gen<Validated<E, A>> =
  Gen.create { Validated.fromEither(genEither(genE, genA).generate()) }

inline fun <reified A : Any> genTry(genA: Gen<A>, genThrowable: Gen<Throwable> = genThrowable()): Gen<Try<A>> = Gen.create {
  genEither(genThrowable, genA).generate().fold(
    { throwable -> Failure<A>(throwable) },
    { a -> Success(a) }
  )
}

fun <A : Any> genNullable(genA: Gen<A>): Gen<A?> = object: Gen<A?> {
    override fun always(): Iterable<A?> = emptyList()

    override fun random(): Sequence<A?> = oneOf(genA, Gen.create { Option.empty<A>() }).random().map {
        if (it is Option<*>) {
            null
        } else {
            it as A
        }
    }

}


fun <A : Any> genNonEmptyList(genA: Gen<A>): Gen<NonEmptyList<A>> =
  Gen.create { NonEmptyList(genA.generate(), Gen.list(genA).generate()) }

fun <K : Any, V> genMap(genK: Gen<K>, genV: Gen<V>): Gen<Map<K, V>> =
  Gen.create { Gen.list(genK).generate().map { it to genV.generate() }.toMap() }

fun <K : Any, V> genMapK(genK: Gen<K>, genV: Gen<V>): Gen<MapK<K, V>> =
  Gen.create { Gen.list(genK).generate().map { it to genV.generate() }.toMap().k() }

fun genTimeUnit(): Gen<TimeUnit> {
    val units = TimeUnit.values()
    val random = Gen.choose(0, units.size - 1)

    return Gen.create {
        units[random.generate()]
    }
}

fun <A : Any> genListK(genA: Gen<A>): Gen<ListK<A>> =
  Gen.list(genA).map { it.k() }

fun <A : Any> genSequenceK(genA: Gen<A>): Gen<SequenceK<A>> =
  Gen.create { Gen.list(genA).generate().asSequence().k() }

fun genChars(): Gen<Char> =
  Gen.from(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())

fun <A> genSetK(genA: Gen<A>): Gen<SetK<A>> where A: Any {
  val genSetA = Gen.set(genA)
  return Gen.create { genSetA.generate().k() }
}

fun <A> Gen<A>.generate(): A = this.random().first()

fun <T : Any> oneOf(vararg generators: Gen<T>): Gen<T> = Gen.create {
    val list = generators.map { it.generate() }
    //FIXME
    Gen.from(list).generate()
}

fun <T : Any> create(source: Sequence<T>): Gen<T> = object : Gen<T> {
    override fun always(): Iterable<T> = emptyList()
    override fun random(): Sequence<T> = source
}

