package arrow.optics.plugin.ir

import arrow.optics.plugin.OpticsNames
import arrow.optics.plugin.fir.OpticsCompanionGenerator
import arrow.optics.plugin.fir.OpticsDslGenerator
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.GeneratedDeclarationKey
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
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.companionObject
import org.jetbrains.kotlin.ir.util.defaultType
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

/** Resolved references to the `arrow.optics` API used inside generated bodies. */
class OpticsIrSymbols(ctx: IrPluginContext) {
  val lensInvoke: IrSimpleFunctionSymbol =
    ctx.referenceFunctions(OpticsNames.LENS_INVOKE).first { it.owner.parameters.count { p -> p.kind == IrParameterKind.Regular } == 2 }
  val isoInvoke: IrSimpleFunctionSymbol =
    ctx.referenceFunctions(OpticsNames.ISO_INVOKE).first { it.owner.parameters.count { p -> p.kind == IrParameterKind.Regular } == 2 }
  val prismInstanceOf: IrSimpleFunctionSymbol =
    ctx.referenceFunctions(OpticsNames.PRISM_INSTANCE_OF).first { it.owner.parameters.none { p -> p.kind == IrParameterKind.Regular } }
  val plens: IrClassSymbol = ctx.referenceClass(OpticsNames.PLENS)!!
  val piso: IrClassSymbol = ctx.referenceClass(OpticsNames.PISO)!!
  val pprism: IrClassSymbol = ctx.referenceClass(OpticsNames.PPRISM)!!
  val plensCompanion: IrClassSymbol = ctx.referenceClass(OpticsNames.PLENS_COMPANION)!!
  val pisoCompanion: IrClassSymbol = ctx.referenceClass(OpticsNames.PISO_COMPANION)!!
  val pprismCompanion: IrClassSymbol = ctx.referenceClass(OpticsNames.PPRISM_COMPANION)!!

  /** For each optic poly-interface, its `plus` composition operator (keyed by the receiver class). */
  val polyPlus: Map<IrClassSymbol, IrSimpleFunctionSymbol> =
    arrow.optics.plugin.DslKind.entries.associate { kind ->
      val cls = ctx.referenceClass(OpticsNames.polyClassFor(kind))!!
      val plus = ctx.referenceFunctions(OpticsNames.plusFor(kind))
        .first { it.owner.parameters.count { p -> p.kind == IrParameterKind.Regular } == 1 }
      cls to plus
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
    if (keyOf(declaration.origin) == OpticsCompanionGenerator.Key &&
      declaration.correspondingPropertySymbol == null && declaration.body == null
    ) {
      buildOpticBody(declaration, declaration.name)
    }
    super.visitSimpleFunction(declaration)
  }

  private fun keyOf(origin: IrDeclarationOrigin): GeneratedDeclarationKey? =
    (origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey

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
    val ctorTypeArgs = opticFn.typeParameters.map { it.coneType() }

    opticFn.body = DeclarationIrBuilder(ctx, opticFn.symbol).irBlockBody {
      val expr = when (kind) {
        IrOpticKind.LENS -> buildLens(opticFn, source, sourceType, focusType, opticName, ctorTypeArgs)
        IrOpticKind.ISO -> buildIso(opticFn, source, sourceType, focusType, opticName, ctorTypeArgs)
        IrOpticKind.PRISM -> buildPrism(sourceType, focusType, opticFn.returnType)
      }
      +irReturn(expr)
    }
  }

  /** `get() = this + Source.focus` for a generated DSL composition extension. */
  private fun buildDslBody(getter: IrSimpleFunction, focusName: Name) {
    val receiver = getter.parameters.first { it.kind == IrParameterKind.ExtensionReceiver }
    val receiverType = receiver.type as IrSimpleType
    val outerClass = receiverType.classOrNull ?: return
    val plus = symbols.polyPlus[outerClass] ?: return
    val sourceType = receiverType.arguments[2].typeOrNull ?: return
    val source = sourceType.classOrNull?.owner ?: return
    val focusType = (getter.returnType as IrSimpleType).arguments[2].typeOrNull ?: return
    val companion = source.companionObject() ?: return
    val baseProp = companion.properties.firstOrNull { it.name == focusName } ?: return
    val baseGetter = baseProp.getter ?: return

    getter.body = DeclarationIrBuilder(ctx, getter.symbol).irBlockBody {
      val base = irCall(baseGetter.symbol, baseGetter.returnType)
      base.setDispatch(irGetObjectValue(companion.defaultType, companion.symbol))
      val composed = irCall(plus, getter.returnType, listOf(focusType, focusType))
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
        reconstruct(source, ctorTypeArgs, irGet(s), fieldName, irGet(v))
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
      val cast = irImplicitCast(irGet(instance), subType)
      irBranch(
        irIs(irGet(instance), subType),
        reconstruct(sub, emptyList(), cast, fieldName, irGet(value)),
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
      +irReturn(reconstruct(source, ctorTypeArgs, irGet(v), fieldName, irGet(v)))
    }
    val call = irCall(symbols.isoInvoke, opticFn.returnType, listOf(sourceType, sourceType, focusType, focusType))
    call.setDispatch(irGetObjectValue(symbols.pisoCompanion.owner.defaultType, symbols.pisoCompanion))
    call.setRegular(0, getLambda)
    call.setRegular(1, reverseGetLambda)
    return call
  }

  /** `instance.field` via the property getter. */
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

  /** Reconstruct [source] via its primary constructor, replacing [overrideName] with [overrideValue]. */
  private fun IrBuilderWithScope.reconstruct(
    source: IrClass,
    ctorTypeArgs: List<IrType>,
    instance: IrExpression,
    overrideName: Name,
    overrideValue: IrExpression,
  ): IrExpression {
    val ctor = source.primaryConstructor!!
    val call = irCallConstructor(ctor.symbol, ctorTypeArgs)
    ctor.parameters.filter { it.kind == IrParameterKind.Regular }.forEach { param ->
      val arg = if (param.name == overrideName) {
        overrideValue
      } else {
        readComponent(source, param.name, param.type, instance)
      }
      call.arguments[param] = arg
    }
    return call
  }
}
