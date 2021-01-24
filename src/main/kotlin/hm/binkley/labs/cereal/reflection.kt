package hm.binkley.labs.cereal

import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Modifier.isStatic
import java.lang.reflect.Modifier.isTransient
import kotlin.reflect.KClass

private val unsafe = Unsafe::class.java.getDeclaredField("theUnsafe").apply {
    isAccessible = true
}.get(null) as Unsafe

/** @todo This breaks singleton identity contracts, such as Kotlin `object` */
@Suppress("UNCHECKED_CAST")
internal fun <T : Any> KClass<T>.newBlankInstance(): T =
    unsafe.allocateInstance(java) as T

private val Field.isStatic get() = isStatic(modifiers)
private val Field.isTransient get() = isTransient(modifiers)

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

internal fun KClass<*>.getSerializedField(name: String) =
    serializedFields.firstOrNull { name == it.name }
        ?: throw AssertionError("Bad field name: $name")
