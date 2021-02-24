package arrow.fold

import arrow.autofold

val foldAnnotationKClass = autofold::class
val foldAnnotationClass = foldAnnotationKClass.java
val foldAnnotationName = "@" + foldAnnotationClass.simpleName
