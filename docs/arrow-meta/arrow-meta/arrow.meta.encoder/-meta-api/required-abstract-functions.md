//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[requiredAbstractFunctions](required-abstract-functions.md)

# requiredAbstractFunctions

[jvm]\
abstract val [TypeClassInstance](../-type-class-instance/index.md).[requiredAbstractFunctions](required-abstract-functions.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Func](../../arrow.meta.ast/-func/index.md)&gt;

The list of functions a type class instance needs to implement to resolve it's hierarchical dependencies to other type classes ex: override fun MF(): arrow.typeclasses.Monad<F> in the KleisliMonadInstance

&lt;!--- KNIT example-arrow-01.kt --&gt;\
fun &lt;F, D&gt; Companion.monad(MF: Monad&lt;F&gt;): KleisliMonad&lt;F, D&gt; =\
  object : arrow.instances.KleisliMonad&lt;F, D&gt; { override fun MF(): arrow.typeclasses.Monad&lt;F&gt; = MF }`<!--- KNIT example-arrow-02.kt -->
