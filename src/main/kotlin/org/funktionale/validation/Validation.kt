package org.funktionale.validation

import org.funktionale.either.Disjunction

class Validation<out E : Any>(vararg disjunctionSequence: Disjunction<E, *>) {


    val failures: List<E> = disjunctionSequence.filter { it.isLeft() }.map { it.swap().get() }

    val hasFailures: Boolean = failures.isNotEmpty()

}


