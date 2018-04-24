package arrow.renzu

import arrow.common.messager.log
import arrow.common.messager.logW
import arrow.common.utils.*
import arrow.instances.AnnotatedInstance
import javaslang.Tuple2
import org.jetbrains.kotlin.serialization.deserialization.TypeTable
import org.jetbrains.kotlin.serialization.deserialization.supertypes
import java.io.File
import javax.lang.model.element.Name

data class Instance(val target: AnnotatedInstance) {
  val name: Name = target.classElement.simpleName
  val arrowModule: String = target.classOrPackageProto.`package`.substringAfterLast(".")

  /**
   * Returns any implemented typeclasses at any level crawling the instance hierarchy.
   */
  fun implementedTypeclasses(): List<ClassOrPackageDataWrapper.Class> =
    target.superTypes.filter { it.fullName.removeBackticks().contains("typeclass") }
}

data class TypeClass(val processor: RenzuProcessor, val simpleName: String, private val classWrapper: ClassOrPackageDataWrapper.Class) {
  val arrowModule: String = classWrapper.`package`.substringAfterLast(".")

  override fun equals(other: Any?): Boolean =
    if (other !is TypeClass) false else simpleName == other.simpleName

  override fun hashCode(): Int = simpleName.hashCode()

  /**
   * Returns the typeclasses that are a direct parent of this one, for composing the typeclass
   * relationship on the tree.
   */
  fun parentTypeClasses(current: ClassOrPackageDataWrapper.Class): List<TypeClass> {
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
}

typealias ParentTypeClass = TypeClass
typealias Instances = Set<Instance>

class RenzuGenerator(private val processor: RenzuProcessor,
                     private val generatedDir: File,
                     annotatedList: List<AnnotatedInstance>) {

  private val typeclassTree: MutableMap<TypeClass, Tuple2<Instances, Set<ParentTypeClass>>> =
    normalizeTypeclassTree(annotatedList.map { Instance(it) })

  private fun normalizeTypeclassTree(instances: List<Instance>)
    : MutableMap<TypeClass, Tuple2<Instances, Set<ParentTypeClass>>> =
    instances.fold(mutableMapOf()) { acc, instance ->
      instance.implementedTypeclasses().forEach { tc ->
        val typeclass = TypeClass(processor, tc.simpleName, tc)
        val parentTypeClasses = typeclass.parentTypeClasses(tc)

        acc.computeIfPresent(typeclass,
          { _, value ->
            Tuple2(
              value._1 + setOf(instance),
              value._2 + parentTypeClasses)
          })
        acc.putIfAbsent(typeclass,
          Tuple2(setOf(instance), parentTypeClasses.toSet()))
      }
      acc
    }

  fun generate() {
    val file = File(generatedDir, "arrow-infographic.txt")
    val elementsToGenerate: List<String> = genDiagramRelations(typeclassTree)
    val source: String = elementsToGenerate.joinToString(separator = "\n")
    file.writeText(source)

    processor.log("arrow-infographic generated: " + file.path)
  }

  /**
   * Returns the UML text for the diagram to be rendered using nomnoml.
   *
   * Sample format for the output:
   *
   * #font: Menlo
   * #fontSize: 15
   * #arrowSize: 1
   * #bendSize: 0.3
   * #lineWidth: 2
   * #padding: 8
   * #zoom: 1
   * #fill: #64B5F6
   * #.typeclass: fill=#64B5F6 visual=database bold
   * #.instances: fill=#B9F6CA visual=class italic bold dashed

   * [<typeclass>Functor]<-[<typeclass>Applicative]
   * [<typeclass>Applicative]<-[<typeclass>Monad]
   * [<typeclass>Monad]<-[<instances>Monad Instances|NonEmptyList|Option|OptionT|SequenceK|State|StateT|Try|Either|EitherT|Eval|Id]
   * [<typeclass>Applicative]<-[Something 2]
   * [<typeclass>Applicative]<-[Something 3]
   */
  private fun genDiagramRelations(typeclassTree: MutableMap<TypeClass, Tuple2<Instances, Set<ParentTypeClass>>>)
    : List<String> {
    val relations = mutableListOf<String>()
    relations += listOf("#font: Menlo") +
      listOf("#fontSize: 15") +
      listOf("#arrowSize: 1") +
      listOf("#bendSize: 0.3") +
      listOf("#lineWidth: 2") +
      listOf("#padding: 8") +
      listOf("#zoom: 1") +
      listOf("#fill: #64B5F6")

    val modules = typeclassTree.flatMap { setOf(it.key.arrowModule) + it.value._1.map { it.arrowModule } }.toSet()
    modules.forEach {
      relations += listOf("#.${normalizeModule(it)}: ${getModuleStyle(it)}")
    }

    typeclassTree.forEach {
      val typeClass = it.key
      val instances = it.value._1
      val parentTypeClasses = it.value._2

      parentTypeClasses.filter { typeClass.simpleName != it.simpleName }.forEach {
        relations += "[<typeclasses>${it.simpleName}]<-[<typeclasses>${typeClass.simpleName}]"
      }

      relations += "[<typeclasses>${typeClass.simpleName}]<-[<instances>${typeClass.simpleName} Instances|${instances
        .map { it.name.toString() }.joinToString(separator = "|")}]"
    }

    return relations.toList()
  }
}
