package arrow.generic

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class Person(val name: String, val age: Int, val p: Person2? = null)

@Serializable
data class Person2(val name: String, val age: Int, val p: Person2? = null)

fun main() {
  val a = Generic.encode(Person(name = "X", age = 98, p = Person2(name = "Y", age = 99, p = null)))
  val b = Generic.encode(1)
  val c = Generic.encode(Pair(1, "Hello, World!"))

  println(a)
  println(b)
  println(c)

  val tree: Branch<String> =
    Branch(Leaf("1"), Branch(Leaf("2"), Leaf("3")))

  val generic = Generic.encode(tree, serializersModule = SerializersModule {
    polymorphic(Any::class, Int::class, Int.serializer())
    polymorphic(Any::class, String::class, String.serializer())
    polymorphic(Any::class, Char::class, Char.serializer())
    polymorphic(Any::class, Float::class, Float.serializer())
    polymorphic(Any::class, Double::class, Double.serializer())
    polymorphic(Any::class, Byte::class, Byte.serializer())
    polymorphic(Any::class, Short::class, Short.serializer())

    polymorphic(Any::class, UByte::class, UByte.serializer())
    polymorphic(Any::class, UShort::class, UShort.serializer())
    polymorphic(Any::class, UInt::class, UInt.serializer())
    polymorphic(Any::class, ULong::class, ULong.serializer())
  })

  println(generic)
}

@Serializable
sealed class Tree<A>

@Serializable
data class Branch<A>(val left: Tree<A>, val right: Tree<A>) : Tree<A>()

@Serializable
data class Leaf<A>(val value: A) : Tree<A>()
