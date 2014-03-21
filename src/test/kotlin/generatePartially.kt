/*
 * Copyright 2013 Mario Arias
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.funktionale.partials.*

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 29/03/13
 * Time: 21:33
 */

val paramType = {(i: Int) -> "P${i}" }

val param = {(i: Int) -> "p${i}" }

val paramWithParenthesis = {(i: Int) -> "(p${i})" }

val paramDeclaration = {(i: Int) -> "p${i}: P${i}" }

val completeParams = {(i: Int)-> (1..i).mapTo(arrayListOf<String>(), paramType).makeString() }

val callFunction = {(i: Int, paramTemplate: (Int)-> String, separator: String)-> (1..i).mapTo(arrayListOf<String>(), paramTemplate).makeString(separator = separator) }

val callFunctionParams = callFunction.partially2(param).partially2(", ")

val callFunctionParamsWithParenthesis = callFunction.partially2(paramWithParenthesis).partially2("")

val closed = {(i: Int) -> (1..i).mapTo(arrayListOf<String>()) { "}" }.makeString(separator = " ") }

fun partially() {

    val filteredParams = {(i: Int, filtered: Int)-> (1..i).filterNotTo(arrayListOf<Int>()) { it.equals(filtered) }.mapTo(arrayListOf<String>(), paramType).makeString() }

    val filteredDeclaredParams = {(i: Int, filtered: Int)-> (1..i).filterNotTo(arrayListOf<Int>()) { it.equals(filtered) }.mapTo(arrayListOf<String>(), paramDeclaration).makeString() }

    (1..22).forEach { i ->

        (1..i).forEach { j ->
            println("""
public fun <${completeParams(i)}, R> Function${i}<${completeParams(i)}, R>.partially${j}(p${j}: P${j}): (${filteredParams(i, j)}) -> R {
    return {(${filteredDeclaredParams(i, j)}) -> this(${callFunctionParams(i)})}
}

                """)
        }

    }
}


fun currying() {

    val returnType = {(i: Int) -> (1..i).mapTo(arrayListOf<String>(), paramType).makeString(separator = ") -> (", prefix = "(", postfix = ")") }

    val returned = {(i: Int) -> (1..i).mapTo(arrayListOf<String>(), paramDeclaration).makeString(separator = ") -> {(", prefix = "(", postfix = ")") }


    (2..22).forEach { i ->
        println("""
public fun<${completeParams(i)}, R> Function${i}<${completeParams(i)}, R>.curried(): ${returnType(i)} -> R {
    return {${returned(i)}  -> this(${callFunctionParams(i)}) ${closed(i)}
}
        """)
    }
}

fun flip() {

    val returnType = {(i: Int) -> (i downTo 1).mapTo(arrayListOf<String>(), paramType).makeString(separator = ") -> (", prefix = "(", postfix = ")") }

    val returned = {(i: Int) -> (i downTo 1).mapTo(arrayListOf<String>(), paramDeclaration).makeString(separator = ") -> {(", prefix = "(", postfix = ")") }

    fun receptorType(i: Int, finalType: String): String {
        if (i > 0) {
            return receptorType((i - 1), "Function1<P${i}, $finalType>")
        } else {
            return finalType
        }
    }

    (2..22).forEach { i ->
        println("""
public fun<${completeParams(i)}, R> ${receptorType(i, "R")}.flip(): ${returnType(i)} -> R {
    return {${returned(i)}  -> this${callFunctionParamsWithParenthesis(i)} ${closed(i)}
}
        """)
    }
}

fun reverse() {

    val returnType = {(i: Int) -> (i downTo 1).mapTo(arrayListOf<String>(), paramType).makeString(separator = ", ", prefix = "(", postfix = ")") }

    val returned = {(i: Int) -> (i downTo 1).mapTo(arrayListOf<String>(), paramDeclaration).makeString(separator = ", ", prefix = "(", postfix = ")") }


    (2..22).forEach { i ->
        println("""
    public fun<${completeParams(i)}, R> Function${i}<${completeParams(i)}, R>.reversed(): ${returnType(i)} -> R {
        return {${returned(i)}  -> this(${callFunctionParams(i)}) }
    }
            """)
    }
}

val anies = {(i: Int) ->
    (1..(i + 1)).mapTo(arrayListOf<String>()) {
        "Any"
    }.makeString(separator = ", ")
}

private fun javaFunClasses(i: Int): String {


    return (0..i).mapTo(arrayListOf<String>()) { i ->
        "javaClass<Function${i}<${anies(i)}>>()"
    }.makeString(separator = ",\n")
}

fun functionClasses() {
    println("""
    return array(${javaFunClasses(22)})
""")
}

val callParamArray = {(i: Int) ->
    (0..(i - 1)).mapTo(arrayListOf<String>()) {
        "args[${it}]"
    }.makeString(separator = ", ")
}


fun callFunctions() {
    println((1..22).mapTo(arrayListOf<String>()) { i ->
        "${i} -> (function!! as Function${i}<${anies(i)}>)(${callParamArray(i)})"
    }.makeString(separator = "\n"))
}

fun String.capitalizeFirstCharacter(): String {
    val firstCharacter = this.get(0).toString().capitalize()
    return firstCharacter + this.substring(1)
}

fun functionsForResultSet(vararg names: String) {
    names.forEach { name ->
        println(
                """
public val ${name}: GetFieldsToken<${name.capitalizeFirstCharacter()}?>
        get(){
            return GetFieldsToken(
                    { columnName -> get${name.capitalizeFirstCharacter()}(columnName) },
                    { columnIndex -> get${name.capitalizeFirstCharacter()}(columnIndex) })
        }
                """
        )
    }
}

//functionsForResultSet("boolean", "byte", "bytes", "characterStream", "clob", "date", "double", "float", "int", "long", "nCharacterStream", "nClob", "nString", "object", "ref", "rowId", "short", "SQLXML", "string", "time", "timestamp", "URL")

fun f(): Array<String> {
    val xs = setOf("foo", "bar")
    return xs.toArray()
}

fun main(args: Array<String>) {
    setOf("foo", "bar").toArray().forEach { it -> println(it as String) }
}

fun<T> Set<T>.toArray(): Array<T> {
    return toArray()
}