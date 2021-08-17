package arrow.optics

import kotlin.reflect.KClass

public val opticsAnnotationKClass: KClass<optics> = optics::class
public val opticsAnnotationClass: Class<optics> = opticsAnnotationKClass.java
