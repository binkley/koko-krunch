package hm.binkley.labs.cereal

import lombok.Generated
import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Modifier.isStatic
import java.lang.reflect.Modifier.isTransient
import kotlin.reflect.KClass

private val unsafe =
    cast<Unsafe>(
        Unsafe::class.java.getDeclaredField("theUnsafe").apply {
            isAccessible = true
        }.get(null)
    )

@Generated // Lie to JaCoCo
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <T> cast(it: Any): T = it as T

/** @todo This breaks singleton identity contracts, such as Kotlin `object` */
internal fun <T : Any> KClass<T>.newBlankInstance(): T =
    cast<T>(unsafe.allocateInstance(java))

internal val KClass<*>.serializedFields: List<Field>
    get() {
        val fields = mutableListOf<Field>()
        var clazz: Class<*> = this.java
        while (Object::class.java != clazz) {
            fields += clazz.declaredFields.filterNot {
                it.isStatic || it.isTransient
            }
            clazz = clazz.superclass
        }

        return fields.onEach { it.isAccessible = true }
    }

/** @todo Save this off; don't rescan class fields each call */
internal fun KClass<*>.getSerializedField(name: String) =
    serializedFields.firstOrNull { name == it.name }
        ?: throw AssertionError("Bad field name: $name")

private val Field.isStatic get() = isStatic(modifiers)
private val Field.isTransient get() = isTransient(modifiers)
