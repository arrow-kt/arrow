package arrow.optics

val opticsAnnotationKClass = optics::class
val opticsAnnotationClass = opticsAnnotationKClass.java
val opticsAnnotationName = "@" + opticsAnnotationKClass.simpleName
