package symbolic

import kotlin.math.pow

sealed class Fun: Stringify, EvalFun<Fun, Constant> {
    val reciprocal: Fun
        get() = Power(this, (-1).const)

    val exp: Fun
        get() = Power(kotlin.math.E.const, this)

    operator fun unaryPlus(): Fun = this

    operator fun unaryMinus(): Fun = Product((-1).const, this)

    operator fun plus(other: Fun): Fun = Sum(this, other)

    operator fun minus(other: Fun): Fun = Sum(this, -other)

    operator fun times(other: Fun): Fun = Product(this, other)

    operator fun div(other: Fun): Fun = Product(this, other.reciprocal)

    fun eval(vararg values: Pair<Fun, Constant>) = this.eval(mapOf(*values))

    operator fun invoke(value: Map<Fun, Constant>) = this.eval(value)

    operator fun invoke(vararg values: Pair<Fun, Constant>) = this.eval(*values)
}

data class Constant(val value: Double): Fun() {
    constructor(n: Number): this(n.toDouble())

    override fun stringify(): String = "$value"

    override fun eval(value: Map<Fun, Constant>): Double = this.value

    override fun toString(): String = this.value.toString()
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

    override fun toString(): String = this.name
}

data class Sum(val a: Fun, val b: Fun): Fun() {
    override fun stringify(): String = "(${a.stringify()} + ${b.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double = a.eval(value) + b.eval(value)

    override fun toString(): String = "Sum(${this.a}, ${this.b})"
}

data class Product(val a: Fun, val b: Fun): Fun() {
    override fun stringify(): String = "(${a.stringify()} * ${a.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double = a.eval(value) * b.eval(value)

    override fun toString(): String = "Product(${this.a}, ${this.b})"
}

data class Power(val base: Fun, val exponent: Fun): Fun() {
    override fun stringify(): String = "(${base.stringify()} ^ ${exponent.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double =
        base.eval(value).pow(exponent.eval(value))

    override fun toString(): String = "Power(${this.base}, ${this.exponent})"
}

data class Ln(val a: Fun): Fun() {
    override fun stringify(): String = "ln(${a.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double =
        kotlin.math.ln(a.eval(value))

    override fun toString(): String = "Ln(${this.a})"
}

data class Sin(val a: Fun): Fun() {
    override fun stringify(): String = "sin(${a.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double =
        kotlin.math.sin(a.eval(value))

    override fun toString(): String = "Sin(${this.a})"
}

data class Cos(val a: Fun): Fun() {
    override fun stringify(): String = "cos(${a.stringify()})"

    override fun eval(value: Map<Fun, Constant>): Double =
        kotlin.math.cos(a.eval(value))

    override fun toString(): String = "Cos(${this.a})"
}