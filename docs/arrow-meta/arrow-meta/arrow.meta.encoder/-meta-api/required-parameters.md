//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[requiredParameters](required-parameters.md)

# requiredParameters

[jvm]\
abstract val [TypeClassInstance](../-type-class-instance/index.md).[requiredParameters](required-parameters.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Parameter](../../arrow.meta.ast/-parameter/index.md)&gt;

The list of parameters a type class instance needs to be able to implement the [requiredAbstractFunctions](required-abstract-functions.md) ex: override fun MF: Monad<F> in KleisliMonadInstance

&lt;!--- KNIT example-arrow-03.kt --&gt;\
fun &lt;F, D&gt; Companion.monad(MF: Monad&lt;F&gt;): KleisliMonad&lt;F, D&gt; =\
  object : arrow.instances.KleisliMonad&lt;F, D&gt; { override fun MF(): arrow.typeclasses.Monad&lt;F&gt; = MF }`<!--- KNIT example-arrow-04.kt -->
