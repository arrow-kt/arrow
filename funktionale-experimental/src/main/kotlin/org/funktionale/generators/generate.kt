/*
 * Copyright 2013 - 2016 Mario Arias
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

package org.funktionale.generators

import org.funktionale.memoization.memoize
import org.funktionale.partials.invoke

val paramType: (Int) -> String = { i: Int -> "P$i" }.memoize()

val outParamType: (Int) -> String = { i: Int -> "out P$i" }.memoize()

val param = { i: Int -> "p$i" }.memoize()

val paramWithParenthesis = { i: Int -> "(p$i)" }.memoize()

val paramDeclaration = { i: Int -> "p$i: P$i" }.memoize()

val paramDeclarationWithVal = { i: Int -> "val p$i: P$i" }.memoize()

fun buildCompleteParams(paramType: (Int) -> String): (Int) -> String = { i: Int -> (1..i).mapTo(arrayListOf(), paramType).joinToString() }.memoize()

val completeParams = buildCompleteParams(paramType)

val completeOutParams = buildCompleteParams(outParamType)

val callFunction = { i: Int, paramTemplate: (Int) -> String, separator: String -> (1..i).mapTo(arrayListOf(), paramTemplate).joinToString(separator = separator) }

val callFunctionParams = callFunction(p2 = param)(p2 = ", ")

val callFunctionParamsWithParenthesis = callFunction(p2 = paramWithParenthesis)(p2 = "")

val closed = { i: Int -> (1..i).mapTo(arrayListOf()) { "}" }.joinToString(separator = " ") }

val filteredParams = { i: Int, filtered: Int -> (1..i).filterNotTo(arrayListOf()) { it == filtered }.mapTo(arrayListOf(), paramType).joinToString() }

val filteredDeclaredParams = { i: Int, filtered: Int -> (1..i).filterNotTo(arrayListOf()) { it == filtered }.mapTo(arrayListOf(), paramDeclaration).joinToString() }

fun partially() {

	for (1..22).forEach { i ->

		(1..i).forEach { j ->
			println(
					"""
fun <${completeParams(i)}, R> ((${completeParams(i)}) -> R).partially$j(${paramDeclaration(j)}): (${filteredParams(i, j)}) -> R {
    return { ${filteredDeclaredParams(i, j)} -> this(${callFunctionParams(i)}) }
}""")
		}

	}
}

fun newPartially() {

	val partials: (Int, Int) -> String = { i: Int, parameter: Int ->
		(1..i).joinToString { num ->
			if (num == parameter) {
				paramDeclaration(num)
			} else {
				"partial$num: Partial<P$num> = partial()"
			}
		}
	}

	(2..22).forEach { i ->
		(1..i).forEach { j ->
			println("""
operator @Suppress("UNUSED_PARAMETER") fun <${completeParams(i)}, R> ((${completeParams(i)}) -> R).invoke(${partials(i, j)}): (${filteredParams(i, j)}) -> R {
    return { ${filteredDeclaredParams(i, j)} -> this(${callFunctionParams(i)}) }
}""")
		}
	}
}

fun currying() {

	val returnType = { i: Int -> (1..i).mapTo(arrayListOf(), paramType).joinToString(separator = ") -> (", prefix = "(", postfix = ")") }

	val returned = { i: Int -> (1..i).mapTo(arrayListOf(), paramDeclaration).joinToString(separator = " -> { ") }

	(2..22).forEach { i ->
		println("""
fun <${completeParams(i)}, R> ((${completeParams(i)}) -> R).curried(): ${returnType(i)} -> R {
    return { ${returned(i)}  -> this(${callFunctionParams(i)}) ${closed(i)}
}
        """)
	}
}

fun uncurrying() {

	val receiverType = { i: Int -> (1..i).mapTo(arrayListOf(), paramType).joinToString(separator = ") -> (", prefix = "(", postfix = ")") }

	val returned = { i: Int -> (1..i).mapTo(arrayListOf(), paramDeclaration).joinToString(separator = ", ") }

	(2..22).forEach { i ->
		println("""
fun<${completeParams(i)}, R> (${receiverType(i)} -> R).uncurried(): (${completeParams(i)}) -> R {
    return { ${returned(i)}  -> this${callFunction(i, paramWithParenthesis, "")} }
}
        """)
	}
}

fun flip() {

	val returnType = { i: Int -> (i downTo 1).mapTo(arrayListOf(), paramType).joinToString(separator = ") -> (", prefix = "(", postfix = ")") }

	val returned = { i: Int -> (i downTo 1).mapTo(arrayListOf(), paramDeclaration).joinToString(separator = ") -> {(", prefix = "(", postfix = ")") }

	fun receptorType(i: Int, finalType: String): String {
		return if (i > 0) {
			receptorType((i - 1), "Function1<P$i, $finalType>")
		} else {
			finalType
		}
	}

	(2..22).forEach { i ->
		println("""
public fun <${completeParams(i)}, R> ${receptorType(i, "R")}.flip(): ${returnType(i)} -> R {
    return {${returned(i)}  -> this${callFunctionParamsWithParenthesis(i)} ${closed(i)}
}
        """)
	}
}

fun reverse() {

	val returnType = { i: Int -> (i downTo 1).mapTo(arrayListOf(), paramType).joinToString(separator = ", ", prefix = "(", postfix = ")") }

	val returned = { i: Int -> (i downTo 1).mapTo(arrayListOf(), paramDeclaration).joinToString(separator = ", ") }

	(2..22).forEach { i ->
		println("""
fun<${completeParams(i)}, R> ((${completeParams(i)}) -> R).reverse(): ${returnType(i)} -> R {
	return { ${returned(i)}  -> this(${callFunctionParams(i)}) }
}""")
	}
}

val anies = { i: Int ->
	(1..(i + 1)).mapTo(arrayListOf()) {
		"Any"
	}.joinToString(separator = ", ")
}

private fun javaFunClasses(i: Int): String = (0..i).mapTo(arrayListOf()) { j ->
		"javaClass<Function$j<${anies(j)}>>()"
	}.joinToString(separator = ",\n")

fun functionClasses() {
	println("""
    return array(${javaFunClasses(22)})
""")
}

val callParamArray = { i: Int ->
	(0..(i - 1)).mapTo(arrayListOf()) {
		"args[$it]"
	}.joinToString(separator = ", ")
}

fun callFunctions() {
	println((1..22).mapTo(arrayListOf()) { i ->
		"$i -> (function!! as Function$i<${anies(i)}>)(${callParamArray(i)})"
	}.joinToString(separator = "\n"))
}

fun String.capitalizeFirstCharacter(): String {
	val firstCharacter = this[0].toString().capitalize()
	return firstCharacter + this.substring(1)
}

fun functionsForResultSet(vararg names: String) {
	names.forEach { name ->
		println(
				"""
public val $name: GetFieldsToken<${name.capitalizeFirstCharacter()}?>
        get(){
            return GetFieldsToken(
                    { columnName -> get${name.capitalizeFirstCharacter()}(columnName) },
                    { columnIndex -> get${name.capitalizeFirstCharacter()}(columnIndex) })
        }
                """
		)
	}
}

fun memoizeKeys() {

	fun params(i: Int) = (1..i).mapTo(arrayListOf(), paramDeclarationWithVal).joinToString(", ")

	println((1..22).mapTo(arrayListOf()) { i ->

		"""
private data class MemoizeKey$i<${completeOutParams(i)}, R>(${params(i)}) : MemoizedCall<(${completeParams(i)}) -> R, R> {
    override fun invoke(f: (${completeParams(i)}) -> R) = f(${callFunctionParams(i)})
}
"""

	}.joinToString(separator = ""))
}

fun complement() {
	println((1..22).mapTo(arrayListOf()) { i ->
		"""
fun <${completeParams(i)}> ((${completeParams(i)}) -> Boolean).complement(): (${completeParams(i)}) -> Boolean {
    return { ${buildCompleteParams(paramDeclaration)(i)} -> !this(${buildCompleteParams(param)(i)}) }
}
"""
	}.joinToString(""))
}

fun memoizeFunctions() {

	fun params(i: Int) = (1..i).mapTo(arrayListOf(), paramDeclaration).joinToString(", ")

	println((1..22).mapTo(arrayListOf()) { i ->
		"""
fun <${completeParams(i)}, R> ((${completeParams(i)}) -> R).memoize(): (${completeParams(i)}) -> R {
    return object : (${completeParams(i)}) -> R {
        private val m = MemoizedHandler<((${completeParams(i)}) -> R), MemoizeKey$i<${completeParams(i)}, R>, R>(this@memoize)
        override fun invoke(${params(i)}) = m(MemoizeKey$i(${callFunctionParams(i)}))
    }
}
"""
	}.joinToString(""))
}

//functionsForResultSet("boolean", "byte", "bytes", "characterStream", "clob", "date", "double", "float", "int", "long", "nCharacterStream", "nClob", "nString", "object", "ref", "rowId", "short", "SQLXML", "string", "time", "timestamp", "URL")

fun f(): Array<String> {
	val xs = setOf("foo", "bar")
	return xs.toArray()
}

fun main(args: Array<String>) {
	//setOf("foo", "bar").toArray().forEach { it -> println(it as String) }
	//partially()
	//newPartially()
	//    currying()
	reverse()
	//uncurrying()
	//memoizeFunctions()
	//memoizeKeys()
	//complement()
}

fun <T> Set<T>.toArray(): Array<T> = toArray()