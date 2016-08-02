package org.funktionale.validation

import org.funktionale.either.*

class Validation<E: Any>(vararg val eitherSequence: Either<E, *>) {

    val failures: List<E>  = eitherSequence.filter{it.isLeft()}.map{it.left().get()}

    val hasFailures: Boolean = failures.isNotEmpty()

}


