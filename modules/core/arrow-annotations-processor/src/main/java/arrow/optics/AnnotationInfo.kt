package arrow.optics

import arrow.optic

val opticsAnnotationKClass = optic::class
val opticsAnnotationClass = opticsAnnotationKClass.java
val opticsAnnotationName = "@" + opticsAnnotationKClass.simpleName
