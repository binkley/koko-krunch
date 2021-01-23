package hm.binkley.labs.cereal

import java.nio.ByteBuffer

typealias Prep = Pair<Int, (ByteBuffer) -> ByteBuffer>

interface Grain<T : Any> {
    fun consent(typeName: String): Boolean
    fun absorb(it: T): Prep
    fun extrude(buf: ByteBuffer, len: Int): T
}
