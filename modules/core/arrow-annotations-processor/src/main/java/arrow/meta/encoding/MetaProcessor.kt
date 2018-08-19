package arrow.meta.encoding

import arrow.common.utils.*
import arrow.derive.normalizeType
import arrow.meta.Class as MetaClass
import arrow.meta.*
import arrow.meta.Function
import me.eugeniomarletti.kotlin.metadata.*
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.Flags
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf as proto

data class Transform(val fileName: String, val trees: List<Tree>)

abstract class MetaProcessor<A : Annotation>(private val annotations: List<KClass<A>>) : AbstractProcessor() {

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = annotations.map { it.java.canonicalName }.toSet()

  abstract fun transform(tree: Tree): Transform

  /**
   * Processor entry point
   */
  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    this.annotations.forEach { annotation ->
      val results: List<Transform> = roundEnv
        .getElementsAnnotatedWith(annotation.java)
        .map { element ->
          when (element.kind) {
            ElementKind.CLASS -> listOf(transform(elementToClass(element as TypeElement))
            ElementKind.INTERFACE -> listOf(transform(elementToClass(element as TypeElement))
            else -> listOf(knownError("Unsupported meta annotation: $annotation over ${element.kind.name} ")
          }
        }
      if (roundEnv.processingOver()) {
        val generatedDir = File(this.generatedDir!!, annotation.simpleName).also { it.mkdirs() }
        results.forEach { t ->
          val file = File(generatedDir, t.fileName)
          t.trees.forEach { tree ->
            file.writeText(tree.code())
          }
        }
      }
    }
  }

  private fun elementToClass(element: TypeElement): arrow.meta.Class {
    val proto = getClassOrPackageDataWrapper(element) as ClassOrPackageDataWrapper.Class
    val functions: List<Function>,
    val superTypes: List<Type>,
    val typeAliases: List<TypeAlias>,
    val enumEntries: List<EnumEntry>,
    val companionName: Name?,
    val nestedClassNames: List<Name>,
    val sealedSubClassNames: List<Name>
    arrow.meta.Class(
      encoding = classEncoding(proto),
      annotations = annotations(proto, element),
      flags = if (proto.classProto.hasFlags()) flags(proto.classProto.flags) else emptyList(),
      `package` = Name(proto.`package`),
      typeParameters = typeParameters(proto),
      name = Name(proto.fullName.normalizeType()),
      simpleName = Name(proto.simpleName),
      constructors = constructors(proto),
      functions = functions(proto)
    )
  }

  private fun functions(protoClass: ClassOrPackageDataWrapper.Class): List<arrow.meta.Function> =
    if (protoClass.functionList.isNotEmpty())
      protoClass.functionList.map { it.toMeta(protoClass) }
    else emptyList()

  fun proto.Function.toMeta(protoClass: ClassOrPackageDataWrapper.Class): arrow.meta.Function =
    Function(
      name = Name(protoClass.nameResolver.getString(name)),
      modality = modality.toMeta(protoClass),
      visibility = visibility.toMeta(),
      flags = flags(flags),
      valueParameters = if (valueParameterCount > 0) valueParameterList.map { it.toMeta(protoClass) } else emptyList(),
      versionRequirement = if (hasVersionRequirement()) protoClass.nameResolver.getString(versionRequirement) else null,

    )

  private fun constructors(protoClass: ClassOrPackageDataWrapper.Class): List<arrow.meta.Constructor> =
    if (protoClass.constructorList.isNotEmpty())
      protoClass.constructorList.map { it.toMeta(protoClass) }
    else emptyList()

  fun proto.Constructor.toMeta(protoClass: ClassOrPackageDataWrapper.Class): arrow.meta.Constructor =
    Constructor(
      visibility = visibility.toMeta(),
      flags = flags(flags),
      valueParameters = if (valueParameterCount > 0) valueParameterList.map { it.toMeta(protoClass) } else emptyList(),
      versionRequirement = if (hasVersionRequirement()) protoClass.nameResolver.getString(versionRequirement) else null,
      primary = isPrimary,
      secondary = isSecondary
    )

  private fun proto.ValueParameter.toMeta(protoClass: ClassOrPackageDataWrapper.Class): ValueParameter =
    ValueParameter(
      annotations = emptyList(),
      flags = flags(flags),
      name = Name(protoClass.nameResolver.getString(name)),
      type = type.toMeta(protoClass),
      varargType = if (hasVarargElementType()) varargElementType.toMeta(protoClass) else null
    )


  fun proto.Visibility?.toMeta(): arrow.meta.Visibility? =
    when (this) {
      proto.Visibility.INTERNAL -> Visibility.Internal
      proto.Visibility.PRIVATE -> Visibility.Private
      proto.Visibility.PROTECTED -> Visibility.Protected
      proto.Visibility.PUBLIC -> Visibility.Public
      proto.Visibility.PRIVATE_TO_THIS -> Visibility.PrivateToThis
      proto.Visibility.LOCAL -> Visibility.Local
      null -> null
    }

  private fun typeParameters(protoClass: ClassOrPackageDataWrapper.Class): List<arrow.meta.TypeParameter> =
    if (protoClass.typeParameters.isNotEmpty())
      protoClass.typeParameters.map { tp ->
        TypeParameter(
          name = Name(protoClass.nameResolver.getString(tp.name)),
          upperBounds = tp.upperBoundList.map { it.toMeta(protoClass) }
        )
      }
    else emptyList()

  private fun proto.Modality.toMeta(protoClass: ClassOrPackageDataWrapper.Class): arrow.meta.Modality? =
    when(this) {
      proto.Modality.FINAL -> Modality.Final
      proto.Modality.OPEN -> TODO()
      proto.Modality.ABSTRACT -> TODO()
      proto.Modality.SEALED -> TODO()
    }

  fun proto.Type.toMeta(protoClass: ClassOrPackageDataWrapper.Class): arrow.meta.Type =
    Type(
      name = Name(value = protoClass.nameResolver.getString(typeParameterName)),
      abbreviatedType = if (hasAbbreviatedType()) abbreviatedType.toMeta(protoClass) else null,
      arguments = argumentList.map { it.toMeta(protoClass) },
      flexibleUpperBound = null,
      nullable = false,
      outerType = null,
      typeAlias = null,
      typeParameter = null
    )

  fun proto.Type.Argument.toMeta(protoClass: ClassOrPackageDataWrapper.Class): arrow.meta.TypeArgument =
    TypeArgument(
      name = Name(type.extractFullName(protoClass)),
      projection = projection.toMeta(protoClass)
    )

  fun proto.Type.Argument.Projection.toMeta(protoClass: ClassOrPackageDataWrapper.Class): arrow.meta.Variance =
    when (this) {
      proto.Type.Argument.Projection.IN -> Variance.In
      proto.Type.Argument.Projection.OUT -> Variance.Out
      proto.Type.Argument.Projection.INV -> Variance.Inv
      proto.Type.Argument.Projection.STAR -> Variance.Star
    }

  private fun annotations(protoClass: ClassOrPackageDataWrapper.Class, element: TypeElement): List<arrow.meta.Annotation> =
    if (protoClass.classProto.hasAnnotations)
      element.annotationMirrors.map(::annotation)
    else emptyList()

  private fun annotation(am: AnnotationMirror): arrow.meta.Annotation =
    arrow.meta.Annotation(
      name = Name((am.annotationType.asElement().simpleName.toString())),
      args = am.elementValues.entries.map {
        val argName: String = it.key.simpleName.toString()
        val argValue: Any = it.value.value
        val argType = it.key.returnType
        arrow.meta.Argument(
          name = Name(argName),
          defaultValue = argValue,
          type = Type(
            name = Name(value = argType.descriptor),
            abbreviatedType = null,
            arguments = emptyList(),
            flexibleUpperBound = null,
            nullable = false,
            outerType = null,
            typeAlias = null,
            typeParameter = null
          )
        )
      }
    )

  private fun classEncoding(protoClass: ClassOrPackageDataWrapper.Class): ClassEncoding =
    when (protoClass.classProto.classKind) {
      proto.Class.Kind.CLASS -> ClassEncoding.Class
      proto.Class.Kind.INTERFACE -> ClassEncoding.Interface
      proto.Class.Kind.ENUM_CLASS -> ClassEncoding.EnumClass
      proto.Class.Kind.ENUM_ENTRY -> ClassEncoding.EnumEntry
      proto.Class.Kind.ANNOTATION_CLASS -> ClassEncoding.AnnotationClass
      proto.Class.Kind.OBJECT -> ClassEncoding.Object
      proto.Class.Kind.COMPANION_OBJECT -> ClassEncoding.CompanionObject
    }
  
  private val activeFlags: List<Flags.BooleanFlagField> = listOf(
    Flags.IS_OPERATOR,
    Flags.IS_INFIX,
    Flags.IS_INLINE,
    Flags.IS_TAILREC,
    Flags.IS_EXTERNAL_CLASS,
    Flags.IS_DELEGATED,
    Flags.IS_EXPECT_CLASS,
    Flags.IS_SUSPEND,
    Flags.IS_CROSSINLINE,
    Flags.IS_NOINLINE,
    Flags.IS_LATEINIT
  )

  private fun flags(flags: Int): List<arrow.meta.Flag> {
    fun getFlag(currentFlag: Int): List<arrow.meta.Flag> = when {
      Flags.IS_OPERATOR.get(currentFlag) -> listOf(Flag.Operator)
      Flags.IS_INFIX.get(currentFlag) -> listOf(Flag.Infix)
      Flags.IS_INLINE.get(currentFlag) -> listOf(Flag.Inline)
      Flags.IS_TAILREC.get(currentFlag) -> listOf(Flag.Tailrec)
      Flags.IS_EXTERNAL_CLASS.get(currentFlag) -> listOf(Flag.External)
      Flags.IS_DELEGATED.get(currentFlag) -> listOf(Flag.Delegated)
      Flags.IS_EXPECT_CLASS.get(currentFlag) -> listOf(Flag.Expect)
      Flags.IS_SUSPEND.get(currentFlag) -> listOf(Flag.Suspend)
      Flags.IS_CROSSINLINE.get(currentFlag) -> listOf(Flag.CrossInline)
      Flags.IS_NOINLINE.get(currentFlag) -> listOf(Flag.NoInline)
      Flags.IS_LATEINIT.get(currentFlag) -> listOf(Flag.LateInit)
      else -> emptyList()
    }
   return activeFlags.flatMap { field ->
      val containsFlag: Boolean = field.get(flags)
      if (containsFlag) {
        val flag = field.toFlags(true)
        getFlag(flag)
      } else emptyList()
    }
  }

}





