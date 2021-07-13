package arrow.core.test.generators

import arrow.core.Const
import arrow.core.Either
import arrow.core.Endo
import arrow.core.Eval
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion.fromListUnsafe
import arrow.core.Option
import arrow.core.Tuple10
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.Validated
import arrow.core.left
import arrow.core.prependTo
import arrow.core.right
import arrow.core.toOption
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.numericDoubles
import io.kotest.property.arbitrary.numericFloats
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import kotlin.jvm.JvmOverloads
import kotlin.math.abs
import kotlin.random.nextInt

public fun <A, B> Arb.Companion.functionAToB(arb: Arb<B>): Arb<(A) -> B> =
  arb.map { b: B -> { _: A -> b } }

public fun <A> Arb.Companion.functionAAToA(arb: Arb<A>): Arb<(A, A) -> A> =
  arb.map { a: A -> { _: A, _: A -> a } }

public fun <A, B> Arb.Companion.functionBAToB(arb: Arb<B>): Arb<(B, A) -> B> =
  arb.map { b: B -> { _: B, _: A -> b } }

public fun <A, B> Arb.Companion.functionABToB(arb: Arb<B>): Arb<(A, B) -> B> =
  arb.map { b: B -> { _: A, _: B -> b } }

public fun <A> Arb.Companion.functionToA(arb: Arb<A>): Arb<() -> A> =
  arb.map { a: A -> { a } }

public fun Arb.Companion.throwable(): Arb<Throwable> =
  Arb.of(listOf(RuntimeException(), NoSuchElementException(), IllegalArgumentException()))

public fun Arb.Companion.doubleSmall(): Arb<Double> =
  Arb.numericDoubles(from = 0.0, to = 100.0)

public fun Arb.Companion.floatSmall(): Arb<Float> =
  Arb.numericFloats(from = 0F, to = 100F)

public fun Arb.Companion.intSmall(factor: Int = 10000): Arb<Int> =
  Arb.int((Int.MIN_VALUE / factor)..(Int.MAX_VALUE / factor))

public fun Arb.Companion.byteSmall(): Arb<Byte> =
  Arb.byte(min = (Byte.MIN_VALUE / 10).toByte(), max = (Byte.MAX_VALUE / 10).toByte())

public fun Arb.Companion.shortSmall(): Arb<Short> {
  val range = (Short.MIN_VALUE / 1000)..(Short.MAX_VALUE / 1000)
  return Arb.short().filter { it in range }
}

public fun Arb.Companion.longSmall(): Arb<Long> =
  Arb.long((Long.MIN_VALUE / 100000L)..(Long.MAX_VALUE / 100000L))

public fun <A, B, C, D> Arb.Companion.tuple4(arbA: Arb<A>, arbB: Arb<B>, arbC: Arb<C>, arbD: Arb<D>): Arb<Tuple4<A, B, C, D>> =
  Arb.bind(arbA, arbB, arbC, arbD, ::Tuple4)

public fun <A, B, C, D, E> Arb.Companion.tuple5(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>
): Arb<Tuple5<A, B, C, D, E>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, ::Tuple5)

public fun <A, B, C, D, E, F> Arb.Companion.tuple6(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>
): Arb<Tuple6<A, B, C, D, E, F>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, ::Tuple6)

public fun <A, B, C, D, E, F, G> Arb.Companion.tuple7(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>
): Arb<Tuple7<A, B, C, D, E, F, G>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, ::Tuple7)

public fun <A, B, C, D, E, F, G, H> Arb.Companion.tuple8(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>,
  arbH: Arb<H>
): Arb<Tuple8<A, B, C, D, E, F, G, H>> =
  Arb.bind(
    Arb.tuple7(arbA, arbB, arbC, arbD, arbE, arbF, arbG),
    arbH
  ) { (a, b, c, d, e, f, g), h ->
    Tuple8(a, b, c, d, e, f, g, h)
  }

public fun <A, B, C, D, E, F, G, H, I> Arb.Companion.tuple9(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>,
  arbH: Arb<H>,
  arbI: Arb<I>
): Arb<Tuple9<A, B, C, D, E, F, G, H, I>> =
  Arb.bind(
    Arb.tuple8(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH),
    arbI
  ) { (a, b, c, d, e, f, g, h), i ->
    Tuple9(a, b, c, d, e, f, g, h, i)
  }

public fun <A, B, C, D, E, F, G, H, I, J> Arb.Companion.tuple10(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>,
  arbH: Arb<H>,
  arbI: Arb<I>,
  arbJ: Arb<J>
): Arb<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
  Arb.bind(
    Arb.tuple9(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, arbI),
    arbJ
  ) { (a, b, c, d, e, f, g, h, i), j ->
    Tuple10(a, b, c, d, e, f, g, h, i, j)
  }

public fun Arb.Companion.nonZeroInt(): Arb<Int> = Arb.int().filter { it != 0 }

public fun Arb.Companion.intPredicate(): Arb<(Int) -> Boolean> =
  Arb.nonZeroInt().flatMap { num ->
    val absNum = abs(num)
    Arb.of(
      listOf<(Int) -> Boolean>(
        { it > num },
        { it <= num },
        { it % absNum == 0 },
        { it % absNum == absNum - 1 }
      )
    )
  }

public fun <A> Arb.Companion.endo(arb: Arb<A>): Arb<Endo<A>> = arb.map { a: A -> Endo<A> { a } }

public fun <B> Arb.Companion.option(arb: Arb<B>): Arb<Option<B>> =
  arb.orNull().map { it.toOption() }

public fun <E, A> Arb.Companion.either(arbE: Arb<E>, arbA: Arb<A>): Arb<Either<E, A>> {
  val arbLeft = arbE.map { Either.Left(it) }
  val arbRight = arbA.map { Either.Right(it) }
  return Arb.choice(arbLeft, arbRight)
}

public fun <E, A> Arb<E>.or(arbA: Arb<A>): Arb<Either<E, A>> = Arb.either(this, arbA)

public fun <E, A> Arb.Companion.validated(arbE: Arb<E>, arbA: Arb<A>): Arb<Validated<E, A>> =
  Arb.either(arbE, arbA).map { Validated.fromEither(it) }

public fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>): Arb<NonEmptyList<A>> =
  Arb.list(arb).filter(List<A>::isNotEmpty).map(::fromListUnsafe)

public fun <A> Arb.Companion.sequence(arbA: Arb<A>): Arb<Sequence<A>> =
  Arb.list(arbA).map { it.asSequence() }

public fun Arb.Companion.unit(): Arb<Unit> =
  Arb.constant(Unit)

public fun <A, B> Arb.Companion.ior(arbA: Arb<A>, arbB: Arb<B>): Arb<Ior<A, B>> =
  arbA.alignWith(arbB) { it }

public fun <A, B> Arb.Companion.arbConst(arb: Arb<A>): Arb<Const<A, B>> =
  arb.map { Const<A, B>(it) }

public fun <A> Arb<A>.eval(): Arb<Eval<A>> =
  map { Eval.now(it) }

private fun <A, B, R> Arb<A>.alignWith(arbB: Arb<B>, transform: (Ior<A, B>) -> R): Arb<R> =
  Arb.bind(this, arbB) { a, b -> transform(Ior.Both(a, b)) }

public fun Arb.Companion.suspendFunThatReturnsEitherAnyOrAnyOrThrows(): Arb<suspend () -> Either<Any, Any>> =
  choice(
    suspendFunThatReturnsAnyRight(),
    suspendFunThatReturnsAnyLeft(),
    suspendFunThatThrows()
  )

public fun Arb.Companion.suspendFunThatReturnsAnyRight(): Arb<suspend () -> Either<Any, Any>> =
  any().map { suspend { it.right() } }

public fun Arb.Companion.suspendFunThatReturnsAnyLeft(): Arb<suspend () -> Either<Any, Any>> =
  any().map { suspend { it.left() } }

public fun Arb.Companion.suspendFunThatThrows(): Arb<suspend () -> Either<Any, Any>> =
  throwable().map { suspend { throw it } } as Arb<suspend () -> Either<Any, Any>>

public fun Arb.Companion.any(): Arb<Any> =
  choice(
    Arb.string() as Arb<Any>,
    Arb.int() as Arb<Any>,
    Arb.long() as Arb<Any>,
//    Arb.float() as Arb<Any>,
//    Arb.double() as Arb<Any>,
    Arb.bool() as Arb<Any>,
    Arb.throwable() as Arb<Any>,
    Arb.unit() as Arb<Any>
  )

@JvmOverloads
public inline fun <reified A> Arb.Companion.array(
  gen: Arb<A>,
  range: IntRange = 0..100
): Arb<Array<A>> {
  check(!range.isEmpty())
  check(range.first >= 0)
  return arb(edgecases = emptyArray<A>() prependTo gen.edgecases().map { arrayOf(it) }) {
    sequence {
      val genIter = gen.generate(it).iterator()
      while (true) {
        val targetSize = it.random.nextInt(range)
        val list = ArrayList<A>(targetSize)
        while (list.size < targetSize && genIter.hasNext()) {
          list.add(genIter.next().value)
        }
        check(list.size == targetSize)
        yield(list.toTypedArray())
      }
    }
  }
}
