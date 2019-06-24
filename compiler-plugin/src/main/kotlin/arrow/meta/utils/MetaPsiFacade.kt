package arrow.meta.higherkind

import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.psi.JavaPsiFacade
import org.jetbrains.kotlin.com.intellij.psi.PsiClass
import org.jetbrains.kotlin.com.intellij.psi.PsiConstantEvaluationHelper
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiElementFactory
import org.jetbrains.kotlin.com.intellij.psi.PsiExpression
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaModule
import org.jetbrains.kotlin.com.intellij.psi.PsiJavaParserFacade
import org.jetbrains.kotlin.com.intellij.psi.PsiNameHelper
import org.jetbrains.kotlin.com.intellij.psi.PsiPackage
import org.jetbrains.kotlin.com.intellij.psi.PsiResolveHelper
import org.jetbrains.kotlin.com.intellij.psi.search.GlobalSearchScope

class MetaPsiFacade(val delegate: JavaPsiFacade) : JavaPsiFacade() {
  override fun isPartOfPackagePrefix(p0: String): Boolean {
    println("MetaPsiFacade.isPartOfPackagePrefix: $p0")
    return delegate.isPartOfPackagePrefix(p0)
  }

  override fun isInPackage(p0: PsiElement, p1: PsiPackage): Boolean {
    println("MetaPsiFacade.isInPackage: $p0, $p1")
    return delegate.isInPackage(p0, p1)
  }


  override fun getElementFactory(): PsiElementFactory {
    println("MetaPsiFacade.getElementFactory: $delegate.elementFactory")
    return delegate.elementFactory
  }

  override fun findModule(p0: String, p1: GlobalSearchScope): PsiJavaModule? {
    println("MetaPsiFacade.findModule: $p0")
    return delegate.findModule(p0, p1)
  }

  override fun findModules(p0: String, p1: GlobalSearchScope): MutableCollection<PsiJavaModule> {
    println("MetaPsiFacade.findModules: $p0")
    return delegate.findModules(p0, p1)
  }

  override fun findPackage(p0: String): PsiPackage? {
    println("MetaPsiFacade.findPackage: $p0")
    return delegate.findPackage(p0)
  }

  override fun getParserFacade(): PsiJavaParserFacade {
    println("MetaPsiFacade.getParserFacade")
    return delegate.parserFacade
  }

  override fun findClasses(p0: String, p1: GlobalSearchScope): Array<PsiClass> {
    println("MetaPsiFacade.findClasses: $p0")
    return delegate.findClasses(p0, p1)
  }

  override fun getProject(): Project =
    delegate.project

  override fun arePackagesTheSame(p0: PsiElement, p1: PsiElement): Boolean {
    return delegate.arePackagesTheSame(p0, p1)
  }

  override fun getConstantEvaluationHelper(): PsiConstantEvaluationHelper {
    return delegate.constantEvaluationHelper
  }

  override fun isConstantExpression(p0: PsiExpression): Boolean {
    return delegate.isConstantExpression(p0)
  }

  override fun findClass(p0: String, p1: GlobalSearchScope): PsiClass? {
    return delegate.findClass(p0, p1)
  }

  override fun getNameHelper(): PsiNameHelper {
    return delegate.nameHelper
  }

  override fun getResolveHelper(): PsiResolveHelper {
    return delegate.resolveHelper
  }
}