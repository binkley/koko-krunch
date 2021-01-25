package hm.binkley.labs.cereal

import lombok.Generated
import java.lang.reflect.Field
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

const val MAGIC: String = "KOKO"
const val VERSION: Byte = 0.toByte()

internal const val NIL_VALUE = -1

class SerialException(message: String) : Exception(message)

inline fun <reified T : Any> ByteArray.read(): T = read(T::class)

fun <T : Any> ByteArray.read(klass: KClass<T>): T = toByteBuffer()
    .assertMetadata()
    .assertEnoughData(klass) {
        val instance = klass.readFrom(this) { blankInstance(it) }
        for ((field, value) in klass.readFrom(this) { readFields(it) })
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
    val fields = this::class.serializedFields.sortedBy { it.name }

    val classPreps = listOf(
        this::class.jvmName,
        fields.size,
    ).map { it.study() }

    val fieldPreps = fields
        .map { it.info(this) }
        .flatMap { it.study() }

    return classPreps + fieldPreps
}

private fun <T : Any> ByteBuffer.blankInstance(expectedClass: KClass<T>) =
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
