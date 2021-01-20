package hm.binkley.labs.cereal

import java.nio.ByteBuffer

internal fun Iterable<Prep>.newBuffer() =
    ByteBuffer.allocate(map { it.allocateSize }.sum() + 1)

internal fun ByteArray.toByteBuffer() = ByteBuffer.wrap(this)

internal val ByteBuffer.byte get() = get()

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

    return -1
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
internal fun <T, R> Class<T>.readFrom(
    buf: ByteBuffer,
    block: ByteBuffer.(Class<T>) -> R,
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
