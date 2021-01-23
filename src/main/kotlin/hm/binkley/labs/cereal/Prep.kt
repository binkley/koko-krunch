package hm.binkley.labs.cereal

import java.math.BigInteger
import java.nio.ByteBuffer
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoField.INSTANT_SECONDS
import java.time.temporal.ChronoField.NANO_OF_SECOND
import java.util.UUID

internal typealias Prep = Pair<Int, (ByteBuffer) -> ByteBuffer>

internal val Prep.allocateSize
    get() = Int.SIZE_BYTES + (if (-1 == first) 0 else first) + 1

internal fun Prep.writeTo(buf: ByteBuffer) = buf.apply {
    putInt(first)
    second(this)
    put(0)
}

internal fun Any?.study(): Prep = when (this) {
    null -> -1 to { it }
    is BigInteger -> with(toByteArray()) { size to { it.put(this) } }
    is Boolean -> Byte.SIZE_BYTES to { it.put(if (this) 1 else 0) }
    is Byte -> Byte.SIZE_BYTES to { it.put(this) }
    is Char -> Char.SIZE_BYTES to { it.putChar(this) }
    is LocalDateTime -> 7 * Int.SIZE_BYTES to {
        val date = toLocalDate()
        it.putInt(date.year)
        it.putInt(date.monthValue)
        it.putInt(date.dayOfMonth)
        val time = toLocalTime()
        it.putInt(time.hour)
        it.putInt(time.minute)
        it.putInt(time.second)
        it.putInt(time.nano)
    }
    is Double -> Double.SIZE_BYTES to { it.putDouble(this) }
    is Duration -> (Long.SIZE_BYTES + Int.SIZE_BYTES) to {
        it.putLong(seconds)
        it.putInt(nano)
    }
    is Enum<*> -> Int.SIZE_BYTES to { it.putInt(ordinal) }
    is Float -> Float.SIZE_BYTES to { it.putFloat(this) }
    is Instant -> (Long.SIZE_BYTES + Int.SIZE_BYTES) to {
        it.putLong(getLong(INSTANT_SECONDS))
        it.putInt(get(NANO_OF_SECOND))
    }
    is Int -> Int.SIZE_BYTES to { it.putInt(this) }
    is Long -> Long.SIZE_BYTES to { it.putLong(this) }
    is Short -> Short.SIZE_BYTES to { it.putShort(this) }
    is String -> with(encodeToByteArray()) { size to { it.put(this) } }
    is UUID -> 2 * Long.SIZE_BYTES to {
        it.putLong(mostSignificantBits)
        it.putLong(leastSignificantBits)
    }
    else -> with(write()) { size to { it.put(this) } }
}
