package arrow.meta.qq

import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

abstract class MetaTreeVisitor : KtTreeVisitorVoid()

//TODO stubs declarations for all the kt elements that can be quoted
//fun MetaComponentRegistrar.Class(match: KtClass.() -> Boolean, map: Class.ClassScope.(KtClass) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Class.Companion, match, map)
//
//fun MetaComponentRegistrar.ClassOrObject(match: KtClassOrObject.() -> Boolean, map: ClassOrObject.ClassOrObjectScope.(KtClassOrObject) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ClassOrObject.Companion, match, map)
//
//fun MetaComponentRegistrar.SecondaryConstructor(match: KtSecondaryConstructor.() -> Boolean, map: SecondaryConstructor.SecondaryConstructorScope.(KtSecondaryConstructor) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SecondaryConstructor.Companion, match, map)
//
//fun MetaComponentRegistrar.PrimaryConstructor(match: KtPrimaryConstructor.() -> Boolean, map: PrimaryConstructor.PrimaryConstructorScope.(KtPrimaryConstructor) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PrimaryConstructor.Companion, match, map)
//
//fun MetaComponentRegistrar.NamedFunction(match: KtNamedFunction.() -> Boolean, map: NamedFunction.NamedFunctionScope.(KtNamedFunction) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(NamedFunction.Companion, match, map)
//
//fun MetaComponentRegistrar.Property(match: KtProperty.() -> Boolean, map: Property.PropertyScope.(KtProperty) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Property.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeAlias(match: KtTypeAlias.() -> Boolean, map: TypeAlias.TypeAliasScope.(KtTypeAlias) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeAlias.Companion, match, map)
//
//fun MetaComponentRegistrar.DestructuringDeclaration(match: KtDestructuringDeclaration.() -> Boolean, map: DestructuringDeclaration.DestructuringDeclarationScope.(KtDestructuringDeclaration) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DestructuringDeclaration.Companion, match, map)
//
//fun MetaComponentRegistrar.DestructuringDeclarationEntry(match: KtDestructuringDeclarationEntry.() -> Boolean, map: DestructuringDeclarationEntry.DestructuringDeclarationEntryScope.(KtDestructuringDeclarationEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DestructuringDeclarationEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.KtFile(match: KtFile.() -> Boolean, map: KtFile.KtFileScope.(KtKtFile) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(KtFile.Companion, match, map)
//
//fun MetaComponentRegistrar.Script(match: Script.() -> Boolean, map: Script.ScriptScope.(KtScript) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Script.Companion, match, map)
//
//fun MetaComponentRegistrar.ImportAlias(match: KtImportAlias.() -> Boolean, map: ImportAlias.ImportAliasScope.(KtImportAlias) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ImportAlias.Companion, match, map)
//
//fun MetaComponentRegistrar.ImportDirective(match: KtImportDirective.() -> Boolean, map: ImportDirective.ImportDirectiveScope.(KtImportDirective) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ImportDirective.Companion, match, map)
//
//fun MetaComponentRegistrar.ImportList(match: KtImportList.() -> Boolean, map: ImportList.ImportListScope.(KtImportList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ImportList.Companion, match, map)
//
//fun MetaComponentRegistrar.ClassBody(match: KtClassBody.() -> Boolean, map: ClassBody.ClassBodyScope.(KtClassBody) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ClassBody.Companion, match, map)
//
//fun MetaComponentRegistrar.ModifierList(match: KtModifierList.() -> Boolean, map: ModifierList.ModifierListScope.(KtModifierList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ModifierList.Companion, match, map)
//
//fun MetaComponentRegistrar.Annotation(match: KtAnnotation.() -> Boolean, map: Annotation.AnnotationScope.(KtAnnotation) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Annotation.Companion, match, map)
//
//fun MetaComponentRegistrar.AnnotationEntry(match: KtAnnotationEntry.() -> Boolean, map: AnnotationEntry.AnnotationEntryScope.(KtAnnotationEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(AnnotationEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.ConstructorCalleeExpression(match: KtConstructorCalleeExpression.() -> Boolean, map: ConstructorCalleeExpression.ConstructorCalleeExpressionScope.(KtConstructorCalleeExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ConstructorCalleeExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeParameterList(match: KtTypeParameterList.() -> Boolean, map: TypeParameterList.TypeParameterListScope.(KtTypeParameterList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeParameterList.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeParameter(match: KtTypeParameter.() -> Boolean, map: TypeParameter.TypeParameterScope.(KtTypeParameter) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeParameter.Companion, match, map)
//
//fun MetaComponentRegistrar.EnumEntry(match: KtEnumEntry.() -> Boolean, map: EnumEntry.EnumEntryScope.(KtEnumEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(EnumEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.ParameterList(match: KtParameterList.() -> Boolean, map: ParameterList.ParameterListScope.(KtParameterList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ParameterList.Companion, match, map)
//
//fun MetaComponentRegistrar.Parameter(match: KtParameter.() -> Boolean, map: Parameter.ParameterScope.(KtParameter) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Parameter.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperTypeList(match: KtSuperTypeList.() -> Boolean, map: SuperTypeList.SuperTypeListScope.(KtSuperTypeList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperTypeList.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperTypeListEntry(match: KtSuperTypeListEntry.() -> Boolean, map: SuperTypeListEntry.SuperTypeListEntryScope.(KtSuperTypeListEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperTypeListEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.DelegatedSuperTypeEntry(match: KtDelegatedSuperTypeEntry.() -> Boolean, map: DelegatedSuperTypeEntry.DelegatedSuperTypeEntryScope.(KtDelegatedSuperTypeEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DelegatedSuperTypeEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperTypeCallEntry(match: KtSuperTypeCallEntry.() -> Boolean, map: SuperTypeCallEntry.SuperTypeCallEntryScope.(KtSuperTypeCallEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperTypeCallEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperTypeEntry(match: KtSuperTypeEntry.() -> Boolean, map: SuperTypeEntry.SuperTypeEntryScope.(KtSuperTypeEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperTypeEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.ConstructorDelegationCall(match: KtConstructorDelegationCall.() -> Boolean, map: ConstructorDelegationCall.ConstructorDelegationCallScope.(KtConstructorDelegationCall) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ConstructorDelegationCall.Companion, match, map)
//
//fun MetaComponentRegistrar.PropertyDelegate(match: KtPropertyDelegate.() -> Boolean, map: PropertyDelegate.PropertyDelegateScope.(KtPropertyDelegate) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PropertyDelegate.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeReference(match: KtTypeReference.() -> Boolean, map: TypeReference.TypeReferenceScope.(KtTypeReference) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeReference.Companion, match, map)
//
//fun MetaComponentRegistrar.ValueArgumentList(match: KtValueArgumentList.() -> Boolean, map: ValueArgumentList.ValueArgumentListScope.(KtValueArgumentList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ValueArgumentList.Companion, match, map)
//
//fun MetaComponentRegistrar.ValueArgument(match: KtValueArgument.() -> Boolean, map: Argument.ArgumentScope.(KtValueArgument) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Argument.Companion, match, map)
//
//fun MetaComponentRegistrar.Expression(match: KtExpression.() -> Boolean, map: Expression.ExpressionScope.(KtExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Expression.Companion, match, map)
//
//fun MetaComponentRegistrar.LoopExpression(match: KtLoopExpression.() -> Boolean, map: LoopExpression.LoopExpressionScope.(KtLoopExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(LoopExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ConstantExpression(match: KtConstantExpression.() -> Boolean, map: ConstantExpression.ConstantExpressionScope.(KtConstantExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ConstantExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.SimpleNameExpression(match: KtSimpleNameExpression.() -> Boolean, map: SimpleNameExpression.SimpleNameExpressionScope.(KtSimpleNameExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SimpleNameExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ReferenceExpression(match: KtReferenceExpression.() -> Boolean, map: ReferenceExpression.ReferenceExpressionScope.(KtReferenceExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ReferenceExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.LabeledExpression(match: KtLabeledExpression.() -> Boolean, map: LabeledExpression.LabeledExpressionScope.(KtLabeledExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(LabeledExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.PrefixExpression(match: KtPrefixExpression.() -> Boolean, map: PrefixExpression.PrefixExpressionScope.(KtPrefixExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PrefixExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.PostfixExpression(match: KtPostfixExpression.() -> Boolean, map: PostfixExpression.PostfixExpressionScope.(KtPostfixExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PostfixExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.UnaryExpression(match: KtUnaryExpression.() -> Boolean, map: UnaryExpression.UnaryExpressionScope.(KtUnaryExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(UnaryExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.BinaryExpression(match: KtBinaryExpression.() -> Boolean, map: BinaryExpression.BinaryExpressionScope.(KtBinaryExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BinaryExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ReturnExpression(match: KtReturnExpression.() -> Boolean, map: ReturnExpression.ReturnExpressionScope.(KtReturnExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ReturnExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ExpressionWithLabel(match: KtExpressionWithLabel.() -> Boolean, map: ExpressionWithLabel.ExpressionWithLabelScope.(KtExpressionWithLabel) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ExpressionWithLabel.Companion, match, map)
//
//fun MetaComponentRegistrar.ThrowExpression(match: KtThrowExpression.() -> Boolean, map: ThrowExpression.ThrowExpressionScope.(KtThrowExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ThrowExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.BreakExpression(match: KtBreakExpression.() -> Boolean, map: BreakExpression.BreakExpressionScope.(KtBreakExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BreakExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ContinueExpression(match: KtContinueExpression.() -> Boolean, map: ContinueExpression.ContinueExpressionScope.(KtContinueExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ContinueExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.IfExpression(match: KtIfExpression.() -> Boolean, map: IfExpression.IfExpressionScope.(KtIfExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(IfExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenExpression(match: KtWhenExpression.() -> Boolean, map: WhenExpression.WhenExpressionScope.(KtWhenExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.CollectionLiteralExpression(match: KtCollectionLiteralExpression.() -> Boolean, map: CollectionLiteralExpression.CollectionLiteralExpressionScope.(KtCollectionLiteralExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(CollectionLiteralExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.TryExpression(match: KtTryExpression.() -> Boolean, map: TryExpression.TryExpressionScope.(KtTryExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TryExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ForExpression(match: KtForExpression.() -> Boolean, map: ForExpression.ForExpressionScope.(KtForExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ForExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.WhileExpression(match: KtWhileExpression.() -> Boolean, map: WhileExpression.WhileExpressionScope.(KtWhileExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhileExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.DoWhileExpression(match: KtDoWhileExpression.() -> Boolean, map: DoWhileExpression.DoWhileExpressionScope.(KtDoWhileExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DoWhileExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.LambdaExpression(match: KtLambdaExpression.() -> Boolean, map: LambdaExpression.LambdaExpressionScope.(KtLambdaExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(LambdaExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.AnnotatedExpression(match: KtAnnotatedExpression.() -> Boolean, map: AnnotatedExpression.AnnotatedExpressionScope.(KtAnnotatedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(AnnotatedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.CallExpression(match: KtCallExpression.() -> Boolean, map: CallExpression.CallExpressionScope.(KtCallExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(CallExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ArrayAccessExpression(match: KtArrayAccessExpression.() -> Boolean, map: ArrayAccessExpression.ArrayAccessExpressionScope.(KtArrayAccessExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ArrayAccessExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.QualifiedExpression(match: KtQualifiedExpression.() -> Boolean, map: QualifiedExpression.QualifiedExpressionScope.(KtQualifiedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(QualifiedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.DoubleColonExpression(match: KtDoubleColonExpression.() -> Boolean, map: DoubleColonExpression.DoubleColonExpressionScope.(KtDoubleColonExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DoubleColonExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.CallableReferenceExpression(match: KtCallableReferenceExpression.() -> Boolean, map: CallableReferenceExpression.CallableReferenceExpressionScope.(KtCallableReferenceExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(CallableReferenceExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ClassLiteralExpression(match: KtClassLiteralExpression.() -> Boolean, map: ClassLiteralExpression.ClassLiteralExpressionScope.(KtClassLiteralExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ClassLiteralExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.DotQualifiedExpression(match: KtDotQualifiedExpression.() -> Boolean, map: DotQualifiedExpression.DotQualifiedExpressionScope.(KtDotQualifiedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DotQualifiedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.SafeQualifiedExpression(match: KtSafeQualifiedExpression.() -> Boolean, map: SafeQualifiedExpression.SafeQualifiedExpressionScope.(KtSafeQualifiedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SafeQualifiedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ObjectLiteralExpression(match: KtObjectLiteralExpression.() -> Boolean, map: ObjectLiteralExpression.ObjectLiteralExpressionScope.(KtObjectLiteralExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ObjectLiteralExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.BlockExpression(match: KtBlockExpression.() -> Boolean, map: BlockExpression.BlockExpressionScope.(KtBlockExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BlockExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.CatchSection(match: KtCatchSection.() -> Boolean, map: CatchSection.CatchSectionScope.(KtCatchSection) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(CatchSection.Companion, match, map)
//
//fun MetaComponentRegistrar.FinallySection(match: KtFinallySection.() -> Boolean, map: FinallySection.FinallySectionScope.(KtFinallySection) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(FinallySection.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeArgumentList(match: KtTypeArgumentList.() -> Boolean, map: TypeArgumentList.TypeArgumentListScope.(KtTypeArgumentList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeArgumentList.Companion, match, map)
//
//fun MetaComponentRegistrar.ThisExpression(match: KtThisExpression.() -> Boolean, map: ThisExpression.ThisExpressionScope.(KtThisExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ThisExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperExpression(match: KtSuperExpression.() -> Boolean, map: SuperExpression.SuperExpressionScope.(KtSuperExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ParenthesizedExpression(match: KtParenthesizedExpression.() -> Boolean, map: ParenthesizedExpression.ParenthesizedExpressionScope.(KtParenthesizedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ParenthesizedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.InitializerList(match: KtInitializerList.() -> Boolean, map: InitializerList.InitializerListScope.(KtInitializerList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(InitializerList.Companion, match, map)
//
//fun MetaComponentRegistrar.AnonymousInitializer(match: KtAnonymousInitializer.() -> Boolean, map: AnonymousInitializer.AnonymousInitializerScope.(KtAnonymousInitializer) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(AnonymousInitializer.Companion, match, map)
//
//fun MetaComponentRegistrar.ScriptInitializer(match: KtScriptInitializer.() -> Boolean, map: ScriptInitializer.ScriptInitializerScope.(KtScriptInitializer) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ScriptInitializer.Companion, match, map)
//
//fun MetaComponentRegistrar.ClassInitializer(match: KtClassInitializer.() -> Boolean, map: ClassInitializer.ClassInitializerScope.(KtClassInitializer) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ClassInitializer.Companion, match, map)
//
//fun MetaComponentRegistrar.PropertyAccessor(match: KtPropertyAccessor.() -> Boolean, map: PropertyAccessor.PropertyAccessorScope.(KtPropertyAccessor) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PropertyAccessor.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeConstraintList(match: KtTypeConstraintList.() -> Boolean, map: TypeConstraintList.TypeConstraintListScope.(KtTypeConstraintList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeConstraintList.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeConstraint(match: KtTypeConstraint.() -> Boolean, map: TypeConstraint.TypeConstraintScope.(KtTypeConstraint) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeConstraint.Companion, match, map)
//
//fun MetaComponentRegistrar.UserType(match: KtUserType.() -> Boolean, map: UserType.UserTypeScope.(KtUserType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(UserType.Companion, match, map)
//
//fun MetaComponentRegistrar.DynamicType(match: KtDynamicType.() -> Boolean, map: DynamicType.DynamicTypeScope.(KtDynamicType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DynamicType.Companion, match, map)
//
//fun MetaComponentRegistrar.FunctionType(match: KtFunctionType.() -> Boolean, map: FunctionType.FunctionTypeScope.(KtFunctionType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(FunctionType.Companion, match, map)
//
//fun MetaComponentRegistrar.SelfType(match: KtSelfType.() -> Boolean, map: SelfType.SelfTypeScope.(KtSelfType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SelfType.Companion, match, map)
//
//fun MetaComponentRegistrar.BinaryWithTypeRHSExpression(match: KtBinaryWithTypeRHSExpression.() -> Boolean, map: BinaryWithTypeRHSExpression.BinaryWithTypeRHSExpressionScope.(KtBinaryWithTypeRHSExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BinaryWithTypeRHSExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.StringTemplateExpression(match: KtStringTemplateExpression.() -> Boolean, map: StringTemplateExpression.StringTemplateExpressionScope.(KtStringTemplateExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(StringTemplateExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.NamedDeclaration(match: KtNamedDeclaration.() -> Boolean, map: NamedDeclaration.NamedDeclarationScope.(KtNamedDeclaration) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(NamedDeclaration.Companion, match, map)
//
//fun MetaComponentRegistrar.NullableType(match: KtNullableType.() -> Boolean, map: NullableType.NullableTypeScope.(KtNullableType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(NullableType.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeProjection(match: KtTypeProjection.() -> Boolean, map: TypeProjection.TypeProjectionScope.(KtTypeProjection) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeProjection.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenEntry(match: KtWhenEntry.() -> Boolean, map: WhenEntry.WhenEntryScope.(KtWhenEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.IsExpression(match: KtIsExpression.() -> Boolean, map: IsExpression.IsExpressionScope.(KtIsExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(IsExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenConditionIsPattern(match: KtWhenConditionIsPattern.() -> Boolean, map: WhenConditionIsPattern.WhenConditionIsPatternScope.(KtWhenConditionIsPattern) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenConditionIsPattern.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenConditionInRange(match: KtWhenConditionInRange.() -> Boolean, map: WhenConditionInRange.WhenConditionInRangeScope.(KtWhenConditionInRange) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenConditionInRange.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenConditionWithExpression(match: KtWhenConditionWithExpression.() -> Boolean, map: WhenConditionWithExpression.WhenConditionWithExpressionScope.(KtWhenConditionWithExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenConditionWithExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ObjectDeclaration(match: KtObjectDeclaration.() -> Boolean, map: ObjectDeclaration.ObjectDeclarationScope.(KtObjectDeclaration) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ObjectDeclaration.Companion, match, map)
//
//fun MetaComponentRegistrar.StringTemplateEntry(match: KtStringTemplateEntry.() -> Boolean, map: StringTemplateEntry.StringTemplateEntryScope.(KtStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(StringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.StringTemplateEntryWithExpression(match: KtStringTemplateEntryWithExpression.() -> Boolean, map: StringTemplateEntryWithExpression.StringTemplateEntryWithExpressionScope.(KtStringTemplateEntryWithExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(StringTemplateEntryWithExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.BlockStringTemplateEntry(match: KtBlockStringTemplateEntry.() -> Boolean, map: BlockStringTemplateEntry.BlockStringTemplateEntryScope.(KtBlockStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BlockStringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.SimpleNameStringTemplateEntry(match: KtSimpleNameStringTemplateEntry.() -> Boolean, map: SimpleNameStringTemplateEntry.SimpleNameStringTemplateEntryScope.(KtSimpleNameStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SimpleNameStringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.LiteralStringTemplateEntry(match: KtLiteralStringTemplateEntry.() -> Boolean, map: LiteralStringTemplateEntry.LiteralStringTemplateEntryScope.(KtLiteralStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(LiteralStringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.EscapeStringTemplateEntry(match: KtEscapeStringTemplateEntry.() -> Boolean, map: EscapeStringTemplateEntry.EscapeStringTemplateEntryScope.(KtEscapeStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(EscapeStringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.PackageDirective(match: KtPackageDirective.() -> Boolean, map: PackageDirective.PackageDirectiveScope.(KtPackageDirective) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PackageDirective.Companion, match, map)
//
//fun MetaComponentRegistrar.KtElement(match: KtKtElement.() -> Boolean, map: KtElement.KtElementScope.(KtKtElement) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(KtElement.Companion, match, map)
//
//fun MetaComponentRegistrar.Declaration(match: KtDeclaration.() -> Boolean, map: Declaration.DeclarationScope.(KtDeclaration) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Declaration.Companion, match, map)
//
//fun MetaComponentRegistrar.Class(match: KtClass.() -> Boolean, map: Class.ClassScope.(KtClass) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Class.Companion, match, map)
//
//fun MetaComponentRegistrar.ClassOrObject(match: KtClassOrObject.() -> Boolean, map: ClassOrObject.ClassOrObjectScope.(KtClassOrObject) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ClassOrObject.Companion, match, map)
//
//fun MetaComponentRegistrar.SecondaryConstructor(match: KtSecondaryConstructor.() -> Boolean, map: SecondaryConstructor.SecondaryConstructorScope.(KtSecondaryConstructor) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SecondaryConstructor.Companion, match, map)
//
//fun MetaComponentRegistrar.PrimaryConstructor(match: KtPrimaryConstructor.() -> Boolean, map: PrimaryConstructor.PrimaryConstructorScope.(KtPrimaryConstructor) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PrimaryConstructor.Companion, match, map)
//
//fun MetaComponentRegistrar.NamedFunction(match: KtNamedFunction.() -> Boolean, map: NamedFunction.NamedFunctionScope.(KtNamedFunction) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(NamedFunction.Companion, match, map)
//
//fun MetaComponentRegistrar.Property(match: KtProperty.() -> Boolean, map: Property.PropertyScope.(KtProperty) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Property.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeAlias(match: KtTypeAlias.() -> Boolean, map: TypeAlias.TypeAliasScope.(KtTypeAlias) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeAlias.Companion, match, map)
//
//fun MetaComponentRegistrar.DestructuringDeclaration(match: KtDestructuringDeclaration.() -> Boolean, map: DestructuringDeclaration.DestructuringDeclarationScope.(KtDestructuringDeclaration) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DestructuringDeclaration.Companion, match, map)
//
//fun MetaComponentRegistrar.DestructuringDeclarationEntry(match: KtDestructuringDeclarationEntry.() -> Boolean, map: DestructuringDeclarationEntry.DestructuringDeclarationEntryScope.(KtDestructuringDeclarationEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DestructuringDeclarationEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.KtFile(match: KtKtFile.() -> Boolean, map: KtFile.KtFileScope.(KtKtFile) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(KtFile.Companion, match, map)
//
//fun MetaComponentRegistrar.Script(match: KtScript.() -> Boolean, map: Script.ScriptScope.(KtScript) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Script.Companion, match, map)
//
//fun MetaComponentRegistrar.ImportDirective(match: KtImportDirective.() -> Boolean, map: ImportDirective.ImportDirectiveScope.(KtImportDirective) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ImportDirective.Companion, match, map)
//
//fun MetaComponentRegistrar.ImportList(match: KtImportList.() -> Boolean, map: ImportList.ImportListScope.(KtImportList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ImportList.Companion, match, map)
//
//fun MetaComponentRegistrar.ClassBody(match: KtClassBody.() -> Boolean, map: ClassBody.ClassBodyScope.(KtClassBody) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ClassBody.Companion, match, map)
//
//fun MetaComponentRegistrar.ModifierList(match: KtModifierList.() -> Boolean, map: ModifierList.ModifierListScope.(KtModifierList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ModifierList.Companion, match, map)
//
//fun MetaComponentRegistrar.Annotation(match: KtAnnotation.() -> Boolean, map: Annotation.AnnotationScope.(KtAnnotation) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Annotation.Companion, match, map)
//
//fun MetaComponentRegistrar.AnnotationEntry(match: KtAnnotationEntry.() -> Boolean, map: AnnotationEntry.AnnotationEntryScope.(KtAnnotationEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(AnnotationEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.ConstructorCalleeExpression(match: KtConstructorCalleeExpression.() -> Boolean, map: ConstructorCalleeExpression.ConstructorCalleeExpressionScope.(KtConstructorCalleeExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ConstructorCalleeExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeParameterList(match: KtTypeParameterList.() -> Boolean, map: TypeParameterList.TypeParameterListScope.(KtTypeParameterList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeParameterList.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeParameter(match: KtTypeParameter.() -> Boolean, map: TypeParameter.TypeParameterScope.(KtTypeParameter) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeParameter.Companion, match, map)
//
//fun MetaComponentRegistrar.EnumEntry(match: KtEnumEntry.() -> Boolean, map: EnumEntry.EnumEntryScope.(KtEnumEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(EnumEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.ParameterList(match: KtParameterList.() -> Boolean, map: ParameterList.ParameterListScope.(KtParameterList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ParameterList.Companion, match, map)
//
//fun MetaComponentRegistrar.Parameter(match: KtParameter.() -> Boolean, map: Parameter.ParameterScope.(KtParameter) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Parameter.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperTypeList(match: KtSuperTypeList.() -> Boolean, map: SuperTypeList.SuperTypeListScope.(KtSuperTypeList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperTypeList.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperTypeListEntry(match: KtSuperTypeListEntry.() -> Boolean, map: SuperTypeListEntry.SuperTypeListEntryScope.(KtSuperTypeListEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperTypeListEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.DelegatedSuperTypeEntry(match: KtDelegatedSuperTypeEntry.() -> Boolean, map: DelegatedSuperTypeEntry.DelegatedSuperTypeEntryScope.(KtDelegatedSuperTypeEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DelegatedSuperTypeEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperTypeCallEntry(match: KtSuperTypeCallEntry.() -> Boolean, map: SuperTypeCallEntry.SuperTypeCallEntryScope.(KtSuperTypeCallEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperTypeCallEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperTypeEntry(match: KtSuperTypeEntry.() -> Boolean, map: SuperTypeEntry.SuperTypeEntryScope.(KtSuperTypeEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperTypeEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.ConstructorDelegationCall(match: KtConstructorDelegationCall.() -> Boolean, map: ConstructorDelegationCall.ConstructorDelegationCallScope.(KtConstructorDelegationCall) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ConstructorDelegationCall.Companion, match, map)
//
//fun MetaComponentRegistrar.PropertyDelegate(match: KtPropertyDelegate.() -> Boolean, map: PropertyDelegate.PropertyDelegateScope.(KtPropertyDelegate) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PropertyDelegate.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeReference(match: KtTypeReference.() -> Boolean, map: TypeReference.TypeReferenceScope.(KtTypeReference) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeReference.Companion, match, map)
//
//fun MetaComponentRegistrar.ValueArgumentList(match: KtValueArgumentList.() -> Boolean, map: ValueArgumentList.ValueArgumentListScope.(KtValueArgumentList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ValueArgumentList.Companion, match, map)
//
//fun MetaComponentRegistrar.Argument(match: KtArgument.() -> Boolean, map: Argument.ArgumentScope.(KtArgument) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Argument.Companion, match, map)
//
//fun MetaComponentRegistrar.Expression(match: KtExpression.() -> Boolean, map: Expression.ExpressionScope.(KtExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(Expression.Companion, match, map)
//
//fun MetaComponentRegistrar.LoopExpression(match: KtLoopExpression.() -> Boolean, map: LoopExpression.LoopExpressionScope.(KtLoopExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(LoopExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ConstantExpression(match: KtConstantExpression.() -> Boolean, map: ConstantExpression.ConstantExpressionScope.(KtConstantExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ConstantExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.SimpleNameExpression(match: KtSimpleNameExpression.() -> Boolean, map: SimpleNameExpression.SimpleNameExpressionScope.(KtSimpleNameExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SimpleNameExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ReferenceExpression(match: KtReferenceExpression.() -> Boolean, map: ReferenceExpression.ReferenceExpressionScope.(KtReferenceExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ReferenceExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.LabeledExpression(match: KtLabeledExpression.() -> Boolean, map: LabeledExpression.LabeledExpressionScope.(KtLabeledExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(LabeledExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.PrefixExpression(match: KtPrefixExpression.() -> Boolean, map: PrefixExpression.PrefixExpressionScope.(KtPrefixExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PrefixExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.PostfixExpression(match: KtPostfixExpression.() -> Boolean, map: PostfixExpression.PostfixExpressionScope.(KtPostfixExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PostfixExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.UnaryExpression(match: KtUnaryExpression.() -> Boolean, map: UnaryExpression.UnaryExpressionScope.(KtUnaryExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(UnaryExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.BinaryExpression(match: KtBinaryExpression.() -> Boolean, map: BinaryExpression.BinaryExpressionScope.(KtBinaryExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BinaryExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ReturnExpression(match: KtReturnExpression.() -> Boolean, map: ReturnExpression.ReturnExpressionScope.(KtReturnExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ReturnExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ExpressionWithLabel(match: KtExpressionWithLabel.() -> Boolean, map: ExpressionWithLabel.ExpressionWithLabelScope.(KtExpressionWithLabel) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ExpressionWithLabel.Companion, match, map)
//
//fun MetaComponentRegistrar.ThrowExpression(match: KtThrowExpression.() -> Boolean, map: ThrowExpression.ThrowExpressionScope.(KtThrowExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ThrowExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.BreakExpression(match: KtBreakExpression.() -> Boolean, map: BreakExpression.BreakExpressionScope.(KtBreakExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BreakExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ContinueExpression(match: KtContinueExpression.() -> Boolean, map: ContinueExpression.ContinueExpressionScope.(KtContinueExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ContinueExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.IfExpression(match: KtIfExpression.() -> Boolean, map: IfExpression.IfExpressionScope.(KtIfExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(IfExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenExpression(match: KtWhenExpression.() -> Boolean, map: WhenExpression.WhenExpressionScope.(KtWhenExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.TryExpression(match: KtTryExpression.() -> Boolean, map: TryExpression.TryExpressionScope.(KtTryExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TryExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ForExpression(match: KtForExpression.() -> Boolean, map: ForExpression.ForExpressionScope.(KtForExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ForExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.WhileExpression(match: KtWhileExpression.() -> Boolean, map: WhileExpression.WhileExpressionScope.(KtWhileExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhileExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.DoWhileExpression(match: KtDoWhileExpression.() -> Boolean, map: DoWhileExpression.DoWhileExpressionScope.(KtDoWhileExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DoWhileExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.LambdaExpression(match: KtLambdaExpression.() -> Boolean, map: LambdaExpression.LambdaExpressionScope.(KtLambdaExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(LambdaExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.AnnotatedExpression(match: KtAnnotatedExpression.() -> Boolean, map: AnnotatedExpression.AnnotatedExpressionScope.(KtAnnotatedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(AnnotatedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.CallExpression(match: KtCallExpression.() -> Boolean, map: CallExpression.CallExpressionScope.(KtCallExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(CallExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ArrayAccessExpression(match: KtArrayAccessExpression.() -> Boolean, map: ArrayAccessExpression.ArrayAccessExpressionScope.(KtArrayAccessExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ArrayAccessExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.QualifiedExpression(match: KtQualifiedExpression.() -> Boolean, map: QualifiedExpression.QualifiedExpressionScope.(KtQualifiedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(QualifiedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.DoubleColonExpression(match: KtDoubleColonExpression.() -> Boolean, map: DoubleColonExpression.DoubleColonExpressionScope.(KtDoubleColonExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DoubleColonExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.CallableReferenceExpression(match: KtCallableReferenceExpression.() -> Boolean, map: CallableReferenceExpression.CallableReferenceExpressionScope.(KtCallableReferenceExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(CallableReferenceExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ClassLiteralExpression(match: KtClassLiteralExpression.() -> Boolean, map: ClassLiteralExpression.ClassLiteralExpressionScope.(KtClassLiteralExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ClassLiteralExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.DotQualifiedExpression(match: KtDotQualifiedExpression.() -> Boolean, map: DotQualifiedExpression.DotQualifiedExpressionScope.(KtDotQualifiedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DotQualifiedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.SafeQualifiedExpression(match: KtSafeQualifiedExpression.() -> Boolean, map: SafeQualifiedExpression.SafeQualifiedExpressionScope.(KtSafeQualifiedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SafeQualifiedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ObjectLiteralExpression(match: KtObjectLiteralExpression.() -> Boolean, map: ObjectLiteralExpression.ObjectLiteralExpressionScope.(KtObjectLiteralExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ObjectLiteralExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.BlockExpression(match: KtBlockExpression.() -> Boolean, map: BlockExpression.BlockExpressionScope.(KtBlockExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BlockExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.CatchSection(match: KtCatchSection.() -> Boolean, map: CatchSection.CatchSectionScope.(KtCatchSection) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(CatchSection.Companion, match, map)
//
//fun MetaComponentRegistrar.FinallySection(match: KtFinallySection.() -> Boolean, map: FinallySection.FinallySectionScope.(KtFinallySection) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(FinallySection.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeArgumentList(match: KtTypeArgumentList.() -> Boolean, map: TypeArgumentList.TypeArgumentListScope.(KtTypeArgumentList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeArgumentList.Companion, match, map)
//
//fun MetaComponentRegistrar.ThisExpression(match: KtThisExpression.() -> Boolean, map: ThisExpression.ThisExpressionScope.(KtThisExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ThisExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.SuperExpression(match: KtSuperExpression.() -> Boolean, map: SuperExpression.SuperExpressionScope.(KtSuperExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SuperExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ParenthesizedExpression(match: KtParenthesizedExpression.() -> Boolean, map: ParenthesizedExpression.ParenthesizedExpressionScope.(KtParenthesizedExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ParenthesizedExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.InitializerList(match: KtInitializerList.() -> Boolean, map: InitializerList.InitializerListScope.(KtInitializerList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(InitializerList.Companion, match, map)
//
//fun MetaComponentRegistrar.AnonymousInitializer(match: KtAnonymousInitializer.() -> Boolean, map: AnonymousInitializer.AnonymousInitializerScope.(KtAnonymousInitializer) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(AnonymousInitializer.Companion, match, map)
//
//fun MetaComponentRegistrar.PropertyAccessor(match: KtPropertyAccessor.() -> Boolean, map: PropertyAccessor.PropertyAccessorScope.(KtPropertyAccessor) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PropertyAccessor.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeConstraintList(match: KtTypeConstraintList.() -> Boolean, map: TypeConstraintList.TypeConstraintListScope.(KtTypeConstraintList) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeConstraintList.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeConstraint(match: KtTypeConstraint.() -> Boolean, map: TypeConstraint.TypeConstraintScope.(KtTypeConstraint) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeConstraint.Companion, match, map)
//
//fun MetaComponentRegistrar.UserType(match: KtUserType.() -> Boolean, map: UserType.UserTypeScope.(KtUserType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(UserType.Companion, match, map)
//
//fun MetaComponentRegistrar.DynamicType(match: KtDynamicType.() -> Boolean, map: DynamicType.DynamicTypeScope.(KtDynamicType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(DynamicType.Companion, match, map)
//
//fun MetaComponentRegistrar.FunctionType(match: KtFunctionType.() -> Boolean, map: FunctionType.FunctionTypeScope.(KtFunctionType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(FunctionType.Companion, match, map)
//
//fun MetaComponentRegistrar.SelfType(match: KtSelfType.() -> Boolean, map: SelfType.SelfTypeScope.(KtSelfType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SelfType.Companion, match, map)
//
//fun MetaComponentRegistrar.BinaryWithTypeRHSExpression(match: KtBinaryWithTypeRHSExpression.() -> Boolean, map: BinaryWithTypeRHSExpression.BinaryWithTypeRHSExpressionScope.(KtBinaryWithTypeRHSExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BinaryWithTypeRHSExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.StringTemplateExpression(match: KtStringTemplateExpression.() -> Boolean, map: StringTemplateExpression.StringTemplateExpressionScope.(KtStringTemplateExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(StringTemplateExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.NamedDeclaration(match: KtNamedDeclaration.() -> Boolean, map: NamedDeclaration.NamedDeclarationScope.(KtNamedDeclaration) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(NamedDeclaration.Companion, match, map)
//
//fun MetaComponentRegistrar.NullableType(match: KtNullableType.() -> Boolean, map: NullableType.NullableTypeScope.(KtNullableType) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(NullableType.Companion, match, map)
//
//fun MetaComponentRegistrar.TypeProjection(match: KtTypeProjection.() -> Boolean, map: TypeProjection.TypeProjectionScope.(KtTypeProjection) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(TypeProjection.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenEntry(match: KtWhenEntry.() -> Boolean, map: WhenEntry.WhenEntryScope.(KtWhenEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.IsExpression(match: KtIsExpression.() -> Boolean, map: IsExpression.IsExpressionScope.(KtIsExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(IsExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenConditionIsPattern(match: KtWhenConditionIsPattern.() -> Boolean, map: WhenConditionIsPattern.WhenConditionIsPatternScope.(KtWhenConditionIsPattern) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenConditionIsPattern.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenConditionInRange(match: KtWhenConditionInRange.() -> Boolean, map: WhenConditionInRange.WhenConditionInRangeScope.(KtWhenConditionInRange) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenConditionInRange.Companion, match, map)
//
//fun MetaComponentRegistrar.WhenConditionWithExpression(match: KtWhenConditionWithExpression.() -> Boolean, map: WhenConditionWithExpression.WhenConditionWithExpressionScope.(KtWhenConditionWithExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(WhenConditionWithExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.ObjectDeclaration(match: KtObjectDeclaration.() -> Boolean, map: ObjectDeclaration.ObjectDeclarationScope.(KtObjectDeclaration) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ObjectDeclaration.Companion, match, map)
//
//fun MetaComponentRegistrar.StringTemplateEntry(match: KtStringTemplateEntry.() -> Boolean, map: StringTemplateEntry.StringTemplateEntryScope.(KtStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(StringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.StringTemplateEntryWithExpression(match: KtStringTemplateEntryWithExpression.() -> Boolean, map: StringTemplateEntryWithExpression.StringTemplateEntryWithExpressionScope.(KtStringTemplateEntryWithExpression) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(StringTemplateEntryWithExpression.Companion, match, map)
//
//fun MetaComponentRegistrar.BlockStringTemplateEntry(match: KtBlockStringTemplateEntry.() -> Boolean, map: BlockStringTemplateEntry.BlockStringTemplateEntryScope.(KtBlockStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(BlockStringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.SimpleNameStringTemplateEntry(match: KtSimpleNameStringTemplateEntry.() -> Boolean, map: SimpleNameStringTemplateEntry.SimpleNameStringTemplateEntryScope.(KtSimpleNameStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(SimpleNameStringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.LiteralStringTemplateEntry(match: KtLiteralStringTemplateEntry.() -> Boolean, map: LiteralStringTemplateEntry.LiteralStringTemplateEntryScope.(KtLiteralStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(LiteralStringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.EscapeStringTemplateEntry(match: KtEscapeStringTemplateEntry.() -> Boolean, map: EscapeStringTemplateEntry.EscapeStringTemplateEntryScope.(KtEscapeStringTemplateEntry) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(EscapeStringTemplateEntry.Companion, match, map)
//
//fun MetaComponentRegistrar.PackageDirective(match: KtPackageDirective.() -> Boolean, map: PackageDirective.PackageDirectiveScope.(KtPackageDirective) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(PackageDirective.Companion, match, map)
//
//fun MetaComponentRegistrar.ScriptInitializer(match: KtScriptInitializer.() -> Boolean, map: ScriptInitializer.ScriptInitializerScope.(KtScriptInitializer) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ScriptInitializer.Companion, match, map)
//
//fun MetaComponentRegistrar.ClassInitializer(match: KtClassInitializer.() -> Boolean, map: ClassInitializer.ClassInitializerScope.(KtClassInitializer) -> List<String>): ExtensionPhase.AnalysisHandler =
//  quote(ClassInitializer.Companion, match, map)