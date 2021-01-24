package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import java.nio.ByteBuffer
import java.time.Duration
import kotlin.reflect.jvm.jvmName

class DurationGrain : Grain<Duration> {
    override fun consent(typeName: String) =
        Duration::class.jvmName == typeName

    override fun absorb(it: Duration): Prep =
        Long.SIZE_BYTES + Int.SIZE_BYTES to { buf ->
            buf.putLong(it.seconds)
            buf.putInt(it.nano)
        }

    override fun extrude(buf: ByteBuffer, len: Int): Duration =
        Duration.ofSeconds(buf.long, buf.int.toLong())
}
