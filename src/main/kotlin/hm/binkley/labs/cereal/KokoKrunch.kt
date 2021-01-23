package hm.binkley.labs.cereal

import lombok.Generated
import java.lang.reflect.Field
import java.nio.ByteBuffer

const val MAGIC = "KOKO"
const val VERSION = 0.toByte()

internal const val NIL_VALUE = -1

inline fun <reified T> ByteArray.read(): T = read(T::class.java)

fun <T> ByteArray.read(clazz: Class<T>): T = toByteBuffer()
    .assertMetadata()
    .assertEnoughData(clazz) {
        val instance = clazz.readFrom(this) { blankInstance(it) }
        for ((field, value) in clazz.readFrom(this) { fields(it) })
            field.set(instance, value)

        assertSentinel()
        assertComplete()

        instance
    }

fun Any.write(): ByteArray {
    val preps = classAndFieldPreps()
    val buf = preps.newBuffer()

    MAGIC.forEach { buf.putByte(it.toByte()) }
    buf.putByte(VERSION)

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
