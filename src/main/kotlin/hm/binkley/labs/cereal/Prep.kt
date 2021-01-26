package hm.binkley.labs.cereal

import java.nio.ByteBuffer
import kotlin.reflect.KClass

typealias Prep = Pair<Int, (ByteBuffer) -> ByteBuffer>

internal val Prep.allocateSize
    get() = Int.SIZE_BYTES + (if (NIL_VALUE == first) 0 else first) + 1

internal fun Prep.writeTo(buf: ByteBuffer) = buf.apply {
    putInt(first)
    second(this)
    put(0)
}

internal fun Any?.study(): Prep = when (this) {
    null -> NIL_VALUE to { it }
    is Boolean -> Byte.SIZE_BYTES to { it.putByte(if (this) 1 else 0) }
    is Byte -> Byte.SIZE_BYTES to { it.putByte(this) }
    is Char -> Char.SIZE_BYTES to { it.putChar(this) }
    is Double -> Double.SIZE_BYTES to { it.putDouble(this) }
    is Enum<*> -> Int.SIZE_BYTES to { it.putInt(ordinal) }
    is Float -> Float.SIZE_BYTES to { it.putFloat(this) }
    is Int -> Int.SIZE_BYTES to { it.putInt(this) }
    is Long -> Long.SIZE_BYTES to { it.putLong(this) }
    is Short -> Short.SIZE_BYTES to { it.putShort(this) }
    else -> serve(this::class)
}

private fun <T : Any> T.serve(type: KClass<*>) =
    serve<T, T, Prep>(
        type = type,
        default = { with(write()) { size to { it.put(this) } } },
        match = { absorb(it) }
    )
