# Review #1 — Arrow Optics K2 compiler plugin

Scope: `arrow-libs/optics/arrow-optics-compiler-plugin` (FIR generators, IR body
generator, shared model, test suite). The plugin compiles and all 57 ported
tests pass. This review focuses on **(a) cases missing from the tests** and
**(b) how the code is structured**, plus the latent correctness risks those gaps
hide.

Overall: the architecture is sound and idiomatic for a K2 plugin (FIR decides
signatures as companion members, IR fills bodies, correlation by
origin-key + return-type classifier). The main weaknesses are **shallow runtime
test coverage** (most generated *bodies* are never executed), a couple of
**latent IR/semantic bugs** that the current tests cannot catch, some
**spec deviations**, and a few **structural/robustness** rough edges.

---

## 1. Test coverage gaps (primary focus)

### 1.1 Most generated optic *bodies* are never executed

The ported KSP tests are overwhelmingly *compile-only* (`compilationSucceeds`)
or assert only `optic != null`. They prove the **signatures** resolve, but they
do **not** run the IR bodies the plugin generates. Concretely, grepping the
tests:

| Optic kind | `get`/`set`/`reverseGet`/`getOrModify` executed at runtime? | Where |
|---|---|---|
| **LENS** (mono data class) | ✅ yes | `CopyTest` (`Person.age.modify`, `Person.address.set/get`, `compose`) |
| **DSL** chains (mono) | ✅ yes | `DSLTests` (`…every…notNull…modify`, `at().set`) |
| **COPY** generated builder | ✅ yes | `GeneratedCopyTest` |
| **ISO** (`get`/`reverseGet`) | ❌ **never** | `IsoTests` only do `val i = …; r = i != null` |
| **PRISM** (`getOrModify`/`reverseGet`) | ❌ **never** | `PrismTests` are all `compilationSucceeds` |
| **Sealed shared-property LENS** (`when`-dispatch `set`) | ❌ **never** | `LensTests` sealed cases only do `LensSealed.property1 != null` |
| **Generic LENS** (`get`/`set`) | ❌ **never** | `LensTests`/`OptionalTests` only `!= null` |
| **Nullable focus** via `notNull` | ❌ **never** (only `!= null`) | `OptionalTests` |

This is the single biggest gap. The **sealed-property lens `set`** is by far the
most intricate body the plugin produces (`when (s) { is Sub -> Sub.copy/ctor … }`
with an exhaustive `else`, constructor reconstruction, and — for generic parents
— an unchecked cast). It is currently generated and type-checked but **never
run**, so a wrong branch, a bad reconstruction, or a wrong field would sail
through CI. The earlier hand-written tests *did* execute these; replacing them
wholesale with the KSP ports lost that coverage.

**Recommendation:** add `evals(...)`-style tests that actually exercise each
kind, e.g.

```kotlin
// sealed lens — the highest-value missing test
val l = LensSealed.property1
val r = l.get(Child2("a", 5)) == "a" &&
        l.set(Child2("a", 5), "z") == Child2("z", 5) &&
        l.set(Child1("a"), "z") == Child1("z")

// iso round-trips
val r = IsoData.field1.reverseGet(IsoData.field1.get(IsoData("x"))) == IsoData("x")

// prism
val r = PrismSealed.prismSealed1.getOrModify(PrismSealed1("x")).isRight() &&
        PrismSealed.prismSealed1.getOrModify(PrismSealed2("y")).isLeft()

// generic lens get/set at a concrete instantiation
val r = OpticsTest.field<String>().set(OpticsTest("x"), "y") == OpticsTest("y")
```

### 1.2 Input shapes never tested

- **Sealed subclass with ≥2 extra constructor fields.** Every sealed-lens test
  subclass has at most one non-uniform field (`number`, `enabled`). The
  multi-sibling reconstruction path (which has a real IR bug, §2.1) is never hit.
- **Generic data class with ≥2 fields** (so the generic `set`/`reconstruct`
  reads a *sibling* whose type must be substituted). Every generic case is
  single-field (`OpticsTest<A>(field)`, `Box<S>(s)`, `Wrapper<T>(item)`), so the
  generic sibling-substitution path (§2.4) is never exercised.
- **Target restrictions other than the one sealed `[DSL]` case.** No test for
  `@optics([LENS])` on a data class (should suppress DSL), `@optics([ISO])`,
  `@optics([PRISM])` on a data class (empty intersection → nothing generated),
  or multi-target combinations. The DSL-suppression-by-target logic
  (`dslEnabled`) is only proven on one path.
- **Multi-level sealed hierarchies** (a sealed subclass of a sealed type):
  `getSealedClassInheritors` returns *direct* inheritors only — behaviour for a
  grandchild reached through an intermediate sealed node is unspecified and
  untested.
- **`object` / `data object` subclasses' prisms executed.** `Loading` exists in
  the "nested generic" compile-only test, but no prism over an object branch is
  run.
- **Private / protected source classes**, classes with a private primary
  constructor, type parameters with interdependent bounds (`<A, B : List<A>>`),
  and **star projection in the *source* class** combined with a prism.

### 1.3 No negative/diagnostic tests with messages

All failure tests are bare `compilationFails()` (no message assertion), and they
fail for *incidental* reasons (unresolved reference at the use site), not because
the plugin reports anything. There is **no test that annotating an ineligible
type** (an `enum class`, a normal `class`, a non-`@JvmInline` class) is rejected
— and indeed the plugin does **not** reject it (§3.1). A reader cannot tell from
the tests whether "ineligible class" is handled at all. At minimum add tests
pinning the *current* behaviour, and ideally a real diagnostic + message test.

### 1.4 No law/round-trip checks

Nothing asserts the lens laws (`get(set(s,a)) == a`, `set(s, get(s)) == s`) or
prism/iso round-trips. Given the bodies are hand-built IR, a couple of
property-style round-trip tests per kind would be cheap, high-value insurance.

---

## 2. Latent correctness risks (not caught by current tests)

### 2.1 IR node sharing in `reconstruct` / `sealedSet`  ⚠️

`OpticsIrGenerationExtension.reconstruct` (line ~296) takes a single
`instance: IrExpression` and reuses **the same node** as the dispatch receiver of
every sibling getter:

```kotlin
ctor.parameters.filter { it.kind == Regular }.forEach { param ->
  val arg = if (param.name == overrideName) overrideValue
            else readComponent(source, param.name, param.type, instance) // <- same `instance` node reused
  call.arguments[param] = arg
}
```

and `sealedSet` (line ~242) builds `val cast = irImplicitCast(irGet(instance), subType)`
once and passes that one `cast` node into `reconstruct`, so a subclass with ≥2
reconstructed fields shares the `IrTypeOperatorCall` node across multiple parents.
Sharing IR nodes violates IR tree invariants (each node should have one parent).

It happens to work today because:
- `CopyTest` exercises the data-class path (`Person.address.set`) where the shared
  node is an `IrGetValue`, which the **JVM** backend tolerates; and
- no sealed-lens `set` is executed at all (§1.1), and no subclass has ≥2 extra
  fields (§1.2), so the shared-`cast` case never runs.

This is fragile: it would likely break under IR validation
(`-Xverify-ir`), on the JS/Native backends, or as soon as a multi-field sealed
`set` is actually executed. **Fix:** don't pass pre-built expressions; pass the
`IrValueParameter`/`IrVariable` (or a `() -> IrExpression` factory) and call
`irGet`/`irImplicitCast` fresh at each use. For `sealedSet`, bind the cast to an
`irTemporary` and `irGet` it per field.

### 2.2 Visibility only combines source + companion, not enclosing classes

`mostRestrictive(source.visibility, owner.visibility)` (companion generator,
lines 120/132) ignores the visibilities of *enclosing* classes that algo §3.3
calls for. For **companion members** this is mostly harmless (the container
already constrains member visibility), but the **top-level DSL extensions**
(`OpticsDslGenerator`, line 92) and **COPY** (`OpticsCopyGenerator`) use only
`source.visibility`. A `public` data class nested inside a `private`/`internal`
outer class can therefore get a top-level extension that is *more visible than
the types it mentions*, which is an "exposed declaration" error — or, worse, a
silently over-broad public API. The `#3869` test only assigns a base member lens
to an `internal val`, so it never stresses this. **Fix:** compute visibility by
folding `mostRestrictive` over the source *and all its containing classifiers*,
and use that for the DSL/COPY top-level declarations too.

### 2.3 `sameType` (sealed-lens uniformity) is shallow

`FirOpticsExtractor.sameType` (line 151) compares only `classId` +
`isMarkedNullable`, ignoring type arguments and variance. Two subclasses
declaring the property as `List<String>` vs `List<Int>` would be considered
"uniform". In practice Kotlin's own override-type checking shields most cases,
and the one "ignoring changed types" test passes for an *unrelated* reason (the
parent is generic, so `sealedLensFoci` bails out entirely — see §3.2), so this
predicate is barely exercised. It should compare full resolved types (e.g. via
the type-context `equalTypes`, or at least recurse into type arguments).

### 2.4 Generic sibling reconstruction uses unsubstituted types

In the generic LENS path the constructor reconstruction reads siblings with
`param.type` (line ~309), which is expressed in the *source class's* type
parameters, while the call actually runs with the *function's* type parameters.
JVM erasure hides this for the single-field generic classes in the suite, but a
multi-field generic data class would produce IR whose argument types disagree
with the (substituted) constructor-parameter types — again likely fine on JVM,
likely flagged by IR verification. A multi-field generic test (§1.2) plus
substituting sibling types would close this.

### 2.5 Eager symbol resolution can crash unrelated compilations

`OpticsIrSymbols` (line 58) resolves every `arrow.optics` symbol with `!!`
*unconditionally* in `IrGenerationExtension.generate`, before checking whether
the module contains any `@optics` class. If the plugin is ever applied to a
module that doesn't depend on `arrow-optics`, `referenceClass(PLENS)!!` throws and
crashes the compiler for *every* file. The test harness always has arrow-optics
on the classpath, so this never surfaces. **Fix:** make `OpticsIrSymbols` lazy
(or construct it only when at least one generated declaration is found), and
prefer a clean diagnostic over `!!` when the runtime is missing.

### 2.6 Target parsing depends on annotation-argument resolution timing

`requestedTargets` reads `resolvedAnnotationsWithArguments` during FIR
*generation* and walks the `targets` expression with a `FirVisitorVoid`,
matching callee names `"ISO"/"LENS"/…`. If argument resolution isn't available at
that phase (version-dependent), it silently returns the empty set → "generate
everything", i.e. a *silent* wrong answer rather than a failure. It also matches
the enum-entry *simple name* anywhere in the subtree, so a hypothetical
`targets = someAliasFor(OpticsTarget.DSL)` or a constant could be mis-read. Low
probability, but worth a defensive comment and a test that pins
`@optics([OpticsTarget.LENS])` behaviour.

---

## 3. Deviations from the algorithm spec (`arrow-optics-algo.md`)

### 3.1 No diagnostics at all (algo §12)

- **Ineligible class** (not data/value/sealed) is a hard error in the spec. Here
  it silently produces an (empty) companion and no optics; the only feedback is a
  later unresolved-reference at the use site. A `FirAdditionalCheckersExtension`
  with a `FirRegularClassChecker` reporting the §12 errors is missing. (This was a
  deliberate, documented descope, but it is a real behavioural divergence and is
  untested in either direction.)
- **Missing companion** is intentionally *not* an error (the plugin
  auto-generates one). Documented and reasonable, but note it changes the
  observable contract vs the KSP processor.
- The §5.2 *informational notes* (non-uniform property, non-data subclass, etc.)
  are silently swallowed.

### 3.2 Generic sealed types: shared-property lens silently skipped

`sealedLensFoci` returns empty whenever the parent has type parameters
(line 121). So for a generic sealed parent with a uniform abstract property, **no
lens is generated** even though §5.2 allows it. The "ignoring changed types" test
*relies* on this skip to fail, which masks the missing feature: the test would
still pass even if `sameType` were broken. Generic prisms (§6) *are* implemented;
generic shared-property lenses are not, and there is no test asserting either the
intended behaviour or the current limitation.

### 3.3 Sealed types get DSL variants for their shared-property lenses

Algo §8.4 says a sealed type's DSL family contains **only the prism variants**;
shared-property lenses do *not* get DSL composition helpers. But
`OpticsDslGenerator.generateProperties` iterates **all** `foci` (line 71),
including the sealed-lens foci, emitting `Lens/Optional/Traversal` DSL extensions
for them. These compile (the base lens exists) so nothing breaks, but it is extra
surface area not in the spec and could cause overload-resolution surprises. No
test checks the §8.4 restriction.

### 3.4 Generic DSL and generic COPY unimplemented

`OpticsDslGenerator` and `OpticsCopyGenerator` both filter to
`typeParameterSymbols.isEmpty()`. Generic DSL chains (e.g. drilling into a
`Box<S>`) and `@optics.copy` on a generic class are not generated. Documented as
a limitation; no test exercises or pins it. (Generic value-class DSL is broken in
KSP too, so parity is partial.)

### 3.5 `inline` option (§3.9) and config flags (§2.2) absent

`OpticsCommandLineProcessor` exposes no options, so the `inline`-optics flag and
the "disable companion requirement" flag from the spec don't exist. Fine for now,
but the command-line processor is dead scaffolding until then.

---

## 4. Structure / maintainability

**Good:** clear phase split; a single source of truth for names
(`OpticsNames`); a compiler-type-free `OpticsModel` (kinds, target computation,
visibility, name lowering); IR symbol resolution centralised in
`OpticsIrSymbols`; the IR side cleverly recovers `source`/`focus` types from the
generated return type, avoiding any FIR→IR side channel.

Rough edges:

- **Dead code in `OpticsNames`.** `LENS`, `ISO`, `PRISM`, `OPTIONAL`,
  `TRAVERSAL` (the type-alias `ClassId`s) and `OPTICS_TARGET` are never
  referenced — generation uses the `P*` interfaces exclusively. Remove them or
  use them.
- **`FirFocus.focusType` is overloaded in meaning.** Its KDoc says "for a
  monomorphic parent", but it is also fed through `substituteOrSelf` in the
  generic LENS/ISO path, while prisms ignore it in favour of `subclass` +
  `refinedSource`. One field with three regimes (lens/iso source-param-relative,
  prism `Sub<*>`, prism-generic-unused) is a readability trap. Consider modelling
  prism foci as a separate type, or document each field's regime precisely.
- **Duplicated generic-function construction.** The two `createMemberFunction`
  blocks in `generateFunctions` (lines 136–171) are near-identical; only the
  type-parameter source (subclass vs parent) and the source/focus computation
  differ. Extract a helper taking `(typeParamSymbols, returnTypeBuilder)`.
- **`foci`/`effectiveTargets` recomputed repeatedly with no caching.**
  `getCallableNamesForClass`, `generateProperties`, `generateFunctions`, plus the
  DSL generator's `annotatedSources`/`getTopLevelCallableIds`/`generateProperties`
  each re-run focus extraction, which re-resolves annotations, re-scans sealed
  inheritors and re-resolves supertypes. On a large module this is O(members ×
  rescans). FIR offers `FirCache`/session-scoped caching; at least memoise
  `effectiveTargets` per symbol.
- **Three separate FIR generators each enumerate annotated symbols.** The
  companion, DSL, and COPY generators independently build predicates and
  re-derive foci. A shared "model of what to generate for class X" computed once
  would reduce duplication and the recomputation above.
- **`coneTypes()` / `sCone()` duplication.** The "FIR type-parameter →
  `ConeTypeParameterTypeImpl`" helper is reimplemented in
  `OpticsCompanionGenerator` and `OpticsDslGenerator`. Hoist into a shared util.
- **Pervasive `!!` and `.first { }` in IR.** `primaryConstructor!!`,
  `prop.getter!!`, `properties.first { … }`, `referenceClass(...)!!`. Most are
  "can't happen" given the FIR contract, but they convert contract violations
  into compiler crashes instead of diagnostics. A few guarded `?: return` with a
  comment (or an internal error reporter) would be friendlier.
- **Test harness is black-box only.** `Compilation.kt`'s `evals` loads a single
  `SourceKt` class and reads one top-level field; there is no way to inspect the
  *generated* declarations (as the KSP suite could inspect generated sources).
  That makes the §1.1 runtime tests the only line of defence — another reason to
  add them.

---

## 5. Prioritised recommendations

1. **Restore runtime tests for every optic kind** (§1.1) — especially the
   sealed-property lens `get`/`set`, ISO round-trip, prism `getOrModify`, and a
   generic lens `set`. Highest value, lowest effort, and would immediately expose
   §2.1/§2.4.
2. **Fix IR node sharing** in `reconstruct`/`sealedSet` (§2.1) — pass value
   symbols / factories, not pre-built nodes; bind the sealed cast to a temporary.
3. **Add a multi-field sealed subclass and a multi-field generic data class**
   to the suite (§1.2) to lock down reconstruction.
4. **Decide on diagnostics** (§3.1): either implement the §12 checker (ineligible
   class at least) or add tests pinning the current silent behaviour so it is
   intentional and visible.
5. **Make `OpticsIrSymbols` lazy / guarded** (§2.5) so the plugin degrades
   gracefully without `arrow-optics`.
6. **Tidy structure** (§4): delete dead `OpticsNames` entries, de-duplicate the
   two generic-function branches and the cone-type helper, and memoise
   `effectiveTargets`/`foci`.
7. **Reconcile with the spec** the sealed-DSL-on-shared-lenses behaviour (§3.3)
   and document/skip generic shared-property lenses deliberately (§3.2).
