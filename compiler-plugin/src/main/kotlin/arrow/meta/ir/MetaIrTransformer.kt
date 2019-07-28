package arrow.meta.ir

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

fun MetaComponentRegistrar.functionAccess(f: IrUtils.(IrFunctionAccessExpression) -> IrElement?): ExtensionPhase.IRGeneration =
  IrGeneration { compilerContext, file, backendContext, bindingContext ->
    file.transformChildren(object : IrElementTransformer<Unit> {
      override fun visitFunctionAccess(expression: IrFunctionAccessExpression, data: Unit): IrElement =
        f(IrUtils(backendContext, compilerContext), expression) ?: super.visitFunctionAccess(expression, data)
    }, Unit)
  }