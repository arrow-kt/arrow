package arrow.plugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.CompilerConfigurationExtension
import org.jetbrains.kotlin.extensions.DeclarationAttributeAltererExtension
import org.jetbrains.kotlin.extensions.PreprocessedVirtualFileFactoryExtension
import org.jetbrains.kotlin.extensions.StorageComponentContainerContributor
import org.jetbrains.kotlin.js.translate.extensions.JsSyntheticTranslateExtension
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.jvm.extensions.AnalysisHandlerExtension
import org.jetbrains.kotlin.resolve.jvm.extensions.PackageFragmentProviderExtension

@AutoService(ComponentRegistrar::class)
class ArrowComponentRegistrar : ComponentRegistrar {

  /**
   * Registers extension points for the different publicly exposed compiler phases
   * Extensions run in the right compiler phase in a predefined order.
   * Regardless of registration order, the execution order observed so far is:
   *
   * ComponentRegistrar.registerProjectComponents
   * CompilerConfigurationExtension.updateConfiguration
   * PackageFragmentProviderExtension.getPackageFragmentProvider
   * AnalysisHandlerExtension.doAnalysis
   * SyntheticResolveExtension.generateSyntheticClasses
   * SyntheticResolveExtension.addSyntheticSupertypes
   * SyntheticResolveExtension.getSyntheticCompanionObjectNameIfNeeded
   * DeclarationAttributeAltererExtension.refineDeclarationModality
   * SyntheticResolveExtension.generateSyntheticMethods
   * SyntheticResolveExtension.getSyntheticCompanionObjectNameIfNeeded
   * SyntheticResolveExtension.getSyntheticFunctionNames
   * SyntheticResolveExtension.getSyntheticNestedClassNames
   * StorageComponentContainerContributor.check
   * ClassBuilderInterceptorExtension.newClassBuilder
   * ClassBuilderInterceptorExtension.DelegatingClassBuilder.newMethod
   * ExpressionCodegenExtension.applyFunction
   * ExpressionCodegenExtension.applyProperty
   * ExpressionCodegenExtension.generateClassSyntheticParts
   */
  override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
    println("ComponentRegistrar.registerProjectComponents")

    // see https://github.com/JetBrains/kotlin/blob/1.1.2/plugins/annotation-collector/src/org/jetbrains/kotlin/annotation/AnnotationCollectorPlugin.kt#L92
    val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

    StorageComponentContainerContributor.registerExtension(project, TestStorageComponentContainerContributor())
    ClassBuilderInterceptorExtension.registerExtension(project, MetaClassBuilderInterceptorExtension(messageCollector))
    PackageFragmentProviderExtension.registerExtension(project, MetaPackageFragmentProviderExtension())
    AnalysisHandlerExtension.registerExtension(project, MetaAnalysisHandlerExtension())
    ExpressionCodegenExtension.registerExtension(project, MetaExpressionCodegenExtension())
    SyntheticResolveExtension.registerExtension(project, MetaSyntheticResolveExtension())
    DeclarationAttributeAltererExtension.registerExtension(project, MetaDeclarationAttributeAltererExtension())
    PreprocessedVirtualFileFactoryExtension.registerExtension(project, MetaPreprocessedVirtualFileFactoryExtension())
    JsSyntheticTranslateExtension.registerExtension(project, MetaJsSyntheticTranslateExtension())
    CompilerConfigurationExtension.registerExtension(project, MetaCompilerConfigurationExtension())
    IrGenerationExtension.registerExtension(project, MetaIrGenerationExtension())

  }
}


