package arrow.generic

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

fun main() {
  val a = Person(name = "X", age = 98, p = Person2(name = "Y", age = 99, p = null)).generic()
  val b = 1.generic()
  val c = Pair(1, "Hello, World!").generic()

  println(a)
  println(b)
  println(c)

  val tree: Branch<String> =
    Branch(Leaf("1"), Branch(Leaf("2"), Leaf("3")))

  val generic = tree.generic(serializersModule = SerializersModule {
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

  println(tree)
}

@Serializable
sealed class Tree<A>

@Serializable
data class Branch<A>(val left: Tree<A>, val right: Tree<A>) : Tree<A>()

@Serializable
data class Leaf<A>(val value: A) : Tree<A>()
