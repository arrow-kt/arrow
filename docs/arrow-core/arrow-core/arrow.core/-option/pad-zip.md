//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[padZip](pad-zip.md)

# padZip

[common]\
fun &lt;[B](pad-zip.md)&gt; [padZip](pad-zip.md)(other: [Option](index.md)&lt;[B](pad-zip.md)&gt;): [Option](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md)?, [B](pad-zip.md)?&gt;&gt;

inline fun &lt;[B](pad-zip.md), [C](pad-zip.md)&gt; [padZip](pad-zip.md)(other: [Option](index.md)&lt;[B](pad-zip.md)&gt;, f: ([A](index.md)?, [B](pad-zip.md)?) -&gt; [C](pad-zip.md)): [Option](index.md)&lt;[C](pad-zip.md)&gt;
