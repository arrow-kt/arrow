package arrow.generic

import com.google.auto.service.AutoService
import arrow.common.utils.AbstractProcessor
import arrow.coproduct
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class CoproductProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(coproduct::class.java.canonicalName)

    override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
        val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }

        generateCoproducts(generatedDir)
    }
}