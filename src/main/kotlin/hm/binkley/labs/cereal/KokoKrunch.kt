package hm.binkley.labs.cereal

import lombok.Generated
import java.lang.reflect.Field
import java.nio.ByteBuffer

inline fun <reified T> ByteArray.read(): T = read(T::class.java)

/**
 * Read in an instance of [clazz] from a byte array following the rules in
 * [write].
 */
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
 * Write out byte array representing a serialized object as a sequence of
 * records:
 * - Each record has 3 parts in the order of:
 *   1. A byte count of the serialized value
 *   2. The serialized value as bytes
 *   3. A terminating sentinel byte with value 0
 * - The complete serialization has an additional terminating sentinel byte
 *   with value 0
 * - The object class name is the first record
 * - An `int` count of serialized fields is the second record
 * - Each non-static, non-transient field, sorted alphabetically by
 *   field name, are the remaining records with each field contributing 3
 *   records each:
 *   1. The field name
 *   2. The field type name
 *   3. The serialized field value if primitive or non-null
 * - If the field value is `null`, use -1 as the value payload byte count, and
 *   do not write a value
 * - If the field value is non-primitive, the third record is the serialized
 *   object recursively following these rules
 */
fun Any.write(): ByteArray {
    val preps = classAndFieldPreps()
    val buf = preps.newBuffer()

    preps.forEach { it.writeTo(buf) }
    buf.put(0)

    return buf.array()
}

private fun Any.classAndFieldPreps(): List<Prep> {
    val fields = this::class.java.serializedFields.sortedBy { it.name }

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

@Generated // Lie to JaCoCo
private data class FieldInfo(
    val name: String,
    val typeName: String,
    val value: Any?,
)

private fun Field.info(o: Any) = FieldInfo(name, type.name, get(o))

private fun FieldInfo.study() = listOf(
    name,
    typeName,
    value
).map { it.study() }
