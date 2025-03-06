import java.lang.System

fun main(args: Array<String>) =
    app(args.firstOrNull() ?: System.getenv("MODE"))