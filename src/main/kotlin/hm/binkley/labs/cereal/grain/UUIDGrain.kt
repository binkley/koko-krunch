package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.reflect.jvm.jvmName

class UUIDGrain : Grain<UUID> {
    override fun consent(typeName: String) =
        UUID::class.jvmName == typeName

    override fun absorb(it: UUID): Prep =
        2 * Long.SIZE_BYTES to { buf ->
            buf.putLong(it.mostSignificantBits)
            buf.putLong(it.leastSignificantBits)
        }

    override fun extrude(buf: ByteBuffer, len: Int): UUID =
        UUID(buf.long, buf.long)
}
