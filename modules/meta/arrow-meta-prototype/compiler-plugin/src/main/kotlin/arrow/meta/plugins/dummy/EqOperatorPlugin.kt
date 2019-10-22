package arrow.meta.plugins.dummy

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType

const val EQEQ = "EQEQ"

val Meta.eq: Plugin
  get() =
    "Eq" {
      meta(
        irFunctionAccess {
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
          if (it.descriptor.fqNameSafe == FqName("kotlin.ir.expressions.IrStatementOrigin.$EQEQ")) {
            val kotlinType: KotlinType = it.descriptor.typeParameters.first().defaultType // making a terrible assumption the types match
            // ^ can be used for the meta dsl
            val irType: IrType? = it.getValueArgument(0)?.type
            val eqDescriptor : FunctionDescriptor = it.descriptor


            val newEqDescriptor: FunctionDescriptor = it.descriptor.newCopyBuilder()
              .setDescriptor(       // create IrSimpleType for Arrow.Eq - Eq<A>
              IrSimpleTypeImpl(
                classifier = irType?.classifier.symbol, // can you downcast irType to irSimpleType?
                hasQuestionMark = false,
                arguments = arguments.map {
                  // need more serious consideration for what the arguments are for here
                },
                annotations = emptyList()
              ).descriptor
            ).build()

            val result = compilerContext.findExtension(kotlinType)

            // change IrCall
            return irCall( it.descriptor, newEqDescriptor)
          }
          else it
        }
      )
    }
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


