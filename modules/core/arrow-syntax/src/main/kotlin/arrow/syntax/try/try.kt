package arrow.syntax.`try`

import arrow.core.*
import arrow.data.*
import arrow.instances.applicative
import arrow.syntax.applicative.tupled

@Deprecated(DeprecatedAmbiguity, ReplaceWith("Try { body }.toOption()"))
inline fun <T> optionTry(body: () -> T): Option<T> = try {
    Some(body())
} catch (e: Throwable) {
    None
}

fun <A, B> merge(
        op1: () -> A,
        op2: () -> B): Try<Tuple2<A, B>> =
        Try.applicative().tupled(
                Try.invoke(op1),
                Try.invoke(op2)
        ).extract()

fun <A, B, C> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C): Try<Tuple3<A, B, C>> =
        Try.applicative().tupled(
                Try.invoke(op1),
                Try.invoke(op2),
                Try.invoke(op3)
        ).extract()

fun <A, B, C, D> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D): Try<Tuple4<A, B, C, D>> =
        Try.applicative().tupled(
                Try.invoke(op1),
                Try.invoke(op2),
                Try.invoke(op3),
                Try.invoke(op4)
        ).extract()

fun <A, B, C, D, E> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E): Try<Tuple5<A, B, C, D, E>> =
        Try.applicative().tupled(
                Try.invoke(op1),
                Try.invoke(op2),
                Try.invoke(op3),
                Try.invoke(op4),
                Try.invoke(op5)
        ).extract()

fun <A, B, C, D, E, F> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E,
        op6: () -> F): Try<Tuple6<A, B, C, D, E, F>> =
        Try.applicative().tupled(
                Try.invoke(op1),
                Try.invoke(op2),
                Try.invoke(op3),
                Try.invoke(op4),
                Try.invoke(op5),
                Try.invoke(op6)
        ).extract()

fun <A, B, C, D, E, F, G> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E,
        op6: () -> F,
        op7: () -> G): Try<Tuple7<A, B, C, D, E, F, G>> =
        Try.applicative().tupled(
                Try.invoke(op1),
                Try.invoke(op2),
                Try.invoke(op3),
                Try.invoke(op4),
                Try.invoke(op5),
                Try.invoke(op6),
                Try.invoke(op7)
        ).extract()

fun <A, B, C, D, E, F, G, H> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E,
        op6: () -> F,
        op7: () -> G,
        op8: () -> H): Try<Tuple8<A, B, C, D, E, F, G, H>> =
        Try.applicative().tupled(
                Try.invoke(op1),
                Try.invoke(op2),
                Try.invoke(op3),
                Try.invoke(op4),
                Try.invoke(op5),
                Try.invoke(op6),
                Try.invoke(op7),
                Try.invoke(op8)
        ).extract()

fun <A, B, C, D, E, F, G, H, I> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E,
        op6: () -> F,
        op7: () -> G,
        op8: () -> H,
        op9: () -> I): Try<Tuple9<A, B, C, D, E, F, G, H, I>> =
        Try.applicative().tupled(
                Try.invoke(op1),
                Try.invoke(op2),
                Try.invoke(op3),
                Try.invoke(op4),
                Try.invoke(op5),
                Try.invoke(op6),
                Try.invoke(op7),
                Try.invoke(op8),
                Try.invoke(op9)
        ).extract()

fun <A, B, C, D, E, F, G, H, I, J> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E,
        op6: () -> F,
        op7: () -> G,
        op8: () -> H,
        op9: () -> I,
        op10: () -> J): Try<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
        Try.applicative().tupled(
                Try.invoke(op1),
                Try.invoke(op2),
                Try.invoke(op3),
                Try.invoke(op4),
                Try.invoke(op5),
                Try.invoke(op6),
                Try.invoke(op7),
                Try.invoke(op8),
                Try.invoke(op9),
                Try.invoke(op10)
        ).extract()