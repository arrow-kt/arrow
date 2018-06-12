package arrow.test.generators

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Applicative
import io.kotlintest.properties.Gen
import java.util.concurrent.TimeUnit

fun <F, A> genApplicative(valueGen: Gen<A>, AP: Applicative<F>): Gen<Kind<F, A>> =
  object : Gen<Kind<F, A>> {
    override fun constants(): Iterable<Kind<F, A>> = valueGen.constants().map { AP.just(it) }

    override fun random(): Sequence<Kind<F, A>> = valueGen.random().map { AP.just(it) }
  }

fun <A, B> genFunctionAToB(genB: Gen<B>): Gen<(A) -> B> =
  object : Gen<(A) -> B> {
    override fun constants(): Iterable<(A) -> B> = genB.map { b:B -> {_: A -> b} }.constants()

    override fun random(): Sequence<(A) -> B> = genB.map { b:B -> {_: A -> b} }.random()
  }

fun <A> genFunctionAAToA(genA: Gen<A>): Gen<(A, A) -> A> =
  object : Gen<(A, A) -> A> {
    override fun constants(): Iterable<(A, A) -> A> = genA.map { a:A -> {_: A, _: A -> a} }.constants()

    override fun random(): Sequence<(A, A) -> A> = genA.map { a:A -> {_: A, _: A -> a} }.random()
  }

fun genThrowable(): Gen<Throwable> = Gen.from(listOf(RuntimeException(), NoSuchElementException(), IllegalArgumentException()))

fun <F, A> genConstructor(valueGen: Gen<A>, cf: (A) -> Kind<F, A>): Gen<Kind<F, A>> = valueGen.map(cf)

fun <F, A, B> genConstructor2(valueGen: Gen<A>, ff: (A) -> Kind<F, (A) -> B>): Gen<Kind<F, (A) -> B>> = valueGen.map(ff)

fun genIntSmall(): Gen<Int> =
  Gen.oneOf(Gen.negativeIntegers(), Gen.choose(0, Int.MAX_VALUE / 10000))

fun <A, B> genTuple(genA: Gen<A>, genB: Gen<B>): Gen<Tuple2<A, B>> = Gen.bind(genA,genB){a:A, b:B -> Tuple2(a, b)}

fun <A, B, C> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>): Gen<Tuple3<A, B, C>> =
  Gen.bind(genA,genB, genC){a:A, b:B, c:C -> Tuple3(a, b, c)

fun <A, B, C, D> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>): Gen<Tuple4<A, B, C, D>> =
  Gen.bind(genA,genB, genC, genD){a:A, b:B, c:C, d:D -> Tuple4(a, b, c, d)

fun <A, B, C, D, E> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>): Gen<Tuple5<A, B, C, D, E>> =
  Gen.bind(genA,genB, genC, genD, genE){a:A, b:B, c:C, d:D, e:E -> Tuple5(a, b, c, d, e)

fun <A, B, C, D, E, F> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>): Gen<Tuple6<A, B, C, D, E, F>> =
  Gen.bind(genA,genB, genC, genD, genE, genF){a:A, b:B, c:C, d:D, e:E, f:F -> Tuple6(a, b, c, d, e, f)

fun <A, B, C, D, E, F, G> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>): Gen<Tuple7<A, B, C, D, E, F, G>> =
  Gen.bind(genA,genB, genC, genD, genE, genF, genG){a:A, b:B, c:C, d:D, e:E, f:F, g:G -> Tuple7(a, b, c, d, e, f, g)

fun <A, B, C, D, E, F, G, H> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>): Gen<Tuple8<A, B, C, D, E, F, G, H>> =
  Gen.bind(genTuple(genA,genB, genC, genD, genE, genF, genG), genH){ tuple: Tuple7<A, B, C, D, E, F, G>, h:H -> Tuple8(tuple.a, tuple.b, tuple.c, tuple.d, tuple.e, tuple.f, tuple.g, h)}

fun <A, B, C, D, E, F, G, H, I> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>, genI: Gen<I>): Gen<Tuple9<A, B, C, D, E, F, G, H, I>> =
  Gen.bind(genTuple(genA,genB, genC, genD, genE, genF, genG, genH), genI){ tuple: Tuple8<A, B, C, D, E, F, G, H>, i:I -> Tuple9(tuple.a, tuple.b, tuple.c, tuple.d, tuple.e, tuple.f, tuple.g, tuple.h, i)

fun <A, B, C, D, E, F, G, H, I, J> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>, genI: Gen<I>, genJ: Gen<J>): Gen<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
  Gen.bind(genTuple(genA,genB, genC, genD, genE, genF, genG, genH, genI), genJ){tuple: Tuple9<A, B, C, D, E, F, G, H, I>, j:J -> Tuple10(tuple.a, tuple.b, tuple.c, tuple.d, tuple.e, tuple.f, tuple.g, tuple.h, tuple.i, j)


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
    override fun constants(): Iterable<Option<B>> {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun random(): Sequence<Option<B>> {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val random = genIntSmall()
    override fun generate(): Option<B> =
      if (random.generate() % 20 == 0) None else Option.just(genB.generate())
  }

inline fun <reified E, reified A> genEither(genE: Gen<E>, genA: Gen<A>): Gen<Either<E, A>> =
  object : Gen<Either<E, A>> {
    override fun constants(): Iterable<Either<E, A>> {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun random(): Sequence<Either<E, A>> {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

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

fun <K, V> genMapK(genK: Gen<K>, genV: Gen<V>): Gen<MapK<K, V>> =
  Gen.create { Gen.list(genK).generate().map { it to genV.generate() }.toMap().k() }

fun genTimeUnit(): Gen<TimeUnit> = object : Gen<TimeUnit> {
  override fun constants(): Iterable<TimeUnit> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun random(): Sequence<TimeUnit> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  val units = TimeUnit.values()
  val random = Gen.choose(0, units.size - 1)
  override fun generate(): TimeUnit = units[random.generate()]
}

fun <A> genListK(genA: Gen<A>): Gen<ListK<A>> =
  Gen.create { Gen.list(genA).generate().k() }

fun <A> genSequenceK(genA: Gen<A>): Gen<SequenceK<A>> =
  Gen.create { Gen.list(genA).generate().asSequence().k() }

fun genChars(): Gen<Char> =
  Gen.oneOf(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())

fun <A> genSetK(genA: Gen<A>): Gen<SetK<A>> {
  val genSetA = Gen.set(genA)
  return Gen.create { genSetA.generate().k() }
}

// For generating recursive data structures with recursion schemes

typealias NatPattern = ForOption
typealias GNat<T> = Kind<T, NatPattern>

fun toGNatCoalgebra() = Coalgebra<NatPattern, Int> {
  if (it == 0) None else Some(it - 1)
}

fun fromGNatAlgebra() = Algebra<NatPattern, Eval<Int>> {
  it.fix().fold({ Eval.Zero }, { it.map { it + 1 } })
}

inline fun <reified T> Int.toGNat(CT: Corecursive<T>): GNat<T> = CT.run {
  ana(Option.functor(), toGNatCoalgebra())
}

inline fun <reified T> GNat<T>.toInt(RT: Recursive<T>): Int = RT.run {
  cata(Option.functor(), fromGNatAlgebra())
}
