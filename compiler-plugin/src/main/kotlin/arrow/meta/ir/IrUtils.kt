package arrow.meta.ir

import arrow.meta.extensions.CompilerContext
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtParameter

class IrUtils(
  val backendContext: BackendContext,
  val compilerContext: CompilerContext
) {

  fun IrFunctionAccessExpression.defaultValues(): List<String> =
    symbol.descriptor.valueParameters
      .mapNotNull { it.findPsi() as? KtParameter }
      .mapNotNull { it.defaultValue?.text }

  fun FunctionDescriptor.irCall(): IrCall {
    val irFunctionSymbol = backendContext.ir.symbols.externalSymbolTable.referenceFunction(this)
    return IrCallImpl(
      startOffset = UNDEFINED_OFFSET,
      endOffset = UNDEFINED_OFFSET,
      type = irFunctionSymbol.owner.returnType,
      symbol = irFunctionSymbol,
      descriptor = irFunctionSymbol.owner.descriptor,
      typeArgumentsCount = irFunctionSymbol.owner.descriptor.typeParameters.size,
      valueArgumentsCount = irFunctionSymbol.owner.descriptor.valueParameters.size
    )
  }

  fun PropertyDescriptor.irGetterCall(): IrCall? {
    val irField = backendContext.ir.symbols.externalSymbolTable.referenceField(this)
    return irField.owner.correspondingPropertySymbol?.owner?.getter?.symbol?.let { irSimpleFunctionSymbol ->
      IrCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = irSimpleFunctionSymbol.owner.returnType,
        symbol = irSimpleFunctionSymbol,
        descriptor = irSimpleFunctionSymbol.owner.descriptor,
        typeArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.typeParameters.size,
        valueArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.valueParameters.size
      )
    }
  }

  fun ClassDescriptor.irConstructorCall(): IrConstructorCall? {
    val irClass = backendContext.ir.symbols.externalSymbolTable.referenceClass(this)
    return irClass.constructors.firstOrNull()?.let { irConstructorSymbol ->
      IrConstructorCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = irConstructorSymbol.owner.returnType,
        symbol = irConstructorSymbol,
        descriptor = irConstructorSymbol.owner.descriptor,
        typeArgumentsCount = irConstructorSymbol.owner.descriptor.typeParameters.size,
        valueArgumentsCount = irConstructorSymbol.owner.descriptor.valueParameters.size,
        constructorTypeArgumentsCount = declaredTypeParameters.size
      )
    }
  }

}