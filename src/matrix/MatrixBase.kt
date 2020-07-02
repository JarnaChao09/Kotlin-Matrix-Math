package matrix

import utils.Size
import utils.Slice
import vector.Vector

import java.util.stream.Stream

import kotlin.reflect.KClass

interface MatrixBase<T>: Iterable<T> where T: Any {
    var size: Size

    val type: KClass<out T>

    val rowLength: Int

    val colLength: Int

    fun transpose(): MatrixBase<T>

    fun stream(): Stream<T>

    override operator fun iterator(): Iterator<T>

    val withIndices: Iterator<Triple<T, Int, Int>>

    infix fun equal(other: MatrixBase<T>): BooleanMatrix

    operator fun get(index: Int): Vector<T>

    operator fun get(indexSlice: Slice): MatrixBase<T>

    operator fun get(indexR: Int, indexC: Int): T

    operator fun get(indexRSlice: Slice, indexCSlice: Slice): MatrixBase<T>

    operator fun get(indexRSlice: Slice, indexC: Int): Vector<T>

    operator fun get(indexR: Int, indexCSlice: Slice): MatrixBase<T>

    operator fun set(index: Int, value: Vector<T>)

    operator fun set(indexSlice: Slice, value: MatrixBase<T>)

    operator fun set(indexR: Int, indexC: Int, value: T)

    operator fun set(indexRSlice: Slice, indexCSlice: Slice, value: MatrixBase<T>)

    operator fun set(indexRSlice: Slice, indexC: Int, value: Vector<T>)

    operator fun set(indexR: Int, indexCSlice: Slice, value: Vector<T>)

    fun rowAppend(other: MatrixBase<T>): MatrixBase<T>

    fun colAppend(other: MatrixBase<T>): MatrixBase<T>

    fun toVector(): Vector<out Vector<T>>

    fun toList(): List<List<T>>
}
