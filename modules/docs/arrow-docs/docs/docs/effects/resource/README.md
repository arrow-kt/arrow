---
layout: docs
title: Resource
permalink: /docs/effects/resource/
---

## Resource

{:.intermediate}
intermediate

`Resource` is a data-type that handles acquiring and releasing resources in correct order.

Consider the following use case:
```kotlin:ank:playground
import arrow.effects.IO
import arrow.effects.extensions.io.fx.fx

object Consumer
object Handle

class Service(val handle: Handle, val consumer: Consumer)

fun createConsumer(): IO<Consumer> = IO { println("Creating consumer"); Consumer }
fun createDBHandle(): IO<Handle> = IO { println("Creating db handle"); Handle }
fun createFancyService(consumer: Consumer, handle: Handle): IO<Service> = IO { println("Creating service"); Service(handle, consumer) }

fun closeConsumer(consumer: Consumer): IO<Unit> = IO { println("Closed consumer") }
fun closeDBHandle(handle: Handle): IO<Unit> = IO { println("Closed db handle") }
fun shutDownFanceService(service: Service): IO<Unit> = IO { println("Closed service") }

//sampleStart
val program = fx {
  val consumer = createConsumer().bind()
  val handle = createDBHandle().bind()
  val service = createFancyService(consumer, handle).bind()

  // use service
  // <...>

  // we are done, now onto releasing resources
  shutDownFanceService(service).bind()
  closeDBHandle(handle).bind()
  closeConsumer(consumer).bind()
}
// sampleEnd

fun main() {
  program.unsafeRunSync()
}
```
Here we are creating and then using a service that has a dependency on two resources: A database handle and a consumer of some sort. All three resources need to be closed in the correct order at the end.
However this program is quite bad! It does not guarantee release if something failed in between and keeping track of acquisition order is unnecessary overhead.

There is already a typeclass called bracket that we can use to make our life easier:
```kotlin:ank:playground
//sampleStart
val bracketProgram =
  createConsumer().bracket(::closeConsumer) { consumer ->
    createDBHandle().bracket(::closeDBHandle) { handle ->
      createFancyService(consumer, handle).bracket(::shutDownFanceService) { service ->
        // use service
        // <...>
        IO.unit
      }
    }
  }
// sampleEnd

fun main() {
  bracketProgram.unsafeRunSync()
}
```

This is already much better. Now our services are guaranteed to close properly and also in order. However this pattern gets worse and worse the more resources you add because you need to nest deeper and deeper.

That is where `Resource` comes in:
```kotlin:ank:playground
//sampleStart
val managedTProgram = Resource.monad(IO.bracket()).binding {
  val consumer = Resource(::createConsumer, ::closeConsumer, IO.bracket()).bind()
  val handle = Resource(::createDBHandle, ::closeDBHandle, IO.bracket()).bind()
  Resource({ createFancyService(consumer, handle) }, ::shutDownFanceService, IO.bracket()).bind()
}.fix().invoke { service ->
  // use service
  // <...>

  IO.unit
}.fix()
// sampleEnd

fun main() {
  managedTProgram.unsafeRunSync()
}
```

All three programs do exactly the same with varying levels of simplicity and overhead. `Resource` uses `Bracket` under the hood but provides a nicer monadic interface for creating and releasing resources in order, whereas bracket is great for one-off acquisitions but becomes more complex with nested resources.

## Constructing a Resource

The primary way of constructing a resource is using its invoke constructor:
```kotlin:ank
val resource = Resource(::createConsumer, ::closeConsumer, IO.bracket())
```

There are other ways of constructing a `Resource`, more specifically you can construct one using either its `Monad` or `Monoid` instance. (Using `just` or `empty`). However this is reserved for special usecases and generally should not be used as they add no finalizers.
You can also lift values of `Kind<F, A>` into `Resource<F, E, A>` using the extension function `liftF`. This has the same drawbacks as `just` and `empty`.

## Using a Resource

To use a `Resource` simply call the `invoke` Method.
```kotlin:ank
val resource = Resource(::createConsumer, ::closeConsumer, IO.bracket())

resource.invoke { consumer -> IO { println(consumer) } }
  .fix().unsafeRunSync()
```

### Supported Type Classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.effects.*

DataType(Resource::class).tcMarkdownList()
```

