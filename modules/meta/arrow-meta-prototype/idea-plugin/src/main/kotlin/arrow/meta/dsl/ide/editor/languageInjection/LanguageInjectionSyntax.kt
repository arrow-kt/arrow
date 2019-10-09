package arrow.meta.dsl.ide.editor.languageInjection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost

interface LanguageInjectionSyntax {
  fun LanguageInjectionSyntax.languageInjector(
    elementToInjectIn: Class<out PsiElement>,
    languagesToInject: (registrar: MultiHostRegistrar, context: PsiElement) -> Unit
  ): MultiHostInjector =
    object : MultiHostInjector {
      override fun elementsToInjectIn(): List<Class<out PsiElement>> =
        listOf(elementToInjectIn)

      override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) =
        languagesToInject(registrar, context)
    }

  fun LanguageInjectionSyntax.elementToInjectIn(): PsiLanguageInjectionHost = TODO("Revisit at a later point")
  /*object : PsiLanguageInjectionHost, ContributedReferenceHost, KtExpression {
  }*/
}
