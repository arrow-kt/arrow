---
layout: docs
title: Traverse
permalink: /docs/typeclasses/traverse/
---

## Traverse

{:.intermediate}
intermediate

`Traverse`, also known as `Traversable` is a Typeclass used to perform traversals over an structure with an effect.

We will see some of the use cases for Traverse. Let's start by looking for an example where the side effects are modeled as data types. Side effects in functional programming are changes outside of the scope of the function, for example, performing some IO operation, modifying global variables, etc...

In Kotlin with Arrow, these aforementioned data types can be modeled as `Option` for missing values, `Either` and `Validated` for things that could either provide a right result or give an error, and `IO`, `Async` for asynchronous computations.


Let's show some examples that will need the following imports.

```kotlin:ank:silent
import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.effects.*
import arrow.effects.deferredk.applicative.applicative
import arrow.instances.either.applicative.applicative
import arrow.instances.either.applicativeError.applicativeError
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

`parseInt`: is a function that will try to convert an `String` parameter `s` to an `Int`, it if succeeds it will return the number inside the `Some`. If it fails, it will return `None`.

 `validateLogin`: This function can be something that could either fail or be a sauccessful operation when validating login credentials */

`userInfo`: This function could be something that could return a Profile asynchronously.

```kotlin:ank:silent
interface OurSideEffectsFunctions {
    fun parseInt(s: String): Option<Int> =
            Try { s.toInt() }.fold(ifFailure = { None }, ifSuccess = { v -> Some(v) })

    fun validateLogin(cred: Credential): Either<SecurityError, Unit>

    fun userInfo(user: User): DeferredK<Profile>
}
```

As we can see, every function above only takes as parameter the argument to perform its operation on.

The next objects are just for mocking and simulate results of a side effect performed by an external system so we can see the output of our examples.

```kotlin:ank:silent
object ValidEffects : OurSideEffectsFunctions {

    override fun validateLogin(cred: Credential): Either<SecurityError, Unit> {
        return Either.right(Unit)
    }

    override fun userInfo(user: User): DeferredK<Profile> {
        return DeferredK.async { Profile(id = user.id) }
    }

    fun savingProfiles(): DeferredK<Unit>  = GlobalScope.async(Dispatchers.Default, CoroutineStart.LAZY){Unit}.k()

}
```

We just defined results for our functions when everything went "right", the next object is also just for simulating the result when something goes "wrong".

```kotlin:ank:silent
object ErrorEffects : OurSideEffectsFunctions {

    override fun validateLogin(cred: Credential): Either<SecurityError, Unit> {
        return Either.left(SecurityError.RuntimeSecurityError("Invalid credentials"))
    }

    override fun userInfo(user: User): DeferredK<Profile> {
        return DeferredK.failed(Throwable("Error retrieving profile"))
    }
}
```

If you want to run these examples you can run them inside a main body.

```kotlin:ank:silent
fun main(args: Array<String>) {

}
```

If we need to extract the information for a List of Users we can create a function that composes with the function defined previously `userInfo`.

```kotlin:ank
fun profilesFor(users: List<User>): List<DeferredK<Profile>> = users.map { u -> ValidEffects.userInfo(u) }
```

Notice how we are returning a List of deferred computations. It would be nice for the ones using this function if we could aggregate the results and return the List of Profile under a single DeferredK, something like `DeferredK<List<Profile>>`

To be able to do this transformation we have the `Traverse` type class.


`Traverse` is defined with the following signature:

interface Traverse<F> : Functor<F>, Foldable<F> {
  fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>
}

For our example, F would be `List` (the initial container), G would be the data type representing the side effect: `Option`, `Either` or `DeferredK`.

So, if we have a `List<User>` (the ones we want to obtain their profiles) and a function `User -> DeferredK<Profile>`, with traverse we can transform and instead of obtaining a `List<DeferredK, Profile>` it can aggregate all the results to obtain a single `DeferredK<List,Profile>`.

In this case, `traverse` can go over the collection, apply the function and aggregate the resulting values (with side effects) in a `List`.

Basically, `F` is some sort of context which may contain a value. We are using List in the example, but there are `Traverse` implementations for `Option`, `Either` or `Validated`.

Let's see another example further clarifying this.

```kotlin:ank
fun parseIntEither(s: String): Either<NumberFormatException, Int> =
  Either.applicativeError<NumberFormatException>().catch(
    { NumberFormatException("Error converting $s to Int") },
    { s.toInt() }
  ).fix()

fun parseIntValidated(s: String): ValidatedNel<NumberFormatException, Int> =
  Validated.fromEither(parseIntEither(s)).toValidatedNel()
```

We can use these two functions to traverse a collection containing strings, converting them to integers and accumulating the errors with Either or Validated.

Example of what these functions do:

```kotlin:ank
parseIntEither("1")
parseIntEither("jimmy")
```

Examples of going through a list with `map` and with `traverse` for `Either`:

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

Examples of going through the list with `traverse` for `Validated`:

```kotlin:ank
listOfValidNumbers.traverse(
  ValidatedNel.applicative(Nel.semigroup<NumberFormatException>())) { element -> parseIntValidated(element)}

listOfInvalidNumbers.traverse(
  ValidatedNel.applicative(Nel.semigroup<NumberFormatException>())) { element -> parseIntValidated(element)}
```

Let's explain what's going on here when traversing a `list` with `Validated`.
We are using an `Applicative` instance of `ValidatedNel`, and for that we need to provide a "proof" that the non-empty-list (Nel) is a `SemiGroup`.
The `Applicative` typeclass instance for `ValidatedNel` allows to run independent computations. And the `SemiGroup` typeclass instance allows us to combine elements of the same type, in this case, it helps `ValidatedNel` with the task of accumulating the errors.

If you want to see other example, you could visit: https://www.enhan.eu/how-to-in-fp/


## Sequence

When we want to traverse a collection that each of its elements already contains an effect, for example, List<Option<A>>, we may want to convert it to Option<List<A>> to work easily with the elements. To do that we could traverse the list applying the identity function.

```kotlin:ank
val listofOptionalNumbers: List<Option<Int>> = listOf(Option(1), Option(2), Option(3))

listofOptionalNumbers.traverse(Option.applicative(), ::identity)
```

The equivalent to do a traverse applying identity is using sequence.

```kotlin:ank
val sequenceOptions = listofOptionalNumbers.sequence(Option.applicative())
```

We could also use sequence on a list of Either.

```kotlin:ank
val listOfEither: List<Either<NumberFormatException, Int>> = listOfValidNumbers.map { s -> parseIntEither(s) }

listOfEither.sequence(Either.applicative<NumberFormatException>())
```

## traverse_ and sequence_

Another usage for sequence is when we are traversing a list of data to which we apply some effectful function and we do not care about the returned values.

Continuing with our first example, imagine a function saveProfile that perform a side effect, saving a profile in a database, and returning Unit asynchronously.

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

### Data Types

The following data types in Arrow provide instances that adhere to the `Traverse` type class.

- [Either]({{ '/docs/datatypes/either' | relative_url }})
- [EitherT]({{ '/docs/datatypes/eithert' | relative_url }})
- [Id]({{ '/docs/datatypes/id' | relative_url }})
- [Ior]({{ '/docs/datatypes/ior' | relative_url }})
- [NonEmptyList]({{ '/docs/datatypes/nonemptylist' | relative_url }})
- [Option]({{ '/docs/datatypes/option' | relative_url }})
- [OptionT]({{ '/docs/datatypes/optiont' | relative_url }})
- [SequenceK]({{ '/docs/datatypes/sequencek' | relative_url }})
- [Try]({{ '/docs/datatypes/try' | relative_url }})
- [Validated]({{ '/docs/datatypes/validated' | relative_url }})
