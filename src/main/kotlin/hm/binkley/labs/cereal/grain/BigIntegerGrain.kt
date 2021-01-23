package hm.binkley.labs.cereal.grain

import hm.binkley.labs.cereal.Grain
import hm.binkley.labs.cereal.Prep
import hm.binkley.labs.cereal.buf
import java.math.BigInteger
import java.nio.ByteBuffer

class BigIntegerGrain : Grain<BigInteger> {
    override fun consent(typeName: String) =
        BigInteger::class.java.name == typeName

    override fun absorb(it: BigInteger): Prep =
        with(it.toByteArray()) { size to { it.put(this) } }

    override fun extrude(buf: ByteBuffer, len: Int): BigInteger =
        buf.buf(len) { BigInteger(it) }
}
