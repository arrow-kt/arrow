---
layout: docs
title: Ior
permalink: /docs/datatypes/ior/
---

## Ior 

`Ior` represents an inclusive-or relationship between two data types.
This makes it very similar to the [`Either`](/docs/datatypes/either) data type, which represents an "exclusive-or" relationship.
What this means, is that an `Ior<A, B>` (also written as `A Ior B`) can contain either an `A`, a `B`, or both an `A` and `B`.
Another similarity to `Either` is that `Ior` is right-biased,
which means that the `map` and `flatMap` functions will work on the right side of the `Ior`, in our case the `B` value.
You can see this in the function signature of `map`:

```kotlin
fun <D> map(f: (B) -> D): Ior<A, D>
```

We can create `Ior` values using `Ior.left`, `Ior.right` and `Ior.both`:

```kotlin:ank
import kategory.*

val right = Ior.Right(3)

val left = Ior.Left("Error")

val both = Ior.Both("Warning", 3)
```

Kategory also offers extension functions for `Ior`, the `leftIor`, `rightIor` and `bothIor`:

```kotlin:ank
import kategory.*

val right = 3.rightIor()

val left = "Error".leftIor()

val both = ("Warning" to 3).bothIor()
```


When we look at the `Monad` or `Applicative` instances of `Ior`, we can see that they actually requires a `Semigroup` instance on the left side.
This is because `Ior` will actually accumulate failures on the left side, very similar to how the [`Validated`](/docs/datatypes/validated) data type does.
This means we can accumulate data on the left side while also being able to short-circuit upon the first right-side-only value.
For example, sometimes, we might want to accumulate warnings together with a valid result and only halt the computation on a "hard error"
Here's an example of how we might be able to do that:

```kotlin:ank
import kategory.*

data class User(val name: String, val pw: String)

fun validateUsername(username: String) = when {
    username.isEmpty() -> Nel.of("Can't be empty").leftIor()
    username.contains(".") -> (Nel.of("Dot in name is deprecated") to username).bothIor()
    else -> username.rightIor()
}


fun validatePassword(password: String) = when {
    password.length < 8 -> Nel.of("Password too short").leftIor()
    password.length < 10 -> (Nel.of("Password should be longer") to password).bothIor()
    else -> password.rightIor()
}

fun validateUser(name: String, pass: String) =
        Ior.monad<Nel<String>>().binding {
            val username = validateUsername(name).bind()
            val password = validatePassword(pass).bind()
            yields(User(username, password))
        }

```

Now we're able to validate user data and also accumulate non-fatal warnings:

```kotlin:ank

validateUser("John", "password12")

validateUser("john.doe", "password")

validateUser("jane", "short")

```

To extract the values, we can use the `fold` method, which expects a function for each case the `Ior` can represent:

```kotlin:ank

validateUser("john.doe", "password").fold(
        { "Error: ${it.head}" },
        { "Success $it"},
        { warnings, (name) -> "Warning: $name; The following warnings occurred: ${warnings.show()}" }
)

```
Similar to [Validated](/docs/datatypes/validated), there is also a type alias for using a `NonEmptyList` on the left side.

```kotlin
typealias IorNel<A, B> = Ior<Nel<A>, B>
```


```kotlin:ank

val left: IorNel<String, Int> = Ior.leftNel("Error")

val both: IorNel<String, Int> = Ior.bothNel("Warning", 1)

```


We can also convert our `Ior` to `Either`, `Validated` or `Option`.
All of these conversions will discard the left side value if both are available:

```kotlin:ank
Ior.Both("Warning", 42).toEither()
```