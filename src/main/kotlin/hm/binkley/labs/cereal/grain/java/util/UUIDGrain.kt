package hm.binkley.labs.cereal.grain.java.util

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.reflect.KClass

class UUIDGrain : Grain<UUID> {
    override fun consent(type: KClass<*>): Boolean = UUID::class == type
    override fun absorb(it: UUID): Prep =
        2 * Long.SIZE_BYTES to { buf ->
            buf.putLong(it.mostSignificantBits)
            buf.putLong(it.leastSignificantBits)
        }

    override fun extrude(buf: ByteBuffer, len: Int): UUID =
        UUID(buf.long, buf.long)
}
