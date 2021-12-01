//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)/[zip](zip.md)

# zip

[common]\
fun &lt;[B](zip.md)&gt; [zip](zip.md)(other: [Option](index.md)&lt;[B](zip.md)&gt;): [Option](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](zip.md)&gt;&gt;

inline fun &lt;[B](zip.md), [C](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, map: ([A](index.md), [B](zip.md)) -&gt; [C](zip.md)): [Option](index.md)&lt;[C](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md)) -&gt; [D](zip.md)): [Option](index.md)&lt;[D](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md)) -&gt; [E](zip.md)): [Option](index.md)&lt;[E](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md)) -&gt; [F](zip.md)): [Option](index.md)&lt;[F](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md)) -&gt; [G](zip.md)): [Option](index.md)&lt;[G](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, g: [Option](index.md)&lt;[G](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md)) -&gt; [H](zip.md)): [Option](index.md)&lt;[H](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, g: [Option](index.md)&lt;[G](zip.md)&gt;, h: [Option](index.md)&lt;[H](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)) -&gt; [I](zip.md)): [Option](index.md)&lt;[I](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, g: [Option](index.md)&lt;[G](zip.md)&gt;, h: [Option](index.md)&lt;[H](zip.md)&gt;, i: [Option](index.md)&lt;[I](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md)) -&gt; [J](zip.md)): [Option](index.md)&lt;[J](zip.md)&gt;

inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md), [K](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, g: [Option](index.md)&lt;[G](zip.md)&gt;, h: [Option](index.md)&lt;[H](zip.md)&gt;, i: [Option](index.md)&lt;[I](zip.md)&gt;, j: [Option](index.md)&lt;[J](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md)) -&gt; [K](zip.md)): [Option](index.md)&lt;[K](zip.md)&gt;
