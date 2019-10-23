package arrow.meta.plugins.dummy

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.resolveClassByFqName
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.descriptors.IrSimpleBuiltinOperatorDescriptorImpl
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.replace
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

const val EQEQ = "EQEQ"

val Meta.eq: Plugin
  get() =
    "Eq" {
      meta(
        /**
         * Need to create a new kotlintype that is of Eq with type arguments A for whatever A is in your comparison with  ==
         *
         * If the member descriptor fqNameSafe matches EQEQ, then:
         * 1. Find the type of the descriptor's arguments to use in new IrSimpleType
         * 2. Create IrSimpleType for Arrow.Eq
         * 3. Get the EQ descriptor from the external symbol table. Create a new descriptor with new IrType "Eq<A>"
         *    ^^ Wait, why is the EQ descriptor needed
         * 4. Replace descriptor of the current IrCall with new descriptor
         */
        irFunctionAccess {
          if (it.descriptor.name.asString().contains(EQEQ)) {
            it.descriptor.safeAs<IrSimpleBuiltinOperatorDescriptorImpl>()?.let { operator ->
              val first = operator.valueParameters[0]
              val eqTypeArg = first.type
              val module = it.descriptor.module
              val eqClassDescriptor = module.resolveClassByFqName(FqName("arrow.typeclasses.Eq"), NoLookupLocation.FROM_BACKEND)
              val extensionType = eqClassDescriptor?.defaultType?.replace(newArguments = listOf(eqTypeArg.asTypeProjection()))
              val typeClassFactory = extensionType?.let { type ->
                findExtension(type)
              }
              val call = when (val extension = typeClassFactory) {
                is FunctionDescriptor -> extension.irCall()
                is ClassDescriptor -> extension.irConstructorCall()
                is PropertyDescriptor -> extension.irGetterCall()
                else -> null
              }
              // eqInt . eqv(a, b)
              // eqInt.run { a.eqv(b) }
              println("call: $call")
            }
            println("Found EQEQ: ${it.descriptor.fqNameSafe}")
          }
          it
        }
      )
    }

fun IrUtils.irCall(oldEqDescriptor: FunctionDescriptor, newEqDescriptor: FunctionDescriptor): IrCall {
  val irFunctionSymbol: IrFunctionSymbol = backendContext.ir.symbols.externalSymbolTable.referenceFunction(oldEqDescriptor)
  return IrCallImpl(
    startOffset = UNDEFINED_OFFSET,
    endOffset = UNDEFINED_OFFSET,
    type = irFunctionSymbol.owner.returnType,
    symbol = irFunctionSymbol,
    descriptor = newEqDescriptor,
    typeArgumentsCount = irFunctionSymbol.owner.descriptor.typeParameters.size,
    valueArgumentsCount = irFunctionSymbol.owner.descriptor.valueParameters.size
  )
}


