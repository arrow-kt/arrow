package arrow.meta.typeclasses

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.qq.func
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetterCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrPropertyAccessorCallBase
import org.jetbrains.kotlin.ir.expressions.mapValueParameters
import org.jetbrains.kotlin.ir.symbols.IrFieldSymbol
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.fqNameSafe
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames
import org.jetbrains.kotlin.psi2ir.findFirstFunction
import org.jetbrains.kotlin.resolve.descriptorUtil.getAllSuperClassifiers
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

val MetaComponentRegistrar.typeClasses: List<ExtensionPhase>
  get() =
    meta(
      func(
        match = {
          println("Considering function to replace: $text")
          valueParameters.any { it.defaultValue?.text == "`*`" }
        },
        map = { func ->
          println("Found function to replace: ${func.name}")
          val scopeNames = func.valueParameters.filter { it.defaultValue?.text == "`*`" }.map { it.name }
          listOf(
            """
              |$modality $visibility fun <$typeParameters> $receiver.$name($valueParameters): $returnType =
              |  ${scopeNames.fold(body.asString()) { acc, scope -> "$scope.run { $acc }" }}
              |"""
          )
        }
      ),
      IrGeneration { compilerContext, file, backendContext, bindingContext ->
        file.transformChildren(object : IrElementTransformer<Unit> {
          override fun visitFunctionAccess(expression: IrFunctionAccessExpression, data: Unit): IrElement {
            val defaultValues = expression.symbol.descriptor.valueParameters
              .mapNotNull { it.findPsi() as? KtParameter }
              .mapNotNull { it.defaultValue?.text }
            return if (defaultValues.contains("`*`")) { // with marker should be replaced by implicit injection
              println("visitFunctionAccess: ${expression.render()}\n${expression.descriptor.findPsi()?.text}")
              expression.mapValueParameters { valueParameterDescriptor ->
                val extensionType = valueParameterDescriptor.type
                val typeClass = extensionType.constructor.declarationDescriptor
                val dataType = extensionType.arguments[0].type.constructor.declarationDescriptor
                val typeClassPackage = typeClass?.parents?.first() as PackageFragmentDescriptor
                val typeClassFactory = typeClassPackage
                  .getMemberScope()
                  .getContributedDescriptors()
                  .find {
                    val result = it is FunctionDescriptor && it.returnType?.isSubtypeOf(extensionType) == true ||
                      it is ClassDescriptor && it.typeConstructor.supertypes.contains(extensionType) ||
                      it is PropertyDescriptor && it.type.isSubtypeOf(extensionType)
                    println("Considering: ${it.javaClass}, ${it.name}: $result")
                    result
                  }
                if (typeClassFactory == null) {
                  compilerContext.messageCollector.report(
                    CompilerMessageSeverity.ERROR,
                    "extension not found for $extensionType",
                    CompilerMessageLocation.create(valueParameterDescriptor.findPsi()?.text)
                  )
                }
                when (typeClassFactory) {
                  is FunctionDescriptor -> {
                    val typeClassIrFactory = backendContext.ir.symbols.externalSymbolTable.referenceFunction(typeClassFactory)
                    IrCallImpl(
                      startOffset = UNDEFINED_OFFSET,
                      endOffset = UNDEFINED_OFFSET,
                      type = typeClassIrFactory.owner.returnType,
                      symbol = typeClassIrFactory,
                      descriptor = typeClassIrFactory.owner.descriptor,
                      typeArgumentsCount = typeClassIrFactory.owner.descriptor.typeParameters.size,
                      valueArgumentsCount = typeClassIrFactory.owner.descriptor.valueParameters.size
                    )
                  }
                  is ClassDescriptor -> {
                    val irClass = backendContext.ir.symbols.externalSymbolTable.referenceClass(typeClassFactory)
                    irClass.constructors.firstOrNull()?.let { typeClassIrFactory ->
                      IrConstructorCallImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = typeClassIrFactory.owner.returnType,
                        symbol = typeClassIrFactory,
                        descriptor = typeClassIrFactory.owner.descriptor,
                        typeArgumentsCount = typeClassIrFactory.owner.descriptor.typeParameters.size,
                        valueArgumentsCount = typeClassIrFactory.owner.descriptor.valueParameters.size,
                        constructorTypeArgumentsCount = typeClassFactory.declaredTypeParameters.size
                      )
                    }
                  }
                  is PropertyDescriptor -> {
                    val irField = backendContext.ir.symbols.externalSymbolTable.referenceField(typeClassFactory)
                    irField.owner.correspondingPropertySymbol?.owner?.getter?.symbol?.let { typeClassIrFactory ->
                      IrCallImpl(
                        startOffset = UNDEFINED_OFFSET,
                        endOffset = UNDEFINED_OFFSET,
                        type = typeClassIrFactory.owner.returnType,
                        symbol = typeClassIrFactory,
                        descriptor = typeClassIrFactory.owner.descriptor,
                        typeArgumentsCount = typeClassIrFactory.owner.descriptor.typeParameters.size,
                        valueArgumentsCount = typeClassIrFactory.owner.descriptor.valueParameters.size
                      )
                    }
                  }
                  else -> null
                }
              }
            } else super.visitFunctionAccess(expression, data)
          }

          override fun visitValueParameter(declaration: IrValueParameter, data: Unit): IrStatement {
            if (declaration.defaultValue != null) {
              println("${declaration.parent.fqNameSafe}:visitValueParameter: ${declaration.descriptor.findPsi()?.text}")
              declaration.defaultValue?.expression
            }
            return super.visitValueParameter(declaration, data)
          }

          override fun visitCall(expression: IrCall, data: Unit): IrElement {
            println("Call: ${expression.render()}")
            expression.transformChildren(this, Unit)
            return super.visitCall(expression, data)
          }

          override fun visitExpression(expression: IrExpression, data: Unit): IrExpression {
            //println("Expression: ${expression.render()}")
            return super.visitExpression(expression, data)
          }
        }, Unit)
      }
    )

private fun KtClass.typeArgumentNames(): List<String> =
  typeClassTypeElement()?.typeArgumentsAsTypes?.map { it.text }.orEmpty()

private fun KtClass.typeClassTypeElement(): KtTypeElement? =
  getSuperTypeList()?.entries?.get(0)?.typeReference?.typeElement

private fun KtClass.typeClassName(): String =
  getSuperNames()[0]

private fun isExtension(ktClass: KtClass): Boolean =
  ktClass.annotationEntries.any {
    it.text == "@extension"
  }

private class HasWithReceiverVisitor : KtVisitorVoid() {
  var result = false
  override fun visitElement(element: PsiElement) {
    element.acceptChildren(this)
  }

}

private fun hasWithReceiver(ktClass: KtClass): Boolean =
  HasWithReceiverVisitor().also(ktClass::accept).result

private operator fun <A> List<A>.rangeTo(s: String): String =
  joinToString(s)

class Spread(
  val prefix: String,
  val separator: String,
  val postfix: String
)

private operator fun <A> List<A>.rangeTo(s: Spread): String =
  joinToString(prefix = s.prefix, separator = s.separator, postfix = s.postfix)