package arrow.tc

import arrow.typeclass

val typeClassAnnotationKClass = typeclass::class
val typeClassAnnotationClass = typeClassAnnotationKClass.java
val typeClassAnnotationName = "@" + typeClassAnnotationClass.simpleName
