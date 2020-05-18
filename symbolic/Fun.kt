package symbolic

import kotlin.math.pow

sealed class Fun(
    open val variables: Set<Variable> = emptySet()
): Simplify<Fun>, Stringify, Differentiable<Variable, Fun>, EvalFun<Fun, Constant, Fun> {
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

    fun eval(vararg values: Pair<Fun, Constant>) =
        try {
            this.eval(mapOf(*values))
        } catch(e: Variable.NoValueException) {
            this.partialEval(mapOf(*values))
        }

    fun evalAllAtZero(): Fun {
        val map = emptyMap<Fun, Constant>().toMutableMap()
        for (i in variables) {
            map[i] = 0.const
        }
        return this.eval(map)
    }

    fun evalAllAt(value: Number): Fun {
        val map = emptyMap<Fun, Constant>().toMutableMap()
        for (i in variables) {
            map[i] = 0.const
        }
        return this.eval(map)
    }

    operator fun invoke(value: Map<Fun, Constant>) =
        try {
            this.eval(value)
        } catch(e: Variable.NoValueException) {
            this.partialEval(value)
        }

    operator fun invoke(vararg values: Pair<Fun, Constant>) = this.eval(*values)

    override fun simplify(): Fun = this

    fun simpleString(): String = this.simplify().stringify()
}

data class Constant(val value: Double): Fun() {
    constructor(n: Number): this(n.toDouble())

    override fun stringify(): String = when (value) {
        kotlin.math.PI -> "PI"
        kotlin.math.E -> "E"
        else -> "$value"
    }

    override fun diff(by: Variable): Fun = 0.const

    override fun evalImpl(value: Map<Fun, Constant>): Double = this.value

    override fun partialEval(value: Map<Fun, Constant>): Fun = this.eval(value)

    override fun toString(): String = "Constant(${this.value})"
}

data class Variable(val name: String): Fun() {
    override val variables: Set<Variable>
        get() = setOf(this)

    class NoValueException(x: Variable): Throwable("No Value Given for ${x.stringify()}")

    override fun stringify(): String = name

    override fun diff(by: Variable): Fun =
        if (this == by) 1.const else 0.const

    override fun evalImpl(value: Map<Fun, Constant>): Double {
        loop@for ((i, j) in value) {
            when(i) {
                this -> return j.value
                else -> continue@loop
            }
        }
        throw NoValueException(this)
    }

    override fun partialEval(value: Map<Fun, Constant>): Fun {
        loop@for ((i, j) in value) {
            when(i) {
                this -> return j
                else -> continue@loop
            }
        }
        return this
    }

    override fun toString(): String = this.name
}

data class Sum(val a: Fun, val b: Fun): Fun() {
    override val variables: Set<Variable>
        get() = setOf(*this.a.variables.toTypedArray(), *this.b.variables.toTypedArray())

    override fun simplify(): Fun {
        val a_ = a.simplify()
        val b_ = b.simplify()
        return when {
            a_ == 0.const -> b_
            b_ == 0.const -> a_
            a_ is Constant && b_ is Constant && a_.value != kotlin.math.PI && b_.value != kotlin.math.PI -> this.evalAllAtZero()
            else -> a_ + b_
        }
    }

    override fun stringify(): String = "(${a.stringify()} + ${b.stringify()})"

    override fun diff(by: Variable): Fun = this.a.diff(by) + this.b.diff(by)

    override fun evalImpl(value: Map<Fun, Constant>): Double = a.evalImpl(value) + b.evalImpl(value)

    override fun partialEval(value: Map<Fun, Constant>): Fun = a.partialEval(value) + b.partialEval(value)

    override fun toString(): String = "Sum(${this.a}, ${this.b})"
}

data class Product(val a: Fun, val b: Fun): Fun() {
    override val variables: Set<Variable>
        get() = setOf(*this.a.variables.toTypedArray(), *this.b.variables.toTypedArray())

    override fun simplify(): Fun {
        val a_ = a.simplify()
        val b_ = b.simplify()
        return when {
            a_ == 0.const || b_ == 0.const -> 0.const
            a_ == 1.const -> b_
            b_ == 1.const -> a_
            a_ is Constant &&
                    b_ is Constant &&
                    a_.value != kotlin.math.PI &&
                    b_.value != kotlin.math.PI ->
                this.evalAllAtZero()
            else -> a_ * b_
        }
    }

    override fun stringify(): String = "(${this.a.stringify()} * ${this.b.stringify()})"

    override fun diff(by: Variable): Fun = this.a.diff(by) * this.b.diff(by)

    override fun evalImpl(value: Map<Fun, Constant>): Double = a.evalImpl(value) * b.evalImpl(value)

    override fun partialEval(value: Map<Fun, Constant>): Fun = a.partialEval(value) * b.partialEval(value)

    override fun toString(): String = "Product(${this.a}, ${this.b})"
}

data class Power(val base: Fun, val exponent: Fun): Fun() {
    override val variables: Set<Variable>
        get() = setOf(*this.base.variables.toTypedArray(), *this.exponent.variables.toTypedArray())

    override fun simplify(): Fun {
        val base_ = base.simplify()
        val exp_ = exponent.simplify()
        return when {
            base_ == 0.const -> 0.const
            exp_ == 0.const -> 1.const
            exp_ == 1.const -> base_
            base_ is Constant && exp_ is Constant -> this.evalAllAtZero()
            else -> base_ pow exp_
        }
    }

    override fun stringify(): String = "(${base.stringify()} ^ ${exponent.stringify()})"

    override fun diff(by: Variable): Fun =
        when {
            this.base is Constant && this.exponent !is Constant ->
                this * Ln(this.base) * this.exponent.diff(by)
            this.base !is Constant && this.exponent is Constant ->
                if (this.exponent == 1.const)
                    Ln(this.base) * this.base.diff(by)
                else
                    this.exponent * (this.base pow (this.exponent.value - 1).const) * this.base.diff(by)
            else -> {
                this.exponent * (this.base pow (this.exponent - 1.const)) * this.base.diff(by) + (this.base pow (this.exponent)) * Ln(
                    this.base
                ) * this.exponent.diff(by)
            }
        }

    override fun evalImpl(value: Map<Fun, Constant>): Double =
        base.evalImpl(value).pow(exponent.evalImpl(value))

    override fun partialEval(value: Map<Fun, Constant>): Fun = base.partialEval(value) pow exponent.partialEval(value)

    override fun toString(): String = "Power(${this.base}, ${this.exponent})"
}

data class Ln(val a: Fun): Fun() {
    override val variables: Set<Variable>
        get() = this.a.variables

    override fun simplify(): Fun {
        val a_ = a.simplify()
        return when (a_) {
            1.const -> 0.const
            Math.E.const -> 1.const
            0.const -> Double.MIN_VALUE.const
            else -> Ln(a_)
        }
    }

    override fun stringify(): String = "ln(${a.stringify()})"

    override fun diff(by: Variable): Fun = this.a.reciprocal * this.a.diff(by)

    override fun evalImpl(value: Map<Fun, Constant>): Double = when(val calc = a.evalImpl(value)) {
        0.0 -> Double.MIN_VALUE
        else -> kotlin.math.ln(calc)
    }

    override fun partialEval(value: Map<Fun, Constant>): Fun = when (val pCalc = a.partialEval(value)) {
        0.0.const -> Double.MIN_VALUE.const
        else -> Ln(a.partialEval(value))
    }

    override fun toString(): String = "Ln(${this.a})"
}

data class Sin(val a: Fun): Fun() {
    override val variables: Set<Variable>
        get() = this.a.variables

    override fun simplify(): Fun = sin(a.simplify())

    override fun stringify(): String = "sin(${a.stringify()})"

    override fun diff(by: Variable): Fun = cos(a) * a.diff(by)

    override fun evalImpl(value: Map<Fun, Constant>): Double =
        kotlin.math.sin(a.evalImpl(value))

    override fun partialEval(value: Map<Fun, Constant>): Fun =
        Sin(a.partialEval(value))

    override fun toString(): String = "Sin(${this.a})"
}

data class Cos(val a: Fun): Fun() {
    override val variables: Set<Variable>
        get() = this.a.variables

    override fun simplify(): Fun = cos(a.simplify())

    override fun stringify(): String = "cos(${a.stringify()})"

    override fun diff(by: Variable): Fun = -sin(a) * a.diff(by)

    override fun evalImpl(value: Map<Fun, Constant>): Double =
        kotlin.math.cos(a.evalImpl(value))

    override fun partialEval(value: Map<Fun, Constant>): Fun =
        Cos(a.partialEval(value))

    override fun toString(): String = "Cos(${this.a})"
}