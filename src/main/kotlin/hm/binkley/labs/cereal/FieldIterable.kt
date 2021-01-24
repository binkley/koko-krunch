package hm.binkley.labs.cereal

import java.lang.reflect.Field
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

internal fun <T : Any> ByteBuffer.fields(kclass: KClass<T>):
    Iterable<Pair<Field, Any?>> = FieldIterable(this, kclass)

private class FieldIterable<T : Any>(
    private val buf: ByteBuffer,
    private val kclass: KClass<T>,
) : Iterable<Pair<Field, Any?>> {
    private val fieldCount: Int = kclass.readFrom(buf) { fieldCount(it) }

    override fun iterator() = FieldIterator()

    inner class FieldIterator : Iterator<Pair<Field, Any?>> {
        private var n = 0

        override fun hasNext() = fieldCount != n
        override fun next(): Pair<Field, Any?> {
            val next = kclass.readFrom(buf) { nextField(it) }
            ++n
            return next
        }
    }
}

private fun <T : Any> ByteBuffer.fieldCount(expectedClass: KClass<T>) =
    readInt().also {
        assertFieldCount(expectedClass, it)
    }

private fun <T : Any> ByteBuffer.nextField(kclass: KClass<T>): Pair<Field, Any?> {
    val field = readField(kclass)
    val len = int
    val value = readValue(field, len)

    assertSentinel()

    return field to value
}

private fun <T : Any> ByteBuffer.readField(kclass: KClass<T>) =
    readString().let { fieldName ->
        kclass.getSerializedField(fieldName).also {
            assertFieldTypeName(it)
        }
    }

private fun ByteBuffer.readValue(
    field: Field,
    len: Int,
) = when {
    NIL_VALUE == len -> null
    field.type.isEnum -> field.type.enumConstants[int]
    else -> when (val typeName = field.type.name) {
        Boolean::class.jvmName -> 0.toByte() != byte
        Byte::class.jvmName -> byte
        Char::class.jvmName -> char
        Double::class.jvmName -> double
        Float::class.jvmName -> float
        Int::class.jvmName -> int
        Long::class.jvmName -> long
        Short::class.jvmName -> short
        else -> serve(typeName, len)
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> ByteBuffer.serve(typeName: String, len: Int) =
    serve<T, ByteBuffer, T>(
        typeName,
        { buf(len) { it.read(Class.forName(typeName).kotlin) as T } },
        { extrude(it, len) }
    )
