package arrow.fold

import com.google.auto.service.AutoService
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.asClassOrPackageDataWrapper
import arrow.common.utils.isSealed
import arrow.common.utils.knownError
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.element.TypeParameterElement

@AutoService(Processor::class)
class AutoFoldProcessor : AbstractProcessor() {

  private val annotatedList = mutableListOf<AnnotatedFold>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = setOf(foldAnnotationClass.canonicalName)

  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    annotatedList += roundEnv
      .getElementsAnnotatedWith(foldAnnotationClass)
      .map { element ->
        when {
          element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isSealed == true -> {
            val (nameResolver, classProto) = element.kotlinMetadata.let { it as KotlinClassMetadata }.data

            AnnotatedFold(
              element as TypeElement,
              element.typeParameters.map(TypeParameterElement::toString),
              element.kotlinMetadata
                .let { it as KotlinClassMetadata }
                .data
                .asClassOrPackageDataWrapper(elementUtils.getPackageOf(element).toString()),
              classProto.sealedSubclassFqNameList
                .map(nameResolver::getString)
                .map { it.replace('/', '.') }
                .map {
                  Variant(it,
                    elementUtils.getTypeElement(it).typeParameters.map(TypeParameterElement::toString),
                    it.substringAfterLast("."))
                }
            )
          }

          else -> knownError("""
            |$this is an invalid target for @autofold.
            |Generation of fold is only supported for sealed classes.
            """.trimMargin())
        }
      }

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, foldAnnotationClass.simpleName).also { it.mkdirs() }
      AutoFoldFileGenerator(annotatedList, generatedDir).generate()
    }
  }
}
