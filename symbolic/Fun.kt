package symbolic

import kotlin.math.pow

sealed class Fun: Stringify, EvalFun<Fun, Constant> {
    operator fun plus(other: Fun) = Sum(this, other)

    operator fun times(other: Fun) = Product(this, other)

    operator fun unaryPlus() = this

    operator fun unaryMinus() = Product((-1).const, this)

    operator fun minus(other: Fun) = Sum(this, -other)

    val reciprocal
        get() = Power(this, (-1).const)

    operator fun div(other: Fun) = Product(this, other.reciprocal)

    infix fun pow(other: Fun) = Power(this, other)

    val exp
        get() = Power(kotlin.math.E.const, this)
}

data class Constant(val value: Double): Fun() {
    constructor(n: Number): this(n.toDouble())

    override fun stringify(): String = "$value"

    override fun eval(value: Map<Fun, Constant>): Double = this.value
}

data class Variable(val name: String): Fun() {
    class NoValueException(x: Variable): Throwable("No Value Given for ${x.stringify()}")
    override fun stringify(): String = name

    override fun eval(value: Map<Fun, Constant>): Double {
        loop@for ((i, j) in value) {
            when(i) {
                this -> return j.value
                else -> continue@loop
            }
        }
        throw NoValueException(this)
    }
}

data class Sum(val a: Fun, val b: Fun): Fun() {
    override fun stringify(): String = "(${a.stringify()} + ${b.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double = a.eval(value) + b.eval(value)
}

data class Product(val a: Fun, val b: Fun): Fun() {
    override fun stringify(): String = "(${a.stringify()} * ${a.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double = a.eval(value) * b.eval(value)
}

data class Power(val base: Fun, val exponent: Fun): Fun() {
    override fun stringify(): String = "(${base.stringify()} ^ ${exponent.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double =
        base.eval(value).pow(exponent.eval(value))
}

data class Ln(val a: Fun): Fun() {
    override fun stringify(): String = "ln(${a.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double =
        kotlin.math.ln(a.eval(value))
}

data class Sin(val a: Fun): Fun() {
    override fun stringify(): String = "sin(${a.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double =
        kotlin.math.sin(a.eval(value))
}

val Number.const: Constant
    get() = Constant(this)

fun sin(x: Fun) = Sin(x)
fun cos(x: Fun) = sin(kotlin.math.PI.const / 2.const - x)
fun tan(x: Fun) = sin(x) / cos(x)
fun csc(x: Fun) = sin(x).reciprocal
fun sec(x: Fun) = cos(x).reciprocal
fun cot(x: Fun) = tan(x).reciprocal