package hm.binkley.labs.cereal

import java.lang.reflect.Field
import java.math.BigInteger
import java.nio.ByteBuffer

internal fun <T> ByteBuffer.fields(clazz: Class<T>)
        : Iterable<Pair<Field, Any?>> = FieldIterable(this, clazz)

private class FieldIterable<T>(
    private val buf: ByteBuffer,
    private val clazz: Class<T>,
    private val fieldCount: Int = clazz.readFrom(buf) { fieldCount(it) },
) : Iterable<Pair<Field, Any?>> {
    override fun iterator() = FieldIterator()

    inner class FieldIterator : Iterator<Pair<Field, Any?>> {
        private var n = 0

        override fun hasNext() = fieldCount != n
        override fun next() = clazz.readFrom(buf) { nextField(it) }.also {
            ++n

            if (DEBUG) println("FIELD #$n -> ${it.first} -> ${it.second}")
        }
    }
}

private fun <T> ByteBuffer.fieldCount(expectedClass: Class<T>) =
    readInt().also {
        if (DEBUG) println("FIELD COUNT -> $it")

        assertFieldCount(expectedClass, it)
    }

private fun <T> ByteBuffer.nextField(clazz: Class<T>): Pair<Field, Any?> {
    val field = readField(clazz)
    val len = int
    val value = if (-1 == len) null else readValue(field, len)

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
): Any? {
    return if (field.type.isEnum) {
        field.type.enumConstants[int]
    } else when (val typeName = field.type.name) {
        BigInteger::class.java.name -> ByteArray(len).let {
            get(it)
            BigInteger(it)
        }
        Boolean::class.java.name -> 0.toByte() != byte
        Byte::class.java.name -> byte
        Char::class.java.name -> char
        Double::class.java.name -> double
        Float::class.java.name -> float
        Int::class.java.name -> int
        Long::class.java.name -> long
        Short::class.java.name -> short
        String::class.java.name -> ByteArray(len).let {
            get(it)
            String(it)
        }
        else -> ByteArray(len).let {
            if (DEBUG) println("--START FIELD NESTED TYPE")

            get(it)
            it.read(Class.forName(typeName)).also {
                if (DEBUG) println("--END FIELD NESTED TYPE ($typeName)")
            }
        }
    }
}
