package kategory.common.utils

import kategory.common.messager.logE
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import me.eugeniomarletti.kotlin.metadata.kaptGeneratedOption
import java.io.File

class KnownException(message: String, val element: Element?) : RuntimeException(message) {
    override val message: String get() = super.message as String
    operator fun component1() = message
    operator fun component2() = element
}

abstract class AbstractProcessor : KotlinAbstractProcessor(), ProcessorUtils {

    val generatedDir: File? get() = options[kaptGeneratedOption]?.let(::File)

    override final fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (!roundEnv.errorRaised()) {
            try {
                onProcess(annotations, roundEnv)
            }
            catch (e: KnownException) {
                logE(e.message, e.element)
            }
        }
        return false
    }

    protected abstract fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment)
}
