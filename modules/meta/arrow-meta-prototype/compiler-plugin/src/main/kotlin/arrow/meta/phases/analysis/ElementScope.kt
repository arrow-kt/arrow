package arrow.meta.phases.analysis

import arrow.meta.quotes.ClassScope
import arrow.meta.quotes.FuncScope
import arrow.meta.quotes.Scope
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockCodeFragment
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtExpressionCodeFragment
import org.jetbrains.kotlin.psi.KtFunctionTypeReceiver
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtInitializerList
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPropertyAccessor
import org.jetbrains.kotlin.psi.KtPropertyDelegate
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtSimpleNameStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtSuperTypeEntry
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeCodeFragment
import org.jetbrains.kotlin.psi.KtTypeElement
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterList
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.resolve.ImportPath

interface ElementScope {
  
  val valKeyword: PsiElement
  
  val varKeyword: PsiElement
  
  val String.expression: Scope<KtExpression>

  val String.dotQualifiedExpression: Scope<KtDotQualifiedExpression>

  val String.expressionOrNull: Scope<KtExpression>
  
  val thisExpression: Scope<KtThisExpression>
  
  val String.thisExpression: Scope<KtThisExpression>

  val String.binaryExpression: Scope<KtBinaryExpression>
  
  val String.callArguments: Scope<KtValueArgumentList>
  
  val String.typeArguments: Scope<KtTypeArgumentList>
  
  val String.typeArgument: Scope<KtTypeProjection>
  
  val String.type: Scope<KtTypeReference>
  
  val KtTypeElement.type: Scope<KtTypeReference>
  
  val String.typeOrNull: Scope<KtTypeReference>
  
  val KtTypeReference.functionTypeReceiver: Scope<KtFunctionTypeReceiver>
  
  val KtTypeReference.functionTypeParameter: Scope<KtParameter>
  
  fun typeAlias(
    name: String,
    typeParameters: List<String>,
    typeElement: KtTypeElement
  ): Scope<KtTypeAlias>
  
  fun typeAlias(
    name: String,
    typeParameters: List<String>,
    body: String
  ): Scope<KtTypeAlias>
  
  val star: PsiElement
  
  val comma: PsiElement
  
  val dot: PsiElement
  
  val colon: PsiElement
  
  val eq: PsiElement
  
  val semicolon: PsiElement
  
  val whiteSpaceAndArrow: Pair<PsiElement, PsiElement>
  
  val whiteSpace: PsiElement
  
  val String.whiteSpace: PsiElement
  
  val Int.newLine: PsiElement
  
  val String.`class`: ClassScope
  
  val String.`object`: Scope<KtObjectDeclaration>
  
  val companionObject: Scope<KtObjectDeclaration>
  
  val String.companionObject: Scope<KtObjectDeclaration>

  val <A: KtDeclaration> Scope<A>.synthetic: Scope<A>
  
  fun property(
    modifiers: String?,
    name: String,
    type: String?,
    isVar: Boolean,
    initializer: String?
  ): Scope<KtProperty>
  
  fun property(
    name: String,
    type: String?,
    isVar: Boolean,
    initializer: String?
  ): Scope<KtProperty>
  
  fun property(
    name: String,
    type: String?,
    isVar: Boolean
  ): Scope<KtProperty>
  
  val String.property: Scope<KtProperty>
  
  fun propertyGetter(expression: KtExpression): Scope<KtPropertyAccessor>
  
  fun propertySetter(expression: KtExpression): Scope<KtPropertyAccessor>
  
  fun propertyDelegate(expression: KtExpression): Scope<KtPropertyDelegate>
  
  val String.destructuringDeclaration: Scope<KtDestructuringDeclaration>
  
  val String.destructuringParameter: Scope<KtParameter>
  
  fun <A : KtDeclaration> String.declaration(): Scope<A>
  
  val String.nameIdentifier: PsiElement
  
  val String.nameIdentifierIfPossible: PsiElement?
  
  val String.simpleName: Scope<KtSimpleNameExpression>
  
  val String.operationName: Scope<KtSimpleNameExpression>
  
  val String.identifier: PsiElement
  
  val String.function: FuncScope
  
  val String.callableReferenceExpression: Scope<KtCallableReferenceExpression>
  
  val String.secondaryConstructor: Scope<KtSecondaryConstructor>
  
  fun modifierList(modifier: KtModifierKeywordToken): Scope<KtModifierList>
  
  val String.modifierList: Scope<KtModifierList>
  
  val emptyModifierList: Scope<KtModifierList>
  
  fun modifier(modifier: KtModifierKeywordToken): PsiElement
  
  val String.annotationEntry: Scope<KtAnnotationEntry>
  
  val emptyBody: Scope<KtBlockExpression>
  
  val anonymousInitializer: Scope<KtAnonymousInitializer>
  
  val emptyClassBody: Scope<KtClassBody>
  
  val String.parameter: Scope<KtParameter>
  
  val String.loopParameter: Scope<KtParameter>
  
  val String.parameterList: Scope<KtParameterList>
  
  val String.typeParameterList: Scope<KtTypeParameterList>
  
  val String.typeParameter: Scope<KtTypeParameter>
  
  val String.lambdaParameterListIfAny: Scope<KtParameterList>
  
  val String.lambdaParameterList: Scope<KtParameterList>
  
  fun lambdaExpression(
    parameters: String,
    body: String
  ): Scope<KtLambdaExpression>
  
  val String.enumEntry: Scope<KtEnumEntry>
  
  val enumEntryInitializerList: Scope<KtInitializerList>
  
  val String.whenEntry: Scope<KtWhenEntry>
  
  val String.whenCondition: Scope<KtWhenCondition>
  
  fun blockStringTemplateEntry(expression: KtExpression): Scope<KtStringTemplateEntryWithExpression>
  
  fun simpleNameStringTemplateEntry(name: String): Scope<KtSimpleNameStringTemplateEntry>
  
  fun literalStringTemplateEntry(literal: String): Scope<KtLiteralStringTemplateEntry>
  
  fun stringTemplate(content: String): Scope<KtStringTemplateExpression>
  
  val String.packageDirective: Scope<KtPackageDirective>

  val String.packageDirectiveOrNull: Scope<KtPackageDirective>
  
  fun importDirective(importPath: ImportPath): Scope<KtImportDirective>
  
  fun primaryConstructor(text: String = ""): Scope<KtPrimaryConstructor>
  
  val primaryConstructorNoArgs: Scope<KtPrimaryConstructor>
  
  fun primaryConstructorWithModifiers(modifiers: String?): Scope<KtPrimaryConstructor>
  
  val constructorKeyword: PsiElement
  
  fun labeledExpression(labelName: String): Scope<KtLabeledExpression>
  
  fun String.typeCodeFragment(context: PsiElement?): Scope<KtTypeCodeFragment>
  
  fun String.expressionCodeFragment(context: PsiElement?): Scope<KtExpressionCodeFragment>
  
  fun String.blockCodeFragment(context: PsiElement?): Scope<KtBlockCodeFragment>
  
  fun `if`(
    condition: KtExpression,
    thenExpr: KtExpression,
    elseExpr: KtExpression? = null
  ): Scope<KtIfExpression>
  
  fun argument(
    expression: KtExpression?,
    name: Name? = null,
    isSpread: Boolean = false,
    reformat: Boolean = true
  ): Scope<KtValueArgument>
  
  val String.argument: Scope<KtValueArgument>
  
  val String.superTypeCallEntry: Scope<KtSuperTypeCallEntry>
  
  val String.superTypeEntry: Scope<KtSuperTypeEntry>
  
  val String.delegatedSuperTypeEntry: Scope<KtConstructorDelegationCall>
  
  val String.block: Scope<KtBlockExpression>
  
  fun singleStatementBlock(
    statement: KtExpression,
    prevComment: String? = null,
    nextComment: String? = null
  ): Scope<KtBlockExpression>
  
  val String.comment: PsiComment

  companion object  {
    fun default(project: Project): ElementScope =
      DefaultElementScope(project)
  }

}
