package arrow.meta.dsl.codegen.ir

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.codegen.ir.IRGeneration
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrAnonymousInitializer
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrErrorDeclaration
import org.jetbrains.kotlin.ir.declarations.IrExternalPackageFragment
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrLocalDelegatedProperty
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrBreak
import org.jetbrains.kotlin.ir.expressions.IrBreakContinue
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrCallableReference
import org.jetbrains.kotlin.ir.expressions.IrCatch
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrComposite
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrContainerExpression
import org.jetbrains.kotlin.ir.expressions.IrContinue
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrDelegatingConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrDoWhileLoop
import org.jetbrains.kotlin.ir.expressions.IrDynamicExpression
import org.jetbrains.kotlin.ir.expressions.IrDynamicMemberExpression
import org.jetbrains.kotlin.ir.expressions.IrDynamicOperatorExpression
import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.expressions.IrEnumConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrErrorCallExpression
import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrFieldAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrGetClass
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrGetObjectValue
import org.jetbrains.kotlin.ir.expressions.IrGetSingletonValue
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrInstanceInitializerCall
import org.jetbrains.kotlin.ir.expressions.IrLocalDelegatedPropertyReference
import org.jetbrains.kotlin.ir.expressions.IrLoop
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrSetField
import org.jetbrains.kotlin.ir.expressions.IrSetVariable
import org.jetbrains.kotlin.ir.expressions.IrSpreadElement
import org.jetbrains.kotlin.ir.expressions.IrStringConcatenation
import org.jetbrains.kotlin.ir.expressions.IrSuspendableExpression
import org.jetbrains.kotlin.ir.expressions.IrSuspensionPoint
import org.jetbrains.kotlin.ir.expressions.IrSyntheticBody
import org.jetbrains.kotlin.ir.expressions.IrThrow
import org.jetbrains.kotlin.ir.expressions.IrTry
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.IrValueAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrVararg
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.expressions.IrWhileLoop
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.resolve.BindingContext

interface IrSyntax {
  fun IrGeneration(generate: (compilerContext: CompilerContext, file: IrFile, backendContext: BackendContext, bindingContext: BindingContext) -> Unit): IRGeneration =
    object : IRGeneration {
      override fun CompilerContext.generate(
        file: IrFile,
        backendContext: BackendContext,
        bindingContext: BindingContext
      ) {
        generate(this, file, backendContext, bindingContext)
      }
    }

  fun irElement(f: IrUtils.(IrElement) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitElement(expression: IrElement, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression)?.let { super.visitElement(it, data) } ?: super.visitElement(expression, data)
      }, Unit)
    }

  fun irModuleFragment(f: IrUtils.(IrModuleFragment) -> IrModuleFragment?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitModuleFragment(expression: IrModuleFragment, data: Unit): IrModuleFragment =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitModuleFragment(expression, data)
      }, Unit)
    }

  fun irFile(f: IrUtils.(IrFile) -> IrFile?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFile(expression: IrFile, data: Unit): IrFile =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitFile(expression, data)
      }, Unit)
    }

  fun irExternalPackageFragment(f: IrUtils.(IrExternalPackageFragment) -> IrExternalPackageFragment?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitExternalPackageFragment(expression: IrExternalPackageFragment, data: Unit): IrExternalPackageFragment =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitExternalPackageFragment(expression, data)
      }, Unit)
    }

  fun irDeclaration(f: IrUtils.(IrDeclaration) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDeclaration(expression: IrDeclaration, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitDeclaration(expression, data)
      }, Unit)
    }

  fun irClass(f: IrUtils.(IrClass) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitClass(expression: IrClass, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitClass(expression, data)
      }, Unit)
    }

  fun irFunction(f: IrUtils.(IrFunction) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFunction(expression: IrFunction, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitFunction(expression, data)
      }, Unit)
    }

  fun irSimpleFunction(f: IrUtils.(IrSimpleFunction) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSimpleFunction(expression: IrSimpleFunction, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitSimpleFunction(expression, data)
      }, Unit)
    }

  fun irConstructor(f: IrUtils.(IrConstructor) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitConstructor(expression: IrConstructor, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitConstructor(expression, data)
      }, Unit)
    }

  fun irProperty(f: IrUtils.(IrProperty) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitProperty(expression: IrProperty, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitProperty(expression, data)
      }, Unit)
    }

  fun irField(f: IrUtils.(IrField) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitField(expression: IrField, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitField(expression, data)
      }, Unit)
    }

  fun irLocalDelegatedProperty(f: IrUtils.(IrLocalDelegatedProperty) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitLocalDelegatedProperty(expression: IrLocalDelegatedProperty, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitLocalDelegatedProperty(expression, data)
      }, Unit)
    }

  fun irEnumEntry(f: IrUtils.(IrEnumEntry) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitEnumEntry(expression: IrEnumEntry, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitEnumEntry(expression, data)
      }, Unit)
    }

  fun irAnonymousInitializer(f: IrUtils.(IrAnonymousInitializer) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitAnonymousInitializer(expression: IrAnonymousInitializer, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitAnonymousInitializer(expression, data)
      }, Unit)
    }

  fun irVariable(f: IrUtils.(IrVariable) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitVariable(expression: IrVariable, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitVariable(expression, data)
      }, Unit)
    }

  fun irTypeParameter(f: IrUtils.(IrTypeParameter) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitTypeParameter(expression: IrTypeParameter, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitTypeParameter(expression, data)
      }, Unit)
    }

  fun irValueParameter(f: IrUtils.(IrValueParameter) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitValueParameter(expression: IrValueParameter, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitValueParameter(expression, data)
      }, Unit)
    }

  fun irBody(f: IrUtils.(IrBody) -> IrBody?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBody(body: IrBody, data: Unit): IrBody =
          f(IrUtils(backendContext, compilerContext), body) ?: super.visitBody(body, data)
      }, Unit)
    }

  fun irExpressionBody(f: IrUtils.(IrExpressionBody) -> IrBody?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitExpressionBody(expression: IrExpressionBody, data: Unit): IrBody =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitExpressionBody(expression, data)
      }, Unit)
    }

  fun irBlockBody(f: IrUtils.(IrBlockBody) -> IrBody?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBlockBody(expression: IrBlockBody, data: Unit): IrBody =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitBlockBody(expression, data)
      }, Unit)
    }

  fun irSyntheticBody(f: IrUtils.(IrSyntheticBody) -> IrBody?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSyntheticBody(expression: IrSyntheticBody, data: Unit): IrBody =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitSyntheticBody(expression, data)
      }, Unit)
    }

  fun irSuspendableExpression(f: IrUtils.(IrSuspendableExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSuspendableExpression(expression: IrSuspendableExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitSuspendableExpression(expression, data)
      }, Unit)
    }

  fun irSuspensionPoint(f: IrUtils.(IrSuspensionPoint) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSuspensionPoint(expression: IrSuspensionPoint, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitSuspensionPoint(expression, data)
      }, Unit)
    }

  fun irExpression(f: IrUtils.(IrExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitExpression(expression: IrExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitExpression(expression, data)
      }, Unit)
    }

  fun <A> Meta.irConst(f: IrUtils.(IrConst<A>) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun <T> visitConst(expression: IrConst<T>, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression as IrConst<A>) ?: super.visitConst(expression, data)
      }, Unit)
    }

  fun irVararg(f: IrUtils.(IrVararg) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitVararg(expression: IrVararg, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitVararg(expression, data)
      }, Unit)
    }

  fun irSpreadElement(f: IrUtils.(IrSpreadElement) -> IrSpreadElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSpreadElement(expression: IrSpreadElement, data: Unit): IrSpreadElement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitSpreadElement(expression, data)
      }, Unit)
    }

  fun irContainerExpression(f: IrUtils.(IrContainerExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitContainerExpression(expression: IrContainerExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitContainerExpression(expression, data)
      }, Unit)
    }

  fun irBlock(f: IrUtils.(IrBlock) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBlock(expression: IrBlock, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitBlock(expression, data)
      }, Unit)
    }

  fun irComposite(f: IrUtils.(IrComposite) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitComposite(expression: IrComposite, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitComposite(expression, data)
      }, Unit)
    }

  fun irStringConcatenation(f: IrUtils.(IrStringConcatenation) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitStringConcatenation(expression: IrStringConcatenation, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitStringConcatenation(expression, data)
      }, Unit)
    }

  fun irDeclarationReference(f: IrUtils.(IrDeclarationReference) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDeclarationReference(expression: IrDeclarationReference, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitDeclarationReference(expression, data)
      }, Unit)
    }

  fun irSingletonReference(f: IrUtils.(IrGetSingletonValue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSingletonReference(expression: IrGetSingletonValue, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitSingletonReference(expression, data)
      }, Unit)
    }

  fun irGetObjectValue(f: IrUtils.(IrGetObjectValue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetObjectValue(expression: IrGetObjectValue, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitGetObjectValue(expression, data)
      }, Unit)
    }

  fun irGetEnumValue(f: IrUtils.(IrGetEnumValue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetEnumValue(expression: IrGetEnumValue, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitGetEnumValue(expression, data)
      }, Unit)
    }

  fun irValueAccess(f: IrUtils.(IrValueAccessExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitValueAccess(expression: IrValueAccessExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitValueAccess(expression, data)
      }, Unit)
    }

  fun irGetValue(f: IrUtils.(IrGetValue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetValue(expression: IrGetValue, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitGetValue(expression, data)
      }, Unit)
    }

  fun irSetVariable(f: IrUtils.(IrSetVariable) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSetVariable(expression: IrSetVariable, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitSetVariable(expression, data)
      }, Unit)
    }

  fun irFieldAccess(f: IrUtils.(IrFieldAccessExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFieldAccess(expression: IrFieldAccessExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitFieldAccess(expression, data)
      }, Unit)
    }

  fun irGetField(f: IrUtils.(IrGetField) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetField(expression: IrGetField, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitGetField(expression, data)
      }, Unit)
    }

  fun irSetField(f: IrUtils.(IrSetField) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitSetField(expression: IrSetField, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitSetField(expression, data)
      }, Unit)
    }

  fun irMemberAccess(f: IrUtils.(IrMemberAccessExpression) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitMemberAccess(expression: IrMemberAccessExpression, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitMemberAccess(expression, data)
      }, Unit)
    }

  fun irFunctionAccess(f: IrUtils.(IrFunctionAccessExpression) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFunctionAccess(expression: IrFunctionAccessExpression, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitFunctionAccess(expression, data)
      }, Unit)
    }

  fun irCall(f: IrUtils.(IrCall) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitCall(expression: IrCall, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitCall(expression, data)
      }, Unit)
    }

  fun irConstructorCall(f: IrUtils.(IrConstructorCall) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitConstructorCall(expression: IrConstructorCall, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitConstructorCall(expression, data)
      }, Unit)
    }

  fun irDelegatingConstructorCall(f: IrUtils.(IrDelegatingConstructorCall) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression)
            ?: super.visitDelegatingConstructorCall(expression, data)
      }, Unit)
    }

  fun irEnumConstructorCall(f: IrUtils.(IrEnumConstructorCall) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitEnumConstructorCall(expression: IrEnumConstructorCall, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitEnumConstructorCall(expression, data)
      }, Unit)
    }

  fun irGetClass(f: IrUtils.(IrGetClass) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitGetClass(expression: IrGetClass, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitGetClass(expression, data)
      }, Unit)
    }

  fun irCallableReference(f: IrUtils.(IrCallableReference) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitCallableReference(expression: IrCallableReference, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitCallableReference(expression, data)
      }, Unit)
    }

  fun irFunctionReference(f: IrUtils.(IrFunctionReference) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitFunctionReference(expression: IrFunctionReference, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitFunctionReference(expression, data)
      }, Unit)
    }

  fun irPropertyReference(f: IrUtils.(IrPropertyReference) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitPropertyReference(expression, data)
      }, Unit)
    }

  fun irLocalDelegatedPropertyReference(f: IrUtils.(IrLocalDelegatedPropertyReference) -> IrElement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitLocalDelegatedPropertyReference(expression: IrLocalDelegatedPropertyReference, data: Unit): IrElement =
          f(IrUtils(backendContext, compilerContext), expression)
            ?: super.visitLocalDelegatedPropertyReference(expression, data)
      }, Unit)
    }

  fun irClassReference(f: IrUtils.(IrClassReference) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitClassReference(expression: IrClassReference, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitClassReference(expression, data)
      }, Unit)
    }

  fun irInstanceInitializerCall(f: IrUtils.(IrInstanceInitializerCall) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitInstanceInitializerCall(expression, data)
      }, Unit)
    }

  fun irTypeOperator(f: IrUtils.(IrTypeOperatorCall) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitTypeOperator(expression, data)
      }, Unit)
    }

  fun irWhen(f: IrUtils.(IrWhen) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitWhen(expression: IrWhen, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitWhen(expression, data)
      }, Unit)
    }

  fun irBranch(f: IrUtils.(IrBranch) -> IrBranch?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBranch(expression: IrBranch, data: Unit): IrBranch =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitBranch(expression, data)
      }, Unit)
    }

  fun irElseBranch(f: IrUtils.(IrElseBranch) -> IrElseBranch?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitElseBranch(expression: IrElseBranch, data: Unit): IrElseBranch =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitElseBranch(expression, data)
      }, Unit)
    }

  fun irLoop(f: IrUtils.(IrLoop) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitLoop(expression: IrLoop, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitLoop(expression, data)
      }, Unit)
    }

  fun irWhileLoop(f: IrUtils.(IrWhileLoop) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitWhileLoop(expression: IrWhileLoop, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitWhileLoop(expression, data)
      }, Unit)
    }

  fun irDoWhileLoop(f: IrUtils.(IrDoWhileLoop) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDoWhileLoop(expression: IrDoWhileLoop, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitDoWhileLoop(expression, data)
      }, Unit)
    }

  fun irTry(f: IrUtils.(IrTry) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitTry(expression: IrTry, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitTry(expression, data)
      }, Unit)
    }

  fun irCatch(f: IrUtils.(IrCatch) -> IrCatch?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitCatch(expression: IrCatch, data: Unit): IrCatch =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitCatch(expression, data)
      }, Unit)
    }

  fun irBreakContinue(f: IrUtils.(IrBreakContinue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBreakContinue(expression: IrBreakContinue, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitBreakContinue(expression, data)
      }, Unit)
    }

  fun irBreak(f: IrUtils.(IrBreak) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitBreak(expression: IrBreak, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitBreak(expression, data)
      }, Unit)
    }

  fun irContinue(f: IrUtils.(IrContinue) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitContinue(expression: IrContinue, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitContinue(expression, data)
      }, Unit)
    }

  fun irReturn(f: IrUtils.(IrReturn) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitReturn(expression: IrReturn, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitReturn(expression, data)
      }, Unit)
    }

  fun irThrow(f: IrUtils.(IrThrow) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitThrow(expression: IrThrow, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitThrow(expression, data)
      }, Unit)
    }

  fun irDynamicExpression(f: IrUtils.(IrDynamicExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDynamicExpression(expression: IrDynamicExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitDynamicExpression(expression, data)
      }, Unit)
    }

  fun irDynamicOperatorExpression(f: IrUtils.(IrDynamicOperatorExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDynamicOperatorExpression(expression: IrDynamicOperatorExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression)
            ?: super.visitDynamicOperatorExpression(expression, data)
      }, Unit)
    }

  fun irDynamicMemberExpression(f: IrUtils.(IrDynamicMemberExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitDynamicMemberExpression(expression: IrDynamicMemberExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitDynamicMemberExpression(expression, data)
      }, Unit)
    }

  fun irErrorDeclaration(f: IrUtils.(IrErrorDeclaration) -> IrStatement?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitErrorDeclaration(expression: IrErrorDeclaration, data: Unit): IrStatement =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitErrorDeclaration(expression, data)
      }, Unit)
    }

  fun irErrorExpression(f: IrUtils.(IrErrorExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitErrorExpression(expression: IrErrorExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitErrorExpression(expression, data)
      }, Unit)
    }

  fun irErrorCallExpression(f: IrUtils.(IrErrorCallExpression) -> IrExpression?): IRGeneration =
    IrGeneration { compilerContext, file, backendContext, bindingContext ->
      file.transformChildren(object : IrElementTransformer<Unit> {
        override fun visitErrorCallExpression(expression: IrErrorCallExpression, data: Unit): IrExpression =
          f(IrUtils(backendContext, compilerContext), expression) ?: super.visitErrorCallExpression(expression, data)
      }, Unit)
    }
}
