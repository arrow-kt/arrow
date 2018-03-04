package arrow.generic

import arrow.product

val productAnnotationKClass = product::class
val productAnnotationClass = productAnnotationKClass.java
val productAnnotationName = "@" + productAnnotationClass.simpleName
val productAnnotationTarget = "data class"