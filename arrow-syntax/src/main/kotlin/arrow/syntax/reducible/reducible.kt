package arrow.syntax.reducible

import arrow.*
import arrow.data.NonEmptyList

fun <F, A> Reducible<F>.toNonEmptyList(fa: HK<F, A>): NonEmptyList<A> =
        reduceRightTo(fa, { a -> NonEmptyList.of(a) }, { a, lnel ->
            lnel.map { nonEmptyList -> NonEmptyList(a, listOf(nonEmptyList.head) + nonEmptyList.tail) }
        }).value()