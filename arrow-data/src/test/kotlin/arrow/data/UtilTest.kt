package arrow.data

import arrow.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class UtilTest : UnitSpec() {

    private val add5 = { i: Int -> i + 5 }
    private val multiplyBy2 = { i: Int -> i * 2 }

    val Greeter.receive: SetterOperation<String, String>
        get() {
            return SetterOperationImpl { k, v ->
                this.receiveHello(k, v)
            }
        }

    val Greeter.sayHello: GetterOperation<String, String>
        get() {
            return GetterOperationImpl { k ->
                this.sayHelloTo(k)
            }
        }

    private fun applyTwoFunctions(i: Int, firstFunction: (Int) -> Int, secondFunction: (Int) -> Int): Int {
        val x = firstFunction(i)
        return secondFunction(x)
    }

    init {

        "testIdentity" {

            applyTwoFunctions(2, add5, multiplyBy2) shouldBe 14

            applyTwoFunctions(2, add5, ::identity) shouldBe 7

            applyTwoFunctions(2, ::identity, ::identity) shouldBe 2
        }

        "testConstant" {

            applyTwoFunctions(2, add5, constant(1)) shouldBe 1

            val list = arrayListOf("foo", "bar", "baz")

            list.map(constant(7)) shouldBe arrayListOf(7, 7, 7)
        }

        "testGetterAndSetterOperations" {
            val greeter = Greeter()

            //Test Setter
            greeter.receive["Hola"] = "Mario"
            "Hola from Mario" shouldBe greeter.getReceivedHello()
            "Hello Mario" shouldBe greeter.sayHello["Mario"]

        }
    }
}


class Greeter {

    var hello: String? = null
    var name: String? = null

    fun receiveHello(hello: String, name: String) {
        this.hello = hello
        this.name = name
    }

    fun getReceivedHello(): String {
        return "$hello from $name"
    }

    fun sayHelloTo(name: String): String {
        return "Hello $name"
    }


}

