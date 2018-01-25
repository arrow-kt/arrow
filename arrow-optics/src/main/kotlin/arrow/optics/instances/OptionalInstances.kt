package arrow.optics.instances

import arrow.core.*
import arrow.optics.*
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.syntax.option.toOption

fun <A> optionOptional(): Optional<Option<A>, A> = Optional(
        getOrModify = { a -> a.fold({ a.left() }, { it.right() }) },
        set = { a -> { it.fold({ Option.empty() }, { a.toOption() }) }}
)

fun <A> nullableOptional(): Optional<A?, A> = Optional(
        getOrModify = { a -> a?.right() ?: a.left() },
        set = { a -> { if (it != null) a else null } }
)

fun <A> listElementPositionOptional(position: Int): Optional<List<A>, A> = Optional(
        getOrModify = { l -> l.getOrNull(position)?.right() ?: l.left() },
        set = { e -> { l -> l.mapIndexed { index: Int, value: A -> if (index == position) e else value } } }
)

fun <A> listElementOptional(predicate: Predicate<A>): Optional<List<A>, A> = Optional(
        getOrModify = { l -> l.find(predicate)?.right() ?: l.left() },
        set = { e -> { l ->
            val location = l.indexOfFirst(predicate)
            if (location == -1) {
                l
            } else {
                l.mapIndexed { index: Int, value: A -> if (index == location) e else value }
            }
        } }
)