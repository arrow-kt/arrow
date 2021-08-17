package arrow.common.utils

import arrow.common.messager.logE
import arrow.common.messager.logW
import arrow.meta.encoder.jvm.KotlinMetatadataEncoder
import com.squareup.kotlinpoet.FileSpec
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import java.io.File
import java.io.IOException
import java.nio.file.Files
import javax.annotation.processing.Filer
import javax.annotation.processing.FilerException
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.StandardLocation

public class KnownException(message: String, public val element: Element?) : RuntimeException(message) {
  override val message: String get() = super.message as String
  public operator fun component1(): String = message
  public operator fun component2(): Element? = element
}

public abstract class AbstractProcessor : KotlinAbstractProcessor(), ProcessorUtils, KotlinMetatadataEncoder {

  private fun Element.kDocLocation(): File =
    locationName()
      .replacePackageSeparatorsToFolderSeparators()
      .replaceInvalidPathCharacters()
      .let { "$tmpDir/build/kdocs/meta/$it.javadoc" }
      .let(::File)

  public fun Element.kDoc(): String? =
    @Suppress("SwallowedException")
    try {
      kDocLocation()
        .readLines().joinToString("\n") {
          val line = it.substringAfter(" * ")
          if (line.trim() == "*") ""
          else line.replace(" ", "·")
        } + "\n"
    } catch (e: Exception) {
      null
    }

  private fun processElementDoc(e: Element) {
    try {
      val doc = elementUtils.getDocComment(e)
      if (doc != null && doc.trim { it <= ' ' }.isNotEmpty()) {
        val kDocLocation = e.kDocLocation()
        @Suppress("SwallowedException")
        try {
          val path = kDocLocation.toPath()
          @Suppress("SwallowedException")
          try {
            Files.createDirectories(path.parent)
          } catch (e: IOException) {
          }
          @Suppress("SwallowedException")
          try {
            kDocLocation.createNewFile()
          } catch (e: IOException) {
          }
          kDocLocation.writeText(doc)
        } catch (x: IOException) {
          logW("Failed to generate kdoc file location: $kDocLocation", e)
        }
      }
    } catch (e: Exception) {
      logE(e.localizedMessage)
    }
  }

  private fun Element.locationName(): String = when (kind) {
    ElementKind.CLASS -> (this as TypeElement).qualifiedName.toString()
    ElementKind.INTERFACE -> (this as TypeElement).qualifiedName.toString()
    ElementKind.METHOD -> (this as ExecutableElement).let {
      val name = (it.enclosingElement as TypeElement).qualifiedName.toString()

      val extensionName = (it.enclosingElement.kotlinMetadata?.asClassOrPackageDataWrapper(it.enclosingElement as TypeElement) as? ClassOrPackageDataWrapper.Class)?.let { classData ->
        val n = classData.getFunction(it)?.toMeta(classData, it)?.receiverType?.simpleName
        if (n == classData.simpleName) "" else "$n-"
      } ?: ""

      val functionName = it.simpleName.toString()
      "$name.$extensionName$functionName"
    }
    else -> knownError("Unsupported @documented $kind")
  }

  private fun processDocs(roundEnv: RoundEnvironment): Unit = Unit

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

private val tmpDir get() = System.getProperty("java.io.tmpdir")
private fun String.replacePackageSeparatorsToFolderSeparators() = replace('.', '/')
private fun String.replaceInvalidPathCharacters() = replace('?', '_')

/** Writes this to `filer`.  */
@Throws(IOException::class)
public fun Filer.writeSafe(
  pkg: CharSequence,
  name: CharSequence,
  fileString: CharSequence,
  logger: ((message: CharSequence) -> Unit)? = null,
  vararg originatingElements: Element?
): Unit = catchDoubleAttempt({
  when (pkg) {
    "unnamed package" -> knownError("package not found")
    else -> {
      val filerSourceFile = createResource(StandardLocation.SOURCE_OUTPUT, pkg, "$name.kt", *originatingElements)
      try {
        filerSourceFile.openWriter().use { writer -> writer.append(fileString) }
      } catch (exception: IOException) {
        knownError("Cannot create the file: ${exception.message}")
      }
    }
  }
}) {
  logger?.invoke("$it by $pkg.$name")
}

/** Writes this to `filer`.  */
@Throws(IOException::class)
public fun FileSpec.writeSafeTo(filer: Filer, logger: ((message: CharSequence) -> Unit)? = null): Unit =
  catchDoubleAttempt({
    writeTo(filer)
  }) {
    logger?.invoke("$it by $packageName.$name")
  }

@Throws(IOException::class)
private fun catchDoubleAttempt(block: () -> Unit, logger: ((message: CharSequence) -> Unit)? = null) {
  try {
    block()
  } catch (ignoreDoubleAttempt: FilerException) {
    if (ignoreDoubleAttempt.message?.startsWith("Attempt to reopen a file for path") == true) {
      logger?.invoke("${ignoreDoubleAttempt.message}")
    } else {
      throw ignoreDoubleAttempt
    }
  }
}
