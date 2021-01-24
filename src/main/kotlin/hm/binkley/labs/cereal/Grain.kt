package hm.binkley.labs.cereal

import java.nio.ByteBuffer
import java.util.ServiceLoader.load

interface Grain<T : Any> {
    fun consent(typeName: String): Boolean
    fun absorb(it: T): Prep
    fun extrude(buf: ByteBuffer, len: Int): T
}

internal fun <T : Any, U, R> U.serve(
    typeName: String,
    default: U.() -> R,
    match: Grain<T>.(U) -> R,
): R {
    val grains = load(Grain::class.java).filter {
        it.consent(typeName)
    }.map {
        @Suppress("UNCHECKED_CAST")
        it as Grain<T>
    }

    return when (grains.size) {
        0 -> default()
        1 -> grains.first().match(this)
        else -> throw Bug("Too many grains: $typeName")
    }
}
