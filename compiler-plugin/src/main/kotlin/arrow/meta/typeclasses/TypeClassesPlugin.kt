package arrow.meta.typeclasses

import arrow.meta.extensions.ExtensionPhase
import arrow.meta.extensions.MetaComponentRegistrar
import arrow.meta.higherkind.buildIrValueParameter
import arrow.meta.qq.classOrObject
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.JvmGeneratorExtensions
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrExpressionBodyImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.mapValueParameters
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.endOffset
import org.jetbrains.kotlin.ir.util.fqNameSafe
import org.jetbrains.kotlin.ir.util.getArgumentsWithIr
import org.jetbrains.kotlin.ir.util.irCall
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.ir.util.startOffset
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames
import org.jetbrains.kotlin.psi2ir.Psi2IrTranslator
import org.jetbrains.kotlin.psi2ir.findFirstFunction
import org.jetbrains.kotlin.psi2ir.generators.BodyGenerator
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.descriptorUtil.parents

val MetaComponentRegistrar.typeClasses: List<ExtensionPhase>
  get() =
    meta(
      classOrObject(::isExtension) { ktClass ->
        val typeClass = ktClass.typeClassName()
        val typeArgs = ktClass.typeArgumentNames()
        val factoryName = typeClass.decapitalize()
        println("intercepted ${ktClass.name}")
        listOf(
          ktClass.text,
          "fun $factoryName(): $typeClass<${typeArgs..","}> = TODO()"
        )
      },
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
                val factoryName = "${typeClass.name.asString().decapitalize()}${dataType?.name}"
                val typeClassFactory = typeClassPackage.getMemberScope().findFirstFunction(factoryName) { it.returnType == extensionType }
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

private operator fun <A> List<A>.rangeTo(s: String): String =
  joinToString(s)

class Spread(
  val prefix: String,
  val separator: String,
  val postfix: String
)

private operator fun <A> List<A>.rangeTo(s: Spread): String =
  joinToString(prefix = s.prefix, separator = s.separator, postfix = s.postfix)