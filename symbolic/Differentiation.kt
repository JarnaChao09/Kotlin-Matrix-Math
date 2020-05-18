package symbolic

import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.PI
import kotlin.math.E

sealed class Expression {
    abstract fun res(x: Double): Double
    fun res(x: Number) = res(x.toDouble())

    abstract fun diff(): Expression
    fun derivative() = diff().simplify()

    open fun simplify() = this

    data class Constant(val value: Double): Expression() {
        constructor(n: Number): this(n.toDouble())

        override fun res(x: Double) = value

        override fun diff() = Constant(0)

        private val isInt: Boolean
            get() = (value.roundToInt() - value).absoluteValue < 1E-6

        override fun toString() =
            when {
                value == PI -> "PI"
                value == E -> "E"
                this.isInt -> "${value.roundToInt()}"
                else -> "$value"
            }
    }

    data class Add(val a: Expression, val b: Expression): Expression() {
        override fun res(x: Double): Double = a.res(x) + b.res(x)

        override fun diff(): Expression = Add(a.diff(), b.diff())

        override fun toString(): String = "($a + $b)"

        override fun simplify(): Expression {
            val aSimp = a.simplify()
            val bSimp = b.simplify()
            return when {
                aSimp == 0.constant -> bSimp
                bSimp == 0.constant -> aSimp
                aSimp is Constant && bSimp is Constant && aSimp.value != PI && bSimp.value != PI -> res(0).constant
                else -> Add(aSimp, bSimp)
            }
        }
    }

    data class Mult(val a: Expression, val b: Expression): Expression() {
        override fun res(x: Double): Double = a.res(x) * b.res(x)

        override fun diff(): Expression = Add(Mult(a, b.diff()), Mult(b, a.diff()))

        override fun toString(): String = "($a * $b)"

        override fun simplify(): Expression {
            val aSimp = a.simplify()
            val bSimp = b.simplify()
            return when {
                aSimp == 0.constant || bSimp == 0.constant -> 0.constant
                aSimp == 1.constant -> bSimp
                bSimp == 1.constant -> aSimp
                aSimp is Constant && bSimp is Constant && aSimp.value != PI && bSimp.value != PI -> res(0).constant
                else -> Mult(aSimp, bSimp)
            }
        }
    }

    object X: Expression() {
        override fun res(x: Double): Double = x

        override fun diff(): Expression = 1.constant

        override fun toString(): String = "x"
    }

    data class Pow(val a: Expression, val b: Expression): Expression() {
        override fun res(x: Double): Double = a.res(x).pow(b.res(x))

        override fun diff(): Expression = b * a.pow(b - 1.constant) * a.diff() + a.pow(b) * Ln(a) * b.diff()

        override fun toString(): String = "($a ^ $b)"

        override fun simplify(): Expression {
            val theA = a.simplify()
            val theB = b.simplify()
            return when {
                theA == 0.constant -> 0.constant
                theB == 0.constant -> 1.constant
                theB == 1.constant -> theA
                theA is Constant && theB is Constant -> res(0).constant
                else -> Pow(theA, theB)
            }
        }
    }

    data class Ln(val a: Expression): Expression() {
        override fun res(x: Double) = kotlin.math.ln(a.res(x))
        override fun diff() = a.reciprocal() * a.diff()
        override fun toString() = "ln($a)"
        override fun simplify(): Expression {
            val aSimp = a.simplify()
            return when (aSimp) {
                1.constant -> 0.constant
                Math.E.constant -> 1.constant
                else -> Ln(aSimp)
            }
        }
    }

    data class Sin(val a: Expression): Expression() {
        override fun res(x: Double) = kotlin.math.sin(a.res(x))
        override fun diff() = cos(a) * a.diff()
        override fun toString() = "sin($a)"
        override fun simplify() = sin(a.simplify())
    }

    operator fun plus(f: Expression) = Add(this, f)
    operator fun times(f: Expression) = Mult(this, f)
    operator fun unaryPlus() = this
    operator fun unaryMinus() = Mult(Constant(-1), this)
    operator fun minus(f: Expression) = Add(this, -f)
    fun reciprocal() = Pow(this, Constant(-1))
    operator fun div(f: Expression) = Mult(this, f.reciprocal())
    fun pow(f: Expression) = Pow(this, f)
    fun exp() = Pow(Math.E.constant, this)
}

val Number.constant: Expression.Constant
    get() = Expression.Constant(this)

fun sin(x: Expression) = Expression.Sin(x)
fun cos(x: Expression) = sin(Math.PI.constant / 2.constant - x)
fun tan(x: Expression) = sin(x)/cos(x)
fun csc(x: Expression) = sin(x).reciprocal()
fun sec(x: Expression) = cos(x).reciprocal()
fun cot(x: Expression) = tan(x).reciprocal()
