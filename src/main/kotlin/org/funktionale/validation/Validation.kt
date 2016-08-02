package org.funktionale.validation

import org.funktionale.either.Either

class Validation<out E: Any>(vararg eitherSequence: Either<E, *>) {

    val failures: List<E>  = eitherSequence.filter{it.isLeft()}.map{it.left().get()}

    val hasFailures: Boolean = failures.isNotEmpty()

}


