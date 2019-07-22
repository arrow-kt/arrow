---
layout: docs
title: Traverse
permalink: /docs/arrow/typeclasses/traverse/
redirect_from:
  - /docs/typeclasses/traverse/
---

## Traverse

{:.intermediate}
intermediate

In functional programming it is very common to encode "effects" as data types - common effects include Option for possibly missing values, Either and Validated for possible errors, and Future for asynchronous computations.

These effects tend to show up in functions working on a single piece of data - for instance parsing a single String into an Int, validating a login, or asynchronously fetching website information for a user.

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.Option
import java.util.concurrent.Future

//sampleStart
interface Profile
interface User

fun parseInt(s: String): Option<Int> = TODO()
data class SecurityError(val message: String)
data class Credentials(val email: String, val password: String)

fun validateLogin(c: Credentials): Either<SecurityError, Unit> = TODO()

fun userInfo(u: User): Future<Profile> = TODO()
//sampleEnd
```

Each function asks only for the data it actually needs; in the case of userInfo, a single User. We certainly could write one that takes a List[User] and fetch profile for all of them, would be a bit strange. If we just wanted to fetch the profile of just one user, we would either have to wrap it in a List or write a separate function that takes in a single user anyways. More fundamentally, functional programming is about building lots of small, independent pieces and composing them to make larger and larger pieces - does this hold true in this case?

Given just User => Future[Profile], what should we do if we want to fetch profiles for a List[User]? We could try familiar combinators like map.

```kotlin:ank:playground
import arrow.core.Either
import arrow.core.Option
import java.util.concurrent.Future

interface Profile
interface User

fun parseInt(s: String): Option<Int> = TODO()
data class SecurityError(val message: String)
data class Credentials(val email: String, val password: String)

fun validateLogin(c: Credentials): Either<SecurityError, Unit> = TODO()
fun userInfo(u: User): Future<Profile> = TODO()
//sampleStart
fun profilesFor(users:  List<User>): List<Future<Profile>> = users.map(::userInfo)
//sampleEnd
```

Note the return type List<Future<Profile>>. This makes sense given the type signatures, but seems unwieldy. We now have a list of asynchronous values, and to work with those values we must then use the combinators on Future for every single one. It would be nicer instead if we could get the aggregate result in a single Future, say a Future<List<Profile>>.

As it turns out, the Future companion object has a traverse method on it. However, that method is specialized to standard library collections and Futures - there exists a much more generalized form that would allow us to parse a List<String> or validate credentials for a List<User>.

Enter Traverse.

### The Typeclass
At center stage of Traverse is the traverse method.

```kotlin:ank:playground
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

interface Traverse<F> : Functor<F>, Foldable<F> {
  fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>
}
```

In our above example, F is List, and G is Option, Either, or Future. For the profile example, traverse says given a List<User> and a function User -> Future<Profile>, it can give you a Future<List<Profile>>.

Abstracting away the G (still imagining F to be List), traverse says given a collection of data, and a function that takes a piece of data and returns an effectful value, it will traverse the collection, applying the function and aggregating the effectful values (in a List) as it goes.

In the most general form, Kind<F, A> is some sort of context which may contain a value (or several). While List tends to be among the most general cases, there also exist Traverse instances for Option, Either, and Validated (among others).

#### Choose your effect

The type signature of Traverse appears highly abstract, and indeed it is - what traverse does as it walks the Kind<F, A> depends on the effect of the function. Let's see some examples where F is taken to be List.

```kotlin:ank:playground
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.toT
import arrow.core.validNel
import arrow.core.Either
//sampleStart
fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))
    
fun parse(s: String): ValidatedNel<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
  else NumberFormatException("$s is not a valid integer.").invalidNel()
//sampleEnd
```
We can now traverse structures that contain strings parsing them into integers and accumulating failures with Either.

```kotlin:ank:playground
import arrow.core.ValidatedNel
import arrow.core.invalidNel
import arrow.core.toT
import arrow.core.validNel
import arrow.core.Either

fun parse(s: String): Either<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) Either.Right(s.toInt())
  else Either.Left(NumberFormatException("$s is not a valid integer."))
    
fun parse(s: String): ValidatedNel<NumberFormatException, Int> =
  if (s.matches(Regex("-?[0-9]+"))) s.toInt().validNel()
  else NumberFormatException("$s is not a valid integer.").invalidNel()
//sampleStart


//sampleEnd

fun main(){
  println(
}
```

### Data types

```kotlin:ank:replace
import arrow.reflect.DataType
import arrow.reflect.tcMarkdownList
import arrow.typeclasses.Traverse

TypeClass(Traverse::class).dtMarkdownList()
```

ank_macro_hierarchy(arrow.typeclasses.Traverse)
