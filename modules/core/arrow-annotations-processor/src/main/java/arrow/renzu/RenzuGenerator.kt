package arrow.renzu

import arrow.common.messager.log
import arrow.common.utils.*
import arrow.instances.AnnotatedInstance
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.supertypes
import java.io.File
import javax.lang.model.element.Name

data class Instance(val target: AnnotatedInstance) {
  val name: Name = target.classElement.simpleName
  val arrowModule: String = target.classOrPackageProto.`package`.substringAfterLast(".")
}

data class TypeClass(val processor: RenzuProcessor, val simpleName: String, val classWrapper: ClassOrPackageDataWrapper.Class) {
  val arrowModule: String = classWrapper.`package`.substringAfterLast(".")

  override fun equals(other: Any?): Boolean =
    if (other !is TypeClass) false else simpleName == other.simpleName

  override fun hashCode(): Int = simpleName.hashCode()

  /**
   * Returns the typeclasses that are a direct parent of this one, for composing the typeclass
   * relationship on the tree.
   */
}

fun parentTypeClasses(processor: RenzuProcessor, current: ClassOrPackageDataWrapper.Class): List<TypeClass> {
  val typeTable = TypeTable(current.classProto.typeTable)
  val interfaces = current.classProto.supertypes(typeTable).map {
    it.extractFullName(current)
  }.filter {
    it != "`kotlin`.`Any`"
  }
  val parentInterfaces: List<ClassOrPackageDataWrapper.Class> = listOf()
  return when {
    interfaces.isEmpty() -> parentInterfaces.map {
      TypeClass(processor, it.simpleName, it)
    }
    else -> {
      interfaces.flatMap { i ->
        try {
          val className = i.removeBackticks().substringBefore("<")
          val typeClassElement = processor.elementUtils.getTypeElement(className)
          val parentInterface = processor.getClassOrPackageDataWrapper(typeClassElement) as ClassOrPackageDataWrapper.Class
          if (i.removeBackticks().contains("typeclass")) {
            parentInterfaces + parentInterface
          } else
            parentInterfaces
        } catch (_: Throwable) {
          emptyList<ClassOrPackageDataWrapper.Class>()
        }
      }.map {
        TypeClass(processor, it.simpleName, it)
      }
    }
  }
}

typealias ParentTypeClass = TypeClass
typealias Instances = Set<Instance>

class RenzuGenerator(
  private val processor: RenzuProcessor,
  annotatedList: List<AnnotatedInstance>,
  val isolateForTests: Boolean = false) {

  private val typeclassTree: Map<TypeClass, Pair<Instances, Set<ParentTypeClass>>> =
    normalizeTypeclassTree(annotatedList.map { Instance(it) })

  private fun normalizeTypeclassTree(instances: List<Instance>)
    : Map<TypeClass, Pair<Instances, Set<ParentTypeClass>>> =
    instances.fold(mapOf()) { acc, instance ->
      parentTypeClasses(processor, instance.target.classOrPackageProto).fold(acc) { acc2, typeclass ->
        val parentTypeClasses = parentTypeClasses(processor, typeclass.classWrapper)

        val value = acc2[typeclass]
        if (value != null) {
          acc2.filterKeys { it != typeclass } + mapOf(typeclass to Pair(
            value.first + setOf(instance),
            value.second + parentTypeClasses))
        } else {
          acc2 + mapOf(typeclass to Pair(
            setOf(instance),
            parentTypeClasses.toSet()))
        }
      }.toSortedMap(Comparator { o1, o2 -> o1.simpleName.compareTo(o2.simpleName) })
    }

  fun generate() {

    val topLevelFiles = setOf("settings.gradle", "pom.xml", "build.xml", "Build.kt", "settings.gradle.kt")

    val generatedDir = if (isolateForTests)
      File("./infographic")
    else
      File("${recurseFilesUpwards(topLevelFiles).absolutePath}/infographic")
        .also { it.mkdirs() }

    val file = File(generatedDir, "arrow-infographic.txt")
    if (!file.exists()) {
      val globalStyles =
        """
        |#font: Menlo
        |#fontSize: 10
        |#arrowSize: 1
        |#bendSize: 0.3
        |#lineWidth: 2
        |#padding: 8
        |#zoom: 1
        |#fill: #64B5F6
        |#.typeclasses: fill=#64B5F6 visual=database bold
        |#.instances: fill=#B9F6CA visual=class italic bold dashed
        """.trimMargin()

      file.appendText(globalStyles)
    }

    val fileRelations = file.readLines()
    val generatedRelations = genGeneratedRelations(typeclassTree)

    val notCollidingFileRelations: List<String> = fileRelations
      .filterNot { rel ->
        rel.isInstanceRelation() && generatedRelations.find { it.contains(rel.instancesBlockName()) } != null
      }.sorted()

    val notCollidingGeneratedRelations: List<String> = generatedRelations
      .toSet()
      .filterNot { fileRelations.contains(it) }
      .filterNot { rel ->
        rel.isInstanceRelation() && fileRelations.find { it.contains(rel.instancesBlockName()) } != null
      }.sorted()

    val source = (notCollidingFileRelations +
      notCollidingGeneratedRelations +
      composedCollidingRelations(fileRelations, generatedRelations)).joinToString(separator = "\n")

    if (source != "\n") {
      file.writeText(source, Charsets.UTF_8)
    }

    processor.log("arrow-infographic generated: " + file.path)
  }

  private fun composedCollidingRelations(fileRelations: List<String>, generatedRelations: List<String>): List<String> {
    val collidingRelations = fileRelations.filter { rel ->
      rel.isInstanceRelation() && generatedRelations.find { it.contains(rel.instancesBlockName()) } != null
    }.sorted()

    return collidingRelations.map { collidingRelation ->
      val collidingRelationInstances = collidingRelation.instanceNames()
      val generatedCollidingRelationInstances = generatedRelations.find {
        it.contains(collidingRelation.instancesBlockName())
      }!!.instanceNames().sorted()

      val composedInstances = (collidingRelationInstances + generatedCollidingRelationInstances)
        .toSet()
        .joinToString("|")

      "[<typeclasses>${collidingRelation.typeClassName()}]<-[<instances>${collidingRelation.typeClassName()
      } Instances|$composedInstances]"
    }
  }

  private fun String.isInstanceRelation(): Boolean = this.contains("Instances")

  private fun String.instancesBlockName(): String = this
    .substringAfter("[<instances>")
    .substringBefore("|")

  private fun String.instanceNames(): List<String> = this
    .substringAfter("Instances|")
    .substringBeforeLast("]")
    .split("|")

  private fun String.typeClassName(): String = this
    .substringAfter("[<typeclasses>")
    .substringBefore("]")

  /**
   * Returns the UML text for the diagram to be rendered using nomnoml.
   *
   * Sample format for the output:
   *
   * [<typeclass>Functor]<-[<typeclass>Applicative]
   * [<typeclass>Applicative]<-[<typeclass>Monad]
   * [<typeclass>Monad]<-[<instances>Monad Instances|NonEmptyList|Option|OptionT|SequenceK|State|StateT|Try|Either|EitherT|Eval|Id]
   * [<typeclass>Applicative]<-[Something 2]
   * [<typeclass>Applicative]<-[Something 3]
   */
  private fun genGeneratedRelations(typeclassTree: Map<TypeClass, Pair<Instances, Set<ParentTypeClass>>>)
    : List<String> =
    typeclassTree.flatMap {
      val typeClass = it.key
      val instances = it.value.first
      val parentTypeClasses = it.value.second

      parentTypeClasses.filter { typeClass.simpleName != it.simpleName }.map {
        "[<typeclasses>${it.simpleName}]<-[<typeclasses>${typeClass.simpleName}]"
      } +
        "[<typeclasses>${typeClass.simpleName}]<-[<instances>${typeClass.simpleName} Instances|${instances
          .sortedBy { it.name.toString() }
          .joinToString(separator = "|") { it.name.toString() }}]"
    }
}
