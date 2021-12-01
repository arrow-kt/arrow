//[arrow-meta](../../../index.md)/[arrow.meta.encoder.jvm](../index.md)/[JvmMetaApi](index.md)/[typeClassInstance](type-class-instance.md)

# typeClassInstance

[jvm]\
open fun [TypeElement](https://docs.oracle.com/javase/8/docs/api/javax/lang/model/element/TypeElement.html).[typeClassInstance](type-class-instance.md)(): [TypeClassInstance](../../arrow.meta.encoder/-type-class-instance/index.md)?

Returns all the type information needed for type class introspection assuming this type element is a valid type class instance: An interface annotated with @extension with at least one type argument and extending another interface with one type argument as the first element in its extends clause
