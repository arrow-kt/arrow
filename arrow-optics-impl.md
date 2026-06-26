# Arrow Optics K2 Compiler Plugin — Implementation Plan

## Implementation status (as of this change)

Implemented end-to-end (FIR signature generation **as companion members** + IR body generation), each with passing `kotlin-compile-testing` tests under `src/test`:

| Feature | Mono | Generic | Tests |
|---|---|---|---|
| **LENS** (data class fields) | ✅ | ✅ (`fun field()` form) | `LensTests` |
| **LENS** nullable focus (`Optional` via `notNull`) | ✅ | — | `LensTests` |
| **LENS** sealed shared abstract property (§5.2, `when`-dispatch `set`) | ✅ | ❌ (mono parents only) | `LensTests` |
| **ISO** (value classes) | ✅ | ✅ | `IsoTests` |
| **PRISM** (sealed subclasses, `Prism.instanceOf`) | ✅ | ❌ (mono parents only) | `PrismTests` |
| **DSL** composition extensions (top-level, §8.2 variant matrix) | ✅ | ❌ (mono sources only) | `DSLTests` |

Key infrastructure in place: companion-member generation (`OpticsCompanionGenerator`), top-level DSL extension generation (`OpticsDslGenerator`), the IR body generator (`OpticsIrGenerationExtension` + `OpticsIrHelpers`), the shared model (`OpticsModel`, `OpticsNames`), and the FIR focus extractor (`FirOpticsExtractor`). The build wires both the FIR and IR phases (`OpticsPluginWrappers`), and a `kotlin-compile-testing` harness (`Compilation.kt`) runs the plugin.

**Not yet implemented (documented follow-ups):**
- **COPY** (`@optics.copy`, §9) — blocked on constructing the `context(Copy<S>) S.Companion.(S) -> Unit` function type in FIR (context parameters) and invoking it in IR; flagged as the most fragile milestone (§2.8, §7.6).
- **Generic PRISM** (§6 refined-supertype + free-var union) and **generic sealed-lens / generic DSL** — the generic-parent type-parameter logic differs from the LENS/ISO mirroring and is gated off for now.
- **§12 diagnostics** — custom FIR error/warning factories (ineligible class, non-uniform property, …). Ineligible classes currently generate no optics rather than reporting a hard error.

Everything below is the original design; sections on COPY/generic-prism/diagnostics describe the intended (not-yet-built) behaviour.

---

This document is the complete, file-by-file implementation plan for replacing the KSP source generator with a **K2 compiler plugin** (FIR declaration generation + IR body generation) in module `arrow-libs/optics/arrow-optics-compiler-plugin`.

The behavioral specification of *what* must be produced is `arrow-optics-algo.md` (§ references below point at it). The one deliberate divergence from that spec is restated explicitly in §0.

---

## 0. The key reinterpretation: companion *members*, not companion *extensions*

The KSP generator emits every base optic as an **extension on `Source.Companion`**:

```kotlin
val Person.Companion.age: Lens<Person, Int> get() = …
```

This plugin instead generates each base optic as a **real member declaration inside `Person.Companion`**:

```kotlin
// conceptually, inside Person's companion object:
val age: Lens<Person, Int> = Lens(get = …, set = …)
```

Why this is possible and preferable:

- FIR's `FirDeclarationGenerationExtension` can add member callables to a class via `getCallableNamesForClass` / `generateProperties` / `generateFunctions`, with `context.owner` being the companion's `FirClassSymbol`. The existing `OpticsCompanionGenerator` already *creates the companion object itself* as a generated nested class; we extend the same generator (or a sibling) to populate it.
- A member `val age` on `Person.Companion` is resolved by user code exactly as `Person.age` (companion members are accessed through the class name), so the **user-facing surface syntax is identical** to the KSP version (`Person.age`, `Thing.`object``, `Source.field<String>()`).

Where a companion member is **impossible**, we keep extensions:

| Declaration | KSP form | Plugin form |
|---|---|---|
| Base ISO/LENS/PRISM | `val S.Companion.x` (ext) | **member of `S.Companion`** |
| Shared-property LENS (sealed) | `val S.Companion.p` (ext) | **member of `S.Companion`** |
| DSL composition helpers (§8) | `val <__S> OuterOptic<__S, S>.x` (ext) | **stays an extension** — receiver is an arbitrary outer optic type, not the companion; cannot be a companion member. Generated as **top-level** callables. |
| COPY builder (§9) | `fun S.copy(block…)` (ext on `S`) | **stays an extension on `S`** — generated as **top-level** callable. |

Consequences that ripple through the plan:

- **Base optics**: declared as companion members in FIR. The §3.4 property-vs-function split still applies. For the **generic** case the member must be a *function with its own type parameters* — companion-object **members can be functions with type parameters** (`fun <A> field(): Lens<Source<A>, A>`), so this is fine; a member *property* cannot introduce type parameters, which is exactly why §3.4 already switches to a function in the generic case.
- **No imports / aliasing logic is needed.** §3.8's fully-qualified-name + alias-on-collision machinery is a *text-generation* concern. In FIR/IR we build cone types and IR symbol references directly against `ClassId`/`CallableId`; there is no rendered source, so collisions are structurally impossible. Backticking (§3.2) likewise disappears: a `Name` carries the raw identifier, and the compiler back-end handles keyword identifiers at the symbol level. **We never render strings.** (This is a major simplification versus KSP.)
- The `inline` option (§3.9) is dropped initially (it was a text modifier; can be revisited as a `status{}` flag later).

---

## 1. Architecture overview — the two-phase split

The plugin runs in two compiler phases:

1. **FIR (frontend) — declaration generation + checkers.** Decides *which* declarations exist and their *signatures* (receiver, type parameters, value parameters, return cone type, containing symbol). Bodies are left empty. Also runs **checkers** that emit the §12 diagnostics. Driven by `FirDeclarationGenerationExtension` subclasses and a `FirAdditionalCheckersExtension`.
2. **IR (backend) — body generation.** Walks generated declarations (`origin is IrDeclarationOrigin.GeneratedByPlugin && pluginKey == Key`), asserts `body == null`, and fills in each body using `DeclarationIrBuilder`. Driven by an `IrGenerationExtension`.

### 1.1 Per-kind decision table

For each optic kind, what FIR declares and what IR builds:

| Kind | Where (FIR) | Signature (FIR) | IR body |
|---|---|---|---|
| **LENS** (data class field) | member of `S.Companion` | mono: `val name: Lens<S, F>`; generic: `fun <Tp…> name(): Lens<S<Tp…>, F>` | `Lens(get = { s -> s.name }, set = { s, v -> s.copy(name = v) })` |
| **LENS** (sealed shared prop, §5.2) | member of `S.Companion` | same as above, focus = property type | `Lens(get = { s -> s.prop }, set = { s, v -> when(s){ is Sub1 -> s.copy(prop=v); … } [as S] })` |
| **ISO** (value class) | member of `S.Companion` | mono: `val name: Iso<S, F>`; generic: `fun <Tp…> name(): Iso<S<Tp…>, F>` | `Iso(get = { s -> s.name }, reverseGet = { v -> S(v) })` |
| **PRISM** (sealed subclass) | member of `S.Companion` | mono: `val sub: Prism<S, Sub>`; generic: `fun <free…> sub(): Prism<Refined, Sub<…>>` where `Refined` is the subclass's *declared supertype* and `free…` is the union per §6 | `Prism.instanceOf<S_or_Refined, Sub>()` (reified) |
| **DSL** (one per base optic kind variant per §8.2) | **top-level** extension | `val <__S [,Tp…]> OuterOptic<__S, S[<Tp…>]>.name: OuterOptic<__S, F> get()` (or `fun` form) | `this + S.name` (compose the receiver with the base companion optic) |
| **COPY** (`@optics.copy`) | **top-level** extension on `S` | `fun [<Tp…>] S[<Tp…>].copy(block: context(Copy<S>) S.Companion.(S) -> Unit): S` | `val me = this; return me.copy { block(this, S.Companion, me) }` using `arrow.optics.copy` |

`F` = focus type (nullable verbatim per §3.6). `OuterOptic` ∈ {`Iso`,`Lens`,`Prism`,`Optional`,`Traversal`} per the §8.2 matrix.

### 1.2 Phase data flow

FIR cannot pass arbitrary objects to IR. Both phases independently re-derive the **focus model** from the source `FirClassSymbol` / IR `IrClass`. We therefore put focus extraction in a **session-independent, symbol-driven module** with two thin adapters (one reading FIR symbols, one reading IR symbols), or — simpler and recommended — re-derive in each phase from the public symbol APIs since the logic is small. The plan uses **shared pure model classes** (data classes describing targets/kinds/foci by `Name`/`ClassId`, no compiler types) plus per-phase extractors that the FIR generator and IR generator each call.

To correlate an IR declaration back to its meaning, IR matches on **name + containing class + signature shape** (it re-runs extraction on the owner `IrClass` and finds the focus whose generated name equals the declaration's name). No cross-phase state is stored.

---

## 2. Lambda bodies in IR — the concrete hard part

All bodies are built with `DeclarationIrBuilder(pluginContext, symbol).irBlockBody { +irReturn(expr) }`. External references are resolved once via `pluginContext.referenceClass/referenceFunctions/referenceConstructors/referenceProperties` against `ClassId`/`CallableId` constants for `arrow.optics`. The recurring difficulty is **building `IrFunctionExpression` lambdas** to pass as `get`/`set`/`reverseGet`.

### 2.1 Building a lambda (the core helper)

A lambda passed to `Lens(get = …, …)` is an `IrFunctionExpression` of `kotlin.FunctionN` type wrapping an `IrSimpleFunction` (origin `LOCAL_FUNCTION_FOR_LAMBDA`). Plan a single helper:

```
fun IrBuilder.buildLambda(
  parameterTypes: List<IrType>,
  returnType: IrType,
  body: IrBlockBodyBuilder.(params: List<IrValueParameter>) -> Unit,
): IrFunctionExpression
```

Implementation outline:
- `pluginContext.irFactory.buildFun { origin = LOCAL_FUNCTION_FOR_LAMBDA; name = SpecialNames.NO_NAME_PROVIDED; visibility = LOCAL; returnType = returnType }`.
- Add value parameters (one per `parameterTypes`) via `addValueParameter`.
- Set `parent` to the enclosing generated function/property accessor.
- Set `fn.body = DeclarationIrBuilder(...).irBlockBody { body(fn.valueParameters) }`.
- Wrap: `IrFunctionExpressionImpl(startOffset, endOffset, type = kFunctionNType(parameterTypes + returnType), function = fn, origin = LAMBDA)`. The function type is obtained via `pluginContext.irBuiltIns.functionN(n).typeWith(parameterTypes + returnType)`.

This helper is used for every `get`/`set`/`reverseGet`.

### 2.2 LENS body (data class)

```
Lens.invoke(
  get     = buildLambda([S]) { (s) -> +irReturn(irCall(prop.getter)(receiver = irGet(s))) },
  set     = buildLambda([S, F]) { (s, v) -> +irReturn(irCall(S.copy)(receiver = irGet(s), arg name=v)) }
)
```

- `Lens(...)` resolves to `PLens.Companion.invoke` — `referenceFunctions(CallableId(PLens.Companion classId, Name("invoke")))`, pick the 2-arg `get/set` overload. Receiver of the call is `irGetObject(PLens.Companion)`.
- `s.name` getter: `referenceProperties` on the source class property, call its getter with dispatch receiver `irGet(s)`. (For a primary-ctor `val`, the property symbol exists on the source `IrClass`.)
- `s.copy(name = v)`: the data class's synthetic `copy` is `referenceFunctions(CallableId(sourceClassId, Name("copy")))`. It has **one value parameter per component, all with default values**; we set only the relevant argument by index and rely on IR default-argument handling. **Trickiness:** in IR you cannot omit defaulted args by name the way source can; you must either supply all arguments (re-reading every other component from `s`) or emit the call with `putValueArgument(i, value)` only for the target index and leave others null *iff* the `copy` symbol's parameters carry `hasDefaultValue` and the back-end inserts a `$default` stub call. The robust approach: **call the `copy$default` synthetic** with a bitmask, OR—simpler and recommended—**reconstruct via the primary constructor**: `S(comp0 = s.c0, …, name = v, …)` reading each other component through its getter. Recommended: use the **constructor reconstruction** for data classes (deterministic, no `$default` mask handling), reading siblings via their property getters. Document both; default to constructor reconstruction.

### 2.3 LENS body (sealed shared property, §5.2)

`get` is `s.prop` (abstract property getter). `set` builds an `irWhen`:

```
set = buildLambda([S, F]) { (s, v) ->
  val branches = subclasses.map { sub ->
    irBranch(
      condition = irIs(irGet(s), sub.defaultTypeStarProjected),
      result    = subReconstruct(irImplicitCast(irGet(s), subType), prop, v)  // sub copy/ctor with prop = v
    )
  }
  val whenExpr = irWhen(type = S_or_Star, branches)   // exhaustive over sealed
  +irReturn( if (parentGeneric) irImplicitCast(whenExpr, S<Tp…>) else whenExpr )
}
```

- Each branch reconstructs the subclass with `prop = v` (constructor reconstruction as in §2.2, reading the subclass's other components from the cast `s`).
- **Unchecked cast (§5.2 generic):** when the parent is generic, each branch yields a star-projected `Sub<*>`; the whole `when` is `irImplicitCast`-ed to `S<Tp…>`. There is no `@Suppress` needed in IR (suppression is a frontend/source concern; the IR cast is unchecked by construction). Use `IrTypeOperator.IMPLICIT_CAST` (or `CAST`); document that the generated IR is trusted.
- `irIs` uses the subclass's type **star-projected** for generic parents.
- Exhaustiveness: provide all sealed inheritors; no `else` branch. Obtain inheritors in IR via `IrClass.sealedSubclasses`.

### 2.4 ISO body (value class)

```
Iso.invoke(
  get        = buildLambda([S]) { (s) -> +irReturn(irCall(prop.getter)(irGet(s))) },
  reverseGet = buildLambda([F]) { (v) -> +irReturn(irCall(S.primaryConstructor)(irGet(v))) }
)
```
`Iso(...)` → `PIso.Companion.invoke`. Value-class constructor call is an ordinary `irCallConstructor`.

### 2.5 PRISM body

The entire body is `Prism.instanceOf<S, Sub>()` — the **inline reified** factory on `PPrism.Companion`.

- `referenceFunctions(CallableId(PPrismCompanionClassId, Name("instanceOf")))` and pick the **reified** overload (the one with a reified type parameter, no `KClass` value parameter).
- Build `irCall(instanceOf)` with dispatch receiver `irGetObject(PPrism.Companion)`, and **set its two type arguments**: `putTypeArgument(0, sourceType)` and `putTypeArgument(1, subType)`. Because the callee is `inline`, the back-end inlines `instanceOf` and resolves the `reified B` at the call site from the supplied type argument — **this is the key point**: an IR call to an inline-reified function must carry concrete type arguments, which it does here. **Risk:** confirm the inliner runs on plugin-generated calls (it runs in a standard lowering after IR generation, so it does). Alternatively, to avoid reliance on reified inlining, generate the `instanceOf(klass = SubClass::class)` overload by emitting an `IrClassReference`; document this as the fallback if reified inlining misbehaves.

### 2.6 DSL body (composition)

`this + S.name`:
- The receiver is the extension receiver value parameter (kind `ExtensionReceiver`): `irGet(function.extensionReceiverParameter!!)`.
- `S.name` is the **base companion optic**: `irGetObject(S.Companion)` then `irCall(base getter)` (mono) or `irCall(base function)()` with the source type args (generic).
- `+` is `plus` on the outer optic kind: `referenceFunctions(CallableId(outerOpticClassId, Name("plus")))`. Build `irCall(plus)` with dispatch receiver = the extension receiver, value arg 0 = the base optic expression. **Trickiness:** there are several `plus` overloads (per kind) with `in`/`out` projected parameters; select by the outer-optic kind that matches the generated variant. Set type arguments (`C`, `D`) to the focus type.

### 2.7 COPY body

```
val me = irTemporary(irGet(extensionReceiver))           // val me = this
val innerLambda = buildLambda([Copy<S>]) { (copyCtx) ->  // me.copy { block(this, S.Companion, me) }
   +irCall(block.invoke)(
       receiver = irGet(blockParam),          // block is the value param
       contextArg = irGet(copyCtx),           // context(Copy<S>)
       extReceiver = irGetObject(S.Companion), // S.Companion.( ... )
       valueArg   = irGet(me)                  // (me)
   )
}
+irReturn(irCall(arrowOpticsCopy)(receiver = irGet(me), lambda = innerLambda))
```

- `arrow.optics.copy` = top-level `referenceFunctions(CallableId(FqName("arrow.optics"), null, Name("copy")))`.
- The `block` parameter type is a **context-parameter function type** `context(Copy<S>) S.Companion.(S) -> Unit`. Building this *type* in FIR is the hard part (see §3 & §9); invoking it in IR means calling its synthetic `invoke` with the context receiver, extension receiver, and value argument in the right slots. **This is the single trickiest piece** and is the last milestone; it depends on context-parameters being enabled (`-Xcontext-parameters`).

### 2.8 Trickiest IR pieces, ranked
1. **COPY** context-parameter function type + invocation (§2.7, §9).
2. **Data-class `set`** without a clean `copy(name=v)` — use constructor reconstruction (§2.2).
3. **Sealed `set`** exhaustive `irWhen` + unchecked cast for generics (§2.3).
4. **`Prism.instanceOf` reified** call emission (§2.5).
5. **DSL `plus` overload selection** and extension-receiver `irGet` (§2.6).
6. **Lambda construction** helper correctness (parent linking, function type) (§2.1).

---

## 3. Shared infrastructure (pure model + extractors)

### 3.1 Focus / target / kind model (compiler-type-free)

Pure data classes (no FIR/IR imports) so both phases share them:

```
enum class ClassKind { DATA, VALUE, SEALED, INELIGIBLE }
enum class OpticKind { ISO, LENS, PRISM }            // base optics actually generated
data class Focus(
  val opticName: Name,            // §3.2 name rule already applied (lowercased-first for prism)
  val focusClassId/coneShape,     // described abstractly; resolved per phase
  val nullable: Boolean,
  val sourceComponentName: Name?, // for lens get/set
  val subclass: ClassRef?,        // for prism / sealed-lens dispatch
)
data class OpticDecl(val kind: OpticKind, val focus: Focus, val isFunction: Boolean /*§3.4*/, val typeParams: List<TpModel>)
```

Type/cone construction is **not** stored here; each phase builds cone (FIR) or `IrType` (IR) from the source symbol on demand. The model only carries names, nullability flags, and which source component/subclass each focus maps to.

### 3.2 Focus extraction (read once per phase)

From the source class symbol:
- **Data class** → `primaryConstructor.valueParameters` → one LENS focus each (name = param name; focus type = param type, nullability preserved §3.6).
- **Value class** → single primary-ctor param → one ISO focus.
- **Sealed** → (a) **PRISM**: one focus per sealed inheritor (`getSealedClassInheritors` in FIR / `sealedSubclasses` in IR); optic name = subclass simple name, first letter lowercased (§3.2). (b) **shared-prop LENS** per §5.2 algorithm:
  1. Collect abstract properties with **no extension receiver**. None → note, skip.
  2. Collect inheritors; **any non-data subclass** → note, skip the lens path (PRISM unaffected, §10).
  3. Partition uniform vs non-uniform (same name + exact resolved type incl. nullability across **all** subclasses). Non-uniform → note, ignore that property.
  4. Any uniform property **not a primary-ctor param** in some subclass → note, skip that property.
  5. Survivors → LENS foci with subclass list (star-projected per-subclass type args).

### 3.3 Companion-vs-extension decision

- Base ISO/LENS/PRISM → **companion member** (this plan's divergence, §0).
- DSL → **top-level extension** (§8).
- COPY → **top-level extension on `S`** (§9).

### 3.4 Property-vs-function decision (§3.4)

`isFunction = sourceClass.typeParameters.isNotEmpty()`. Property when monomorphic, function (carrying re-declared type params) when generic. For DSL/COPY the same rule applies, with the extra `__S` type parameter (DSL) or the class's params (COPY) always present.

### 3.5 Type-parameter declaration vs reference (§3.5)

- **Declaration form**: re-declare each source type parameter on the generated function via FIR `typeParameter(name, variance = INVARIANT, isReified = false){ bound(...) }`. **Variance dropped** (functions can't carry `out`/`in`). **Upper bounds preserved**, except a trivial `Any?` bound is omitted.
- **Reference form**: when mentioning `S<Tp…>` build a cone type with the freshly declared type-parameter symbols as arguments. Star projection in source → star projection in the built cone.
- **PRISM specialisation (§6)**: source type is the subclass's *declared supertype* (`Refined`), and the generated function's type params are the **union** of (free type vars in `Refined`'s arguments) ∪ (subclass's own type params). Compute by walking the subclass's supertype reference for the sealed parent.

### 3.6 Visibility (§3.3)

Most-restrictive combine of companion visibility, source-class visibility, and all enclosing-class visibilities. Combine pairwise: `public` identity; `private` dominates; `internal`+`protected` → `private`; `local` propagates. Apply via `status { visibility = computed }` in the FIR DSL builders. (Note the existing `OpticsCompanionGenerator` already sets companion visibility to the source's `rawStatus.visibility`.)

### 3.7 Naming (§3.2) — no backticking needed

Optic name is a `Name` (`Name.identifier(...)`). LENS/ISO = component name verbatim. PRISM = subclass simple name with first char lowercased. **No backticks** — `Name` holds the raw identifier and the back-end emits valid bytecode for keyword names. Keyword handling and the lambda-parameter naming rules of §3.2 are irrelevant (lambda params are anonymous in IR).

### 3.8 Nullability (§3.6)

Focus cone/`IrType` carries `isMarkedNullable` directly from the source component type. No `Optional` is generated; `notNull` promotion is the library's job (§7). Nothing to do beyond preserving nullability.

### 3.9 Target-set computation (§2.3) intersected with kind

Read `@optics(targets)`: map ISO/LENS/PRISM/DSL; drop OPTIONAL; empty → `{ISO,LENS,PRISM,DSL}`. Intersect with kind:
- sealed → `{PRISM, LENS, DSL}`
- value → `{ISO, DSL}`
- data → `{LENS, DSL}`
Add COPY iff `@optics.copy` present. Implement as a pure function `computeTargets(kind, annotationTargets, hasCopy): Set<Target>`.

---

## 4. File-by-file plan

All paths under `arrow-libs/optics/arrow-optics-compiler-plugin/`.

### New shared files — `src/main/kotlin/arrow/optics/plugin/`

| File | Responsibility | Key declarations |
|---|---|---|
| `OpticsNames.kt` | Central `ClassId`/`CallableId`/`FqName` constants for arrow-optics API. | `PLENS_COMPANION_INVOKE`, `PISO_COMPANION_INVOKE`, `PPRISM_COMPANION_INSTANCE_OF`, `LENS_CLASS_ID`, `OPTIONAL/TRAVERSAL/PRISM/ISO_CLASS_ID`, `PLUS` callable ids per kind, `ARROW_OPTICS_COPY`, `COPY_CLASS_ID`, `OPTICS_ANNOTATION_FQNAME` (move from generator), `OPTICS_COPY_ANNOTATION_FQNAME`, `OPTICS_TARGET_*`. |
| `model/OpticsModel.kt` | Compiler-type-free model. | `ClassKind`, `OpticKind`, `Target`, `Focus`, `OpticDecl`, `computeTargets(...)`, `lowercaseFirst(Name)`, visibility-combine helper `mostRestrictive(...)`. |

### New FIR files — `src/main/kotlin/arrow/optics/plugin/fir/`

| File | Responsibility | Key declarations |
|---|---|---|
| `OpticsCompanionGenerator.kt` *(modify existing)* | Keep creating the empty companion. **Add base-optic member generation.** | Extend `getCallableNamesForClass` (when `owner` is the generated/existing companion of an `@optics` class, return the optic `Name`s computed from the source); add `generateProperties(callableId, context)` for monomorphic base optics and `generateFunctions(callableId, context)` for generic ones. Use `createMemberProperty(owner, Key, name, returnType, isVal=true)` / `createMemberFunction(...)` from `org.jetbrains.kotlin.fir.plugin`, computing the return cone type via a new `FirFocusExtractor`. Reuse existing `Key`, predicate, `isGeneratedOpticsCompanion`. **Important:** to attach members to a *user-declared* companion (not only the generated one), the predicate match must be on the **source class**; resolve the source class from the companion via `owner.getContainingClassSymbol()` / outer class, and only emit when that source matches the predicate. |
| `FirFocusExtractor.kt` | FIR adapter reading a `FirRegularClassSymbol` to produce `List<OpticDecl>` + the cone-type builders. | `extract(sourceClassSymbol, session): List<OpticDecl>`; helpers `coneForFocus`, `coneForSource(tpSymbols)`, sealed-inheritor scan via `sourceClassSymbol.getSealedClassInheritors(session)`, abstract-property scan, uniformity check (§5.2). Reused by checker. |
| `OpticsDslGenerator.kt` | Generates the **top-level** DSL extension callables (§8). | `FirDeclarationGenerationExtension`; `getTopLevelCallableIds()` returns the DSL callable ids; `generateProperties`/`generateFunctions` build extension props/funcs with `createTopLevelProperty`/`createTopLevelFunction`, `extensionReceiverType { tps -> OuterOptic<__S, S> }`, an extra `__S` type parameter, `hasBackingField = false`. Emits one variant per §8.2 matrix entry. Needs `@ExperimentalTopLevelDeclarationsGenerationApi` and possibly `hasPackage`. Own `object Key`. |
| `OpticsCopyGenerator.kt` | Generates the **top-level** `copy` extension (§9) when `@optics.copy`. | `FirDeclarationGenerationExtension`; top-level `fun S.copy(block: context(Copy<S>) S.Companion.(S) -> Unit): S`. Builds the context-parameter function type for `block`. Own `object Key`. Gated on context-parameters support. |
| `OpticsCheckers.kt` | §12 diagnostics. | `FirAdditionalCheckersExtension` providing a `FirClassChecker` (or declaration checker) that reports: ineligible-class error, missing-companion error, and the §5.2 informational notes. Uses a `KtDiagnosticFactory0`/`Factory1` set defined in `OpticsErrors.kt`. |
| `OpticsErrors.kt` | Diagnostic factory + message bundle. | `object OpticsErrors`-style factories (`NOT_DATA_VALUE_SEALED`, `MISSING_COMPANION` as errors; `NO_ABSTRACT_PROPERTIES`, `NON_DATA_SUBCLASS`, `NON_UNIFORM_PROPERTY`, `PROPERTY_NOT_CTOR_PARAM` as warnings/infos) and a `BaseDiagnosticRendererFactory` with messages. Register via the checkers extension. |

### New IR files — `src/main/kotlin/arrow/optics/plugin/ir/`

| File | Responsibility | Key declarations |
|---|---|---|
| `OpticsIrGenerationExtension.kt` | Entry point; visits generated declarations, dispatches by kind/owner. | `class … : IrGenerationExtension { override fun generate(moduleFragment, pluginContext) }`. A `IrElementVisitorVoid` that finds `IrSimpleFunction`/`IrProperty` accessors whose `origin is GeneratedByPlugin` with `pluginKey ∈ {CompanionGenerator.Key, DslGenerator.Key, CopyGenerator.Key}` and dispatches to builders. Resolves all external symbols once into an `OpticsIrSymbols` holder. |
| `OpticsIrSymbols.kt` | Resolved external IR symbols. | `referenceFunctions`/`referenceClass`/`referenceConstructors`/`referenceProperties` for everything in `OpticsNames.kt`; cached. |
| `IrBuilders.kt` | Reusable IR builder helpers. | `buildLambda(...)` (§2.1), `irFunctionType(...)`, constructor-reconstruction helper `reconstruct(classSymbol, overrides)`, `composePlus(...)`. |
| `LensIrBuilder.kt` | LENS body (data + sealed) (§2.2, §2.3). | `buildDataLens`, `buildSealedLens`. |
| `IsoIrBuilder.kt` | ISO body (§2.4). | `buildIso`. |
| `PrismIrBuilder.kt` | PRISM body (§2.5). | `buildPrism`. |
| `DslIrBuilder.kt` | DSL body (§2.6). | `buildDslComposition`. |
| `CopyIrBuilder.kt` | COPY body (§2.7). | `buildCopy`. |
| `IrFocusExtractor.kt` | IR-side re-derivation of foci from `IrClass` (mirror of FIR extractor). | `extract(irClass, pluginContext): List<OpticDecl>` + `irTypeForFocus`, sealed dispatch via `IrClass.sealedSubclasses`. |

### Modified wiring — `src/main/kotlin/arrow/optics/plugin/fir/OpticsPluginWrappers.kt`

- Register the new FIR generators and checkers, and the IR extension:

```
override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
  FirExtensionRegistrarAdapter.registerExtension(OpticsPluginRegistrar())
  IrGenerationExtension.registerExtension(OpticsIrGenerationExtension())
}

class OpticsPluginRegistrar : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +::OpticsCompanionGenerator      // now also generates base optic members
    +::OpticsDslGenerator
    +::OpticsCopyGenerator
    +::OpticsCheckers                 // FirAdditionalCheckersExtension
  }
}
```

(If COPY must be gated on `-Xcontext-parameters`, read a CLI option in `OpticsCommandLineProcessor` and only register the copy generator when enabled — or always register and let it no-op when context params are off.)

### `build.gradle.kts` additions

Add a test source set with the K2-plugin test harness deps (mirroring the KSP module, minus KSP):

```
dependencies {
  compileOnly(kotlin("compiler"))

  testImplementation(kotlin("test"))
  testImplementation(kotlin("compiler"))            // to instantiate OpticsPluginComponentRegistrar
  testImplementation(libs.kotest.assertionsCore)
  testImplementation(libs.classgraph)
  testImplementation(libs.kotlinCompileTesting) {
    exclude(group = libs.classgraph.get().module.group, module = libs.classgraph.get().module.name)
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib")
  }
  testRuntimeOnly(projects.arrowAnnotations)
  testRuntimeOnly(projects.arrowCore)
  testRuntimeOnly(projects.arrowOptics)
}
tasks.withType<Test>().configureEach { maxParallelForks = 1; useJUnitPlatform() }
```

Also pass `arrowVersion` system property to tests as the KSP module does (check that module's parent build for how `arrowVersion` is wired; replicate). No `libs.ksp`, no `kotlinCompileTestingKsp`.

### Service files — `src/main/resources/META-INF/services/`

Already present for the `CommandLineProcessor` and `CompilerPluginRegistrar`. No change unless we add a second registrar; we do not.

### Test files — `src/test/kotlin/arrow/optics/plugin/`

| File | Responsibility |
|---|---|
| `Compilation.kt` | Port from KSP module; replace `configureKsp{…}` with `compilerPluginRegistrars = listOf(OpticsPluginComponentRegistrar())`. Keep `classpathOf`, `evals`, `compilationSucceeds`, `failsWith`. |
| `Utils.kt` | Port the `package`/`imports`/`dslModel` fixtures. |
| `LensTests.kt`, `IsoTests.kt`, `PrismTests.kt`, `OptionalTests.kt`, `DSLTests.kt`, `CopyTest.kt`, `GeneratedCopyTest.kt` | Port from KSP module — the *behavioral* expectations are unchanged because the user-facing surface (`Person.age`, `Source.field()`) is identical. These become the cross-implementation conformance suite. |

---

## 5. Diagnostics (§12) in FIR

In `OpticsCheckers.kt` (a `FirAdditionalCheckersExtension`), register a class-level checker:

- **Error `NOT_DATA_VALUE_SEALED`**: on a class annotated `@optics` (predicate match) whose `classKind`/modality is not data / `@JvmInline value` / sealed → `reporter.reportOn(source, OpticsErrors.NOT_DATA_VALUE_SEALED)`.
- **Error `MISSING_COMPANION`**: annotated class with no companion. **Subtlety:** the `OpticsCompanionGenerator` *creates* a companion when missing, so by checker time a companion always exists. Two options: (a) make companion auto-generation conditional on a config flag and emit the error when the flag is off and the user omitted a companion (matches algo §2.2 "can be disabled by a configuration flag"); (b) since we auto-generate, **drop** this error by default. Recommended: keep the flag (default = auto-generate, no error), and emit `MISSING_COMPANION` only when auto-generation is disabled.
- **Informational notes (warnings)** in the §5.2 path, reported from the **shared extractor** results so FIR and the user see them: `NO_ABSTRACT_PROPERTIES`, `NON_DATA_SUBCLASS`, `NON_UNIFORM_PROPERTY` (Factory1 carrying the property name), `PROPERTY_NOT_CTOR_PARAM`. These are non-fatal; the corresponding optic is simply not generated, so use-site references fail to resolve later (matches §12).

Messages live in `OpticsErrors.kt` via a `BaseDiagnosticRendererFactory`; register the renderer so messages display. If `MutableDiagnosticReporter` is needed inside generation (it generally is not — prefer checkers), it can be obtained from the checker context.

---

## 6. Sequencing / milestones

Each milestone ends with a green test. Build the harness first so every later step is verifiable.

**M0 — Harness + wiring.**
- Add IR extension registration in `OpticsPluginWrappers.kt` (empty `OpticsIrGenerationExtension` that does nothing yet).
- `build.gradle.kts` test deps; port `Compilation.kt`/`Utils.kt` with `compilerPluginRegistrars = listOf(OpticsPluginComponentRegistrar())`.
- **Test:** an `@optics data class` with no references compiles (companion is generated; no members yet).

**M1 — LENS, monomorphic data class, end to end.**
- `OpticsNames.kt`, `model/OpticsModel.kt`, `FirFocusExtractor.kt` (data-class branch only).
- `OpticsCompanionGenerator`: announce one property `Name` per ctor param via `getCallableNamesForClass`; `generateProperties` returns `createMemberProperty(companion, Key, name, Lens<S,F> cone, isVal=true)`.
- IR: `IrBuilders.buildLambda`, `LensIrBuilder.buildDataLens` (constructor-reconstruction `set`), `OpticsIrSymbols`, dispatch in `OpticsIrGenerationExtension`.
- **Test (port `LensTests` first case):** `val i: Lens<LensData, String> = LensData.field1` evaluates / `.evals("r" to true)`. Also nullable focus → `Lens<S, String?>` (OptionalTests subset).

**M2 — LENS, generic data class (function form).**
- §3.4 function switch; `FirFocusExtractor` type-param re-declaration (§3.5, bounds preserved, variance dropped); reference cone `S<Tp…>`.
- `generateFunctions` with `createMemberFunction` + `typeParameter(...)`, `returnTypeProvider`.
- IR: `buildDataLens` handles type-parameterized owner (type args on `copy`/ctor calls).
- **Test:** `OpticsTest<A>(val field: A)` → `OpticsTest.field<String>()` typed `Lens<OpticsTest<String>, String>`.

**M3 — Sealed shared-property LENS (§5.2).**
- `FirFocusExtractor` sealed branch: inheritor scan, abstract-prop uniformity, ctor-param check; emit notes via checker.
- IR `buildSealedLens`: exhaustive `irWhen`, per-subclass reconstruction, generic unchecked cast.
- **Test (port `LensTests` sealed cases):** `LensSealed.property1` get/set across `Child1`/`Child2`; `Box<A>.tag()` generic with cast.

**M4 — ISO (value class).**
- `FirFocusExtractor` value branch; `IsoIrBuilder.buildIso`.
- **Test:** port `IsoTests` (`IsoData.field1`, generic `IsoData<T>.field1()`).

**M5 — PRISM (sealed).**
- `FirFocusExtractor` prism foci (subclass list, refined supertype, free-var union §6); `PrismIrBuilder.buildPrism` with reified `instanceOf`.
- **Test:** port `PrismTests` (mono `PrismSealed.prismSealed1`; generic `PrismSealed2<C>()` with refined source `PrismSealed<String, C>`).

**M6 — DSL (§8).**
- `OpticsDslGenerator` top-level extensions, per-kind variant matrix (§8.2); `DslIrBuilder.buildDslComposition` (`this + S.x`) with correct `plus` overload.
- **Test:** port `DSLTests` (`Employees.employees.every.company.notNull.address.street.name` chain).

**M7 — COPY (§9).**
- `OpticsCopyGenerator` top-level `copy` with context-parameter `block` type; `CopyIrBuilder.buildCopy`.
- Gate on `-Xcontext-parameters` (test passes `contextParameters = true`).
- **Test:** port `CopyTest`/`GeneratedCopyTest` (`me.copy { age transform { it+1 }; address.city.name set "…" }`).

**M8 — Diagnostics + conformance sweep.**
- `OpticsCheckers.kt`, `OpticsErrors.kt`; port `failsWith`/`compilationFails` tests (ineligible class, etc.).
- Run the full ported KSP test suite as conformance.

---

## 7. Risks / open questions

1. **Companion members carrying type parameters.** A member *property* cannot declare type params, but the generic case already uses a **member function**, which can — so generic base optics as companion members are sound. Confirm `createMemberFunction` with `typeParameter{}` on a companion-object owner works (it should; member functions on objects routinely have type params).
2. **Visibility of generated members to same-module user code at IR/resolution.** FIR-generated members must be visible to the FIR resolution of user call sites (`Person.age`). This requires the generator to **announce names** (`getCallableNamesForClass`) reliably for the *user-declared* companion, not just the plugin-generated one. The existing generator only handles the case where it *created* the companion. **Open:** verify name announcement works when the user wrote `companion object` themselves — match on the source class via the companion's containing class and the predicate. This is the highest-risk correctness item; test in M1 with both an explicit and an absent companion.
3. **`Prism.instanceOf` reified inlining in IR.** A plugin-emitted `irCall` to an inline-reified function with concrete type arguments must survive the inliner. Generally fine, but if problematic, fall back to the `instanceOf(klass: KClass<B>)` overload with an `IrClassReference` (§2.5). Decide in M5.
4. **Data-class `set` without source-level named/defaulted `copy`.** Use **constructor reconstruction** to avoid `copy$default` bitmask handling (§2.2). Confirm value classes and sealed subclasses always expose a primary constructor (they do for data/value classes).
5. **Backticking / keyword names.** Not a concern at the symbol level — `Name.identifier("in")` is valid and the back-end emits correct bytecode. No special handling, unlike KSP's unconditional backticks.
6. **Context parameters for COPY (§9).** Requires `-Xcontext-parameters`; building a `context(Copy<S>) S.Companion.(S) -> Unit` *type* in FIR (cone with context-receiver) and invoking it in IR are both experimental-API surfaces. Treat COPY as the last, most fragile milestone; gate behind the flag and possibly behind a CLI option.
7. **DSL `plus` overload selection.** Each optic kind's `plus` has variance-projected parameters; selecting the right `FirNamedFunctionSymbol`/`IrSimpleFunctionSymbol` per outer-optic kind needs care (filter `referenceFunctions` by dispatch-receiver class). Validate types compile in M6.
8. **"Extension on Companion" reinterpretation.** Re-read §3.1/§3.4 of the algo as "companion **member**" for base optics throughout this plan; only DSL (§8) and COPY (§9) remain genuine extensions (top-level). The algo's import/aliasing (§3.8), backticking (§3.2), and file-grouping (§3.10) sections are **not implemented** — they are artifacts of text generation and have no analogue in FIR/IR.
9. **`getSealedClassInheritors` API stability** across the K2 version pinned by `kotlin("compiler")`. Verify the exact signature (`session` parameter) against the compiler version resolved by the version catalog before M3/M5.
