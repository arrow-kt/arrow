package arrow.reflect

import arrow.extension
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassInfoList
import io.github.classgraph.TypeArgument

/**
 * The triple of a [dataType], a [typeClass] and an [instance]
 */
data class TypeClassExtension(
  val dataType: DataType,
  val typeClass: TypeClass,
  val instance: Extension
)

internal fun ClassInfo.isTypeClassInstance(): Boolean =
  typeSignature.superinterfaceSignatures.isNotEmpty() &&
    typeSignature.superinterfaceSignatures[0].typeArguments.isNotEmpty()

internal fun ClassInfo.asClassTypeExtension(): TypeClassExtension? =
  if (isTypeClassInstance()) {
    val typeClass = typeSignature.superinterfaceSignatures[0].fullyQualifiedClassName
    val dataType = typeSignature.superinterfaceSignatures[0].typeArguments[0].unKind()
    TypeClassExtension(
      dataType = dataType,
      typeClass = TypeClass(Class.forName(typeClass).kotlin),
      instance = Extension(Class.forName(name).kotlin)
    )
  } else null

/**
 * A list of all type class instance @extensions in the classpath
 */
internal val classPathExtensionsInfo: ClassInfoList =
  ClassGraph()
    .enableClassInfo()
    .enableAnnotationInfo()
    .enableExternalClasses()
    .scan().use {
      it.getClassesWithAnnotation(extension::class.qualifiedName)
    }

/**
 * A list of all type class instance @extensions in the classpath
 */
internal val classPathExtensions: List<TypeClassExtension> =
  classPathExtensionsInfo
    .filter(ClassInfo::isTypeClassInstance)
    .mapNotNull(ClassInfo::asClassTypeExtension)

/**
 * The nasty bits. Down-kind a deeply nested kind to introspect the data type that is targeting
 */
private fun TypeArgument.unKind(): DataType =
    Class.forName(toString()
      .replace("arrow.Kind<? extends", "")
      //order is important for next 2 since `*?` removes pattern for `*\w`
      .replace(", \\? extends \\w.*\\w".toRegex(), "") //? extends java.lang.Throwable
      .replace(", \\? extends \\w.*?".toRegex(), "") //? extends F
      .replace("<.*".toRegex(), "")
      .replace(">", "")
      .replace(".For", ".")
      .trim()).kotlin.let(::DataType)