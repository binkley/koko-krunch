package hm.binkley.labs.cereal

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

internal fun ByteBuffer.readSentinel() = byte
