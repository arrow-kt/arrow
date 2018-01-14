package arrow.generic

import arrow.*
import arrow.core.*
import arrow.generic.typeclasses.Generic
import arrow.typeclasses.*

/**
 * An Heterogeneous list of values that preserves type information
 */
sealed class HList {
    abstract fun size(): Int
}
data class HCons<out H, out T : HList>(val head: H, val tail: T) : HList() {
    override fun size(): Int = 1 + tail.size()
}
object HNil : HList() {
    override fun size(): Int = 0
}

/**
 * HList supported arity
 */
typealias HList1<A> = HCons<A, HNil>
typealias HList2<A, B> = HCons<A, HCons<B, HNil>>
typealias HList3<A, B, C> = HCons<A, HCons<B, HCons<C, HNil>>>

fun <A> hListOf(a: A): HList1<A> = HList1(a, HNil)

fun <A, B> hListOf(a: A, b: B): HList2<A, B> = HList2(a, HCons(b, HNil))

fun <A, B, C> hListOf(a: A, b: B, c: C): HList3<A, B, C> = HList3(a, HCons(b, HCons(c, HNil)))

fun <A, B> Tuple2<A, B>.toHList(): HList2<A, B> =
        hListOf(this.a, this.b)

fun <A, B, C> Tuple3<A, B, C>.toHList(): HList3<A, B, C> =
        hListOf(this.a, this.b, this.c)

fun <A, B> HList2<A, B>.tupled(): Tuple2<A, B> =
        Tuple2(this.head, this.tail.head)

fun <A, B, C> HList3<A, B, C>.tupled(): Tuple3<A, B, C> =
        Tuple3(this.head, this.tail.head, this.tail.tail.head)

@isos data class Person(val name: String, val age: Int) {
    companion object
}

fun Person.toHList(): HList2<String, Int> =
        hListOf(this.name, this.age)

fun Person.tupled(): Tuple2<String, Int> =
        Tuple2(this.name, this.age)

fun Person.Companion.fromTuple(t: Tuple2<String, Int>): Person =
        Person(t.a, t.b)

fun Person.Companion.fromHList(t: HList2<String, Int>): Person =
        Person.fromTuple(t.tupled())

typealias PersonGeneric = Tuple2<String, Int>

@instance(Person::class)
interface PersonGenericInstance : Generic<Person, PersonGeneric> {
    override fun from(r: PersonGeneric): Person = Person.fromTuple(r)

    override fun to(t: Person): PersonGeneric = t.tupled()
}

fun <B> Person.foldLabelled(f: (Tuple2<String, String>, Tuple2<String, Int>) -> B): B {
    val t = tupledLabelled()
    return f(t.a, t.b)
}

fun Person.tupledLabelled(): Tuple2<Tuple2<String, String>, Tuple2<String, Int>> =
        ("name" toT name) toT ("age" toT age)

@instance(Tuple2::class)
interface Tuple2SemigroupInstance<A, B> : Semigroup<Tuple2<A, B>> {

    fun SA(): Semigroup<A>

    fun SB(): Semigroup<B>

    override fun combine(a: Tuple2<A, B>, b: Tuple2<A, B>): Tuple2<A, B> =
            Tuple2(SA().combine(a.a, b.a),
                    SB().combine(a.b, b.b))
}

fun combine(person: Person, other: Person): Person =
        Person.fromTuple(semigroup<Tuple2<String, Int>>().combine(person.tupled(), other.tupled()))

fun Person.semigroup(): Semigroup<Person> =