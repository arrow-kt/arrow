@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package arrow.optics.plugin.ir

import arrow.optics.plugin.DslKind
import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.fir.OpticsCompanionGenerator
import arrow.optics.plugin.fir.OpticsCopyGenerator
import arrow.optics.plugin.fir.OpticsDslGenerator
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBranch
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irElseBranch
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObjectValue
import org.jetbrains.kotlin.ir.builders.irImplicitCast
import org.jetbrains.kotlin.ir.builders.irIs
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irWhen
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.Name

class OpticsIrGenerationExtension : IrGenerationExtension {
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    val symbols = OpticsIrSymbols(pluginContext)
    moduleFragment.acceptChildrenVoid(OpticsBodyGenerator(pluginContext, symbols))
  }
}

/**
 * Resolved references to the `arrow.optics` API used inside generated bodies.
 *
 * Everything is resolved lazily: the symbols are only looked up the first time a generated optic
 * body is actually built, so applying the plugin to a module that does not depend on `arrow-optics`
 * (and therefore generates nothing) never forces resolution and never crashes (review §2.5).
 */
class OpticsIrSymbols(private val ctx: IrPluginContext) {
  val finder get() = ctx.finderForBuiltins()

  val lensInvoke: IrSimpleFunctionSymbol by lazy {
    finder.findFunctions(OpticsNames.LENS_INVOKE).first { it.owner.parameters.count { p -> p.kind == IrParameterKind.Regular } == 2 }
  }
  val isoInvoke: IrSimpleFunctionSymbol by lazy {
    finder.findFunctions(OpticsNames.ISO_INVOKE).first { it.owner.parameters.count { p -> p.kind == IrParameterKind.Regular } == 2 }
  }
  val prismInstanceOf: IrSimpleFunctionSymbol by lazy {
    finder.findFunctions(OpticsNames.PRISM_INSTANCE_OF).first { it.owner.parameters.none { p -> p.kind == IrParameterKind.Regular } }
  }
  val plens: IrClassSymbol by lazy { finder.findClass(OpticsNames.PLENS)!! }
  val piso: IrClassSymbol by lazy { finder.findClass(OpticsNames.PISO)!! }
  val pprism: IrClassSymbol by lazy { finder.findClass(OpticsNames.PPRISM)!! }
  val plensCompanion: IrClassSymbol by lazy { finder.findClass(OpticsNames.PLENS_COMPANION)!! }
  val pisoCompanion: IrClassSymbol by lazy { finder.findClass(OpticsNames.PISO_COMPANION)!! }
  val pprismCompanion: IrClassSymbol by lazy { finder.findClass(OpticsNames.PPRISM_COMPANION)!! }

  /** For each optic poly-interface, its `plus` composition operator (keyed by the receiver class). */
  val polyPlus: Map<IrClassSymbol, IrSimpleFunctionSymbol> by lazy {
    DslKind.entries.associate { kind ->
      val cls = finder.findClass(OpticsNames.polyClassFor(kind))!!
      val plus = finder.findFunctions(OpticsNames.plusFor(kind))
        .first { it.owner.parameters.count { p -> p.kind == IrParameterKind.Regular } == 1 }
      cls to plus
    }
  }

  // COPY builder support.
  val copyClass: IrClassSymbol by lazy { finder.findClass(OpticsNames.COPY)!! }
  val arrowOpticsCopy: IrSimpleFunctionSymbol by lazy {
    finder.findFunctions(OpticsNames.ARROW_OPTICS_COPY).first { fn ->
      fn.owner.parameters.any { it.kind == IrParameterKind.ExtensionReceiver } &&
        fn.owner.parameters.count { it.kind == IrParameterKind.Regular } == 1
    }
  }
}

private enum class IrOpticKind { LENS, ISO, PRISM }

private class OpticsBodyGenerator(
  private val ctx: IrPluginContext,
  private val symbols: OpticsIrSymbols,
) : IrVisitorVoid() {

  override fun visitElement(element: IrElement) {
    element.acceptChildrenVoid(this)
  }

  override fun visitProperty(declaration: IrProperty) {
    val getter = declaration.getter
    if (getter != null) {
      when (keyOf(declaration.origin)) {
        OpticsCompanionGenerator.Key -> {
          declaration.backingField = null
          buildOpticBody(getter, declaration.name)
        }

        OpticsDslGenerator.Key -> {
          declaration.backingField = null
          buildDslBody(getter, declaration.name)
        }
      }
    }
    super.visitProperty(declaration)
  }

  override fun visitSimpleFunction(declaration: IrSimpleFunction) {
    if (declaration.correspondingPropertySymbol == null) {
      when (keyOf(declaration.origin)) {
        OpticsCompanionGenerator.Key -> if (declaration.body == null) buildOpticBody(declaration, declaration.name)

        // Generic sources get their DSL composition helpers as functions (see OpticsDslGenerator).
        OpticsDslGenerator.Key -> if (declaration.body == null) buildDslBody(declaration, declaration.name)

        // The copy member is created with a placeholder body (see OpticsCopyGenerator), so overwrite it.
        OpticsCopyGenerator.Key -> buildCopyBody(declaration)
      }
    }
    super.visitSimpleFunction(declaration)
  }

  /** `{ this.copy { block(this, Source.Companion, this@copy) } }` for a generated `@optics.copy` member. */
  private fun buildCopyBody(copyFn: IrSimpleFunction) {
    val receiver = copyFn.parameters.first { it.kind == IrParameterKind.DispatchReceiver }
    val blockParam = copyFn.parameters.first { it.kind == IrParameterKind.Regular }
    val sourceType = receiver.type
    val source = sourceType.classOrNull?.owner ?: return
    val companion = source.companionObject() ?: return
    val copyType = symbols.copyClass.typeWith(sourceType)
    val unit = ctx.irBuiltIns.unitType
    val function3Invoke = ctx.irBuiltIns.functionN(3).functions.first { it.name.asString() == "invoke" }.symbol

    copyFn.body = DeclarationIrBuilder(ctx, copyFn.symbol).irBlockBody {
      val lambda = ctx.buildLambda(copyFn, listOf(copyType), unit) { (copyReceiver) ->
        val invoke = irCall(function3Invoke, unit)
        invoke.setDispatch(irGet(blockParam))
        invoke.setRegular(0, irGet(copyReceiver))
        invoke.setRegular(1, irGetObjectValue(companion.defaultType, companion.symbol))
        invoke.setRegular(2, irGet(receiver))
        +invoke
      }
      val call = irCall(symbols.arrowOpticsCopy, sourceType, listOf(sourceType))
      call.setExtension(irGet(receiver))
      call.setRegular(0, lambda)
      +irReturn(call)
    }
  }

  private fun keyOf(origin: IrDeclarationOrigin): GeneratedDeclarationKey? = (origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey

  /** Fill in the body of a generated companion optic ([opticFn] is the property getter or the standalone function). */
  private fun buildOpticBody(opticFn: IrSimpleFunction, opticName: Name) {
    val kind = when (opticFn.returnType.classOrNull) {
      symbols.plens -> IrOpticKind.LENS
      symbols.piso -> IrOpticKind.ISO
      symbols.pprism -> IrOpticKind.PRISM
      else -> return
    }
    val source = opticFn.parentAsClass.parentAsClass
    val rt = opticFn.returnType as IrSimpleType
    val sourceType = rt.arguments[0].typeOrNull!!
    val focusType = rt.arguments[2].typeOrNull!!
    val ctorTypeArgs = opticFn.typeParameters.map { it.defaultType }

    opticFn.body = DeclarationIrBuilder(ctx, opticFn.symbol).irBlockBody {
      val expr = when (kind) {
        IrOpticKind.LENS -> buildLens(opticFn, source, sourceType, focusType, opticName, ctorTypeArgs)
        IrOpticKind.ISO -> buildIso(opticFn, source, sourceType, focusType, opticName, ctorTypeArgs)
        IrOpticKind.PRISM -> buildPrism(sourceType, focusType, opticFn.returnType)
      }
      +irReturn(expr)
    }
  }

  /**
   * `this + Source.focus` for a generated DSL composition extension. [opticFn] is the property getter
   * (monomorphic source, whose base optic is a companion *property*) or the standalone function
   * (generic source, whose base optic is a companion *function* taking the source's type arguments).
   */
  private fun buildDslBody(opticFn: IrSimpleFunction, focusName: Name) {
    val receiver = opticFn.parameters.first { it.kind == IrParameterKind.ExtensionReceiver }
    val receiverType = receiver.type as IrSimpleType
    val outerClass = receiverType.classOrNull ?: return
    val plus = symbols.polyPlus[outerClass] ?: return
    val sourceType = receiverType.arguments[2].typeOrNull ?: return
    val source = sourceType.classOrNull?.owner ?: return
    val focusType = (opticFn.returnType as IrSimpleType).arguments[2].typeOrNull ?: return
    val companion = source.companionObject() ?: return

    // The base optic is a companion *property* for a monomorphic source and a companion *function*
    // (taking the source's type arguments) for a generic one. Detect which by looking it up, rather
    // than by the DSL helper's own type-parameter count: a prism helper for a non-generic subclass of
    // a *generic* sealed parent has only `__S` yet its base optic is still a function.
    val baseSymbol: IrSimpleFunctionSymbol
    val baseReturn: IrType
    val baseTypeArgs: List<IrType>
    val baseGetter = companion.properties.firstOrNull { it.name == focusName }?.getter
    if (baseGetter != null) {
      baseSymbol = baseGetter.symbol
      baseReturn = baseGetter.returnType
      baseTypeArgs = emptyList()
    } else {
      val baseFn = companion.functions.firstOrNull { it.name == focusName } ?: return
      baseSymbol = baseFn.symbol
      baseReturn = (baseFn.returnType.classOrNull ?: return).typeWith(sourceType, sourceType, focusType, focusType)
      baseTypeArgs = opticFn.typeParameters.dropLast(1).map { it.defaultType }
    }

    opticFn.body = DeclarationIrBuilder(ctx, opticFn.symbol).irBlockBody {
      val base = irCall(baseSymbol, baseReturn, baseTypeArgs)
      base.setDispatch(irGetObjectValue(companion.defaultType, companion.symbol))
      val composed = irCall(plus, opticFn.returnType, listOf(focusType, focusType))
      composed.setDispatch(irGet(receiver))
      composed.setRegular(0, base)
      +irReturn(composed)
    }
  }

  private fun IrBuilderWithScope.buildPrism(
    sourceType: IrType,
    focusType: IrType,
    returnType: IrType,
  ): IrExpression {
    val call = irCall(symbols.prismInstanceOf, returnType, listOf(sourceType, focusType))
    call.setDispatch(irGetObjectValue(symbols.pprismCompanion.owner.defaultType, symbols.pprismCompanion))
    return call
  }

  private fun IrBuilderWithScope.buildLens(
    opticFn: IrSimpleFunction,
    source: IrClass,
    sourceType: IrType,
    focusType: IrType,
    fieldName: Name,
    ctorTypeArgs: List<IrType>,
  ): IrExpression {
    val getLambda = ctx.buildLambda(opticFn, listOf(sourceType), focusType) { (s) ->
      +irReturn(readComponent(source, fieldName, focusType, irGet(s)))
    }
    val setLambda = ctx.buildLambda(opticFn, listOf(sourceType, focusType), sourceType) { (s, v) ->
      val body = if (source.modality == Modality.SEALED) {
        sealedSet(source, sourceType, fieldName, s, v)
      } else {
        reconstruct(source, ctorTypeArgs, { irGet(s) }, fieldName) { irGet(v) }
      }
      +irReturn(body)
    }
    val call = irCall(symbols.lensInvoke, opticFn.returnType, listOf(sourceType, sourceType, focusType, focusType))
    call.setDispatch(irGetObjectValue(symbols.plensCompanion.owner.defaultType, symbols.plensCompanion))
    call.setRegular(0, getLambda)
    call.setRegular(1, setLambda)
    return call
  }

  /** `when (s) { is Sub1 -> Sub1(prop = v, ...); ... }` over a sealed hierarchy. */
  private fun IrBuilderWithScope.sealedSet(
    source: IrClass,
    sourceType: IrType,
    fieldName: Name,
    instance: IrValueParameter,
    value: IrValueParameter,
  ): IrExpression {
    val branches = source.sealedSubclasses.map { subSymbol ->
      val sub = subSymbol.owner
      val subType = sub.defaultType
      irBranch(
        irIs(irGet(instance), subType),
        // A fresh `instance as Sub` is built for every field read, so no IR node is shared.
        reconstruct(sub, emptyList(), { irImplicitCast(irGet(instance), subType) }, fieldName) { irGet(value) },
      )
    } + irElseBranch(irCall(ctx.irBuiltIns.noWhenBranchMatchedExceptionSymbol))
    return irWhen(sourceType, branches)
  }

  private fun IrBuilderWithScope.buildIso(
    opticFn: IrSimpleFunction,
    source: IrClass,
    sourceType: IrType,
    focusType: IrType,
    fieldName: Name,
    ctorTypeArgs: List<IrType>,
  ): IrExpression {
    val getLambda = ctx.buildLambda(opticFn, listOf(sourceType), focusType) { (s) ->
      +irReturn(readComponent(source, fieldName, focusType, irGet(s)))
    }
    val reverseGetLambda = ctx.buildLambda(opticFn, listOf(focusType), sourceType) { (v) ->
      +irReturn(reconstruct(source, ctorTypeArgs, { irGet(v) }, fieldName) { irGet(v) })
    }
    val call = irCall(symbols.isoInvoke, opticFn.returnType, listOf(sourceType, sourceType, focusType, focusType))
    call.setDispatch(irGetObjectValue(symbols.pisoCompanion.owner.defaultType, symbols.pisoCompanion))
    call.setRegular(0, getLambda)
    call.setRegular(1, reverseGetLambda)
    return call
  }

  /** `instance.field` via the property getter; [instance] must produce a fresh expression. */
  private fun IrBuilderWithScope.readComponent(
    source: IrClass,
    fieldName: Name,
    focusType: IrType,
    instance: IrExpression,
  ): IrExpression {
    val prop = source.properties.first { it.name == fieldName }
    val call = irCall(prop.getter!!.symbol, focusType)
    call.setDispatch(instance)
    return call
  }

  /**
   * Reconstruct [source] via its primary constructor, replacing [overrideName] with [overrideValue].
   * [instance] and [overrideValue] are *factories* that must produce a fresh IR node on every call, so
   * that reading several sibling components never shares the same IR node (which would break IR
   * invariants — see review §2.1).
   */
  private fun IrBuilderWithScope.reconstruct(
    source: IrClass,
    ctorTypeArgs: List<IrType>,
    instance: () -> IrExpression,
    overrideName: Name,
    overrideValue: () -> IrExpression,
  ): IrExpression {
    val ctor = source.primaryConstructor!!
    val call = irCallConstructor(ctor.symbol, ctorTypeArgs)
    ctor.parameters.filter { it.kind == IrParameterKind.Regular }.forEach { param ->
      val arg = if (param.name == overrideName) {
        overrideValue()
      } else {
        readComponent(source, param.name, param.type, instance())
      }
      call.arguments[param] = arg
    }
    return call
  }
}
