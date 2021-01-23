package hm.binkley.labs.cereal

import java.lang.reflect.Field
import java.math.BigInteger
import java.nio.ByteBuffer
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

internal fun <T> ByteBuffer.fields(clazz: Class<T>):
    Iterable<Pair<Field, Any?>> = FieldIterable(this, clazz)

private class FieldIterable<T>(
    private val buf: ByteBuffer,
    private val clazz: Class<T>,
) : Iterable<Pair<Field, Any?>> {
    private val fieldCount: Int = clazz.readFrom(buf) { fieldCount(it) }

    override fun iterator() = FieldIterator()

    inner class FieldIterator : Iterator<Pair<Field, Any?>> {
        private var n = 0

        override fun hasNext() = fieldCount != n
        override fun next(): Pair<Field, Any?> {
            val next = clazz.readFrom(buf) { nextField(it) }
            ++n
            return next
        }
    }
}

private fun <T> ByteBuffer.fieldCount(expectedClass: Class<T>) =
    readInt().also {
        assertFieldCount(expectedClass, it)
    }

private fun <T> ByteBuffer.nextField(clazz: Class<T>): Pair<Field, Any?> {
    val field = readField(clazz)
    val len = int
    val value = readValue(field, len)

    assertSentinel()

    return field to value
}

private fun <T> ByteBuffer.readField(clazz: Class<T>) =
    readString().let { fieldName ->
        clazz.getSerializedField(fieldName).also {
            assertFieldTypeName(it)
        }
    }

private fun ByteBuffer.readValue(
    field: Field,
    len: Int,
) = when {
    -1 == len -> null
    field.type.isEnum -> field.type.enumConstants[int]
    else -> when (val typeName = field.type.name) {
        BigInteger::class.java.name -> buf(len) { BigInteger(it) }
        Boolean::class.java.name -> 0.toByte() != byte
        Byte::class.java.name -> byte
        Char::class.java.name -> char
        Double::class.java.name -> double
        Duration::class.java.name -> Duration.ofSeconds(long, int.toLong())
        Float::class.java.name -> float
        Instant::class.java.name -> Instant.ofEpochSecond(long, int.toLong())
        Int::class.java.name -> int
        LocalDateTime::class.java.name ->
            LocalDateTime.of(int, int, int, int, int, int, int)
        Long::class.java.name -> long
        Short::class.java.name -> short
        String::class.java.name -> buf(len) { String(it) }
        UUID::class.java.name -> UUID(long, long)
        else -> buf(len) { it.read(Class.forName(typeName)) }
    }
}
