//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Validated](index.md)/[withEither](with-either.md)

# withEither

[common]\
inline fun &lt;[EE](with-either.md), [B](with-either.md)&gt; [withEither](with-either.md)(f: ([Either](../-either/index.md)&lt;[E](index.md), [A](index.md)&gt;) -&gt; [Either](../-either/index.md)&lt;[EE](with-either.md), [B](with-either.md)&gt;): [Validated](index.md)&lt;[EE](with-either.md), [B](with-either.md)&gt;

Convert to an Either, apply a function, convert back. This is handy when you want to use the Monadic properties of the Either type.
