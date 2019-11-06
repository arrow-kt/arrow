---
layout: docs-core
title: Ior
permalink: /docs/arrow/data/ior/
redirect_from:
  - /docs/datatypes/ior/
---

## Ior

{:.beginner}
beginner

`Ior` represents an inclusive-or relationship between two data types.
This makes it very similar to the [`Either`](/docs/arrow/core/either) data type, which represents an "exclusive-or" relationship.
An `Ior<A, B>` (also written as `A Ior B`) can contain either an `A`, a `B`, or both.
Another similarity to `Either` is that `Ior` is right-biased,
which means that the `map` and `flatMap` functions will work on the right side of the `Ior`, in our case the `B` value.
You can see this in the function signature of `map`:

```kotlin
fun <D> map(f: (B) -> D): Ior<A, D>
```

We can create `Ior` values using `Ior.Left`, `Ior.Right`, and `Ior.Both`:

```kotlin:ank
import arrow.*
import arrow.core.*

Ior.Right(42)
```

```kotlin:ank
Ior.Left("Error")
```

```kotlin:ank
Ior.Both("Warning", 41)
```

Arrow also offers extension functions for `Ior`, the `leftIor`, `rightIor`, and `bothIor`:

```kotlin:ank
3.rightIor()
```

```kotlin:ank
"Error".leftIor()
```

```kotlin:ank
("Warning" to 3).bothIor()
```


When we look at the `Monad` or `Applicative` instances of `Ior`, we can see that they actually require a `Semigroup` instance on the left side.
This is because `Ior` will actually accumulate failures on the left side, very similarly to how the [`Validated`](/docs/arrow/data/validated) data type does.
This means we can accumulate data on the left side, while also being able to short-circuit upon the first right-side-only value.
For example, we might want to accumulate warnings together with a valid result, and only halt the computation on a "hard error."
Here's an example of how we are able to do that:

```kotlin
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
        binding<Nel<String>> {
            val (username) = validateUsername(name)
            val (password) = validatePassword(pass)
            User(username, password)
        }.fix()
```

Now we're able to validate user data and also accumulate non-fatal warnings:

```kotlin
validateUser("John", "password12")
//Right(value=User(name=John, pw=password12))
```

```kotlin
validateUser("john.doe", "password")
//Both(leftValue=NonEmptyList(all=[Dot in name is deprecated, Password should be longer]), rightValue=User(name=john.doe, pw=password))
```

```kotlin
validateUser("jane", "short")
//Left(value=NonEmptyList(all=[Password too short]))
```

To extract the values, we can use the `fold` method, which expects a function for each case the `Ior` can represent:

```kotlin
validateUser("john.doe", "password").fold(
        { "Error: ${it.head}" },
        { "Success $it" },
        { warnings, (name) -> "Warning: $name; The following warnings occurred: ${warnings.show()}" }
)
//Warning: john.doe; The following warnings occurred: Dot in name is deprecated, Password should be longer
```
Similar to [Validated](/docs/arrow/data/validated), there is also a type alias for using a `NonEmptyList` on the left side.

```kotlin
typealias IorNel<A, B> = Ior<Nel<A>, B>
```

```kotlin:ank
Ior.leftNel<String, Int>("Error")
```

```kotlin:ank
Ior.bothNel("Warning", 41)
```

We can also convert our `Ior` to `Either`, `Validated`, or `Option`.
All of these conversions will discard the left side value if both are available:

```kotlin:ank
Ior.Both("Warning", 41).toEither()
```

```kotlin:ank
Ior.Both("Warning", 41).toValidated()
```

```kotlin:ank
Ior.Both("Warning", 41).toOption()
```

### Supported type classes

```kotlin:ank:replace
import arrow.reflect.*
import arrow.core.*

DataType(Ior::class).tcMarkdownList()
```

## Credits

Contents partially adapted from [Cats Ior](https://typelevel.org/cats/datatypes/ior.html)
