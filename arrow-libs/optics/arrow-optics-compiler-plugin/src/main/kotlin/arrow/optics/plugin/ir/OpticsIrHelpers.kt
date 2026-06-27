@file:OptIn(UnsafeDuringIrConstructionAPI::class)

package arrow.optics.plugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.name.SpecialNames

/**
 * Build an anonymous lambda `{ p0, p1, ... -> body }` as an [IrFunctionExpression], for use as a
 * `get`/`set`/`reverseGet` argument to an optic factory.
 */
fun IrPluginContext.buildLambda(
  parent: IrDeclarationParent,
  parameterTypes: List<IrType>,
  returnType: IrType,
  body: IrBlockBodyBuilder.(params: List<IrValueParameter>) -> Unit,
): IrFunctionExpression {
  val lambda = irFactory.buildFun {
    name = SpecialNames.NO_NAME_PROVIDED
    origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
    visibility = DescriptorVisibilities.LOCAL
    modality = Modality.FINAL
    this.returnType = returnType
  }
  lambda.parent = parent
  parameterTypes.forEachIndexed { i, t -> lambda.addValueParameter("p$i", t) }
  lambda.body = DeclarationIrBuilder(this, lambda.symbol).irBlockBody {
    body(lambda.parameters)
  }
  val functionType = irBuiltIns.functionN(parameterTypes.size).typeWith(parameterTypes + returnType)
  return IrFunctionExpressionImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, functionType, lambda, IrStatementOrigin.LAMBDA)
}

/** Set the dispatch-receiver argument of [this] call, addressing it by parameter kind. */
fun IrMemberAccessExpression<*>.setDispatch(receiver: IrExpression) {
  val owner = (symbol.owner as? IrFunction) ?: return
  val dispatch = owner.parameters.firstOrNull { it.kind == IrParameterKind.DispatchReceiver } ?: return
  arguments[dispatch] = receiver
}

/** Set the [n]-th regular argument of [this] call. */
fun IrMemberAccessExpression<*>.setRegular(n: Int, value: IrExpression) {
  val owner = (symbol.owner as? IrFunction) ?: return
  val regulars = owner.parameters.filter { it.kind == IrParameterKind.Regular }
  arguments[regulars[n]] = value
}

/** Set the extension-receiver argument of [this] call. */
fun IrMemberAccessExpression<*>.setExtension(receiver: IrExpression) {
  val owner = (symbol.owner as? IrFunction) ?: return
  val ext = owner.parameters.firstOrNull { it.kind == IrParameterKind.ExtensionReceiver } ?: return
  arguments[ext] = receiver
}
