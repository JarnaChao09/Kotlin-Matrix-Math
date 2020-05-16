package symbolic

fun main() {
    val x = Variable("x")

    println(x)

    val y = x + 10

    println(y)

    val z by Var()

    val t = y + z

    println(t)

    println(t(x to 2, z to 4))
}
