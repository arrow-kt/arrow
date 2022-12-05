---
library: fx
---
<!--- INCLUDE
import arrow.fx.coroutines.parMap
import arrow.fx.coroutines.parZip

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
-->
```kotlin
suspend fun process(): List<Employee> {
  // maps to 'Employee' in parallel using 'parZip'
  val audrey = parZip({ "Audrey" }, { company("Arrow") }) { 
    name, company -> Employee(name, company) 
  } 
  val pepe   = parZip({  "Pepe"  }, { company("Arrow") }) { 
    name, company -> Employee(name, company) 
  }
  // hires in parallel using 'parMap'
  return listOf(audrey, pepe).parMap { hire(it) } 
}
```
