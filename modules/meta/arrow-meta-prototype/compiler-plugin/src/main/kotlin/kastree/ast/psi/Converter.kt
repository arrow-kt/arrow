package kastree.ast.psi

import kastree.ast.ExtrasMap
import kastree.ast.Node
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.descriptors.annotations.AnnotationUseSiteTarget
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.*
import java.util.*

open class Converter {
  protected open fun onNode(node: Node, elem: PsiElement) { }

  open fun convertAnnotated(v: KtAnnotatedExpression) = Node.Expr.Annotated(
    anns = convertAnnotationSets(v),
    expr = convertExpr(v.baseExpression ?: error("No annotated expr for $v"))
  ).map(v).let {
    // As a special case, instead of annotating a type/binary op, we mean to just annotate its lhs
    val expr = it.expr
    when (expr) {
      is Node.Expr.BinaryOp -> expr.copy(
        lhs = it.copy(expr = expr.lhs)
      )
      is Node.Expr.TypeOp -> expr.copy(
        lhs = it.copy(expr = expr.lhs)
      )
      else -> it
    }
  }

  open fun convertAnnotation(v: KtAnnotationEntry) = Node.Modifier.AnnotationSet.Annotation(
    names = v.typeReference?.names ?: error("Missing annotation name"),
    typeArgs = v.typeArguments.map { convertType(it) ?: error("No ann typ arg for $v") },
    args = convertValueArgs(v.valueArgumentList)
  ).map(v)

  open fun convertAnnotationSet(v: KtAnnotation) = Node.Modifier.AnnotationSet(
    target = v.useSiteTarget?.let(::convertAnnotationSetTarget),
    anns = v.entries.map(::convertAnnotation)
  ).map(v)

  open fun convertAnnotationSets(v: KtElement): List<Node.Modifier.AnnotationSet> = v.children.flatMap { elem ->
    // We go over the node children because we want to preserve order
    when (elem) {
      is KtAnnotationEntry ->
        listOf(Node.Modifier.AnnotationSet(
          target = elem.useSiteTarget?.let(::convertAnnotationSetTarget),
          anns = listOf(convertAnnotation(elem))
        ).map(elem))
      is KtAnnotation ->
        listOf(convertAnnotationSet(elem))
      is KtFileAnnotationList ->
        convertAnnotationSets(elem)
      else ->
        emptyList()
    }
  }

  open fun convertCommands(v: KtElement): List<Node.Command> =
    v.children.filterIsInstance<PsiComment>().map { Node.Command(name = it.text) }

  open fun convertAnnotationSetTarget(v: KtAnnotationUseSiteTarget) = when (v.getAnnotationUseSiteTarget()) {
    AnnotationUseSiteTarget.FIELD -> Node.Modifier.AnnotationSet.Target.FIELD
    AnnotationUseSiteTarget.FILE -> Node.Modifier.AnnotationSet.Target.FILE
    AnnotationUseSiteTarget.PROPERTY -> Node.Modifier.AnnotationSet.Target.PROPERTY
    AnnotationUseSiteTarget.PROPERTY_GETTER -> Node.Modifier.AnnotationSet.Target.GET
    AnnotationUseSiteTarget.PROPERTY_SETTER -> Node.Modifier.AnnotationSet.Target.SET
    AnnotationUseSiteTarget.RECEIVER -> Node.Modifier.AnnotationSet.Target.RECEIVER
    AnnotationUseSiteTarget.CONSTRUCTOR_PARAMETER -> Node.Modifier.AnnotationSet.Target.PARAM
    AnnotationUseSiteTarget.SETTER_PARAMETER -> Node.Modifier.AnnotationSet.Target.SETPARAM
    AnnotationUseSiteTarget.PROPERTY_DELEGATE_FIELD -> Node.Modifier.AnnotationSet.Target.DELEGATE
  }

  open fun convertAnonFunc(v: KtNamedFunction) = Node.Expr.AnonFunc(convertFunc(v))

  open fun convertArrayAccess(v: KtArrayAccessExpression) = Node.Expr.ArrayAccess(
    expr = convertExpr(v.arrayExpression ?: error("No array expr for $v")),
    indices = v.indexExpressions.map(::convertExpr)
  ).map(v)

  open fun convertBinaryOp(v: KtBinaryExpression) = Node.Expr.BinaryOp(
    lhs = convertExpr(v.left ?: error("No binary lhs for $v")),
    oper = binaryTokensByText[v.operationReference.text].let {
      if (it != null) Node.Expr.BinaryOp.Oper.Token(it).map(v.operationReference)
      else Node.Expr.BinaryOp.Oper.Infix(v.operationReference.text).map(v.operationReference)
    },
    rhs = convertExpr(v.right ?: error("No binary rhs for $v"))
  ).map(v)

  open fun convertBinaryOp(v: KtQualifiedExpression) = Node.Expr.BinaryOp(
    lhs = convertExpr(v.receiverExpression),
    oper = Node.Expr.BinaryOp.Oper.Token(
      if (v is KtDotQualifiedExpression) Node.Expr.BinaryOp.Token.DOT else Node.Expr.BinaryOp.Token.DOT_SAFE
    ),
    rhs = convertExpr(v.selectorExpression ?: error("No qualified rhs for $v"))
  ).map(v)

  open fun convertBlock(v: KtBlockExpression) = Node.Block(
    stmts = v.statements.map(::convertStmtNo)
  ).map(v)

  open fun convertBrace(v: KtBlockExpression) = Node.Expr.Brace(
    params = emptyList(),
    block = convertBlock(v)
  ).map(v)

  open fun convertBrace(v: KtFunctionLiteral) = Node.Expr.Brace(
    params = v.valueParameters.map(::convertBraceParam),
    block = v.bodyExpression?.let(::convertBlock)
  ).map(v)

  open fun convertBrace(v: KtLambdaExpression) = Node.Expr.Brace(
    params = v.valueParameters.map(::convertBraceParam),
    block = v.bodyExpression?.let(::convertBlock)
  ).map(v)

  open fun convertBraceParam(v: KtParameter) = Node.Expr.Brace.Param(
    vars = convertPropertyVars(v),
    destructType = if (v.destructuringDeclaration != null) v.typeReference?.let(::convertType) else null
  ).map(v)

  open fun convertBreak(v: KtBreakExpression) = Node.Expr.Break(
    label = v.getLabelName()
  ).map(v)

  open fun convertCall(v: KtCallExpression) = Node.Expr.Call(
    expr = convertExpr(v.calleeExpression ?: error("No call expr for $v")),
    typeArgs = v.typeArguments.map(::convertType),
    args = convertValueArgs(v.valueArgumentList),
    lambda = v.lambdaArguments.singleOrNull()?.let(::convertCallTrailLambda)
  ).map(v)

  open fun convertCallTrailLambda(v: KtLambdaArgument): Node.Expr.Call.TrailLambda {
    var label: String? = null
    var anns: List<Node.Modifier.AnnotationSet> = emptyList()
    fun KtExpression.extractLambda(allowParens: Boolean = false): KtLambdaExpression? = when (this) {
      is KtLambdaExpression -> this
      is KtLabeledExpression -> baseExpression?.extractLambda(allowParens).also {
        label = getLabelName()
      }
      is KtAnnotatedExpression -> baseExpression?.extractLambda(allowParens).also {
        anns = convertAnnotationSets(this)
      }
      is KtParenthesizedExpression -> if (allowParens) expression?.extractLambda(allowParens) else null
      else -> null
    }
    val expr = v.getArgumentExpression()?.extractLambda() ?: error("No lambda for $v")
    return Node.Expr.Call.TrailLambda(
      anns = anns,
      label = label,
      func = convertBrace(expr)
    ).map(v)
  }

  open fun convertCollLit(v: KtCollectionLiteralExpression) = Node.Expr.CollLit(
    exprs = v.getInnerExpressions().map(::convertExpr)
  ).map(v)

  open fun convertConst(v: KtConstantExpression) = Node.Expr.Const(
    value = v.text,
    form = when (v.node.elementType) {
      KtNodeTypes.BOOLEAN_CONSTANT -> Node.Expr.Const.Form.BOOLEAN
      KtNodeTypes.CHARACTER_CONSTANT -> Node.Expr.Const.Form.CHAR
      KtNodeTypes.INTEGER_CONSTANT -> Node.Expr.Const.Form.INT
      KtNodeTypes.FLOAT_CONSTANT -> Node.Expr.Const.Form.FLOAT
      KtNodeTypes.NULL -> Node.Expr.Const.Form.NULL
      else -> error("Unrecognized const type for $v")
    }
  )

  open fun convertConstructor(v: KtSecondaryConstructor) = Node.Decl.Constructor(
    mods = convertModifiers(v),
    params = v.valueParameters.map(::convertFuncParam),
    delegationCall = if (v.hasImplicitDelegationCall()) null else v.getDelegationCall().let {
      Node.Decl.Constructor.DelegationCall(
        target =
        if (it.isCallToThis) Node.Decl.Constructor.DelegationTarget.THIS
        else Node.Decl.Constructor.DelegationTarget.SUPER,
        args = convertValueArgs(it.valueArgumentList)
      ).map(it)
    },
    block = v.bodyExpression?.let(::convertBlock)
  ).map(v)

  open fun convertContinue(v: KtContinueExpression) = Node.Expr.Continue(
    label = v.getLabelName()
  ).map(v)

  open fun convertDecl(v: KtDeclaration): Node.Decl = when (v) {
    is KtEnumEntry -> convertEnumEntry(v)
    is KtClassOrObject -> convertStructured(v)
    is KtAnonymousInitializer -> convertInit(v)
    is KtNamedFunction -> convertFunc(v)
    is KtDestructuringDeclaration -> convertProperty(v)
    is KtProperty -> convertProperty(v)
    is KtTypeAlias -> convertTypeAlias(v)
    is KtSecondaryConstructor -> convertConstructor(v)
    else -> error("Unrecognized declaration type for $v")
  }

  open fun convertDoubleColonRefCallable(v: KtCallableReferenceExpression) = Node.Expr.DoubleColonRef.Callable(
    recv = v.receiverExpression?.let { convertDoubleColonRefRecv(it, v.questionMarks) },
    name = v.callableReference.getReferencedName()
  ).map(v)

  open fun convertDoubleColonRefClass(v: KtClassLiteralExpression) = Node.Expr.DoubleColonRef.Class(
    recv = v.receiverExpression?.let { convertDoubleColonRefRecv(it, v.questionMarks) }
  ).map(v)

  open fun convertDoubleColonRefRecv(v: KtExpression, questionMarks: Int): Node.Expr.DoubleColonRef.Recv = when(v) {
    is KtSimpleNameExpression -> Node.Expr.DoubleColonRef.Recv.Type(
      type = Node.TypeRef.Simple(
        listOf(Node.TypeRef.Simple.Piece(v.getReferencedName(), emptyList()).map(v))
      ).map(v),
      questionMarks = questionMarks
    ).map(v)
    is KtCallExpression ->
      if (v.valueArgumentList == null && v.lambdaArguments.isEmpty())
        Node.Expr.DoubleColonRef.Recv.Type(
          type = Node.TypeRef.Simple(listOf(Node.TypeRef.Simple.Piece(
            name = v.calleeExpression?.text ?: error("Missing text for call ref type of $v"),
            typeParams = convertTypeParams(v.typeArgumentList)
          ).map(v))).map(v),
          questionMarks = questionMarks
        ).map(v)
      else Node.Expr.DoubleColonRef.Recv.Expr(convertExpr(v)).map(v)
    is KtDotQualifiedExpression -> {
      val lhs = convertDoubleColonRefRecv(v.receiverExpression, questionMarks)
      val rhs = v.selectorExpression?.let { convertDoubleColonRefRecv(it, questionMarks) }
      if (lhs is Node.Expr.DoubleColonRef.Recv.Type && rhs is Node.Expr.DoubleColonRef.Recv.Type)
        Node.Expr.DoubleColonRef.Recv.Type(
          type = Node.TypeRef.Simple(lhs.type.pieces + rhs.type.pieces).map(v),
          questionMarks = 0
        ).map(v)
      else Node.Expr.DoubleColonRef.Recv.Expr(convertExpr(v)).map(v)
    }
    else -> Node.Expr.DoubleColonRef.Recv.Expr(convertExpr(v)).map(v)
  }

  open fun convertEnumEntry(v: KtEnumEntry) = Node.Decl.EnumEntry(
    mods = convertModifiers(v),
    name = v.name ?: error("Unnamed enum"),
    args = convertValueArgs((v.superTypeListEntries.firstOrNull() as? KtSuperTypeCallEntry)?.valueArgumentList),
    members = v.declarations.map(::convertDecl)
  ).map(v)

  open fun convertExpr(v: KtExpression): Node.Expr = when (v) {
    is KtIfExpression -> convertIf(v)
    is KtTryExpression -> convertTry(v)
    is KtForExpression -> convertFor(v)
    is KtWhileExpressionBase -> convertWhile(v)
    is KtBinaryExpression -> convertBinaryOp(v)
    is KtQualifiedExpression -> convertBinaryOp(v)
    is KtUnaryExpression -> convertUnaryOp(v)
    is KtBinaryExpressionWithTypeRHS -> convertTypeOp(v)
    is KtIsExpression -> convertTypeOp(v)
    is KtCallableReferenceExpression -> convertDoubleColonRefCallable(v)
    is KtClassLiteralExpression -> convertDoubleColonRefClass(v)
    is KtParenthesizedExpression -> convertParen(v)
    is KtStringTemplateExpression -> convertStringTmpl(v)
    is KtConstantExpression -> convertConst(v)
    is KtBlockExpression -> convertBrace(v)
    is KtFunctionLiteral -> convertBrace(v)
    is KtLambdaExpression -> convertBrace(v)
    is KtThisExpression -> convertThis(v)
    is KtSuperExpression -> convertSuper(v)
    is KtWhenExpression -> convertWhen(v)
    is KtObjectLiteralExpression -> convertObject(v)
    is KtThrowExpression -> convertThrow(v)
    is KtReturnExpression -> convertReturn(v)
    is KtContinueExpression -> convertContinue(v)
    is KtBreakExpression -> convertBreak(v)
    is KtCollectionLiteralExpression -> convertCollLit(v)
    is KtSimpleNameExpression -> convertName(v)
    is KtLabeledExpression -> convertLabeled(v)
    is KtAnnotatedExpression -> convertAnnotated(v)
    is KtCallExpression -> convertCall(v)
    is KtArrayAccessExpression -> convertArrayAccess(v)
    is KtNamedFunction -> convertAnonFunc(v)
    is KtProperty -> convertPropertyExpr(v)
    is KtDestructuringDeclaration -> convertPropertyExpr(v)
    // TODO: this is present in a recovery test where an interface decl is on rhs of a gt expr
    is KtClass -> throw Unsupported("Class expressions not supported")
    else -> error("Unrecognized expression type from $v")
  }

  open fun convertFile(v: KtFile) = Node.File(
    anns = convertAnnotationSets(v),
    pkg = v.packageDirective?.takeIf { it.packageNames.isNotEmpty() }?.let(::convertPackage),
    imports = v.importDirectives.map(::convertImport),
    commands = convertCommands(v),
    decls = v.declarations.map(::convertDecl)
  ).map(v)

  open fun convertFor(v: KtForExpression) = Node.Expr.For(
    anns = v.loopParameter?.annotations?.map(::convertAnnotationSet) ?: emptyList(),
    vars = convertPropertyVars(v.loopParameter ?: error("No param on for $v")),
    inExpr = convertExpr(v.loopRange ?: error("No in range for $v")),
    body = convertExpr(v.body ?: error("No body for $v"))
  ).map(v)

  open fun convertFunc(v: KtNamedFunction) = Node.Decl.Func(
    mods = convertModifiers(v),
    typeParams =
    if (v.hasTypeParameterListBeforeFunctionName()) v.typeParameters.map(::convertTypeParam) else emptyList(),
    receiverType = v.receiverTypeReference?.let(::convertType),
    name = v.name,
    paramTypeParams =
    if (!v.hasTypeParameterListBeforeFunctionName()) v.typeParameters.map(::convertTypeParam) else emptyList(),
    params = v.valueParameters.map(::convertFuncParam),
    type = v.typeReference?.let(::convertType),
    typeConstraints = v.typeConstraints.map(::convertTypeConstraint),
    body = v.bodyExpression?.let(::convertFuncBody)
  ).map(v)

  open fun convertFuncBody(v: KtExpression) =
    if (v is KtBlockExpression) Node.Decl.Func.Body.Block(convertBlock(v)).map(v)
    else Node.Decl.Func.Body.Expr(convertExpr(v)).map(v)

  open fun convertFuncParam(v: KtParameter) = Node.Decl.Func.Param(
    mods = convertModifiers(v),
    readOnly = if (v.hasValOrVar()) !v.isMutable else null,
    name = v.name ?: error("No param name"),
    type = v.typeReference?.let(::convertType),
    default = v.defaultValue?.let(::convertExpr)
  ).map(v)

  open fun convertIf(v: KtIfExpression) = Node.Expr.If(
    expr = convertExpr(v.condition ?: error("No cond on if for $v")),
    body = convertExpr(v.then ?: error("No then on if for $v")),
    elseBody = v.`else`?.let(::convertExpr)
  ).map(v)

  open fun convertImport(v: KtImportDirective) = Node.Import(
    names = v.importedFqName?.pathSegments()?.map { it.asString() } ?: error("Missing import path"),
    wildcard = v.isAllUnder,
    alias = v.aliasName
  ).map(v)

  open fun convertInit(v: KtAnonymousInitializer) = Node.Decl.Init(
    block = convertBlock(v.body as? KtBlockExpression ?: error("No init block for $v"))
  ).map(v)

  open fun convertLabeled(v: KtLabeledExpression) = Node.Expr.Labeled(
    label = v.getLabelName() ?: error("No label name for $v"),
    expr = convertExpr(v.baseExpression ?: error("No label expr for $v"))
  ).map(v)

  open fun convertModifiers(v: KtModifierListOwner) = convertModifiers(v.modifierList)

  open fun convertModifiers(v: KtModifierList?) = v?.node?.children().orEmpty().mapNotNull { node ->
    // We go over the node children because we want to preserve order
    node.psi.let {
      when (it) {
        is KtAnnotationEntry -> Node.Modifier.AnnotationSet(
          target = it.useSiteTarget?.let(::convertAnnotationSetTarget),
          anns = listOf(convertAnnotation(it))
        ).map(it)
        is KtAnnotation -> convertAnnotationSet(it)
        is PsiWhiteSpace -> null
        else -> when (node.text) {
          // We ignore some modifiers because we handle them elsewhere
          "enum", "companion" -> null
          else -> modifiersByText[node.text]?.let {
            Node.Modifier.Lit(it).let { lit -> (node.psi as? KtElement)?.let { lit.map(it) } ?: lit }
          } ?: error("Unrecognized modifier: ${node.text}")
        }
      }
    }
  }.toList()

  open fun convertName(v: KtSimpleNameExpression) = Node.Expr.Name(
    name = v.getReferencedName()
  ).map(v)

  open fun convertObject(v: KtObjectLiteralExpression) = Node.Expr.Object(
    parents = v.objectDeclaration.superTypeListEntries.map(::convertParent),
    members = v.objectDeclaration.declarations.map(::convertDecl)
  ).map(v)

  open fun convertPackage(v: KtPackageDirective) = Node.Package(
    mods = convertModifiers(v),
    names = v.packageNames.map { it.getReferencedName() }
  ).map(v)

  open fun convertParen(v: KtParenthesizedExpression) = Node.Expr.Paren(
    expr = convertExpr(v.expression ?: error("No paren expr for $v"))
  )

  open fun convertParent(v: KtSuperTypeListEntry) = when (v) {
    is KtSuperTypeCallEntry -> Node.Decl.Structured.Parent.CallConstructor(
      type = v.typeReference?.let(::convertTypeRef) as? Node.TypeRef.Simple ?: error("Bad type on super call $v"),
      typeArgs = v.typeArguments.map(::convertType),
      args = convertValueArgs(v.valueArgumentList),
      // TODO
      lambda = null
    ).map(v)
    else -> Node.Decl.Structured.Parent.Type(
      type = v.typeReference?.let(::convertTypeRef) as? Node.TypeRef.Simple ?: error("Bad type on super call $v"),
      by = (v as? KtDelegatedSuperTypeEntry)?.delegateExpression?.let(::convertExpr)
    ).map(v)
  }

  open fun convertPrimaryConstructor(v: KtPrimaryConstructor) = Node.Decl.Structured.PrimaryConstructor(
    mods = convertModifiers(v),
    params = v.valueParameters.map(::convertFuncParam)
  ).map(v)

  open fun convertProperty(v: KtDestructuringDeclaration) = Node.Decl.Property(
    mods = convertModifiers(v),
    readOnly = !v.isVar,
    typeParams = emptyList(),
    receiverType = null,
    vars = v.entries.map(::convertPropertyVar),
    typeConstraints = emptyList(),
    delegated = false,
    expr = v.initializer?.let(::convertExpr),
    accessors = null
  ).map(v)

  open fun convertProperty(v: KtProperty) = Node.Decl.Property(
    mods = convertModifiers(v),
    readOnly = !v.isVar,
    typeParams = v.typeParameters.map(::convertTypeParam),
    receiverType = v.receiverTypeReference?.let(::convertType),
    vars = listOf(Node.Decl.Property.Var(
      name = v.name ?: error("No property name on $v"),
      type = v.typeReference?.let(::convertType)
    ).map(v)),
    typeConstraints = v.typeConstraints.map(::convertTypeConstraint),
    delegated = v.hasDelegateExpression(),
    expr = v.delegateExpressionOrInitializer?.let(::convertExpr),
    accessors = v.accessors.map(::convertPropertyAccessor).let {
      if (it.isEmpty()) null else Node.Decl.Property.Accessors(
        first = it.first(),
        second = it.getOrNull(1)
      )
    }
  ).map(v)

  open fun convertPropertyAccessor(v: KtPropertyAccessor) =
    if (v.isGetter) Node.Decl.Property.Accessor.Get(
      mods = convertModifiers(v),
      type = v.returnTypeReference?.let(::convertType),
      body = v.bodyExpression?.let(::convertFuncBody)
    ).map(v) else Node.Decl.Property.Accessor.Set(
      mods = convertModifiers(v),
      paramMods = v.parameter?.let(::convertModifiers) ?: emptyList(),
      paramName = v.parameter?.name,
      paramType = v.parameter?.typeReference?.let(::convertType),
      body = v.bodyExpression?.let(::convertFuncBody)
    ).map(v)

  open fun convertPropertyExpr(v: KtDestructuringDeclaration) = Node.Expr.Property(
    decl = convertProperty(v)
  ).map(v)

  open fun convertPropertyExpr(v: KtProperty) = Node.Expr.Property(
    decl = convertProperty(v)
  ).map(v)

  open fun convertPropertyVar(v: KtDestructuringDeclarationEntry) =
    if (v.name == "_") null else Node.Decl.Property.Var(
      name = v.name ?: error("No property name on $v"),
      type = v.typeReference?.let(::convertType)
    ).map(v)

  open fun convertPropertyVars(v: KtParameter) =
    v.destructuringDeclaration?.entries?.map(::convertPropertyVar) ?: listOf(
      if (v.name == "_") null else Node.Decl.Property.Var(
        name = v.name ?: error("No property name on $v"),
        type = v.typeReference?.let(::convertType)
      ).map(v)
    )

  open fun convertReturn(v: KtReturnExpression) = Node.Expr.Return(
    label = v.getLabelName(),
    expr = v.returnedExpression?.let(::convertExpr)
  ).map(v)

  open fun convertStmtNo(v: KtExpression) =
    if (v is KtDeclaration) Node.Stmt.Decl(convertDecl(v)).map(v) else Node.Stmt.Expr(convertExpr(v)).map(v)

  open fun convertStringTmpl(v: KtStringTemplateExpression) = Node.Expr.StringTmpl(
    elems = v.entries.map(::convertStringTmplElem),
    raw = v.text.startsWith("\"\"\"")
  ).map(v)

  open fun convertStringTmplElem(v: KtStringTemplateEntry) = when (v) {
    is KtLiteralStringTemplateEntry ->
      Node.Expr.StringTmpl.Elem.Regular(v.text).map(v)
    is KtSimpleNameStringTemplateEntry ->
      Node.Expr.StringTmpl.Elem.ShortTmpl(v.expression?.text ?: error("No short tmpl text")).map(v)
    is KtBlockStringTemplateEntry ->
      Node.Expr.StringTmpl.Elem.LongTmpl(convertExpr(v.expression ?: error("No expr tmpl"))).map(v)
    is KtEscapeStringTemplateEntry ->
      if (v.text.startsWith("\\u"))
        Node.Expr.StringTmpl.Elem.UnicodeEsc(v.text.substring(2)).map(v)
      else
        Node.Expr.StringTmpl.Elem.RegularEsc(v.unescapedValue.first()).map(v)
    else ->
      error("Unrecognized string template type for $v")
  }

  open fun convertStructured(v: KtClassOrObject) = Node.Decl.Structured(
    mods = convertModifiers(v),
    form = when (v) {
      is KtClass -> when {
        v.isEnum() -> Node.Decl.Structured.Form.ENUM_CLASS
        v.isInterface() -> Node.Decl.Structured.Form.INTERFACE
        else -> Node.Decl.Structured.Form.CLASS
      }
      is KtObjectDeclaration ->
        if (v.isCompanion()) Node.Decl.Structured.Form.COMPANION_OBJECT
        else Node.Decl.Structured.Form.OBJECT
      else -> error("Unknown type of $v")
    },
    name = v.name ?: error("Missing name"),
    typeParams = v.typeParameters.map(::convertTypeParam),
    primaryConstructor = v.primaryConstructor?.let(::convertPrimaryConstructor),
    // TODO: this
    parentAnns = emptyList(),
    parents = v.superTypeListEntries.map(::convertParent),
    typeConstraints = v.typeConstraints.map(::convertTypeConstraint),
    members = v.declarations.map(::convertDecl)
  ).map(v)

  open fun convertSuper(v: KtSuperExpression) = Node.Expr.Super(
    typeArg = v.superTypeQualifier?.let(::convertType),
    label = v.getLabelName()
  ).map(v)

  open fun convertThis(v: KtThisExpression) = Node.Expr.This(
    label = v.getLabelName()
  ).map(v)

  open fun convertThrow(v: KtThrowExpression) = Node.Expr.Throw(
    expr = convertExpr(v.thrownExpression ?: error("No throw expr for $v"))
  ).map(v)

  open fun convertTry(v: KtTryExpression) = Node.Expr.Try(
    block = convertBlock(v.tryBlock),
    catches = v.catchClauses.map(::convertTryCatch),
    finallyBlock = v.finallyBlock?.finalExpression?.let(::convertBlock)
  ).map(v)

  open fun convertTryCatch(v: KtCatchClause) = Node.Expr.Try.Catch(
    anns = v.catchParameter?.annotations?.map(::convertAnnotationSet) ?: emptyList(),
    varName = v.catchParameter?.name ?: error("No catch param name for $v"),
    varType = v.catchParameter?.typeReference?.
      let(::convertTypeRef) as? Node.TypeRef.Simple ?: error("Invalid catch param type for $v"),
    block = convertBlock(v.catchBody as? KtBlockExpression ?: error("No catch block for $v"))
  ).map(v)

  open fun convertType(v: KtTypeProjection) =
    v.typeReference?.let { convertType(it, v.modifierList) }

  open fun convertType(v: KtTypeReference, modifierList: KtModifierList?): Node.Type = Node.Type(
    mods = convertModifiers(modifierList),
    ref = convertTypeRef(v)
  ).map(v)

  open fun convertType(v: KtTypeReference): Node.Type = Node.Type(
    // Paren modifiers are inside the ref...
    mods = if (v.hasParentheses()) emptyList() else convertModifiers(v),
    ref = convertTypeRef(v)
  ).map(v)

  open fun convertTypeAlias(v: KtTypeAlias) = Node.Decl.TypeAlias(
    mods = convertModifiers(v),
    name = v.name ?: error("No type alias name for $v"),
    typeParams = v.typeParameters.map(::convertTypeParam),
    type = convertType(v.getTypeReference() ?: error("No type alias ref for $v"))
  ).map(v)

  open fun convertTypeConstraint(v: KtTypeConstraint) = Node.TypeConstraint(
    anns = v.children.mapNotNull {
      when (it) {
        is KtAnnotationEntry ->
          Node.Modifier.AnnotationSet(target = null, anns = listOf(convertAnnotation(it))).map(it)
        is KtAnnotation -> convertAnnotationSet(it)
        else -> null
      }
    },
    name = v.subjectTypeParameterName?.getReferencedName() ?: error("No type constraint name for $v"),
    type = convertType(v.boundTypeReference ?: error("No type constraint type for $v"))
  ).map(v)

  open fun convertTypeOp(v: KtBinaryExpressionWithTypeRHS) = Node.Expr.TypeOp(
    lhs = convertExpr(v.left),
    oper = v.operationReference.let {
      Node.Expr.TypeOp.Oper(typeTokensByText[it.text] ?: error("Unable to find op ref $it")).map(it)
    },
    rhs = convertType(v.right ?: error("No type op rhs for $v"))
  ).map(v)

  open fun convertTypeOp(v: KtIsExpression) = Node.Expr.TypeOp(
    lhs = convertExpr(v.leftHandSide),
    oper = v.operationReference.let {
      Node.Expr.TypeOp.Oper(typeTokensByText[it.text] ?: error("Unable to find op ref $it")).map(it)
    },
    rhs = convertType(v.typeReference ?: error("No type op rhs for $v"))
  )

  open fun convertTypeParam(v: KtTypeParameter) = Node.TypeParam(
    mods = convertModifiers(v),
    name = v.name ?: error("No type param name for $v"),
    type = v.extendsBound?.let(::convertTypeRef)
  ).map(v)

  open fun convertTypeParams(v: KtTypeArgumentList?) = v?.arguments?.map {
    if (it.projectionKind == KtProjectionKind.STAR) null
    else convertType(it)
  } ?: emptyList()

  open fun convertTypeRef(v: KtTypeReference) =
    convertTypeRef(v.typeElement ?: error("Missing typ elem")).let {
      if (!v.hasParentheses()) it else Node.TypeRef.Paren(
        mods = convertModifiers(v),
        type = it
      ).map(v)
    }

  open fun convertTypeRef(v: KtTypeElement): Node.TypeRef = when (v) {
    is KtFunctionType -> Node.TypeRef.Func(
      receiverType = v.receiverTypeReference?.let(::convertType),
      params = v.parameters.map {
        Node.TypeRef.Func.Param(
          name = it.name,
          type = convertType(it.typeReference ?: error("No param type"))
        ).map(it)
      },
      type = convertType(v.returnTypeReference ?: error("No return type"))
    ).map(v)
    is KtUserType -> Node.TypeRef.Simple(
      pieces = generateSequence(v) { it.qualifier }.toList().reversed().map {
        Node.TypeRef.Simple.Piece(
          name = it.referencedName ?: error("No type name for $it"),
          typeParams = convertTypeParams(it.typeArgumentList)
        ).map(it)
      }
    ).map(v)
    is KtNullableType -> Node.TypeRef.Nullable(
      // If there are modifiers or the inner type is a function, the type is a paren
      type = convertModifiers(v.modifierList).let { mods ->
        val innerType = convertTypeRef(v.innerType ?: error("No inner type for nullable"))
        if (v.innerType !is KtFunctionType && mods.isEmpty()) innerType
        else Node.TypeRef.Paren(mods, convertTypeRef(v.innerType!!))
      }
    ).map(v)
    is KtDynamicType -> Node.TypeRef.Dynamic().map(v)
    else -> error("Unrecognized type of $v")
  }

  open fun convertUnaryOp(v: KtUnaryExpression) = Node.Expr.UnaryOp(
    expr = convertExpr(v.baseExpression ?: error("No unary expr for $v")),
    oper = v.operationReference.let {
      Node.Expr.UnaryOp.Oper(unaryTokensByText[it.text] ?: error("Unable to find op ref $it")).map(it)
    },
    prefix = v is KtPrefixExpression
  ).map(v)

  open fun convertValueArg(v: KtValueArgument) = Node.ValueArg(
    name = v.getArgumentName()?.asName?.asString(),
    asterisk = v.getSpreadElement() != null,
    expr = convertExpr(v.getArgumentExpression() ?: error("No expr for value arg"))
  ).map(v)

  open fun convertValueArgs(v: KtValueArgumentList?) = v?.arguments?.map(::convertValueArg) ?: emptyList()

  open fun convertWhen(v: KtWhenExpression) = Node.Expr.When(
    expr = v.subjectExpression?.let(::convertExpr),
    entries = v.entries.map(::convertWhenEntry)
  ).map(v)

  open fun convertWhenCond(v: KtWhenCondition) = when (v) {
    is KtWhenConditionWithExpression -> Node.Expr.When.Cond.Expr(
      expr = convertExpr(v.expression ?: error("No when cond expr for $v"))
    ).map(v)
    is KtWhenConditionInRange -> Node.Expr.When.Cond.In(
      expr = convertExpr(v.rangeExpression ?: error("No when in expr for $v")),
      not = v.isNegated
    ).map(v)
    is KtWhenConditionIsPattern -> Node.Expr.When.Cond.Is(
      type = convertType(v.typeReference ?: error("No when is type for $v")),
      not = v.isNegated
    ).map(v)
    else -> error("Unrecognized when cond of $v")
  }

  open fun convertWhenEntry(v: KtWhenEntry) = Node.Expr.When.Entry(
    conds = v.conditions.map(::convertWhenCond),
    body = convertExpr(v.expression ?: error("No when entry body for $v"))
  ).map(v)

  open fun convertWhile(v: KtWhileExpressionBase) = Node.Expr.While(
    expr = convertExpr(v.condition ?: error("No while cond for $v")),
    body = convertExpr(v.body ?: error("No while body for $v")),
    doWhile = v is KtDoWhileExpression
  ).map(v)

  protected open fun <T: Node> T.map(v: PsiElement) = also { onNode(it, v) }

  class Unsupported(message: String) : UnsupportedOperationException(message)

  open class WithExtras : Converter(), ExtrasMap {
    // Sometimes many nodes are created from the same element, but we only want the last node we're given. We
    // remove the previous nodes we've found for the same identity when we see a new one. So we don't have to
    // keep PSI elems around, we hold a map to the element's identity hash code. Then we use that number to tie
    // to the extras to keep duplicates out. Usually using identity hash codes would be problematic due to
    // potential reuse, we know the PSI objects are all around at the same time so it's good enough.
    protected val nodesToPsiIdentities = IdentityHashMap<Node, Int>()
    protected val psiIdentitiesToNodes = mutableMapOf<Int, Node>()
    protected val extrasBefore = mutableMapOf<Int, List<Node.Extra>>()
    protected val extrasWithin = mutableMapOf<Int, List<Node.Extra>>()
    protected val extrasAfter = mutableMapOf<Int, List<Node.Extra>>()
    // This keeps track of ws nodes we've seen before so we don't duplicate them
    protected val seenExtraPsiIdentities = mutableSetOf<Int>()

    override fun extrasBefore(v: Node) = nodesToPsiIdentities[v]?.let { extrasBefore[it] } ?: emptyList()
    override fun extrasWithin(v: Node) = nodesToPsiIdentities[v]?.let { extrasWithin[it] } ?: emptyList()
    override fun extrasAfter(v: Node) = nodesToPsiIdentities[v]?.let { extrasAfter[it] } ?: emptyList()

    internal val allExtrasBefore get() = extrasBefore
    internal val allExtrasAfter get() = extrasAfter

    override fun onNode(node: Node, elem: PsiElement) {
      // We ignore whitespace and comments here to prevent recursion
      if (elem is PsiWhiteSpace || elem is PsiComment) return
      // If we've done this elem before, just set this node as the curr and move on
      val elemId = System.identityHashCode(elem)
      nodesToPsiIdentities[node] = elemId
      psiIdentitiesToNodes.put(elemId, node)?.also { prevNode ->
        nodesToPsiIdentities.remove(prevNode)
        return
      }
      // Since we've never done this element before, grab its extras and persist
      val (beforeElems, withinElems, afterElems) = nodeExtraElems(elem)
      convertExtras(beforeElems).map {
        // As a special case, we make sure all non-block comments start a line when "before"
        if (it is Node.Extra.Comment && !it.startsLine && it.text.startsWith("//")) it.copy(startsLine = true)
        else it
      }.also { if (it.isNotEmpty()) extrasBefore[elemId] = it }
      convertExtras(withinElems).also { if (it.isNotEmpty()) extrasWithin[elemId] = it }
      convertExtras(afterElems).also { if (it.isNotEmpty()) extrasAfter[elemId] = it }
    }

    open fun nodeExtraElems(elem: PsiElement): Triple<List<PsiElement>, List<PsiElement>, List<PsiElement>> {
      val before = mutableListOf<PsiElement>()
      var within = mutableListOf<PsiElement>()
      var after = mutableListOf<PsiElement>()

      // Before starts with all directly above ws/comments (reversed to be top-down)
      before += elem.siblings(forward = false, withItself = false).takeWhile {
        it is PsiWhiteSpace || it is PsiComment
      }.toList().reversed()

      // Go over every child...
      var seenInvalid = false
      elem.allChildren.forEach { child ->
        if (child is PsiWhiteSpace || child is PsiComment) {
          // If it's a ws/comment before anything else, it's a before
          if (!seenInvalid) before += child else {
            // Otherwise it's within or after
            within.add(child)
            after.add(child)
          }
        } else {
          seenInvalid = true
          // Clear after since we've seen a non-ws node
          after.clear()
        }
      }
      // Within needs to have the after vals trimmed
      within = within.subList(0, within.size - after.size)

      // After includes all siblings before the first newline or all if there are only ws/comment siblings
      var indexOfFirstNewline = -1
      var seenNonWs = false
      elem.siblings(forward = true, withItself = false).forEach {
        if (it !is PsiWhiteSpace && it !is PsiComment) seenNonWs = true
        else if (!seenNonWs) {
          if (indexOfFirstNewline == -1 && it is PsiWhiteSpace && it.textContains('\n'))
            indexOfFirstNewline = after.size
          after.add(it)
        }
      }
      if (seenNonWs && indexOfFirstNewline != -1) after = after.subList(0, indexOfFirstNewline)

      return Triple(before, within, after)
    }

    open fun convertExtras(elems: List<PsiElement>): List<Node.Extra> = elems.mapNotNull { elem ->
      // Ignore elems we've done before
      val elemId = System.identityHashCode(elem)
      if (!seenExtraPsiIdentities.add(elemId)) null else when (elem) {
        is PsiWhiteSpace -> elem.text.count { it == '\n' }.let { newlineCount ->
          if (newlineCount > 1) Node.Extra.BlankLines(newlineCount - 1).map(elem) else null
        }
        is PsiComment -> Node.Extra.Comment(
          text = elem.text,
          startsLine = ((elem.prevSibling ?: elem.prevLeaf()) as? PsiWhiteSpace)?.textContains('\n') == true,
          endsLine = elem.tokenType == KtTokens.EOL_COMMENT ||
            ((elem.nextSibling ?: elem.nextLeaf()) as? PsiWhiteSpace)?.textContains('\n') == true
        ).map(elem)
        else -> null
      }
    }
  }

  companion object : Converter() {
    internal val modifiersByText = Node.Modifier.Keyword.values().map { it.name.toLowerCase() to it }.toMap()
    internal val binaryTokensByText = Node.Expr.BinaryOp.Token.values().map { it.str to it }.toMap()
    internal val unaryTokensByText = Node.Expr.UnaryOp.Token.values().map { it.str to it }.toMap()
    internal val typeTokensByText = Node.Expr.TypeOp.Token.values().map { it.str to it }.toMap()

    internal val KtTypeReference.names get() = (typeElement as? KtUserType)?.names ?: emptyList()
    internal val KtUserType.names get(): List<String> =
      referencedName?.let { (qualifier?.names ?: emptyList()) + it } ?: emptyList()
    internal val KtExpression?.block get() = (this as? KtBlockExpression)?.statements ?: emptyList()
    internal val KtDoubleColonExpression.questionMarks get() =
      generateSequence(node.firstChildNode, ASTNode::getTreeNext).
        takeWhile { it.elementType != KtTokens.COLONCOLON }.
        count { it.elementType == KtTokens.QUEST }
  }
}
