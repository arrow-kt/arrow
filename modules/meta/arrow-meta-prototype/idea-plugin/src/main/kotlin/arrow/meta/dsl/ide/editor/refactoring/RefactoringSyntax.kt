package arrow.meta.dsl.ide.editor.refactoring

import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugin.idea.IdeMetaPlugin
import com.intellij.lang.ImportOptimizer
import com.intellij.lang.LanguageImportStatements.INSTANCE
import com.intellij.lang.LanguageNamesValidation
import com.intellij.lang.LanguageRefactoringSupport
import com.intellij.lang.refactoring.NamesValidator
import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.refactoring.changeSignature.ChangeSignatureHandler
import com.intellij.refactoring.rename.RenamePsiFileProcessor

interface RefactoringSyntax {
  fun IdeMetaPlugin.addImportOptimizer(
    supports: (file: PsiFile?) -> Boolean,
    processFile: (file: PsiFile?) -> Runnable
  ): ExtensionPhase =
    extensionProvider(
      INSTANCE,
      object : ImportOptimizer {
        override fun supports(file: PsiFile?): Boolean = supports(file)

        override fun processFile(file: PsiFile?): Runnable = processFile(file)
      }
    )

  /**
   * TODO: Test Default values
   */
  fun IdeMetaPlugin.addRefactoringSupportProvider(
    introduceFunctionalParameterHandler: RefactoringActionHandler? = null,
    pushDownHandler: RefactoringActionHandler? = null,
    introduceFunctionalVariableHandler: RefactoringActionHandler? = null,
    isInplaceRenameAvailable: (element: PsiElement, context: PsiElement?) -> Boolean = Noop.boolean2False,
    extractInterfaceHandler: RefactoringActionHandler? = null,
    introduceConstantHandler: RefactoringActionHandler? = null,
    introduceVariableHandler: RefactoringActionHandler? = null,
    introduceVariableHandlerOnPsi: (element: PsiElement?) -> RefactoringActionHandler? = Noop.nullable1(),
    extractModuleHandler: RefactoringActionHandler? = null,
    isInplaceIntroduceAvailable: (element: PsiElement, context: PsiElement?) -> Boolean = Noop.boolean2False,
    pullUpHandler: RefactoringActionHandler? = null,
    isSafeDeleteAvailable: (element: PsiElement) -> Boolean = Noop.boolean1False,
    introduceFieldHandler: RefactoringActionHandler? = null,
    isMemberInplaceRenameAvailable: (element: PsiElement, context: PsiElement?) -> Boolean = Noop.boolean2False,
    extractMethodHandler: RefactoringActionHandler? = null,
    changeSignatureHandler: ChangeSignatureHandler? = null,
    extractClassHandler: RefactoringActionHandler? = null,
    isAvailable: (context: PsiElement) -> Boolean = Noop.boolean1True,
    extractSuperClassHandler: RefactoringActionHandler? = null,
    introduceParameterHandler: RefactoringActionHandler? = null
  ): ExtensionPhase =
    extensionProvider(
      LanguageRefactoringSupport.INSTANCE,
      object : RefactoringSupportProvider() {
        override fun getIntroduceFunctionalParameterHandler(): RefactoringActionHandler? =
          introduceFunctionalParameterHandler

        override fun getPushDownHandler(): RefactoringActionHandler? =
          pushDownHandler

        override fun getIntroduceFunctionalVariableHandler(): RefactoringActionHandler {
          return introduceFunctionalVariableHandler ?: super.getIntroduceFunctionalVariableHandler()
        }

        override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
          isInplaceRenameAvailable(element, context)

        override fun getExtractInterfaceHandler(): RefactoringActionHandler? =
          extractInterfaceHandler

        override fun getIntroduceConstantHandler(): RefactoringActionHandler? =
          introduceConstantHandler

        override fun getIntroduceVariableHandler(): RefactoringActionHandler? =
          introduceVariableHandler

        override fun getIntroduceVariableHandler(element: PsiElement?): RefactoringActionHandler? =
          introduceVariableHandlerOnPsi(element)

        override fun getExtractModuleHandler(): RefactoringActionHandler? =
          extractModuleHandler

        override fun isInplaceIntroduceAvailable(element: PsiElement, context: PsiElement?): Boolean =
          isInplaceIntroduceAvailable(element, context)

        override fun isSafeDeleteAvailable(element: PsiElement): Boolean =
          isSafeDeleteAvailable(element)

        override fun getPullUpHandler(): RefactoringActionHandler? =
          pullUpHandler

        override fun getIntroduceFieldHandler(): RefactoringActionHandler? =
          introduceFieldHandler

        override fun isMemberInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
          isMemberInplaceRenameAvailable(element, context)

        override fun getExtractMethodHandler(): RefactoringActionHandler? =
          extractMethodHandler

        override fun getChangeSignatureHandler(): ChangeSignatureHandler? =
          changeSignatureHandler

        override fun getExtractClassHandler(): RefactoringActionHandler? =
          extractClassHandler

        override fun isAvailable(context: PsiElement): Boolean =
          isAvailable(context)

        override fun getExtractSuperClassHandler(): RefactoringActionHandler? =
          extractSuperClassHandler

        override fun getIntroduceParameterHandler(): RefactoringActionHandler? =
          introduceParameterHandler
      }
    )

  fun IdeMetaPlugin.addNamesValidator(
    isKeyword: (name: String, project: Project?) -> Boolean,
    isIdentifier: (name: String, project: Project?) -> Boolean
  ): ExtensionPhase =
    extensionProvider(
      LanguageNamesValidation.INSTANCE,
      object : NamesValidator {
        override fun isKeyword(name: String, project: Project?): Boolean =
          isKeyword(name, project)

        override fun isIdentifier(name: String, project: Project?): Boolean =
          isIdentifier(name, project)
      }
    )

  /**
   * [RenamePsiFileProcessor]
   */
  fun IdeMetaPlugin.addRenamePsiFileProcessor():
    ExtensionPhase =
    extensionProvider(
      RenamePsiFileProcessor.EP_NAME,
      TODO()
    )

  /**
   * Can be used for Introducing Variables or more
   */
  fun RefactoringSyntax.refactoringActionHandler(
    invokeWithEditor: (project: Project, editor: Editor?, file: PsiFile?, dataContext: DataContext?) -> Unit,
    invoke: (project: Project, elements: Array<out PsiElement>, dataContext: DataContext?) -> Unit
  ): RefactoringActionHandler =
    object : RefactoringActionHandler {
      override fun invoke(project: Project, editor: Editor?, file: PsiFile?, dataContext: DataContext?) =
        invokeWithEditor(project, editor, file, dataContext)

      /**
       * Not called when introducing a variable
       */
      override fun invoke(project: Project, elements: Array<out PsiElement>, dataContext: DataContext?) =
        invoke(project, elements, dataContext)
    }
}
