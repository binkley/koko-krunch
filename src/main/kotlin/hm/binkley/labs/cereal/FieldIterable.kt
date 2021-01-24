package hm.binkley.labs.cereal

import java.lang.reflect.Field
import java.nio.ByteBuffer
import kotlin.reflect.KClass

internal fun <T : Any> ByteBuffer.readFields(klass: KClass<T>):
    Iterable<Pair<Field, Any?>> = FieldIterable(this, klass)

private class FieldIterable<T : Any>(
    private val buf: ByteBuffer,
    private val klass: KClass<T>,
) : Iterable<Pair<Field, Any?>> {
    private val fieldCount: Int = klass.readFrom(buf) { fieldCount(it) }

    override fun iterator() = FieldIterator()

    inner class FieldIterator : Iterator<Pair<Field, Any?>> {
        private var n = 0

        override fun hasNext() = fieldCount != n
        override fun next(): Pair<Field, Any?> {
            val next = klass.readFrom(buf) { nextField(it) }
            ++n
            return next
        }
    }
}

private fun <T : Any> ByteBuffer.fieldCount(expectedClass: KClass<T>) =
    readInt().also {
        assertFieldCount(expectedClass, it)
    }

private fun <T : Any> ByteBuffer.nextField(klass: KClass<T>): Pair<Field, Any?> {
    val field = readField(klass)
    val len = int
    val value = readValue(field, len)

    assertSentinel()

    return field to value
}
