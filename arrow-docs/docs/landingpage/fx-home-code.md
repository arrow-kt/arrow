```kotlin:ank:playground
import arrow.fx.coroutines.parTraverse
import arrow.fx.coroutines.parMapN

data class Street(val name: String) 
data class Company(val name: String) 
data class Employee(val name: String, val company: Company, val hired : Boolean = false)

/** An async non blocking service **/
suspend fun company(name: String): Company =
  Company("$name on ${Thread.currentThread().name}")  

/** An async non blocking service **/
suspend fun street(name: String): Street =
  Street("$name on ${Thread.currentThread().name}")    
  
/** An async non blocking service **/
suspend fun hire(employee: Employee): Employee =
  employee.copy(hired = true)
  
fun employee(name: String, company: Company): Employee =
  Employee(name, company)

suspend fun main() {
    //sampleStart
    //maps each function to `::employee` in parallel
    val audrey = parMapN({ "Audrey" }, { company("Arrow") }, ::employee) 
    val pepe   = parMapN({  "Pepe"  }, { company("Arrow") }, ::employee)
    val candidates = listOf(audrey, pepe)
    val employees = candidates.parTraverse(::hire) //hires in parallel
    //sampleEnd
    println(employees)
}
```
