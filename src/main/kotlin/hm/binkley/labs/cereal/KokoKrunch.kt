package hm.binkley.labs.cereal

import java.lang.reflect.Field
import java.nio.ByteBuffer

inline fun <reified T> ByteArray.read(): T = read(T::class.java)

/** @todo Freeze/thaw supertype fields */
fun <T> ByteArray.read(clazz: Class<T>): T =
    toByteBuffer().assertEnoughData(clazz) {
        val instance = clazz.readFrom(this) { blankInstance(it) }
        for ((field, value) in clazz.readFrom(this) { fields(it) })
            field.set(instance, value)

        assertSentinel()
        assertComplete()

        instance
    }

/**
 * Write out byte array representing a serialized object:
 * - Each record has 3 parts:
 *   1. Byte count of the value payload
 *   2. The value payload in bytes
 *   3. A terminating sentinel 0 byte
 * - The complete byte array has an additional terminating sentinel 0 byte
 * - First record is the object class name
 * - Second record is a count of serialized fields
 * - Following records are for each non-static, non-transient field, sorted by
 *   field name alphabetically, each field contributing 3 more records:
 *   1. The field name
 *   2. The field type
 *   3. The field value for the serialized object if primitive or non-null
 * - If the field value is `null`, use -1 as the value payload byte count, and
 *   do not write a value
 */
fun Any.write(): ByteArray {
    val preps = classAndFieldPreps()
    val buf = preps.newBuffer()

    preps.forEach { it.writeTo(buf) }
    buf.put(0)

    return buf.array()
}

private fun Any.classAndFieldPreps(): List<Prep> {
    val fields = this::class.java.serializedFields.sortedWith { a, b ->
        a.name.compareTo(b.name)
    }

    val classPreps = listOf(
        this::class.java.name,
        fields.size,
    ).map { it.study() }

    val fieldPreps = fields
        .map { it.info(this) }
        .flatMap { it.study() }

    return classPreps + fieldPreps
}

private fun <T> ByteBuffer.blankInstance(expectedClass: Class<T>) =
    assertClassName(expectedClass).let {
        expectedClass.newBlankInstance()
    }

private data class FieldInfo(
    val name: String,
    val typeName: String,
    val value: Any?,
) {
    fun study() = listOf(
        name,
        typeName,
        value
    ).map { it.study() }
}

private fun Field.info(o: Any) = FieldInfo(name, type.name, get(o))