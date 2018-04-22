package arrow.renzu

import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.fullName
import arrow.common.utils.removeBackticks
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

typealias TypeClass = ClassOrPackageDataWrapper.Class
typealias ParentTypeClass = ClassOrPackageDataWrapper.Class
typealias Instances = List<String>

class RenzuGenerator(private val generatedDir: File, annotatedList: List<AnnotatedInstance>) {

  private val instances: List<Instance> = annotatedList.map { Instance(it) }
  private val typeclassTree: MutableMap<TypeClass, Tuple2<Instances, ParentTypeClass>> =
    normalizeTypeclassTree(instances)

  private fun normalizeTypeclassTree(instances: List<Instance>)
    : MutableMap<TypeClass, Tuple2<Instances, ParentTypeClass>> =
    instances.fold(typeclassTree) { acc, instance ->
      instance.implementedTypeclasses().forEach { tc ->
        acc.computeIfPresent(tc, { key, value ->
          Tuple2(value._1 + listOf(instance.receiverTypeSimpleName), key)
        })
        acc.putIfAbsent(tc, Tuple2(listOf(instance.receiverTypeSimpleName), tc))
      }
      acc
    }

  fun generate() {
    val file = File(generatedDir, "arrow-infographic.kt")

    instances.forEach {
      val elementsToGenerate: List<String> = genDiagramRelations(typeclassTree)
      val source: String = elementsToGenerate.joinToString(separator = "\n")
      file.writeText(source)
    }
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

      if (typeClass.fullName != parentTypeClass.fullName) {
        relations += "[<typeclass>${parentTypeClass.fullName}]<-[<typeclass>${typeClass.fullName}]"
      }

      relations += "[<typeclass>${typeClass.fullName}]<-[<instances>Instances|${instances.joinToString(separator = "|")}]"
    }

    return relations.toList()
  }
}
