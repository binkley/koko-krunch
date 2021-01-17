package hm.binkley.labs.cereal

import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Modifier

private val unsafe = Unsafe::class.java.getDeclaredField("theUnsafe").apply {
    isAccessible = true
}.get(null) as Unsafe

@Suppress("UNCHECKED_CAST")
internal fun <T> Class<T>.newBlankInstance(): T =
    unsafe.allocateInstance(this) as T

private val Field.isStatic get() = Modifier.isStatic(modifiers)
private val Field.isTransient get() = Modifier.isTransient(modifiers)

internal val Class<*>.serializedFields: List<Field>
    get() {
        val fields = mutableListOf<Field>()
        var clazz: Class<*>? = this
        while (null != clazz && java.lang.Object::class.java != clazz) {
            fields += clazz.declaredFields.filterNot {
                it.isStatic || it.isTransient
            }

            clazz = clazz.superclass
        }

        return fields.onEach { it.isAccessible = true }
    }

internal val Class<*>.serializedFieldsX
    get() = declaredFields.filterNot {
        it.isStatic || it.isTransient
    }.onEach {
        it.isAccessible = true
    }

internal fun Class<*>.getSerializedField(name: String): Field {
    var clazz: Class<*>? = this
    while (null != clazz && java.lang.Object::class.java != clazz) return try {
        val field = clazz.getDeclaredField(name)
        if (field.isStatic || field.isTransient) continue
        field.isAccessible = true
        field
    } catch (e: NoSuchFieldException) {
        continue
    } finally {
        clazz = clazz.superclass
    }

    throw AssertionError("Bad field name: $name")
}
