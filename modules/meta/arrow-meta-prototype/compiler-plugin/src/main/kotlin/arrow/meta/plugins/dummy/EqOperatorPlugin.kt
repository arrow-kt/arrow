package arrow.meta.plugins.dummy

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.plugins.typeclasses.findExtension
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
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
           * 4. Replace descriptor of the current IrCall with new descriptor
           */
          if (it.descriptor.fqNameSafe == FqName("kotlin.ir.expressions.IrStatementOrigin.$EQEQ")) {
            val type : IrType = it.descriptor.
            val typee: KotlinType = it.descriptor.typeParameters.first().defaultType // making a terrible assumption the types match
            val irType = it.getValueArgument(0)?.type

            // create IrSimpleType for Arrow.Eq
            IrSimpleTypeImpl(
              classifier = irType.symbol,
              hasQuestionMark = false,
              arguments = arguments.map {
                when (it) {
                  is IrTypeProjection -> makeTypeProjection(
                    it.type.remapTypeParameters(source, target, shift),
                    it.variance
                  )
                  else -> it
                }
              },
              annotations = emptyList()
            )

            val result = compilerContext.findExtension(it.getValueArgument(0).type)

            // change IrCall
            return IrCallImpl(
              startOffset = UNDEFINED_OFFSET,
              endOffset = UNDEFINED_OFFSET,
              type = irMemberAccessExpression.descriptor.returnType,
              symbol = irMemberAccessExpression.descriptor.symbole,
              descriptor = irFunctionSymbol.owner.descriptor, // resulting descriptor
              typeArgumentsCount = irFunctionSymbol.owner.descriptor.typeParameters.size,
              valueArgumentsCount = irFunctionSymbol.owner.descriptor.valueParameters.size
            )
          }
          else irMemberAccessExpression

          if (1 == 2) {
            // fun EQEQ value parameter 1 value parameter 2
          }
        } // alter resolution at the bytecode level via IR
      )
    }
}

fun FunctionDescriptor.irCall(): IrCall {
  val irFunctionSymbol: IrFunctionSymbol = backendContext.ir.symbols.externalSymbolTable.referenceFunction(this)
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


