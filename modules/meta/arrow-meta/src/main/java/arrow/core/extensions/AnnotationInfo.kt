package arrow.core.extensions

import arrow.extension

val instanceAnnotationKClass = extension::class
val instanceAnnotationClass = instanceAnnotationKClass.java
val instanceAnnotationName = "@" + instanceAnnotationClass.simpleName
