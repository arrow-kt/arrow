package arrow.meta.higherkind

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.CodeAnalyzerInitializer
import org.jetbrains.kotlin.resolve.lazy.DeclarationScopeProvider
import org.jetbrains.kotlin.resolve.lazy.KotlinCodeAnalyzer

class MetaCodeAnalyzerInitializer(val delegate: CodeAnalyzerInitializer): CodeAnalyzerInitializer by delegate {
  override fun createTrace(): BindingTrace =
    delegate.createTrace()


  override fun initialize(trace: BindingTrace, module: ModuleDescriptor, codeAnalyzer: KotlinCodeAnalyzer) {
    delegate.initialize(trace, module, MetaKotlinCodeAnalyzer(codeAnalyzer))
  }
}

class MetaKotlinCodeAnalyzer(val delegate: KotlinCodeAnalyzer): KotlinCodeAnalyzer by delegate {

  override fun getDeclarationScopeProvider(): DeclarationScopeProvider {
    println("MetaKotlinCodeAnalyzer.DeclarationScopeProvider")
    return delegate.declarationScopeProvider
  }

  override fun getClassDescriptor(p0: KtClassOrObject, p1: LookupLocation): ClassDescriptor {
    println("MetaKotlinCodeAnalyzer.getClassDescriptor")
    return delegate.getClassDescriptor(p0, p1)
  }
}