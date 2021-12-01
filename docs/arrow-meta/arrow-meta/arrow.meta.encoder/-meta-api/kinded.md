//[arrow-meta](../../../index.md)/[arrow.meta.encoder](../index.md)/[MetaApi](index.md)/[kinded](kinded.md)

# kinded

[jvm]\
abstract val [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md).[kinded](kinded.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Determine if this [TypeName.ParameterizedType](../../arrow.meta.ast/-type-name/-parameterized-type/index.md) is in kinded position. ex: arrow.Kind&lt;ForOption, A&gt; => true ex: Option&lt;A&gt; => false

The current definition of kinded for Arrow Meta is that a parameterized kinded type is a type that:

<ol><li>Extends from arrow.Kind.</li><li>The type has two type arguments.</li><li>The first type argument is a type variable.</li></ol>
