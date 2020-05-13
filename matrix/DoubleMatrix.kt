package matrix

import utils.Size
import utils.Slice
import utils.by
import vector.DoubleVector
import vector.IntVector
import vector.Vector
import java.lang.IllegalArgumentException
import java.util.stream.DoubleStream
import kotlin.reflect.KClass

class DoubleMatrix(dim: Size, initBlock: (r: Int, c: Int) -> Double): NumberMatrix<Double>(dim, initBlock) {
    constructor(x: Int, y: Int, initBlock: (Int) -> Double = { 0.0 }): this(dim = Size(x, y), initBlock = { _, _ -> initBlock(0)})

    constructor(vectorOfVector: Vector<DoubleVector>, asColVectors: Boolean = false): this(
        dim = if (asColVectors) vectorOfVector[0].length by vectorOfVector.length else vectorOfVector.length by vectorOfVector[0].length,
        initBlock = if (asColVectors) { r, c -> vectorOfVector[c][r] } else { r, c -> vectorOfVector[r][c] }
    )

    constructor(vector: DoubleVector, asCol: Boolean = false):
            this(
                dim = if (asCol) Size(vector.length, 1) else Size(
                    1,
                    vector.length
                ),
                initBlock = if (asCol) { i, _ -> vector[i]} else { _, i -> vector[i]  }
            )

    constructor(matrix: Matrix<Double>): this(dim = matrix.dim, initBlock = { r, c -> matrix[r, c] })

    constructor(dim1: Size, asRows: Boolean, initBlock: (Int) -> Double): this(dim = dim1, initBlock = if (asRows) { _, i -> initBlock(i) } else { r, _ -> initBlock(r) })

    constructor(dim1: Size): this(dim1, initBlock = { _, _ -> 0.0 })

    constructor(): this(Size(3, 3), initBlock = { _, _ -> 0.0 })

    sealed class Scope {
        class Base: Scope()

        val matrix: Vector<DoubleVector> = Vector(0) { DoubleVector.EMPTY }

        operator fun DoubleVector.not(): Scope =
            this@Scope.also { this@Scope.matrix.append(this) }
    }

    companion object {
        @JvmStatic
        fun empty(dim: Size): DoubleMatrix = DoubleMatrix(dim, asRows = true) { 0.0 }

        @JvmStatic
        fun zeros(dim: Size): DoubleMatrix = empty(dim)

        @JvmStatic
        fun ones(dim: Size): DoubleMatrix = DoubleMatrix(dim, asRows = true) { 1.0 }

        @JvmStatic
        fun identity(n: Int): DoubleMatrix = DoubleMatrix(n by n) { r, c -> if (r == c) 1.0 else 0.0 }

        @JvmStatic
        fun diagonal(vararg elements: Double): DoubleMatrix = DoubleMatrix(elements.size by elements.size) { r, c -> if (r == c) elements[r] else 0.0 }

        @JvmStatic
        fun scalar(n: Int, value: Double): DoubleMatrix = diagonal(*DoubleArray(n) { value })

        @JvmStatic
        @JvmName("ofDoubles")
        fun of(matrix: List<List<Number>>): DoubleMatrix {
            val mat = Vector(matrix.size) { DoubleVector(matrix[0].size) }
            for (i in matrix.indices) {
                mat[i] = DoubleVector(matrix[i].size) { j -> matrix[i][j].toDouble() }
            }
            return DoubleMatrix(mat)
        }

        @JvmStatic
        @JvmName("ofDoubles")
        fun of(dim: Size, vararg elements: Number): DoubleMatrix {
            val mat = DoubleMatrix(dim)
            for (i in 0 until dim.x) {
                for(j in 0 until dim.y) {
                    mat[i, j] = elements[dim.x * i + j].toDouble()
                }
            }
            return mat
        }

        @JvmStatic
        val EMPTY: DoubleMatrix
            get() = empty(0 by 0)
    }

    override fun get(index: Int): DoubleVector = DoubleVector(this.colLength) { i -> super.get(index)[i] }

    override fun get(indexSlice: Slice): DoubleMatrix = DoubleMatrix(super.get(indexSlice))

    override fun get(indexR: Int, indexCSlice: Slice): DoubleMatrix = DoubleMatrix(super.get(indexR, indexCSlice))

    override fun get(indexRSlice: Slice, indexC: Int): DoubleVector = DoubleVector(this.colLength) { i -> super.get(indexRSlice, indexC)[i] }

    override fun get(indexRSlice: Slice, indexCSlice: Slice): DoubleMatrix = DoubleMatrix(super.get(indexRSlice, indexCSlice))

    override fun rowAppend(other: MatrixBase<Double>): DoubleMatrix = super.rowAppend(other) as DoubleMatrix

    override fun colAppend(other: MatrixBase<Double>): DoubleMatrix = super.colAppend(other) as DoubleMatrix

    override var internalMatrix: Vector<Vector<Double>> =
        Vector(dim.x) { i -> DoubleVector(dim.y) { j -> initBlock(i, j) } }

    override val type: KClass<Double> by lazy { Double::class }

    val doubleStream: DoubleStream
        get() = this.stream.mapToDouble { x -> x }

    val array: Array<DoubleArray>
        get() {
            val ret = Array(this.toArray().size) { DoubleArray(this.toArray()[0].size) }
            var index = 0
            for (i in this.toArray()) {
                ret[index++] = i.toDoubleArray()
            }
            return ret
        }

    override fun toArray(): Array<Array<Double>> {
        val ret = MutableList(this.size.x) { Array(this.size.y) { 0.0 } }
        var index = 0
        for (i in this.toList()) {
            ret[index++] = i.toTypedArray()
        }
        return ret.toTypedArray()
    }

    override fun plus(other: NumberMatrix<Double>): DoubleMatrix {
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] =
                this.internalMatrix[i] as DoubleVector + other.vector[i] as DoubleVector
        }
        return ret
    }

    override fun minus(other: NumberMatrix<Double>): DoubleMatrix {
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] =
                this.internalMatrix[i] as DoubleVector - other.vector[i] as DoubleVector
        }
        return ret
    }

    override fun times(other: NumberMatrix<Double>): DoubleMatrix {
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] =
                this.internalMatrix[i] as DoubleVector * other.vector[i] as DoubleVector
        }
        return ret
    }

    override fun div(other: NumberMatrix<Double>): DoubleMatrix {
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] =
                this.internalMatrix[i] as DoubleVector / other.vector[i] as DoubleVector
        }
        return ret
    }

    override fun rem(other: NumberMatrix<Double>): DoubleMatrix {
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] =
                this.internalMatrix[i] as DoubleVector % other.vector[i] as DoubleVector
        }
        return ret
    }

    override fun pow(other: NumberMatrix<Double>): DoubleMatrix {
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] =
                this.internalMatrix[i] as DoubleVector pow other.vector[i] as DoubleVector
        }
        return ret
    }

    override fun plusAssign(other: NumberMatrix<Double>) {
        this.internalMatrix = (this + other).internalMatrix
    }

    override fun minusAssign(other: NumberMatrix<Double>) {
        this.internalMatrix = (this - other).internalMatrix
    }

    override fun timesAssign(other: NumberMatrix<Double>) {
        this.internalMatrix = (this * other).internalMatrix
    }

    override fun divAssign(other: NumberMatrix<Double>) {
        this.internalMatrix = (this / other).internalMatrix
    }

    override fun remAssign(other: NumberMatrix<Double>) {
        this.internalMatrix = (this % other).internalMatrix
    }

    override fun powAssign(other: NumberMatrix<Double>) {
        this.internalMatrix = this.pow(other).vector
    }

    override fun dot(other: NumberMatrix<Double>): DoubleMatrix {
        val ret = DoubleMatrix(this.dim.x, 1)
        for (i in 0 until this.dim.x) {
            ret[i, 0] = (this.internalMatrix[i] as DoubleVector) dot (other.vector[i] as DoubleVector)
        }
        return ret
    }

    override fun cross(other: NumberMatrix<Double>): DoubleMatrix {
        val ret = DoubleMatrix(this.dim.x, 1)
        for (i in 0 until this.dim.x) {
            ret[i] =
                (this.internalMatrix[i] as DoubleVector) cross (other.vector[i] as DoubleVector)
        }
        return ret
    }

    override fun trace(): Double {
        var sum = 0.0
        for (i in 0 until this.rowLength) {
            sum += this[i, i]
        }
        return sum
    }

    override fun rank(): Int {
        TODO("Not yet implemented")
    }

    override fun inverse(): NumberMatrix<Double> {
        TODO("Not yet implemented")
    }

    override fun determinant(): Double {
        TODO("Not yet implemented")
    }

    override fun adjugate(): DoubleMatrix {
        TODO("Not yet implemented")
    }

    override fun cofactor(row: Int, col: Int): Double {
        TODO("Not yet implemented")
    }

    override fun firstMinor(row: Int, col: Int): DoubleMatrix {
        TODO("Not yet implemented")
    }

    override fun laplaceExpansion(row: Int, col: Int): Double {
        TODO("Not yet implemented")
    }

    override val lup: LUPDecomposition
        get() = LUPDecomposition(this)

    override fun matMul(other: NumberMatrix<Double>): DoubleMatrix {
        if (this.dim.y != other.dim.x) throw IllegalArgumentException("${this.dim} is not compatible with ${other.dim}")
        val ret = zeros(this.dim.x by other.dim.y)
        for (i in 0 until ret.rowLength) {
            for (j in 0 until ret.colLength) {
                ret[i, j] = this[i] dot DoubleVector(other.rowLength) { k -> other.t[j][k] }
            }
        }
        return ret
    }

    override fun matDiv(other: NumberMatrix<Double>): DoubleMatrix {
        TODO("Not yet implemented")
    }
}
