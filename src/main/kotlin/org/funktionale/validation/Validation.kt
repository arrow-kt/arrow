package org.funktionale.validation

import org.funktionale.either.Disjunction
import org.funktionale.either.Either

class Validation<out E : Any>(vararg eitherSequence: Either<E, *>) {

    constructor(vararg disjunctionSequence: Disjunction<E, *>) : this(*disjunctionSequence.map { it.toEither() }.toTypedArray())

    val failures: List<E> = eitherSequence.filter { it.isLeft() }.map { it.left().get() }

    val hasFailures: Boolean = failures.isNotEmpty()

}


