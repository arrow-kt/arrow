//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Either](../index.md)/[Companion](index.md)/[conditionally](conditionally.md)

# conditionally

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

inline fun &lt;[L](conditionally.md), [R](conditionally.md)&gt; [conditionally](conditionally.md)(test: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), ifFalse: () -&gt; [L](conditionally.md), ifTrue: () -&gt; [R](conditionally.md)): [Either](../index.md)&lt;[L](conditionally.md), [R](conditionally.md)&gt;

Will create an [Either](../index.md) from the result of evaluating the first parameter using the functions provided on second and third parameters. Second parameter represents function for creating an [Left](../-left/index.md) in case of a false result of evaluation and third parameter will be used to create a [Right](../-right/index.md) in case of a true result.

#### Return

[Right](../-right/index.md) if evaluation succeed, [Left](../-left/index.md) otherwise

## Parameters

common

| | |
|---|---|
| test | expression to evaluate and build an [Either](../index.md) |
| ifFalse | function to create a [Left](../-left/index.md) in case of false result of test |
| ifTrue | function to create a [Right](../-right/index.md) in case of true result of test |
