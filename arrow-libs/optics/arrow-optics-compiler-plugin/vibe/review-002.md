# Review #2 — Arrow Optics K2 compiler plugin

Scope: `arrow-libs/optics/arrow-optics-compiler-plugin/src/main` (FIR generators,
FIR checker, IR body generator, shared model). The focus is on **Kotlin language
corner cases the plugin does not handle yet** and **problems in FIR/IR
generation**. The question behind it: is the plugin stable enough for regular use?

Method:

* I read every source file in `src/main`, plus the relevant compiler sources
  (Kotlin 2.4.21, the version this repo builds against) and the `arrow-optics`
  runtime the generated code calls into.
* I checked each suspicion empirically. A temporary test (since deleted) compiled
  about 50 small programs with `kotlin-compile-testing` against the real plugin on
  JVM, evaluated them where possible, and ran some of them with
  `-Xverify-ir=error`. Each finding below says whether it was **confirmed by a
  probe** or **inferred from the code**.

Overall verdict: **not yet stable for regular use.** The common path (public,
top-level, monomorphic data/value/sealed classes on JVM) works and is now
runtime-tested. But several ordinary Kotlin shapes either fail to compile in
confusing ways, are silently ignored, or produce bytecode that crashes at class-load
time. Also, **every monomorphic class with the default targets produces IR that
fails IR verification**. JVM erasure hides this today, but it is a serious risk for
the klib-based backends (JS/Wasm/Native), and Arrow is a multiplatform library.

---

## 0. Summary

| # | Finding | Severity | Evidence |
|---|---|---|---|
| 1.1 | `private`/`protected` source classes get optics nobody can use | Critical | probe |
| 1.2 | Invalid IR: generic reconstruction, generic sealed subclasses, monomorphic DSL properties | Critical | probe (`-Xverify-ir`) |
| 1.3 | Generated file name `XOptics` gives a duplicate JVM class or a **`ClassFormatError` at runtime** | Critical | probe |
| 1.4 | Name collisions in the companion silently drop optics | High | probe |
| 1.5 | Generic multi-level sealed hierarchies produce malformed prism types (`LCE<>`) | High | probe |
| 2.1 | Type-parameter bounds are dropped (spec §3.5) | High | probe |
| 2.2 | Private constructors/properties are bypassed and exposed | High | probe |
| 2.3 | Many silent no-ops: import aliases, foreign `@Serializable`, ineligible kinds, `[DSL]`-only | High | probe |
| 2.4 | Results depend on FIR phase (raw modality, eager supertype resolution) | High (IDE) | code + compiler sources |
| 3.x | IR robustness: `!!`/`first{}`, lookup by name only, offsets, multi-field value classes | Medium | code |
| 4.x | Ecosystem: Kotlin-version lock-in, IDE loading, KMP expect/actual, IC | High (adoption) | code / design |

---

## 1. Critical / high: confirmed defects

### 1.1 `private` and `protected` source classes get optics nobody can use

Every one of these fails to compile at the **use site**, in the same file as the
class:

```kotlin
@optics private data class P(val x: Int)            // with or without an explicit companion
private val l = P.x
// e: Cannot access 'val x: Lens<P, Int>': it is private in 'p.P.Companion'.

@optics private data class P(val q: Q) { companion object }   // DSL
private val l = P.q.x
// e: Cannot access 'val <__S> PLens<__S, __S, Q, Q>.x ...': it is private in file.

open class Outer {
  @optics protected data class In(val x: Int, val y: Int) { companion object }
  fun t() = In.x.get(In(1, 2))
  // e: Cannot access 'val x: Lens<Outer.In, Int>': it is protected in 'p.Outer.In.Companion'.
}

private class Outer { @optics data class In(...) ; @optics data class In2(val i: In) }
// same two errors as above (lens private in companion, DSL private in file)

@optics @optics.copy private data class C(val x: Int)
C(1).copy { C.x set 2 }   // e: Cannot access 'val x' ... private in 'p.C.Companion'
```

Root causes:

* **The generated companion copies the class's visibility.**
  `OpticsCompanionGenerator.kt:75` does `visibility = owner.rawStatus.visibility`.
  On a *member*, `private` means "private to the containing class". A private
  companion of a private top-level class is therefore usable only *inside* `P`,
  not in the file. A hand-written companion would be `public`, and the class's
  own visibility already limits access.
* **Member optics take the most restrictive visibility of all enclosing classes.**
  `OpticsCompanionGenerator.kt:118` and `:130` (and `OpticsCopyGenerator.kt:64`
  for `copy`) use `mostRestrictive(effectiveVisibility(source), owner.visibility)`.
  Algo §3.3 describes this rule for *top-level extensions* in the KSP output. For
  *members* it is both unnecessary (the container already restricts access) and
  wrong, because member `private` and top-level `private` mean different things.
* **Top-level DSL extensions are `private` in a *different*, synthetic file.**
  `OpticsDslGenerator.kt:110` and `:151` put the DSL in the synthetic file
  `XOptics.kt`. A `private` top-level declaration there is invisible from the
  user's file, so the DSL for a private class (or a class nested in a private
  class) can never be used.
* **`protected` cannot be expressed for top-level declarations.**
  `mostRestrictive` (`OpticsModel.kt:66`) can return `Protected`, which is then
  assigned to a top-level DSL extension. That is not valid Kotlin and only goes
  unnoticed because checkers do not run on generated declarations. Mixing
  `internal` with `protected` collapses to `private`, which hits the same problem.

Suggested fix:

* Give the generated companion `public` visibility (or leave the builder default).
* Make base optics and `copy` `public` *members*, restricted only by the property's
  and constructor's visibility (see §2.2).
* For the top-level DSL, if the effective visibility is `private` or `protected`,
  either skip the DSL and report a warning, or (better) generate the DSL helpers
  as members/extensions inside the companion when that is possible. Do not emit a
  `protected` or file-`private` top-level declaration.
* Add tests for: a private top-level class, a class nested in a private class, a
  protected nested class, and an internal class nested in a public one.

### 1.2 The plugin produces invalid IR in three independent places

Run with `-Xverify-ir=error`, the following all fail. The existing test suite never
enables verification, so all of this is currently hidden by JVM erasure.

**(a) Generic reconstruction uses the class's own type parameters, not the
function's.** This is review #1 §2.4, still open, and the problem is wider than
reported there.

```text
@optics data class Pair2<A, B>(val first: A, val second: B)      -> invalid IR (A, B out of scope)
@optics([ISO]) @JvmInline value class Box<T>(val t: T)          -> invalid IR (T out of scope)
@optics([LENS]) data class W<T : Comparable<T>>(val item: T, ...) -> invalid IR
@optics data class G<A>(val a: A)  + DSL use                     -> invalid IR (the base lens body)
```

There are two sources:

* `reconstruct` reads siblings with `param.type`
  (`OpticsIrGenerationExtension.kt:358`), which is expressed in the class's type
  parameters.
* `irCallConstructor(ctor.symbol, ctorTypeArgs)` (`:353`) gives the call the type
  `callee.owner.returnType`, i.e. `Box<T of Box>`, **not** the substituted type.
  You can see this in `ExpressionHelpers.kt` in the compiler sources. That is why
  even the *single-field* generic value class fails.

Fix: build an `IrTypeSubstitutor` from the class's type parameters to
`opticFn.typeParameters`. Use it for sibling types, and set the constructor call's
`type` explicitly to `sourceType`.

**(b) Sealed shared-property lens with a generic subclass.**

```kotlin
@optics([LENS, PRISM]) sealed class S {
  abstract val n: String
  data class A<T>(override val n: String, val t: T) : S()
  data class B(override val n: String) : S()
}
```

On JVM, `S.n.set(S.A("a", 1), "z")` evaluates correctly. Under verification it is
invalid IR, because `sealedSet` (`OpticsIrGenerationExtension.kt:293-302`):

* uses `sub.defaultType`, i.e. `A<T>` with `T` out of scope, both for the `is`
  check and for the cast, and
* calls `reconstruct(sub, emptyList(), ...)`, so the constructor call for a
  *generic* subclass gets **no type arguments at all** (they are left `null`).

Fix: use the star-projected type (`A<*>`) for `irIs`/`irImplicitCast`. Pass erased
or star-like type arguments to the constructor, or better, bind the cast in an
`irTemporary` and take the arguments from it. Make the call's type the parent type.

**(c) Every monomorphic DSL *property* is invalid IR, reported against "the
frontend".**

```kotlin
@optics data class Foo(val x: Int) { companion object }   // default targets
// e: .../p/FooOptics.kt The frontend generated invalid IR. ... references a type parameter
//    'TYPE_PARAMETER GENERATED[OpticsDslGenerator.Key] name:__S' that is not available in the current scope.
```

This fires once per DSL variant (3 for a lens, 5 for an iso) for **every
monomorphic `@optics` data, value or sealed class with the default targets**, and
for the whole `dslModel` used by the tests. The message blames the frontend
because verification runs right after Fir2Ir, before the IR plugin. So the
declaration that `createTopLevelProperty(... typeParameter("__S") ...)`
(`OpticsDslGenerator.kt:94-111`) generates is already broken by Fir2Ir.

The generic *function* form of the DSL does not have this problem.

Next steps:

* Build a minimal reproduction with a plain FIR plugin that generates a generic
  top-level extension property with `hasBackingField = false`.
* Check whether the type-parameter references in `returnTypeProvider` /
  `extensionReceiverType` should be created differently.
* If the fault is in Kotlin, file a KT issue and add a workaround.

On JVM this is erased away. For klib backends, IR is serialized with symbol
references to type parameters, so this is the kind of defect that turns into klib
serialization or linkage crashes (not verified here, since the harness is
JVM-only).

**Recommendation:** add `-Xverify-ir=error` (and `-Xverify-ir-visibility`) to
`buildCompilation` in `src/test/.../Compilation.kt` **permanently**. Today that
would make a large part of the suite fail, and that failure count is a fair
measure of the remaining IR debt.

### 1.3 Synthetic file names collide: duplicate JVM classes or runtime `ClassFormatError`

`OpticsDslGenerator.kt:88` and `:125` name the synthetic file
`"${source.classId.shortClassName}Optics"`, using only the *short* name.

* **Two nested classes with the same short name in one package.** The probe:

  ```kotlin
  class A { @optics data class Leaf(val v: Int) }
  class B { @optics data class Leaf(val v: String) }
  ```

  This **compiles successfully** and then fails when loaded:

  ```text
  java.lang.ClassFormatError: Duplicate method name "getV" with signature
    "(Larrow/optics/PLens;)Larrow/optics/PLens;" in class file p/LeafOpticsKt
  ```

  Both DSL families land in `LeafOpticsKt`, and their receivers erase to the same
  `PLens`. Because checkers skip generated declarations, no
  "platform declaration clash" is reported. This is the worst failure mode in the
  review: the build is green and the crash only appears at runtime.
* **A user file whose name happens to match.** A user-written `LeafOptics.kt` in
  the same package as `@optics data class Leaf` fails with
  `Duplicate JVM class name 'p/LeafOpticsKt'`. This is likely in real projects,
  since "`XOptics.kt`" is a natural name for hand-written optics helpers.

Fix:

* Derive the file name from the full relative class name plus a reserved suffix,
  e.g. `Outer_Leaf__Optics`. The KSP processor used a `__Optics` suffix (algo
  §3.10), so this also keeps parity.
* Keep in mind that the erased-signature clash also happens *within* one file
  whenever two sources in the same file share a field name. With one file per
  source class, the remaining risk is limited to the case where one class's own
  variants clash, which they do not today.

### 1.4 Name collisions in the companion silently drop optics

Optic names come from constructor-parameter names, lowercased subclass names and
shared-property names. Nothing deduplicates them.

* **Prism vs shared-property lens.**

  ```kotlin
  @optics sealed interface E {
    val id: String
    data class Id(override val id: String) : E
    data class Other(override val id: String) : E
  }
  val l: Lens<E, String> = E.id
  // e: Initializer type mismatch: expected 'PLens<E, E, String, String>', actual 'Prism<E, E.Id>'.
  ```

  `generateProperties` and `generateFunctions` use
  `foci.firstOrNull { it.opticName == name }` (`OpticsCompanionGenerator.kt:114`,
  `:129`), so whichever focus comes first wins (prisms come first) and the other
  is silently lost.
* **Two subclasses with the same simple name** under different containers (for
  example `A.Leaf : S` and `B.Leaf : S`) both map to `leaf`. Only one prism is
  produced. Inferred from the same code path.
* **A user-declared companion member with the same name.**
  `companion object { val x = 5 }` on `data class U(val x: Int)` gives
  `Conflicting declarations` pointing at the *user's* code, with no hint that the
  plugin caused it. In addition, the IR DSL body looks up the base optic **by name
  only** (`OpticsIrGenerationExtension.kt:227`, `:233`), so when overloads exist
  it can bind to a user member instead of the generated one.

Fix:

* Detect collisions in a checker and report a clear diagnostic that names both
  sources of the name.
* Skip a generated optic when a user member with that name already exists.
* In IR, look up the base optic filtered by `origin == GeneratedByPlugin(Key)`.

### 1.5 Generic multi-level sealed hierarchies get malformed prism types

```kotlin
@optics sealed interface LCE<out D> {
  data object Loading : LCE<Nothing>
  sealed interface CE<out D> : LCE<D>
  data class Content<out D>(val data: D) : CE<D>
}
val p = LCE.content<Int>()
p.getOrNull(LCE.Content(1))
// e: Argument type mismatch: actual type is 'LCE.Content<Int>', but 'LCE<>{1}' was expected.
```

How this happens:

* `getSealedClassInheritors` keeps a class when `isSupertypeOf(current, session)`
  holds (`FirOpticsExtractor.kt:307`). That check is **transitive**
  (`FirHelpers.kt:103` walks supertypes recursively), so grandchildren such as
  `Content` get prisms on `LCE`.
* `refinedSource` only looks at the *direct* supertypes
  (`sub.resolvedSuperTypes.firstOrNull { it.classId == symbol.classId }`,
  `FirOpticsExtractor.kt:258`). For a grandchild it is `null`.
* The code then falls back to `source.constructType(emptyArray(), false)`
  (`OpticsCompanionGenerator.kt:137`, `OpticsDslGenerator.kt:174`). For a
  *generic* parent that builds a type with **zero type arguments**: the `LCE<>` in
  the error above.

The existing test `Nested generic sealed hierarchies, #3384` passes only because it
never uses the generated prism.

There is also an inconsistency. FIR uses transitive inheritors, while IR's
`sealedSet` uses `IrClass.sealedSubclasses`, which are *direct* only. For lenses
this is harmless today, because an intermediate sealed node is not a data class and
so disables the lens. It is still a trap for future changes.

Fix: decide on direct vs transitive inheritors and document the choice.

* If transitive, compute the parent supertype through the full substitution chain
  (for example with `lookupSuperTypes(..., substituteTypes = true)` or
  `findCorrespondingSupertypes`).
* Never fall back to a zero-argument constructed type for a generic class. Fail
  loudly, or skip the focus with a diagnostic, instead.

---

## 2. High: semantic gaps and unsupported language corner cases

### 2.1 Type-parameter bounds are dropped

`opticFunction` (`OpticsCompanionGenerator.kt:174`) and the generic DSL
(`OpticsDslGenerator.kt:144`) declare type parameters with `typeParameter(tp.name)`
and **no bounds**, which contradicts algo §3.5. The probe:

```kotlin
@optics data class W<T : Comparable<T>>(val item: T, val other: Int)
val bad = W.item<Any>()      // compiles; `W<Any>` violates the declared bound
```

The existing test `Respects upper bounds, #3692` only checks `i != null`.
Consequences:

* the generated API accepts impossible instantiations;
* the IDE shows signatures without bounds;
* the constructor call inside the body is type-incorrect IR, saved on JVM only
  because the backend inserts a `checkcast`.

Fix: `typeParameter(tp.name) { bound { tps -> substitutor(tps).substituteOrSelf(b) } }`
for every `tp.resolvedBounds` entry that is not the trivial `Any?`. Use the bound
provider that receives the type-parameter list, so that interdependent bounds such
as `<A, B : List<A>>` work. Variance is correctly dropped already.

### 2.2 Private constructors and private properties are bypassed

```kotlin
@optics data class PC private constructor(val x: Int) {
  companion object { fun of(x: Int) = PC(if (x < 0) 0 else x) }
}
PC.x.set(PC.of(1), -1).x   // == -1: the invariant enforced by `of` is bypassed
```

The compiler even prints *"This will become an error in language version 2.5"*
(KT-11914) for the data-class `copy` on that class. Kotlin itself is moving
`copy` to the constructor's visibility, and the generated lens re-opens the
loophole that Kotlin is closing.

The visibility computation (`FirOpticsExtractor.kt:230-239`) only considers
classes. It ignores:

* the **primary constructor's** visibility, which matters for lens `set`, iso
  `reverseGet` and `copy`;
* the **property's** visibility. For example,
  `data class PrismSealed1(private val a: String?)` in the existing tests gets a
  *public* lens onto a private field;
* for sealed lenses, **each subclass's** constructor visibility.

A related, unverified risk: `sealedSet` calls `Sub(...)` from `Parent.Companion`.
If `Sub`'s constructor is `private`, that call is not legal Kotlin, so the JVM
synthetic-accessor lowering has no reason to generate an accessor. With the
default JVM 1.8 target (no nestmates), that probably means `IllegalAccessError` at
runtime. This is worth a probe.

Fix: fold the constructor and property visibilities into the optic's visibility.
Alternatively (closer to KSP), skip the focus and emit a warning when the user
would not be able to write the optic by hand.

### 2.3 Silent no-ops: the plugin often fails without saying anything

Algo §12 lists hard errors. Apart from the `@Serializable` check, the plugin
reports none of them, and several ordinary inputs make it do nothing at all:

| Input | What happens | Evidence |
|---|---|---|
| `import arrow.optics.optics as lenses` + `@lenses` | **Nothing generated**: the predicate does not match aliased annotations. `@lenses.copy` and `targets` would also be missed by `checkEvenIfUnresolved`. | probe |
| Any annotation *named* `Serializable` (e.g. a user's `annotation class Serializable`) | No companion is generated, so **no optics**, and no diagnostic, because the checker resolves the annotation properly and sees it is not kotlinx's | probe |
| `@optics interface I`, `@optics enum class E`, `@optics object O`, `@optics data object D` | Accepted silently. A companion is generated (nested in an object, for `object`/`data object`), and nothing else | probe |
| `@optics([OpticsTarget.DSL]) data class Foo` | Nothing at all is generated, because DSL foci are filtered by the LENS target (`FirOpticsExtractor.kt:86`) | probe |
| `targets` given via an import-aliased enum entry | The entry is ignored, so the class may silently get "generate everything" | code |

On the `Serializable` heuristic: `checkEvenIfUnresolved`
(`FirOpticsExtractor.kt:328-347`) compares only the *class-name* segments of an
unresolved user type. The package is ignored entirely, so `@Serializable`,
`@com.foo.Serializable` and a local `Serializable` all match. The same heuristic
also drives `matches()` in two generators and `effectiveTargets`.

Also, the note at `OpticsCompanionGenerator.kt:53-56` says eligibility "cannot be
checked" in the companion phase. That is true for `resolvedStatus`, but `isData`,
`isInline`/`isValue`, `isCompanion`, `isInner` and `classKind` all read **raw**
status or raw FIR (`FirSymbolStatusUtils.kt`: `isData = rawStatus.isData`), and an
explicit `sealed` modifier is present in raw status too. So:

* `classKind` in {`OBJECT`, `ENUM_CLASS`, `ANNOTATION_CLASS`, non-sealed
  `INTERFACE`} and ineligible kinds in general can be rejected *before* a companion
  is generated;
* and in any case they should be rejected with a diagnostic.

Fix: add a `FirRegularClassChecker` (next to `OpticsSerializableChecker`) that
reports:

* an ineligible class kind;
* an empty effective target set (`[DSL]` alone, `[PRISM]` on a data class, ...);
* the §5.2 "lens skipped" notes, as warnings;
* name collisions (§1.4);
* a missing `arrow-optics` runtime (§3.1);
* (ideally) the use of an import alias.

Each needs a negative test that checks the message (the suite currently has none).

### 2.4 FIR results depend on the resolution phase (stability in the IDE)

The CLI resolves files phase by phase. In the IDE (LL-FIR / Analysis API),
declarations are resolved lazily and in an unpredictable order. Code that is only
correct "after phase X" therefore produces results that depend on what happened to
be resolved first.

* **Abstract interface properties are detected through raw modality.**
  `sealedLensFoci` keeps a property only if `rawStatus.modality == ABSTRACT`
  (`FirOpticsExtractor.kt:153`). In the raw FIR builder, modality is `null` unless
  a modifier is written (`PsiRawFirBuilder.kt:178-187`). So in a
  `sealed interface Shape { val id: String }`, `id` is **not** abstract until
  STATUS has run. It works in the CLI test
  (`SerializationTests ... shared abstract property`) because
  `getCallableNamesForClass` happens to run after STATUS there. In the IDE, the
  name set can be computed earlier, and then `Shape.id` would resolve
  intermittently. Fix: call `symbol.lazyResolveToPhase(STATUS)` and use
  `resolvedStatus`, or treat "member of an interface, no initializer, no accessor
  body" as abstract.
* **Eager, transitive supertype resolution from inside generation.**
  `getSealedClassInheritors` runs `lazyResolveToPhase(SUPER_TYPES)` on **every
  class in the package**, including nested ones, and then walks supertypes
  transitively through `isSupertypeOf`, possibly into other packages and modules.
  This happens on *every* call to `generateProperties`/`generateFunctions`, for
  every sealed source. In the IDE, cross-declaration lazy resolution triggered
  from a generator is a known source of resolve-contract violations and lock
  contention. In the CLI it costs O(classes in the package) per call.
* **Name supersets amplify the cost.** With `resolveSupertypes = false`, the
  candidate set is *every class in the package*, so `getCallableNamesForClass`
  and `getTopLevelCallableIds` advertise a lowercase name for **every class in
  the package**. Every lookup of such a name, for example any `foo` in the
  package, triggers the full resolving path above, only to return an empty list.
* **Nothing is cached** (review #1 §4 is still open). `effectiveTargets`, `foci`
  and `annotatedSources` are recomputed for each name in each generator.
  `annotatedSources()` re-runs `getSymbolsByPredicate` and target parsing for
  every top-level callable lookup.

Fix:

* Use the compiler's own sealed-inheritors data (`getSealedClassInheritors(session)`
  from `fir.declarations.utils`, populated by `FirSealedClassInheritorsProcessor`)
  where it is guaranteed to be available. Keep the package scan only for the
  *name* phase, and keep it cheap (names only, no resolution).
* Memoise per class symbol with `session.firCachesFactory.createCache`.
* Restrict candidate names to classes that syntactically mention a supertype
  with the parent's short name. That is a cheap raw-FIR filter.

### 2.5 Other language corner cases worth pinning down

* **Value classes with more than one field** (`-Xvalue-classes`, and the future
  Valhalla-style value classes). `constructorFoci` returns one ISO per parameter
  (`FirOpticsExtractor.kt:262-272`), but an iso only makes sense for exactly one
  field. Guard on `valueParameterSymbols.size == 1`.
* **Phantom type parameters on generic subclasses**
  (`data class S<A, X>(val a: A) : T<A>()`). The prism `fun <A, X> s()` compiles
  and works, but `X` can never be inferred, so the user must always spell it out.
  This matches KSP behaviour; it should be documented.
* **Subclasses that only fix the parent's arguments**
  (`object Empty : Parent<Nothing>()`). The resulting `Prism<Parent<Nothing>, Empty>`
  only applies to `Parent<Int>` if the parent is declared covariant. Document it.
* **Sealed subclasses named like keywords** (`In`, `Is`) produce `` `in` `` and
  `` `is` ``. This works (probe), but users need backticks. Fine as is.
* **`@JvmField` constructor properties**, **enum entries implementing a sealed
  interface**, **mono grandchildren**, **named companions**, **subclasses in other
  files** and **local classes**: all OK (probe). Add tests so they stay that way.
* **Context-parameter, suspend and definitely-non-null (`T & Any`) field types**:
  not probed. The substitution path should handle them, but they are worth one
  test each.

---

## 3. Medium: robustness of IR generation

### 3.1 Crash paths instead of diagnostics

* `OpticsIrSymbols` resolves symbols lazily now (review #1 §2.5 is fixed), but
  still with `!!` (`OpticsIrGenerationExtension.kt:81-105`). `@optics` lives in
  **arrow-annotations**, so a user can have the annotation without
  **arrow-optics**. In that case FIR happily generates declarations whose types
  reference `arrow.optics.Lens`/`PLens`, which do not resolve. There is no FIR
  diagnostic, and IR then crashes with an NPE (inferred).
  Fix: in FIR, check
  `session.symbolProvider.getClassLikeSymbolByClassId(OpticsNames.PLENS) != null`
  once per session. If it is missing, generate nothing and report a clear error
  from the checker.
* Runtime overloads are picked by arity (`first { regular params == 2 }`,
  `:73-79`, `:93`, `:101`). That is fragile against API evolution in
  `arrow-optics`; for example, adding another two-argument
  `PLens.Companion.invoke` silently changes behaviour or throws
  `NoSuchElementException` inside the compiler. Prefer matching on parameter
  types, or expose a dedicated `@PublishedApi internal` factory for the plugin.
* `readComponent` uses `properties.first { it.name == fieldName }` and
  `getter!!`, and `reconstruct` uses `primaryConstructor!!`
  (`OpticsIrGenerationExtension.kt:333-352`). These hold for well-formed input,
  but they turn any FIR/IR disagreement into an internal compiler error. Prefer
  `?: error("…plugin invariant…")` with context, or skip the body with a reported
  error.

### 3.2 Other IR issues

* **The DSL base-optic lookup is by name only** (see §1.4). Filter by plugin
  origin.
* **No source offsets.** Lambdas use `UNDEFINED_OFFSET`
  (`OpticsIrHelpers.kt:52`), so stack traces through generated getters and setters
  have no line numbers, and stepping and coverage tools skip them. Using the
  generated declaration's offsets (the FIR fake source points at the class) would
  be friendlier.
* `visitProperty` sets `declaration.backingField = null`
  (`OpticsIrGenerationExtension.kt:124`, `:129`), but FIR already declares
  `hasBackingField = false`. This is harmless, but it hides a mismatch if one
  ever appears. Prefer a `check`.
* `buildOpticBody` infers the optic kind from the return type's classifier
  (`:183-188`). That works because FIR builds `Lens`/`Iso`/`Prism` through their
  type aliases and Fir2Ir expands them. It would be more robust to encode the kind
  explicitly, for example by using different `GeneratedDeclarationKey` objects per
  kind.
* `IrPluginContext.finderForBuiltins()` is used to find `arrow.optics` library
  symbols. Check that this is the intended finder for non-builtin library
  symbols. In particular, check whether lookups made through it are recorded for
  incremental compilation; a source-scoped finder may be more appropriate.

---

## 4. Ecosystem concerns that block "regular usage"

1. **Kotlin version lock-in.** The plugin compiles against Kotlin 2.4.21 compiler
   internals. Examples: `IrParameterKind`, `context(...)` checker signatures,
   `finderForBuiltins`, `ExperimentalTopLevelDeclarationsGenerationApi`,
   `FirResolvedDeclarationStatusImpl`-based builders. These are not binary-stable
   across Kotlin minor versions, and sometimes not across patch versions. A user
   on a different Kotlin version will get `NoSuchMethodError` or
   `AbstractMethodError` deep inside the compiler. Options:
   * publish per-Kotlin-version artifacts (as Compose did before it was bundled);
   * check the compiler version in `OpticsPluginComponentRegistrar` and fail
     early with a clear message;
   * add a small compatibility layer.

   Related: the module uses `compileOnly(kotlin("compiler"))`, while Gradle runs
   `kotlin-compiler-embeddable`. This works today, because only
   `org.jetbrains.kotlin.*` types are referenced (`KtElement` in
   `OpticsSerializableDiagnostics`). Any accidental `com.intellij.*` reference
   would break only under Gradle, so a smoke test through the real Gradle plugin
   is advisable.
2. **IDE support.** IntelliJ in K2 mode loads only bundled or allow-listed
   compiler plugins by default, and runs them with the *IDE's* Kotlin compiler,
   not the project's. Without coordination with JetBrains, users will see
   unresolved `Person.name` everywhere in the editor even though the build passes.
   Document the registry switch (`kotlin.k2.only.bundled.compiler.plugins.enabled`)
   and the version-matching caveat. Also, §2.4 matters most precisely in the IDE.
3. **Kotlin Multiplatform.**
   * `expect`/`actual`: a companion and its members generated into an
     `expect class` must be matched by the `actual`. That requires `@optics` on
     both, and both must classify the same way. Nothing guards or tests this.
   * Top-level DSL generation runs once per FIR session (common and each
     platform), so classes in common code need a test to check that their DSL is
     visible from platform code and not generated twice.
   * The suite is JVM-only (`jvmTarget = 1.8`). Given §1.2, a JS or Native smoke
     compile of the existing model is a must before claiming KMP support.
4. **Incremental compilation.** Top-level generation is an experimental API, and
   each source class gets its own synthetic file. There are no IC tests: rename a
   class, remove `@optics`, add or remove a field, or move a class between
   packages, then check for stale `XOpticsKt` classes and dependent
   recompilation. These should be covered by Gradle integration tests.
5. **Other companion-generating plugins.** Only kotlinx.serialization is
   special-cased, by short name, see §2.3. Any other plugin that generates a
   companion will conflict with ours. At least, generate the companion only when
   no other extension does: check `classSymbol.companionObjectSymbol` in
   `generateNestedClassLikeDeclaration`, which is already done, but the name is
   still advertised in `getNestedClassifiersNames`. Also document the conflict.
6. **Cosmetic.** Diagnostics for generated declarations point to
   `<working dir>/p/FooOptics.kt`, a path that does not exist (see the probe
   output). Users will be confused by errors in files they cannot open.

---

## 5. Status of review #1

| Item | Status |
|---|---|
| §1.1 runtime tests per optic kind | **Fixed.** `RuntimeTests.kt` covers lens, iso, prism, sealed lens, generic lens, `notNull` |
| §2.1 IR node sharing | **Fixed.** `reconstruct` takes factories |
| §2.2 enclosing-class visibility | Implemented, but applying it to *members* is what causes §1.1 |
| §2.3 shallow `sameType` | **Fixed.** Recursive over arguments and projections. Note that two type-parameter types still compare equal, because both have a `null` `classId`; harmless while generic parents are skipped |
| §2.4 generic sibling types | **Open**, confirmed by `-Xverify-ir` (§1.2a) |
| §2.5 eager IR symbols | **Fixed** (lazy), but see §3.1 |
| §2.6 target parsing timing | **Fixed** (raw arguments), small alias caveat (§2.3) |
| §3.1 diagnostics | **Open** (§2.3) |
| §3.2 generic sealed shared lens | **Open.** Still skipped silently (`FirOpticsExtractor.kt:149`) |
| §3.3 sealed DSL on shared lenses | **Fixed** (`dslFoci`) |
| §3.4 generic DSL / generic copy | Generic DSL done; generic copy still skipped. This is harmless, because arrow's `A.copy {}` extension takes over (probe) |
| §3.5 CLI options | Open |
| §4 structure | Partly. Still duplicated: the `@Serializable` check (`FirOpticsExtractor.kt:101` *and* `:118`), `coneOf`/`coneTypes`, two different `matches()` definitions (companion vs DSL generator), the dead `monoClassFor`, the commented `SERIALIZABLE_ANNOTATION_FQNAME`, and no caching |

---

## 6. Tests to add (each corresponds to a probe that exposed an issue)

1. Enable `-Xverify-ir=error` in `buildCompilation` for every test. Run the data
   model through a JS (or Native) compilation at least once.
2. Visibility: a private top-level class (base optic, DSL, copy), a class nested
   in a private class, a protected nested class, an internal class nested in a
   public one.
3. Name clashes: nested `A.Leaf`/`B.Leaf` in one package (**evaluate** the result,
   not just compile); a user file `LeafOptics.kt`; prism vs shared-lens name; a
   user companion member with the same name.
4. Generics: a bounded type parameter, where `W.item<Any>()` must **fail**; a
   generic multi-level sealed hierarchy whose grandchild prism is *used*; a sealed
   lens over a generic subclass, under IR verification.
5. Encapsulation: a private constructor, a private property, a private subclass
   constructor in a sealed lens, all executed on a JVM 1.8 target.
6. Diagnostics, with message assertions: an ineligible kind (`interface`, `enum`,
   `object`, `data object`); `[DSL]` alone; import alias; a foreign `Serializable`
   annotation; a missing `arrow-optics` runtime.
7. IDE-like ordering: drive the FIR generators through the Analysis API test
   infrastructure, or at least call `getCallableNamesForClass` before STATUS, for
   the sealed-interface shared property.
8. KMP: `expect`/`actual` sealed and data classes; DSL from common code used in
   platform code.

---

## 7. Prioritised recommendations

1. **Fix the invalid IR (§1.2)** and turn on `-Xverify-ir=error` in the tests.
   Without this, KMP support cannot be claimed, and JVM correctness rests on
   erasure.
2. **Fix the synthetic file naming (§1.3).** A runtime `ClassFormatError` from a
   green build is unacceptable for a stable release.
3. **Fix the visibility model (§1.1, §2.2).** Use a public companion, members
   restricted by constructor and property visibility, and no `protected` or
   foreign-file `private` top-level DSL.
4. **Add a real checker (§2.3, §1.4)** so that every "nothing happened" case
   becomes a diagnostic.
5. **Make FIR phase-safe and cached (§2.4).** Use resolved modality, the
   compiler's sealed-inheritor data, and per-symbol caches.
6. **Copy bounds and fix multi-level generic prisms (§2.1, §1.5).**
7. **Decide on the distribution story (§4):** version pinning, IDE loading,
   KMP/IC test coverage.
