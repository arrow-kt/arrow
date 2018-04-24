package arrow.renzu

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.fullName
import arrow.common.utils.removeBackticks
import arrow.common.utils.simpleName
import arrow.instances.AnnotatedInstance
import javaslang.Tuple2
import java.io.File

data class Instance(val target: AnnotatedInstance) {
  val name = target.classElement.simpleName
  val receiverTypeName = target.dataType.nameResolver.getString(target.dataType.classProto.fqName)
    .replace("/", ".")
  val receiverTypeSimpleName = receiverTypeName.substringAfterLast(".")

  fun implementedTypeclasses(): List<ClassOrPackageDataWrapper.Class> =
    target.superTypes.filter { it.fullName.removeBackticks().contains("typeclass") }
}

data class TypeClass(val simpleName: String, private val classWrapper: ClassOrPackageDataWrapper.Class) {
  override fun equals(other: Any?): Boolean =
    if (other !is TypeClass) false else simpleName == other.simpleName

  override fun hashCode(): Int = simpleName.hashCode()
}

typealias ParentTypeClass = TypeClass
typealias Instances = List<String>

class RenzuGenerator(private val generatedDir: File, annotatedList: List<AnnotatedInstance>) {

  private val typeclassTree: MutableMap<TypeClass, Tuple2<Instances, ParentTypeClass>> =
    normalizeTypeclassTree(annotatedList.map { Instance(it) })

  private fun normalizeTypeclassTree(instances: List<Instance>)
    : MutableMap<TypeClass, Tuple2<Instances, ParentTypeClass>> =
    instances.fold(mutableMapOf()) { acc, instance ->
      instance.implementedTypeclasses().forEach { tc ->
        acc.computeIfPresent(TypeClass(tc.simpleName, tc),
          { _, value ->
            Tuple2(value._1 + listOf(instance.receiverTypeSimpleName), TypeClass(tc.simpleName, tc))
          })
        acc.putIfAbsent(TypeClass(tc.simpleName, tc),
          Tuple2(listOf(instance.receiverTypeSimpleName), TypeClass(tc.simpleName, tc)))
      }
      acc
    }

  fun generate() {
    val file = File(generatedDir, "arrow-infographic.txt")
    val elementsToGenerate: List<String> = genDiagramRelations(typeclassTree)
    val source: String = elementsToGenerate.joinToString(separator = "\n")
    file.writeText(source)
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
   * [<typeclass>Monad]<-[<instances>Instances|NonEmptyList|Option|OptionT|SequenceK|State|StateT|Try|Either|EitherT|Eval|Id]
   * [<typeclass>Applicative]<-[Something 2]
   * [<typeclass>Applicative]<-[Something 3]
   */
  private fun genDiagramRelations(typeclassTree: MutableMap<TypeClass, Tuple2<Instances, ParentTypeClass>>)
    : List<String> {
    val relations = mutableListOf<String>()
    relations += listOf("#font: Menlo") +
      listOf("#fontSize: 15") +
      listOf("#arrowSize: 1") +
      listOf("#bendSize: 0.3") +
      listOf("#lineWidth: 2") +
      listOf("#padding: 8") +
      listOf("#zoom: 1") +
      listOf("#fill: #64B5F6") +
      listOf("#.typeclass: fill=#64B5F6 visual=database bold") +
      listOf("#.instances: fill=#B9F6CA visual=class italic bold dashed")

    typeclassTree.forEach {
      val typeClass = it.key
      val instances = it.value._1
      val parentTypeClass = it.value._2

      if (typeClass.simpleName != parentTypeClass.simpleName) {
        relations += "[<typeclass>${parentTypeClass.simpleName}]<-[<typeclass>${typeClass.simpleName}]"
      }

      relations += "[<typeclass>${typeClass.simpleName}]<-[<instances>Instances|${instances.joinToString(separator = "|")}]"
    }

    return relations.toList()
  }
}
