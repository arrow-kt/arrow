package arrow.meta.phases.analysis

import arrow.meta.phases.CompilerContext
import arrow.meta.quotes.Quote
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.analyzer.ModuleInfo
import org.jetbrains.kotlin.com.intellij.openapi.editor.Document
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BodyResolver
import org.jetbrains.kotlin.resolve.StatementFilter
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.lazy.declarations.ClassMemberDeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.declarations.PackageMemberDeclarationProvider
import org.jetbrains.kotlin.types.KotlinType

interface MetaAnalyzer {

  fun <P : KtElement, K : KtElement, S> CompilerContext.subscribeToEditorHooks(
    project: Project,
    quoteFactory: Quote.Factory<P, K, S>,
    match: K.() -> Boolean,
    map: S.(K) -> List<String>,
    transformation: (VirtualFile, Document) -> Pair<KtFile, AnalysisResult>?
  )

  fun createBodyResolver(
    resolveSession: ResolveSession,
    trace: BindingTrace,
    file: KtFile,
    statementFilter: StatementFilter
  ): BodyResolver

  fun KtFile.metaAnalysys(moduleInfo: ModuleInfo? = null): AnalysisResult
  fun metaPackageFragments(module: ModuleDescriptor, fqName: FqName): List<PackageFragmentDescriptor>
  fun metaSubPackagesOf(module: ModuleDescriptor, fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName>
  fun metaSyntheticFunctionNames(thisDescriptor: ClassDescriptor): List<Name>
  fun metaSyntheticNestedClassNames(thisDescriptor: ClassDescriptor): List<Name>
  fun metaSyntheticMethods(name: Name, thisDescriptor: ClassDescriptor): List<SimpleFunctionDescriptor>
  fun metaSyntheticProperties(name: Name, thisDescriptor: ClassDescriptor): List<PropertyDescriptor>
  fun metaSyntheticPackageClasses(name: Name, packageDescriptor: PackageFragmentDescriptor, declarationProvider: PackageMemberDeclarationProvider): List<ClassDescriptor>
  fun metaCompanionObjectNameIfNeeded(classDescriptor: ClassDescriptor): Name?
  fun metaSyntheticSupertypes(classDescriptor: ClassDescriptor): List<KotlinType>
  fun metaSyntheticClasses(name: Name, classDescriptor: ClassDescriptor, declarationProvider: ClassMemberDeclarationProvider): List<ClassDescriptor>
  fun populateSyntheticCache(document: com.intellij.openapi.editor.Document, transformation: (com.intellij.openapi.vfs.VirtualFile, com.intellij.openapi.editor.Document) -> Pair<KtFile, AnalysisResult>?)
}
