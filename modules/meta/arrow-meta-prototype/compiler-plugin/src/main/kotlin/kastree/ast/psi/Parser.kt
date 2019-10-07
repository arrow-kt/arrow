package kastree.ast.psi

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiErrorElement
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

open class Parser(val converter: Converter = Converter) {
  protected val proj by lazy {
    KotlinCoreEnvironment.createForProduction(
      Disposer.newDisposable(),
      CompilerConfiguration(),
      EnvironmentConfigFiles.JVM_CONFIG_FILES
    ).project
  }

  fun parseFile(code: String, throwOnError: Boolean = true) = converter.convertFile(parsePsiFile(code).also { file ->
    if (throwOnError) file.collectDescendantsOfType<PsiErrorElement>().let {
      if (it.isNotEmpty()) throw ParseError(file, it)
    }
  })

  fun parsePsiFile(code: String) =
    PsiManager.getInstance(proj).findFile(LightVirtualFile("temp.kt", KotlinFileType.INSTANCE, code)) as KtFile

  data class ParseError(
    val file: KtFile,
    val errors: List<PsiErrorElement>
  ) : IllegalArgumentException("Failed with ${errors.size} errors, first: ${errors.first().errorDescription}")

  companion object : Parser() {
    init {
      // To hide annoying warning on Windows
      System.setProperty("idea.use.native.fs.for.win", "false")
    }
  }
}
