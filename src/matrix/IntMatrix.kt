package matrix

import utils.Size
import utils.Slice
import utils.by
import vector.IntVector
import vector.Vector

import java.lang.IllegalArgumentException
import java.util.stream.IntStream
import kotlin.math.absoluteValue

import kotlin.reflect.KClass

class IntMatrix(dim: Size, initBlock: (r: Int, c: Int) -> Int): NumberMatrix<Int>(dim, initBlock) {
    constructor(x: Int, y: Int, initBlock: (Int) -> Int = { 0 }): this(dim = Size(x, y), initBlock = { _, _ -> initBlock(0)})

    constructor(vector: IntVector): this(dim = Size(1, vector.length), initBlock = { _, i -> vector[i]  })

    constructor(vector: IntVector, asCol: Boolean = false):
            this(
                dim = if (asCol) Size(vector.length, 1) else Size(
                    1,
                    vector.length
                ),
                initBlock = if (asCol) { i, _ -> vector[i]} else { _, i -> vector[i]  }
            )

    constructor(vectorOfVector: Vector<IntVector>, asColVectors: Boolean = false): this(
        dim = if (asColVectors) Size(
            vectorOfVector[0].length,
            vectorOfVector.length
        ) else Size(vectorOfVector.length, vectorOfVector[0].length),
        initBlock = if (asColVectors) { r, c -> vectorOfVector[c][r] } else { r, c -> vectorOfVector[r][c] }
    )

    constructor(matrix: Matrix<Int>): this(dim = matrix.dim, initBlock = { r, c -> matrix[r, c] })

    constructor(dim1: Size, asRows: Boolean, initBlock: (Int) -> Int): this(dim = dim1, initBlock = if (asRows) { _, i -> initBlock(i)} else { r, _ -> initBlock(r)})

    constructor(dim1: Size): this(dim1, initBlock = { _, _ -> 0 })

    constructor(): this(Size(3, 3), initBlock = { _, _ -> 0 })

    sealed class Scope {
        class Base: Scope()

        val matrix: Vector<IntVector> = Vector(0) { IntVector.EMPTY }

        operator fun IntVector.not(): Scope =
            this@Scope.also { this@Scope.matrix.append(this) }
    }

    companion object {
        @JvmStatic
        fun empty(dim: Size) = IntMatrix(dim, asRows = true) { 0 }

        @JvmStatic
        fun zeros(dim: Size): IntMatrix = empty(dim)

        @JvmStatic
        fun ones(dim: Size): IntMatrix = IntMatrix(dim, asRows = true) { 1 }

        @JvmStatic
        fun identity(n: Int): IntMatrix = IntMatrix(n by n) { r, c -> if (r == c) 1 else 0 }

        @JvmStatic
        fun diagonal(vararg elements: Number): IntMatrix = IntMatrix(elements.size by elements.size) { r, c -> if (r == c) elements[r].toInt() else 0 }

        @JvmStatic
        fun scalar(n: Int, value: Int): IntMatrix = diagonal(*Array(n) { value })

        @JvmStatic
        @JvmName("ofInts")
        fun of(matrix: List<List<Int>>): IntMatrix {
            val mat = Vector(matrix.size) { IntVector(matrix[0].size) }
            for (i in 0 until mat.length) {
                mat[i] = IntVector(matrix[i].size) { j -> matrix[i][j] }
            }
            return IntMatrix(mat)
        }

        @JvmStatic
        @JvmName("ofInts")
        fun of(dim: Size, vararg elements: Int): IntMatrix {
            val mat = IntMatrix(dim)
            for (i in 0 until dim.x) {
                for(j in 0 until dim.y) {
                    mat[i, j] = elements[dim.x * i + j]
                }
            }
            return mat
        }

        @JvmStatic
        val EMPTY: IntMatrix
            get() = empty(0 by 0)
    }

    override fun get(index: Int): IntVector = IntVector(this.colLength) { i -> super.get(index)[i] }

    override fun get(indexSlice: Slice): IntMatrix = IntMatrix(super.get(indexSlice))

    override fun get(indexR: Int, indexCSlice: Slice): IntMatrix = IntMatrix(super.get(indexR, indexCSlice))

    override fun get(indexRSlice: Slice, indexC: Int): IntVector = IntVector(this.colLength) { i -> super.get(indexRSlice, indexC)[i] }

    override fun get(indexRSlice: Slice, indexCSlice: Slice): IntMatrix = IntMatrix(super.get(indexRSlice, indexCSlice))

    override fun rowAppend(other: MatrixBase<Int>): IntMatrix = super.rowAppend(other) as IntMatrix

    override fun colAppend(other: MatrixBase<Int>): IntMatrix = super.colAppend(other) as IntMatrix

    override var internalMatrix: Vector<Vector<Int>> =
        Vector(dim.x) { i -> IntVector(dim.y) { j -> initBlock(i, j) } }

    override val inv: IntMatrix
        get() = this.inverse()

    override val t: IntMatrix
        get() = this.transpose()

    override fun transpose(): IntMatrix =
        super.transpose() as IntMatrix

    override val type: KClass<Int> by lazy { Int::class }

    val intStream: IntStream
        get() = this.stream.mapToInt { x -> x }

    override val vector: Vector<IntVector>
        get() = this.toVector()

    override fun toVector(): Vector<IntVector> =
        Vector(this.rowLength) { r -> IntVector(this.colLength) { c -> this[r, c] } }

    val intArray: Array<IntArray>
        get() {
            val ret = Array(this.toArray().size) { IntArray(this.toArray()[0].size) }
            var index = 0
            for (i in this.toArray()) {
                ret[index++] = i.toIntArray()
            }
            return ret
        }

    override fun toArray(): Array<Array<Int>> {
        val ret = MutableList(this.size.x) { Array(this.size.y) { 0 } }
        var index = 0
        for (i in this.toList()) {
            ret[index++] = i.toTypedArray()
        }
        return ret.toTypedArray()
    }


    override fun plus(other: NumberMatrix<Int>): IntMatrix {
        other as IntMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] + other[i]
        }
        return ret
    }

    override fun minus(other: NumberMatrix<Int>): IntMatrix {
        other as IntMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] - other[i]
        }
        return ret
    }

    override fun times(other: NumberMatrix<Int>): IntMatrix {
        other as IntMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] * other[i]
        }
        return ret
    }

    override fun div(other: NumberMatrix<Int>): IntMatrix {
        other as IntMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] / other[i]
        }
        return ret
    }

    override fun rem(other: NumberMatrix<Int>): IntMatrix {
        other as IntMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] % other[i]
        }
        return ret
    }

    override fun pow(other: NumberMatrix<Int>): IntMatrix {
        other as IntMatrix
        val ret = empty(this.dim)
        for (i in 0 until ret.rowLength) {
            ret[i] = this[i] pow other[i]
        }
        return ret
    }

    override fun plusAssign(other: NumberMatrix<Int>) {
        other as IntMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] + other[i]
        }
    }

    override fun minusAssign(other: NumberMatrix<Int>) {
        other as IntMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] - other[i]
        }
    }

    override fun timesAssign(other: NumberMatrix<Int>) {
        other as IntMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] * other[i]
        }
    }

    override fun divAssign(other: NumberMatrix<Int>) {
        other as IntMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] / other[i]
        }
    }

    override fun remAssign(other: NumberMatrix<Int>) {
        other as IntMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] % other[i]
        }
    }

    override fun powAssign(other: NumberMatrix<Int>) {
        other as IntMatrix
        for (i in 0 until this.rowLength) {
            this[i] = this[i] pow other[i]
        }
    }

    override fun dot(other: NumberMatrix<Int>): IntMatrix {
        other as IntMatrix
        val ret = IntMatrix(this.dim.x, 1)
        for (i in 0 until this.rowLength) {
            ret[i, 0] = this[i] dot other[i]
        }
        return ret
    }

    override fun cross(other: NumberMatrix<Int>): IntMatrix {
        other as IntMatrix
        val ret = IntMatrix(this.dim.x, 1)
        for (i in 0 until this.dim.x) {
            ret[i] = this[i] cross other[i]
        }
        return ret
    }

    fun toDoubleMatrix(): DoubleMatrix =
        DoubleMatrix(this.size) { r, c -> this[r, c].toDouble() }

    override fun trace(): Int {
        var sum = 0
        for (i in 0 until this.rowLength) {
            sum += this[i, i]
        }
        return sum
    }

    override fun inverse(): IntMatrix {
        val src = IntMatrix(this)
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
            if (akk == 0) throw Error.NotRegular()
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
                a[ii][k] = 0

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

    override fun adjugate(): IntMatrix {
        TODO("Not yet implemented")
    }

    override fun cofactor(row: Int, col: Int): Int {
        TODO("Not yet implemented")
    }

    override fun firstMinor(row: Int, col: Int): IntMatrix {
        if (this.isEmpty()) throw RuntimeException("First Minor of Empty Matrix is not defined")

        if (row !in 0 until rowLength) throw IllegalArgumentException("Invalid row $row for 0..${rowLength - 1}")

        if (col !in 0 until colLength) throw IllegalArgumentException("Invalid col $col for 0..${colLength - 1}")

        val vectors = this.vector
        vectors.removeAt(row)
        vectors.forEach { it.removeAt(col) }

        return IntMatrix(vectors)
    }

    override fun laplaceExpansion(row: Int, col: Int): Int {
        TODO("Not yet implemented")
    }

    override val lup: LUPDecomposition
        get() = LUPDecomposition(this.toDoubleMatrix())

    override fun matMul(other: NumberMatrix<Int>): IntMatrix {
        other as IntMatrix
        if (this.dim.y != other.dim.x) throw IllegalArgumentException("${this.dim} is not compatible with ${other.dim}")
        val ret = zeros(this.dim.x by other.dim.y)
        for (i in 0 until ret.rowLength) {
            for (j in 0 until ret.colLength) {
                ret[i, j] = this[i] dot IntVector(other.rowLength) { k -> other.t[j, k] }
            }
        }
        return ret
    }

    override fun matDiv(other: NumberMatrix<Int>): IntMatrix =
        this * other.inv
}
