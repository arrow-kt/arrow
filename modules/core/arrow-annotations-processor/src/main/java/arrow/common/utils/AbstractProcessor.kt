package arrow.common.utils

import arrow.common.messager.logE
import arrow.common.messager.logW
import arrow.documented
import arrow.meta.encoder.jvm.KotlinMetatadataEncoder
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.file.Files
import java.nio.file.Files.createFile
import java.nio.file.Files.delete
import java.nio.file.Path
import java.nio.file.Paths
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.FileObject
import javax.tools.JavaFileManager
import javax.tools.StandardLocation
import javax.annotation.processing.Filer



class KnownException(message: String, val element: Element?) : RuntimeException(message) {
  override val message: String get() = super.message as String
  operator fun component1() = message
  operator fun component2() = element
}

abstract class AbstractProcessor : KotlinAbstractProcessor(), ProcessorUtils, KotlinMetatadataEncoder {

  private fun Element.kDocLocation(): File =
    File(
      System.getProperty("user.dir") +
        "/build/kdocs/meta/"+ "${locationName().replace('.', '/')}.javadoc")

  fun Element.kDoc(): String? =
    try {
      kDocLocation()
        .readLines().joinToString("\n") {
          val line = it.substringAfter(" * ")
          if (line.trim() == "*") ""
          else line
        } + "\n"
    } catch (e: Exception) {
      null
    }

  private fun processElementDoc(e: Element): Unit {
    val doc = elementUtils.getDocComment(e)
    val kDocLocation = e.kDocLocation()
    if (doc != null && doc.trim { it <= ' ' }.isNotEmpty()) {
      try {
        Files.createDirectories(kDocLocation.toPath().parent)
        Files.createFile(kDocLocation.toPath())
        kDocLocation.writeText(doc)
      } catch (x: IOException) {
        logE("Failed to generate kdoc file location: $kDocLocation", e)
      }
    }
  }

  private fun Element.locationName(): String {
    return when (kind) {
      ElementKind.CLASS -> (this as TypeElement).qualifiedName.toString()
      ElementKind.INTERFACE -> (this as TypeElement).qualifiedName.toString()
      ElementKind.METHOD -> (this as ExecutableElement).let {
        val name = (it.enclosingElement as TypeElement).qualifiedName.toString()
        val functionName = it.simpleName.toString()
        "$name.$functionName"
      }
      else -> throw RuntimeException("Unsupported @documented $kind")
    }
  }

  private fun processDocs(roundEnv: RoundEnvironment): Unit =
    roundEnv
      .getElementsAnnotatedWith(documented::class.java)
      .filterIsInstance<TypeElement>().forEach {
        processElementDoc(it)
        it.enclosedElements.forEach(::processElementDoc)
      }


  final override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
    if (!roundEnv.errorRaised()) {
      try {
        processDocs(roundEnv)
        onProcess(annotations, roundEnv)
      } catch (e: KnownException) {
        logE(e.message, e.element)
      }
    }
    return false
  }

  protected abstract fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment)

}
