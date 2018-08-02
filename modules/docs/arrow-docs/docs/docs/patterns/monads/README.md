---
layout: docs
title: The Monad Tutorial
permalink: /docs/patterns/monads/
---

## Monads explained in Kotlin (again)

{:.intermediate}
intermediate

### Credits

This doc has been adapted from Mikhail Shilkov's blog entry [`Monads explained in C# (again)`](https://mikhail.io/2018/07/monads-explained-in-csharp-again/). It attempts to explain the rationale behind Monads, providing simple examples and how they relate to standard library constructs.

If you're just interested in the API, head down to the [`Monad`]({{ '/docs/typeclasses/monad' | relative_url }}) typeclass page.

### Intro

I love functional programming for the simplicity that it brings.

But at the same time, I realize that learning functional programming is a challenging process. FP comes with a baggage of unfamiliar vocabulary that can be daunting for somebody coming from an object-oriented language like Kotlin.

![](https://mikhail.io/2018/07/monads-explained-in-csharp-again//functional-programming-word-cloud.png)

*some of functional lingo*

`Monad` is probably the most infamous term from the list above. Monads have reputation of being something very abstract and very confusing.

### The Fallacy of Monad Tutorials

Numerous attempts were made to explain monads in simple definitions; and monad tutorials have become a genre of its own. And yet, times and times again, they fail to enlighten the readers.

The shortest explanation of monads looks like this:

> A Monad is just a monoid in the category of endofunctors

It's both mathematically correct and totally useless to anybody learning functional programming. To understand this statement, one has to know the terms "monoid", "category" and "endofunctors" and be able to mentally compose them into something meaningful.

The same problem is apparent in most monad tutorials. They assume some pre-existing knowledge in heads of their readers, and if that assumption fails, the tutorial doesn't click.

Focusing too much on mechanics of monads instead of explaining why they are important is another common problem.

Douglas Crockford grasped this fallacy very well:

>The monadic curse is that once someone learns what monads are and how to use them, they lose the ability to explain them to other people

The problem here is likely the following. Every person who understands monads had their own path to this knowledge. It hasn't come all at once, instead there was a series of steps, each giving an insight, until the last final step made the puzzle complete.

But they don't remember the whole path anymore. They go online and blog about that very last step as the key to understanding, joining the club of flawed explanations.

There is an actual academic paper from Tomas Petricek that studies monad tutorials.

I've read that paper and a dozen of monad tutorials online. And of course, now I came up with my own.

I'm probably doomed to fail too, at least for some readers.

### Story of Composition

The base element of each functional program is Function. In typed languages each function is just a mapping between the type of its input parameter and output parameter. Such type can be annotated as `func: TypeA -> TypeB`.

Kotlin is an object-oriented language, so we use methods to declare functions. There are two ways to define a method comparable to function `func` above. I can use a top level "static" method:

```kotlin
fun func(a: ClassA): ClassB { ... }
```

... or an instance method:

```kotlin
class ClassA {
    // Instance method
    fun func(): ClassB { ... }
}
```

The top level form looks closer to the function notation, but both ways are actually equivalent for the purpose of our discussion. I will use instance methods in my examples, however all of them could be written as top level extension methods too.

How do we compose more complex workflows, programs and applications, out of such simple building blocks? A lot of patterns in both OOP and FP worlds revolve around this question. And monads are one of the answers.

My sample code is going to be about conferences and speakers. The method implementations aren't really important, just watch the types carefully. There are 4 classes (types) and 3 methods (functions):

```kotlin
class Speaker {
    fun nextTalk(): Talk { ... }
}

class Talk {
    fun getConference(): Conference { ... }
}

class Conference {
    fun getCity(): City { ... }
}

class City { ... }
```

These methods are currently very easy to compose into a workflow:

```kotlin
fun nextTalkCity(speaker: Speaker): City {
    val talk = speaker.nextTalk()
    val conf = talk.getConference()
    val city = conf.getCity()
    return city
}
```

Because the return type of the previous step always matches the input type of the next step, we can write it even shorter:

```kotlin
fun nextTalkCity(speaker: Speaker): City =
  speaker
    .nextTalk()
    .getConference()
    .getCity()
```

This code looks quite readable. It's concise and it flows from top to bottom, from left to right, similar to how we are used to read any text. There is not much noise too.

That's not what real codebases look like though, because there are multiple complications along the happy composition path. Let's look at some of them.

### NULLs

Any class instance in Kotlin can be null. In the example above I might get runtime errors if one of the methods ever returns null back.

Typed functional programming always tries to be explicit about types, so I'll re-write the signatures of my methods to annotate the return types as nullables:

```kotlin
class Speaker {
    fun nextTalk(): Talk? { ... }
}

class Talk {
    fun getConference(): Conference? { ... }
}

class Conference {
    fun getCity(): City? { ... }
}

class City { ... }
```

Now, when composing our workflow, we need to take care of null results:

```kotlin
fun nextTalkCity(speaker: Speaker?): City? {
    if (speaker == null) return null

    val talk = speaker.nextTalk()
    if (talk == null) return null

    val conf = talk.getConference()
    if (conf == null) return null

    val city = conf.getCity()
    return city
}
```

It's still the same method, but it got more noise now. Even though I used short-circuit returns and one-liners, it still got harder to read.

To fight that problem, smart language designers came up with the [Safe Call Operator](https://kotlinlang.org/docs/reference/null-safety.html#safe-calls):

```kotlin
fun nextTalkCity(speaker: Speaker?): City? {
    return
        speaker
        ?.nextTalk()
        ?.getConference()
        ?.getCity()
}
```

Now we are almost back to our original workflow code: it's clean and concise, we just got 3 extra `?` symbols around.

Let's take another leap.

### Collections

Quite often a function returns a collection of items, not just a single item. To some extent, that's a generalization of `null` case: with `T?` we might get 0 or 1 results back, while with a collection we can get 0 to any n results.

Our sample API could look like this:

```kotlin
class Speaker {
    fun getTalks(): List<Talk> { ... }
}

class Talk {
    fun getConferences(): List<Conference> { ... }
}

class Conference {
    fun getCities() List<City> { ... }
}
```

I used `List<T>` but it could be any class or even a `Sequence<T>`.

How would we combine the methods into one workflow? The traditional version would look like this:

```kotlin
fun allCitiesToVisit(speaker: Speaker): List<City> {
    val result = mutableListOf<City>()

    for (talk in speaker.getTalks())
        for (conf in talk.getConferences())
            for (city in conf.getCities())
                result.add(city)

    return result
}
```

It reads ok-ish still. But the combination of nested loops and mutation with some conditionals sprinkled on them can get unreadable pretty soon. The exact workflow might be lost in the mechanics.

As an alternative, the Kotlin language designers included extension methods. We can write code like this:

```kotlin
fun allCitiesToVisit(speaker: Speaker): List<City> {
    return
        speaker
        .getTalks()
        .flatMap { talk -> talk.getConferences() }
        .flatMap { conf -> conf.getCities() }
}
```

Let me do one further trick and format the same code in an unusual way:

```kotlin
fun allCitiesToVisit(Speaker speaker): List<City> {
    return
        speaker
        .getTalks()           .flatMap { x -> x
        .getConferences()    }.flatMap { x -> x
        .getCities()         }
}
```

Now you can see the original original code on the left, combined with just a bit of technical repetitive clutter on the right. Hold on, I'll show you where I'm going.

Let's discuss another possible complication.

### Asynchronous Calls

What if our methods need to access some remote database or service to produce the results? This should be shown in type signature,
and we can imagine a library providing a `Task<T>` type for that:

```kotlin
class Speaker {
    fun nextTalk(): Task<Talk> { ... }
}

class Talk {
    fun getConference(): Task<Conference> { ... }
}

class Conference {
    fun getCity(): Task<City> { ... }
}
```

This change breaks our nice workflow composition again.

```kotlin
fun nextTalkCity(speaker: Speaker): Task<City> {
    return
        speaker.nextTalk().execute()
        .then { talk -> talk.getConference() }.execute()
        .then { conf -> conf.getCity() }.execute()
}
```

Hard to read, but let me apply my formatting trick again:

```kotlin
fun nextTalkCity(speaker: Speaker): Task<City> {
    return
        speaker
        .nextTalk()         .execute().then { x -> x
        .getConference()   }.execute().then { x -> x
        .getCity()         }
}
```

You can see that, once again, it's our nice readable workflow on the left plus some mechanical repeatable junction code on the right.

### Pattern

Can you see a pattern yet?

I'll repeat the `T?`, `List<T>` `Task<T>`-based workflows again:

```kotlin
fun nextTalkCity(speaker: Speaker?): City? {
    return
        speaker               ?
        .nextTalk()           ?
        .getConference()      ?
        .getCity()
}

fun allCitiesToVisit(speaker: Speaker): List<City> {
    return
        speaker
        .getTalks()            .flatMap { x -> x
        .getConferences()     }.flatMap { x -> x
        .getCities()          }
}

fun nextTalkCity(speaker: Speaker): Task<City> {
    return
        speaker
        .nextTalk()            .execute().then { x -> x
        .getConference()      }.execute().then { x -> x
        .getCity()            }
}
```

In all 3 cases there was a complication which prevented us from sequencing method calls fluently. In all 3 cases we found the gluing code to get back to fluent composition.

Let's try to generalize this approach. Given some generic container type `WorkflowThatReturns<T>`, we have a method to combine an instance of such workflow with a function which accepts the result of that workflow and returns another workflow back:

```kotlin
class WorkflowThatReturns<T> {
    fun addStep(step: (T) -> WorkflowThatReturns<U>): WorkflowThatReturns<U>
}
```

In case this is hard to grasp, have a look at the picture of what is going on:

![](https://mikhail.io/2018/07/monads-explained-in-csharp-again//monad-bind.png)

An instance of type `T` sits in a generic container.

We call `addStep` with a function, which maps `T` to `U` sitting inside yet another container.

We get an instance of `U` but inside two containers.

Two containers are automatically unwrapped into a single container to get back to the original shape.

Now we are ready to add another step!

In the following code, `nextTalk` returns the first instance inside the container:

```kotlin
fun workflow(speaker: Speaker): WorkflowThatReturns<City> {
    return
        speaker
        .nextTalk()
        .addStep { x -> x.getConference() }
        .addStep { x -> x.getCity() }
}
```

Subsequently, `addStep` is called two times to transfer to `Conference` and then `City` inside the same container:

![](https://mikhail.io/2018/07/monads-explained-in-csharp-again//monad-two-binds.png)

### Finally, Monads

The name of this pattern is **Monad**.

In Arrow terms, a Monad is an interface with two operations: a constructor `just`, and `flatMap`.

```kotlin
interface Monad<F>: Applicative<F>, Functor<F> {
    fun <A> just (instance: A): Kind<F, A>

    fun <A, B> Kind<F, A>.flatMap(f: (A) ->  Kind<F, B>)
}
```

The constructor `just` is used to put an object into container `Kind<F, A>` as described in the [glossary]({{ '/docs/patterns/glossary/#type-constructors' | relative_url }}), `flatMap` is used to replace one contained object with another contained object.

It's important that `flatMaps`'s argument returns `Kind<F, B>` and not just `B`, as this new contained object can have different behavior, like a left branch of `Either` or an async execution of `IO`.

We can think of `flatMap` as a combination of `map` and `flatten` as defined by the following signature:

```kotlin
fun <F, A, B> Kind<F, A>.map(f: (A) ->  B): Kind<F, B> // Inherited from Functor

fun <F, A> Kind<F, Kind<F, A>>.flatten(): Kind<F, A>

container
  .map { x -> just(x + 1) } // Kind<F, Kind<F, Int>>
  .flatten() // Kind<F, Int>
```

Even though I spent quite some time with examples, I expect you to be slightly confused at this point. That's ok.

Keep going and let's have a look at several sample implementations of Monad pattern.

### Option

My first example was with nullable `?`. The full pattern containing either 0 or 1 instance of some type is called Option (it maybe has a value, or maybe not).

Option is another approach to dealing with "no value" value, alternative to the concept of null. You can read more about [`Option`]({{ '/docs/datatypes/option' | relative_url }}) to see how Arrow implemented it.

When null is not allowed, any API contract gets more explicit: either you return type `T` and it's always going to be filled, or you return `Option<T>`.
The client will see that Option type is used, so it will be forced to handle the case of absent value.

Given an imaginary repository contract (which does something with customers and orders):

```kotlin
interface OptionAwareRepository {
    fun getCustomer(id: Int): Option<Customer>

    fun getAddress(id: Int): Option<Address>

    fun getOrder(id: Int): Option<Order>
}
```

The client can be written with `flatMap` method composition, without branching, in fluent style:

```kotlin
fun shipperOfLastOrderOnCurrentAddress(customerId: Int): Option<Shipper> =
    repo.getCustomer(customerId)
        .flatMap { c -> c.address }
        .flatMap { a -> repo.getAddress(a.id) }
        .flatMap { a -> a.lastOrder }
        .flatMap { lo -> repo.getOrder(lo.id) }
        .flatMap { o -> o.shipper }
```

### Sequence can implement Monad

Sequence is an interface for enumerable containers.

You might have used `Sequence<T>` before, but what you probably didn't know is that it's a container type that fills the requirements of a Monad: sequences can be constructed, and they can be flatMapped.

Here's the signature of `flatMap` as defined in the [Kotlin standard library](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/flat-map.html):

```kotlin
fun <T, R> Sequence<T>.flatMap(
    transform: (T) -> Sequence<R>
): Sequence<R>
```

And here is an example of composition:

```kotlin
val shippers: Sequence<Shipper> =
    customers
        .flatMap { c -> c.addresses }
        .flatMap { a -> a.orders }
        .flatMap { o -> o.shippers }
```

The query has no idea about how the collections are stored (encapsulated in containers). We use functions of type `A -> Sequence<B>` to produce new sequences using the `flatMap` operation.

### Deferred (Future)

In the Kotlin coroutines library `Deferred<T>` type is used to denote asynchronous computation which will eventually return an instance of `T`.
The other names for similar concepts in other languages are Promise and Future.

While the typical usage of Deferred in Kotlin is different from the Monad pattern we discussed, I can still come up with a Future class with the familiar structure:

```kotlin
class Future<T>(
    val instance: Deferred<T>
) {
    constructor(instance: T) {
        this.instance = async(LAZY) { instance }
    }

    constructor(instance: Deferred<T>) {
        this.instance = instance
    }

    fun <U> flatMap(func: (T) -> Future<U>): Future<U> =
        Future(async(LAZY) {
            val t = instance.await()
            func(t).await()
        })

    fun runSync(action: (Try<T>) -> Unit) =
        runBlocking { action(Try { instance.await() }) }
}
```

Effectively, it's just a wrapper around the Deferred which doesn't add too much value, but it's a useful illustration because now we can do:

```kotlin
repository
    .loadSpeaker()
    .flatMap { speaker -> speaker.nextTalk()
        .flatMap { talk -> talk.getConference() }
        .flatMap { conference -> conference.getCity().map { it to speaker } }
    }
    .runSync { (city, speaker) -> city.fold(
        { Logger.logError(it); reservations.cancel() },
        { reservations.bookFlight(speaker, city) })
    }
```

We are back to the familiar structure. Time for some more complications.

### Abstraction for all Monads

We're going to dispel one common misconception.
Sometimes the word Monad is used to refer to types like Option, Future, Either... and so on, and that's not correct.
Those are called [datatypes]({{ '/docs/datatypes/intro' | relative_url }}) or just types. Let's see the difference!

As you have seen, neither Future nor Option implement Monad directly.
This is intentional, as you can potentially have several Monad implementations for a single type.
For example, RxJava's Observable can be chained using `flatMap`, `switchMap`, and `concatMap`, and using each is still a Monad.

Instead, Arrow specifies that Monad must be implemented by a separate object or class, referred as the "instance of Monad for type F".

```kotlin
object FutureMonadInstance: Monad<ForFuture> {
    override fun <A> just (instance: A): Future<A> =
        Future(a)

    override fun <A, B> FutureOf<A>.flatMap(f: (A) ->  FutureOf<F, B>): Future<B> =
        flatMap(f) // as per precedence rules the class method is called
}

object OptionMonadInstance: Monad<ForOption> {
    override fun <A> just (instance: A): Option<A> =
        Some(a)

    override fun <A, B> OptionOf<A>.flatMap(f: (A) ->  OptionOf<B>): Option<B> =
        flatMap(f) // as per precedence rules the class method is called
}

object ObservableSwitchMonadInstance: Monad<ForObservable> {
    override fun <A> just (instance: A): Observable<A> =
        Observable.just(a)

    override fun <A, B> ObservableOf<A>.flatMap(f: (A) ->  ObservableOf<B>): Observable<B> =
        switchMap(f)
}

object ObservableConcatMonadInstance: Monad<ForObservable> {
    override fun <A> just (instance: A): Observable<A> =
        Observable.just(a)

    override fun <A, B> ObservableOf<A>.flatMap(f: (A) ->  ObservableOf<B>): Observable<B> =
        concatMap(f)
}
```

What are the benefits of separating the instances from the direct implementation, causing a duplication in methods and an extra layer of indirection?

The main use case is allowing you to write code that is generic for any object that can provide a Monad instance object.

```kotlin
fun <F> Monad<F>.shipperOfLastOrderOnCurrentAddress(customerId: Int): Kind<F, Shipper> =
    repo.getCustomer(customerId)
        .flatMap { c -> c.address }
        .flatMap { a -> repo.getAddress(a.id) }
        .flatMap { a -> a.lastOrder }
        .flatMap { lo -> repo.getOrder(lo.id) }
        .flatMap { o -> o.shipper }
```
In this case, like with any other interface, Monad defines the API and behavior but not the implementation details.
This pattern is specially useful for libraries that must remain agnostic to implementations, and gives them a *lingua franca* when building on top of each other.

Using the Monad and other similar abstractions, Arrow can provide a rich collection of extension functions and new language extensions that can be reused by other codebases.

You can read more about generalizing code in the [glossary]({{ '/docs/patterns/glossary' | relative_url }}) and [typeclasses intro]({{ '/docs/typeclasses/intro' | relative_url }}).

### Non-Sequential Workflows

Up until now, all the composed workflows had very linear, sequential structure: the output of a previous step was always the input for the next step.
That piece of data could be discarded after the first use because it was never needed for later steps:

![](https://mikhail.io/2018/07/monads-explained-in-csharp-again//linear-workflow.png)

Quite often though, this might not be the case. A workflow step might need data from two or more previous steps combined.

In the example above, `bookFlight` method might actually needs both `Speaker` and `City` objects:

![](https://mikhail.io/2018/07/monads-explained-in-csharp-again//non-linear-workflow.png)

In this case, we would have to use a lambda to save the speaker variable until we get a talk too:

```kotlin
repository
    .loadSpeaker()
    .runSync { speaker ->
        speaker
            .nextTalk()
            .flatMap { talk -> talk.getConference() }
            .flatMap { conference -> conference.getCity() }
            .runSync { city -> city.fold(
                { Logger.logError(it); reservations.cancel() },
                { reservations.bookFlight(speaker, city) })
            }
        )
    }
```

Obviously, this gets ugly very soon.

To solve this structural problem the Kotlin language introduced coroutines, the design of which was inspired by other languages such as C# and JavaScript.

If we move back to using Deferred instead of our custom Future, we are able to write

```kotlin
val speaker = repository.loadSpeaker().await()
val talk = speaker.nextTalk().await()
val conference = talk.getConference().await()
val city = conference.getCity().await()
reservations.bookFlight(speaker, city).await()
```

Even though we lost the fluent syntax, at least the block has just one level, which makes it easier to read and navigate.

By using coroutines Arrow provides a specialization that enables readable async/await style code for any Monad.
This specialization can be accessed using the function `binding` on any Monad, and the method `bind`. Internally, `Monad#flatMap` is used for chaining.

```kotlin
fun <F> bookSpeakersFlights(M: Monad<F>): Kind<F, Reservation> =
    M.binding {
        val speaker = repository.loadSpeaker().bind()
        val talk = speaker.nextTalk().bind()
        val conference = talk.getConference().bind()
        val city = conference.getCity().bind()
        reservations.bookFlight(speaker, city).bind()
    }

bookSpeakersFlights(ObservableSwitchMonadInstance).fix() // Observable<Reservation>

bookSpeakersFlights(OptionMonadInstance).fix() // Option<Reservation>

bookSpeakersFlights(ListMonadInstance).fix() // List<Reservation>
```

These are called [Monad Comprehensions]({{ '/docs/patterns/monad_comprehensions' | relative_url }}), and you can find a complete section of the docs explaining it.

### Monad Laws

There are a couple laws that `just` constructor and `flatMap` need to adhere to, so that they produce a Monad with a stable implementation.
These laws are encoded in Arrow as tests you can find in the `arrow-test` module, and are already tested for all instances in the library.

A typical monad tutorial will make a lot of emphasis on the laws, but I find them less important to explain to a beginner. Nonetheless, here they are for the sake of completeness.

`Left Identity law` says that that Monad constructor is a neutral operation: you can safely run it before Bind, and it won't change the result of the function call:

```
// Given
val value: A
val f: (A) -> Kind<F, B>

// Then (== means both parts are equivalent)
just(value).flatMap(f) == f(value)
```

`Right Identity law` says that given a monadic value, wrapping its contained data into another monad of same type and then Binding it, doesn't change the original value:

```
// Given
val monadicValue: Kind<F, A>

// Then (== means both parts are equivalent)
monadicValue.flatMap { x -> just(x) } == monadicValue
```

`Associativity law` means that the order in which Bind operations are composed does not matter:

```
// Given
val m: Kind<F, T>
val f: (T) -> Kind<F, U>
val g: (T) -> Kind<F, V>

// Then (== means both parts are equivalent)
m.flatMap(f).flatMap(g) == m.flatMap { a -> f(a).flatMap(g) }
```

The laws may look complicated, but in fact they are very natural expectations that any developer has when working with monads, so don't spend too much mental effort on memorizing them.

### Conclusion

You should not be afraid of the "M-word" just because you are a Kotlin programmer.

Kotlin does not have a notion of monads as predefined language constructs, but that doesn't mean we can't borrow some ideas from the functional world.
Having said that, it's also true that Kotlin is lacking some powerful ways to combine and generalize monads that are available in functional programming languages.
