package kategory

typealias Result = String
typealias EmailValidation = Nel<Result>
typealias EmailValidationResult = Validated<Result, Unit>

fun main(args: Array<String>) {

    listOf(
            "hello@bye.com",
            "@nolocalpart.com",
            "nodomain@",
            "onlypartdomain@bye",
            "onlyotherpartdomain@.com",
            "no.at.com"
    )
            .map(::validateEmail)
            .reduce { acc, validated -> acc.combine(validated) }
//            .reduce(EmailValidation::plus)
//            .all
//            .joinToString(separator = "\n")
            .println()
}

private fun Any.println() = println(this)


val at = '@'
fun validateEmail(email: String): EmailValidationResult {
    fun isEmailValid(email: String): EmailValidationResult = when {
        !email.contains(at) -> "Email doesn't contain $at".invalid()
        email.indexOf(at) == 0 -> "Email doesn't contain local-part".invalid()
        else -> email.split(at).filter { !it.isBlank() }.let<List<String>, Validated<Result, Unit>> { emailParts ->
            when {
                emailParts.size < 2 -> "Email doesn't contain domain".invalid()
                emailParts.size > 2 -> "Email cannot contain more than one $at".invalid()
                emailParts[1].contains('.')
                        && emailParts[1].indexOf('.') == 0 -> "Email domain should have a name before dot".invalid()
                else -> Unit.valid()
            }
        }
    }

    return isEmailValid(email)
//            .fold({
//                "$email is not valid, reason: $it".nel()
//            }, {
//                "$email is a valid email".nel()
//            })
}

//private operator fun <E> List<E>.compareTo(i: Int): Int =
//    size.compareTo(i)

/*
if ...
else if ...
else **.let
 */
