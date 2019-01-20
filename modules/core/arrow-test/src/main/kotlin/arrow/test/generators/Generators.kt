package arrow.test.generators

import arrow.Kind
import arrow.Kind2
import arrow.core.*
import arrow.core.extensions.option.functor.map
import arrow.core.extensions.option.functor.functor
import arrow.data.*
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.typeclasses.Corecursive
import arrow.recursion.typeclasses.Recursive
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import io.kotlintest.properties.Gen
import java.util.concurrent.TimeUnit

fun <F, A> genApplicative(valueGen: Gen<A>, AP: Applicative<F>): Gen<Kind<F, A>> =
   valueGen.map { AP.just(it) }

fun <F, A, E> genApplicativeError(valueGen: Gen<A>, errorGen: Gen<E>, AP: ApplicativeError<F, E>): Gen<Kind<F, A>> =
  Gen.oneOf<Either<E, A>>(valueGen.map(::Right), errorGen.map(::Left)).map {
    it.fold(AP::raiseError, AP::just)
  }

fun <F, A> genApplicativeError(valueGen: Gen<A>, AP: ApplicativeError<F, Throwable>): Gen<Kind<F, A>> =
  genApplicativeError(valueGen, genThrowable(), AP)

fun <A, B> genFunctionAToB(genB: Gen<B>): Gen<(A) -> B> = genB.map { b:B -> { _: A -> b } }

fun <A> genFunctionAAToA(genA: Gen<A>): Gen<(A, A) -> A> = genA.map { a:A -> { _: A, _: A -> a } }

fun genThrowable(): Gen<Throwable> = Gen.from(listOf(RuntimeException(), NoSuchElementException(), IllegalArgumentException()))

fun <F, A> genConstructor(valueGen: Gen<A>, cf: (A) -> Kind<F, A>): Gen<Kind<F, A>> = valueGen.map(cf)

fun <F, A> genDoubleConstructor(valueGen: Gen<A>, cf: (A) -> Kind2<F, A, A>): Gen<Kind2<F, A, A>> =
  valueGen.map(cf)

fun <F, A, B> genConstructor2(valueGen: Gen<A>, ff: (A) -> Kind<F, (A) -> B>): Gen<Kind<F, (A) -> B>> = valueGen.map(ff)

fun genIntSmall(): Gen<Int> = Gen.oneOf(Gen.choose(Int.MIN_VALUE / 10000, -1), Gen.choose(0, Int.MAX_VALUE / 10000))

fun <A, B> genTuple(genA: Gen<A>, genB: Gen<B>): Gen<Tuple2<A, B>> = Gen.bind(genA,genB){ a:A, b:B -> Tuple2(a, b) }

fun <A, B, C> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>): Gen<Tuple3<A, B, C>> =
  Gen.bind(genA,genB, genC){a:A, b:B, c:C -> Tuple3(a, b, c)}

fun <A, B, C, D> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>): Gen<Tuple4<A, B, C, D>> =
  Gen.bind(genA,genB, genC, genD){a:A, b:B, c:C, d:D -> Tuple4(a, b, c, d)}

fun <A, B, C, D, E> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>): Gen<Tuple5<A, B, C, D, E>> =
  Gen.bind(genA,genB, genC, genD, genE){a:A, b:B, c:C, d:D, e:E -> Tuple5(a, b, c, d, e)}

fun <A, B, C, D, E, F> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>): Gen<Tuple6<A, B, C, D, E, F>> =
  Gen.bind(genA,genB, genC, genD, genE, genF){a:A, b:B, c:C, d:D, e:E, f:F -> Tuple6(a, b, c, d, e, f)}

fun <A, B, C, D, E, F, G> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>): Gen<Tuple7<A, B, C, D, E, F, G>> =
  Gen.bind(genA,genB, genC, genD, genE, genF, genG){a:A, b:B, c:C, d:D, e:E, f:F, g:G -> Tuple7(a, b, c, d, e, f, g) }

fun <A, B, C, D, E, F, G, H> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>): Gen<Tuple8<A, B, C, D, E, F, G, H>> =
  Gen.bind(genTuple(genA,genB, genC, genD, genE, genF, genG), genH){ tuple: Tuple7<A, B, C, D, E, F, G>, h:H -> Tuple8(tuple.a, tuple.b, tuple.c, tuple.d, tuple.e, tuple.f, tuple.g, h)}

fun <A, B, C, D, E, F, G, H, I> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>, genI: Gen<I>): Gen<Tuple9<A, B, C, D, E, F, G, H, I>> =
  Gen.bind(genTuple(genA,genB, genC, genD, genE, genF, genG, genH), genI){ tuple: Tuple8<A, B, C, D, E, F, G, H>, i:I -> Tuple9(tuple.a, tuple.b, tuple.c, tuple.d, tuple.e, tuple.f, tuple.g, tuple.h, i)}

fun <A, B, C, D, E, F, G, H, I, J> genTuple(genA: Gen<A>, genB: Gen<B>, genC: Gen<C>, genD: Gen<D>, genE: Gen<E>, genF: Gen<F>, genG: Gen<G>, genH: Gen<H>, genI: Gen<I>, genJ: Gen<J>): Gen<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
  Gen.bind(genTuple(genA,genB, genC, genD, genE, genF, genG, genH, genI), genJ){tuple: Tuple9<A, B, C, D, E, F, G, H, I>, j:J -> Tuple10(tuple.a, tuple.b, tuple.c, tuple.d, tuple.e, tuple.f, tuple.g, tuple.h, tuple.i, j)}

fun genNonZeroInt(): Gen<Int> = Gen.int().filter { it != 0 }

fun genLessThan(max: Int) : Gen<Int> = Gen.int().filter { it < max }

fun genLessEqual(max: Int) : Gen<Int> = Gen.int().filter { it <= max }

fun genGreaterThan(min: Int): Gen<Int> = Gen.int().filter { it > min }

fun genGreaterEqual(min: Int) : Gen<Int> = Gen.int().filter { it >= min }

fun genGreaterOrEqThan(max: Int) : Gen<Int> = Gen.int().filter { it >= max }

fun genIntPredicate(): Gen<(Int) -> Boolean> =
  genNonZeroInt().flatMap { num ->
    val absNum = Math.abs(num)
    Gen.from(listOf<(Int) -> Boolean>(
      { it > num },
      { it <= num },
      { it % absNum == 0 },
      { it % absNum == absNum - 1 })
    )
  }

fun <B> genOption(genB: Gen<B>): Gen<Option<B>> =
  genB.orNull().map { it.toOption() }

fun <E, A> genEither(genE: Gen<E>, genA: Gen<A>): Gen<Either<E, A>>  {
  val genLeft = genE.map<Either<E, A>> { Left(it) }
  val genRight = genA.map<Either<E, A>> { Right(it) }
  return Gen.oneOf(genLeft,genRight)
}

fun <E, A> genValidated(genE: Gen<E>, genA: Gen<A>): Gen<Validated<E, A>> =
  genEither(genE, genA).map { Validated.fromEither(it) }

fun <A> genTry(genA: Gen<A>, genThrowable: Gen<Throwable> = genThrowable()): Gen<Try<A>> =
  genEither(genThrowable, genA).map{ it.fold({ Failure(it) }, { Success(it) }) }

fun <A> genNonEmptyList(genA: Gen<A>): Gen<NonEmptyList<A>> =
  genA.flatMap { head -> Gen.list(genA).map { NonEmptyList(head, it) } }

fun <K: Comparable<K>, V> genSortedMapK(genK: Gen<K>, genV: Gen<V>): Gen<SortedMapK<K, V>> =
  Gen.bind(genK,genV) { k:K , v:V -> sortedMapOf(k to v) }.map { it.k() }

fun <K, V> genMapK(genK: Gen<K>, genV: Gen<V>): Gen<MapK<K, V>> =
  Gen.map(genK,genV).map { it.k() }

fun genTimeUnit(): Gen<TimeUnit> = Gen.from(TimeUnit.values())

fun <A> genListK(genA: Gen<A>): Gen<ListK<A>> = Gen.list(genA).map{ it.k() }

fun <A> genSequenceK(genA: Gen<A>): Gen<SequenceK<A>> = Gen.list(genA).map{ it.asSequence().k() }

fun genNonEmptyString(): Gen<String> = Gen.string().filter{it.isNotEmpty()}

fun genChar(): Gen<Char> =
  Gen.from(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())

fun <A> genSetK(genA: Gen<A>): Gen<SetK<A>> = Gen.set(genA).map{ it.k() }

// For generating recursive data structures with recursion schemes

typealias NatPattern = ForOption
typealias GNat<T> = Kind<T, NatPattern>

fun toGNatCoalgebra(): Coalgebra<NatPattern, Int> = Coalgebra{
  if (it == 0) None else Some(it - 1)
}

fun fromGNatAlgebra(): Algebra<NatPattern, Eval<Int>> = Algebra{
  it.fix().fold({ Eval.Zero }, { it.map { it + 1 } })
}

inline fun <reified T> Corecursive<T>.toGNat(i: Int): GNat<T> =
  Option.functor().ana(i, toGNatCoalgebra())

inline fun <reified T> Recursive<T>.toInt(i: GNat<T>): Int =
  Option.functor().cata(i, fromGNatAlgebra())
