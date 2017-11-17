package kategory.fold

import kategory.autofold

val foldAnnotationKClass = autofold::class
val foldAnnotationClass = foldAnnotationKClass.java
val foldAnnotationName = "@" + foldAnnotationClass.simpleName
