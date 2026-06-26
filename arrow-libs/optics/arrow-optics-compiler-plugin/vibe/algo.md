# How Arrow Optics generates code

This document describes, in precise detail, the algorithm used to generate Arrow
Optics code from classes annotated with `@optics`. It is written purely in terms
of *what code is produced from what input* — the program-analysis machinery used
to inspect the source classes is deliberately omitted.

Throughout, the **source class** is the class carrying the `@optics` annotation,
a **focus** is a single thing an optic points at (a constructor parameter, an
abstract property, or a sealed subclass), and the **companion** is the source
class's companion object.

---

## Table of contents

1. [High-level model](#1-high-level-model)
2. [The `@optics` annotation and target selection](#2-the-optics-annotation-and-target-selection)
3. [Conventions shared by every generator](#3-conventions-shared-by-every-generator)
4. [ISO — value classes](#4-iso--value-classes)
5. [LENS](#5-lens)
6. [PRISM — sealed classes and interfaces](#6-prism--sealed-classes-and-interfaces)
7. [OPTIONAL — nullable foci](#7-optional--nullable-foci)
8. [DSL — composition extensions](#8-dsl--composition-extensions)
9. [COPY — the `@optics.copy` builder](#9-copy--the-opticscopy-builder)
10. [Behaviour by kind of class](#10-behaviour-by-kind-of-class)
11. [Generics and variance, consolidated](#11-generics-and-variance-consolidated)
12. [Diagnostics and failure modes](#12-diagnostics-and-failure-modes)
13. [Known limitations and vestigial behaviour](#13-known-limitations-and-vestigial-behaviour)

---

## 1. High-level model

For each annotated source class the generator produces **one source file** that
contains a set of *top-level declarations*. Every generated optic is an
**extension member on the source class's companion object** (e.g.
`val Person.Companion.age: Lens<Person, Int>`), which is why a companion object
is required.

The pipeline is:

1. Determine the **set of targets** to generate for the class (ISO, LENS, PRISM,
   DSL, COPY) from the annotation arguments, intersected with what the class's
   *kind* (data / value / sealed) supports.
2. For each target, extract its **foci** from the class structure.
3. For each target, render a **snippet** (a package, a set of imports, and a body
   of declarations).
4. Group all snippets that share the same package and same enclosing-name, join
   them (concatenating bodies and unioning imports), and emit one file per group.

The five user-facing optic "cases" map onto generation as follows. Note that
**`OPTIONAL` is not a standalone generator**: optionals arise from a lens onto a
nullable focus combined with the library combinator `notNull` (see §7).

| Case      | Produced by                              | Applies to                              |
|-----------|------------------------------------------|-----------------------------------------|
| ISO       | iso generator                            | value classes                           |
| LENS      | lens generator                           | data classes; sealed types (shared properties) |
| PRISM     | prism generator                          | sealed classes / interfaces             |
| OPTIONAL  | *no generator* — lens-to-nullable + `notNull` | nullable foci of the above         |
| DSL       | DSL generator (iso/lens/prism variants)  | value / data / sealed                   |
| (COPY)    | copy generator (opt-in via `@optics.copy`) | data classes                          |

---

## 2. The `@optics` annotation and target selection

### 2.1 The annotation

```kotlin
annotation class optics(val targets: Array<OpticsTarget> = emptyArray()) {
  annotation class copy()
}
enum class OpticsTarget { ISO, LENS, PRISM, OPTIONAL, DSL }
```

* `@optics` with no arguments means *"generate everything that matches this
  class's kind"*.
* `@optics([OpticsTarget.LENS, OpticsTarget.DSL])` restricts generation to the
  listed targets (still intersected with what the kind supports).
* `@optics.copy` is a separate marker that additionally requests the `copy`
  builder (§9).

### 2.2 Eligible classes

Only **data classes**, **value classes** (`@JvmInline value class`), and **sealed
classes / sealed interfaces** may be annotated. Any other kind of class is a
hard error ("Only data, sealed, and value classes can be annotated with
@optics") and nothing is generated.

A **companion object must be declared** on the source class; otherwise it is a
hard error ("must declare a companion object"). (This check can be disabled by a
configuration flag, in which case the user is responsible for supplying a
companion; the default is on.)

### 2.3 How the target set is computed

First the annotation's `targets` array is read. Each entry is mapped to an
internal target; **`ISO`, `LENS`, `PRISM`, `DSL` are recognised, and `OPTIONAL`
is silently dropped** (it has no dedicated generator). If the resulting set is
empty (the `@optics()` no-arg case), it defaults to `{ISO, LENS, PRISM, DSL}`.

That set is then **intersected with the targets allowed for the class's kind**:

| Class kind         | Allowed targets            |
|--------------------|----------------------------|
| sealed class/iface | `PRISM`, `LENS`, `DSL`     |
| value class        | `ISO`, `DSL`              |
| data class (other) | `LENS`, `DSL`             |

Finally, `COPY` is added iff the class also carries `@optics.copy`.

Consequences of the intersection:

* A bare `@optics` on a **data class** generates a **LENS** and a **lens DSL**
  (never an ISO — even though ISO is in the default set, it is intersected away).
* A bare `@optics` on a **value class** generates an **ISO** and an **iso DSL**.
* A bare `@optics` on a **sealed type** generates a **PRISM**, *and* a **LENS**
  for any shared abstract properties (§5.2), *and* a **prism DSL**.
* Asking for a target the kind does not support (e.g. `@optics([PRISM])` on a
  data class) intersects to the empty set and produces nothing for that target.

### 2.4 The `@optics.copy` marker

Independent of the optic targets, `@optics.copy` adds a `COPY` target that
generates a `copy { … }` builder function (§9). It is meaningful for data
classes (it delegates to Kotlin's `copy`).

---

## 3. Conventions shared by every generator

### 3.1 Everything is a companion extension

Each generated base optic is an extension on `SourceClass.Companion`. This is
what lets users write `Person.age`, `Person.address`, `Thing.object`, etc.:
the bare class name resolves to its companion, and the optic is an extension on
that companion.

### 3.2 Names and keyword escaping

* The **optic name** for a LENS/ISO focus is the **parameter/property name**.
  For a PRISM focus it is the **subclass's simple name with the first letter
  lowercased** (`PrismSealed1` → `prismSealed1`, `Object` → `object`,
  `In` → `in`).
* Every optic name is emitted **wrapped in backticks** unconditionally
  (``` `age` ```, ``` `object` ```, ``` `in` ```). Back-ticking an ordinary
  identifier is a no-op in Kotlin and uniformly handles names that collide with
  keywords. This is why users reference, e.g., ``Thing.`object` `` and
  ``PrismSealed.`in` ``.
* **Type names, package names and the source-class name** are escaped
  *segment-by-segment*: each dotted segment that is a Kotlin keyword is
  back-ticked (so the package `it.facile.assicurati` becomes
  `` `it`.facile.assicurati ``), while non-keyword segments are left alone.
* The lambda parameter used inside generated `get`/`set` bodies is the source
  class's simple name **with the first letter lowercased** (`LensData` →
  `lensData`), also keyword-sanitised.

### 3.3 Visibility

The generated optic's visibility is the **most restrictive** of:

* the companion object's visibility,
* the source class's visibility, and
* the visibilities of *all* enclosing classes.

These are combined pairwise (`public` is the identity; `private` dominates;
mixing `internal` and `protected` collapses to `private`; `local` propagates).
The resulting modifier (`public`, `internal`, `private`, `protected`) is emitted
as a prefix on every generated declaration. For example, a data class nested in
an `internal sealed interface` yields `internal` lenses.

### 3.4 The non-generic vs generic split: property vs function

This split is decided by whether the **source class has type parameters**:

* **No type parameters** → the optic is an **extension property with a getter**:
  ```kotlin
  val Source.Companion.x: Optic<Source, Focus> get() = …
  ```
* **Has type parameters** → the optic is an **extension function** (so it can
  introduce its own type parameters):
  ```kotlin
  fun <A, B> Source.Companion.x(): Optic<Source<A, B>, Focus> = …
  ```

So users access `Source.x` for monomorphic classes and `Source.x()` for generic
ones (and `Source.x<String>()` when they need to fix the type).

### 3.5 Type parameters, bounds, variance, star

When the source class is generic, the generator renders two related strings:

* **Declaration form** (`<A : Bound, B>`): each parameter is `name` optionally
  followed by `: bound1, bound2`. A bound equal to `kotlin.Any?` is omitted
  (it is the trivial bound). This form is used to declare the extension
  function's own type parameters, so **declared upper bounds are preserved**
  (e.g. `Wrapper<T : Foo>` produces `fun <T : Foo> Wrapper.Companion.item(): …`).
* **Reference form** (`<A, B>`): just the names, used wherever the source type is
  *mentioned* (e.g. `Wrapper<T>` as the optic's source argument).

Two important rules:

* **Declaration-site variance on the source class's own type parameters is
  dropped.** A function type parameter cannot carry `out`/`in`, so a class
  declared `Foo<out T>` contributes the parameter `T` (no variance) to the
  generated function.
* A type parameter that is **star-projected** (`*`) at the source is rendered as
  `*` in both forms.

### 3.6 Nullability

A nullable focus type is rendered with a trailing `?`. A lens onto a nullable
field therefore has a **nullable focus type** (`Lens<S, String?>`); turning it
into an `Optional<S, String>` is the user's job via `notNull` (§7).

### 3.7 Type-argument variance

When a focus *type argument* (as opposed to the source class's own parameter)
carries variance, it is rendered literally:

* `*` (star) → `*`
* invariant → the type as-is
* covariant → `out Type`
* contravariant → `in Type`

So a field of type `Extendable<out ITest>` produces a focus type
`Extendable<out ITest>`.

### 3.8 Fully-qualified emission, imports, and collision aliasing

Generated code refers to the source class, the focus types, and the optic types
(`arrow.optics.Lens`, …) using **fully-qualified names**, so generated files
normally need **no imports** at all. The exceptions:

* **Property-name vs optic-type collision.** If the source class declares a
  property whose name equals the first package segment of an optic type (e.g. a
  property literally named `arrow`, colliding with `arrow.optics.Lens`), the
  optic type is imported under an alias `ArrowOptics<TypeName>` and the alias is
  used in the body.
* **Property-name vs package-segment collision (DSL).** If a property name
  equals one of the source class's own package segments, the source class is
  imported under a sanitised alias and that alias is used.
* The prism generator always lists `arrow.core.left`, `arrow.core.right`,
  `arrow.core.identity` as imports (vestigial — the current prism body does not
  use them; see §13).
* The copy generator imports `arrow.optics.copy`.

### 3.9 The `inline` option

A configuration flag controls whether generated optics are `inline`. When on,
the keyword `inline` is inserted both before the `val`/`fun` and before `get()`.
When off (the default), no `inline` is emitted. This affects only the modifier;
the structure is identical.

### 3.10 File organisation

All snippets produced for one annotated class are grouped by `(package, name)`,
joined (bodies concatenated, imports unioned, de-duplicated), and written to a
single file whose name is the source class's (possibly nested) name plus the
suffix `__Optics`. The file begins with a `package` directive (unless the
package is unnamed) followed by the unioned imports.

---

## 4. ISO — value classes

**Applies to:** value classes (`@JvmInline value class`). An iso expresses the
loss-less isomorphism between the wrapper and its single wrapped value.

**Focus extraction:** the iso has exactly **one focus**, the value class's single
constructor parameter (its type and name).

**Generated shape (monomorphic):**

```kotlin
@optics @JvmInline
value class IsoData(val field1: String) { companion object }
```
produces
```kotlin
public val IsoData.Companion.`field1`: arrow.optics.Iso<IsoData, kotlin.String> get() =
  arrow.optics.Iso(
    get = { isoData: IsoData -> isoData.`field1` },
    reverseGet = { `field1`: kotlin.String -> IsoData(`field1`) }
  )
```

* `get` projects the wrapped value.
* `reverseGet` re-wraps it by calling the value class constructor.

**Generated shape (generic):**

```kotlin
@optics @JvmInline
value class IsoData<T>(val field1: T) { companion object }
```
produces
```kotlin
public fun <T> IsoData.Companion.`field1`(): arrow.optics.Iso<IsoData<T>, T> =
  arrow.optics.Iso(
    get = { isoData: IsoData<T> -> isoData.`field1` },
    reverseGet = { `field1`: T -> IsoData(`field1`) }
  )
```

(Note: the ISO generator references `arrow.optics.Iso` directly and does not
apply the alias-on-collision handling of §3.8.)

---

## 5. LENS

A lens focuses on one component of a product that is always present, providing a
`get` and a `set`. The lens generator handles two structurally different inputs.

### 5.1 Data classes

**Focus extraction:** **one focus per primary-constructor parameter**, taking the
parameter's type and name. (Secondary constructors are ignored; only the primary
constructor's parameters become lenses, because `set` relies on Kotlin's
generated `copy`.)

**Generated shape (monomorphic):**

```kotlin
@optics data class LensData(val field1: String) { companion object }
```
produces
```kotlin
public val LensData.Companion.`field1`: arrow.optics.Lens<LensData, kotlin.String> get() =
  arrow.optics.Lens(
    get = { lensData: LensData -> lensData.`field1` },
    set = { lensData: LensData, value: kotlin.String -> lensData.copy(`field1` = value) }
  )
```

**Generated shape (generic):**

```kotlin
@optics data class OpticsTest<A>(val field: A) { companion object }
```
produces
```kotlin
public fun <A> OpticsTest.Companion.`field`(): arrow.optics.Lens<OpticsTest<A>, A> =
  arrow.optics.Lens(
    get = { opticsTest: OpticsTest<A> -> opticsTest.`field` },
    set = { opticsTest: OpticsTest<A>, value: A -> opticsTest.copy(`field` = value) }
  )
```

Each parameter produces an independent lens; a class with N constructor
parameters produces N lenses.

### 5.2 Sealed classes / interfaces — lenses on shared properties

When a sealed type is annotated, the lens generator additionally tries to produce
a lens for each **abstract property that is uniform across the whole hierarchy**.
The extraction algorithm:

1. Collect the sealed type's **abstract properties that have no extension
   receiver**. If there are none, **emit an informational note and generate no
   lens** for this class.
2. Collect the **sealed subclasses**. If **any subclass is not a data class**
   (e.g. a plain `object`, a `data object`, or a non-data class), emit a note and
   **generate no lens** (the `set` body relies on every subclass having `copy`).
3. Partition the abstract properties into:
   * **uniform** ("good") — properties for which **every** subclass declares a
     property of the *same name* and *exactly the same resolved type* (nullability
     included); and
   * **non-uniform** ("bad") — the rest. Each non-uniform property triggers a note
     ("not uniform across all children") and is **ignored** (no lens for it).
4. If any uniform property is **not a constructor parameter** in some subclass
   (i.e. it is overridden as a body property rather than in the constructor),
   emit a note and **generate no lens** for the class (again because `set` uses
   `copy(name = …)`).
5. For each surviving uniform property, build a focus with the property's type and
   name, plus the **list of all subclasses** (each rendered with star projections
   for its own type parameters, e.g. `Box.Full<*>`).

**Generated shape.** The `get` reads the abstract property; the `set` dispatches
over the concrete subclass and calls `copy`:

```kotlin
@optics sealed class LensSealed {
  abstract val property1: String
  data class Child1(override val property1: String) : LensSealed()
  data class Child2(override val property1: String, val n: Int) : LensSealed()
  companion object
}
```
produces
```kotlin
public val LensSealed.Companion.`property1`: arrow.optics.Lens<LensSealed, kotlin.String> get() =
  arrow.optics.Lens(
    get = { lensSealed: LensSealed -> lensSealed.`property1` },
    set = { lensSealed: LensSealed, value: kotlin.String ->
      when (lensSealed) {
        is LensSealed.Child1 -> lensSealed.copy(`property1` = value)
        is LensSealed.Child2 -> lensSealed.copy(`property1` = value)
      }
    }
  )
```

* The `when` is exhaustive over the sealed subclasses.
* The subclasses may be declared **inside** the sealed type or as **top-level**
  siblings — both are discovered.

**Generic sealed parents and the unchecked cast.** If any subclass is rendered
with a star projection (because it is itself generic), each `copy` returns a
star-projected type, so the whole `when` is force-cast back to the parent's
parameterised type and annotated with `@Suppress("UNCHECKED_CAST")`:

```kotlin
@optics sealed class Box<A> {
  abstract val tag: String
  data class Full<A>(override val tag: String, val a: A) : Box<A>()
  data class Empty<A>(override val tag: String) : Box<A>()
  companion object
}
```
produces
```kotlin
public fun <A> Box.Companion.`tag`(): arrow.optics.Lens<Box<A>, kotlin.String> =
  arrow.optics.Lens(
    get = { box: Box<A> -> box.`tag` },
    set = { box: Box<A>, value: kotlin.String ->
      @Suppress("UNCHECKED_CAST")
      when (box) {
        is Box.Full<*> -> box.copy(`tag` = value)
        is Box.Empty<*> -> box.copy(`tag` = value)
      } as Box<A>
    }
  )
```

As in §3.4, the parent being generic switches the declaration from a property to
a function.

---

## 6. PRISM — sealed classes and interfaces

A prism focuses on one branch of a sum type: it succeeds when the value is of a
particular subtype and re-injects that subtype unchanged.

**Applies to:** sealed classes and sealed interfaces.

**Focus extraction:** **one focus per sealed subclass**. For each subclass the
generator records:

* the subclass's fully-qualified name (and the parameterised form, e.g.
  `Sub<A, B>`, used as the prism's focus type);
* the optic name = subclass simple name, first letter lowercased;
* the **refined source type** = the supertype as written in the subclass's
  `extends`/`implements` clause (e.g. `Parent<String, C>`), which becomes the
  prism's *source* type;
* the subclass's own type parameters.

**Generated body.** Every prism body is simply the library combinator that builds
a "is-instance-of" prism:

```kotlin
… = arrow.optics.Prism.instanceOf()
```

The combinator's source and focus types are inferred from the declared optic
type, and its `reverseGet` is the identity (every subclass value *is* a value of
the parent).

**Generated shape (monomorphic parent):**

```kotlin
@optics sealed class PrismSealed {
  data class PrismSealed1(val a: String?) : PrismSealed()
  data class PrismSealed2(val b: String?) : PrismSealed()
  companion object
}
```
produces
```kotlin
public val PrismSealed.Companion.`prismSealed1`: arrow.optics.Prism<PrismSealed, PrismSealed.PrismSealed1> get() =
  arrow.optics.Prism.instanceOf()

public val PrismSealed.Companion.`prismSealed2`: arrow.optics.Prism<PrismSealed, PrismSealed.PrismSealed2> get() =
  arrow.optics.Prism.instanceOf()
```

A sealed type with a single subclass produces a single prism with no special
casing.

**Generated shape (generic parent).** The source type of each prism is the
**refined supertype** of the subclass, and the function's type parameters are the
union of:

* the **free type variables appearing in the refined supertype's arguments**
  (type arguments that are themselves type parameters), and
* the **subclass's own type parameters**.

```kotlin
@optics sealed class PrismSealed<A, B> {
  data class PrismSealed1(val a: String?) : PrismSealed<String, String>()
  data class PrismSealed2<C>(val b: C?) : PrismSealed<String, C>()
  companion object
}
```
produces
```kotlin
// PrismSealed1 extends PrismSealed<String, String>; no free variables → no type params
public fun PrismSealed.Companion.`prismSealed1`(): arrow.optics.Prism<PrismSealed<String, String>, PrismSealed.PrismSealed1> =
  arrow.optics.Prism.instanceOf()

// PrismSealed2<C> extends PrismSealed<String, C>; free var C
public fun <C> PrismSealed.Companion.`prismSealed2`(): arrow.optics.Prism<PrismSealed<String, C>, PrismSealed.PrismSealed2<C>> =
  arrow.optics.Prism.instanceOf()
```

Note how the source type is the *specialised* `PrismSealed<String, …>` rather than
the bare `PrismSealed<A, B>`: a prism that picks `PrismSealed1` can only do so out
of a `PrismSealed<String, String>`. (Whether the parent is treated as generic is
decided by the parent's own type parameters, per §3.4.)

---

## 7. OPTIONAL — nullable foci

There is **no separate optional generator**, and the `OPTIONAL` value of
`OpticsTarget` is ignored by target selection (§2.3). Optionals are obtained
compositionally:

1. The lens generator produces a lens whose **focus type is nullable** for any
   nullable field (§5.1, §3.6). For example:

   ```kotlin
   @optics data class OptionalData(val field1: String?) { companion object }
   // generated:
   public val OptionalData.Companion.`field1`: arrow.optics.Lens<OptionalData, kotlin.String?> get() = …
   ```

2. The library combinator `notNull` (hand-written in Arrow, not generated) turns
   an optic whose focus is `S?` into an `Optional` whose focus is `S`. Because
   `Lens` is a subtype of `Optional`, it applies directly to the generated lens:

   ```kotlin
   val opt: Optional<OptionalData, String> = OptionalData.field1.notNull
   ```

3. The same holds for generic classes (`OptionalData.field1<String>().notNull`)
   and for the DSL: every generated DSL family includes an **`Optional` variant**
   (§8), and `notNull` is available within DSL chains
   (`…company.notNull.address…`).

In other words, the plugin's contribution to "optionals" is to make the lens'
focus correctly nullable; promotion to `Optional` is a library step.

---

## 8. DSL — composition extensions

In addition to the base companion optics, the generator emits **composition
helpers** so that optics can be chained with property-like syntax
(`Employees.employees.every.company.notNull.address.street.name`). These are the
"DSL" target.

### 8.1 The shape of a DSL extension

Every DSL extension has the form

```kotlin
val <__S> OuterOptic<__S, Source>.`focus`: OuterOptic<__S, Focus> get() = this + Source.`focus`
```

(or the `fun`-with-type-parameters form when the source class is generic). Here:

* `__S` is a fresh type variable standing for "whatever the outer optic starts
  from". The receiver is *any* optic that currently focuses on `Source`.
* `this + Source.\`focus\`` **composes** the outer optic with the base companion
  optic generated for that focus (`+` is optic composition). The result is an
  optic from `__S` straight to `Focus`.
* `Source` is the source class (alias-qualified per §3.8); `Source.\`focus\``
  refers to the base optic from §4–6.

For generic source classes the extension becomes a function that re-introduces
the class's type parameters alongside `__S`:

```kotlin
fun <__S, A, B> OuterOptic<__S, Source<A, B>>.`focus`(): OuterOptic<__S, Focus> =
  this + Source.`focus`()
```

### 8.2 Which optic kinds get a variant, and why

For each focus the generator emits **several copies** of the extension above, one
per *outer optic kind*. The set of kinds is exactly those `X` for which
`X` composed with the base optic's kind is still an `X`. This follows the optic
subtyping lattice (`Iso` is both a `Lens` and a `Prism`; `Lens` and `Prism` are
each an `Optional`; `Optional` is a `Traversal`) together with the fact that each
`+` overload requires its argument to be of the same kind:

| Base optic kind (source) | Generated outer-optic variants            |
|--------------------------|-------------------------------------------|
| Lens (data class field)  | `Lens`, `Optional`, `Traversal`           |
| Prism (sealed subclass)  | `Optional`, `Prism`, `Traversal`          |
| Iso (value class)        | `Iso`, `Lens`, `Optional`, `Prism`, `Traversal` |

Intuition: composing with a `Lens` cannot turn an outer `Prism` back into a
`Prism` (it weakens to `Optional`), so no `Prism` variant is produced for a lens
focus; composing with an `Iso` preserves *every* kind, so all five variants are
produced for a value-class focus.

### 8.3 Lens DSL (data classes)

For a data class, each constructor-parameter focus produces three extensions:

```kotlin
@optics data class Street(val number: Int, val name: String) { companion object }
```
produces (for `name`):
```kotlin
public val <__S> arrow.optics.Lens<__S, Street>.`name`:     arrow.optics.Lens<__S, kotlin.String>     get() = this + Street.`name`
public val <__S> arrow.optics.Optional<__S, Street>.`name`: arrow.optics.Optional<__S, kotlin.String> get() = this + Street.`name`
public val <__S> arrow.optics.Traversal<__S, Street>.`name`:arrow.optics.Traversal<__S, kotlin.String>get() = this + Street.`name`
```

### 8.4 Prism DSL (sealed types)

For a sealed type, each subclass focus produces three extensions (`Optional`,
`Prism`, `Traversal`), referencing the base prism:

```kotlin
@optics sealed interface Thing {
  data class Object(val value: Int) : Thing
  companion object
}
```
produces (for the `Object` branch, named `object`):
```kotlin
public val <__S> arrow.optics.Optional<__S, Thing>.`object`:  arrow.optics.Optional<__S, Thing.Object>  get() = this + Thing.`object`
public val <__S> arrow.optics.Prism<__S, Thing>.`object`:     arrow.optics.Prism<__S, Thing.Object>     get() = this + Thing.`object`
public val <__S> arrow.optics.Traversal<__S, Thing>.`object`: arrow.optics.Traversal<__S, Thing.Object> get() = this + Thing.`object`
```

For a **generic** sealed type the DSL uses the refined source type and the same
union of type parameters as the base prism (§6), prefixed with `__S`.

(Note: for a sealed type, the DSL target produces only the **prism** family of
composition helpers. The shared-property lenses of §5.2 are still generated as
base companion optics, but they do not get their own DSL composition variants.)

### 8.5 Iso DSL (value classes)

For a value class, the single focus produces all five variants (`Iso`, `Lens`,
`Optional`, `Prism`, `Traversal`):

```kotlin
@optics @JvmInline value class Cents(val value: Int) { companion object }
```
produces:
```kotlin
public val <__S> arrow.optics.Iso<__S, Cents>.`value`:      arrow.optics.Iso<__S, kotlin.Int>       get() = this + Cents.`value`
public val <__S> arrow.optics.Lens<__S, Cents>.`value`:     arrow.optics.Lens<__S, kotlin.Int>      get() = this + Cents.`value`
public val <__S> arrow.optics.Optional<__S, Cents>.`value`: arrow.optics.Optional<__S, kotlin.Int>  get() = this + Cents.`value`
public val <__S> arrow.optics.Prism<__S, Cents>.`value`:    arrow.optics.Prism<__S, kotlin.Int>     get() = this + Cents.`value`
public val <__S> arrow.optics.Traversal<__S, Cents>.`value`:arrow.optics.Traversal<__S, kotlin.Int> get() = this + Cents.`value`
```

### 8.6 How chains and library combinators interleave

The DSL extensions only handle drilling into *generated* optics. Built-in
combinators provided by the library — `every` (into all elements of a
collection/traversable), `at`/`index` (into a map/list position), `notNull`
(§7) — are also optics of these kinds, so they compose seamlessly in the middle
of a generated chain, e.g.

```kotlin
Employees.employees.every.company.notNull.address.street.name
```

Each `.segment` is either a generated DSL extension (composing the next base
optic) or a library combinator; all of them are just `this + …` compositions
under the hood.

---

## 9. COPY — the `@optics.copy` builder

When `@optics.copy` is present, the generator emits an **extension `copy`
function** that mirrors Kotlin's `copy` but lets the body address nested fields
through the generated optics instead of manual nesting.

**Generated shape (monomorphic):**

```kotlin
@optics @optics.copy data class Person(val name: String, val age: Int, val address: Address) {
  companion object
}
```
produces
```kotlin
public fun Person.copy(
  block: context(arrow.optics.Copy<Person>) Person.Companion.(Person) -> Unit
): Person {
  val me = this
  return me.copy { block(this, Person.Companion, me) }
}
```

* The inner `me.copy { … }` is the **library** `copy` builder (imported as
  `arrow.optics.copy`), which threads a mutable `Copy<Person>` through the block.
* The `block` is invoked with three things in scope:
  * the `Copy<Person>` **context** — providing `set` and `transform` operations
    on any `Traversal<Person, …>`;
  * the **companion** `Person.Companion` as receiver — so the base optics
    (`address`, `age`, …) and their DSL chains resolve inside the block; and
  * the original value `me` as the lambda argument.

This lets users write:

```kotlin
me.copy {
  age transform { it + 1 }
  address.city.name set "Amsterdam"
  address.coordinates set listOf(2, 3)
}
```

where `address.city.name` is resolved by composing the companion lens
`Person.address` with the DSL extensions for `city` and `name`, and `set` comes
from the `Copy` context.

**Generic classes** produce the analogous function with the class's type
parameters added: `fun <A, B> Source<A, B>.copy(block: context(Copy<Source<A, B>>)
Source.Companion.(Source<A, B>) -> Unit): Source<A, B>`.

The `copy` builder relies on Kotlin **context parameters**.

---

## 10. Behaviour by kind of class

| Source class                       | Base optics generated                                                                 | DSL family | `@optics.copy` |
|------------------------------------|----------------------------------------------------------------------------------------|------------|----------------|
| **data class**                     | one **Lens** per primary-constructor parameter (focus nullable if the field is)        | lens DSL (Lens/Optional/Traversal) | yes → `copy { }` |
| **value class** (`@JvmInline`)     | one **Iso** for the single wrapped value                                               | iso DSL (Iso/Lens/Optional/Prism/Traversal) | (not typical) |
| **sealed class / sealed interface**| one **Prism** per subclass; **plus** one **Lens** per *uniform abstract* property (§5.2) | prism DSL (Optional/Prism/Traversal) | (not typical) |
| anything else                      | none — hard error                                                                      | —          | —              |

Notes on sealed hierarchies specifically:

* **Subclasses** may be nested in the sealed type or declared as top-level
  siblings; both are found and used.
* A **single-subclass** sealed type still gets a prism (no special handling).
* **Non-data subclasses** (plain `object`, `data object`, ordinary classes)
  disable the *shared-property lens* path (§5.2 step 2) but do **not** affect
  prism generation — each subclass still gets a prism.
* **Sealed interfaces** behave exactly like sealed classes.
* Annotated subclasses that are themselves data classes get their *own* lenses,
  isos, etc., independently of the parent's prisms.

---

## 11. Generics and variance, consolidated

* **Presence of type parameters** flips every base optic from an extension
  *property* to an extension *function* that re-declares those parameters
  (§3.4). Accessors therefore become calls: `Source.field()`,
  `Source.field<String>()`.
* **Upper bounds are preserved** on the generated function's type parameters,
  except the trivial `Any?` bound which is dropped (§3.5). E.g.
  `Wrapper<T : Foo>` ⇒ `fun <T : Foo> Wrapper.Companion.item(): Lens<Wrapper<T>, T>`.
* **Declaration-site variance on the source class's own parameters is dropped**
  in the generated parameter list (`out`/`in` cannot appear on a function type
  parameter) (§3.5).
* **Star projections** in the source's parameters are carried through as `*`
  (§3.5), and in sealed-lens `set` dispatch they appear as `is Sub<*> ->` with an
  `@Suppress("UNCHECKED_CAST")` cast on the result (§5.2).
* **Variance on focus type arguments** is rendered literally (`out`/`in`/`*`)
  (§3.7), so `Extendable<out ITest>` stays `Extendable<out ITest>`.
* **Prisms specialise the source type** to the subclass's actual supertype and
  only quantify over the type variables that genuinely remain free (§6).
* **Nullability** flows into the focus type verbatim; an `Optional` is then
  obtained via `notNull` (§7).

---

## 12. Diagnostics and failure modes

The generator distinguishes **errors** (reported against the source, nothing
generated) from **informational notes** (a particular optic is quietly skipped).

**Errors:**

* Annotating a class that is not data/value/sealed.
* Missing companion object (when the companion check is enabled).

**Informational notes (skip, do not fail the build by themselves):** all are in
the sealed-class *lens* path (§5.2):

* the sealed type has **no abstract properties** without extension receiver;
* the sealed type has a **non-data-class subclass**;
* a candidate property is **not uniform** across subclasses (different type or
  nullability) → that property is ignored;
* a uniform property is **not a constructor parameter** in some subclass.

Because skipping is silent, code that *references* an optic which was not
generated fails to compile at the use site (not at the annotation). For example:

* a sealed type with an abstract property but **zero subclasses** → no lens →
  `Sealed.property` does not resolve;
* a sealed type whose subclasses **change the property's type or nullability** →
  the property is ignored → `Sealed.property()` does not resolve.

Separately, if a class **references a type that cannot be resolved**, optics for
it are not produced (so referencing them fails to compile).

---

## 13. Known limitations and vestigial behaviour

* **`OpticsTarget.OPTIONAL` is inert.** It is part of the public enum but is not
  mapped to any generator; optionals are produced via lens-to-nullable +
  `notNull` (§7).
* **Generic value-class DSL is currently broken.** For a generic value class the
  iso-DSL generic branch emits a dangling type-parameter list (e.g. `<__S,>`) and
  does not bind the class's own parameter, so it does not compile. (The base iso
  itself, §4, is fine; only the DSL composition helpers are affected.) The base
  iso and the non-generic iso DSL work.
* **Prism imports are vestigial.** The prism file always imports
  `arrow.core.left`, `arrow.core.right`, `arrow.core.identity`, left over from an
  earlier implementation that built prisms by hand; the current body is just
  `Prism.instanceOf()` and uses none of them.
* **Unused diagnostic messages.** Messages such as "Iso generation is supported
  for data classes with up to 22 constructor parameters" and the DSL "invalid
  target" message exist but are not currently reachable, because ISO is now
  restricted to value classes and targets are pre-filtered by class kind before
  the per-target evaluators run (so most "invalid target for this kind" branches
  are defensive and never execute).
* **ISO does not alias on collision.** Unlike the lens and DSL generators, the iso
  generator refers to `arrow.optics.Iso` directly and does not apply the
  property-name/type collision aliasing of §3.8.
