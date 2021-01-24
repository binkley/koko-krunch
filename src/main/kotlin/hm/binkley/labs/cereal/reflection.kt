package hm.binkley.labs.cereal

import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

private val unsafe = Unsafe::class.java.getDeclaredField("theUnsafe").apply {
    isAccessible = true
}.get(null) as Unsafe

/** @todo This breaks singleton identity contracts, such as Kotlin `object` */
@Suppress("UNCHECKED_CAST")
internal fun <T : Any> KClass<T>.newBlankInstance(): T =
    unsafe.allocateInstance(java) as T

private val Field.isStatic get() = Modifier.isStatic(modifiers)
private val Field.isTransient get() = Modifier.isTransient(modifiers)

internal val KClass<*>.serializedFields: List<Field>
    get() {
        val fields = mutableListOf<Field>()
        var clazz: Class<*>? = this.java
        while (null != clazz && Object::class.java != clazz) {
            fields += clazz.declaredFields.filterNot {
                it.isStatic || it.isTransient
            }

            clazz = clazz.superclass
        }

        return fields.onEach { it.isAccessible = true }
    }

internal fun KClass<*>.getSerializedField(name: String): Field {
    var clazz: Class<*>? = this.java
    while (null != clazz && Object::class.java != clazz) return try {
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
