package arrow.syntax.eval

import arrow.core.*
import arrow.instances.*
import arrow.syntax.applicative.tupled

fun <A, B> merge(
        op1: () -> A,
        op2: () -> B): Eval<Tuple2<A, B>> =
        Eval.applicative().tupled(
                Eval.later(op1),
                Eval.later(op2)
        ).ev()

fun <A, B, C> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C): Eval<Tuple3<A, B, C>> =
        Eval.applicative().tupled(
                Eval.later(op1),
                Eval.later(op2),
                Eval.later(op3)
        ).ev()

fun <A, B, C, D> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D): Eval<Tuple4<A, B, C, D>> =
        Eval.applicative().tupled(
                Eval.later(op1),
                Eval.later(op2),
                Eval.later(op3),
                Eval.later(op4)
        ).ev()

fun <A, B, C, D, E> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E): Eval<Tuple5<A, B, C, D, E>> =
        Eval.applicative().tupled(
                Eval.later(op1),
                Eval.later(op2),
                Eval.later(op3),
                Eval.later(op4),
                Eval.later(op5)
        ).ev()

fun <A, B, C, D, E, F> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E,
        op6: () -> F): Eval<Tuple6<A, B, C, D, E, F>> =
        Eval.applicative().tupled(
                Eval.later(op1),
                Eval.later(op2),
                Eval.later(op3),
                Eval.later(op4),
                Eval.later(op5),
                Eval.later(op6)
        ).ev()

fun <A, B, C, D, E, F, G> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E,
        op6: () -> F,
        op7: () -> G): Eval<Tuple7<A, B, C, D, E, F, G>> =
        Eval.applicative().tupled(
                Eval.later(op1),
                Eval.later(op2),
                Eval.later(op3),
                Eval.later(op4),
                Eval.later(op5),
                Eval.later(op6),
                Eval.later(op7)
        ).ev()

fun <A, B, C, D, E, F, G, H> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E,
        op6: () -> F,
        op7: () -> G,
        op8: () -> H): Eval<Tuple8<A, B, C, D, E, F, G, H>> =
        Eval.applicative().tupled(
                Eval.later(op1),
                Eval.later(op2),
                Eval.later(op3),
                Eval.later(op4),
                Eval.later(op5),
                Eval.later(op6),
                Eval.later(op7),
                Eval.later(op8)
        ).ev()

fun <A, B, C, D, E, F, G, H, I> merge(
        op1: () -> A,
        op2: () -> B,
        op3: () -> C,
        op4: () -> D,
        op5: () -> E,
        op6: () -> F,
        op7: () -> G,
        op8: () -> H,
        op9: () -> I): Eval<Tuple9<A, B, C, D, E, F, G, H, I>> =
        Eval.applicative().tupled(
                Eval.later(op1),
                Eval.later(op2),
                Eval.later(op3),
                Eval.later(op4),
                Eval.later(op5),
                Eval.later(op6),
                Eval.later(op7),
                Eval.later(op8),
                Eval.later(op9)
        ).ev()

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
        op10: () -> J): Eval<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
        Eval.applicative().tupled(
                Eval.later(op1),
                Eval.later(op2),
                Eval.later(op3),
                Eval.later(op4),
                Eval.later(op5),
                Eval.later(op6),
                Eval.later(op7),
                Eval.later(op8),
                Eval.later(op9),
                Eval.later(op10)
        ).ev()