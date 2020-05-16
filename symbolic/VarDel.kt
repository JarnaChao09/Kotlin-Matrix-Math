package symbolic

import kotlin.reflect.KProperty

class VarDel(val name: String? = null) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Variable = Variable(name ?: property.name)
}

typealias Var = VarDel