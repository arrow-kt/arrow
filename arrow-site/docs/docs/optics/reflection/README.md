---
layout: docs-optics
title: Index
permalink: /optics/reflection/
---

## Usage with reflection

Although we strongly recommend generating optics using the [DSL and `@optics` attribute]({{ '/optics/dsl/' | relative_url }}), sometimes this is not possible. For those scenarios we provide a small utility package `arrow-optics-reflect` which bridges Arrow Optics with [Kotlin's reflection](https://kotlinlang.org/docs/reflection.html) capabilities.

Kotlin provides a simple way to obtain a reference to a member of a class, by using `ClassName::memberName`. For example, given the following class definition:

```kotlin
data class Person(val name: String, val friends: List<String>)
```

we can use `Person::name` and `Person::friends` to refer to each of the fields in the class. Those references are very similar to optics.

In fact, what `arrow-optics-reflect` does is provide extension methods which turn those references into optics. You can obtain a lens for the `name` field in `Person` by writing:

```kotlin
Person::name.lens
```

which you can later use as [any other lens]({{ '/optics/lens' | relative_url }}):

```kotlin
val p = Person("me", listOf("pat", "mat"))
val m = Person::name.lens.modify(p) { it.capitalize() }
```

⚠️ **WARNING**: this only works on `data` classes with a public `copy` method (which is the default.) Remember that, as opposed to a mutable variable, optics will always create a _new_ copy when asking for modification.

### Nullables and collections

Sometimes it's preferable to expose a field using a different optic:

- When the type of the field is nullable, you can use `optional` to obtain an [optional]({{ '/optics/optional' | relative_url }}) instead of a lens.
- When the type of the field is a collection, you can use `iter` to obtain _read-only_ access to it (technically, you obtain a [fold]({{ '/optics/fold' | relative_url }}).) If the type is a subclass of `List`, you can use `every` to get read/write access.

```kotlin
val p = Person("me", listOf("pat", "mat"))
val m = Person::friends.every.modify(p) { it.capitalize() }
```

### Prisms

A common pattern in Kotlin programming is to define a sealed abstract class (or interface) with subclasses representing choices in a union.

```kotlin
sealed interface Cutlery
object Fork: Cutlery
object Spoon: Cutlery
```

We provide an `instance` method which creates a [prism]({{ '/optics/prism' | relative_url }}) which focus only on a certain subclass of a parent class. Both ends are important and must be provided when creating the optic:

```kotlin
instance<Cutlery, Fork>()
```

You can compose this optic freely with others. Here's an example in which we obtain the number of forks in a list of cutlery using optics:

```kotlin
val things = listOf(Fork, Spoon, Fork)
val forks = Every.list<Cutlery>() compose instance<Cutlery, Fork>()
val noOfForks = forks.size(things)
```