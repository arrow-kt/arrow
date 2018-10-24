---
layout: docs
title: Reader
permalink: /docs/datatypes/reader/
---

## Reader 

{:.intermediate}
intermediate

Reader is a datatype that represents computation that can read values from shared environment.

We can use `Reader().lift` to create `Reader` instance.

```kotlin:ank
import arrow.data.Reader

val container: Reader<String, String> = Reader().lift { "{ container: $it }" }
val subContainer: Reader<String, String> = Reader().lift { "{ subContainer: $it" }
val content: Reader<String, String> = Reader().lift { "{ content: \"$it\" }" }
```

Right now, we just lift 3 functions into `Reader`s.

You can also use `.reader()` extension function instead of `Reader().lift`

```kotlin:ank
import arrow.data.reader

val content2: Reader<String, String> = { c: String -> "{ content: \"$c\" }" }.reader()
```

or use the `Reader` constructor.

```kotlin:ank
import arrow.core.Id

val content3: Reader<String, String> = Reader { Id("{ content: \"$it\" }") }
```

Notice that when using Reader constructor, the return type must be wrapped in `Id` since `Reader` is actually just a special case of `Kleisli` that the result wrapper is `Id`.

Eventually, we want to have `content` (with the actual content in there) which being wrapped around by `subContainer` and wrap it again by `container`. We can do it like this

```kotlin:ank
import arrow.data.andThen

content
    .andThen(subContainer)
    .andThen(container)
    .run("world")

```

When you `run` it, you are providing the actual environment to the reader chain, you can see that the result you get will be wrapped in `Id`. You can `.value()` to get the actual value out or use `runId` instead.


```kotlin:ank
import arrow.data.runId

content
    .andThen(subContainer)
    .andThen(container)
    .runId("world")

```


## Functions

#### Local
This function allows doing a transformation of the environment before it is being executed.

```kotlin:ank
content
    .local<String> { "hello $it" }
    .andThen(subContainer)
    .andThen(container)
    .runId("world")

```

#### Ap
The `ap` function takes argument of type `Reader` that contains function and it basically pipes through value of the `Reader` that calls `ap` to the function wrapped inside.

```kotlin:ank
import arrow.core.applicative

fun wrap(prefix: String): Reader<String, (String) -> String> =
    Reader().lift { env -> { "{ ${prefix}Of${env.capitalize()}: $it }" } }

content
    .ap(Id.applicative(), wrap("yetAnotherContainer"))
    .andThen(subContainer)
    .andThen(container)
    .runId("world")

```

#### Map
The `map` function transform the `Reader` output value.

```kotlin:ank
import arrow.core.functor

content
    .andThen(subContainer)
    .andThen(container)
    .map(Id.functor()) { it.replace(":", " =>")}
    .runId("world")

```

#### FlatMap
`flatMap` is similar to `map` except that the function in the transformation is returning `Reader` which means you have access to both result of reader that calls `flatMap` and the environment.

```kotlin:ank
import arrow.core.monad

content
    .andThen(subContainer)
    .andThen(container)
    .flatMap(Id.monad()) { value: String ->
        Reader().lift<String, String> { env -> "$env $value $env" }
    }
    .runId("world")

```


#### AndThen
`andThen` can be used in 3 scenarios.

First, use it to compose `Reader` like we have been using it so far. You might notice at this point that `Reader` is just a wrapper of function.

```
data class Content(val text: String, val charCount: Int)

val charCount: Reader<String, Content> = Reader().lift { Content(it, it.count()) }
val contentWithCharCount: Reader<Content, String> =
        Reader().lift { "{ content: \"${it.text}\", charCount: ${it.charCount} }" }

charCount
    .andThen(contentWithCharCount)
    .andThen(subContainer)
    .andThen(container)
    .runId("world")

// { container: { subContainer: { content: "world", charCount: 5 } }
```

Second, use it to set the value of the computation to a specific value altogether.

```kotlin:ank
content
    .andThen(subContainer)
    .andThen(container)
    .andThen(Reader().just("I DON'T CARE"))
    .runId("world")

```

Note that the `just` function returns a `Reader` with a specified value.

Thrid, use it as an alias for `map`

```kotlin:ank
content
    .andThen(subContainer)
    .andThen(container)
    .andThen { it.replace(":", " =>") }
    .runId("world")

```

## Monad Comprehension

Instead of just chaining functions, we can just use monad comprehension with `Reader` as well.

There is another that we haven't talked about yet, which is the `ask` function. It is the function that pipe environment into the result of the `Reader` which will be useful in this context.

```
val hello = Reader().monad<String>().binding {
    val name = Reader().ask<String>().bind()
    val containedContent = content.andThen(container)
    val holaContainer = containedContent.local<String> { "hola $it" }.bind()
    val niHaoContainer = containedContent.local<String> { "你好 $it" }.bind()

    """
    {
        name: "$name",
        greetings: {
            hola: $holaContainer,
            niHao: $niHaoContainer
        }
    }
    """.trimIndent()
}.fix()

hello.runId("world")


/*
{
    name: "world",
    greetings: {
        hola: { container: { content: "hola world" } },
        niHao: { container: { content: "你好 world" } }
    }
}
*/
```


## Available Instances

* [Applicative]({{ '/docs/typeclasses/applicative' | relative_url }})
* [ApplicativeError]({{ '/docs/typeclasses/applicativeerror' | relative_url }})
* [Functor]({{ '/docs/typeclasses/functor' | relative_url }})
* [Monad]({{ '/docs/typeclasses/monad' | relative_url }})
* [MonadError]({{ '/docs/typeclasses/monaderror' | relative_url }})
