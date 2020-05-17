package symbolic

fun main() {
//    val x = Variable("x")
//
//    println(x)
//
//    val y = x + 10
//
//    println(y)
//
//    val z by Var()
//
//    val t = y + z
//
//    println(t)
//
//    println(t(x to 2, z to 4))
//
//    val t2 = t + 2.const * z
//
//    println(t2.stringify())
//
//    println(t2.variables)

    val x by Var()

    val y = x pow 2

    println(y.diff(x).stringify())

    println(y.diff(x).evalAllAtZero())

    val y1 = x + 10

    val y1Prime = d(y1) / d(x)

    println(y1Prime(x to 2))
}
