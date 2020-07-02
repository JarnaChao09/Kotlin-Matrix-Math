package matrix

import utils.Size
import utils.Slice
import utils.by
import vector.DoubleVector
import vector.IntVector
import vector.Vector
import java.lang.IllegalArgumentException
import java.util.stream.DoubleStream
import kotlin.math.absoluteValue
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
        fun diagonal(vararg elements: Number): DoubleMatrix = DoubleMatrix(elements.size by elements.size) { r, c -> if (r == c) elements[r].toDouble() else 0.0 }

        @JvmStatic
        fun scalar(n: Int, value: Double): DoubleMatrix = diagonal(*Array(n) { value })

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

    val doubleVector: Vector<DoubleVector>
        get() = Vector(this.rowLength) { r -> DoubleVector(this.colLength) { c -> this[r, c] } }

    val doubleArray: Array<DoubleArray>
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
        other as DoubleMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] + other[i]
        }
        return ret
    }

    override fun minus(other: NumberMatrix<Double>): DoubleMatrix {
        other as DoubleMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] - other[i]
        }
        return ret
    }

    override fun times(other: NumberMatrix<Double>): DoubleMatrix {
        other as DoubleMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] * other[i]
        }
        return ret
    }

    override fun div(other: NumberMatrix<Double>): DoubleMatrix {
        other as DoubleMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] / other[i]
        }
        return ret
    }

    override fun rem(other: NumberMatrix<Double>): DoubleMatrix {
        other as DoubleMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] % other[i]
        }
        return ret
    }

    override fun pow(other: NumberMatrix<Double>): DoubleMatrix {
        other as DoubleMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] pow other[i]
        }
        return ret
    }

    override fun plusAssign(other: NumberMatrix<Double>) {
        other as DoubleMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] + other[i]
        }
    }

    override fun minusAssign(other: NumberMatrix<Double>) {
        other as DoubleMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] - other[i]
        }
    }

    override fun timesAssign(other: NumberMatrix<Double>) {
        other as DoubleMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] * other[i]
        }
    }

    override fun divAssign(other: NumberMatrix<Double>) {
        other as DoubleMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] / other[i]
        }
    }

    override fun remAssign(other: NumberMatrix<Double>) {
        other as DoubleMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] % other[i]
        }
    }

    override fun powAssign(other: NumberMatrix<Double>) {
        other as DoubleMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] pow other[i]
        }
    }

    override fun dot(other: NumberMatrix<Double>): DoubleMatrix {
        other as DoubleMatrix
        val ret = DoubleMatrix(this.dim.x, 1)
        for (i in 0 until this.dim.x) {
            ret[i, 0] = this[i] dot other[i]
        }
        return ret
    }

    override fun cross(other: NumberMatrix<Double>): DoubleMatrix {
        other as DoubleMatrix
        val ret = DoubleMatrix(this.dim.x, 1)
        for (i in 0 until this.dim.x) {
            ret[i] = this[i] cross other[i]
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

    override fun inverse(): DoubleMatrix {
        val src = DoubleMatrix(this)
        val last = this.rowLength - 1
        val a = src.toArray()

        val identity = identity(this.rowLength)

        for (k in 0..last) {
            var i = k
            var akk = a[k][k].absoluteValue
            for (j in (k+1)..last) {
                val v = a[j][k].absoluteValue
                if (v > akk) {
                    i = j
                    akk = v
                }
            }
            if (akk == 0.0) throw Error.NotRegular()
            if (i != k) {
                val temp1 = a[i]
                a[i] = a[k]
                a[k] = temp1

                val temp2 = identity[i]
                identity[i] = identity[k]
                identity[k] = temp2
            }
            akk = a[k][k]

            for (ii in 0..last) {
                if (ii == k) {
                    continue
                }
                val q = a[ii][k] / akk
                a[ii][k] = 0.0

                for (j in (k+1)..last) {
                    a[ii][j] = a[ii][j] - (a[k][j] * q)
                }

                for (j in 0..last) {
                    identity[ii, j] = identity[ii, j] - (identity[k, j] * q)
                }
            }
            
            for (j in (k+1)..last) {
                a[k][j] = a[k][j] / akk
            }
            for (j in 0..last) {
                identity[k, j] = identity[k, j] / akk
            }
        }
        return identity
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
        if (this.isEmpty()) throw RuntimeException("First Minor of Empty Matrix is not defined")

        if (row !in 0 until rowLength) throw IllegalArgumentException("Invalid row $row for 0..${rowLength - 1}")

        if (col !in 0 until colLength) throw IllegalArgumentException("Invalid col $col for 0..${colLength - 1}")

        val vectors = this.doubleVector
        vectors.removeAt(row)
        vectors.forEach { it.removeAt(col) }

        return DoubleMatrix(vectors)
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

    override fun matDiv(other: NumberMatrix<Double>): DoubleMatrix =
        this * other.inv
}
