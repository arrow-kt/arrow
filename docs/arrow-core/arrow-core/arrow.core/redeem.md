//[arrow-core](../../index.md)/[arrow.core](index.md)/[redeem](redeem.md)

# redeem

[common]\
inline fun &lt;[A](redeem.md), [B](redeem.md), [C](redeem.md)&gt; [Either](-either/index.md)&lt;[A](redeem.md), [B](redeem.md)&gt;.[redeem](redeem.md)(fe: ([A](redeem.md)) -&gt; [C](redeem.md), fa: ([B](redeem.md)) -&gt; [C](redeem.md)): [Either](-either/index.md)&lt;[A](redeem.md), [C](redeem.md)&gt;

inline fun &lt;[A](redeem.md), [B](redeem.md)&gt; [Option](-option/index.md)&lt;[A](redeem.md)&gt;.[redeem](redeem.md)(fe: ([Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) -&gt; [B](redeem.md), fb: ([A](redeem.md)) -&gt; [B](redeem.md)): [Option](-option/index.md)&lt;[B](redeem.md)&gt;

inline fun &lt;[E](redeem.md), [A](redeem.md), [B](redeem.md)&gt; [Validated](-validated/index.md)&lt;[E](redeem.md), [A](redeem.md)&gt;.[redeem](redeem.md)(fe: ([E](redeem.md)) -&gt; [B](redeem.md), fa: ([A](redeem.md)) -&gt; [B](redeem.md)): [Validated](-validated/index.md)&lt;[E](redeem.md), [B](redeem.md)&gt;
