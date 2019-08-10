package arrow.meta.comprehensions

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.ir.IrUtils
import arrow.meta.ir.irBody
import arrow.meta.qq.MetaTreeVisitor
import org.jetbrains.kotlin.backend.common.descriptors.isFunctionOrKFunctionType
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrStatementContainer
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.toKotlinType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.replace
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.types.typeUtil.substitute
import org.jetbrains.kotlin.utils.addToStdlib.cast
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 1. replace all instances of bind application with flatMap:
 *  - Variable assignments using the variable name as the flatMap lambda variable
 *  - Anonymous application using a generated bind0, bind1 etc name for each anonymous binding found
 * 2. flatMap replacements takes care scoping all bodies with as many nested flatMap as bindings are found within a body
 */
@ExperimentalContracts
val MetaComponentRegistrar.comprehensions: List<ExtensionPhase>
  get() =
    meta(
      irBody { body ->
        val bindings = body.bindings()
        val flatMapCalls = flatMapCalls(bindings)
        println("flatMapCalls: ${flatMapCalls.size}")
        println(body.dump())
        body
      }
    )

private fun IrUtils.flatMapCalls(list: List<IrElement>): List<IrCall> =
  list.mapNotNull { irElement ->
    when (irElement) {
      is IrVariable -> {
        flatMapCall(irElement.initializer.cast())
      }
      is IrCall -> {
        flatMapCall(irElement)
      }
      else -> TODO("Unsupported ir node for bind: $irElement")
    }
  }

private fun IrUtils.flatMapCall(irElement: IrCall): IrCall? =
  irElement.extensionReceiver?.type?.let { type ->
    val dataType = type.toKotlinType()
    val dataTypeScope = irElement.extensionReceiver?.type?.classOrNull?.descriptor?.unsubstitutedMemberScope
    val flatMap = dataTypeScope?.getContributedFunctions(Name.identifier("flatMap"), NoLookupLocation.FROM_BACKEND)?.find { fn ->
      val substitutedReturnType = fn.returnType?.asTypeProjection()?.substitute { it.replace(dataType.arguments) }?.type
      // flatMap should have a single value parameter
      fn.valueParameters.size == 1 &&
        // that is a function1 with two type arguments
        fn.valueParameters[0].type.isFunctionOrKFunctionType &&
        fn.valueParameters[0].type.arguments.size == 2 &&
        // the function return type should be assignable from the flatMap value argument return type
        fn.returnType?.let { fn.valueParameters[0].type.arguments[1].type.isSubtypeOf(it) } == true &&
        // flatMaps substituted return type should be assignable from the receiver data type
        substitutedReturnType?.let { dataType.isSubtypeOf(it) } == true
    }
    flatMap?.irCall()
  }

private fun IrElement.isVariableWithBinding() =
  this is IrVariable && initializer is IrCall &&
    (initializer as IrCall).symbol.owner.descriptor.isBind()

private fun IrElement.isBindingCall() =
  this is IrCall && this.descriptor.isBind()

private fun DeclarationDescriptor.isBind(): Boolean =
  fqNameSafe == FqName("arrow.not")

private fun IrElement.collectChildren(f: (IrElement) -> Boolean): List<IrElement> {
  val found = arrayListOf<IrElement>()
  accept(object : IrElementVisitor<Unit, ArrayList<IrElement>> {
    override fun visitElement(element: IrElement, data: ArrayList<IrElement>) {
      val result = f(element)
      if (result) data.add(element)
      element.acceptChildren(this, data)
    }
  }, found)
  return found
}

private fun IrBody.bindings(): List<IrElement> =
  collectChildren {
    it.isBindingCall() || it.isVariableWithBinding()
  }

