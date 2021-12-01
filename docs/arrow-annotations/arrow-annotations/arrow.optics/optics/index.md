//[arrow-annotations](../../../index.md)/[arrow.optics](../index.md)/[optics](index.md)

# optics

[common]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.CLASS](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-c-l-a-s-s/index.html)])

annotation class [optics](index.md)(targets: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[OpticsTarget](../-optics-target/index.md)&gt;)val x = 1<!--- KNIT example-arrow-annotations-01.kt -->

Empty arrays means "Everything that matches annotated class"

## Constructors

| | |
|---|---|
| [optics](optics.md) | [common]<br>fun [optics](optics.md)(targets: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[OpticsTarget](../-optics-target/index.md)&gt; = emptyArray()) |

## Properties

| Name | Summary |
|---|---|
| [targets](targets.md) | [common]<br>val [targets](targets.md): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[OpticsTarget](../-optics-target/index.md)&gt; |
