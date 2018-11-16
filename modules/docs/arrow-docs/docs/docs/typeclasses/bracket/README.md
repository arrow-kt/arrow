---
layout: docs
title: Bracket
permalink: /docs/typeclasses/bracket/
video: EUqg3fSahhk
---

## Bracket

{:.intermediate}
intermediate

The `Bracket` Type class abstracts the ability to safely **acquire**, **use**, and then **release** a resource. 

Essentially, it could be considered the functional programming equivalent to the well known imperative 
`try/catch/finally` structure.

`Bracket` extends [`MonadError`]({{ '/docs/typeclasses/monaderror' | relative_url }}).

It ensures that the acquired resource is released at the end after using it, **even if the using action throws an error**. 
That ensures that no errors are swallowed.

Another key point of `Bracket` would be the ability to abstract over whether the resource is going to be used 
synchronously or asynchronously. 

### Example

Let's say we want to work with a file. Let's use a mock `File` API to avoid messing with the real one here.

These methods would allow to open a File, close it and also read it's content as a string.

```kotlin:ank
import arrow.effects.IO

/**
 * Mock File API.
 */
class File(url: String) {
    fun open(): File = this
    fun close(): Unit {}
    override fun toString(): String = "This file contains some interesting content!"
}

fun openFile(uri: String): IO<File> = IO { File(uri).open() }

fun closeFile(file: File): IO<Unit> = IO { file.close() }

fun fileToString(file: File): IO<String> = IO { file.toString() }
```

Note that we wrapped them into [`IO`]({{ '/docs/effects/io' | relative_url }}). [`IO`]({{ '/docs/effects/io' | relative_url }}) 
is able to wrap any side effecting computation to make it pure. In a real world system, these operations would contain 
side effects since they'd end up accessing the file system.
 
Read the [`IO`]({{ '/docs/effects/io' | relative_url }}) docs for more context on this.
 
Now, let's say we want to open a file, do some work with it, and then close it. With `Bracket`, we could make that 
process look like this:

```kotlin:ank
import arrow.effects.IO

openFile("data.json").bracket(release = { closeFile(it) }) { file ->
    fileToString(file)
}
``` 

This would ensure the file gets closed right after completing the `use` operation, which would be `fileToString(file)` 
here. If that operation throws an error, the file would also be closed.

Note that the result is still an `IO.Async` operation, which means it's still deferred (not executed yet). 

### Combinators

#### Kind<F, A>#bracket

Requires passing `release` and `use` lambdas. It ensures acquiring, using and releasing the resource at the end.

`fun <A, B> Kind<F, A>.bracket(release: (A) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B>`

```kotlin:ank
import arrow.effects.IO

openFile("data.json").bracket(release = { closeFile(it) }) { file ->
    fileToString(file)
}
```

#### Kind<F, A>#bracketCase

It's a generalized version of `bracket()` which uses `ExitCase` to distinguish between different exit cases when 
releasing the acquired resource. `ExitCase` can take the values `Completed`, `Canceled`, or `Error(e)`.  So depending 
how the `use` execution finalizes, the corresponding `ExitCase` value will be passed to the `release` lambda.

Requires passing `release` and `use` lambdas. It ensures acquiring, using and releasing the resource at the end.

`fun <A, B> Kind<F, A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<F, Unit>, use: (A) -> Kind<F, B>): Kind<F, B>`

```kotlin:ank
import arrow.effects.IO
import arrow.effects.typeclasses.ExitCase

openFile("data.json").bracketCase(
    release = { file, exitCase ->
      when (exitCase) {
        is ExitCase.Completed -> { /* do something */ }
        is ExitCase.Cancelled -> { /* do something */ }
        is ExitCase.Error -> { /* do something */ }
      }
      closeFile(file)
    },
    use = { file ->
      fileToString(file)
    })
```

#### Other combinators

For a full list of other useful combinators available in `Bracket` see the [Source][bracket_source]{:target="_blank"}

### Laws

Arrow provides [`BracketLaws`][bracket_laws_source]{:target="_blank"} in the form of test cases for internal 
verification of lawful instances and third party apps creating their own Bracket instances.

#### Creating your own `Bracket` instances

Arrow already provides Bracket instances for most common datatypes both in Arrow and the Kotlin stdlib.
Oftentimes you may find the need to provide your own for unsupported datatypes.

You may create or automatically derive instances of `Bracket` for your own datatypes which you will be able to use in 
the context of abstract polymorphic code.

See [Deriving and creating custom typeclass]({{ '/docs/patterns/glossary' | relative_url }})

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.effects.typeclasses.Bracket

TypeClass(Bracket::class).dtMarkdownList()
```

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

```kotlin:ank:outFile(diagram.nomnol)
import arrow.reflect.*
import arrow.effects.typeclasses.Bracket

TypeClass(Bracket::class).hierarchyGraph()
```

[bracket_source]: https://github.com/arrow-kt/arrow/blob/master/modules/effects/arrow-effects/src/main/kotlin/arrow/effects/typeclasses/Bracket.kt
[bracket_laws_source]: https://github.com/arrow-kt/arrow/blob/master/modules/core/arrow-test/src/main/kotlin/arrow/test/laws/BracketLaws.kt
