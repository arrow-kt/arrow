---
layout: docs-core
title: Repeat
permalink: /docs/arrow/typeclasses/repeat/
redirect_from:
  - /docs/typeclasses/repeat/
---

## Repeat




The `Repeat` typeclass extends the `Zip` typeclass with a way to repeat the structure.

### Main Combinators

#### repeat

Provides a structure that can be used to zip with. The structure repeats the provided value and is
potentially infinite.

`fun <A> repeat(a: A): Kind<F, A>`

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.sequencek.repeat.repeat
import arrow.core.*

val seq = generateSequence(0) { it + 1 }.k()
SequenceK.repeat().run {
    repeat("Item").zip(seq).fix().take(5).toList()
}
```

### Laws

Arrow provides [`RepeatLaws`][functor_laws_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Repeat instances.

#### Creating your own `Repeat` instances

Arrow already provides Repeat instances for common datatypes (e.g. Option, SequenceK). See their implementations
and accompanying testcases for reference.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

Additionally all instances of [`Repeat`]({{ '/docs/arrow/typeclasses/repeat' | relative_url }}) implement the `Zip` typeclass directly
since they are all subtypes of `Zip`

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Repeat

TypeClass(Repeat::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Repeat)

[functor_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-typeclasses/src/main/kotlin/arrow/typeclasses/Repeat.kt
[functor_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/RepeatLaws.kt
