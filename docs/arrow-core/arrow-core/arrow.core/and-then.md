//[arrow-core](../../index.md)/[arrow.core](index.md)/[andThen](and-then.md)

# andThen

[common]\
inline fun &lt;[E](and-then.md), [A](and-then.md), [B](and-then.md)&gt; [Validated](-validated/index.md)&lt;[E](and-then.md), [A](and-then.md)&gt;.[andThen](and-then.md)(f: ([A](and-then.md)) -&gt; [Validated](-validated/index.md)&lt;[E](and-then.md), [B](and-then.md)&gt;): [Validated](-validated/index.md)&lt;[E](and-then.md), [B](and-then.md)&gt;

Apply a function to a Valid value, returning a new Validation that may be valid or invalid

Example:

import arrow.core.Validated\
Validated.Valid(5).andThen { Valid(10) } // Result: Valid(10)\
Validated.Valid(5).andThen { Invalid(10) } // Result: Invalid(10)\
Validated.Invalid(5).andThen { Valid(10) } // Result: Invalid(5)<!--- KNIT example-validated-21.kt -->

[common, js, jvm, native]\
[common, js, jvm, native]\
infix fun &lt;[P1](and-then.md), [P2](and-then.md), [IP](and-then.md), [R](and-then.md)&gt; ([P1](and-then.md), [P2](and-then.md)) -&gt; [IP](and-then.md).[andThen](and-then.md)(f: ([IP](and-then.md)) -&gt; [R](and-then.md)): ([P1](and-then.md), [P2](and-then.md)) -&gt; [R](and-then.md)

[common, js, jvm, native]\
infix fun &lt;[IP](and-then.md), [R](and-then.md)&gt; () -&gt; [IP](and-then.md).[andThen](and-then.md)(f: ([IP](and-then.md)) -&gt; [R](and-then.md)): () -&gt; [R](and-then.md)

[common, js, jvm, native]\
infix fun &lt;[P1](and-then.md), [IP](and-then.md), [R](and-then.md)&gt; ([P1](and-then.md)) -&gt; [IP](and-then.md).[andThen](and-then.md)(f: ([IP](and-then.md)) -&gt; [R](and-then.md)): ([P1](and-then.md)) -&gt; [R](and-then.md)
