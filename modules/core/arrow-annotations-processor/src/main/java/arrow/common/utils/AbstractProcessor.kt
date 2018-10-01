package arrow.common.utils

import aballano.kotlinmemoization.memoize
import arrow.common.messager.logE
import arrow.meta.processor.MetaProcessorUtils
import com.squareup.kotlinpoet.TypeName
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class KnownException(message: String, val element: Element?) : RuntimeException(message) {
  override val message: String get() = super.message as String
  operator fun component1() = message
  operator fun component2() = element
}

abstract class AbstractProcessor : KotlinAbstractProcessor(), MetaProcessorUtils {

  override val typeNameToMeta: (typeName: TypeName) -> arrow.meta.ast.TypeName =
    ::typeNameToMetaImpl.memoize()

  override val typeNameDownKind: (typeName: arrow.meta.ast.TypeName) -> arrow.meta.ast.TypeName =
    ::typeNameDownKindImpl.memoize()

  final override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
    if (!roundEnv.errorRaised()) {
      try {
        onProcess(annotations, roundEnv)
      } catch (e: KnownException) {
        logE(e.message, e.element)
      }
    }
    return false
  }

  protected abstract fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment)

  val typeElementToMeta: (classElement: TypeElement) -> ClassOrPackageDataWrapper =
    ::getClassOrPackageDataWrapper.memoize()

  override val TypeElement.meta: ClassOrPackageDataWrapper.Class
    get() = typeElementToMeta(this) as ClassOrPackageDataWrapper.Class

}
