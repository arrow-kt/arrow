package arrow.optics.plugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId

actual fun IrPluginContext.findFunctions(callableId: CallableId): Collection<IrSimpleFunctionSymbol> =
  finderForBuiltins().findFunctions(callableId)

actual fun IrPluginContext.findClass(classId: ClassId): IrClassSymbol? =
  finderForBuiltins().findClass(classId)
