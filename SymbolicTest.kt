import symbolic.Expression.*
import symbolic.*

fun main() {
    val f = X.pow(3.constant)
    println(f)
    val fPrime = f.derivative()
    println(fPrime)
    println(fPrime.res(2))

    println(X.pow(X).derivative())
    println(X.exp().derivative())
    println(X.pow(2.constant).exp())
    println(X.pow(2.constant).exp().derivative().res(3))

    println(cot(X).derivative())
}