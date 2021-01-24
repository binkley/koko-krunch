package hm.binkley.labs.cereal

import java.lang.reflect.Field
import java.nio.ByteBuffer
import kotlin.reflect.KClass

internal fun Iterable<Prep>.newBuffer() =
    ByteBuffer.allocate(MAGIC.length + 1 + map { it.allocateSize }.sum() + 1)

internal fun ByteArray.toByteBuffer() = ByteBuffer.wrap(this)
internal val ByteBuffer.byte get() = get()
internal fun ByteBuffer.putByte(byte: Byte) = put(byte)

internal fun <T> ByteBuffer.buf(len: Int, ctor: (ByteArray) -> T): T =
    ByteArray(len).let {
        get(it)
        ctor(it)
    }

internal fun ByteArray.indexOf(bytes: ByteArray): Int {
    val len = size
    val bLen = bytes.size
    var i = 0

    loop@ while (len - i >= bLen) {
        var j = 0
        while (j < bLen) {
            if (this[i + j] != bytes[j]) {
                ++i
                continue@loop
            }
            ++j
        }
        return i
    }

    return -1 // TODO: Not same as NIL_VALUE!
}

internal fun ByteArray.replaceAt(at: Int, bytes: ByteArray) {
    val len = bytes.size
    var i = 0
    while (i < len) {
        this[at + i] = bytes[i]
        ++i
    }
}

/** @todo Syntactic sugar causes cancer of the semicolon */
internal fun <T : Any, R> KClass<T>.readFrom(
    buf: ByteBuffer,
    block: ByteBuffer.(KClass<T>) -> R,
): R = buf.block(this)

internal fun ByteBuffer.readString() = ByteArray(int).let {
    get(it)
    assertSentinel()
    String(it)
}

internal fun ByteBuffer.readInt() = int.let {
    assertIntLength(it)
    int.also {
        assertSentinel()
    }
}

internal fun <T : Any> ByteBuffer.readKClass() = readString().toKClass<T>()

private fun <T : Any> String.toKClass() =
    cast<Class<T>>(Class.forName(this)).kotlin

internal fun <T : Any> ByteBuffer.readField(klass: KClass<T>) =
    readString().let { fieldName ->
        klass.getSerializedField(fieldName).also {
            assertFieldTypeName(it)
        }
    }

internal fun ByteBuffer.readValue(
    field: Field,
    len: Int,
) = when {
    NIL_VALUE == len -> null
    field.type.isEnum -> field.type.enumConstants[int]
    else -> when (val type = field.type.kotlin) {
        Boolean::class -> 0.toByte() != byte
        Byte::class -> byte
        Char::class -> char
        Double::class -> double
        Float::class -> float
        Int::class -> int
        Long::class -> long
        Short::class -> short
        else -> serve(type, len)
    }
}

private fun <T : Any> ByteBuffer.serve(type: KClass<T>, len: Int) =
    serve<T, ByteBuffer, T>(
        type,
        { buf(len) { it.read(type) } },
        { extrude(it, len) }
    )

internal fun ByteBuffer.readSentinel() = byte
