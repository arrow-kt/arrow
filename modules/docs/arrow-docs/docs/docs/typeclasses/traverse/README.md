---
layout: docs
title: Traverse
permalink: /docs/typeclasses/traverse/
---

## Traverse

{:.intermediate}
intermediate

`Traverse` is a type class also known as `Traversable` and is used to perform traversals over an structure with an effect.

We will see some of the use cases for Traverse. Let's start by looking for an example where the side effects are modeled as data types. Side effects in functional programming are changes outside of the scope of the function, for example, performing some IO operation, modifying global variables, etc...

In Kotlin with Arrow, these aforementioned data types can be modeled as [Async](../datatypes/option/README.md) for missing values, [Either](../datatypes/either/README.md) and [Validated](../datatypes/validated/README.md) for things that could either provide a valid result or give an error, and [IO](../effects/io/README.md), [Async](../effects/async/README.md) for asynchronous computations.


Let's show some examples that will need the following imports.

```kotlin:ank:silent
import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.effects.*
import arrow.effects.deferredk.applicative.applicative
import arrow.instances.either.applicative.applicative
import arrow.instances.either.applicativeError.catch
import java.lang.NumberFormatException
import arrow.instances.list.applicative.*
import arrow.instances.list.foldable.sequence_
import arrow.instances.list.foldable.traverse_
import arrow.instances.list.traverse.sequence
import arrow.instances.list.traverse.traverse
import arrow.instances.validated.applicative.applicative
import arrow.instances.nonemptylist.semigroup.semigroup
import arrow.instances.option.applicative.applicative
import kotlinx.coroutines.*
```

Next, we will define some data classes and functions to show case different examples of traverse below.

```kotlin:ank:silent
sealed class SecurityError {
    data class RuntimeSecurityError(val cause: String) : SecurityError()
}

interface Credential
data class Profile(val id: String)
data class User(val id: String, val name: String)
```

`parseInt`: is a function that will try to convert an `String` parameter `s` to an `Int`, it if succeeds it will return the number inside `Some`. If it fails, it will return `None`.

 `validateLogin`: This function can be something that could either fail or be a successful operation when validating login credentials */

`userInfo`: This function could be something that could return a Profile asynchronously.

```kotlin:ank:silent
interface SideEffectingFunctions {
    fun parseInt(s: String): Option<Int> =
            Try { s.toInt() }.fold(ifFailure = { None }, ifSuccess = { v -> Some(v) })

    fun validateLogin(cred: Credential): Either<SecurityError, Unit>

    fun userInfo(user: User): DeferredK<Profile>
}
```

As we can see, every function above only takes as parameter the argument to perform its operation on.

This is just a mock implementation of the previous interface, where we simulate results of a side effect performed by an external system. We'll make good use of it later.

```kotlin:ank:silent
object ValidEffects : SideEffectingFunctions {

    override fun validateLogin(cred: Credential): Either<SecurityError, Unit> {
        return Either.right(Unit)
    }

    override fun userInfo(user: User): DeferredK<Profile> {
        return DeferredK.async { Profile(id = user.id) }  // assuming profile details successfully fetched
    }

    fun savingProfiles(): DeferredK<Unit>  = GlobalScope.async(Dispatchers.Default, CoroutineStart.LAZY){Unit}.k() // assuming saving profiles successfully

}
```

We just defined results for our functions for the happy case, or in other words, when everything went "right". The next mock for our `SideEffectingFunctions` simulates the result when something goes "wrong".

```kotlin:ank:silent
object ErrorEffects : SideEffectingFunctions {

    override fun validateLogin(cred: Credential): Either<SecurityError, Unit> {
        return Either.left(SecurityError.RuntimeSecurityError("Invalid credentials"))
    }

    override fun userInfo(user: User): DeferredK<Profile> {
        return DeferredK.failed(Throwable("Error retrieving profile"))
    }
}
```

If we need to extract the profile information for a List of Users we can create a function that reuses the `userInfo` function defined previously.

```kotlin:ank
fun profilesFor(users: List<User>): List<DeferredK<Profile>> = users.map { u -> ValidEffects.userInfo(u) }
```

Notice how we are returning a List of deferred computations. It would be nice for the ones using this function if we could aggregate the results and return the List of Profile under a single DeferredK, something like `DeferredK<List<Profile>>`

To be able to do this transformation we have the `Traverse` type class.


`Traverse` is defined with the following signature:

interface Traverse<F> : Functor<F>, Foldable<F> {
  fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>
}

For our example, F would be `List` (the initial container), G would be the data type representing the side effect: [Async](../datatypes/option/README.md), [Either](../datatypes/either/README.md) or `DeferredK`.

So, if we have a `List<User>` (the ones we want to obtain their profiles) and a function `User -> DeferredK<Profile>`, with traverse we can transform and instead of obtaining a `List<DeferredK, Profile>` it can aggregate all the results to obtain a single `DeferredK<List,Profile>`.

In this case, `traverse` can go over the collection, apply the function and aggregate the resulting values (with side effects) in a `List`.

Basically, `F` is some sort of context which may contain a value. We are using List in the example, but there are `Traverse` implementations for [Async](../datatypes/option/README.md), [Either](../datatypes/either/README.md) or [Validated](../datatypes/validated/README.md).

Let's see another example for further clarification.

```kotlin:ank
fun parseIntEither(s: String): Either<NumberFormatException, Int> =
  catch(
    { NumberFormatException("Error converting $s to Int") },
    { s.toInt() }
  ).fix()

fun parseIntValidated(s: String): ValidatedNel<NumberFormatException, Int> =
  Validated.fromEither(parseIntEither(s)).toValidatedNel()
```

Example of what these functions do:

```kotlin:ank
parseIntEither("1")
parseIntEither("jimmy")
```

We can use these two functions to traverse a collection containing strings, converting them to integers and accumulating the errors with Either or Validated.
Examples of going through a list with `map` and with `traverse` for [Either](../datatypes/either/README.md):

```kotlin:ank
val listOfValidNumbers = listOf("1", "2", "3")
val listOfInvalidNumbers = listOf("1", "jimmy", "peter")

listOfValidNumbers.map { s -> parseIntEither(s) }

listOfValidNumbers.traverse(
  Either.applicative()
) { element -> parseIntEither(element) }

listOfInvalidNumbers.traverse(
  Either.applicative()
) { element -> parseIntEither(element) }
```

Examples of going through the list with `traverse` for [Validated](../datatypes/validated/README.md):

```kotlin:ank
listOfValidNumbers.traverse(
  ValidatedNel.applicative(Nel.semigroup<NumberFormatException>())) { element -> parseIntValidated(element)}

listOfInvalidNumbers.traverse(
  ValidatedNel.applicative(Nel.semigroup<NumberFormatException>())) { element -> parseIntValidated(element)}
```

Let's explain what's going on here when traversing a `list` with [Validated](../datatypes/validated/README.md).
We are using an `Applicative` instance of `ValidatedNel`, and for that we need to provide a "proof" that the non-empty-list (Nel) is a [Semigroup](../typeclasses/semigroup/README.md).
The `Applicative` typeclass instance for `ValidatedNel` allows to run independent computations. And the [Semigroup](../typeclasses/semigroup/README.md) typeclass instance allows us to combine elements of the same type, in this case, it helps `ValidatedNel` with the task of accumulating the errors.

If you want to see other example, you could visit: https://www.enhan.eu/how-to-in-fp/


## sequence

When we want to traverse a collection that each of its elements already contains an effect, for example, List<Option<A>>, we may want to convert it to Option<List<A>> to work easily with the elements. To do that we could traverse the list applying the `::identity` transformation function to each one of the elements.

```kotlin:ank
val listofOptionalNumbers: List<Option<Int>> = listOf(Option(1), Option(2), Option(3))

listofOptionalNumbers.traverse(Option.applicative(), ::identity)
```

**The equivalent to do a traverse applying identity is using sequence.**

```kotlin:ank
val sequenceOptions = listofOptionalNumbers.sequence(Option.applicative())
```

We could also use sequence on a list of `Either`.

```kotlin:ank
val listOfEither: List<Either<NumberFormatException, Int>> = listOfValidNumbers.map { s -> parseIntEither(s) }

listOfEither.sequence(Either.applicative<NumberFormatException>())
```

## traverse_ and sequence_

Another usage for `sequence` is when we are traversing a list of data to which we apply some effectful function and we do not care about the returned values.

Continuing with our first example, imagine a function `saveProfile` that perform a side effect, saving a profile in a database, and returning `Unit` asynchronously.

```kotlin:ank
fun saveProfile(user: User): DeferredK<Unit> = ValidEffects.savingProfiles()
```

If we apply traverse, we will have an Asynchronous computation result with a List of Unit that we do not care about.

```kotlin:ank
fun saveProfiles(users: List<User>): Kind<ForDeferredK, Kind<ForListK, Unit>> = users.traverse(DeferredK.applicative(), { user -> saveProfile(user)})
```

We would just prefer to have an Unit as a result since that would convey the same information.

Traversing solely for the sake of the effect (ignoring any values that may be produced, Unit or otherwise) is common, so Foldable (superclass of Traverse) provides `traverse_` and `sequence_` methods that do the same thing as `traverse` and `sequence` but ignores any value produced along the way, returning Unit at the end.

```kotlin:ank
val listOfUsers = listOf(User("1","Jimmy"), User("2","Peter"), User("3","Rob"))
val result: Kind<ForDeferredK, Kind<ForListK, Unit>> = saveProfiles(listOfUsers)
result.fix().unsafeRunSync()
```

In the example above, result will hold an asynchronous computation with a list of effectful results, a list of Unit.

result.fix().unsafeRunSync()) is just to force the computation of the asynchronous operation and see the result of the example.

That should return a ListK(list=[kotlin.Unit, kotlin.Unit, kotlin.Unit])

Let's see what we would get with `traverse_`:

```kotlin:ank
val l = listOfUsers.traverse_(DeferredK.applicative(), {user -> saveProfile(user)})
l.fix().unsafeRunSync()
```

That should return kotlin.Unit

Now, if we have a list already containing effects with results we do not care about: listOfAsyncResults, we can apply `sequence_` to aggregate the result.

```kotlin:ank
val listOfAsyncResults = listOfUsers.map { u -> saveProfile(u)}
listOfAsyncResults.sequence_(DeferredK.applicative())
```

### Data types

```kotlin:ank:replace
import arrow.reflect.*
import arrow.typeclasses.Traverse

TypeClass(Traverse::class).dtMarkdownList()
```

### Hierarchy

<canvas id="hierarchy-diagram"></canvas>
<script>
  drawNomNomlDiagram('hierarchy-diagram', 'diagram.nomnol')
</script>

```kotlin:ank:outFile(diagram.nomnol)
import arrow.reflect.*
import arrow.typeclasses.Traverse

TypeClass(Traverse::class).hierarchyGraph()
```
