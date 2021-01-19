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
